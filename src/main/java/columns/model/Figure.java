package columns.model;

import columns.model.kernel.RandomGenerator;

class Figure {


	public int x = GameConfig.WIDTH / 2 + 1, y = 1, c[] = new int[4];

	public Figure(RandomGenerator random)
	{
		x = GameConfig.WIDTH / 2 + 1;
		y = 1;
		c[0] = 0;
		c[1] = Math.abs(random.nextInt())%7+1;
		c[2] = Math.abs(random.nextInt())%7+1;
		c[3] = Math.abs(random.nextInt())%7+1;
	}

	public void moveRight() {
		x++;
	}

	public void moveLeft() {
		x--;
	}

	public void rotateUp() {
		int t = c[1];
		c[1] = c[2];
		c[2] = c[3];
		c[3] = t;
	}

	public void rotateDown() {
		int t = c[1];
		c[1] = c[3];
		c[3] = c[2];
		c[2] = t;
	}

	public void moveDown() {
		y++;
	}
}
