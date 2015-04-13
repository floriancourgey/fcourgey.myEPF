package com.fcourgey.myepfnew.activite;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.fcourgey.myepfnew.R;

public class EdtFragment extends Fragment {
	
	private static MainActivite a;
	
	public static final String TAG = "EdtFragment";

	public static final String URL_MY_EPF = "https://my.epf.fr/";
	public static final String URL_LOGIN_REQUETE = URL_MY_EPF;
	public static final String URL_LOGIN_RESULTAT = "InternalSite/Login.asp";
	public static final String URL_ACCUEIL_RESULTAT = URL_MY_EPF+"default.aspx";
	public static final String URL_EDT_REQUETE = URL_MY_EPF+"_layouts/crypt/generer_cle.aspx?service=planning";
	
	public static final String URL_MYDATA = "https://mydata.epf.fr/";
	public static final String URL_EDT_RESULTAT = URL_MYDATA;
	
	
	public static final int NB_SEC_REQ_TIMEOUT = 20;
	
	public static boolean telechargementEdtEnCours = false;
	
	@SuppressWarnings("unused")
	private static WebView wvCachee;
	
	private static View vue;
	
	private SemainesPagerAdapter semainesPagerAdapter;
	
	
	/**
	 * onCreate
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		a = (MainActivite)getActivity();
		
		vue=inflater.inflate(R.layout.semaines_activite, container, false);
		
		ViewPager viewPager = (ViewPager) vue.findViewById(R.id.accueil_pager);
		semainesPagerAdapter = new SemainesPagerAdapter(getChildFragmentManager(), a);
        viewPager.setOffscreenPageLimit(SemainesPagerAdapter.NOMBRE_DE_SEMAINES_MAX+1);
        viewPager.setAdapter(semainesPagerAdapter);
        
		return vue;
	}
	
	/**
	 * Affiche l'avancement sur la progressBar et son textview correspondant
	 * Fait un system.out.println également
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

	public static void setTelechargementEdtEnCours(boolean telechargementEdtEnCours) {
		EdtFragment.telechargementEdtEnCours = telechargementEdtEnCours;
	}

}
