package engine;

import game.GameEngine;

import ia.AlgoHumain;
import ia.IAlgorithme;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Player {
	public static final String fileBaniereRouge = "BaniereRouge.png";
	public static final String fileBaniereBleue = "BaniereBleu.png";
	public static final String fileVictoireRouge = "VictoryRed.png";
	public static final String fileVictoireBleue = "VictoryBlue.png";
	
	private static BufferedImage baniereBleue;
	private static BufferedImage baniereRouge;
	private static BufferedImage vicBleue;
	private static BufferedImage vicRouge;
	
	private KingAgent commandant;
	private ArrayList<Agent> lstUnite;
	private ArrayList<Factory> lstFactory;
	private int _ressources,_croissance;
	private boolean _isRed;
	
	protected IAlgorithme _algorithme;
	
	public Player(boolean isRed) {
		lstUnite = new ArrayList<Agent>();
		lstFactory = new ArrayList<Factory>();
		_isRed = isRed;
	}
	
	public Player(boolean isRed,IAlgorithme algo) {
		this(isRed);
		_algorithme = algo;
	}
	
	public Player(boolean isRed, IAlgorithme algo, int ressourcesInit,int croissance) {
		this(isRed,algo);
		_ressources = ressourcesInit;
		_croissance = croissance;
	}
	
	public void setCommandantPosition(int x, int y) {
		commandant = new KingAgent(x,y,this);
	}
	
	public boolean isRed() {
		return _isRed;
	}
	
	public KingAgent getCommandant() {
		return commandant;
	}
	
	public ArrayList<Factory> getListOfFactory() {
		return lstFactory;
	}
	
	public ArrayList<Agent> getListOfUnite() {
		return lstUnite;
	}
	
	public ArrayList<Agent> getListOfUniteLarge() {
		ArrayList<Agent> al = new ArrayList<Agent>();
		al.add(commandant);
		al.addAll(lstUnite);
		return al;
	}

	public void addFactory(int xFac, int yFac) {
		Factory f = new Factory(xFac,yFac,_isRed);
		if(!lstFactory.contains(f)) {
			lstFactory.add(f);
		}else {
			throw new IllegalArgumentException("La factory existe déjà");
		}
	}

	public boolean aPerdu() {
		return (commandant.getPV() <= 0);
	}

	/**
	 * 
	 * @param ge
	 */
	public void jouer(GameEngine ge) {
		_algorithme.jouer(ge);
	}

	public boolean traiteEvent() {
		return _algorithme.traiteEvent();
	}

	public boolean peutConstruireBaseAgent() {
		return _ressources >= GameEngine.COUT_BASE;
	}
	
	public boolean peutAugmenterRessources() {
		return _ressources >= GameEngine.COUT_AUGMENTATION_RESSOURCES;
	}
	
	public boolean peutConstruireSuperAgent() {
		return _ressources >= GameEngine.COUT_SUPER;
	}
	
	public void initTour() {
		_ressources += _croissance;
		commandant.setUsed(false);
		for(Agent a : lstUnite) {
			a.setUsed(false);
		}
		for(Factory f : lstFactory) {
			f.setUsed(false);
		}
	}

	
	public Agent creerAgent(Factory f, Agent a, int cout) {
		if(f.appartientA(this)) {
			f.setUsed(true);
			a.setX(f.getX());
			a.setY(f.getY());
			a.setUsed(true);
			lstUnite.add(a);
			_ressources -= cout;
			if(_ressources >= 0) {
				return a;
			}else {
				throw new IllegalArgumentException("Pas assez de ressources");
			}
		}else {
			throw new IllegalArgumentException("L'usine n'appartient pas au joueur");
		}
	}

	public void augmenterRessources(Factory usineCourante) {
		if(usineCourante.appartientA(this)) {
			_croissance += GameEngine.AUGMENTATION_CROISSANCE;
			_ressources -= GameEngine.COUT_AUGMENTATION_RESSOURCES;
			usineCourante.setUsed(true);
			if(_ressources < 0) {
				throw new IllegalArgumentException("Pas assez de ressources");
			}
		}else {
			throw new IllegalArgumentException("L'usine n'appartient pas au joueur");
		}
	}
	
	public BufferedImage getImageBaniere() {
		if(_isRed) {
			return baniereRouge;
		}else {
			return baniereBleue;
		}
	}
	
	public BufferedImage getImageVictoire() {
		if(_isRed) {
			return vicRouge;
		}else {
			return vicBleue;
		}
	}
	
	public Color getColor() {
		if(_isRed) {
			return new Color(128,0,0);
		}else {
			return new Color(0,0,128);
		}
	}
	
	public int getRessources() {
		return _ressources;
	}

	public int getCroissance() {
		return _croissance;
	}
	
	public String getName() {
		if(_isRed) {
			return "Joueur rouge";
		}else {
			return "Joueur bleu";
		}
	}

	public void setRessources(int ressourcesInit) {
		_ressources = ressourcesInit;
		
	}

	public void setCroissance(int croissanceInit) {
		_croissance = croissanceInit;
		
	}
	
	public void setAlgorithme(IAlgorithme algo) {
		_algorithme = algo;	
	}
	
	public static void initImage(String directory) {
		try {
			baniereRouge = ImageIO.read(new File(directory + File.separator + fileBaniereRouge));
			baniereBleue = ImageIO.read(new File(directory + File.separator + fileBaniereBleue));
			vicBleue = ImageIO.read(new File(directory + File.separator + fileVictoireBleue));
			vicRouge = ImageIO.read(new File(directory + File.separator + fileVictoireRouge));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void finDeTour() {
		_algorithme.setFinDeTour(true);		
	}

	public boolean isCurseurVisible() {
		return _algorithme.traiteEvent();
	}

	public void removeUnite(Agent agent) {
		lstUnite.remove(agent);
		_algorithme.remove(agent);
	}

	public float calcPowMilitaire() {
		float resultat = 0f;
		for(Agent a : lstUnite) {
			resultat += Agent.valueOf(a);
		}
		resultat += (commandant.getAtq() + commandant.getDef()) * commandant.getPV();
		return resultat;
	}




	

	
}
