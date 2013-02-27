package ia;

import java.util.ArrayList;

import engine.Agent;
import engine.KingAgent;
import engine.Player;
import engine.Terrain;

public class Comportement {
	
	private Player _joueur;
	private float score;
	private float pLongTerme,penaliteMort,recompenseCmdt;
	
	/*ATQ*/
	private float coeffTuer,coeffDmgInfl,coeffDmgRec,coeffCibleCommune;
	
	public Comportement(Player joueur,float pLongTerme,float penaliteMort,float recompenseCmdt) {
		this._joueur = joueur;
		this.pLongTerme = pLongTerme;
		this.penaliteMort = penaliteMort;
		this.recompenseCmdt = recompenseCmdt;
	}
	
	public void setCoeffATQ(float coeffTUER, float coeffDmgInfl, float coeffDmgRec,float coeffCibleCommune) {
		this.coeffTuer = coeffTUER;
		this.coeffDmgInfl = coeffDmgInfl;
		this.coeffDmgRec = coeffDmgRec;
		this.coeffCibleCommune = coeffCibleCommune;
	}
	
	public Terrain choisirDestination(ArrayList<Terrain> lstTerrain, Player ennemi) {
		if(Math.random() < pLongTerme) {
			return evaluerDestination(lstTerrain);
		}else {
			return destinationObjectif(ennemi);
		}
	}
	
	/**
	 * 
	 * 
	 * @param posEnnemisAdj les cases adjacentes à l'agent qui contiennent des ennemis
	 * @param position le terrain de l'agent
	 * @param a l'agent
	 * @return la position de la meilleure cible ou null s'il vaut mieux ne pas attaquer
	 */
	public Terrain choixATQ(ArrayList<Terrain> posEnnemisAdj,Terrain position,Agent a) {
		Terrain res = null;
		float[] scores = new float[posEnnemisAdj.size()];
		float total = 0.0f;
		for(int i = 0 ; i < posEnnemisAdj.size(); i ++) {
			scores[i] = 0;
			Terrain t = posEnnemisAdj.get(i);
			Agent ennemi = t.getOccupant();
			int dmgInfliges = a.calculDegats(ennemi, t), dmgRecus = ennemi.calculDegats(a, position);
			boolean ennemiTue = (ennemi.getPV() - dmgInfliges <= 0); 
			if(ennemiTue) {
				dmgRecus = 0;
				scores[i] += coeffTuer * Agent.valueOf(ennemi);
			}
			boolean agentTue = (a.getPV() - dmgRecus <= 0);
			if(agentTue) {
				scores[i] -= penaliteMort;
			}
			ArrayList<Agent> lstAllies = calculerMenaces(a, _joueur);
			boolean cibleCommune = lstAllies.size() > 0;
			if(cibleCommune) {
				scores[i] += Math.min(4, lstAllies.size()) * coeffCibleCommune;
			}
			if(a instanceof KingAgent) {
				scores[i] += recompenseCmdt;
			}
			scores[i] += dmgInfliges * coeffDmgInfl + dmgRecus * coeffDmgRec;
			total += scores[i];
		}
		for(int i = 0; i < scores.length ; i++) {
			scores[i] = scores[i] / total;
		}
		double decision = Math.random();
		total = 0;
		for(int i = 0; i < scores.length ; i++) {
			if(decision < total + scores[i]) {
				res = posEnnemisAdj.get(i);
			}else {
				total += (scores[i] < 0 ? 0 : scores[i]);
			}
		}
		return res;
	}
	
	public ArrayList<Agent> calculerMenaces(Agent agent, Player ennemi) {
		ArrayList<Agent> resultat = new ArrayList<Agent>();
		for(Agent a : ennemi.getListOfUniteLarge()) {
			if(a.getX() <= agent.getX() + a.getMouvement() && a.getX() >= agent.getX() + a.getMouvement()) {
				if(a.getY() <= agent.getY() + a.getMouvement() && a.getY() >= agent.getY() + a.getMouvement()) {
					resultat.add(a);
				}
			}
		}
		return resultat;
	}
	
	public Terrain evaluerDestination(ArrayList<Terrain> lstTerrain) {
		return null;
	}
	
	public Terrain destinationObjectif(Player ennemi) {
		return null;
	}

}
