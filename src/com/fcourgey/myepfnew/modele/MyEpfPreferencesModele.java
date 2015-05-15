package com.fcourgey.myepfnew.modele;

import com.fcourgey.android.mylib.framework.Activite;
import com.fcourgey.android.mylib.framework.PreferencesModele;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.fragment.SemainesPagerAdapter;

public class MyEpfPreferencesModele extends PreferencesModele {
	
	public final static int TAILLE_MIN_IDENTIFIANT = 3;
	public final static int TAILLE_MIN_MDP = 3;
	
	public static final int NB_SEMAINES_MIN = 1;
	public static final int NB_SEMAINES_MAX = 20;
	
	
	public static String KEY_IDENTIFIANT;
	public static String KEY_MDP;
	public static String KEY_CM;
	public static String KEY_HEURES;
	public static String KEY_NB_SEMAINES_TO_DL;
	public static String KEY_JSON_SEMAINE = "key_json_semaine";
	public static String KEY_C_EXAM;
	public static String KEY_C_TP;
	public static String KEY_C_TD;
	public static String KEY_C_CM;
	public static String KEY_C_AUTRES;
	public static String KEY_C_DEVOIRS;
	public static String FIRST_TIME;
	public static String KEY_PHOTO_X;
	public static String KEY_PHOTO_Y;
	public static String KEY_CACHE;
	public static String KEY_DOIT_VIDER_CACHE_SUITE_AU_BUG_20 = "KEY_DOIT_VIDER_CACHE_SUITE_AU_BUG_20";
	public static String KEY_NOM_PRENOM = "KEY_NOM_PRENOM";
	public static String V14_MDP_HASHE="V14_MDP_HASHE";
	public static String V14A_MDP_HASHE="V14A_MDP_HASHE";
	public static String KEY_EDT_DEJA_TELECHARGE_AU_MOINS_UNE_FOIS = "KEY_EDT_DEJA_TELECHARGE_AU_MOINS_UNE_FOIS";
	
//	private static Activite mere;
	
	/**
	 * doit être créé en premier pour tout initialiser
	 */
	public MyEpfPreferencesModele(Activite mere){
		super(mere);
		KEY_IDENTIFIANT = mere.getResources().getString(R.string.key_login);
		KEY_MDP = mere.getResources().getString(R.string.key_mdp);
		KEY_CM = mere.getResources().getString(R.string.key_cm);
		KEY_HEURES = mere.getResources().getString(R.string.key_heures);
		KEY_NB_SEMAINES_TO_DL = mere.getResources().getString(R.string.key_nb_semaines_to_dl);
		KEY_C_EXAM = mere.getResources().getString(R.string.key_c_exam);
		KEY_C_TP = mere.getResources().getString(R.string.key_c_tp);
		KEY_C_TD = mere.getResources().getString(R.string.key_c_td);
		KEY_C_CM = mere.getResources().getString(R.string.key_c_cm);
		KEY_C_AUTRES = mere.getResources().getString(R.string.key_c_autre);
		KEY_C_DEVOIRS = mere.getResources().getString(R.string.key_c_devoirs);
		KEY_PHOTO_X = mere.getResources().getString(R.string.key_photo_x);
		KEY_PHOTO_Y = mere.getResources().getString(R.string.key_photo_y);
		KEY_CACHE = mere.getResources().getString(R.string.key_cache);
		if(getBoolean(KEY_DOIT_VIDER_CACHE_SUITE_AU_BUG_20, true)){
			viderCache();
			putBoolean(KEY_DOIT_VIDER_CACHE_SUITE_AU_BUG_20, false);
		}
	}
	public MyEpfPreferencesModele() throws Exception{
		super();
	}
	
	// identifiant
	public String getIdentifiant(){
		return pref.getString(KEY_IDENTIFIANT, "");
	}
	public void setLogin(String login){
		pref.edit().putString(KEY_IDENTIFIANT, login).apply();
	}
	// mdp
	public String getMdp(){
		return pref.getString(KEY_MDP, "");
	}
	public void setMdp(String mdp){
		pref.edit().putString(KEY_MDP, mdp).apply();
	}
	// Nombre de semaines à DL
	public int getNbSemainesToDl(){
		int iDefaut = SemainesPagerAdapter.NOMBRE_DE_SEMAINES_MAX_DEFAUT;
		String sDefaut = Integer.toString(iDefaut);
		try{
			return Integer.parseInt(pref.getString(KEY_NB_SEMAINES_TO_DL, sDefaut));
		} catch (Exception e){
			return iDefaut;
		}
	}
	public void setNbSemainesToDl(int i){
		pref.edit().putInt(KEY_NB_SEMAINES_TO_DL, i).apply();
	}
	// CM
	public boolean getCm(){
		return pref.getBoolean(KEY_CM, true);
	}
	public void setCm(boolean activer){
		pref.edit().putBoolean(KEY_CM, activer).apply();
	}
	// json semaine
	public String getJsonSemaine(int indexSemaine){
		return pref.getString(KEY_JSON_SEMAINE+indexSemaine, null);
	}
	public void setJsonSemaine(int indexSemaine, String s){
		pref.edit().putString(KEY_JSON_SEMAINE+indexSemaine, s).apply();
	}
	public void supprimerJsonSemaine(int indexSemaine){
		pref.edit().remove(KEY_JSON_SEMAINE+indexSemaine).commit();
	}
	public void supprimerTousLesJsonSemaine(){
		for(int i=0 ; i<60 ; i++){
			supprimerJsonSemaine(i);
		}
	}
	
	/**
	 * supprime :
	 * - les json semaines
	 * - à venir
	 */
	public static void viderCache(){
		try {
			viderJsonSemaine();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * supprime tous les JSON Semaines existant 
	 */
	public static void viderJsonSemaine(){
		for(int i=NB_SEMAINES_MIN ; i<NB_SEMAINES_MAX ; i++){
			String value = getPrefs().getString(MyEpfPreferencesModele.KEY_JSON_SEMAINE+i,null);
			if (value == null) {
				continue;
			} else {
				getPrefs().edit().remove(MyEpfPreferencesModele.KEY_JSON_SEMAINE+i).commit();
			}
		}
	}
}
