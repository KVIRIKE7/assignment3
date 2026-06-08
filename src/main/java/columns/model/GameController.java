package columns.model;

import columns.model.kernel.ModelListener;
import columns.model.kernel.Platform;

public class GameController implements ModelListener {

	Board board;
	View view = new View();
	Platform platform;

	public GameController(Platform platform) {
		this.platform = platform;
		board = new Board();
		board.setModelListener(this);
		board.initFields();
		view.setScreen(platform.getScreen());
	}

	public void renderGameView() {
		view.showHelp();
		view.showLevel(board.level);
		view.showScore(board.Score);
		view.drawField(board.newField);
		view.drawFigure(board.figure);
	}

	public void runGameLoop(Platform platform) {
		board.initBoard();
	
		do {
			platform.setTc(platform.currentTime());
			board.figure = new Figure(platform.getRandomGenerator());
			view.drawFigure(board.figure);
			while (board.figureMayMoveDown()) {
				checkTimeAndMoveDownIfNeeded(platform);
				board.DScore = 0;
				processUserEventsIfAny(platform);
			}
			board.pasteFigure(board.figure);
			do {
				board.noChanges = true;
				board.findMatches();
				if (foundMatches()) {
					platform.delay(500);
					board.collapse();
				}
			} while (foundMatches());
		} while (!board.isFieldFull());
	}

	boolean foundMatches() {
		return !board.noChanges;
	}

	void processUserEventsIfAny(Platform platform) {
		do {
			platform.delay(50);
			if (platform.isKeyPressed()) {
				processEvent(platform.getEvent());
			}
		} while ((int) (platform.currentTime() - platform.getTc()) <= (GameConfig.MAX_LEVEL - board.level) * GameConfig.TIME_SHIFT
				+ GameConfig.MIN_TIME_SHIFT);
	}

	void checkTimeAndMoveDownIfNeeded(Platform platform) {
		if ((int) (platform.currentTime() - platform.getTc()) > (GameConfig.MAX_LEVEL - board.level)
				* GameConfig.TIME_SHIFT
				+ GameConfig.MIN_TIME_SHIFT) {
			platform.setTc(platform.currentTime());
			view.hideFigure(board.figure);
			board.figure.moveDown();
			view.drawFigure(board.figure);
		}
	}

	@Override
	public void levelHasChanged(int level) {
		view.showLevel(level);
	}

	@Override
	public void tripletDetected(int a, int b, int c, int d, int i, int j) {
		view.drawBox(a, b, 8);
		view.drawBox(j, i, 8);
		view.drawBox(c, d, 8);
	}

	@Override
	public void fieldWasUpdated(int[][] field) {
		view.drawField(field);
	}

	@Override
	public void scoreUpdated(long score) {
		view.showScore(score);
	}

	void processEvent(GameEvent event) {
		platform.setKeyPressed(false);
		switch (event) {
		case LEFT:
			if (board.canMoveLeft()) {
				view.hideFigure(board.figure);
				board.figure.moveLeft();
				view.drawFigure(board.figure);
			}
			break;
		case RIGHT:
			if (board.canMoveRight()) {
				view.hideFigure(board.figure);
				board.figure.moveRight();
				view.drawFigure(board.figure);
			}
			break;
		case UP:
			board.figure.rotateUp();
			view.drawFigure(board.figure);
			break;
		case DOWN:
			board.figure.rotateDown();
			view.drawFigure(board.figure);
			break;
		case DROP:
			view.hideFigure(board.figure);
			board.dropFigure(board.figure);
			view.drawFigure(board.figure);
			platform.setTc(0);
			break;
		case PAUSE:
			while (!platform.isKeyPressed()) {
				view.hideFigure(board.figure);
				platform.delay(500);
				view.drawFigure(board.figure);
				platform.delay(500);
			}
			platform.setTc(platform.currentTime());
			break;
		case LEVEL_DOWN:
			if (board.level > 0) {
				board.level = board.level - 1;
			}
			board.figuresMatchedCounter = 0;
			view.showLevel(board.level);
			break;
		case LEVEL_UP:
			if (board.level < GameConfig.MAX_LEVEL) {
				board.level = board.level + 1;
			}
			board.figuresMatchedCounter = 0;
			view.showLevel(board.level);
			break;
		default:
			break;
		}
	}


}
