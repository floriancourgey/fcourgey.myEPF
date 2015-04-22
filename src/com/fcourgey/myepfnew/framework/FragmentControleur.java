package com.fcourgey.myepfnew.framework;

import android.content.res.Resources;
import android.os.Bundle;


public class FragmentControleur {
	
	// chaque contrôleur est associé à un fragment
	protected Fragment f;
	// et à une vue
	protected FragmentVue vue;
	// raccourci pour l'activité
	protected Activite a;
	
	public FragmentControleur(Fragment f){
		this.f = f;
		this.a = f.getActivite();
	}
	
	// getter
	public Fragment getFragment(){
		return f;
	}
	public FragmentVue getVue(){
		return vue;
	}
	public Activite getActivite() {
		return a;
	}
	// raccourci
	public Resources getResources(){
		return f.getActivite().getResources();
	}
	// alias de @see getResources
	public Resources getRessources(){
		return getResources();
	}
	// raccourci
	protected Bundle getArguments(){
		return f.getArguments();
	}
	// alias de @see getArguments
	protected Bundle getArgs(){
		return getArguments();
	}
	
}
