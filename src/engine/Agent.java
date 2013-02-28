package engine;

import game.GameEngine;

import java.awt.image.BufferedImage;

public abstract class Agent{
	private static int NB_UNITES = 0;
	
	protected int _x,_y,_speed,_pv,_pvMax,_atq,_def;
	protected boolean _used;
	protected Player _joueur;
	protected int ID_Unite;
	
	public Agent(int x,int y,int speed,Player joueur,int pvMax,int atq, int def) {
		_x = x;
		_y = y;
		_speed = speed;
		_pvMax = pvMax;
		_pv = pvMax;
		_joueur = joueur;
		_atq = atq;
		_def = def;
		_used = false;
		ID_Unite = NB_UNITES;
		NB_UNITES ++;
	}
	
	/**
	 * Methode retournant l'objet BufferedImage correspondant
	 * à l'état courant de l'objet (joueur,état)
	 * @return BufferedImage
	 */
	public abstract BufferedImage getImage();
	
	
	/**
	 * Methode retournant l'objet BufferedImage correspondant
	 * à l'état courant de l'objet (joueur,état). L'image est
	 * destinée a être affichée sur le paneau de contrôle
	 * @return BufferedImage
	 */
	public abstract BufferedImage getImageCtrl();
	
	public abstract String getName();
	
	public int calculDegats(Agent a, Terrain t) {
		int atq = this._atq;
		int def = a.getDef() + t.getDef();
		if(estFortContre(a)) {
			atq ++;
			def --;
		}else if(estFaibleContre(a)) {
			atq --;
			def ++;
		}
		def = (def < 0 ? 0 : def);
		return (atq < def ? 0 : atq - def); 
	}
	
	public int getX() {
		return _x;
	}

	public void setX(int x) {
		this._x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		this._y = y;
	}

	public boolean isUsed() {
		return _used;
	}

	public void setUsed(boolean used) {
		this._used = used;
	}
	
	public int getPV() {
		return this._pv;
	}
	
	public int getPVMax() {
		return _pvMax;
	}
	
	public int getMouvement() {
		return _speed;
	}

	public int getDef() {
		return _def;
	}
	
	public int getAtq() {
		return _atq;
	}
	
	public Player getJoueur() {
		return _joueur;
	}
	
	public boolean isDead() {
		return  (_pv <= 0 ? true : false);
	}
	
	public boolean appartientA(Player joueurCourant) {
		return _joueur.equals(joueurCourant);
	}
	
	public boolean estFaibleContre(Agent a) {
		int[] faiblesses = getFaiblesses();
		if (faiblesses != null) {
			for(int i : faiblesses) {
				if(i == a.getIDClass()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean estFortContre(Agent a) {
		int[] forces = getForces();
		if (forces != null) {
			for(int i : forces) {
				if(i == a.getIDClass()) {
					return true;
				}
			}
		}
		return false;
	}

	public int getPrix() {
		return GameEngine.COUT_BASE;
	}
	public void setPV(int newPV) {
		_pv = (newPV > _pvMax ? _pvMax : newPV);
	}
	
	public boolean equals(Object o) {
		if(o != null && (o instanceof Agent)) {
			Agent a = (Agent) o;
			return ID_Unite == a.ID_Unite;
		}
		return false;
	}
	
	public static int valueOf(Agent a) {
		return (a.getAtq() + a.getDef()) * a.getPV() / a.getPrix();
	}
	
	
	public abstract int getIDClass();
	public abstract int[] getFaiblesses();
	public abstract int[] getForces();
}
