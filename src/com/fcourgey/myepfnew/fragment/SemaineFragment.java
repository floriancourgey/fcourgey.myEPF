package com.fcourgey.myepfnew.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.controleur.SemaineControleur;

public class SemaineFragment extends Fragment {
	
	public static final String KEY_INDEX_FRAGMENT = "KEY_INDEX_FRAGMENT";
	
	/**
	 * appelé avant de lancer un onCreate
	 * +
	 * initialise le numéro d'index
	 */
	public static Fragment newInstance(int indexFragment) {
		SemaineFragment f = new SemaineFragment();
		Bundle args = new Bundle();

		args.putInt(KEY_INDEX_FRAGMENT, indexFragment);

		f.setArguments(args);
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		int indexFragment = getArguments().getInt(KEY_INDEX_FRAGMENT);
		
		controleur = new SemaineControleur(this, inflater, container, indexFragment);

		return controleur.getVue().getVue();
	}
}
