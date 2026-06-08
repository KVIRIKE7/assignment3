package columns.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Board behavior: boundaries, paste, drop, match detection,
 * collapse/pack, scoring, level changes, and game-over detection.
 */
class BoardTest {

    private Board board;
    private FakeModelListener listener;

    @BeforeEach
    void setUp() {
        board = new Board();
        board.initFields();
        listener = new FakeModelListener();
        board.setModelListener(listener);
        board.initBoard();
        // Give board a default figure at center so boundary tests have a valid figure
        board.figure = figureAt(GameConfig.WIDTH / 2 + 1, 1);
    }

    /** Creates a figure placed at (x, y) with colors 1,2,3. */
    private Figure figureAt(int x, int y) {
        Figure f = new Figure(new FakeRandom(1, 2, 3));
        f.x = x;
        f.y = y;
        return f;
    }

    // -----------------------------------------------------------------------
    // initBoard
    // -----------------------------------------------------------------------

    @Test
    void initBoardClearsAllCells() {
        board.newField[3][5] = 4;
        board.initBoard();
        assertEquals(0, board.newField[3][5]);
    }

    @Test
    void initBoardResetsScore() {
        board.Score = 9999;
        board.initBoard();
        assertEquals(0, board.Score);
    }

    @Test
    void initBoardResetsLevel() {
        board.level = 5;
        board.initBoard();
        assertEquals(0, board.level);
    }

    @Test
    void initBoardResetsFiguresMatchedCounter() {
        board.figuresMatchedCounter = 20;
        board.initBoard();
        assertEquals(0, board.figuresMatchedCounter);
    }

    // -----------------------------------------------------------------------
    // pasteFigure
    // -----------------------------------------------------------------------

    @Test
    void pasteFigureWritesColorsAtCorrectRows() {
        Figure f = figureAt(3, 5);
        board.pasteFigure(f);
        assertEquals(1, board.newField[3][5]);
        assertEquals(2, board.newField[3][6]);
        assertEquals(3, board.newField[3][7]);
    }

    @Test
    void pasteFigureDoesNotAffectOtherColumns() {
        Figure f = figureAt(3, 5);
        board.pasteFigure(f);
        assertEquals(0, board.newField[2][5]);
        assertEquals(0, board.newField[4][5]);
    }

    // -----------------------------------------------------------------------
    // canMoveLeft / canMoveRight boundaries
    // -----------------------------------------------------------------------

    @Test
    void cannotMoveLeftAtLeftBorder() {
        board.figure = figureAt(1, 1);
        assertFalse(board.canMoveLeft());
    }

    @Test
    void canMoveLeftWhenNotAtBorderAndPathClear() {
        board.figure = figureAt(3, 1);
        assertTrue(board.canMoveLeft());
    }

    @Test
    void cannotMoveLeftWhenBlockedByCell() {
        board.figure = figureAt(4, 1);
        // Place a block at the cell the figure would move into (x-1, y+2)
        board.newField[3][3] = 2;
        assertFalse(board.canMoveLeft());
    }

    @Test
    void cannotMoveRightAtRightBorder() {
        board.figure = figureAt(GameConfig.WIDTH, 1);
        assertFalse(board.canMoveRight());
    }

    @Test
    void canMoveRightWhenNotAtBorderAndPathClear() {
        board.figure = figureAt(3, 1);
        assertTrue(board.canMoveRight());
    }

    @Test
    void cannotMoveRightWhenBlockedByCell() {
        board.figure = figureAt(4, 1);
        board.newField[5][3] = 2;
        assertFalse(board.canMoveRight());
    }

    // -----------------------------------------------------------------------
    // figureMayMoveDown
    // -----------------------------------------------------------------------

    @Test
    void figureMayNotMoveDownAtBottom() {
        // At DEPTH-2 the figure bottom (y+2) would be at DEPTH, which is floor
        board.figure = figureAt(3, GameConfig.DEPTH - 2);
        assertFalse(board.figureMayMoveDown());
    }

    @Test
    void figureMayNotMoveDownWhenCellBelowIsOccupied() {
        board.figure = figureAt(3, 5);
        // cell below bottom of figure = y+3 = row 8
        board.newField[3][8] = 1;
        assertFalse(board.figureMayMoveDown());
    }

    @Test
    void figureMayMoveDownWhenPathIsClear() {
        board.figure = figureAt(3, 1);
        assertTrue(board.figureMayMoveDown());
    }

    // -----------------------------------------------------------------------
    // dropFigure
    // -----------------------------------------------------------------------

    @Test
    void dropFigureLandsAboveOccupiedCell() {
        // Fill bottom row so the figure drops to just above it
        for (int col = 1; col <= GameConfig.WIDTH; col++) {
            board.newField[col][GameConfig.DEPTH] = 1;
        }
        Figure f = figureAt(3, 1);
        board.dropFigure(f);
        // Bottom of figure is y+2; bottom row is DEPTH which is filled,
        // so figure lands at DEPTH-1-2 = DEPTH-3
        assertEquals(GameConfig.DEPTH - 3, f.y);
    }

    @Test
    void dropFigureOnEmptyBoardGoesToNearBottom() {
        Figure f = figureAt(3, 1);
        board.dropFigure(f);
        // Empty board: lowest free cell is DEPTH, figure bottom at y+2=DEPTH → y=DEPTH-2
        assertEquals(GameConfig.DEPTH - 2, f.y);
    }

    // -----------------------------------------------------------------------
    // isFieldFull (game-over detection)
    // -----------------------------------------------------------------------

