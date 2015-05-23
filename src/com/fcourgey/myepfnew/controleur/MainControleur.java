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
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.fcourgey.android.mylib.framework.ActiviteControleur;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.activite.PreferencesActivite;
import com.fcourgey.myepfnew.entite.MyEpfUrl;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;
import com.fcourgey.myepfnew.fragment.AProposFragment;
import com.fcourgey.myepfnew.fragment.BulletinFragment;
import com.fcourgey.myepfnew.fragment.EdtFragment;
import com.fcourgey.myepfnew.fragment.NotesFragment;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
import com.fcourgey.myepfnew.outils.Android;
import com.fcourgey.myepfnew.vue.DrawerVue;
import com.mikhaellopez.circularimageview.CircularImageView;

@SuppressWarnings("deprecation")
public class MainControleur extends ActiviteControleur {
	
	private static final String TAG = "MainControleur";
	
//	private DrawerVue vue;
	
	private MainActivite a;
	
	private Fragment fragmentActuel;
	
	private String identifiant;
	
	// design
	@InjectView(R.id.photo_profil)
	protected CircularImageView photo_profil;
	
	public static String CHEMIN_PHOTO_PROFIL = "{FILES_DIR}/photo-profil-{IDENTIFIANT}.jpg";
	
	private static final String REGEX_PHOTO = "id=\"photo\" src=\"([/\\w-]*.jpg)\"";
	private static final String STOP_PHOTO = "id=\"photo\"";
	private static final String REGEX_NOM = "<div class=\"user-name font2\">[\\w]* (.*) \\(.*\\) </div> -->";
	private static final String STOP_NOM = "font2";
	
	public MainControleur(MainActivite a, Bundle savedInstanceState) {
		super(a, savedInstanceState);
		this.a = a;
		identifiant = a.getIdentifiant();
		CHEMIN_PHOTO_PROFIL = CHEMIN_PHOTO_PROFIL.replace("{FILES_DIR}", a.getFilesDir().toString());
		CHEMIN_PHOTO_PROFIL = CHEMIN_PHOTO_PROFIL.replace("{IDENTIFIANT}",identifiant);
		
		ButterKnife.inject(this, a);
		
		vue = new DrawerVue(this);

		// affiche photo de profil si existe
		// sinon, le DL sera appelé par onMyEPFConnected
		if(isPhotoProfilDownloaded()){
			afficherPhotoProfil();
		}
		// affiche le nom prénom si existe
		if(isNomPrenomDownloaded()){
			afficherNomPrenom();
		}
		// lance le fragment EDT si l'appli vient de se lancer
		if(savedInstanceState == null){
			onEdtClicked();
		}
	}
	
	
	
	/**
	 * quand le délai d'attente est dépassé
	 * et qu'on est pas connecté à my.epf
	 */
	public void onDelaiDAttenteDepassé() {
		if(fragmentActuel instanceof EdtFragment){
			EdtFragment f = (EdtFragment)fragmentActuel;
			f.onDelaiDAttenteDepassé();
		} else if(fragmentActuel instanceof BulletinFragment) {
			BulletinFragment f = (BulletinFragment)fragmentActuel;
			f.onDelaiDAttenteDepassé();
		}
	}
	
	/**
	 * au clic sur l'emploi du temps
	 */
	public void onEdtClicked(){
		lancerFragment(new EdtFragment());
	}
	
	/**
	 * au clic sur le bulletin
	 */
	public void onBulletinClicked(){
		lancerFragment(new BulletinFragment());
	}
	
	/**
	 * au clic sur les notes
	 */
	public void onNotesClicked(){
		lancerFragment(new NotesFragment());
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
		Fragment newFragment = new AProposFragment();
		fragmentActuel = newFragment;
        FragmentTransaction transaction = a.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
	}
	
	/**
	 * au clic sur Quitter
	 */
	public void onQuitterClicked(){
		Android.quitter();
	}
	
	private void lancerFragment(Fragment newFragment){
		fragmentActuel = newFragment;
        FragmentTransaction transaction = a.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
	}

	private void fermerDrawer(){
		DrawerVue drawerVue = (DrawerVue)vue;
		drawerVue.getLayoutGeneral().closeDrawer(drawerVue.getVue());
	}
	
