package columns.model;

import columns.model.kernel.Screen;

class View {

	private Screen screen;

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public void drawBox(int x, int y, int c) {
		if (c == 0) {
			screen.setColor(Black());
			screen.fillRect(GameConfig.LEFT_BORDER + x * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE,
					GameConfig.TOP_BORDER + y * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE, GameConfig.BOX_SIZE, GameConfig.BOX_SIZE);
			screen.drawRect(GameConfig.LEFT_BORDER + x * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE,
					GameConfig.TOP_BORDER + y * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE, GameConfig.BOX_SIZE, GameConfig.BOX_SIZE);
		} else if (c == 8) {
			screen.setColor(screen.White());
			screen.drawRect(GameConfig.LEFT_BORDER + x * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE + 1,
					GameConfig.TOP_BORDER + y * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE + 1, GameConfig.BOX_SIZE - 2,
					GameConfig.BOX_SIZE - 2);
			screen.drawRect(GameConfig.LEFT_BORDER + x * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE + 2,
					GameConfig.TOP_BORDER + y * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE + 2, GameConfig.BOX_SIZE - 4,
					GameConfig.BOX_SIZE - 4);
			screen.setColor(Black());
			screen.fillRect(GameConfig.LEFT_BORDER + x * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE + 3,
					GameConfig.TOP_BORDER + y * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE + 3, GameConfig.BOX_SIZE - 6,
					GameConfig.BOX_SIZE - 6);
		} else {
			screen.setColor(c);
			screen.fillRect(GameConfig.LEFT_BORDER + x * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE,
					GameConfig.TOP_BORDER + y * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE, GameConfig.BOX_SIZE, GameConfig.BOX_SIZE);
			screen.setColor(Black());
			screen.drawRect(GameConfig.LEFT_BORDER + x * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE,
					GameConfig.TOP_BORDER + y * GameConfig.BOX_SIZE - GameConfig.BOX_SIZE, GameConfig.BOX_SIZE, GameConfig.BOX_SIZE);
		}
		// g.setColor (Color.black);
	}

	int Black() {
		return screen.Black();
	}

	public void drawVerticalTripletAt(int x, int y, int color1, int color2, int color3) {
		drawBox(x, y, color1);
		drawBox(x, y + 1, color2);
		drawBox(x, y + 2, color3);
	}

	public void drawFigure(Figure f) {
		drawVerticalTripletAt(f.x, f.y, f.c[1], f.c[2], f.c[3]);
	}

	public void hideFigure(Figure f) {
		drawVerticalTripletAt(f.x, f.y, 0, 0, 0);
	}

	public void drawField(int[][] field) {
		for (int i = 1; i <= GameConfig.DEPTH; i++) {
			for (int j = 1; j <= GameConfig.WIDTH; j++) {
				drawBox(j, i, field[j][i]);
			}
		}
	}


	public void showHelp() {
		screen.setColor(Black());
	
		screen.drawString(" Keys available:", 200 - GameConfig.LEFT_BORDER, 102);
		screen.drawString("Roll Box Up:     ", 200 - GameConfig.LEFT_BORDER, 118);
		screen.drawString("Roll Box Down:   ", 200 - GameConfig.LEFT_BORDER, 128);
		screen.drawString("Figure Left:     ", 200 - GameConfig.LEFT_BORDER, 138);
		screen.drawString("Figure Right:    ", 200 - GameConfig.LEFT_BORDER, 148);
		screen.drawString("Level High/Low: +/-", 200 - GameConfig.LEFT_BORDER, 158);
		screen.drawString("Drop Figure:   space", 200 - GameConfig.LEFT_BORDER, 168);
		screen.drawString("Pause:           P", 200 - GameConfig.LEFT_BORDER, 180);
		screen.drawString("Quit:     Esc or Q", 200 - GameConfig.LEFT_BORDER, 190);
	}

	public void showLevel(int level) {
		screen.setColor(Black());
		screen.clearRect(GameConfig.LEFT_BORDER + 100, 390, 100, 20);
		screen.drawString("Level: " + level, GameConfig.LEFT_BORDER + 100, 400);
	}

	public void showScore(long score) {
		screen.setColor(Black());
		screen.clearRect(GameConfig.LEFT_BORDER, 390, 100, 20);
		screen.drawString("Score: " + score, GameConfig.LEFT_BORDER, 400);
	}


}
