package ia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;

import ui.GUI.EventUsine;

import engine.Agent;
import engine.AxeAgent;
import engine.Factory;
import engine.HorseAgent;
import engine.Player;
import engine.SpearAgent;
import engine.SwordAgent;
import engine.Terrain;
import game.GameEngine;

public class AlgoIA implements IAlgorithme{
	private static float COEFF_RSRC = 0.2f;
	private static float COEFF_MILITARY = 0.5f;
	private static float COEFF_CROISSANCE = 0.3f;
	private static int PEREQ_DELTA = 4;
	private static int NB_GENERATION_RDM = 5;
	
	private Player _joueur,_ennemi;
	
	private float probaSuper = 0.3f, probaBase = 0.35f,probaRsrc = 0.3f,probaNothing = 0.005f;
	private Random randGenerator;
	
	private HashMap<Agent,Comportement> mapComportement;
	private Comportement cmdtComportement,bestComportement = null;
	
	public AlgoIA(Player joueur,Player ennemi,GameEngine ge) {
		randGenerator = new Random();
		this._joueur = joueur;
		this._ennemi = ennemi;
		mapComportement = new HashMap<Agent,Comportement>();
		cmdtComportement = new Comportement(ge, joueur,_ennemi, Float.MAX_VALUE, 10,10,10);
		cmdtComportement.setDistribution(0.01f, 0.99f);
		cmdtComportement.setCoeffLongTerme(0f);
		cmdtComportement.setCoeffATQ(1.0f, 0.1f, 0.9f, 5f);
		bestComportement = Comportement.comportementAleatoire(ge, joueur,_ennemi);
		bestComportement.setScore(0);
	}

	@Override
	public void jouer(GameEngine gEngine) {
		//TODO Gestion des unites
		for(Agent a : _joueur.getListOfUnite()) {
			gererAgent(a,gEngine);
		}
		
		//TODO Gestion du commandant
		Agent king = _joueur.getCommandant();
		gererAgent(king,gEngine,cmdtComportement);
		
		
		
		//TODO Gestion de la production
		//Nouvelle estimation des menaces pesant sur le commandant
		ArrayList<Terrain>positionsMenaces = new ArrayList<Terrain>();
		for(Agent a : _ennemi.getListOfUniteLarge()) {
			if(a.getX() <= king.getX() + a.getMouvement() && a.getX() >= king.getX() + a.getMouvement()) {
				if(a.getY() <= king.getY() + a.getMouvement() && a.getY() >= king.getY() + a.getMouvement()) {
					positionsMenaces.add(gEngine.getTerrain(a.getX(),a.getY()));
				}
			}
		}
		bestComportement = findBestComportement();
		
		for(Factory usine : _joueur.getListOfFactory()) {
			Terrain t = gEngine.getTerrain(usine.getX(), usine.getY());
			if(t.getOccupant() == null) {
				gEngine.construireAgent(_joueur, usine, determinerAction(positionsMenaces));
			}
			if(t.getOccupant() != null) {
				if(randGenerator.nextDouble() < ((double)NB_GENERATION_RDM / (mapComportement.size() + 1))) {
					mapComportement.put(t.getOccupant(),Comportement.comportementAleatoire(gEngine, _joueur,_ennemi));
				}else {
					mapComportement.put(t.getOccupant(),bestComportement.clone());
				}
			}
		}
	}
	
	/**
	 * Methode permettant de déterminer le meilleur comportement parmi ceux
	 * des unités encore vivantes et le meilleur comportement déterminé précédemment
	 * 
	 * @return le meilleur comportement
	 */
	private Comportement findBestComportement() {
		Comportement best = null;
		float max = - Float.MAX_VALUE;
		for(Comportement c : mapComportement.values()) {
			if(c.getScore() > max) {
				best = c;
				max = c.getScore();
			}
		}
		if(bestComportement.getScore() < max)
			return best;
		else
			return bestComportement;
	}
	
