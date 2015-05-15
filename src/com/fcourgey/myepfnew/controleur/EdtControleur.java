package com.fcourgey.myepfnew.controleur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import android.support.v4.view.ViewPager;
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
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.entite.MyEpfUrl;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;
import com.fcourgey.myepfnew.fragment.SemainesPagerAdapter;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
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
	
	public static final String TAG = "EdtControleur";
	
	private JSONObject jsonMain;	// commence par {"cours":[{"id":1,
	private JSONArray jsonCours; 	// commence par {"id":1,"start":"2015
	private SparseArray<ArrayList<JSONObject>> mapCours; // SparseArray meilleur que Hmap pour <Integer, whatever>
	
	public static final int semainesAvant = 2;// TODO récup pref
	public static final int semainesApres = 3;// TODO récup pref
	private final int indexPremiereSemaine; // [1 ; 53]
	private final int indexDerniereSemaine; // idem
	public static int indexSemaineActuelle; // idem
	
	private SemainesPagerAdapter semainesPagerAdapter;

	public EdtControleur(Fragment f, LayoutInflater inflater, ViewGroup container) {
		super(f, inflater, container);
		
		vue = new AsyncFragmentVue(this, inflater, container, R.layout.edt_fragment);
		
		// init index semaine
		Calendar now = Calendar.getInstance();
		indexSemaineActuelle = now.get(Calendar.WEEK_OF_YEAR);
		indexPremiereSemaine = now.get(Calendar.WEEK_OF_YEAR)-semainesAvant;
		indexDerniereSemaine = now.get(Calendar.WEEK_OF_YEAR)+semainesApres;
		Log.i(TAG, "semaines ["+indexPremiereSemaine+" ; "+indexSemaineActuelle+" ; "+indexDerniereSemaine+"]");
		
		// si on a déjà du json dans les pref, on charge la vue complète
		MyEpfPreferencesModele prefs = (MyEpfPreferencesModele)getActivite().getPrefs();
		if(prefs.getCoursSemaine(indexSemaineActuelle) != null){
			chargerVueComplete();
		}
		// sinon vue défaut
		else {
			chargerVueDefaut("Connexion à my.epf");
		}
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
//		System.out.println(jsonCours);
		avancement(jsonCours.length()+" cours", 55);
		avancement("mapping json<->hmap de liste de json", 55);
		mapCours = new SparseArray<ArrayList<JSONObject>>();
		for(int i=0 ; i<jsonCours.length() ; i++){
			try {
				JSONObject cours = jsonCours.getJSONObject(i);
				String sDate = cours.getString(JsonMyEPF.KEY_COURS_DEBUT);
				Calendar date = StringOutils.toCalendar(sDate, false);
				int iSemaine = date.get(Calendar.WEEK_OF_YEAR);
				if(iSemaine >= indexPremiereSemaine && iSemaine <= indexDerniereSemaine){
					ArrayList<JSONObject> lCours = mapCours.get(iSemaine);
					if(lCours == null){
						lCours = new ArrayList<JSONObject>();
					}
					lCours.add(cours);
					mapCours.put(iSemaine, lCours);
				}
			} catch (JSONException e) {
				avancement("Impossible de mapper le cours "+i, 0);
			}
		}
		int key;
		int nbCours = 0;
		for(int i = 0; i < mapCours.size(); i++) {
			key = mapCours.keyAt(i);
			ArrayList<JSONObject> lCours = mapCours.get(key);
			for(JSONObject cours : lCours){
//				System.out.println(cours.toString());
				nbCours++;
			}
		}
		avancement("mapping OK : "+nbCours+" cours sur "+mapCours.size()+" semaines", 55);
		avancement("enregistrement json pref", 55);
		MyEpfPreferencesModele prefs = (MyEpfPreferencesModele)getActivite().getPrefs();
		for(int i = 0; i < mapCours.size(); i++) {
			key = mapCours.keyAt(i);
			ArrayList<JSONObject> lCours = mapCours.get(key);
			prefs.setCoursSemaine(key, lCours);
		}
		onMapCoursMapped();
	}
	
	/**
	 * appelé quand le jsonCours
	 */
	private void onMapCoursMapped(){
		semainesPagerAdapter.onMapCoursMapped();
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
			String url = MyEpfUrl.EDT_JSON;
			HttpGet httpGet = new HttpGet(url);
			String cookies = CookieManager.getInstance().getCookie(MyEpfUrl.MYDATA);
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
		semainesPagerAdapter.onMyEPFConnected();
	}
	
	@Override
	public void chargerVueComplete(){
		super.chargerVueComplete();
		ViewPager viewPager = (ViewPager) vue.getVue().findViewById(R.id.accueil_pager);
		semainesPagerAdapter = new SemainesPagerAdapter(getFragment().getChildFragmentManager(), (MainActivite)a);
		viewPager.setOffscreenPageLimit(SemainesPagerAdapter.NOMBRE_DE_SEMAINES_MAX+1);
		viewPager.setAdapter(semainesPagerAdapter);
		int positionViewPager = EdtControleur.semainesAvant;
		Calendar now = Calendar.getInstance();
		Calendar samediActuel = Calendar.getInstance();
		samediActuel.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		samediActuel.set(Calendar.HOUR_OF_DAY, 14); // TODO dans les pref
		if(now.after(samediActuel)){
			positionViewPager++;
		}
		viewPager.setCurrentItem(positionViewPager);
	}
	private void chargerVueDefaut(String texte) {
		super.chargerVueDefaut();
		((TextView)vue.getVue().findViewById(R.id.tvTitre)).setText(texte);
	}
}
