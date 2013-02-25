package prof;

public class Point2d {
	public double x=0;
	public double y=0;
	public Point2d (double inX, double inY)
	{
		x = inX;
		y = inY;
	}
	public Point2d (int inX, int inY)
	{
		x = (double)inX;
		y = (double)inY;
	}
}

