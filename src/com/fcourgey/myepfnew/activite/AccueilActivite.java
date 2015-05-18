package com.fcourgey.myepfnew.activite;

import android.content.Intent;
import android.os.Bundle;

import com.fcourgey.android.mylib.framework.Activite;
import com.fcourgey.myepfnew.controleur.AccueilControleur;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;

public class AccueilActivite extends Activite {
	
	public static MyEpfPreferencesModele prefs;
	
	/**
	 * Point de départ de l'appli
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	if(prefs == null){
			prefs = new MyEpfPreferencesModele(this);
		}
    	
    	controleur = new AccueilControleur(this, savedInstanceState);
	}
	
	/**
	 * Lance MainActivite
	 * et
	 * quitte cette activité
	 */
	public void lancerSemainesActivite(){
		Intent intent = new Intent(this, MainActivite.class);
		startActivity(intent);
		finish();
	}
	
	public MyEpfPreferencesModele getPrefs() {
		return prefs;
	}
}
