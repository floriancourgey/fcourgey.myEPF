package com.fcourgey.myepfnew.activite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.controlleur.DrawerControleur;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;
import com.fcourgey.myepfnew.modele.PreferencesModele;
import com.fcourgey.myepfnew.outils.Securite;
import com.mikhaellopez.circularimageview.CircularImageView;

@SuppressWarnings("deprecation")
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
	
	public static boolean connecteAMyEpf = false;
	public static boolean enTrainDeSeConnecterAMyEPF = false;
	
	private static boolean telechargementEdtEnCours = false;
	
	private static WebView wvCachee;
	
	private static boolean serverOk;
	
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
		semainesPagerAdapter = new SemainesPagerAdapter(getActivity().getSupportFragmentManager(), a);
        viewPager.setOffscreenPageLimit(SemainesPagerAdapter.NOMBRE_DE_SEMAINES_MAX+1);
        viewPager.setAdapter(semainesPagerAdapter);
        
		return vue;
	}
	
	/**
	 * Est exécuté lorsque la connexion à myEPF a réussi
	 */
	private void onMyEPFConnected(){
		enTrainDeSeConnecterAMyEPF = false;
		connecteAMyEpf = true;
//		initPhotoEtNomProfil();
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
	 * initialise les cookies
	 */
	private void initCookies(){
		CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(EdtFragment.wvCachee.getContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		cookieManager.setAcceptCookie(true);
		cookieSyncManager.sync();
	}

	/**
	 * initalise la webview
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebSettings(){
		WebSettings webSettings = EdtFragment.wvCachee.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:35.0) Gecko/20100101 Firefox/35.0 Waterfox/35.0");
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

	public static boolean isServerOk() {
		return serverOk;
	}

	public static void setServerOk(boolean b){
		serverOk = b;
	}

	

	public static boolean isConnecteAMyEpf() {
		return connecteAMyEpf;
	}

	public static void setConnecteAMyEpf(boolean connecteAMyEpf) {
		EdtFragment.connecteAMyEpf = connecteAMyEpf;
	}

	public static boolean isTelechargementEdtEnCours() {
		return telechargementEdtEnCours;
	}

	public static void setTelechargementEdtEnCours(boolean telechargementEdtEnCours) {
		EdtFragment.telechargementEdtEnCours = telechargementEdtEnCours;
	}

}
