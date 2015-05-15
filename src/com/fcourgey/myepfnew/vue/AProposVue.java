package com.fcourgey.myepfnew.vue;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.FragmentControleur;
import com.fcourgey.android.mylib.framework.FragmentVue;
import com.fcourgey.myepfnew.R;

public class AProposVue extends FragmentVue{

	public AProposVue(FragmentControleur controleur, LayoutInflater inflater, ViewGroup container, int idLayout, String versionName) {
		super(controleur, inflater, container, idLayout);
		
		TextView tvAPropos = (TextView)vue.findViewById(R.id.tvAPropos);
		tvAPropos.setText("myEPF version "+versionName+" développé par Florian Courgey pour l'EPF École d'ingénieur-e-s.");
	}
	
}
