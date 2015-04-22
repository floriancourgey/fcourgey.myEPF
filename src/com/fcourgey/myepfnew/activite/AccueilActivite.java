package com.fcourgey.myepfnew.activite;

import android.content.Intent;
import android.os.Bundle;

import com.fcourgey.myepfnew.controleur.AccueilControleur;
import com.fcourgey.myepfnew.framework.Activite;

public class AccueilActivite extends Activite {
	
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
