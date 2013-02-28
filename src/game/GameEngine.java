package game;

import ia.AlgoHumain;
import ia.AlgoIA;
import ia.PathFinder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import sun.management.resources.agent;
import ui.DialogATQ;
import ui.GUI;
import ui.GUI.*;

import engine.Agent;
import engine.AxeAgent;
import engine.Curseur;
import engine.Factory;
import engine.Forest;
import engine.Fort;
import engine.Grass;
import engine.HorseAgent;
import engine.KingAgent;
import engine.Mountain;
import engine.Player;
import engine.SpearAgent;
import engine.SwordAgent;
import engine.Terrain;
import engine.Water;
import engine.WorldMap;

public class GameEngine  {
	private static final String TOKEN_WIDTH = "w";
	private static final String TOKEN_HEIGHT = "h";
	private static final String TOKEN_STARTMAP = "startMap";
	private static final String TOKEN_ENDMAP = "endMap";
	private static final String TOKEN_PARAM = "=";
	private static final String TOKEN_SEPAR = ":";
	private static final String TOKEN_KING = "posKing";
	private static final String TOKEN_FACTORY = "fac";
	
	public final static int COUT_AUGMENTATION_RESSOURCES = 30;
	public final static int COUT_BASE = 50;
	public final static int COUT_SUPER = 200;
	public static final int AUGMENTATION_CROISSANCE = 5;
	public static final int RESSOURCES_INIT = 500;
	public static final int CROISSANCE_INIT = 10;
	
	private Player pRed;
	private Player pBlue,joueurCourant;
	protected WorldMap carte;
	private GUI _usrInterface;
	
	/*Evènements*/
	private boolean eventEnCours = false;
	private ArrayList<Terrain> terrainsAccessibles;
	private HashMap<Integer,Terrain> terrainsATQ;
	private Agent agentCourant;
	private Factory usineCourante;
	private Curseur curseurAtq = null;
	
	public GameEngine(GUI usrInterface) {
		_usrInterface = usrInterface;
		terrainsAccessibles = new ArrayList<Terrain>();
	}
	
	/**
	 * Fonction membre se chargeant de charger une carte stockée dans un fichier texte.
	 * @param file : le fichier desciptif d'une carte
	 */
	public void loadFile(File file) {
		carte = new WorldMap();
		pRed = new Player(true);
		pRed.setAlgorithme( new AlgoHumain(pRed));
		pRed.setRessources(RESSOURCES_INIT);
		pRed.setCroissance(CROISSANCE_INIT);
		joueurCourant = pRed;
		pBlue = new Player(false);
		//pBlue.setAlgorithme(new AlgoHumain(pBlue));
		pBlue.setAlgorithme( new AlgoIA(pBlue,pRed,this));
		pBlue.setRessources(RESSOURCES_INIT);
		pBlue.setCroissance(CROISSANCE_INIT);
		
		try {
			Scanner sc = new Scanner(file);
			boolean lectureMap = false;
			int x, y = 0;
			
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				/*Si la ligne n'est pas commentée*/
				if(!line.startsWith("//")) {
					if(line.startsWith(TOKEN_WIDTH)) {
						carte.setWidth(Integer.parseInt(line.substring(line.indexOf(TOKEN_PARAM) + 1)));
					}else if (line.startsWith(TOKEN_HEIGHT)) {
						carte.setHeight(Integer.parseInt(line.substring(line.indexOf(TOKEN_PARAM) + 1)));
					}else if (line.startsWith(TOKEN_KING)) {
						String str = line.substring(line.indexOf(TOKEN_PARAM) + 1, line.indexOf(TOKEN_SEPAR));
						int xKing = Integer.parseInt(str);
						str = line.substring(line.indexOf(TOKEN_SEPAR) + 1);
						int yKing = Integer.parseInt(str);
						if(line.contains(TOKEN_KING + "Blue")) {
							pBlue.setCommandantPosition(xKing, yKing);							
						}else {
							pRed.setCommandantPosition(xKing, yKing);
						}
					}else if(line.startsWith(TOKEN_FACTORY)){
						String str = line.substring(line.indexOf(TOKEN_PARAM) + 1, line.indexOf(TOKEN_SEPAR));
						int xFac = Integer.parseInt(str);
						str = line.substring(line.indexOf(TOKEN_SEPAR) + 1);
						int yFac = Integer.parseInt(str);
						if(line.contains(TOKEN_FACTORY + "Blue")) {
							pBlue.addFactory(xFac,yFac);							
						}else {
							pRed.addFactory(xFac,yFac);
						}
					}else if(line.startsWith(TOKEN_STARTMAP)) {
						lectureMap = true;
						carte.initMap();
					}else if(line.startsWith(TOKEN_ENDMAP)) {
						lectureMap = false;
					}else if(lectureMap) {
						x = 0;
						while (!line.isEmpty()) {
							int ID_Terrain;
							
							if(line.contains(TOKEN_SEPAR)) {
								ID_Terrain = Integer.parseInt(line.substring(0,line.indexOf(TOKEN_SEPAR)));
								line = line.substring(line.indexOf(TOKEN_SEPAR) + 1);
							}else {
								ID_Terrain = Integer.parseInt(line);
								line = "";
							}
							carte.addTerrain(x,y,ID_Terrain);
							x ++;
						}
						y ++;
					}
					
				}
			}
			carte.initJoueur(pRed);
			carte.initJoueur(pBlue);
			carte.initVoisins();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		carte.initImage();
		Thread t = new Thread(new Partie2J(this, pRed, pBlue,_usrInterface));
		t.start();
	}
	
	
	
