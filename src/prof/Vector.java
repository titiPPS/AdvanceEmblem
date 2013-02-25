package prof;

public class Vector {
	double length = 0.0;
	double orientation = 0.0;
	public Vector(double inOrientation,  double inLength)
	{
		orientation = inOrientation;
		length = inLength;
	}
	public double getOrientation()
	{
		return orientation;
	}
	public double getLength()
	{
		return length;
	}
}
