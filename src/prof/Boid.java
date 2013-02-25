package prof;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Boid {

	private final double coeffAvoid = 0.15;
	private final double coeffAlign = 0.33;
	private final double coeffAggregate = 0.33;
	
	private double xCenter=0.0; 
	private double yCenter=0.0; 
	private double orientation=Math.PI/2.0; 
	
	private double rotationalSpeed=0.0;
	private double translationalSpeed=0.0;

	private int worldWidth = 0;
	private int worldHeight = 0;

	ArrayList<Boid> neighbors = new ArrayList<Boid>();

	public Boid(double x, double y, int inWorldWidth, int inWorldHeight)
	{
		xCenter=x;
		yCenter=y;

		worldWidth = inWorldWidth;
		worldHeight = inWorldHeight;
	}

	public Boid(double x, double y, double inOrientation, int inWorldWidth, int inWorldHeight)
	{
		xCenter=x;
		yCenter=y;
		orientation=inOrientation;

		worldWidth = inWorldWidth;
		worldHeight = inWorldHeight;
	}

	/**
	 * Fonction calculant le vecteur de mouvement idéal pour que le boid courant
	 * s'éloigne de ses voisins.
	 * Si le boid ne possède pas de voisin, alors le vecteur retourné correspond à
	 * une vitesse de rotation nulle et à la précédente vitesse de translation
	 * 
	 * @return desiredMovement le vecteur de mouvement désiré pour s'éloigner de ses voisin 
	 */
	public Vector avoid( )
	{
		double localRotSpeed = 0.0;
		if (neighbors.size() > 0)
		{
			double averageX = 0.0;
			double averageY = 0.0;
			for (int index = 0 ; index < neighbors.size() ; index++)
			{
				Point2d neighbor = neighbors.get(index).getPosition();
				averageX += (xCenter-neighbor.x);
				averageY += (yCenter-neighbor.y);
			}
			averageX = averageX / neighbors.size();
			averageY = averageY / neighbors.size();
			double averageOrientation = Math.atan2(averageY,averageX);
			double deltaAngle = averageOrientation - orientation;
			if (deltaAngle < -Math.PI) deltaAngle = deltaAngle + 2*Math.PI;
			if (deltaAngle > Math.PI) deltaAngle = deltaAngle - 2*Math.PI;
			localRotSpeed = deltaAngle/4.0;
		}
		Vector desiredMovement = new Vector(localRotSpeed,5.0);
		return desiredMovement;
	}

	public Vector align( )
	{
		double localRotSpeed = 0.0;
		if (neighbors.size() > 0)
		{
			double averageX = 0.0;
			double averageY = 0.0;
			for (int index = 0 ; index < neighbors.size() ; index++)
			{
				double orientationNeighbor = neighbors.get(index).getOrientation();
				averageX += Math.cos(orientationNeighbor);
				averageY += Math.sin(orientationNeighbor);
			}
			averageX = averageX / neighbors.size();
			averageY = averageY / neighbors.size();
			double averageOrientation = Math.atan2(averageY,averageX);
			double deltaAngle = averageOrientation - orientation;
			if (deltaAngle < -Math.PI) deltaAngle = deltaAngle + 2*Math.PI;
			if (deltaAngle > Math.PI) deltaAngle = deltaAngle - 2*Math.PI;
			localRotSpeed = deltaAngle/4.0;
		}
		Vector desiredMovement = new Vector(localRotSpeed,0.0);
		return desiredMovement;
	}

	public Vector aggregate( )
	{
		double localRotSpeed = 0.0;
		double localTransSpeed = 0.0;
		if (neighbors.size() > 0)
		{
			double averageX = 0.0;
			double averageY = 0.0;
			for (int index = 0 ; index < neighbors.size() ; index++)
			{
				Point2d neighbor = neighbors.get(index).getPosition();
				averageX = averageX + neighbor.x;
				averageY = averageY + neighbor.y;
			}
			averageX = averageX/neighbors.size();
			averageY = averageY/neighbors.size();
			double direction = Math.atan2(averageY-yCenter,averageX-xCenter);
			double deltaAngle = direction - orientation;
			if (deltaAngle < -Math.PI) deltaAngle = deltaAngle + 2*Math.PI;
			if (deltaAngle > Math.PI) deltaAngle = deltaAngle - 2*Math.PI;
			localRotSpeed = deltaAngle/4.0;
			localTransSpeed = (Math.sqrt(((averageX - xCenter) * (averageX - xCenter)) + ((averageY - yCenter) * (averageY - yCenter))))/4.0;
		}
		Vector desiredMovement = new Vector(localRotSpeed,localTransSpeed);
		return desiredMovement;
	}
		
	/**
	 * Procédure calculant le nouveau vecteur de mouvement en prenant en compte 
	 * la volonté d'aggrégation, d'éloignement et d'alignement par rapport aux voisins actuels.
	 * 
	 */
	public void controllerBoid( )
	{
		Vector avoidVector = avoid();
		Vector aggregateVector = aggregate();
		Vector alignVector = align();

		double averageX = 0.0;
		double averageY = 0.0;
		averageX += coeffAvoid * avoidVector.getLength() * Math.cos(avoidVector.getOrientation());
		averageY += coeffAvoid * avoidVector.getLength() * Math.sin(avoidVector.getOrientation());
		averageX += coeffAggregate * aggregateVector.getLength() * Math.cos(aggregateVector.getOrientation());
		averageY += coeffAggregate * aggregateVector.getLength() * Math.sin(aggregateVector.getOrientation());
		averageX += coeffAlign * alignVector.getLength() * Math.cos(alignVector.getOrientation());
		averageY += coeffAlign * alignVector.getLength() * Math.sin(alignVector.getOrientation());
		rotationalSpeed = Math.atan2(averageY,averageX);
		translationalSpeed = Math.sqrt((averageX * averageX) + (averageY * averageY));
	}

	/**
		set variables
	*/
	public void setNeighbors (ArrayList<Boid> inNeighbors)
	{
		neighbors = inNeighbors;
	}

	public void setRotationalSpeed (double inRotationalSpeed)
	{
		if (inRotationalSpeed < -Math.PI) rotationalSpeed = -Math.PI;
		else if (inRotationalSpeed > Math.PI) rotationalSpeed = Math.PI;
		else rotationalSpeed = inRotationalSpeed;
	}

	public void setTranslationalSpeed (double inTranslationalSpeed)
	{
		if (inTranslationalSpeed < 0) translationalSpeed = 0;
		else if (inTranslationalSpeed > 20) translationalSpeed = 20;
		else translationalSpeed = inTranslationalSpeed;
	}

	/**
		get variables
	*/

	public Point2d getPosition( )
	{
		return new Point2d(xCenter,yCenter);
	}

	public double getOrientation( )
	{
		return orientation;
	}

	/**
		compute movements
	*/

	public void applyDynamics( )
	{
		orientation = orientation + rotationalSpeed;
		if (orientation < -Math.PI) orientation = orientation + 2*Math.PI;
		if (orientation > Math.PI) orientation = orientation - 2*Math.PI;

		xCenter = xCenter + (translationalSpeed*Math.cos(orientation));
		yCenter = yCenter + (translationalSpeed*Math.sin(orientation));

		if (xCenter < 0) xCenter = worldWidth + xCenter;
		if (xCenter >= worldWidth) xCenter = xCenter - worldWidth ;

		if (yCenter < 0) yCenter = worldHeight + yCenter;
		if (yCenter >= worldHeight) yCenter = yCenter - worldHeight ;
	}

	/**
	 * Procédure permettant d'afficher le boid sur une bufferImage passée en paramètre
	 * Monde continu, si un boid dépasse une limite, il ressort par la limite opposée
	 * @param Image : le buffer image sur lequel le boid doit être affiché
	*/
	public void traceBoid(ImageBuffer Image)
	{	
		/*Calcul des points du triangle en fonction de l'orientation*/
		double x1 = 10*Math.cos(orientation);
		double y1 = 10*Math.sin(orientation);
		double x2 = 10*Math.cos(orientation+((3*Math.PI)/4.0));
		double y2 = 10*Math.sin(orientation+((3*Math.PI)/4.0));
		double x3 = 10*Math.cos(orientation-((3*Math.PI)/4.0));
		double y3 = 10*Math.sin(orientation-((3*Math.PI)/4.0));
		Point2d s1 = new Point2d(x1,y1);
		Point2d s2 = new Point2d(x2,y2);
		Point2d s3 = new Point2d(x3,y3);
		
		/*Pour (x,y) allant de (-20,-20) à (20,20)*/
		for ( int x = -20 ; x < 20 ; x++ )
		{
			for ( int y = -20 ; y < 20 ; y++ )
			{
				Point2d current = new Point2d(x,y);
				
				/* Si (x,y) est à droite du triangle (s1,s2,s3) */
				if (right(s1,s2,current) && right(s2,s3,current) && right(s3,s1,current))
				{
					
					int drawX = x + (int) xCenter;
					int drawY = y + (int) yCenter;

					if (drawX < 0) drawX = worldWidth + drawX;
					if (drawX >= worldWidth) drawX = drawX - worldWidth ;

					if (drawY < 0) drawY = worldHeight + drawY;
					if (drawY >= worldHeight) drawY = drawY - worldHeight ;

					Image.setPixel(drawX, drawY,128,128,128);
				}
			}
		}
	}

	/*---------------------------------------------------------------------
		compute the area of a triangle thanks to the cross product of a two edges vector
		positive if abc are oriented counter-clockwise (c on the left of ab)
		negative if the triangle is oriented clockwise (c on the right of ab)
		return only the sign of the area
		---------------------------------------------------------------------*/
	public int areaSign( Point2d a, Point2d b, Point2d c )
	{
		
		//area2 : equal to 2 times the area of the triangle abc
		double area2 =	( b.x - a.x ) * (double)( c.y - a.y ) -
			( c.x - a.x ) * (double)( b.y - a.y );

		/* The area should be an integer. */
		if      ( area2 >  0.5 ) return  1;
		else if ( area2 < -0.5 ) return -1;
		else                     return  0;
	}

	/*---------------------------------------------------------------------
		Returns true if c is strictly to the right of the directed
		line through a to b.
		---------------------------------------------------------------------*/
	public boolean right( Point2d a, Point2d b, Point2d c )
	{ 
		return  areaSign( a, b, c ) > 0;
	}
	
	// ----- ----- ----- ----- -----


}
