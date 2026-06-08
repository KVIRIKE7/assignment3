package columns.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GameController.processEvent — each game event is exercised
 * through a FakePlatform so no real time, screen, or applet is needed.
 *
 * Setup creates a controller, then manually places a figure on the board
 * at a known position so every event test starts from a predictable state.
 */
class GameControllerTest {

    private FakePlatform platform;
    private GameController controller;

    // Known center column position where all events have room to act
    private static final int CENTER_X = GameConfig.WIDTH / 2 + 1;
    private static final int START_Y  = 1;

    @BeforeEach
    void setUp() {
        // Colors 1,2,3 for each new figure created during the test
        platform = new FakePlatform(new FakeRandom(1, 2, 3, 1, 2, 3, 1, 2, 3));
        controller = new GameController(platform);

        // Place a figure at a known position so event tests can assert on it
        controller.board.initBoard();
        controller.board.figure = new Figure(new FakeRandom(1, 2, 3));
        controller.board.figure.x = CENTER_X;
        controller.board.figure.y = START_Y;
    }

    // -----------------------------------------------------------------------
    // LEFT event
    // -----------------------------------------------------------------------

    @Test
    void leftEventMovesFigureLeft() {
        int xBefore = controller.board.figure.x;
        controller.processEvent(GameEvent.LEFT);
        assertEquals(xBefore - 1, controller.board.figure.x);
    }

    @Test
    void leftEventDoesNotMoveFigureThroughLeftWall() {
        controller.board.figure.x = 1;
        controller.processEvent(GameEvent.LEFT);
        assertEquals(1, controller.board.figure.x);
    }

    @Test
    void leftEventDoesNotMoveFigureIntoOccupiedCell() {
        controller.board.figure.x = 4;
        controller.board.figure.y = 1;
        // Block at (x-1, y+2) = (3, 3)
        controller.board.newField[3][3] = 2;
        controller.processEvent(GameEvent.LEFT);
        assertEquals(4, controller.board.figure.x);
    }

    // -----------------------------------------------------------------------
    // RIGHT event
    // -----------------------------------------------------------------------

    @Test
    void rightEventMovesFigureRight() {
        int xBefore = controller.board.figure.x;
        controller.processEvent(GameEvent.RIGHT);
        assertEquals(xBefore + 1, controller.board.figure.x);
    }

    @Test
    void rightEventDoesNotMoveFigureThroughRightWall() {
        controller.board.figure.x = GameConfig.WIDTH;
        controller.processEvent(GameEvent.RIGHT);
        assertEquals(GameConfig.WIDTH, controller.board.figure.x);
    }

    @Test
    void rightEventDoesNotMoveFigureIntoOccupiedCell() {
        controller.board.figure.x = 4;
        controller.board.figure.y = 1;
        controller.board.newField[5][3] = 2;
        controller.processEvent(GameEvent.RIGHT);
        assertEquals(4, controller.board.figure.x);
    }

    // -----------------------------------------------------------------------
    // UP event (rotate up)
    // -----------------------------------------------------------------------

    @Test
    void upEventRotatesFigureColorsUpward() {
        // Start: c[1]=1, c[2]=2, c[3]=3
        controller.processEvent(GameEvent.UP);
        assertEquals(2, controller.board.figure.c[1]);
        assertEquals(3, controller.board.figure.c[2]);
        assertEquals(1, controller.board.figure.c[3]);
    }

    // -----------------------------------------------------------------------
    // DOWN event (rotate down)
    // -----------------------------------------------------------------------

    @Test
    void downEventRotatesFigureColorsDownward() {
        // Start: c[1]=1, c[2]=2, c[3]=3
        controller.processEvent(GameEvent.DOWN);
        assertEquals(3, controller.board.figure.c[1]);
        assertEquals(1, controller.board.figure.c[2]);
        assertEquals(2, controller.board.figure.c[3]);
    }

