package columns.original;

import java.util.Random;


class Figure {

	static int x = Columns.Width / 2 + 1, y = 1, c[] = new int[4];
	static Random r = new Random();

	Figure()
	{
		x = Columns.Width / 2 + 1;
		y = 1;
		c[0] = 0;
		c[1] = Math.abs(r.nextInt())%7+1;
		c[2] = Math.abs(r.nextInt())%7+1;
		c[3] = Math.abs(r.nextInt())%7+1;
	}
}