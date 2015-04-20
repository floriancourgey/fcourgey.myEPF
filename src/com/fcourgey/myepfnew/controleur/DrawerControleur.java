package com.fcourgey.myepfnew.controleur;

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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.BulletinFragment;
import com.fcourgey.myepfnew.activite.EdtFragment;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.activite.PreferencesActivite;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;
import com.fcourgey.myepfnew.modele.PreferencesModele;
import com.fcourgey.myepfnew.outils.Android;
import com.fcourgey.myepfnew.vue.DrawerVue;
import com.mikhaellopez.circularimageview.CircularImageView;

public class DrawerControleur {
	
	private static final String TAG = "DrawerControleur";
	
	private MainActivite a;
	
	private DrawerVue vue;
	
	public static String CHEMIN_PHOTO_PROFIL;
	
	public DrawerControleur(MainActivite a, Bundle savedInstanceState) {
		this.a = a;
		CHEMIN_PHOTO_PROFIL = a.getFilesDir()+"/photoprofil.jpg";
		vue = new DrawerVue(this, a.getIdentifiant());
		// affiche photo de profil si existe
		// sinon, le DL sera appelé par onMyEPFConnected
		if(isPhotoProfilDownloaded()){
			afficherPhotoProfil();
		}
		if(savedInstanceState == null){
			onEdtClicked();
		}
	}
	
	/**
	 * au clic sur l'emploi du temps
	 */
	public void onEdtClicked(){
		Fragment newFragment = new EdtFragment();
        FragmentTransaction transaction = a.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
	}
	
	/**
	 * au clic sur le bulletin
	 */
	public void onBulletinClicked(){
		Fragment newFragment = new BulletinFragment();
        FragmentTransaction transaction = a.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
	}

	/**
	 * au clic sur les préférences
	 */
	public void onPreferencesClicked(){
		// init popup
    	AlertDialog.Builder builder = new AlertDialog.Builder(a);
    	builder.setMessage("Les préférences seront prises en compte au prochain redémarrage de l'appli.")
    	       .setTitle("Hey")
    	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   // lancement activity
                   		Intent intent = new Intent(a, PreferencesActivite.class);
                   		a.startActivity(intent);
                   }
               });
        // lancement popup
        builder.create().show();
	}
	/**
	 * au clic sur A propos
	 */
	public void onAProposClicked(){
		// init popup
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
    	builder = new AlertDialog.Builder(a);
		String versionName;
		try {
			versionName = "version "+a.getPackageManager().getPackageInfo(a.getPackageName(), 0).versionName+" ";
		} catch (NameNotFoundException e) {
			versionName = "";
		}
    	builder.setMessage("myEPF "+versionName+"développé par Florian Courgey pour l'EPF École d'ingénieur-e-s.")
    	       .setTitle("A propos")
    	       .setPositiveButton("OK", null);
        // lancement popup
        builder.create().show();
	}
	/**
	 * au clic sur Quitter
	 */
	public void onQuitterClicked(){
		Android.quitter();
	}

	private void fermerDrawer(){
		vue.getLayoutGeneral().closeDrawer(vue.getVue());
	}
	
	/**
	 * ouvre/ferme le drawer au clic sur le bouton menu
	 */
	public void clicSurBoutonMenu() {
		ouvrirFermerDrawer();
	}
	
	/**
	 * Affiche la photo de profil
	 */
	private void afficherPhotoProfil(){
		CircularImageView photoProfil = (CircularImageView)a.findViewById(R.id.photo_profil);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bmp = BitmapFactory.decodeFile(CHEMIN_PHOTO_PROFIL, options);
		// crop du bmp
		int decalage_x = a.getPrefs().getInt(PreferencesModele.KEY_PHOTO_X, 0);
		if(decalage_x < 0)
			decalage_x = Math.abs(decalage_x);
		int decalage_y = a.getPrefs().getInt(PreferencesModele.KEY_PHOTO_Y, 0);
		if(decalage_y < 0)
			decalage_y = Math.abs(decalage_y);
		if(bmp.getWidth() < bmp.getHeight())
			bmp=Bitmap.createBitmap(bmp, decalage_x, decalage_y,bmp.getWidth(), bmp.getWidth());
		else
			bmp=Bitmap.createBitmap(bmp, decalage_x, decalage_y,bmp.getHeight(), bmp.getHeight());
		photoProfil.setImageBitmap(bmp);
		Log.i(TAG, "màj photo de profil ok");
	}
	
	private boolean isPhotoProfilDownloaded(){
		File file = new File(CHEMIN_PHOTO_PROFIL);
		if(file.exists()){
			return true;
		} else {
			return false;
		}
	}

	public void initPhotoProfil(){
		if(isPhotoProfilDownloaded()){
			Log.i(TAG, "photo de profil existante");
			afficherPhotoProfil();
		} else {
			Log.i(TAG, "photo de profil non existante, téléchargement");
			// download photo de profil
			// +
			// afficherPhotoProfil()
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			a.runOnUiThread(new Runnable() {
				public void run() {
					HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
					HttpContext localContext = new BasicHttpContext();
					HttpGet httpGet = new HttpGet(MainActivite.URL_PROFIL);
					String cookies = CookieManager.getInstance().getCookie(MainActivite.URL_MYDATA);
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

							String urlPhoto = MainActivite.URL_MYDATA+urlPhotoRelatif;

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
	 * Listener de la listview des titres 
	 */
	public void onTitresClicked(AdapterView<?> parent, View view,
			int position, long id, ListView lTitres){
		String titre = (String) lTitres.getItemAtPosition(position);
		if(titre.equals(a.getResources().getString(R.string.edt_titre))){
			onEdtClicked();
		} else if(titre.equals(a.getResources().getString(R.string.bulletin_titre))){
			onBulletinClicked();
		} else if(titre.equals(a.getResources().getString(R.string.pref_titre))){
			onPreferencesClicked();
		} else if(titre.equals(a.getResources().getString(R.string.apropos_titre))){
			onAProposClicked();
		} else if(titre.equals(a.getResources().getString(R.string.quitter_titre))){
			onQuitterClicked();
		}
		fermerDrawer();
	}

	/**
	 * Ferme le drawer si ouvert
	 * Ouvre le drawer si fermé
	 */
	public void ouvrirFermerDrawer() {
		if (!vue.getLayoutGeneral().isDrawerOpen(vue.getVue())) {
			vue.getLayoutGeneral().openDrawer(vue.getVue());
        } else {
        	vue.getLayoutGeneral().closeDrawer(vue.getVue());
        }
	}
	
	/**
	 * ?
	 */
	@SuppressWarnings("deprecation")
	public void onPostCreate(Bundle savedInstanceState) {
		// Sync the toggle state after onRestoreInstanceState has occurred.
		vue.getToggleBouton().syncState();
	}

	/**
	 * ? 
	 */
	@SuppressWarnings("deprecation")
	public void onConfigurationChanged(Configuration newConfig) {
		vue.getToggleBouton().onConfigurationChanged(newConfig);
	}

	public DrawerVue getVue() {
		return vue;
	}

	public MainActivite getActivite() {
		return a;
	}
}