    // -----------------------------------------------------------------------
    // DROP event
    // -----------------------------------------------------------------------

    @Test
    void dropEventMovesFigureTowardBottom() {
        int yBefore = controller.board.figure.y;
        controller.processEvent(GameEvent.DROP);
        // After drop the figure y should be greater (lower on board)
        assertTrue(controller.board.figure.y > yBefore,
                "DROP should move figure downward");
    }

    @Test
    void dropEventResetsTc() {
        platform.setTc(99999L);
        controller.processEvent(GameEvent.DROP);
        assertEquals(0L, platform.getTc());
    }

    @Test
    void dropEventClearsKeyPressedFlag() {
        platform.setKeyPressed(true);
        controller.processEvent(GameEvent.DROP);
        assertFalse(platform.isKeyPressed());
    }

    // -----------------------------------------------------------------------
    // LEVEL_UP event
    // -----------------------------------------------------------------------

    @Test
    void levelUpEventIncreasesLevel() {
        int before = controller.board.level;
        controller.processEvent(GameEvent.LEVEL_UP);
        assertEquals(before + 1, controller.board.level);
    }

    @Test
    void levelUpEventDoesNotExceedMaxLevel() {
        controller.board.level = GameConfig.MAX_LEVEL;
        controller.processEvent(GameEvent.LEVEL_UP);
        assertEquals(GameConfig.MAX_LEVEL, controller.board.level);
    }

    @Test
    void levelUpEventResetsMatchCounter() {
        controller.board.figuresMatchedCounter = 15;
        controller.processEvent(GameEvent.LEVEL_UP);
        assertEquals(0, controller.board.figuresMatchedCounter);
    }

    // -----------------------------------------------------------------------
    // LEVEL_DOWN event
    // -----------------------------------------------------------------------

    @Test
    void levelDownEventDecreasesLevel() {
        controller.board.level = 3;
        controller.processEvent(GameEvent.LEVEL_DOWN);
        assertEquals(2, controller.board.level);
    }

    @Test
    void levelDownEventDoesNotGoBelowZero() {
        controller.board.level = 0;
        controller.processEvent(GameEvent.LEVEL_DOWN);
        assertEquals(0, controller.board.level);
    }

    @Test
    void levelDownEventResetsMatchCounter() {
        controller.board.figuresMatchedCounter = 20;
        controller.processEvent(GameEvent.LEVEL_DOWN);
        assertEquals(0, controller.board.figuresMatchedCounter);
    }

    // -----------------------------------------------------------------------
    // PAUSE event
    // -----------------------------------------------------------------------

    @Test
    void pauseEventExitsWhenKeyIsPressed() {
        // FakePlatform.delay() advances time and we prime a key press
        // after a single delay cycle so the pause loop terminates.
        // We do this by pre-setting keyPressed=true before the pause event,
        // which makes the while(!isKeyPressed) condition immediately false.
        platform.setKeyPressed(true);
        // Should not hang — exits immediately because key is already pressed
        controller.processEvent(GameEvent.PAUSE);
        // After pause, tc is reset to current time
        assertEquals(platform.currentTime(), platform.getTc());
    }

    // -----------------------------------------------------------------------
    // NONE event (no-op safety check)
    // -----------------------------------------------------------------------

    @Test
    void noneEventDoesNotChangeFigurePosition() {
        int xBefore = controller.board.figure.x;
        int yBefore = controller.board.figure.y;
        controller.processEvent(GameEvent.NONE);
        assertEquals(xBefore, controller.board.figure.x);
        assertEquals(yBefore, controller.board.figure.y);
    }

    // -----------------------------------------------------------------------
    // processEvent clears key-pressed flag regardless of event type
    // -----------------------------------------------------------------------

    @Test
    void processEventAlwaysClearsKeyPressed() {
        platform.setKeyPressed(true);
        controller.processEvent(GameEvent.LEFT);
        assertFalse(platform.isKeyPressed());
    }
}