	/**
	 * Methodes qui appelle l'initialisation des images de chaque classe
	 * ayant un rôle graphique.
	 * @param dir : le chemin du dossier contenant l'ensemble des images nécessaires
	 */
	public void initImage(String dir) {
		/*Curseur*/
		Curseur.initImage(dir);
		/*Terrains*/
		Grass.initImage(dir);
		Mountain.initImage(dir);
		Forest.initImage(dir);
		Water.initImage(dir);
		Fort.initImage(dir);
		
		Factory.initImage(dir);
		
		/*Agents*/
		KingAgent.initImage(dir);
		AxeAgent.initImage(dir);
		SwordAgent.initImage(dir);
		SpearAgent.initImage(dir);
		HorseAgent.initImage(dir);
		
		/*Joueurs*/
		Player.initImage(dir);
	}
	
	public BufferedImage initMap(File f) {
		loadFile(f);
		return carte.initImage();
	}

	/**
	 * Le gameEngine traite l'évènement mais la gestion se fait sous le contrôle du joueur et de son algorithme.
	 * (Si le joueur est une IA, l'évènement est sans conséquence).
	 * @param ac l'évènement clavier capturé (sous la forme d'enum ActionClavier)
	 */
	public void traiterEvent(ActionClavier ac) {
		if(joueurCourant != null) {
			if(joueurCourant.traiteEvent()) {
				switch (ac) {
				case up :
					 deplacerCurseur(0,-1);
					break;
				case down :
					deplacerCurseur(0,1);
					break;
				case left :
					deplacerCurseur(-1,0);
					break;
				case right :
					deplacerCurseur(1,0);
					break;
				case ok :
					Terrain t = getTerrainCurseur();
					if(!eventEnCours) {
						agentCourant = t.getOccupant();
						if(agentCourant != null && !agentCourant.isUsed() && agentCourant.appartientA(joueurCourant)) {
							eventEnCours = true;
							terrainsAccessibles = PathFinder.listeDestination(t, agentCourant.getMouvement());
							for(Terrain temp : terrainsAccessibles) {
								temp.setSelected(true);
							}
							carte.repaintLstTerrain(terrainsAccessibles);
						}else if(agentCourant == null) {
							usineCourante = t.getUsine();
							if(usineCourante != null && !usineCourante.isUsed() && usineCourante.appartientA(joueurCourant)) {
								_usrInterface.showMenuUsine(joueurCourant);
							}
						}
					}else {
						//terrainsATQ = carte.choixAtq(getTerrainCurseur(), agentCourant);
						//String txtUp = null;
						//if(terrainsATQ.containsKey(WorldMap.UP)) {
						_usrInterface.showMenuAgent(carte.choixAtq(carte.getTerrainCurseur(),agentCourant).size() > 0);
					}
					_usrInterface.repaintMap(carte.getImage());
					break;
				case cancel :
					eventEnCours = false;
					for(Terrain temp : terrainsAccessibles) {
						temp.setSelected(false);
					}
					carte.repaintLstTerrain(terrainsAccessibles);
					_usrInterface.repaintMap(carte.getImage());
					break;
				default :
					break;
			}
				
			}
		}
	}

