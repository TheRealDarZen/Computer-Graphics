import numpy as np
import pygame
from pygame.locals import *
from OpenGL.GL import *
from OpenGL.GLU import *
import math
import os
import sys
from collections import defaultdict


class Material:
    def __init__(self):
        self.Ka = [0.2, 0.2, 0.2]  # ambient
        self.Kd = [0.8, 0.8, 0.8]  # diffuse
        self.Ks = [1.0, 1.0, 1.0]  # specular
        self.Ns = 50.0  # shininess
        self.d = 1.0  # transparency


class Mesh:
    def __init__(self):
        self.vertices = []
        self.normals = []
        self.faces = []
        self.material = Material()


class Camera:
    def __init__(self):
        self.eye = np.array([0.0, 0.0, 5.0])
        self.target = np.array([0.0, 0.0, 0.0])
        self.up = np.array([0.0, 1.0, 0.0])
        self.fov = 45.0
        self.aspect = 1.0
        self.near = 0.1
        self.far = 100.0

    def get_view_matrix(self):

        forward = self.target - self.eye
        forward = forward / np.linalg.norm(forward)

        right = np.cross(forward, self.up)
        right = right / np.linalg.norm(right)

        up = np.cross(right, forward)

        return forward, right, up


class SceneViewer:
    def __init__(self, width=800, height=600):
        self.width = width
        self.height = height
        self.camera = Camera()
        self.camera.aspect = width / height
        self.meshes = []
        self.materials = {}
        self.scene_bounds = None

        self.move_speed = 0.1
        self.rotate_speed = 0.02
        self.zoom_speed = 0.1

        self.keys = set()

    def load_obj_file(self, filename):

        if not os.path.exists(filename):
            print(f"File {filename} does not exist")
            return False

        vertices = []
        normals = []
        current_material = None
        current_mesh = None

        try:
            with open(filename, 'r') as f:
                for line in f:
                    line = line.strip()
                    if not line or line.startswith('#'):
                        continue

                    parts = line.split()
                    if not parts:
                        continue

                    if parts[0] == 'v':  # vertex
                        x, y, z = map(float, parts[1:4])
                        vertices.append([x, y, z])

                    elif parts[0] == 'vn':  # vertex normal
                        x, y, z = map(float, parts[1:4])
                        normals.append([x, y, z])

                    elif parts[0] == 'usemtl':  # use material
                        current_material = parts[1]
                        current_mesh = Mesh()
                        if current_material in self.materials:
                            current_mesh.material = self.materials[current_material]
                        self.meshes.append(current_mesh)

                    elif parts[0] == 'f':  # face
                        if current_mesh is None:
                            current_mesh = Mesh()
                            self.meshes.append(current_mesh)

                        face_vertices = []
                        face_normals = []

                        for vertex_data in parts[1:]:
                            indices = vertex_data.split('/')
                            v_idx = int(indices[0]) - 1
                            face_vertices.append(vertices[v_idx])

                            if len(indices) > 2 and indices[2] and normals:
                                n_idx = int(indices[2]) - 1
                                face_normals.append(normals[n_idx])
                            else:
                                if len(face_vertices) >= 3:
                                    v1 = np.array(vertices[v_idx])
                                    v2 = np.array(vertices[int(parts[1].split('/')[0]) - 1])
                                    v3 = np.array(vertices[int(parts[2].split('/')[0]) - 1])
                                    normal = np.cross(v2 - v1, v3 - v1)
                                    normal = normal / np.linalg.norm(normal)
                                    face_normals.append(normal.tolist())

                        current_mesh.faces.append({
                            'vertices': face_vertices[:],
                            'normals': face_normals[:] if face_normals else None
                        })

            self.calculate_scene_bounds()
            print(f"Loaded {len(self.meshes)} meshes from file {filename}")
            return True

        except Exception as e:
            print(f"Error while loading OBJ file: {e}")
            return False

    def load_mtl_file(self, filename):

        if not os.path.exists(filename):
            return

        current_material = None

        try:
            with open(filename, 'r') as f:
                for line in f:
                    line = line.strip()
                    if not line or line.startswith('#'):
                        continue

                    parts = line.split()
                    if not parts:
                        continue

                    if parts[0] == 'newmtl':
                        current_material = parts[1]
                        self.materials[current_material] = Material()

                    elif current_material and parts[0] == 'Ka':
                        self.materials[current_material].Ka = [float(parts[1]), float(parts[2]), float(parts[3])]

                    elif current_material and parts[0] == 'Kd':
                        self.materials[current_material].Kd = [float(parts[1]), float(parts[2]), float(parts[3])]

                    elif current_material and parts[0] == 'Ks':
                        self.materials[current_material].Ks = [float(parts[1]), float(parts[2]), float(parts[3])]

                    elif current_material and parts[0] == 'Ns':
                        self.materials[current_material].Ns = float(parts[1])

                    elif current_material and parts[0] == 'd':
                        self.materials[current_material].d = float(parts[1])

        except Exception as e:
            print(f"Error while loading MTL file: {e}")

    def load_camera_file(self, filename):

        if not os.path.exists(filename):
            return False

        try:
            with open(filename, 'r') as f:
                lines = f.readlines()

            if len(lines) >= 6:

                eye_data = lines[0].strip().split()
                self.camera.eye = np.array([float(eye_data[0]), float(eye_data[1]), float(eye_data[2])])

                target_data = lines[1].strip().split()
                self.camera.target = np.array([float(target_data[0]), float(target_data[1]), float(target_data[2])])

                res_data = lines[2].strip().split()
                xres, yres = int(res_data[0]), int(res_data[1])
                self.camera.aspect = xres / yres

                print(f"Loaded parameters from file {filename}")
                return True

        except Exception as e:
            print(f"Error while loading file: {e}")

        return False

    def calculate_scene_bounds(self):

        if not self.meshes:
            return

        min_coords = [float('inf')] * 3
        max_coords = [float('-inf')] * 3

        for mesh in self.meshes:
            for face in mesh.faces:
                for vertex in face['vertices']:
                    for i in range(3):
                        min_coords[i] = min(min_coords[i], vertex[i])
                        max_coords[i] = max(max_coords[i], vertex[i])

        self.scene_bounds = {
            'min': np.array(min_coords),
            'max': np.array(max_coords),
            'center': np.array([(min_coords[i] + max_coords[i]) / 2 for i in range(3)]),
            'size': np.array([max_coords[i] - min_coords[i] for i in range(3)])
        }

    def setup_default_camera(self):

        if not self.scene_bounds:
            return

        center = self.scene_bounds['center']
        size = self.scene_bounds['size']
        max_size = max(size)

        diagonal = np.array([1, 1, 1]) * max_size * 0.8
        self.camera.eye = center + diagonal
        self.camera.target = center

        # Dostosuj FOV aby objąć całą scenę
        distance = np.linalg.norm(diagonal)
        self.camera.fov = 2 * math.degrees(math.atan(max_size / (2 * distance)))
        self.camera.fov = max(10, min(self.camera.fov, 120))  # Limit FOV

    def setup_lighting(self):

        glEnable(GL_LIGHTING)
        glEnable(GL_LIGHT0)
        glEnable(GL_LIGHT1)

        # Main light
        light0_pos = [10.0, 10.0, 10.0, 1.0]
        if self.scene_bounds:
            center = self.scene_bounds['center']
            size = max(self.scene_bounds['size'])
            light0_pos = [center[0] + size, center[1] + size, center[2] + size, 1.0]

        glLightfv(GL_LIGHT0, GL_POSITION, light0_pos)
        glLightfv(GL_LIGHT0, GL_DIFFUSE, [0.8, 0.8, 0.8, 1.0])
        glLightfv(GL_LIGHT0, GL_SPECULAR, [1.0, 1.0, 1.0, 1.0])

        # Additional light
        light1_pos = [-5.0, 5.0, -5.0, 1.0]
        if self.scene_bounds:
            light1_pos = [center[0] - size / 2, center[1] + size / 2, center[2] - size / 2, 1.0]

        glLightfv(GL_LIGHT1, GL_POSITION, light1_pos)
        glLightfv(GL_LIGHT1, GL_DIFFUSE, [0.4, 0.4, 0.4, 1.0])
        glLightfv(GL_LIGHT1, GL_SPECULAR, [0.2, 0.2, 0.2, 1.0])

        # Abmient light
        glLightModelfv(GL_LIGHT_MODEL_AMBIENT, [0.2, 0.2, 0.2, 1.0])

    def render_scene(self):

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

        # Set up projection matrix
        glMatrixMode(GL_PROJECTION)
        glLoadIdentity()
        gluPerspective(self.camera.fov, self.camera.aspect, self.camera.near, self.camera.far)

        # Set up modelview matrix
        glMatrixMode(GL_MODELVIEW)
        glLoadIdentity()
        gluLookAt(
            self.camera.eye[0], self.camera.eye[1], self.camera.eye[2],
            self.camera.target[0], self.camera.target[1], self.camera.target[2],
            self.camera.up[0], self.camera.up[1], self.camera.up[2]
        )

        # Render meshes
        for mesh in self.meshes:
            self.render_mesh(mesh)

    def render_mesh(self, mesh):

        # Set material
        glMaterialfv(GL_FRONT, GL_AMBIENT, mesh.material.Ka + [1.0])
        glMaterialfv(GL_FRONT, GL_DIFFUSE, mesh.material.Kd + [mesh.material.d])
        glMaterialfv(GL_FRONT, GL_SPECULAR, mesh.material.Ks + [1.0])
        glMaterialf(GL_FRONT, GL_SHININESS, mesh.material.Ns)

        # Render faces
        for face in mesh.faces:
            vertices = face['vertices']
            normals = face['normals']

            if len(vertices) == 3:
                glBegin(GL_TRIANGLES)
            elif len(vertices) == 4:
                glBegin(GL_QUADS)
            else:
                glBegin(GL_POLYGON)

            for i, vertex in enumerate(vertices):
                if normals and i < len(normals):
                    glNormal3fv(normals[i])
                glVertex3fv(vertex)

            glEnd()

    def handle_camera_movement(self):

        if not self.keys:
            return

        forward, right, up = self.camera.get_view_matrix()

        # LEFT/RIGHT/UP/DOWN
        if K_LEFT in self.keys or K_a in self.keys:
            self.camera.eye -= right * self.move_speed
            self.camera.target -= right * self.move_speed
        if K_RIGHT in self.keys or K_d in self.keys:
            self.camera.eye += right * self.move_speed
            self.camera.target += right * self.move_speed
        if K_UP in self.keys or K_w in self.keys:
            self.camera.eye += up * self.move_speed
            self.camera.target += up * self.move_speed
        if K_DOWN in self.keys or K_s in self.keys:
            self.camera.eye -= up * self.move_speed
            self.camera.target -= up * self.move_speed

        # FORWARD/BACKWARD
        if K_q in self.keys:
            self.camera.eye += forward * self.move_speed
            self.camera.target += forward * self.move_speed
        if K_e in self.keys:
            self.camera.eye -= forward * self.move_speed
            self.camera.target -= forward * self.move_speed

        # ORBITING
        if K_j in self.keys:  # Left
            self.orbit_camera(-self.rotate_speed, 0)
        if K_l in self.keys:  # Right
            self.orbit_camera(self.rotate_speed, 0)
        if K_i in self.keys:  # Up
            self.orbit_camera(0, self.rotate_speed)
        if K_k in self.keys:  # Down
            self.orbit_camera(0, -self.rotate_speed)

        # Looking around
        if K_u in self.keys:  # Left
            self.look_around(-self.rotate_speed, 0)
        if K_o in self.keys:  # Right
            self.look_around(self.rotate_speed, 0)
        if K_y in self.keys:  # Up
            self.look_around(0, self.rotate_speed)
        if K_h in self.keys:  # Down
            self.look_around(0, -self.rotate_speed)

    def orbit_camera(self, horizontal_angle, vertical_angle):

        # Target -> Eye vector
        to_eye = self.camera.eye - self.camera.target
        distance = np.linalg.norm(to_eye)

        if distance < 0.001:
            return

        # Converting to spherical coordinates
        theta = math.atan2(to_eye[0], to_eye[2])  # XZ plane angle
        phi = math.acos(to_eye[1] / distance)  # OY angle

        # Change angles
        theta += horizontal_angle
        phi += vertical_angle

        # Phi limit
        phi = max(0.1, min(math.pi - 0.1, phi))

        # Converting back to normal coordinates
        self.camera.eye[0] = self.camera.target[0] + distance * math.sin(phi) * math.sin(theta)
        self.camera.eye[1] = self.camera.target[1] + distance * math.cos(phi)
        self.camera.eye[2] = self.camera.target[2] + distance * math.sin(phi) * math.cos(theta)

    def look_around(self, horizontal_angle, vertical_angle):

        forward, right, up = self.camera.get_view_matrix()

        # horizontal
        cos_h, sin_h = math.cos(horizontal_angle), math.sin(horizontal_angle)
        new_forward = forward * cos_h + right * sin_h

        # vertical
        cos_v, sin_v = math.cos(vertical_angle), math.sin(vertical_angle)
        final_forward = new_forward * cos_v + up * sin_v

        # Normalize and set a new target
        final_forward = final_forward / np.linalg.norm(final_forward)
        distance = np.linalg.norm(self.camera.target - self.camera.eye)
        self.camera.target = self.camera.eye + final_forward * distance

    def run(self, obj_file=None):

        pygame.init()
        pygame.display.set_mode((self.width, self.height), DOUBLEBUF | OPENGL)
        pygame.display.set_caption("3D Visualization")

        # OpenGL initial settings
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_NORMALIZE)
        glClearColor(0.1, 0.1, 0.1, 1.0)

        # Load scene
        if obj_file and os.path.exists(obj_file):
            # MTL
            mtl_file = obj_file.replace('.obj', '.mtl')
            self.load_mtl_file(mtl_file)

            # OBJ
            if self.load_obj_file(obj_file):
                cam_file = obj_file.replace('.obj', '.cam')
                if not self.load_camera_file(cam_file):
                    self.setup_default_camera()
            else:
                # Test scene
                self.create_test_scene()
        else:
            # Also test scene
            self.create_test_scene()

        self.setup_lighting()

        clock = pygame.time.Clock()
        running = True

        print("Camera controls:")
        print("WASD / Arrow keys - Panning")
        print("Q/E - Moving forward/backward")
        print("IJKL - Orbiting")
        print("YUHO - Looking around")
        print("ESC - Exit")

        while running:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    running = False
                elif event.type == pygame.KEYDOWN:
                    if event.key == pygame.K_ESCAPE:
                        running = False
                    else:
                        self.keys.add(event.key)
                elif event.type == pygame.KEYUP:
                    self.keys.discard(event.key)

            self.handle_camera_movement()
            self.render_scene()

            pygame.display.flip()
            clock.tick(60)

        pygame.quit()

    def create_test_scene(self):

        # Cube
        cube_mesh = Mesh()
        cube_mesh.material.Kd = [1.0, 0.5, 0.5]

        vertices = [
            [-1, -1, -1], [1, -1, -1], [1, 1, -1], [-1, 1, -1],
            [-1, -1, 1], [1, -1, 1], [1, 1, 1], [-1, 1, 1]
        ]

        faces = [
            [0, 1, 2, 3],
            [5, 4, 7, 6],
            [4, 0, 3, 7],
            [1, 5, 6, 2],
            [3, 2, 6, 7],
            [4, 5, 1, 0]
        ]

        for face_indices in faces:
            face_vertices = [vertices[i] for i in face_indices]
            cube_mesh.faces.append({'vertices': face_vertices, 'normals': None})

        self.meshes.append(cube_mesh)

        # Smaller cube
        small_cube = Mesh()
        small_cube.material.Kd = [0.5, 1.0, 0.5]

        for face_indices in faces:
            face_vertices = [[v[0] * 0.3 + 3, v[1] * 0.3, v[2] * 0.3] for i, v in
                             enumerate([vertices[j] for j in face_indices])]
            small_cube.faces.append({'vertices': face_vertices, 'normals': None})

        self.meshes.append(small_cube)

        self.calculate_scene_bounds()
        self.setup_default_camera()


if __name__ == "__main__":

    viewer = SceneViewer()

    obj_file = None
    if len(sys.argv) > 1:
        obj_file = sys.argv[1]

    viewer.run(obj_file)