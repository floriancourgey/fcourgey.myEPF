package com.fcourgey.myepfnew.controlleur;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.fcourgey.myepfnew.activite.AccueilActivite;
import com.fcourgey.myepfnew.modele.PreferencesModele;
import com.fcourgey.myepfnew.outils.Securite;
import com.fcourgey.myepfnew.vue.IdentifiantsVue;

public class AccueilControleur {
	
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
	public AccueilControleur(final AccueilActivite activite){
		// locale
		Locale.setDefault(Locale.FRANCE);
		
		// si c'est la première fois qu'on lance la versionName 14 / versionCode 14000
		// on écrit le login hashé dans les pref
		try {
			PackageInfo pinfo = activite.getPackageManager().getPackageInfo(activite.getPackageName(), 0);
			int versionCode = pinfo.versionCode;
			if(versionCode == 14000 && activite.getPrefs().getBoolean(PreferencesModele.V14_MDP_HASHE, false)==false){
				String mdp = activite.getPrefs().getMdp();
				System.out.println("Mdp avant hash : " + mdp);
				try {
					String mdpHashe = Securite.encrypt(mdp);
					System.out.println("Mdp apres hash: " + mdpHashe);
					activite.getPrefs().setMdp(mdpHashe);
					String mdpVerif = activite.getPrefs().getMdp();
					if(mdpVerif.equals(mdpHashe)){
						activite.getPrefs().putBoolean(PreferencesModele.V14_MDP_HASHE, true);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		Log.i(TAG, login+" et "+mdp);
		// si pas de login, on demande avec une popup
		if(login == null || login.length() < PreferencesModele.TAILLE_MIN_IDENTIFIANT || mdp == null || mdp.length()<PreferencesModele.TAILLE_MIN_MDP){
			IdentifiantsVue.show(activite);
		} 
		// sinon lancer appli
		else {
			activite.lancerSemainesActivite();
		}
	}
}
