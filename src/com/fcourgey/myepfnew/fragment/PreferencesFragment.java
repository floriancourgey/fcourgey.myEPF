package com.fcourgey.myepfnew.fragment;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.fcourgey.android.mylib.framework.Activite;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
import com.fcourgey.myepfnew.outils.Android;
import com.fcourgey.myepfnew.outils.Securite;
import com.fcourgey.myepfnew.vue.ViderCacheVue;

public class PreferencesFragment extends PreferenceFragment {
	
	private String mdpDansPreferences;
	
	private MyEpfPreferencesModele pref;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		pref = (MyEpfPreferencesModele)((Activite)getActivity()).getPrefs();
		
        addPreferencesFromResource(R.layout.preferences);

        // ajustement de certaines pref
        /// nb semaines to dl
        ListPreference prefNbSemainesToDl = (ListPreference) findPreference(MyEpfPreferencesModele.KEY_NB_SEMAINES_TO_DL);
        String[] entries = new String[MyEpfPreferencesModele.NB_SEMAINES_MAX-MyEpfPreferencesModele.NB_SEMAINES_MIN+1];
        for(int i=0 ; i<MyEpfPreferencesModele.NB_SEMAINES_MAX-MyEpfPreferencesModele.NB_SEMAINES_MIN+1 ; i++){
        	entries[i] = Integer.toString(i+MyEpfPreferencesModele.NB_SEMAINES_MIN);
        }
        prefNbSemainesToDl.setEntries(entries);
        prefNbSemainesToDl.setEntryValues(entries);
        /// vider le cache
        Preference cache = (Preference)findPreference(MyEpfPreferencesModele.KEY_CACHE);
        cache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ViderCacheVue.show(getActivity(), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						MyEpfPreferencesModele.viderCache();
						Android.quitter();
					}
				});
				return false;
			}
		});
        /// hash mdp
        mdpDansPreferences = pref.getMdp();
        findPreference(MyEpfPreferencesModele.KEY_MDP).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
            	// try catch au cas où pour le o.toString()
            	try{
	                // si mdp identiques, on touche à rien
	                String nouveauMdp = o.toString();
	                if(nouveauMdp.equals(mdpDansPreferences)){
	                	return false;
	                }
	                // sinon on le sauvegarde haché
	                pref.setMdp(Securite.encrypt(nouveauMdp));
            	} catch(Exception e){
            		e.printStackTrace();
            	}
                return false;
            }
            
        });
	}
}
