package com.fcourgey.myepfnew.vue;

import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.AsyncFragmentVue;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.controleur.NotesControleur;

public class NotesVue extends AsyncFragmentVue {
	
	public NotesVue(NotesControleur controleur, ViewGroup container){
		super(controleur, R.layout.notes_fragment, container);
	}
}
