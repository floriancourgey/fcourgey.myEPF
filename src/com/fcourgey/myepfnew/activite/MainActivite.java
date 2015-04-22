package com.fcourgey.myepfnew.activite;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.controleur.DrawerControleur;
import com.fcourgey.myepfnew.framework.Activite;
import com.fcourgey.myepfnew.outils.Securite;

@SuppressWarnings("deprecation")
public class MainActivite extends Activite {
	
	private static final String TAG = "MainActivite";
	
	private DrawerControleur drawer;
	
	public static final String URL_MYDATA = EdtFragment.URL_MYDATA;
	public static final String URL_PROFIL = URL_MYDATA+"pegasus/index.php?com=tracking&job=tracking-etudiant";

	public static boolean connecteAMyEpf = false;
	public static boolean enTrainDeSeConnecterAMyEPF = false;
		
	private static String identifiant;
	private static String mdp;
	
	private static WebView wvCachee;
	
	public static boolean serverOk;
	
	private ProgressBar pbConnexionMyEpf;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_activite);
		
		pbConnexionMyEpf = (ProgressBar)findViewById(R.id.pbConnexionMyEpf);
		pbConnexionMyEpf.getProgressDrawable().setColorFilter(Color.CYAN, Mode.SRC_IN);
		
		identifiant = getPrefs().getIdentifiant();
		try {
			mdp = Securite.decrypt(getPrefs().getMdp());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// connexion à myEPF si pas fait et pas en cours
		if(!connecteAMyEpf && !enTrainDeSeConnecterAMyEPF){
			Log.i(TAG, "Connexion à myEPF");
			connexionMyEPF();
		}
		
		drawer = new DrawerControleur(this,savedInstanceState);
	}
	
	/**
	 * Se connecte à myEPF
	 * +
	 * Lance onMyEPFConnected
	 */
	public void connexionMyEPF() {
		enTrainDeSeConnecterAMyEPF = true;
		avancement("Requête login", 5);
		wvCachee = (WebView)findViewById(R.id.wvCachee);
		runOnUiThread(new Runnable() {
			@SuppressLint("NewApi")
			public void run() {
				initWebSettings();
				initCookies();
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				      WebView.setWebContentsDebuggingEnabled(true);
				}
				MainActivite.wvCachee.setWebChromeClient(new WebChromeClient());
				MainActivite.wvCachee.loadUrl(EdtFragment.URL_LOGIN_REQUETE);
				MainActivite.wvCachee.setWebViewClient(new WebViewClient(){
				    private boolean ignorerRequetesAccueil = false;
				    
				    private boolean premiereRequeteEdtResultat = true;
				    private boolean secondeRequeteEdtResultat = false;

				    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				        handler.proceed();
				    }

				    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				        super.onReceivedError(view, errorCode, description, failingUrl);
				    }

				    public boolean shouldOverrideUrlLoading(WebView view, String url) {
				        view.loadUrl(url);
				        return true;
				    }

				    public void onPageFinished(WebView wvCachee, String url) {
				    	// 1
				    	if(url.contains(EdtFragment.URL_LOGIN_RESULTAT)){
				    		String js = "javascript:";
				    		js+="document.getElementById('user_name').value='Education\\\\"+identifiant+"';";
				    		js+="document.getElementById('password').value='"+mdp+"';";
				    		js+="document.getElementById('form1').submit();";
				    		wvCachee.loadUrl(js);
				    		avancement("Requête accueil", 35);
				    	} 
				    	// 1 bis -> mauvais identifiants
				    	// 2
				    	else if(url.equals(EdtFragment.URL_ACCUEIL_RESULTAT)){
				    		// on est connectés, on lance l'edt
				    		if(!ignorerRequetesAccueil){
				    			ignorerRequetesAccueil = true;
				    			wvCachee.loadUrl(EdtFragment.URL_EDT_REQUETE);
				    			avancement("Requête init edt 1", 50);
				    		}
				    	} 
				    	// 3
				    	else if(url.contains(EdtFragment.URL_EDT_RESULTAT)){
				    		if(premiereRequeteEdtResultat){
				    			avancement("Requête init edt 2", 65);
				    			premiereRequeteEdtResultat = false;
				    			secondeRequeteEdtResultat = true;
				    		} else if(secondeRequeteEdtResultat){
				    			secondeRequeteEdtResultat = false;
				    			onMyEPFConnected();
				    		}
				    		
				    	}
				    }
				});
				MainActivite.this.runOnUiThread(new Runnable() {
					class CompteARebours extends CountDownTimer {
						CompteARebours(long duree, long intervalleActualisation) {
							super(duree, intervalleActualisation);
						}
						public void onTick(long millisUntilFinished) {}
						public void onFinish() {
							if (!serverOk) {
								enTrainDeSeConnecterAMyEPF = false;
								avancement("Délai d'attente dépassé", 0);
							}
						}
					}
					public void run() {
						new CompteARebours(EdtFragment.NB_SEC_REQ_TIMEOUT*1000, 300).start();
					}
				});
			}
		});
	}
	
	private void avancement(final String texte, final int pourcentage) {
		try{
			runOnUiThread(new Runnable() {
				public void run() {
					if(pourcentage >= 100){
						Log.i(TAG, "Avancement terminé : "+texte);
						MainActivite.this.pbConnexionMyEpf.setVisibility(View.GONE);
						
					} else if(pourcentage > 0){
						Log.i(TAG, "Avancement "+pourcentage+" : "+texte);
						MainActivite.this.pbConnexionMyEpf.setProgress(pourcentage);
						MainActivite.this.pbConnexionMyEpf.setVisibility(View.VISIBLE);
						
					} else {
						Log.i(TAG, "Avancement erreur : "+texte);
						EdtFragment.setTelechargementEdtEnCours(false);
						MainActivite.this.pbConnexionMyEpf.setVisibility(View.GONE);
					}

				}
			});
		} catch (Exception e){
			e.printStackTrace();
			Log.w(TAG, "Erreur inconnue dans avancement..");
		}
	}

	/**
	 * Est exécuté lorsque la connexion à myEPF a réussi
	 */
	private void onMyEPFConnected(){
		avancement("my.epf connecté", 100);
		enTrainDeSeConnecterAMyEPF = false;
		connecteAMyEpf = true;
		serverOk = true;
		drawer.onMyEPFConnected();
	}	
	
	/**
	 * initialise les cookies
	 */
	private void initCookies(){
		CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(wvCachee.getContext());
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
		WebSettings webSettings = wvCachee.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:35.0) Gecko/20100101 Firefox/35.0 Waterfox/35.0");
	}
	
	/**
	 * pour le drawer
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    drawer.onPostCreate(savedInstanceState);
	}
	/**
	 * pour le drawer
	 */
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawer.onConfigurationChanged(newConfig);
    }

	/**
	 * Menu et drawer
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawer.getVue().getToggleBouton().onOptionsItemSelected(item)) {
	      return true;
	    }
		if (item.getItemId() == android.R.id.home) {
			drawer.ouvrirFermerDrawer();
		}
		
	    switch (item.getItemId()) {
		    case R.id.edt:
	            drawer.onEdtClicked();
	            return true;
		    case R.id.bulletin:
	            drawer.onBulletinClicked();
	            return true;
	        case R.id.preferences:
	            drawer.onPreferencesClicked();
	            return true;
	        case R.id.apropos:
	        	drawer.onAProposClicked();
	            return true;
	        case R.id.quitter:
	        	drawer.onQuitterClicked();
	            return true;
	        case android.R.id.home:
	        	drawer.ouvrirFermerDrawer();
	        	return true;
	        default:
	        	return super.onOptionsItemSelected(item);
	    }
	}

	/**
	 * au clic sur le bouton menu du téléphone => drawer
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	drawer.clicSurBoutonMenu();
	        return true;
	    }
	    return super.onKeyDown(keyCode, e);
	}
	
	
	public String getLogin() {
		return identifiant;
	}
	public String getIdentifiant() {
		return identifiant;
	}
}
