package com.fcourgey.myepfnew.controleur;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.AsyncFragmentControleur;
import com.fcourgey.android.mylib.framework.AsyncFragmentVue;
import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.fragment.SemainesPagerAdapter;

public class EdtControleur extends AsyncFragmentControleur {
	
	public static final String TAG = "EdtFragment";

	public static boolean telechargementEdtEnCours = false;
	
	@SuppressWarnings("unused")
	private static WebView wvCachee;
	
	private SemainesPagerAdapter semainesPagerAdapter;

	public EdtControleur(Fragment f, LayoutInflater inflater, ViewGroup container) {
		super(f, inflater, container);
		
		vue = new AsyncFragmentVue(this, inflater, container, R.layout.edt_fragment);
		
		ViewPager viewPager = (ViewPager) vue.getVue().findViewById(R.id.accueil_pager);
		semainesPagerAdapter = new SemainesPagerAdapter(getFragment().getChildFragmentManager(), (MainActivite)a);
        viewPager.setOffscreenPageLimit(SemainesPagerAdapter.NOMBRE_DE_SEMAINES_MAX+1);
        viewPager.setAdapter(semainesPagerAdapter);
        
        if(vueErreurChargee){
        	chargerVueErreur(derniereErreurTitre, derniereErreurMessage);
		}else if(MainActivite.edtDejaTelechargeUneFois || SemaineControleur.premiereSemaineTelechargee){
        	chargerVueComplete();
        } else {
        	chargerVueDefaut("Connexion à my.epf.fr en cours");
        }
	}
	
	
	/**
	 * Affiche l'avancement sur la progressBar et son textview correspondant
	 * Fait un print log également
	 */
	public void avancement(final String texte, final int pourcentage){
		if(semainesPagerAdapter != null){
			semainesPagerAdapter.avancement(texte, pourcentage);
		}
	}
	
	/**
	 * retourne true si internet ok
	 * @return true/false
	 */
	@SuppressWarnings("unused")
	private boolean isInternetOk(){
		ConnectivityManager manager = (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo telecom = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifi != null && wifi.isConnectedOrConnecting()) {
			return true;
		}
		if (telecom != null && telecom.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	
	
	public static boolean isTelechargementEnCours(){
		return telechargementEdtEnCours;
	}

	public static boolean isTelechargementEdtEnCours() {
		return telechargementEdtEnCours;
	}

	public static void setTelechargementEdtEnCours(boolean pTelechargementEdtEnCours) {
		telechargementEdtEnCours = pTelechargementEdtEnCours;
	}

	/**
	 * cache les semaines
	 * et
	 * affiche le layout d'erreur
	 * 
	 * sauf si l'edt a déjà été téléchargé
	 */
	public void onDelaiDAttenteDepassé() {
		if(MainActivite.edtDejaTelechargeUneFois){
			return;
		}
		chargerVueErreur("délai d'attente dépassé", "Impossible de se connecter à my.epf.fr\nCheck tes identifiants dans les préférences\net redémarre l'appli");
	}


	public void onMyEPFConnected() {
		chargerVueComplete();
	}
	
	public void chargerVueDefaut(String texte) {
		super.chargerVueDefaut();
		((TextView)vue.getVue().findViewById(R.id.tvTitre)).setText(texte);
	}
}
