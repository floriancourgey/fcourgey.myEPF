package com.fcourgey.myepfnew.outils;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fcourgey.android.mylib.framework.Activite;
import com.fcourgey.myepfnew.entite.Cours;
import com.fcourgey.myepfnew.entite.Cours.Type;
import com.fcourgey.myepfnew.modele.DbModele;

public class JsonMyEPF {
	
	public static final String KEY_ARRAY_COURS = "cours";
	public static final String KEY_COURS_ID = "id";
	public static final String KEY_COURS_TITRE = "title";
	public static final String KEY_COURS_TYPE = "type";
	public static final String KEY_COURS_PROF = "enseignant";
	public static final String KEY_COURS_SALLE = "salle";
	public static final String KEY_COURS_DEBUT = "start";
	public static final String KEY_COURS_FIN = "end";
	public static final String KEY_COURS_COMMENTAIRE = "commentaire";	
	
	public static Cours jsoToListeCours(JSONObject jso, Activite a) throws JSONException{
		String nom = jso.getString(KEY_COURS_TITRE);
		nom = nom.replaceAll("\\(\\w+\\) ([\\w ]+)", "$1");
	    String professeur = jso.getString(KEY_COURS_PROF);
	    String salle = jso.getString(KEY_COURS_SALLE);
	    Type type;
	    try{
	    	type = Type.valueOf(jso.getString(KEY_COURS_TYPE));
	    } catch (Exception e){
	    	type = Type.AUTRE;
	    }
	    String commentaire = jso.getString(KEY_COURS_COMMENTAIRE);
	    Calendar horaireDebut = StringOutils.toCalendar(jso.getString(KEY_COURS_DEBUT), true);
	    Calendar horaireFin = StringOutils.toCalendar(jso.getString(KEY_COURS_FIN), true);
	    
	    String dateId = jso.getString(KEY_COURS_DEBUT);
	    
	    // recherche des devoirs dans la db
	    DbModele modele = new DbModele(a);
	    String devoirs = null;
		try {
			devoirs = modele.getDevoir(dateId);
		} catch (Exception e) {}
		
		Cours c = new Cours(dateId, nom, professeur, salle, horaireDebut, horaireFin, type, commentaire, devoirs);
		return c;
	}
	
	public static ArrayList<Cours> jsaToListeCours(JSONArray jsa, Activite a){
		ArrayList<Cours> lCours = new ArrayList<Cours>();
		
		try{
			for(int i=0; i<jsa.length() ; i++){
	    		JSONObject jso = jsa.getJSONObject(i);
	    		Cours c = jsoToListeCours(jso, a);
	    		lCours.add(c);
	    	}
		} catch(Exception e){
			e.printStackTrace(); // TODO
			return null;
		}
		return lCours;
	}
}