	/**
	 * Methode permettant de réaliser la décision d'un agent, selon son
	 * comportement associé.
	 * 
	 * @param a : l'agent concerné
	 * @param gEngine : le moteur du jeu
	 * @param c : le comportement de l'agent concerné
	 */
	private void gererAgent(Agent a,GameEngine gEngine,Comportement c) {
		float currentValue = _joueur.calcPowMilitaire() - _ennemi.calcPowMilitaire();
		ArrayList<Terrain> lstDest = PathFinder.listeDestination(gEngine.getTerrain(a.getX(), a.getY()), a.getMouvement());
		Terrain dest = c.choisirDestination(a, lstDest, _ennemi);
		if(dest != null) {
			gEngine.deplacerAgent(gEngine.getTerrain(a.getX(), a.getY()), dest, true);
		}
		lstDest = new ArrayList<Terrain>();
		lstDest.addAll( gEngine.getTerrain(a.getX(), a.getY()).getListeVoisins());
		for(int i = lstDest.size() - 1 ; i >=0 ; i --) {
			Terrain t = lstDest.get(i);
			if(t.getOccupant() == null || t.getOccupant().appartientA(_joueur)) {
				lstDest.remove(i);
			}
		}
		dest = c.choixATQ(lstDest, gEngine.getTerrain(a.getX(), a.getY()), a);
		if(dest != null) {
			gEngine.attaquer(gEngine.getTerrain(a.getX(), a.getY()), dest);
		}
		currentValue -= _joueur.calcPowMilitaire() - _ennemi.calcPowMilitaire();
		if(!a.isDead()) {
			c.setScore(c.getScore() + 0.1f + currentValue );
		}
	}
	
	/**
	 * Methode permettant de réaliser la décision d'un agent, selon son
	 * comportement associé. Ce comportement sera celui stocké dans la map
	 * des comportements ou le meilleur comportement actuel si aucun comportement n'est trouvé.
	 * 
	 * La gestion sera faite ensuite par la méthode éponyme.
	 * 
	 * @param a : l'agent concerné
	 * @param gEngine : le moteur du jeu
	 */
	private void gererAgent(Agent a,GameEngine gEngine) {
		Comportement c = mapComportement.get(a);
		c = (c == null ? bestComportement.clone() : c);
		gererAgent(a, gEngine, c);
	}
	
	/**
	 * Methode permettant de déterminer l'action d'une usine pour le tour courant.
	 * 
	 * @param positionsMenaces
	 * @return l'évenement usine associé à la décision
	 */
	public EventUsine determinerAction(ArrayList<Terrain> positionsMenaces) {
		float pNothing = probaNothing, pRsrc = probaRsrc, pBase = probaBase, pSuper = probaSuper;
		float currentPowJoueur = _joueur.calcPowMilitaire(), currentPowEnnemi = _ennemi.calcPowMilitaire();
		int currentCroissance = _joueur.getCroissance(), currentRsrc = _joueur.getRessources() ;
		float currentValue = valueOfState(currentPowJoueur, currentPowEnnemi, currentCroissance, 
				currentRsrc, _joueur.getListOfFactory().size());
		float delta = 0.0f;
		EventUsine resultat = EventUsine.nothing;
		double rdm = Math.random();
		if(currentPowJoueur - currentPowEnnemi <= 0) {
			pNothing *= 0.5f;
			pBase *= 0.5f;
			pBase += pNothing + pBase;
		}
		/*On met à jour la variable de décision aléatoire en fonction des ressources du joueur*/
		if(_joueur.getRessources() < GameEngine.COUT_SUPER) {
			if(_joueur.getRessources() < GameEngine.COUT_BASE) {
				if(_joueur.getRessources() < GameEngine.COUT_AUGMENTATION_RESSOURCES) {
					rdm = 0;
				}else {
					rdm *= (pNothing + pRsrc);
				}
			}else {
				rdm *= (pNothing + pRsrc + pBase);
			}
		}else if(positionsMenaces.size() > 0){
			double random = Math.random();
			if(random > Math.pow(2, -positionsMenaces.size())) {
				rdm += pNothing + pRsrc;
				rdm = Math.min(1.0, rdm);
			}
		}
		
		if(rdm < (pNothing + pRsrc)) {
			//On privilégie les ressources
			if(rdm > pNothing) {
				//On augmente la croissance
				resultat = EventUsine.gold;
				currentRsrc -= GameEngine.COUT_AUGMENTATION_RESSOURCES;
				currentCroissance += GameEngine.AUGMENTATION_CROISSANCE;
				delta = (valueOfState(currentPowJoueur, currentPowEnnemi, currentCroissance,
						currentRsrc , _joueur.getListOfFactory().size()) - currentValue) / currentValue;
				pRsrc += delta / PEREQ_DELTA;
			}
		}else {
			//Construction d'unites militaires
			if(rdm < pNothing + pRsrc + pBase) {
				//Base Agent
				Agent a;
				resultat = determinerBaseAgent();
				a = getInstanceFromEU(resultat);
				currentRsrc -= GameEngine.COUT_AUGMENTATION_RESSOURCES;
				currentPowJoueur += Agent.valueOf(a);
				delta = (valueOfState(currentPowJoueur, currentPowEnnemi, currentCroissance,
						currentRsrc , _joueur.getListOfFactory().size()) - currentValue) / currentValue;
				pBase += delta / PEREQ_DELTA;
				
			}else if(rdm >= pNothing + pRsrc + pBase) {
				//superAgent
				resultat = EventUsine.horse;
				currentRsrc -= GameEngine.COUT_AUGMENTATION_RESSOURCES;
				currentPowJoueur += Agent.valueOf(HorseAgent.getInstance());
				delta = (valueOfState(currentPowJoueur, currentPowEnnemi, currentCroissance,
						currentRsrc , _joueur.getListOfFactory().size()) - currentValue) / currentValue;
				pSuper += delta / PEREQ_DELTA;
			}
		}
		equilibrerProba();
		return resultat;
	}
	
