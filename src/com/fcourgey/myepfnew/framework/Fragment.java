package com.fcourgey.myepfnew.framework;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment extends android.support.v4.app.Fragment {
	
	// chaque fragment a une activité
	protected Activite a;
	// et un controleur
	protected FragmentControleur controleur;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// init mere
		a = (Activite)getActivity();
		
		return null;
	}
	
	public Activite getActivite(){
		return a;
	}
	public FragmentControleur getControleur(){
		return controleur;
	}
}
