package com.fcourgey.myepfnew.activite;

<<<<<<< HEAD
import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
=======
>>>>>>> origin/new-archi
import android.content.res.Configuration;
import android.os.Bundle;
<<<<<<< HEAD
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
=======
import android.view.KeyEvent;
import android.view.MenuItem;
>>>>>>> origin/new-archi

import com.fcourgey.android.mylib.framework.Activite;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.controleur.MainControleur;
import com.fcourgey.myepfnew.entite.Module;
import com.fcourgey.myepfnew.entite.MyEpfUrl;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
<<<<<<< HEAD
import com.fcourgey.myepfnew.outils.Securite;
import com.fcourgey.myepfnew.outils.XmlNoteMyEpf;
import com.fcourgey.myepfnew.vue.DrawerVue;
=======
import com.fcourgey.myepfnew.vue.MainVue;
>>>>>>> origin/new-archi

public class MainActivite extends Activite {
	
<<<<<<< HEAD
	private static final String TAG = "MainActivite";
	
	protected MainControleur controleur;
	
	public static final int NB_SEC_REQ_TIMEOUT = 15;

	public static boolean connecteAMyEpf = false;
	public static boolean enTrainDeSeConnecterAMyEPF = false;
		
	private static String identifiant;
	private static String mdp;
	
	// composant
	@InjectView(R.id.pbConnexionMyEpf)
    protected ProgressBar pbConnexionMyEpf;
	@InjectView(R.id.wvCachee)
	protected WebView wvCachee;
	
	public static boolean edtDejaTelechargeUneFois = false;
	
//	private ProgressBar pbConnexionMyEpf;
	