	/**
	 * Transmet le déplacement du curseur à la WorldMap
	 */
	public void deplacerCurseur(int vx, int vy) {
		carte.deplacerCurseur(vx,vy);
		_usrInterface.repaintMap(carte.getImage());
		_usrInterface.repaintPaneauCtrl(carte.getTerrainCurseur());
	}
	
	public Terrain getTerrainCurseur() {
		return carte.getTerrainCurseur();
	}
	
	public BufferedImage getImage() {
		return carte.getImage();
	}

	public void resultatMenu(EventAgent ea) {
		if(ea == EventAgent.attaquer) {
			Terrain former = carte.getTerrain(agentCourant.getX(), agentCourant.getY());
			deplacerAgent(former, carte.getTerrainCurseur(),false);
			Terrain newPosition = carte.getTerrain(agentCourant.getX(), agentCourant.getY());
			int x = agentCourant.getX();
			int y = agentCourant.getY();
			DialogATQ dialog = new DialogATQ(carte.getTerrain(x, y), carte.getTerrain(x, y - 1), carte.getTerrain(x, y + 1)
					, carte.getTerrain(x - 1, y), carte.getTerrain(x + 1, y));
			
			dialog.showDialog();
			if(dialog.getResultat() != null) {
				attaquer(newPosition, dialog.getResultat());
				if(!agentCourant.isDead()) {
					agentCourant.setUsed(true);
				}
				for(Terrain temp : terrainsAccessibles) {
					temp.setSelected(false);
				}
				carte.repaintLstTerrain(terrainsAccessibles);
				
				agentCourant = null;
				eventEnCours = false;
				_usrInterface.repaintMap(carte.getImage());
				_usrInterface.repaintPaneauCtrl(newPosition);
			}else {
				if(former.getX() != agentCourant.getX() && former.getY() != agentCourant.getY()) {
					agentCourant.setX(former.getX());
					agentCourant.setY(former.getY());
					former.setOccupant(agentCourant);
					newPosition.setOccupant(null);
					carte.repaintTerrain(former);
					carte.repaintTerrain(newPosition);
				}
			}
			
		}else {
			deplacerAgentCourant(carte.getTerrainCurseur());
		}
		
		
	}
	
	public void deplacerAgentCourant(Terrain dest) {
		Terrain init = carte.getTerrain(agentCourant.getX(),agentCourant.getY());
		carte.deplacerAgent(init,dest,_usrInterface);
		agentCourant.setUsed(true);
		for(Terrain temp : terrainsAccessibles) {
			temp.setSelected(false);
		}
		carte.repaintLstTerrain(terrainsAccessibles);
		
		agentCourant = null;
		eventEnCours = false;
		_usrInterface.repaintMap(carte.getImage());
		_usrInterface.repaintPaneauCtrl(dest);
	}
	
	/**
	 * Méthode permettant de traiter un évènement du menu usine
	 * (c'est à dire la création d'une unité ou l'augmentation
	 * de la croissance de ressources)
	 * 
	 * @param eu l'évènement usine qui s'est produit
	 */
	public void resultatMenu(EventUsine eu) {
		construireAgent(joueurCourant, usineCourante, eu);
	}
	
	public void construireAgent(Player p, Factory u, EventUsine eu) {
		Agent a;
		switch(eu) {
			case horse:
				a = p.creerAgent(u, new HorseAgent(0,0,p),GameEngine.COUT_SUPER);
				carte.ajouterAgent(a);
				break;
			case sword:
				a = p.creerAgent(u, new SwordAgent(0,0,p),GameEngine.COUT_BASE);
				carte.ajouterAgent(a);
				break;
			case spear:
				a = p.creerAgent(u, new SpearAgent(0,0,p),GameEngine.COUT_BASE);
				carte.ajouterAgent(a);
				break;
			case axe:
				a = p.creerAgent(u, new AxeAgent(0,0,p),GameEngine.COUT_BASE);
				carte.ajouterAgent(a);
				break;
			case gold:
				p.augmenterRessources(u);
				break;
			default:
				break;
		}
		carte.repaintUsine(u);
		/**
		 * Déclanchement des différents repaint
		 */
		_usrInterface.repaintMap(carte.getImage(), u.getX() * WorldMap.TAILLE_CASE,
			u.getY() * WorldMap.TAILLE_CASE, WorldMap.TAILLE_CASE, WorldMap.TAILLE_CASE);
			_usrInterface.updateInfo(p);
			carte.updateImageCtrl();
			_usrInterface.repaintPaneauCtrl(carte.getTerrainCurseur());
	}

