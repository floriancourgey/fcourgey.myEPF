package com.fcourgey.myepfnew.controleur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.AsyncFragmentControleur;
import com.fcourgey.android.mylib.framework.AsyncFragmentVue;
import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.entite.Url;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;
import com.fcourgey.myepfnew.outils.JsonMyEPF;
import com.fcourgey.myepfnew.outils.StringOutils;

/**
 * Est lancé onMyEpfConnected
 * 
 * récupère le JSON mère
 * 
 * lance les semaines
 *
 */
@SuppressWarnings("deprecation")
public class EdtControleur extends AsyncFragmentControleur {
	
	public static final String TAG = "EdtFragment";
	
	private JSONObject jsonMain;	// commence par {
	private JSONArray jsonCours; 	// commence par [
	private SparseArray<JSONObject> mapCours; // SparseArray meilleur que Hmap pour <Integer, whatever>
	
	private final int indexPremiereSemaine; // [1 ; 53]
	private final int indexDerniereSemaine; // idem
	private final int indexSemaineActuelle; // idem

	public EdtControleur(Fragment f, LayoutInflater inflater, ViewGroup container) {
		super(f, inflater, container);
		
		vue = new AsyncFragmentVue(this, inflater, container, R.layout.edt_fragment);
		
		// init index semaine
		int semainesAvant = 0; // TODO récup pref
		int semainesApres = 5; // TODO récup pref
		Calendar now = Calendar.getInstance();
		indexSemaineActuelle = now.get(Calendar.WEEK_OF_YEAR);
		indexPremiereSemaine = now.get(Calendar.WEEK_OF_YEAR)-semainesAvant;
		indexDerniereSemaine = now.get(Calendar.WEEK_OF_YEAR)+semainesApres;
		Log.i(TAG, "semaines [ "+indexPremiereSemaine+" ; "+indexSemaineActuelle+" ; "+indexDerniereSemaine+" ]");
	}
	
	
	/**
	 * Affiche l'avancement sur la progressBar et son textview correspondant
	 * Fait un print log également
	 */
	public void avancement(final String texte, final int pourcentage){
		Log.i(TAG, "Avancement "+pourcentage+" : "+texte);
	}
	
	/**
	 * executé au téléchargement du main JSON
	 */
	private void onMainJsonDownloaded(){
		avancement("récupération des cours", 55);
		try{
			jsonCours = jsonMain.getJSONArray(JsonMyEPF.KEY_ARRAY_COURS);
		} catch(JSONException e){
			e.printStackTrace();
			avancement("Impossible de récupérer le jsonCours", 0);
		}
		System.out.println(jsonCours);
		avancement(jsonCours.length()+" cours", 55);
		avancement("mapping OR", 55);
		mapCours = new SparseArray<JSONObject>();
		for(int i=0 ; i<jsonCours.length() ; i++){
			try {
				JSONObject cours = jsonCours.getJSONObject(i);
				String sDate = cours.getString(JsonMyEPF.KEY_COURS_DEBUT);
				Calendar date = StringOutils.toCalendar(sDate, false);
				int iSemaine = date.get(Calendar.WEEK_OF_YEAR);
				if(iSemaine >= indexPremiereSemaine && iSemaine <= indexDerniereSemaine){
					mapCours.put(iSemaine, cours);
				}
			} catch (JSONException e) {
				avancement("Impossible de mapper le cours "+i, 0);
			}
		}
		avancement("mapping OK : "+mapCours.size()+" cours", 55);
		int key;
		for(int i = 0; i < mapCours.size(); i++) {
			key = mapCours.keyAt(i);
			JSONObject cours = mapCours.get(key);
			System.out.println(cours.toString());
		}
	}

	/**
	 * télécharge le main JSON de l'URL magique
	 */
	private void telecharcherMainJson(){
		new GetEdtSemaine().execute();
	}
	private class GetEdtSemaine extends AsyncTask<Boolean, Void, Void>{
		@Override
		protected Void doInBackground(Boolean... params) {
			avancement("Téléchargement de l'edt", 55);

			HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
			HttpContext localContext = new BasicHttpContext();
			String url = Url.EDT_JSON;
			HttpGet httpGet = new HttpGet(url);
			String cookies = CookieManager.getInstance().getCookie(Url.MYDATA);
			httpGet.setHeader(SM.COOKIE, cookies);
			avancement("Requête finale", 55);
			Log.i(TAG, "load get de l'url "+url);
			InputStream is;
			try {
				is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
			} catch (Exception e) {
				e.printStackTrace();
				avancement("Impossible de joindre le serveur EPF (étonnant à ce stade)", 0);
				return null;
			}
			avancement("Conversion en JSON", 55);
			try {
				StringBuilder sb = new StringBuilder();
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				try {
					JSONObject json = new JSONObject(sb.toString());
					jsonMain = json;
					onMainJsonDownloaded();
				} catch (JSONException e) {
					e.printStackTrace();
					avancement("Impossible de convertir en JSON", 0);
					Log.e(TAG, sb.toString());
					return null;
				}
			} catch(IOException e){
				e.printStackTrace();
				avancement("Impossible de lire la réponse finale", 0);
				return null;
			}
			return null;
		}
	}
	
	public void onMyEPFConnected() {
		telecharcherMainJson();
		chargerVueComplete();
	}
	
	public void chargerVueDefaut(String texte) {
		super.chargerVueDefaut();
		((TextView)vue.getVue().findViewById(R.id.tvTitre)).setText(texte);
	}
}
