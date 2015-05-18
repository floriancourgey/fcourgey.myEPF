package com.fcourgey.myepfnew.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.AsyncFragment;
import com.fcourgey.myepfnew.controleur.EdtControleur;

public class EdtFragment extends AsyncFragment {
	
	/**
	 * onCreate
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		controleur = new EdtControleur(this, inflater, container);
		
		return controleur.getView();
	}

	public void onDelaiDAttenteDepassé() {
//		((EdtControleur)controleur).onDelaiDAttenteDepassé();
	}
	
	

}