    @Test
    void isFieldFullFalseOnEmptyBoard() {
        assertFalse(board.isFieldFull());
    }

    @Test
    void isFieldFullTrueWhenAnyColumnInRow3HasBlock() {
        board.newField[4][3] = 2;
        assertTrue(board.isFieldFull());
    }

    @Test
    void isFieldFullFalseWhenOnlyRow4HasBlock() {
        board.newField[4][4] = 2;
        assertFalse(board.isFieldFull());
    }

    // -----------------------------------------------------------------------
    // findMatches — vertical, horizontal, diagonal
    // -----------------------------------------------------------------------

    @Test
    void findMatchesDetectsVerticalTriplet() {
        // Three same-color cells in column 3, rows 5-7
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        assertFalse(board.noChanges, "Should detect vertical triplet");
    }

    @Test
    void findMatchesDetectsHorizontalTriplet() {
        // Three same-color cells in row 8, columns 3-5
        board.newField[3][8] = 3;
        board.newField[4][8] = 3;
        board.newField[5][8] = 3;
        board.findMatches();
        assertFalse(board.noChanges, "Should detect horizontal triplet");
    }

    @Test
    void findMatchesDetectsDiagonalTriplet() {
        // Diagonal: (3,7), (4,8), (5,9)
        board.newField[3][7] = 5;
        board.newField[4][8] = 5;
        board.newField[5][9] = 5;
        board.findMatches();
        assertFalse(board.noChanges, "Should detect diagonal triplet");
    }

    @Test
    void findMatchesNoMatchLeavesNoChangesTrue() {
        board.newField[3][5] = 1;
        board.newField[3][6] = 2;
        board.newField[3][7] = 3;
        board.findMatches();
        assertTrue(board.noChanges, "No matching triplet — noChanges should remain true");
    }

    @Test
    void findMatchesIncrementsScorePerTriplet() {
        // level=0: each triplet adds (0+1)*10 = 10
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        assertEquals(10L, board.Score);
    }

    @Test
    void findMatchesClearsMatchedCellsInOldField() {
        board.newField[3][5] = 4;
        board.newField[3][6] = 4;
        board.newField[3][7] = 4;
        board.findMatches();
        // After findMatches, matched cells in oldField should be zero
        assertEquals(0, board.oldField[3][5]);
        assertEquals(0, board.oldField[3][6]);
        assertEquals(0, board.oldField[3][7]);
    }

    @Test
    void findMatchesFiresTripletDetectedCallback() {
        board.newField[3][5] = 6;
        board.newField[3][6] = 6;
        board.newField[3][7] = 6;
        board.findMatches();
        assertFalse(listener.detectedTriplets.isEmpty(), "Listener should receive triplet notification");
    }

    // -----------------------------------------------------------------------
    // collapse / packField
    // -----------------------------------------------------------------------

    @Test
    void collapsePacksRemainingCellsDownward() {
        // Put a block at row 5, leave row 6 empty
        board.newField[3][5] = 1;
        board.newField[3][6] = 0;
        board.newField[3][7] = 0;

        // Simulate: oldField has block at 5 (survived matching), nothing at 6/7
        board.oldField[3][5] = 1;

        board.collapse();

        // After pack, the surviving block should have fallen to the bottom row
        assertEquals(1, board.newField[3][GameConfig.DEPTH]);
        assertEquals(0, board.newField[3][5]);
    }

    @Test
    void collapseFiresFieldWasUpdatedCallback() {
        board.collapse();
        assertEquals(1, listener.fieldUpdatedCount);
    }

    @Test
    void collapseFiresScoreUpdatedCallback() {
        board.collapse();
        assertEquals(1, listener.scoreUpdatedCount);
    }

    @Test
    void collapseAddsDScoreToScore() {
        board.DScore = 25;
        board.Score = 10;
        board.collapse();
        assertEquals(35L, board.Score);
    }

    // -----------------------------------------------------------------------
    // Scoring behavior
    // -----------------------------------------------------------------------

    @Test
    void scoreIncreasesMoreAtHigherLevel() {
        // level 0 → 10 per triplet; level 2 → 30 per triplet
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        long scoreAtLevel0 = board.Score;

        board.initBoard();
        board.level = 2;
        board.newField[3][5] = 2;
        board.newField[3][6] = 2;
        board.newField[3][7] = 2;
        board.findMatches();
        long scoreAtLevel2 = board.Score;

        assertTrue(scoreAtLevel2 > scoreAtLevel0,
                "Higher level should produce higher score per triplet");
    }

    // -----------------------------------------------------------------------
    // Level changes
    // -----------------------------------------------------------------------

    @Test
    void levelIncreasesAfterMatchThreshold() {
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
        // Trigger changeLevelIfNeeded indirectly via collapse (which calls it)
        board.collapse();
        assertEquals(1, board.level);
    }

    @Test
    void levelDoesNotExceedMaxLevel() {
        board.level = GameConfig.MAX_LEVEL;
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
        board.collapse();
        assertEquals(GameConfig.MAX_LEVEL, board.level);
    }

    @Test
    void levelChangedCallbackFiresOnLevelUp() {
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD;
        board.collapse();
        assertEquals(1, listener.levelChangedCount);
        assertEquals(1, listener.lastLevel);
    }

    @Test
    void levelChangedCallbackNotFiredBelowThreshold() {
        board.figuresMatchedCounter = GameConfig.NEXT_LEVEL_THRESHOLD - 1;
        board.collapse();
        assertEquals(0, listener.levelChangedCount);
    }
}