	/**
	 * ouvre/ferme le drawer au clic sur le bouton menu
	 */
	public void clicSurBoutonMenu() {
		ouvrirFermerDrawer();
	}
	
	/**
	 * retourne vrai si la préférence NOM_PRENOM existe
	 * faux sinon
	 */
	private boolean isNomPrenomDownloaded(){
		if(a.getPrefs().getString(MyEpfPreferencesModele.KEY_NOM_PRENOM+identifiant)==null){
			return false;
		} else {
			return true;
		}
	}
	
	public void onMyEpfConnected(){
		initPhotoProfil();
		initNomPrenom();
		if(fragmentActuel instanceof EdtFragment){
			EdtFragment f = (EdtFragment)fragmentActuel;
			((EdtControleur)f.getControleur()).onMyEpfConnected();
		} else if(fragmentActuel instanceof BulletinFragment) {
			BulletinFragment f = (BulletinFragment)fragmentActuel;
			((BulletinControleur)f.getControleur()).onMyEpfConnected();
		} else if(fragmentActuel instanceof NotesFragment){
			NotesFragment f = (NotesFragment)fragmentActuel;
			((NotesControleur)f.getControleur()).onMyEpfConnected();
		}
	}
	
	/**
	 * Affiche la photo de profil
	 */
	private void afficherPhotoProfil(){
//		CircularImageView photoProfil = (CircularImageView)a.findViewById(R.id.photo_profil);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bmp = BitmapFactory.decodeFile(CHEMIN_PHOTO_PROFIL, options);
		// crop du bmp
		int decalage_x = a.getPrefs().getInt(MyEpfPreferencesModele.KEY_PHOTO_X, 0);
		if(decalage_x < 0)
			decalage_x = Math.abs(decalage_x);
		int decalage_y = a.getPrefs().getInt(MyEpfPreferencesModele.KEY_PHOTO_Y, 0);
		if(decalage_y < 0)
			decalage_y = Math.abs(decalage_y);
		if(bmp.getWidth() < bmp.getHeight())
			bmp=Bitmap.createBitmap(bmp, decalage_x, decalage_y,bmp.getWidth(), bmp.getWidth());
		else
			bmp=Bitmap.createBitmap(bmp, decalage_x, decalage_y,bmp.getHeight(), bmp.getHeight());
		photo_profil.setImageBitmap(bmp);
		Log.i(TAG, "afficherPhotoProfil OK");
	}
	
	/**
	 * retourne vrai si /data/data/.../photo_profil.jpg existe
	 * faux sinon
	 */
	private boolean isPhotoProfilDownloaded(){
		File file = new File(CHEMIN_PHOTO_PROFIL);
		if(file.exists()){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * se connecte à my data
	 * +
	 * parse le html et cherche le chemin de la photo de profil
	 * (appelle @see afficherPhotoProfil() )
	 */
	public void initPhotoProfil(){
		if(isPhotoProfilDownloaded()){
			Log.i(TAG, "initPhotoProfil : photo existante");
		} else {
			Log.i(TAG, "initPhotoProfil : photo non existante, téléchargement");
			// download photo de profil
			// +
			// afficherPhotoProfil()
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			a.runOnUiThread(new Runnable() {
				public void run() {
					HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
					HttpContext localContext = new BasicHttpContext();
					HttpGet httpGet = new HttpGet(MyEpfUrl.PROFIL);
					String cookies = CookieManager.getInstance().getCookie(MyEpfUrl.MYDATA);
					httpGet.setHeader(SM.COOKIE, cookies);
					InputStream is = null;
					try {
						is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
					} catch (Exception e) {
						e.printStackTrace();
						Log.i(TAG, "Impossible de joindre le serveur EPF (étonnant à ce stade)");
					}
					try {
						String line;
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						while ((line = br.readLine()) != null) {
							if(line.contains(STOP_PHOTO)){
								break;
							}
						}
						br.close();

						String regex = REGEX_PHOTO;
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

							Log.i(TAG, urlPhotoRelatif);

							String urlPhoto = MyEpfUrl.MYDATA+urlPhotoRelatif;

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
								Log.i(TAG, "Impossible de joindre le serveur EPF (2) (étonnant à ce stade)");
							}
						}
					} catch(IOException e){
						e.printStackTrace();
						Log.i(TAG, "Impossible de lire la réponse finale");
					}
				}
			});
		}
	}
	