	/**
	 * Methode permettant de mettre en oeuvre la fin du tour d'un joueur
	 * 	- fin de l'évènement en cours
	 * 	- rafraichissement de l'écran
	 * 	- appel au fin de tour du joueur
	 */
	public void finDeTour() {
		if(joueurCourant.traiteEvent()) {
			eventEnCours = false;
			for(Terrain temp : terrainsAccessibles) {
				temp.setSelected(false);
			}
			carte.repaintLstTerrain(terrainsAccessibles);
			_usrInterface.repaintMap(carte.getImage());
			joueurCourant.finDeTour();
		}
		
	}
	
	/**
	 * Methode permettant aux unités placées sur les deux terrains donnés
	 * en paramètre de s'infliger mutuellement des dégats.
	 * 
	 * @param tATQ : le terrain de l'attaquant
	 * @param tDEF : le terrain de l'attaqué
	 */
	public void attaquer(Terrain tATQ, Terrain tDEF) {
		if(tATQ != null && tATQ.getOccupant() != null) {
			if(tDEF != null && tDEF.getOccupant() != null) {
				Agent aATQ = tATQ.getOccupant();
				Agent aDEF = tDEF.getOccupant();
				
				aDEF.setPV(aDEF.getPV() - aATQ.calculDegats(aDEF, tDEF));
				if(aDEF.isDead()) {
					aDEF.getJoueur().removeUnite(aDEF);
					tDEF.setOccupant(null);
					if(aDEF.getJoueur().getCommandant().equals(aDEF)) {
						aDEF.getJoueur().finDeTour();
					}
				}else {
					aATQ.setPV(aATQ.getPV() - aDEF.calculDegats(aATQ, tATQ));
					if(aATQ.isDead()) {
						aATQ.getJoueur().removeUnite(aATQ);
						tATQ.setOccupant(null);
						if(aATQ.getJoueur().getCommandant().equals(aATQ)) {
							aATQ.getJoueur().finDeTour();
						}
					}
				}
			}
			ArrayList<Terrain> al = new ArrayList<Terrain>();
			al.add(tATQ);
			al.add(tDEF);
			carte.repaintLstTerrain(al);
			_usrInterface.repaintPaneauCtrl(tATQ);
		}
	}

	/**
	 * Methode permettant de déplacer un agent du terrain
	 * init vers le terrain dest.
	 * Si le mouvement est permanent (cad ne sera pas annulé, alors
	 * l'unité est considérée comme utilisée pour le tour en cours)
	 * 
	 * @param init
	 * @param dest
	 * @param mvtPermanent : vrai si le déplacement ne sera en aucun cas annulé
	 */
	public void deplacerAgent(Terrain init,Terrain dest,boolean mvtPermanent) {
		if(init != null && init.getOccupant() != null) {
			carte.deplacerAgent(init,dest,_usrInterface);
			dest.getOccupant().setUsed(mvtPermanent);
			carte.repaintTerrain(dest);
			_usrInterface.repaintMap(carte.getImage(),dest.getX(),dest.getY(),WorldMap.TAILLE_CASE, WorldMap.TAILLE_CASE);
		}
	}
	
	public void initJoueurCourant(Player p) {		
		joueurCourant = p;
		for(int x = 0 ; x < carte.getWidth() ; x++) {
			for(int y = 0; y < carte.getHeight() ; y++) {
				carte.getTerrain(x, y).effetSurOccupantStart(joueurCourant);
			}
		}
		carte.setPosCurseur(p);
		_usrInterface.repaintMap(carte.getImage());
		_usrInterface.repaintPaneauCtrl(carte.getTerrainCurseur());
		_usrInterface.initJoueur(p);
	}

	public void repaintJoueurMap(Player p) {
		carte.repaintJoueur(p);
		_usrInterface.repaintMap(carte.getImage());
	}

	public Terrain getTerrain(int x, int y) {
		return carte.getTerrain(x, y);
	}
	
	public void finDePartie(Player winner) {
		_usrInterface.repaintMap(winner.getImageVictoire());
	}
}
