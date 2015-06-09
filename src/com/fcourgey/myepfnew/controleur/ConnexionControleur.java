package com.fcourgey.myepfnew.controleur;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.entite.MyEpfUrl;
import com.fcourgey.myepfnew.outils.Securite;

@SuppressWarnings("deprecation")
public class ConnexionControleur {
	
	private static final int NB_SEC_REQ_TIMEOUT = 20;
	
	public static boolean connecte = false;
	public static boolean enCours = false;
	public static boolean mauvaisId = false;
		
	final private WebView wvCachee;
	final private MainControleur mainControleur;
	final private MainActivite a;
	
	public ConnexionControleur(MainControleur mainControleur, WebView wvCachee) {
		this.wvCachee = wvCachee;
		this.mainControleur = mainControleur;
		this.a = (MainActivite) mainControleur.getActivite();
		connexionMyEPF();
	}
	
	/**
	 * Se connecte à myEPF
	 * +
	 * Lance onMyEPFConnected
	 */
	@JavascriptInterface
	public void connexionMyEPF() {
		enCours = true;
		mainControleur.avancement("Requête login", 55);
		a.runOnUiThread(new Runnable() {
			@JavascriptInterface
			@SuppressLint({ "NewApi", "JavascriptInterface", "SetJavaScriptEnabled" })
			public void run() {
				initWebSettings();
				initCookies();
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				      WebView.setWebContentsDebuggingEnabled(true);
				}
				wvCachee.setWebChromeClient(new WebChromeClient() {
					public boolean onConsoleMessage(ConsoleMessage cm) {
						Log.d("WebChromeClient", cm.message() + " -- ligne "+ cm.lineNumber() + " de "+ cm.sourceId() );
						return true;
					}
					@Override
					public void onProgressChanged(WebView view, int newProgress) {
						super.onProgressChanged(view, newProgress);
						System.out.println("progress "+newProgress);
					}
				});
				Log.e("", "début");
				wvCachee.loadUrl("https://my.epf.fr/uniquesig43523f2eee35a4f2e72f9c80fd936369/uniquesig0/InternalSite/M/default.aspx?__ufps=554362&site_name=portail&secure=1&orig_url=https%3a%2f%2fmy.epf.fr%2f");
				wvCachee.setWebViewClient(new WebViewClient(){

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
				    private int indexRequeteLogin = 0;
				    private int indexRequeteAccueil = 0;
				    private int indexRequeteEdt = 0;
				    @JavascriptInterface
				    public void onPageFinished(WebView wvCachee, String url) {
				    	System.out.println("onPageFinished "+url);
				    	// 1
				    	if(url.contains("InternalSite/M/default.aspx")){
				    		indexRequeteLogin++;
				    		if(indexRequeteLogin == 1){
				    			Log.e("", "show login form");
					    		wvCachee.loadUrl("javascript:__doPostBack('FormLogOnTypeRegularLogOnCommand','FormLogOnType')");
				    		} else if(indexRequeteLogin == 2){
				    			Log.e("", "login");
				    			String identifiant = mainControleur.getIdentifiant();
				    			String mdpEchapé = Securite.decrypt(a.getPrefs().getMdp()).replace("'", "\\'");
				    			String js = "javascript:{";
				    			js += "document.getElementsByName('FormLogOnUsernameTextBox')[0].value='Education\\\\"+identifiant+"';";
				    			js += "document.getElementsByName('FormLogOnPasswordTextBox')[0].value='"+mdpEchapé+"';";
				    			js += "document.getElementsByName('FormLogOnLogOnCommand')[0].click();";
				    			js += "};";
				    			wvCachee.loadUrl(js);
				    		}
				    	}
				    	// 1 bis mauvais id
				    	else if(url.contains("InternalSite/M/LogOn.aspx")){
				    		Log.e("", "mauvais id");
			    			connecte = false;
			    			enCours = false;
			    			mauvaisId = true;
			    			//TODO lien ac controleur
				    	}
				    	// 2
				    	else if(url.equals(MyEpfUrl.ACCUEIL_RESULTAT)){
				    		indexRequeteAccueil++;
				    		Log.e("", "requete "+indexRequeteAccueil+" accueil");
				    		if(indexRequeteAccueil == 1)
				    			wvCachee.loadUrl(MyEpfUrl.EDT_REQUETE);
				    	} 
				    	// 3
				    	else if(url.contains(MyEpfUrl.EDT_RESULTAT)){
				    		indexRequeteEdt++;
				    		Log.e("", "requete "+indexRequeteEdt+" edt");
				    		 if(indexRequeteEdt == 2){
				    			 connecte = true;
				    			 enCours = false;
				    			 mainControleur.onMyEpfConnected();
				    		 }
				    	}
				    }
				});
				a.runOnUiThread(new Runnable() {
					class CompteARebours extends CountDownTimer {
						CompteARebours(long duree, long intervalleActualisation) {
							super(duree, intervalleActualisation);
						}
						public void onTick(long millisUntilFinished) {
							if(mauvaisId)
								this.cancel();
//							System.out.println("tic tac "+millisUntilFinished/1000);
						}
						public void onFinish() {
							if (!connecte) {
								enCours = false;
								mainControleur.onDelaiDAttenteDepassé();
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
}