	/**
	 * Methode permettant d'obtenir une instance d'agent à des fins de test à partir
	 * d'un eventUsinse
	 * 
	 * @param eu
	 * @return l'instance de test correspondant
	 */
	private Agent getInstanceFromEU(EventUsine eu) {
		switch (eu) {
		case axe :
			return AxeAgent.getInstance();
		case spear :
			return SpearAgent.getInstance();
		case sword :
			return SwordAgent.getInstance();
		case horse :
			return HorseAgent.getInstance();
		default :
			return null;
		}
	}

	private EventUsine determinerBaseAgent() {
		float scoreAxe = 0, scoreSword = 0, scoreSpear = 0;
		Agent axe = AxeAgent.getInstance(), sword = SwordAgent.getInstance(), spear = SpearAgent.getInstance();
		for(Agent agent : _ennemi.getListOfUnite()) {
			/*AXE*/
			if(axe.estFaibleContre(agent)) {
				scoreAxe --;
			}else {
				scoreAxe ++;
			}
			/*SWORD*/
			if(sword.estFaibleContre(agent)) {
				scoreSword --;
			}else {
				scoreSword ++;
			}
			/*SPEAR*/
			if(spear.estFaibleContre(agent)) {
				scoreSpear --;
			}else {
				scoreSpear ++;
			}
		}
		scoreAxe = Math.max(0, scoreAxe);
		scoreSpear = Math.max(0, scoreSpear);
		scoreSword = Math.max(0, scoreSword);
		/*on ajoute 0.01 pour contrer le cas où tmp = 0*/
		float tmp = scoreAxe + scoreSword + scoreSpear;
		scoreAxe = (scoreAxe > 0 ? scoreAxe / tmp : 0);
		scoreSpear = (scoreSpear > 0 ? scoreSpear / tmp : 0);
		scoreSword = (scoreSword > 0 ? scoreSword / tmp : 0);
		double rand = Math.random();
		if(rand < scoreAxe) {
			return EventUsine.axe;
		}else if(rand < scoreAxe + scoreSword) {
			return EventUsine.sword;
		}else {
			return EventUsine.spear;
		}
	}

	public float valueOfState(float powJoueur, float powEnnemi, int croissance, int ressource,int nbUsines) {
		float tmp = ((powJoueur - powEnnemi) / powEnnemi) * COEFF_MILITARY;
		croissance = (croissance > nbUsines * GameEngine.COUT_SUPER ? nbUsines * GameEngine.COUT_SUPER : croissance);
		tmp -= ((GameEngine.COUT_SUPER + GameEngine.COUT_BASE) / croissance) * COEFF_CROISSANCE;
		tmp += ((ressource + croissance) / GameEngine.COUT_SUPER) * COEFF_RSRC;
		return tmp;
	}
	
	public void equilibrerProba() {
		probaBase = (probaBase < 0f ? 0f : probaBase);
		probaNothing = (probaNothing < 0f ? 0f : probaNothing);
		probaRsrc = (probaRsrc < 0f ? 0f : probaRsrc);
		probaSuper = (probaSuper < 0f ? 0f : probaSuper);
		float tmp = probaBase + probaNothing + probaRsrc +probaSuper;
		probaBase /= tmp;
		probaNothing /= tmp;
		probaRsrc /= tmp;
		probaSuper /= tmp;
	}


	protected Terrain destKing() {
		return null;
	}
	@Override
	public boolean traiteEvent() {
		return false;
	}

	@Override
	public void setFinDeTour(boolean b) {		
	}

	@Override
	public void remove(Agent agent) {
		mapComportement.remove(agent);
	}

}
