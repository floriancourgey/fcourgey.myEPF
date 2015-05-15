package com.fcourgey.myepfnew.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.AsyncFragment;
import com.fcourgey.myepfnew.controleur.BulletinControleur;

public class BulletinFragment extends AsyncFragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		controleur = new BulletinControleur(this, inflater, container);
		
		return controleur.getVue().getVue();
	}
	
	public void onDelaiDAttenteDepassé() {
		((BulletinControleur)controleur).onDelaiDAttenteDepassé();
	}
	
}
