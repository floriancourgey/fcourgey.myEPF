package com.fcourgey.myepfnew.activite;

import android.os.Bundle;

import com.fcourgey.android.mylib.framework.Activite;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.fragment.PreferencesFragment;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;

public class PreferencesActivite extends Activite {
	
	private MyEpfPreferencesModele prefs;
	
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		
		prefs = AccueilActivite.prefs;
		
		// bug sur lollipop : préférences non chargées
		// cette ligne les fait apparaître
		setContentView(R.layout.preferences_activity_bug);
		
		// on lance le fragment préférences
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();

	}

	@Override
	public MyEpfPreferencesModele getPrefs() {
		return prefs;
	}
}
