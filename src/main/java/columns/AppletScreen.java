package columns;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;

import columns.model.kernel.Screen;


public class AppletScreen implements Screen {

	private static final Color[] COLORS = { Color.black, Color.cyan, Color.blue, Color.red, Color.green, Color.yellow,
			Color.pink, Color.magenta, Color.white };

	private Graphics g;

	public AppletScreen(Graphics g) {
		this.g = g;
	}

	@Override
	public void setColor(int color) {
		g.setColor(COLORS[color]);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		g.fillRect(x, y, width, height);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);

	}

	@Override
	public void drawString(String string, int x, int y) {
		g.drawString(string, x, y);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		g.clearRect(x, y, width, height);
	}

	@Override
	public int Black() {
		return Arrays.asList(COLORS).indexOf(Color.black);
	}

	@Override
	public int White() {
		return Arrays.asList(COLORS).indexOf(Color.white);
	}

}
