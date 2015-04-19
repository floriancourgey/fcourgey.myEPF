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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
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
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;
import com.fcourgey.myepfnew.modele.PreferencesModele;
import com.fcourgey.myepfnew.outils.Securite;
import com.mikhaellopez.circularimageview.CircularImageView;

@SuppressWarnings("deprecation")
public class MainActivite extends _MereActivite {
	
	private static final String TAG = "MainActivite";
	
	private DrawerControleur drawer;
	
	public static final String URL_MYDATA = EdtFragment.URL_MYDATA;
	public static final String URL_PROFIL = URL_MYDATA+"pegasus/index.php?com=tracking&job=tracking-etudiant";

	public static boolean connecteAMyEpf = false;
	public static boolean enTrainDeSeConnecterAMyEPF = false;
		
	public static String CHEMIN_PHOTO_PROFIL;
	
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
		
		CHEMIN_PHOTO_PROFIL = getFilesDir()+"/photoprofil.jpg";
		
		identifiant = getPrefs().getIdentifiant();
		try {
			mdp = Securite.decrypt(getPrefs().getMdp());
		} catch (Exception e) {
			e.printStackTrace();
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
		initPhotoEtNomProfil();
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

	private void afficherPhotoProfil(){
		CircularImageView photoProfil = (CircularImageView)findViewById(R.id.photo_profil);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bmp = BitmapFactory.decodeFile(CHEMIN_PHOTO_PROFIL, options);
		// crop du bmp
		int decalage_x = getPrefs().getInt(PreferencesModele.KEY_PHOTO_X, 0);
		if(decalage_x < 0)
			decalage_x = Math.abs(decalage_x);
		int decalage_y = getPrefs().getInt(PreferencesModele.KEY_PHOTO_Y, 0);
		if(decalage_y < 0)
			decalage_y = Math.abs(decalage_y);
		if(bmp.getWidth() < bmp.getHeight())
			bmp=Bitmap.createBitmap(bmp, decalage_x, decalage_y,bmp.getWidth(), bmp.getWidth());
		else
			bmp=Bitmap.createBitmap(bmp, decalage_x, decalage_y,bmp.getHeight(), bmp.getHeight());
		photoProfil.setImageBitmap(bmp);
		Log.i(TAG, "màj photo de profil ok");
	}

	private void initPhotoEtNomProfil(){
		// check si existe
		File file = new File(CHEMIN_PHOTO_PROFIL);
		if(file.exists()){
			Log.i(TAG, "photo de profil existante");
			afficherPhotoProfil();
		} else {
			Log.i(TAG, "photo de profil non existante, téléchargement");
			// download photo de profil
			// +
			// afficherPhotoProfil()
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			runOnUiThread(new Runnable() {
				public void run() {
					HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
					HttpContext localContext = new BasicHttpContext();
					HttpGet httpGet = new HttpGet(URL_PROFIL);
					String cookies = CookieManager.getInstance().getCookie(URL_MYDATA);
					httpGet.setHeader(SM.COOKIE, cookies);
					InputStream is = null;
					try {
						is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Impossible de joindre le serveur EPF (étonnant à ce stade)");
					} 
					try {
						String line;
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						while ((line = br.readLine()) != null) {
							if(line.contains("id=\"photo\"")){
								break;
							}
						}
						br.close();

						String regex = "id=\"photo\" src=\"([/\\w-]*.jpg)\"";
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher;
						try{
							matcher = pattern.matcher(line);
						}catch(Exception e){
							e.printStackTrace();
							return;
						}

						if(matcher.find()){
							String urlPhotoRelatif = matcher.group(0);
							urlPhotoRelatif = urlPhotoRelatif.split("\"")[3];

							System.out.println(urlPhotoRelatif);

							String urlPhoto = URL_MYDATA+urlPhotoRelatif;

							httpGet = new HttpGet(urlPhoto);
							httpGet.setHeader(SM.COOKIE, cookies);
							try {
								is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
								FileOutputStream fos = new FileOutputStream(new File(CHEMIN_PHOTO_PROFIL));
								int inByte;
								while((inByte = is.read()) != -1) fos.write(inByte);
								is.close();
								fos.close();
								Log.i(TAG, "photo de profil non existante, téléchargement OK");
								afficherPhotoProfil();
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println("Impossible de joindre le serveur EPF (2) (étonnant à ce stade)");
							}
						}
					} catch(IOException e){
						e.printStackTrace();
						System.out.println("Impossible de lire la réponse finale");
					}
				}
			});
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
