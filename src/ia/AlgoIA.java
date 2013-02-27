package ia;

import java.util.ArrayList;

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
	
	private Player _joueur,_ennemi;
	
	private float coeffRsrc, coeffMilitary,coeffCroissance;
	private float probaSuper = 0.3f, probaBase = 0.35f,probaRsrc = 0.3f,probaNothing = 0.005f;
	
	public AlgoIA(Player joueur,Player ennemi) {
		this._joueur = joueur;
		this._ennemi = ennemi;
		coeffRsrc = COEFF_RSRC;
		coeffMilitary = COEFF_MILITARY;
		coeffCroissance = COEFF_CROISSANCE;
	}

	@Override
	public void jouer(GameEngine gEngine) {
		//TODO Gestion du commandant
		Agent king = _joueur.getCommandant();
		//On établit la liste des menaces pesant sur le commandant
		ArrayList<Terrain> positionsMenaces = new ArrayList<Terrain>();
		for(Agent a : _ennemi.getListOfUniteLarge()) {
			if(a.getX() <= king.getX() + a.getMouvement() && a.getX() >= king.getX() + a.getMouvement()) {
				if(a.getY() <= king.getY() + a.getMouvement() && a.getY() >= king.getY() + a.getMouvement()) {
					positionsMenaces.add(gEngine.getTerrain(a.getX(),a.getY()));
				}
			}
		}
		
		//TODO Gestion des unites
		
		//TODO Gestion de la production
		//Nouvelle estimation des menaces pesant sur le commandant
		positionsMenaces = new ArrayList<Terrain>();
		for(Agent a : _ennemi.getListOfUniteLarge()) {
			if(a.getX() <= king.getX() + a.getMouvement() && a.getX() >= king.getX() + a.getMouvement()) {
				if(a.getY() <= king.getY() + a.getMouvement() && a.getY() >= king.getY() + a.getMouvement()) {
					positionsMenaces.add(gEngine.getTerrain(a.getX(),a.getY()));
				}
			}
		}
		
		for(Factory usine : _joueur.getListOfFactory()) {
			Terrain t = gEngine.getTerrain(usine.getX(), usine.getY());
			if(t.getOccupant() == null) {
				gEngine.construireAgent(_joueur, usine, determinerAction(positionsMenaces));
			}
		}
	}
	
	public EventUsine determinerAction(ArrayList<Terrain> positionsMenaces) {
		float currentPowJoueur = _joueur.calcPowMilitaire(), currentPowEnnemi = _ennemi.calcPowMilitaire();
		int currentCroissance = _joueur.getCroissance(), currentRsrc = _joueur.getRessources() ;
		float currentValue = valueOfState(currentPowJoueur, currentPowEnnemi, currentCroissance, 
				currentRsrc, _joueur.getListOfFactory().size());
		float delta = 0.0f;
		EventUsine resultat = EventUsine.nothing;
		double rdm = Math.random();
		/*On met à jour la variable de décision aléatoire en fonction des ressources du joueur*/
		if(_joueur.getRessources() < GameEngine.COUT_SUPER) {
			if(_joueur.getRessources() < GameEngine.COUT_BASE) {
				if(_joueur.getRessources() < GameEngine.COUT_AUGMENTATION_RESSOURCES) {
					rdm = 0;
				}else {
					rdm *= (probaNothing + probaRsrc);
				}
			}else {
				rdm *= (probaNothing + probaRsrc + probaBase);
			}
		}else if(positionsMenaces.size() > 0){
			double random = Math.random();
			if(random > Math.pow(2, -positionsMenaces.size())) {
				rdm += probaNothing + probaRsrc;
				rdm = Math.min(1.0, rdm);
			}
		}
		
		if(rdm < (probaNothing + probaRsrc)) {
			//On privilégie les ressources
			if(rdm > probaNothing) {
				//On augmente la croissance
				resultat = EventUsine.gold;
				currentRsrc -= GameEngine.COUT_AUGMENTATION_RESSOURCES;
				currentCroissance += GameEngine.AUGMENTATION_CROISSANCE;
				delta = (valueOfState(currentPowJoueur, currentPowEnnemi, currentCroissance,
						currentRsrc , _joueur.getListOfFactory().size()) - currentValue) / currentValue;
				probaRsrc += delta / PEREQ_DELTA;
			}
		}else {
			//Construction d'unites militaires
			if(rdm < probaNothing + probaRsrc + probaBase) {
				//Base Agent
				Agent a;
				resultat = determinerBaseAgent();
				a = getInstanceFromEU(resultat);
				currentRsrc -= GameEngine.COUT_AUGMENTATION_RESSOURCES;
				currentPowJoueur += Agent.valueOf(a);
				delta = (valueOfState(currentPowJoueur, currentPowEnnemi, currentCroissance,
						currentRsrc , _joueur.getListOfFactory().size()) - currentValue) / currentValue;
				probaBase += delta / PEREQ_DELTA;
				
			}else if(rdm >= probaNothing + probaRsrc + probaBase) {
				//superAgent
				resultat = EventUsine.horse;
				currentRsrc -= GameEngine.COUT_AUGMENTATION_RESSOURCES;
				currentPowJoueur += Agent.valueOf(HorseAgent.getInstance());
				delta = (valueOfState(currentPowJoueur, currentPowEnnemi, currentCroissance,
						currentRsrc , _joueur.getListOfFactory().size()) - currentValue) / currentValue;
				probaSuper += delta / PEREQ_DELTA;
			}
		}
		equilibrerProba();
		return resultat;
	}
	
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
		}else if(rand < scoreSword) {
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
		// TODO Auto-generated method stub
		
	}

}
