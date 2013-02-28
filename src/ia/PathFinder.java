package ia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import engine.Player;
import engine.Terrain;

public class PathFinder {
	
	public static HashSet<Terrain> listeDestination2(Terrain init, int mouvement) {
		HashSet<Terrain> resultat = new HashSet<Terrain>();
		resultat.add(init);
		if(mouvement > 0) {
			for(Terrain t : init.getListeVoisins()) {
				if(t.getCout() <= mouvement && t.isAccessible()) {
					resultat.add(t);
					resultat.addAll(listeDestination(t,mouvement - t.getCout()));
				}
			}
		}
		return resultat;
	}
	
	public static ArrayList<Terrain> listeDestination(Terrain init, int mouvement) {
		ArrayList<Terrain> resultat = new ArrayList<Terrain>();
		ArrayList<Terrain> dejaVu = new ArrayList<Terrain>();
		ArrayList<Terrain> enCours = new ArrayList<Terrain>();
		enCours.add(init);
		HashMap<Terrain, Integer> coutsTerrain = new HashMap<Terrain, Integer>();
		HashMap<Terrain, Terrain> pere = new HashMap<Terrain, Terrain>();
		coutsTerrain.put(init, 0);
		while(!enCours.isEmpty()){
			//System.out.println("inside while");
			Terrain t = choisirTerrain(enCours, coutsTerrain);
			if(coutsTerrain.get(t) < mouvement) {
				enCours.remove(t);
				dejaVu.add(t);
				for(Terrain voisin : t.getListeVoisins()){
					if(voisin.isAccessible()) {
						if(!dejaVu.contains(voisin) && !enCours.contains(voisin)){
							pere.put(voisin, t);	
							coutsTerrain.put(voisin, voisin.getCout() + coutsTerrain.get(t));
							enCours.add(voisin);
						}
						else{
							if( coutsTerrain.get(voisin) > coutsTerrain.get(t) + voisin.getCout()){
								if(dejaVu.contains(voisin)){
									dejaVu.remove(voisin);
								}
								pere.put(voisin, t);
								coutsTerrain.put(voisin, voisin.getCout() + coutsTerrain.get(t));
								enCours.add(voisin);
							}
						}
					}
				}
			}
			else if(coutsTerrain.get(t) == mouvement) {
				enCours.remove(t);
				dejaVu.add(t);
			}else {
				enCours.remove(t);
			}
		}
		for(Map.Entry<Terrain,Integer> entre : coutsTerrain.entrySet()) {
			if(entre.getValue() <= mouvement) {
				resultat.add(entre.getKey());
			}
		}
		return resultat;
	}
	
	public static ArrayList<Terrain> aStar(Terrain init,Terrain dest, int mouvement) {
		ArrayList<Terrain> dejaVu = new ArrayList<Terrain>();
		ArrayList<Terrain> enCours = new ArrayList<Terrain>();
		enCours.add(init);
		boolean cheminTrouve = false;
		HashMap<Terrain, Integer> coutsTerrain = new HashMap<Terrain, Integer>();
		HashMap<Terrain, Terrain> pere = new HashMap<Terrain, Terrain>();
		coutsTerrain.put(init, 0);
		while(!enCours.isEmpty() && !cheminTrouve){
			//System.out.println("inside while");
			Terrain t = choisirTerrain(enCours, coutsTerrain);
			if(dest.equals(t)){
				cheminTrouve = true;
			}
			else{
				enCours.remove(t);
				dejaVu.add(t);
				for(Terrain voisin : t.getListeVoisins()){
					if(voisin.isAccessible()) {
						if(!dejaVu.contains(voisin) && !enCours.contains(voisin)){
							pere.put(voisin, t);	
							coutsTerrain.put(voisin, voisin.getCout() + coutsTerrain.get(t));
							enCours.add(voisin);
						}
						else{
							if( coutsTerrain.get(voisin) > coutsTerrain.get(t) + voisin.getCout()){
								if(dejaVu.contains(voisin)){
									dejaVu.remove(voisin);
								}
								pere.put(voisin, t);
								coutsTerrain.put(voisin, voisin.getCout() + coutsTerrain.get(t));
								enCours.add(voisin);
							}
						}
					}
				}
			}
		}
		if(!cheminTrouve) {
			return null;
		}else {
			ArrayList<Terrain> resultat = new ArrayList<Terrain>();
			Terrain t = dest;
			while(!t.equals(init)) {
				resultat.add(0, t);
				t = pere.get(t);
			}
			resultat.add(0,t);
			for(int i =  resultat.size() - 1; i > 0; i--) {
				if(coutsTerrain.get(resultat.get(i)) > mouvement) {
					resultat.remove(i);
				}
			}

			return resultat;
		}
	}
	
	public static ArrayList<Terrain> aStarForATQ(Terrain init,Terrain dest, int mouvement,Player p) {
		ArrayList<Terrain> dejaVu = new ArrayList<Terrain>();
		ArrayList<Terrain> enCours = new ArrayList<Terrain>();
		enCours.add(init);
		boolean cheminTrouve = false;
		HashMap<Terrain, Integer> coutsTerrain = new HashMap<Terrain, Integer>();
		HashMap<Terrain, Terrain> pere = new HashMap<Terrain, Terrain>();
		coutsTerrain.put(init, 0);
		while(!enCours.isEmpty() && !cheminTrouve){
			//System.out.println("inside while");
			Terrain t = choisirTerrain(enCours, coutsTerrain);
			if(dest.equals(t)){
				cheminTrouve = true;
			}
			else{
				enCours.remove(t);
				dejaVu.add(t);
				for(Terrain voisin : t.getListeVoisins()){
					if(voisin.isAccessible(p)) {
						if(!dejaVu.contains(voisin) && !enCours.contains(voisin)){
							pere.put(voisin, t);	
							coutsTerrain.put(voisin, voisin.getCout() + coutsTerrain.get(t));
							enCours.add(voisin);
						}
						else{
							if( coutsTerrain.get(voisin) > coutsTerrain.get(t) + voisin.getCout()){
								if(dejaVu.contains(voisin)){
									dejaVu.remove(voisin);
								}
								pere.put(voisin, t);
								coutsTerrain.put(voisin, voisin.getCout() + coutsTerrain.get(t));
								enCours.add(voisin);
							}
						}
					}
				}
			}
		}
		if(!cheminTrouve) {
			return null;
		}else {
			ArrayList<Terrain> resultat = new ArrayList<Terrain>();
			Terrain t = dest;
			while(!t.equals(init)) {
				resultat.add(0, t);
				t = pere.get(t);
			}
			resultat.add(0,t);
			for(int i =  resultat.size() - 1; i > 0; i--) {
				if(coutsTerrain.get(resultat.get(i)) > mouvement) {
					resultat.remove(i);
				}
			}

			return resultat;
		}
	}
	
	private static Terrain choisirTerrain(ArrayList<Terrain> enCours,HashMap<Terrain, Integer> coutsTerrain) {
		double min = Integer.MAX_VALUE;
		Terrain resultat = null;
		for(Terrain t : enCours){
			if(min >= coutsTerrain.get(t)){
				resultat = t;
				min = coutsTerrain.get(t);
			}
		}
		return resultat;
	}
}
