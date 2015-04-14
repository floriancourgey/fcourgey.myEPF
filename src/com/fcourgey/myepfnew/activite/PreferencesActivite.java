package com.fcourgey.myepfnew.activite;

import android.os.Bundle;

import com.fcourgey.myepfnew.R;

public class PreferencesActivite extends _MereActivite {
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		
		// bug sur lollipop : préférences non chargées
		// cette ligne les fait apparaître
		setContentView(R.layout.preferences_activity_bug);
		
		// on lance le fragment préférences
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PreferencesFragment()).commit();

	}
}