	private MyEpfPreferencesModele prefs;
=======
>>>>>>> origin/new-archi
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activite);
<<<<<<< HEAD
		
		prefs = AccueilActivite.prefs;
		if(prefs == null){
			prefs = new MyEpfPreferencesModele(this);
		}
		
		ButterKnife.inject(this);
		
		edtDejaTelechargeUneFois = prefs.getBoolean(MyEpfPreferencesModele.KEY_EDT_DEJA_TELECHARGE_AU_MOINS_UNE_FOIS, false);
		
		identifiant = prefs.getIdentifiant();
		try {
			mdp = Securite.decrypt(prefs.getMdp());
		} catch (Exception e) {
			e.printStackTrace(); // TODO
		}
		
		controleur = new MainControleur(this, savedInstanceState);

		pbConnexionMyEpf.getProgressDrawable().setColorFilter(Color.CYAN, Mode.SRC_IN);

		// connexion à myEPF si pas fait et pas en cours
		if(!connecteAMyEpf && !enTrainDeSeConnecterAMyEPF){
			avancement("Connexion à myEPF", 55);
			connexionMyEPF();
		}
	}
	
	/**
	 * Se connecte à myEPF
	 * +
	 * Lance onMyEPFConnected
	 */
	@JavascriptInterface
	public void connexionMyEPF() {
		enTrainDeSeConnecterAMyEPF = true;
		avancement("Requête login", 55);
//		wvCachee = (WebView)findViewById(R.id.wvCachee);
		runOnUiThread(new Runnable() {
			@JavascriptInterface
			@SuppressLint({ "NewApi", "JavascriptInterface", "SetJavaScriptEnabled" })
			public void run() {
				initWebSettings();
				initCookies();
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				      WebView.setWebContentsDebuggingEnabled(true);
				}
				wvCachee.setWebChromeClient(new WebChromeClient());
				wvCachee.loadUrl(MyEpfUrl.LOGIN_REQUETE);
				wvCachee.setWebViewClient(new WebViewClient(){
					
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

				    @JavascriptInterface
				    public void onPageFinished(WebView wvCachee, String url) {
				    	// 1
				    	if(url.contains(MyEpfUrl.LOGIN_RESULTAT)){
				    		String js = "javascript:";
				    		js+="document.getElementById('user_name').value='Education\\\\"+identifiant+"';";
				    		String mdpEchapé = mdp.replace("'", "\\'");
				    		js+="document.getElementById('password').value='"+mdpEchapé+"';";
				    		js+="document.getElementById('form1').submit();";
				    		wvCachee.loadUrl(js);
				    		avancement("Requête accueil", 55);
				    	}
				    	// 1 bis -> mauvais identifiants
				    	// 2
				    	else if(url.equals(MyEpfUrl.ACCUEIL_RESULTAT)){
				    		// on est connectés, on lance l'edt
				    		if(!ignorerRequetesAccueil){
				    			ignorerRequetesAccueil = true;
				    			wvCachee.loadUrl(MyEpfUrl.EDT_REQUETE);
				    			avancement("Requête init edt 1", 55);
				    		}
				    	} 
				    	// 3
				    	else if(url.contains(MyEpfUrl.EDT_RESULTAT)){
				    		if(premiereRequeteEdtResultat){
				    			avancement("Requête init edt 2", 55);
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
						public void onTick(long millisUntilFinished) {
//							System.out.println("tic tac "+millisUntilFinished/1000);
						}
						public void onFinish() {
							if (!connecteAMyEpf) {
								onDelaiDAttenteDepassé();
							}
						}
					}
					public void run() {
						new CompteARebours(NB_SEC_REQ_TIMEOUT*1000, 250).start();
					}
				});
			}
		});
	}
	
	private void onDelaiDAttenteDepassé(){
		enTrainDeSeConnecterAMyEPF = false;
		avancement("Délai d'attente dépassé", 0);
		controleur.onDelaiDAttenteDepassé();
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
		avancement("onMyEPFConnected", 100);
		enTrainDeSeConnecterAMyEPF = false;
		connecteAMyEpf = true;
		controleur.onMyEpfConnected();
=======
		controleur = new MainControleur(this, savedInstanceState);
	}
	
	/**
	 * au clic sur le bouton menu du téléphone => drawer
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	((MainControleur)controleur).clicSurBoutonMenu();
	        return true;
	    }
	    return super.onKeyDown(keyCode, e);
>>>>>>> origin/new-archi
	}
	
	public String getIdentifiant(){
		return ((MainControleur)controleur).getIdentifiant();
	}
	@Override
	public MyEpfPreferencesModele getPrefs() {
		return ((MainControleur)controleur).getPrefs();
	}
	
	/**
	 * pour le drawer
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    if(controleur != null)
<<<<<<< HEAD
	    	controleur.onPostCreate(savedInstanceState);
=======
	    	((MainControleur) controleur).onPostCreate(savedInstanceState);
>>>>>>> origin/new-archi
	}
	/**
	 * pour le drawer
	 */
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(controleur != null)
<<<<<<< HEAD
        	controleur.onConfigurationChanged(newConfig);
=======
        	((MainControleur) controleur).onConfigurationChanged(newConfig);
>>>>>>> origin/new-archi
    }
	/**
	 * Menu et drawer
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
<<<<<<< HEAD
		if (((DrawerVue)controleur.getVue()).getToggleBouton().onOptionsItemSelected(item)) {
=======
		if (((MainVue)controleur.getVue()).getToggleBouton().onOptionsItemSelected(item)) {
>>>>>>> origin/new-archi
	      return true;
	    }
		MainControleur controleur = (MainControleur)this.controleur;
		if (item.getItemId() == android.R.id.home) {
			controleur.ouvrirFermerDrawer();
		}
		
	    switch (item.getItemId()) {
		    case R.id.edt:
	            controleur.onEdtClicked();
	            return true;
		    case R.id.bulletin:
	            controleur.onBulletinClicked();
	            return true;
	        case R.id.preferences:
	            controleur.onPreferencesClicked();
	            return true;
	        case R.id.apropos:
	        	controleur.onAProposClicked();
	            return true;
	        case R.id.quitter:
	        	controleur.onQuitterClicked();
	            return true;
	        case android.R.id.home:
	        	controleur.ouvrirFermerDrawer();
	        	return true;
	        default:
	        	return super.onOptionsItemSelected(item);
	    }
	}
<<<<<<< HEAD

	/**
	 * au clic sur le bouton menu du téléphone => drawer
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	controleur.clicSurBoutonMenu();
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
	public WebView getWvCachee(){
		return wvCachee;
	}

	@Override
	public MyEpfPreferencesModele getPrefs() {
		return prefs;
	}
=======
>>>>>>> origin/new-archi
}
