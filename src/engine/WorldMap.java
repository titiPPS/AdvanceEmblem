package engine;

import ia.PathFinder;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import ui.GUI;

public class WorldMap {
	static public final int TAILLE_CASE = 20;
	static public final int tailleImageCtrl = 100;
	static public final int UP = 1;
	static public final int DOWN = 2;
	static public final int LEFT = 3;
	static public final int RIGHT = 4;

	
	private int _width, _height;
	private Terrain[][] mapMonde = null;
	private BufferedImage imageCarte,imageCtrl;
	private Curseur _curseur;
	
	public WorldMap(int w, int h) {
		_width = w;
		_height = h;
		_curseur = new Curseur(w,h);
	}
	
	public WorldMap() {
		_curseur = new Curseur(0,0);
	}
	
	public void addTerrain(int x, int y, int ID_Terrain) {
		mapMonde[x][y] = Terrain.makeTerrain(ID_Terrain,x,y);
	}
	
	public void afficherMap() {
		for (int y = 0; y < _height ; y++) {
			for (int x = 0 ; x < _width ; x ++) {
				System.out.print(mapMonde[x][y].printTerrain());
			}
			System.out.println("");
		}
	}
	
	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public void setWidth(int w) {
		if(w < 0) {
			throw new IllegalArgumentException("W ne doit pas être < 0");
		}
		_width = w;
		_curseur.setxMax(w);
	}
	
	public void initMap() {
		mapMonde = new Terrain[_width][_height];
	}
	
	public void setHeight(int h) {
		if(h < 0) {
			throw new IllegalArgumentException("H ne doit pas être < 0");
		}
		_height = h;
		_curseur.setyMax(h);
	}

	public BufferedImage initImage() {
		 imageCarte = new BufferedImage(_width * TAILLE_CASE,_height * TAILLE_CASE, BufferedImage.TYPE_INT_ARGB);
		 imageCtrl = new BufferedImage(tailleImageCtrl, tailleImageCtrl, BufferedImage.TYPE_INT_ARGB);
		 for(int y = 0; y < _height ; y ++) {
			 for (int x = 0; x < _width ; x ++) {
				 Graphics2D g = imageCarte.createGraphics();
				 g.drawImage(mapMonde[x][y].getImage(), x * 20, y * 20, 20, 20, null);
			 }
		 }
		 return imageCarte;
	}
	
	/**
	 * Initialisation de la carte avec les données du joueur
	 * Dans le cas présent, cela consiste à placer son commandant
	 * et ses usines.
	 * @param p
	 */
	public void initJoueur(Player p) {
		KingAgent king = p.getCommandant();
		mapMonde[king.getX()][king.getY()].setOccupant(king);
		for(Factory f : p.getListOfFactory()) {
			if(mapMonde[f.getX()][f.getY()].getUsine() == null) {
				mapMonde[f.getX()][f.getY()].setUsine(f);
			}
		}
	}
	
	public void initVoisins() {
		/*Ajout des voisins en x*/
		for(int x = 1 ; x < _width; x++) {
			for(int y = 0 ; y < _height ; y++) {
				mapMonde[x][y].ajouterVoisin(mapMonde[x-1][y]);
				mapMonde[x-1][y].ajouterVoisin(mapMonde[x][y]);
			}
		}
		
		/*Ajout des voisins en y*/
		for(int x = 0 ; x < _width ; x ++) {
			for(int y = 1 ; y < _height ; y ++) {
				mapMonde[x][y].ajouterVoisin(mapMonde[x][y-1]);
				mapMonde[x][y-1].ajouterVoisin(mapMonde[x][y]);
			}
		}
				
	}
	
	/**
	 * Méthode permettant de mettre à jour l'image 100px
	 * correspondant à la position du curseur
	 */
	public void updateImageCtrl() {
		Terrain t = mapMonde[_curseur.getX()][_curseur.getY()];
		Graphics2D g = imageCtrl.createGraphics();
		g.drawImage(t.getImageCtrl(), 0, 0, tailleImageCtrl, tailleImageCtrl, null);
		
	}
	
