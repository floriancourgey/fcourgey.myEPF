package com.fcourgey.myepfnew.controleur;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.fcourgey.android.mylib.framework.ActiviteControleur;
import com.fcourgey.myepfnew.activite.AccueilActivite;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
import com.fcourgey.myepfnew.vue.IdentifiantsVue;

public class AccueilControleur extends ActiviteControleur {
	
	@SuppressWarnings("unused")
	private static final String TAG = "AccueilControleur";
	
	/**
	 * Check si les identifiants existent :
	 * - si oui,
	 * on lance l'appli
	 * 
	 * - si non,
	 * on les demande puis on lance l'appli
	 */
	@SuppressLint("InflateParams")
	public AccueilControleur(final AccueilActivite activite, Bundle savedInstanceState){
		super(activite, savedInstanceState);
		// locale
		Locale.setDefault(Locale.FRANCE);
			
		// si c'est la première fois qu'on lance la version 14a
		// on supprime le login+mdp pour éviter toute erreur de hash
		try {
			PackageInfo pinfo = activite.getPackageManager().getPackageInfo(activite.getPackageName(), 0);
			int versionCode = pinfo.versionCode;
			if(versionCode == 14002 && activite.getPrefs().getBoolean(MyEpfPreferencesModele.V14A_MDP_HASHE, false)==false){
				activite.getPrefs().setLogin("");
				activite.getPrefs().setMdp("");
				if(activite.getPrefs().getMdp().equals("")){
					activite.getPrefs().putBoolean(MyEpfPreferencesModele.V14A_MDP_HASHE, true);
				}
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		// on lit le login
		String login = activite.getPrefs().getIdentifiant();
		// et le mdp
		String mdp = activite.getPrefs().getMdp();
		// si pas de login, on demande avec une popup
		if(login == null || login.length() < MyEpfPreferencesModele.TAILLE_MIN_IDENTIFIANT || mdp == null || mdp.length()<MyEpfPreferencesModele.TAILLE_MIN_MDP){
			IdentifiantsVue.show(activite);
		} 
		// sinon lancer appli
		else {
			activite.lancerSemainesActivite();
		}
	}
}
