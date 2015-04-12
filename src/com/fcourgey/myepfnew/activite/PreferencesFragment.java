package com.fcourgey.myepfnew.activite;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.modele.PreferencesModele;
import com.fcourgey.myepfnew.outils.Android;
import com.fcourgey.myepfnew.vue.ViderCacheVue;

public class PreferencesFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);

        // ajustement de certaines pref
        /// nb semaines to dl
        ListPreference prefNbSemainesToDl = (ListPreference) findPreference(PreferencesModele.KEY_NB_SEMAINES_TO_DL);
        String[] entries = new String[PreferencesModele.NB_SEMAINES_MAX-PreferencesModele.NB_SEMAINES_MIN+1];
        for(int i=0 ; i<PreferencesModele.NB_SEMAINES_MAX-PreferencesModele.NB_SEMAINES_MIN+1 ; i++){
        	entries[i] = Integer.toString(i+PreferencesModele.NB_SEMAINES_MIN);
        }
        prefNbSemainesToDl.setEntries(entries);
        prefNbSemainesToDl.setEntryValues(entries);
        /// vider le cache
        Preference cache = (Preference)findPreference(PreferencesModele.KEY_CACHE);
        cache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ViderCacheVue.show(getActivity(), new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						PreferencesModele.viderCache();
						Android.quitter();
					}
				});
				return false;
			}
		});
	}
}