	/**
	 * Méthode qui déplace le curseur et qui se charge de mettre à jour l'image de la carte.
	 * 
	 *@param vx la variation en x du curseur
	 * @param vy la variation en y du curseur
	 */
	public void deplacerCurseur(int vx, int vy) {
		int exPosX = _curseur.getX();
		int exPosY = _curseur.getY();
		_curseur.deplacer(vx,vy);
		if(mapMonde[exPosX][exPosY].isSelected() == mapMonde[_curseur.getX()][_curseur.getY()].isSelected()) {
			Terrain t = mapMonde[exPosX][exPosY];
			//Mise à jour de l'image : dessin des deux images de base
			Graphics2D g = imageCarte.createGraphics();
			g.drawImage(t.getImage(), exPosX * TAILLE_CASE, exPosY * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
			
			//Dessin du curseur
			if(_curseur.isVisible()) {
				g.drawImage(_curseur.getImage(false), _curseur.getX() * TAILLE_CASE, _curseur.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
			}
			
			//Dessin de l'image se trouvant sur le tableau de commande
			updateImageCtrl();
		}else {
			/*On annule le déplacement en effectuant le déplacement opposé*/
			_curseur.deplacer(-vx,-vy);
		}
	}
	

	public BufferedImage getImage() {
		return imageCarte;
	}

	public BufferedImage getImageCurseur() {
		return imageCtrl;
	}

	public Terrain getTerrainCurseur() {
		return mapMonde[_curseur.getX()][_curseur.getY()];
	}

	public void repaintLstTerrain(Collection<Terrain> c) {
		Graphics2D g = imageCarte.createGraphics();
		for(Terrain t : c) {
			g.drawImage(t.getImage(), t.getX() * TAILLE_CASE, t.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
		}
		if(_curseur.isVisible()) {
			g.drawImage(_curseur.getImage(false), _curseur.getX() * TAILLE_CASE, _curseur.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
		}
	}

	public boolean isCurseurAdjToEnnemi(Player jCourant) {
		boolean resultat = false;
		int deplacementX[] ={1,-1,0,0};
		int deplacementY[] ={0,0,-1,1};
		for(int i = 0; i < deplacementX.length && !resultat; i++) {
			_curseur.deplacer(deplacementX[i], deplacementY[i]);
			if(mapMonde[_curseur.getX()][_curseur.getY()].getOccupant() != null) {
				resultat = !mapMonde[_curseur.getX()][_curseur.getY()].getOccupant().appartientA(jCourant);
			}
			_curseur.deplacer(-deplacementX[i], -deplacementY[i]);
		}
		
		return resultat;
	}

	public void deplacerAgent(Terrain init, Terrain dest,GUI ui) {
		Agent agent = init.getOccupant();
		init.setOccupant(null);
		
		ArrayList<Terrain> etapes = PathFinder.aStar(init, dest, agent.getMouvement());
		if(ui != null) {
			Graphics2D g = imageCarte.createGraphics();
			for(int i = 0 ; i < etapes.size() - 1 ; i++) {
				Terrain t1 = etapes.get(i);
				Terrain t2 = etapes.get(i+1);
				int vx = t2.getX() - t1.getX();
				int vy = t2.getY() - t1.getY();
				int xRepaint = (vx >= 0 ? t1.getX() : t2.getX()) * TAILLE_CASE;
				int yRepaint = (vy >= 0 ? t1.getY() : t2.getY()) * TAILLE_CASE;
				int wRepaint = TAILLE_CASE + (vx == 0 ? 0 : TAILLE_CASE);
				int hRepaint =  TAILLE_CASE + (vy == 0 ? 0 : TAILLE_CASE);
				g.drawImage(t1.getImage(), t1.getX() * TAILLE_CASE, t1.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
				g.drawImage(t2.getImage(), t2.getX() * TAILLE_CASE, t2.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
				g.drawImage(agent.getImage(), t1.getX() * TAILLE_CASE + vx * TAILLE_CASE / 2,
						t1.getY() * TAILLE_CASE + vy * TAILLE_CASE / 2, TAILLE_CASE, TAILLE_CASE, null);
				ui.repaintMap(imageCarte,xRepaint,yRepaint,wRepaint,hRepaint);
				try {
					Thread.sleep(100);
				}catch(Exception e) {
					
				}
				g.drawImage(t1.getImage(), t1.getX() * TAILLE_CASE, t1.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
				g.drawImage(t2.getImage(), t2.getX() * TAILLE_CASE, t2.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
				g.drawImage(agent.getImage(), t2.getX() * TAILLE_CASE, t2.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
				ui.repaintMap(imageCarte,xRepaint,yRepaint,wRepaint,hRepaint);
				try {
					Thread.sleep(100);
				}catch(Exception e) {
					
				}
			}
			dest = etapes.get(etapes.size() - 1);
			dest.setOccupant(agent);
			agent.setX(dest.getX());
			agent.setY(dest.getY());	
		}
		
	}

	public Terrain getTerrain(int x, int y) {
		if (x > 0 && x < _width && y > 0 && y < _height) {
			return mapMonde[x][y];
		}else {
			return null;
		}
		
	}

	public void ajouterAgent(Agent a) {
		Terrain t =	mapMonde[a.getX()][a.getY()];
		t.setOccupant(a);
		Graphics2D g = imageCarte.createGraphics();
		g.drawImage(t.getImage(), t.getX() * TAILLE_CASE, t.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
		if(_curseur.isVisible()) {
			g.drawImage(_curseur.getImage(false), _curseur.getX() * TAILLE_CASE, _curseur.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
		}
	}

	public void setPosCurseur(Player p) {
		_curseur.set_visible(p.isCurseurVisible());
		Agent k = p.getCommandant();
		int vx = k.getX() - _curseur.getX();
		int vy = k.getY() - _curseur.getY();
		deplacerCurseur(vx, vy);
	}

	public void repaintUsine(Factory f) {
		Terrain t =	mapMonde[f.getX()][f.getY()];
		Graphics2D g = imageCarte.createGraphics();
		g.drawImage(t.getImage(), t.getX() * TAILLE_CASE, t.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
		if(_curseur.isVisible()) {
			g.drawImage(_curseur.getImage(false), _curseur.getX() * TAILLE_CASE, _curseur.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
		}
		
	}

	public void repaintJoueur(Player p) {
		Graphics2D g = imageCarte.createGraphics();
		Terrain t;
		for(Agent a : p.getListOfUnite()) {
			t =	mapMonde[a.getX()][a.getY()];
			g.drawImage(t.getImage(), t.getX() * TAILLE_CASE, t.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
		}
		for(Factory f : p.getListOfFactory()) {
			t =	mapMonde[f.getX()][f.getY()];
			g.drawImage(t.getImage(), t.getX() * TAILLE_CASE, t.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
		}
		t = mapMonde[p.getCommandant().getX()][p.getCommandant().getY()];
		g.drawImage(t.getImage(), t.getX() * TAILLE_CASE, t.getY() * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE, null);
	}

	public HashMap<Integer,Terrain> choixAtq(Terrain t,Agent a) {
		HashMap<Integer,Terrain> resultat = new HashMap<Integer,Terrain>();
		Terrain temp;
		if(t.getX() > 0) {
			temp = mapMonde[t.getX() - 1][t.getY()];
			if(temp.getOccupant() != null && !temp.getOccupant().appartientA(a.getJoueur())) {
				resultat.put(LEFT,temp);
			}
		}
		if(a.getX() < _width) {
			temp = mapMonde[t.getX() + 1][t.getY()];
			if(temp.getOccupant() != null && !temp.getOccupant().appartientA(a.getJoueur())) {
				resultat.put(RIGHT,temp);
			}
		}
		if(a.getY() > 0) {
			temp = mapMonde[t.getX()][t.getY() - 1];
			if(temp.getOccupant() != null && !temp.getOccupant().appartientA(a.getJoueur())) {
				resultat.put(UP,temp);
			}
		}
		if(a.getY() < _height) {
			temp = mapMonde[t.getX()][t.getY() + 1];
			if(temp.getOccupant() != null && !temp.getOccupant().appartientA(a.getJoueur())) {
				resultat.put(DOWN,temp);
			}
		}
		return resultat;
	}

	
	
}
