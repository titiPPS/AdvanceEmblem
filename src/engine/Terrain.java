package engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Terrain {
	protected boolean _bIsAccessible, _isSelected;
	protected int _coutMvt;
	protected double _coeffDef,_coeffPre;
	protected int _def;
	protected Agent _occupant;
	protected Factory _usine;
	protected ArrayList<Terrain> _lstVoisins;
	protected int _x,_y;
	
	
	public Terrain(int x, int y,int mvtCost, boolean isAccessible,int def) {
		_x = x;
		_y = y;
		_coutMvt = mvtCost;
		_bIsAccessible = isAccessible;
		_isSelected = false;
		/*_coeffDef = def;
		_coeffPre = pre;*/
		_def = def;
		_occupant = null;
		_usine = null;
		_lstVoisins = new ArrayList<Terrain>();
	}
	
	
	protected abstract BufferedImage getBaseImage();
	protected abstract BufferedImage getCtrlImage();
	protected abstract BufferedImage getSelectImage();
	public abstract String getName();
	
	public void ajouterVoisin(Terrain t) {
		if(t._bIsAccessible && _bIsAccessible && !_lstVoisins.contains(t)) {
			_lstVoisins.add(t);
		}
	}
	
	public ArrayList<Terrain> getListeVoisins() {
		return _lstVoisins;
	}
	
	public BufferedImage getImage() {
		BufferedImage image;
		if(_isSelected) {
			image = getSelectImage();
		}else {
			image = getBaseImage();
		}
		
		BufferedImage bi = new BufferedImage(image.getWidth(), image.getWidth(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0,0,image.getWidth(),image.getHeight(), null);
		if(_usine != null) {
			g.drawImage(_usine.getImage(), 0,0,image.getWidth(),image.getHeight(), null);
		}
		if(_occupant != null) {
			g.drawImage(_occupant.getImage(), 0,0,image.getWidth(),image.getHeight(), null);
		}
		return bi;
	}
	
	public BufferedImage getImageCtrl() {
		BufferedImage image = getCtrlImage();
		BufferedImage bi = new BufferedImage(image.getWidth(), image.getWidth(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0,0,image.getWidth(),image.getHeight(), null);
		if(_usine != null) {
			g.drawImage(_usine.getImageCtrl(), 0,0,image.getWidth(),image.getHeight(), null);
		}
		if(_occupant != null) {
			g.drawImage(_occupant.getImageCtrl(), 0,0,image.getWidth(),image.getHeight(), null);
		}
		return bi;
	}
	
	public Agent getOccupant() {
		return _occupant;
	}
	
	public void setOccupant(Agent a) {
		if(_occupant == null) {
			if(_bIsAccessible) {
				_occupant = a;
			}else {
				throw new IllegalArgumentException("Le terrain spécifié n'est pas accessible aux unités");
			}
		}else if(a != null){
			throw new IllegalArgumentException("La case n'est pas vide");
		}else {
			_occupant = a;
		}
	}
	
	public String printTerrain() {
		return " ";
	}

	public Factory getUsine() {
		return _usine;
	}

	public void setUsine(Factory f) {
		_usine = f;		
	}

	public boolean isAccessible() {
		return _bIsAccessible && _occupant == null;
	}
	
	public int getCout() {
		return _coutMvt;
	}
	
	public void setSelected(boolean b) {
		_isSelected = b;
	}
	
	public boolean isSelected() {
		return _isSelected;
	}
	
	public int getX() {
		return _x;
	}

	public int getY() {
		return _y;
	}
	
	public int getDef() {
		return _def;
	}
	
	@Override
	public boolean equals(Object o1) {
		if(o1 instanceof Terrain) {
			Terrain t = (Terrain) o1;
			return t._x == _x && t._y == _y;
		}else {
			return false;
		}
	}
	
	public boolean getAccessible() {
		return _bIsAccessible;
	}
	
	public static Terrain makeTerrain(int ID,int x, int y) {
		switch (ID) {
		case Grass.ID_TERRAIN :
			return new Grass(x,y);
		case Forest.ID_TERRAIN :
			return new Forest(x,y);
		case Mountain.ID_TERRAIN :
			return new Mountain(x,y);
		case Water.ID_TERRAIN :
			return new Water(x,y);
		default :
			return new Grass(x,y);
		}
	}


	public String precisionToString() {
		return "Pre : " + (int) (_coeffPre * 100) + " %";
	}


	public String defenseToString() {
		return "Def : " + (int) (_coeffDef * 100) + " %";
	}


		
}
