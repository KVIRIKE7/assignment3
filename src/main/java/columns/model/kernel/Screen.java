package columns.model.kernel;

public interface Screen {

	void setColor(int color);

	void fillRect(int x, int y, int width, int height);

	void drawRect(int x, int y, int width, int height);

	void drawString(String string, int x, int y);

	void clearRect(int x, int y, int width, int height);

	int Black();

	int White();

}
