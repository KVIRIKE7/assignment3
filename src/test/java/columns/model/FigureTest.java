package columns.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Figure movement and rotation.
 *
 * FakeRandom returns a predictable sequence so figure colors are known:
 *   c[1]=1, c[2]=2, c[3]=3  (values 1,2,3 passed to the constructor).
 */
class FigureTest {

    /** Creates a figure with deterministic colors 1, 2, 3. */
    private Figure figureWith(int c1, int c2, int c3) {
        return new Figure(new FakeRandom(c1, c2, c3));
    }

    // --- Initial state ---

    @Test
    void newFigureStartsAtCenterColumn() {
        Figure f = figureWith(1, 2, 3);
        assertEquals(GameConfig.WIDTH / 2 + 1, f.x);
    }

    @Test
    void newFigureStartsAtRowOne() {
        Figure f = figureWith(1, 2, 3);
        assertEquals(1, f.y);
    }

    @Test
    void newFigureColorsMatchRandomSequence() {
        Figure f = figureWith(1, 2, 3);
        // c[0] is always 0 (unused/sentinel), c[1..3] from random
        assertEquals(0, f.c[0]);
        assertEquals(1, f.c[1]);
        assertEquals(2, f.c[2]);
        assertEquals(3, f.c[3]);
    }

    // --- Movement ---

    @Test
    void moveLeftDecreasesX() {
        Figure f = figureWith(1, 2, 3);
        int before = f.x;
        f.moveLeft();
        assertEquals(before - 1, f.x);
    }

    @Test
    void moveRightIncreasesX() {
        Figure f = figureWith(1, 2, 3);
        int before = f.x;
        f.moveRight();
        assertEquals(before + 1, f.x);
    }

    @Test
    void moveDownIncreasesY() {
        Figure f = figureWith(1, 2, 3);
        int before = f.y;
        f.moveDown();
        assertEquals(before + 1, f.y);
    }

    @Test
    void multipleMoveDownsAccumulate() {
        Figure f = figureWith(1, 2, 3);
        f.moveDown();
        f.moveDown();
        f.moveDown();
        assertEquals(4, f.y);
    }

    // --- Rotation ---

    @Test
    void rotateUpShiftsColorsUpward() {
        // Before: c[1]=1, c[2]=2, c[3]=3
        // After rotateUp: c[1]=2, c[2]=3, c[3]=1
        Figure f = figureWith(1, 2, 3);
        f.rotateUp();
        assertEquals(2, f.c[1]);
        assertEquals(3, f.c[2]);
        assertEquals(1, f.c[3]);
    }

    @Test
    void rotateDownShiftsColorsDownward() {
        // Before: c[1]=1, c[2]=2, c[3]=3
        // After rotateDown: c[1]=3, c[2]=1, c[3]=2
        Figure f = figureWith(1, 2, 3);
        f.rotateDown();
        assertEquals(3, f.c[1]);
        assertEquals(1, f.c[2]);
        assertEquals(2, f.c[3]);
    }

    @Test
    void rotateUpThreeTimesRestoresOriginalOrder() {
        Figure f = figureWith(1, 2, 3);
        f.rotateUp();
        f.rotateUp();
        f.rotateUp();
        assertEquals(1, f.c[1]);
        assertEquals(2, f.c[2]);
        assertEquals(3, f.c[3]);
    }

    @Test
    void rotateDownThreeTimesRestoresOriginalOrder() {
        Figure f = figureWith(1, 2, 3);
        f.rotateDown();
        f.rotateDown();
        f.rotateDown();
        assertEquals(1, f.c[1]);
        assertEquals(2, f.c[2]);
        assertEquals(3, f.c[3]);
    }

    @Test
    void rotateUpThenDownRestoresOriginalOrder() {
        Figure f = figureWith(1, 2, 3);
        f.rotateUp();
        f.rotateDown();
        assertEquals(1, f.c[1]);
        assertEquals(2, f.c[2]);
        assertEquals(3, f.c[3]);
    }

    @Test
    void rotateDownThenUpRestoresOriginalOrder() {
        Figure f = figureWith(1, 2, 3);
        f.rotateDown();
        f.rotateUp();
        assertEquals(1, f.c[1]);
        assertEquals(2, f.c[2]);
        assertEquals(3, f.c[3]);
    }
}
