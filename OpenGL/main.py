import math
import pygame
import time

from pygame.locals import (
    QUIT, DOUBLEBUF, OPENGL
)

from OpenGL.GL import (
    glBegin, glEnd, glClear, glClearColor, glColor3f, glColorMaterial,
    glEnable, glDisable, glLoadIdentity, glMatrixMode, glNormal3fv,
    glLightf, glLightfv, glLightModelfv, glPopMatrix, glPushMatrix,
    glScalef, glTranslatef, glVertex3fv
)

from OpenGL.GL import (
    GL_AMBIENT_AND_DIFFUSE, GL_COLOR_BUFFER_BIT, GL_COLOR_MATERIAL,
    GL_DEPTH_BUFFER_BIT, GL_DEPTH_TEST, GL_DIFFUSE, GL_FRONT, GL_LIGHT0,
    GL_LIGHT1, GL_LIGHT2, GL_LIGHTING, GL_LIGHT_MODEL_AMBIENT, GL_MODELVIEW,
    GL_NORMALIZE, GL_POSITION, GL_PROJECTION, GL_QUADS, GL_SPOT_CUTOFF,
    GL_SPOT_DIRECTION, GL_SPOT_EXPONENT
)
from OpenGL.GLU import (
    gluPerspective, gluLookAt, gluNewQuadric, gluSphere, gluDeleteQuadric
)


def draw_cube():
    vertices = ((1, -1, -1), (1, 1, -1), (-1, 1, -1), (-1, -1, -1),
                (1, -1, 1), (1, 1, 1), (-1, -1, 1), (-1, 1, 1))
    surfaces = ((0, 1, 2, 3), (3, 2, 7, 6), (6, 7, 5, 4),
                (4, 5, 1, 0), (1, 5, 7, 2), (4, 0, 3, 6))
    normals = [(0, 0, -1), (-1, 0, 0), (0, 0, 1),
               (1, 0, 0), (0, 1, 0), (0, -1, 0)]

    glBegin(GL_QUADS)
    for i, surface in enumerate(surfaces):
        glNormal3fv(normals[i])
        for v_index in surface:
            glVertex3fv(vertices[v_index])
    glEnd()


def draw_sphere():
    quadric = gluNewQuadric()
    gluSphere(quadric, 1.0, 20, 20)
    gluDeleteQuadric(quadric)


def draw_pawn():

    glPushMatrix()
    glTranslatef(0, 0, 0.3)
    glScalef(1.0, 1.0, 0.5)
    draw_sphere()
    glPopMatrix()

    glPushMatrix()
    glTranslatef(0, 0, 1.75)
    glScalef(0.4, 0.4, 1.0)
    draw_sphere()
    glPopMatrix()

    glPushMatrix()
    glTranslatef(0, 0, 2.5)
    glScalef(0.6, 0.6, 0.6)
    draw_sphere()
    glPopMatrix()


def draw_chessboard():
    for row in range(8):
        for col in range(8):
            if (row + col) % 2 == 0:
                glColor3f(0.9, 0.9, 0.7)
            else:
                glColor3f(0.3, 0.2, 0.1)

            glPushMatrix()
            glTranslatef(col - 3.5, row - 3.5, 0)
            glScalef(0.5, 0.5, 0.1)
            draw_cube()
            glPopMatrix()


def draw_pawns():

    pawn_positions = [
        (0, 1), (1, 1), (2, 1), (3, 1), (4, 1), (5, 1), (6, 1), (7, 1),
        (0, 6), (1, 6), (2, 6), (3, 6), (4, 6), (5, 6), (6, 6), (7, 6),
    ]

    for i, (col, row) in enumerate(pawn_positions):

        if row == 1:
            glColor3f(0.9, 0.9, 0.9)
        else:
            glColor3f(0.1, 0.1, 0.1)

        glPushMatrix()
        glTranslatef(col - 3.5, row - 3.5, 0.1)
        glScalef(0.3, 0.3, 0.3)
        draw_pawn()
        glPopMatrix()


def setup_lighting():
    glEnable(GL_LIGHTING)
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_NORMALIZE)
    glEnable(GL_COLOR_MATERIAL)
    glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE)

    # Ambient light
    glLightModelfv(GL_LIGHT_MODEL_AMBIENT, [0.2, 0.2, 0.2, 1.0])

    glEnable(GL_LIGHT0)
    glLightfv(GL_LIGHT0, GL_POSITION, [0, 0, 8, 1])
    glLightfv(GL_LIGHT0, GL_DIFFUSE, [1.0, 1.0, 0.8, 1.0])
    glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, [0, 0, -1])
    glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 30.0)
    glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 2.0)

    glEnable(GL_LIGHT1)
    glLightfv(GL_LIGHT1, GL_POSITION, [6, 6, 4, 1])
    glLightfv(GL_LIGHT1, GL_DIFFUSE, [0.8, 0.8, 1.0, 1.0])
    glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, [-1, -1, -1])
    glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 45.0)
    glLightf(GL_LIGHT1, GL_SPOT_EXPONENT, 1.5)

    glEnable(GL_LIGHT2)
    glLightfv(GL_LIGHT2, GL_POSITION, [-6, -6, 4, 1])
    glLightfv(GL_LIGHT2, GL_DIFFUSE, [1.0, 0.8, 0.8, 1.0])
    glLightfv(GL_LIGHT2, GL_SPOT_DIRECTION, [1, 1, -1])
    glLightf(GL_LIGHT2, GL_SPOT_CUTOFF, 40.0)
    glLightf(GL_LIGHT2, GL_SPOT_EXPONENT, 2.0)


def setup_camera(angle):

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()

    radius = 12
    cam_x = radius * math.cos(angle)
    cam_y = radius * math.sin(angle)
    cam_z = 8

    gluLookAt(cam_x, cam_y, cam_z,  # Camera pos
              0, 0, 0,  # Point to look at
              0, 0, 1)  # Vector up


def main():
    pygame.init()
    display = (800, 600)
    pygame.display.set_mode(display, DOUBLEBUF | OPENGL)
    pygame.display.set_caption("Chessboard 3D")

    glMatrixMode(GL_PROJECTION)
    gluPerspective(45, (display[0] / display[1]), 0.1, 50.0)

    # Background color
    glClearColor(0.1, 0.1, 0.3, 1.0)

    setup_lighting()

    clock = pygame.time.Clock()
    angle = 0

    while True:
        for event in pygame.event.get():
            if event.type == QUIT:
                pygame.quit()
                return

        # Camera angle
        angle += 0.01

        # Clear buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

        # Set camera
        setup_camera(angle)

        # Draw scene
        draw_chessboard()
        draw_pawns()

        # Swap buffers
        pygame.display.flip()
        clock.tick(60)


if __name__ == "__main__":
    main()