	/**
	 * se connecte à my data
	 * +
	 * parse le html et cherche le nom du profil
	 * (appelle @see afficherNomPrenomProfil() )
	 */
	public void initNomPrenom(){
		if(isNomPrenomDownloaded()){
			Log.i(TAG, "initNomPrenom : nom existant");
		} else {
			Log.i(TAG, "initNomPrenom : nom non existant, téléchargement");
			// recherche nom de profil
			// +
			// afficherNomPrenomProfil()
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			a.runOnUiThread(new Runnable() {
				public void run() {
					HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
					HttpContext localContext = new BasicHttpContext();
					HttpGet httpGet = new HttpGet(MyEpfUrl.PROFIL);
					String cookies = CookieManager.getInstance().getCookie(MyEpfUrl.MYDATA);
					httpGet.setHeader(SM.COOKIE, cookies);
					InputStream is = null;
					try {
						is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
					} catch (Exception e) {
						e.printStackTrace();
						Log.i(TAG, "Impossible de joindre le serveur EPF (étonnant à ce stade)");
					} 
					try {
						String line;
						BufferedReader br = new BufferedReader(new InputStreamReader(is));
						while ((line = br.readLine()) != null) {
							if(line.contains(STOP_NOM)){
								System.out.print(line);
								break;
							}
						}
						br.close();

						String regex = REGEX_NOM;
						Pattern pattern = Pattern.compile(regex);
						Matcher matcher;
						try{
							matcher = pattern.matcher(line);
						}catch(Exception e){
							e.printStackTrace();
							return;
						}

						if(matcher.find()){
							String nomPrenom = matcher.group(1);
							// sauvegarde dans les pref
							a.getPrefs().putString(MyEpfPreferencesModele.KEY_NOM_PRENOM+identifiant, nomPrenom);
							// affichage
							afficherNomPrenom();
						}
					} catch(IOException e){
						e.printStackTrace();
						Log.i(TAG, "Impossible de lire la réponse finale");
					}
				}
			});
		}
	}
	
	/**
	 * 
	 */
	public void afficherNomPrenom(){
		String nomPrenom = a.getPrefs().getString(MyEpfPreferencesModele.KEY_NOM_PRENOM+identifiant);
		TextView tvNomPrenom = (TextView)a.findViewById(R.id.tvNomPrenom);
		if(nomPrenom == null){
			tvNomPrenom.setVisibility(View.GONE);
			Log.e(TAG, "nomPrenom null");
		} else {
			tvNomPrenom.setText(nomPrenom);
		}
		Log.d(TAG, "affichageNomPrenom OK");
	}
	
	/**
	 * Listener de la listview des titres 
	 */
	public void onTitresClicked(AdapterView<?> parent, View view,
			int position, long id, ListView lTitres){
		String titre = (String) lTitres.getItemAtPosition(position);
		if(titre.equals(a.getResources().getString(R.string.edt_titre))){
			lTitres.setItemChecked(position, true);
			onEdtClicked();
		} else if(titre.equals(a.getResources().getString(R.string.bulletin_titre))){
			lTitres.setItemChecked(position, true);
			onBulletinClicked();
		} else if(titre.equals(a.getResources().getString(R.string.notes_titre))){ 
			onNotesClicked();
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
		DrawerVue drawerVue = (DrawerVue)vue;
		if (!drawerVue.getLayoutGeneral().isDrawerOpen(drawerVue.getVue())) {
			drawerVue.getLayoutGeneral().openDrawer(drawerVue.getVue());
        } else {
        	drawerVue.getLayoutGeneral().closeDrawer(drawerVue.getVue());
        }
	}
	
	/**
	 * ?
	 */
	public void onPostCreate(Bundle savedInstanceState) {
		// Sync the toggle state after onRestoreInstanceState has occurred.
		DrawerVue drawerVue = (DrawerVue)vue;
		if(vue != null && drawerVue.getToggleBouton()!=null)
			drawerVue.getToggleBouton().syncState();
	}

	/**
	 * ? 
	 */
	public void onConfigurationChanged(Configuration newConfig) {
		((DrawerVue) vue).getToggleBouton().onConfigurationChanged(newConfig);
	}
	public String getIdentifiant() {
		return identifiant;
	}
}
