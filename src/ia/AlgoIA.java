package ia;

import java.util.ArrayList;

import engine.Agent;
import engine.Player;
import engine.Terrain;
import game.GameEngine;

public class AlgoIA implements IAlgorithme{
	private Player _joueur,_ennemi;
	
	public AlgoIA(Player joueur,Player ennemi) {
		this._joueur = joueur;
		this._ennemi = ennemi;
	}

	@Override
	public void jouer(GameEngine gEngine) {
		//TODO Gestion du commandant
		Agent king = _joueur.getCommandant();
		//On établit la liste des menaces
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
