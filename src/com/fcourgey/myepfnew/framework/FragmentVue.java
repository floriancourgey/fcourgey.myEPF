package com.fcourgey.myepfnew.framework;

import android.content.res.Resources;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentVue {
	// chaque vue est associée à un contrôleur
	protected FragmentControleur controleur;
	// et à un rendu/view/vue 
	protected View vue;
	
	public FragmentVue(FragmentControleur controleur, LayoutInflater inflater, ViewGroup container, int idLayout){
		this.controleur = controleur;
		vue = inflater.inflate(idLayout, container, false);
	}
	
	public FragmentActivity getActivite(){
		return controleur.getActivite();
	}
	public View getVue(){
		return vue;
	}
	// raccourci
	public Resources getResources(){
		return controleur.getResources();
	}
	// alias de @see getResources
	public Resources getRessources(){
		return getResources();
	}
}
