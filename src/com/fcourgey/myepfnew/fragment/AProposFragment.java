package com.fcourgey.myepfnew.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.controleur.AProposControleur;

public class AProposFragment extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		controleur = new AProposControleur(this, inflater, container);
		
		return controleur.getView();
	}
}
