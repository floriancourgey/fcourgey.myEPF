package com.fcourgey.myepfnew.controleur;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ReadOnlyBufferException;
import java.util.Calendar;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.AsyncFragmentControleur;
import com.fcourgey.android.mylib.framework.AsyncFragmentVue;
import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.entite.Url;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;

@SuppressWarnings("deprecation")
public class BulletinControleur extends AsyncFragmentControleur {
	
public static String CHEMIN_BULLETIN;
	
	private boolean ecritureOK = false;
	
	public BulletinControleur(Fragment f, LayoutInflater inflater, ViewGroup container) {
		super(f, inflater, container);

		vue = new AsyncFragmentVue(this, inflater, container, R.layout.bulletin_fragment);

		Calendar c = Calendar.getInstance();
		String identifiant = ((MainActivite)a).getIdentifiant();
		int iAnnee = c.get(Calendar.YEAR);
		// si on est apr septembre, on décale de -1
		if(c.get(Calendar.MONTH)<=Calendar.SEPTEMBER){
			iAnnee--;
		}
		Url.BULLETIN = Url.BULLETIN.replace("{ANNEE}", Integer.toString(iAnnee)).replace("{LOGIN}", identifiant);

		try{
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				ecritureOK = true;
				CHEMIN_BULLETIN = Environment.getExternalStorageDirectory()+"/bulletin-myepf-"+identifiant+".pdf";
			} else {
				ecritureOK = false;
				throw new ReadOnlyBufferException();
			}
		}catch(ReadOnlyBufferException e){
			lectureSeule();
			return;
		}


		((TextView)vue.getVue().findViewById(R.id.bTelechargerBulletin)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(ecritureOK)
					onTelechargerBulletinClicked();
				else 
					lectureSeule();
			}
		});

		// si on est pas encore connecté à my.epf
		// vue défaut
		if(MainActivite.connecteAMyEpf){
			chargerVueComplete();
		} else if(MainActivite.enTrainDeSeConnecterAMyEPF && !MainActivite.connecteAMyEpf){
			chargerVueDefaut();
		} else {
			lectureSeule();
		}
	}
	
	private void lectureSeule(){
		chargerVueErreur("SD en Lecture seule", "Il est impossible d'enregistrer le bulletin sur la SD");
	}
	
	private void onTelechargerBulletinClicked() {
		telechargerEtAfficherBulletin();
	}
	
	/**
	 * télécharge le bulletin si connecté à myepf
	 * dans 
	 */
	private void telechargerEtAfficherBulletin() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		getActivite().runOnUiThread(new Runnable() {
			public void run() {
				HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
				HttpContext localContext = new BasicHttpContext();
				Log.i("Bulletin.telechargerEtAfficherBulletin", "url : "+Url.BULLETIN);
				HttpGet httpGet = new HttpGet(Url.BULLETIN);
				String cookies = CookieManager.getInstance().getCookie(Url.MY_EPF);
				httpGet.setHeader(SM.COOKIE, cookies);
				InputStream is = null;
				OutputStream output = null;
				try {
					is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
				} catch (Exception e) {
					e.printStackTrace();
					Log.i("Bulletin.telechargerEtAfficherBulletin", "Impossible de joindre le serveur EPF (étonnant à ce stade 2)");
					return;
				} 
				try {
					// Lecture du PDF, décommenter pour debug
//					String reponseHtml = "";
//					String line;
//					BufferedReader br = new BufferedReader(new InputStreamReader(is));
//					while ((line = br.readLine()) != null) {
//						System.out.print(line);
//						Log.e("telechargerBulletin", line);
//						reponseHtml += line;
//
//					}
//					br.close();
//					
//					//  debug dans le wvDebug
//					String mime = "text/html";
//					String encoding = "utf-8";
//					WebView wvDebug = (WebView)vue.findViewById(R.id.wvDebug);
//					wvDebug.loadDataWithBaseURL(null, reponseHtml, mime, encoding, null);
					
					// écriture du fichier
					final File file = new File(CHEMIN_BULLETIN);
					output = new FileOutputStream(file);
					final byte[] buffer = new byte[1024];
		            int read;

		            while ((read = is.read(buffer)) != -1){
		                output.write(buffer, 0, read);
		            }

		            output.flush();
		            output.close();
		            
		            onTelechargerBulletin();
				} catch(IOException e){
					e.printStackTrace();
					Log.i("Bulletin.telechargerEtAfficherBulletin", "Impossible de lire la réponse finale");
				}
			}
		});
	}
	
	private void onTelechargerBulletin(){
		File file = new File(CHEMIN_BULLETIN);
		Log.i("bulletin.onTelechargerBulletin", "ouvertue du PDF "+CHEMIN_BULLETIN);
		if(!file.exists() || file.isDirectory()) {
			// impossible de trouver le bulletin
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivite());
			builder	
			.setTitle(R.string.bulletin_nopdf_titre)
			.setMessage("Impossible de trouver le bulletin sur le téléphone")
			.setPositiveButton(R.string.ok, null);
			builder.create().show();
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),"application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		try {
			getActivite().startActivity(intent);
		} catch (Exception e) {
			String message = getActivite().getResources().getString(R.string.bulletin_nopdf_message);
			message = message.replace("{CHEMIN_BULLETIN}", CHEMIN_BULLETIN);
			// aucune appli pour ouvrir un PDF
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivite());
			builder	
			.setTitle(R.string.bulletin_nopdf_titre)
			.setMessage(message)
			.setPositiveButton(R.string.ok, null);
			builder.create().show();
			return;
		}  
	}

	/**
	 * cache les semaines
	 * et
	 * affiche le layout d'erreur
	 */
	public void onDelaiDAttenteDepassé() {
		chargerVueErreur("délai d'attente dépassé", "Impossible de se connecter à my.epf.fr\nCheck tes identifiants dans les préférences\net redémarre l'appli");		
	}

	public void onMyEPFConnected() {
		chargerVueComplete();
	}

}
