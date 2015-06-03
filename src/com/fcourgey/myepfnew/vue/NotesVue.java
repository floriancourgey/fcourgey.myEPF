package com.fcourgey.myepfnew.vue;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.fcourgey.android.mylib.framework.AsyncFragmentVue;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.controleur.NotesControleur;

public class NotesVue extends AsyncFragmentVue {
	
	@InjectView(R.id.tvDerniereActualisation)
	protected TextView tvDerniereActualisation;
	@InjectView(R.id.llDerniereActualisation)
	protected LinearLayout llDerniereActualisation;
	
	public NotesVue(NotesControleur controleur, ViewGroup container, String derniereActu){
		super(controleur, R.layout.notes_fragment, container);
		
		ButterKnife.inject(this, getView());
		
		setDerniereActualisation(derniereActu);
	}
	
	public void setDerniereActualisation(String s){
		if(s != null){
			llDerniereActualisation.setVisibility(View.VISIBLE);
			tvDerniereActualisation.setText(s);
		} else {
			llDerniereActualisation.setVisibility(View.GONE);
		}
	}
}
