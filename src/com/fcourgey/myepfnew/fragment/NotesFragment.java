package com.fcourgey.myepfnew.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.AsyncFragment;
import com.fcourgey.myepfnew.controleur.NotesControleur;

public class NotesFragment extends AsyncFragment {
	/**
	 * onCreate
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		if(controleur == null)
			controleur = new NotesControleur(this, inflater, container);
		
		return controleur.getView();
	}

}
