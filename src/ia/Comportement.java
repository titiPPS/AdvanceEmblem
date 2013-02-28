package ia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import engine.Agent;
import engine.KingAgent;
import engine.Player;
import engine.Terrain;
import game.GameEngine;

public class Comportement {
	
	private Player _joueur;
	private GameEngine gEngine;
	private float score;
	private float penaliteMort,recompenseCmdt;
	
	/*Long terme*/
	private float pLongTerme,probaCmdt;
	
	/*ATQ*/
	private float coeffTuer,coeffDmgInfl,coeffDmgRec,coeffCibleCommune;
	
	/*Type de comportement*/
	private float objDEF,objATQ;
	
	public Comportement(GameEngine ge,Player joueur,float penaliteMort,float recompenseCmdt) {
		this._joueur = joueur;
		this.gEngine = ge;
		this.penaliteMort = penaliteMort;
		this.recompenseCmdt = recompenseCmdt;
	}
	
	
	public void setDistribution(float ATQ, float DEF) {
		objDEF = DEF;
		objATQ = ATQ;
	}
	
	public void setCoeffATQ(float coeffTUER, float coeffDmgInfl, float coeffDmgRec,float coeffCibleCommune) {
		this.coeffTuer = coeffTUER;
		this.coeffDmgInfl = coeffDmgInfl;
		this.coeffDmgRec = coeffDmgRec;
		this.coeffCibleCommune = coeffCibleCommune;
	}
	
	public void setCoeffLongTerme(float pLongTerme,float probaCmdt) {
		this.pLongTerme = pLongTerme;
		this.probaCmdt = probaCmdt;
	}
	
	public Terrain choisirDestination(Agent agent, ArrayList<Terrain> lstTerrain, Player ennemi) {
		if(Math.random() < pLongTerme) {
			return evaluerDestination(lstTerrain);
		}else {
			return destinationObjectif(agent,ennemi);
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
		Terrain res = null;
		float[] scores = new float[lstTerrain.size()];
		for(int i = 0 ; i < lstTerrain.size(); i ++) {
			scores[i] = evaluerTerrain(lstTerrain.get(i));
		}
		return res;
	}
	
	private float evaluerTerrain(Terrain terrain) {
		float score = 0f;
		return 0;
	}

	public Terrain destinationObjectif(Agent a,Player ennemi) {
		Terrain init = gEngine.getTerrain(a.getX(), a.getY());
		if(objATQ > objDEF) {
			Agent comdt = ennemi.getCommandant();
			Terrain dest = gEngine.getTerrain(comdt.getX(), comdt.getY());
			ArrayList<Terrain> cheminToCmdt = PathFinder.aStarForATQ(init, dest, a.getMouvement(), _joueur);
			for(int j = 1; j < cheminToCmdt.size(); j++) {
				if(cheminToCmdt.get(j).getOccupant() != null) {
					return cheminToCmdt.get(j - 1);
				}
			}
			
		}else {
			boolean xCondition = (a.getX() + a.getMouvement() >= _joueur.getCommandant().getX() && a.getX() - a.getMouvement() <= _joueur.getCommandant().getX());
			boolean yCondition = (a.getY() + a.getMouvement() >= _joueur.getCommandant().getY() && a.getY() - a.getMouvement() <= _joueur.getCommandant().getY());
			if (xCondition && yCondition){
				//Perimetre de protection
				ArrayList<Agent> lstMenaces = calculerMenaces(_joueur.getCommandant(), ennemi);
				Collections.sort(lstMenaces, new Comparator<Agent>() {
					@Override
					public int compare(Agent o1, Agent o2) {
					return Agent.valueOf(o2) - Agent.valueOf(o1);
					}
				});
				Terrain[] tab = new Terrain[a.getMouvement()];
				ArrayList<Terrain> chemin;
				
				/*On évalue la menace la plus pertinente (il vaut mieux tuer qqn
				 * plutôt que de se diriger vers qqn d'autre et ne rien faire)
				 * 
				 */
				for(int i = 0; i < lstMenaces.size() ; i++) {
					chemin = PathFinder.aStarForATQ(init, gEngine.getTerrain(a.getX(), a.getY()), a.getMouvement(), _joueur);
					if(chemin != null) {
						for(int j = 1; j < chemin.size(); j++) {
							if(chemin.get(j).getOccupant() != null) {
								return chemin.get(j);
							}
						}
					}
				}
				
			}else {
				//on essaye de rejoindre le commandant et on dézingue ce qu'il y a sur le chemin
				boolean destinationFound = false;
				int cpt = 0;
				//Terrain init = gEngine.getTerrain(_joueur.getCommandant().getX(), _joueur.getCommandant().getY());
				ArrayList<Terrain> al;
				HashSet<Terrain> lstDestCmdt = PathFinder.listeDestination(init, _joueur.getCommandant().getMouvement());
				Terrain[] tab = new Terrain[lstDestCmdt.size()];
				tab = lstDestCmdt.toArray(tab);
				while(!destinationFound && cpt < tab.length) {
					al = PathFinder.aStar(init, tab[cpt], a.getMouvement());
					if(al != null) {
						return tab[cpt];
					}
					cpt ++;
				}
				
			}
		}
		HashSet<Terrain> set = PathFinder.listeDestination( gEngine.getTerrain( a.getX(), a.getY()), a.getMouvement());
		ArrayList<Terrain> al = new ArrayList<Terrain>();
		al.addAll(set);
		return evaluerDestination(al);
	}

}
