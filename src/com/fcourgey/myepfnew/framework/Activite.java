package com.fcourgey.myepfnew.framework;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.fcourgey.myepfnew.modele.PreferencesModele;

public class Activite extends ActionBarActivity {
	
	public static final String logcatFilter = "tag:^(?!.*(dalvik|EGL|Choreo)).*$";
	
	public static Context context;
	public static PreferencesModele prefs;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(context == null){
			context =  this;
		}
		if(prefs == null){
			prefs = new PreferencesModele(this);
		}
	}
	

	public Context getContext(){
		return context;
	}
	public PreferencesModele getPrefs(){
		return prefs;
	}
}
