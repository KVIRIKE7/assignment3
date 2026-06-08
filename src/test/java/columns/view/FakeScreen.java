package columns.model;

import columns.model.kernel.Screen;
import java.util.ArrayList;
import java.util.List;

/**
 * Fake Screen that records every draw call without touching AWT.
 */
class FakeScreen implements Screen {

    List<String> calls = new ArrayList<>();
    int lastColor = 0;

    @Override
    public void setColor(int color) {
        lastColor = color;
        calls.add("setColor:" + color);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        calls.add("fillRect:" + x + "," + y + "," + width + "," + height);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        calls.add("drawRect:" + x + "," + y + "," + width + "," + height);
    }

    @Override
    public void drawString(String string, int x, int y) {
        calls.add("drawString:" + string + "@" + x + "," + y);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        calls.add("clearRect:" + x + "," + y + "," + width + "," + height);
    }

    @Override
    public int Black() { return 0; }

    @Override
    public int White() { return 1; }

    boolean hasCall(String prefix) {
        return calls.stream().anyMatch(c -> c.startsWith(prefix));
    }
}
