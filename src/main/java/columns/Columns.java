package columns;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.util.Random;

import columns.model.GameController;
import columns.model.GameEvent;
import columns.model.kernel.Platform;
import columns.model.kernel.RandomGenerator;
import columns.model.kernel.Screen;

public class Columns extends Applet implements Runnable, Platform {

	static private RandomGenerator random = new RandomGenerator() {

		Random r = new Random();

		@Override
		public int nextInt() {
			return r.nextInt();
		}
	};

	private int keyPressed;
	private long tc;
	private boolean isKeyPressed = false;
	private Graphics graphics;
	private Thread thr = null;
	private GameController controller;


	// Applet methods

	@Override
	public void init() {
		graphics = getGraphics();
		graphics.setColor(Color.black);
		setFocusable(true);
		controller = new GameController(this);
	}

	@Override
	public boolean keyDown(Event e, int k) {
		setKeyPressed(true);
		keyPressed = e.key;
		return true;
	}

	@Override
	public boolean lostFocus(Event e, Object w) {
		setKeyPressed(true);
		keyPressed = 'P';
		return true;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		controller.renderGameView();
		requestFocus();
	}

	@Override
	public void start() {
		graphics.setColor(Color.black);

		if (thr == null) {
			thr = new Thread(this);
			thr.start();
		}
		requestFocus();
	}

	@Override
	public void stop() {
		if (thr != null) {
			thr.stop();
			thr = null;
		}
	}

	// ---< Runnable >------

	@Override
	public void run() {
		controller.runGameLoop(this);
	}

	// --------------< Platform methods >-----------

	@Override
	public void delay(long t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public boolean isKeyPressed() {
		return isKeyPressed;
	}

	@Override
	public void setKeyPressed(boolean isKeyPressed) {
		this.isKeyPressed = isKeyPressed;
	}

	@Override
	public Screen getScreen() {
		return new AppletScreen(graphics);
	}

	@Override
	public long currentTime() {
		return System.currentTimeMillis();
	}

	@Override
	public long getTc() {
		return tc;
	}

	@Override
	public void setTc(long tc) {
		this.tc = tc;
	}

	@Override
	public int getKeyPressed() {
		return keyPressed;
	}

	@Override
	public GameEvent getEvent() {
		GameEvent event = GameEvent.NONE;
		switch (keyPressed) {
		case Event.LEFT:
		event = GameEvent.LEFT;
			break;
		case Event.RIGHT:
			event = GameEvent.RIGHT;
			break;
		case Event.UP:
			event = GameEvent.UP;
			break;
		case Event.DOWN:
			event = GameEvent.DOWN;
			break;
		case ' ':
			event = GameEvent.DROP;
			break;
		case 'P':
		case 'p':
			event = GameEvent.PAUSE;
			break;
		case '-':
			event = GameEvent.LEVEL_DOWN;
			break;
		case '+':
			event = GameEvent.LEVEL_UP;
			break;
		}
		return event;
	}

	@Override
	public RandomGenerator getRandomGenerator() {
		return random;
	}
}
