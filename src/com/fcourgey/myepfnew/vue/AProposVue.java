package com.fcourgey.myepfnew.vue;

import android.view.ViewGroup;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.FragmentVue;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.controleur.AProposControleur;

public class AProposVue extends FragmentVue{

	public AProposVue(AProposControleur controleur, ViewGroup container, String versionName) {
		super(controleur, R.layout.apropos_fragment, container);
		
		TextView tvAPropos = (TextView)findViewById(R.id.tvAPropos);
		tvAPropos.setText("myEPF version "+versionName+" développé par Florian Courgey pour l'EPF École d'ingénieur-e-s.");
	}
	
}
