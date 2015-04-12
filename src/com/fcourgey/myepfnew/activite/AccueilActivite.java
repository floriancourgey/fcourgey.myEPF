package com.fcourgey.myepfnew.activite;

import android.content.Intent;
import android.os.Bundle;

import com.fcourgey.myepfnew.controlleur.AccueilControleur;

public class AccueilActivite extends _MereActivite {
	
	/**
	 * Point de départ de l'appli
	 */
	@Override
	public void onCreate(Bundle b) {
    	super.onCreate(b);
    	
    	new AccueilControleur(this);
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
}
