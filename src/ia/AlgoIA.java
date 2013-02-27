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
	
	private Player _joueur,_ennemi;
	private float coeffRsrc, coeffMilitary,coeffCroissance;
	
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
				
			}
		}
	}
	
	public EventUsine determinerAction(int currentCroissance, float currentMilitary, float currentRsrc) {
		float max = 0;
		EventUsine resultat = EventUsine.nothing;
		for(EventUsine eu : EventUsine.values()) {
			float result = 0.0f;
			switch(eu) {
				case horse:
					if(_joueur.getRessources() >= GameEngine.COUT_SUPER)
						result = valueOfConstruction(GameEngine.COUT_SUPER, HorseAgent.getInstance(), currentCroissance,
							currentMilitary, currentRsrc);
					break;
				case spear :
					if(_joueur.getRessources() >= GameEngine.COUT_BASE)
						result = valueOfConstruction(GameEngine.COUT_BASE, SpearAgent.getInstance(), currentCroissance,
								currentMilitary, currentRsrc);
					break;
				case sword :
					if(_joueur.getRessources() >= GameEngine.COUT_BASE)
						result = valueOfConstruction(GameEngine.COUT_BASE, SwordAgent.getInstance(), currentCroissance,
								currentMilitary, currentRsrc);
					break;
				case axe :
					if(_joueur.getRessources() >= GameEngine.COUT_BASE)
						result = valueOfConstruction(GameEngine.COUT_BASE, AxeAgent.getInstance(), currentCroissance,
								currentMilitary, currentRsrc);
					break;
				case gold :
					if(_joueur.getRessources() >= GameEngine.COUT_AUGMENTATION_RESSOURCES)
						result = valueOfConstruction(GameEngine.COUT_AUGMENTATION_RESSOURCES, HorseAgent.getInstance(), currentCroissance,
								currentMilitary, currentRsrc);
					break;
			}
		}
		return resultat;
	}
	
	public float valueOfConstruction(float coutOp,Agent a, float croissance, float military, float currentRsrc) {
		float result = (currentRsrc - coutOp) * coeffRsrc;
		if(a != null) {
			result += (military + (a.getAtq() + a.getDef()) * a.getPV() / coutOp) * coeffMilitary;
		} else {
			result += military * coeffMilitary;
		}
		result += (croissance / GameEngine.COUT_SUPER > _joueur.getListOfFactory().size() ? 
				0 : (croissance / GameEngine.COUT_SUPER + croissance / GameEngine.COUT_BASE)* coeffCroissance);
		return result;
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
