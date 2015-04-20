package com.fcourgey.myepfnew.activite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.CookieManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.entite.Cours;
import com.fcourgey.myepfnew.entite.Cours.Type;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;
import com.fcourgey.myepfnew.modele.DbModele;
import com.fcourgey.myepfnew.modele.PreferencesModele;
import com.fcourgey.myepfnew.vue.CoursVue;

public class SemaineFragment extends Fragment {
	
	// constantes
	public static final int HEURE_MIN = 8;
	public static final int HEURE_MAX = 18;
	public static final int INTERVALLE = 15;// minutes
	public static final int NB_INTERVALLE_PAR_HEURE = 60/INTERVALLE;
	public static final int NB_TOTAL_INTERVALLE = (HEURE_MAX-HEURE_MIN)*60/INTERVALLE;
	private static final String KEY_INDEX = "key_index";
	public static final String URL_MAGIQUE = "https://mydata.epf.fr/pegasus/index.php?com=planning&job=get-cours&direction=";
	public static final String URL_MAGIQUE_NONE = "none";
	public static final String URL_MAGIQUE_NEXT = "next";
	
	// mère
	private static MainActivite mere;
	
	// index
	private int indexFragment; 	// 0 ; SemainesPagerAdapter.NOMBRE_DE_SEMAINES_MAX
	private int indexSemaine;	// 1 ; 52
	
	// booléens
	private boolean vueCompleteChargee = false;

	// design
	private View v;
	private ProgressBar pbTelechargement;
	private ProgressBar pbTelechargement2;
	private TextView tvTelechargement;
	private TextView tvTelechargement2;
	
	// json reçu
	private JSONObject json;
	
	// liste des cours
	private ArrayList<CoursVue> lCoursVues;
	
	// Date du lundi de cette semaine
	private Calendar lundiActuel;
	
	
	/**
     * appelé avant de lancer un onCreate
     * +
     * initialise le numéro d'index
     */
	public static Fragment newInstance(int indexFragment) {
		SemaineFragment f = new SemaineFragment();
        Bundle args = new Bundle();
        
        args.putInt(KEY_INDEX, indexFragment);
        
        f.setArguments(args);
        return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		// init mere
		mere = (MainActivite)getActivity();	
		
		// init index fragment
		indexFragment = getArguments().getInt(KEY_INDEX);
		// init index semaine
		Calendar now = Calendar.getInstance();
		indexSemaine = now.get(Calendar.WEEK_OF_YEAR) + indexFragment;
		Calendar ceSamedi = Calendar.getInstance();
		ceSamedi.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		ceSamedi.set(Calendar.HOUR_OF_DAY, 14);
		if(now.after(ceSamedi)){
			indexSemaine++;
		}
		
		Log.i(tag(), "onCreateView frag n°"+indexFragment+" pour la semaine n°"+indexSemaine);
		
		// init design
		v = inflater.inflate(R.layout.semaine_fragment, container, false);
		pbTelechargement = (ProgressBar)v.findViewById(R.id.pbTelechargement);
		pbTelechargement2 = (ProgressBar)v.findViewById(R.id.pbTelechargement2);
		tvTelechargement = (TextView)v.findViewById(R.id.tvTelechargement);
		tvTelechargement2 = (TextView)v.findViewById(R.id.tvTelechargement2);
		
		// chargement du json si présent
		chargerDepuisJsonSauvegarde();
		
//		if(now.after(ceSamedi)){
//			// si on est plus tard que samedi 14h, on décale
//			lancerTelechargement(true, true);
//		}
		
		
		if(indexFragment==0){
			lancerTelechargement(true, false);
		} else {
			lancerTelechargement(false, false);
		}
		
		return v;
	}
	
	/**
	 * cherche un json sauvegardé et le charge
	 * @return success : false KO / true OK
	 */
	private void chargerDepuisJsonSauvegarde() {
		String jsonSauvegarde = mere.getPrefs().getJsonSemaine(indexSemaine);
		if(jsonSauvegarde == null || jsonSauvegarde.length() < 1){
			return;
		}
		JSONObject json;
		try {
			json = new JSONObject(jsonSauvegarde);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		onEdtDownloaded(json);
	}

	/**
	 * appelé quand l'edt a été téléchargé
	 */
	private void onEdtDownloaded(JSONObject json){
		
		EdtFragment.setTelechargementEdtEnCours(false);
		
		// si json téléchargé
		if(this.json != null){
			avancement("Edt téléchargé identique à sauvegarde", 100, false);
			// je compare le json téléchargé et le sauvegardé
			// si identique, pas besoin de se casser la tête : return
			if(this.json.toString().equals(json.toString())){
				Log.i(tag(), "Le JSON sauvegardé et le téléchargé sont identiques, skip");
				return;
			}
		} 
		// si json récupéré depuis sauvegarde
		else {
			avancement("Affichage de l'edt", 80, false);
		}
		this.json = json;
		try{
			lundiActuel = stringToCalendar(json.getString("debut_semaine_active"), false);
		} catch (Exception e){
			e.printStackTrace(); // TODO
			return;
		}
		Log.i(tag(), "debut_semaine_active "+lundiActuel.get(Calendar.DAY_OF_MONTH)+" "+lundiActuel.get(Calendar.MONTH));
				
//		avancement("Semaine récupérée en "+(SystemClock.elapsedRealtime()-temps1)/1000+"s.", 100);
		
		MainActivite.serverOk = true;
		chargerVueComplete();
		
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				initHeader();
				initEdt();
			}
		});
	}
	
	private void onCoursDisplayed(){
		SemainesPagerAdapter.definirCm(null);
		updateProchainSite();
		avancement("Edt téléchargé", 100, false);
	}
	
	/**
	 * in :  2015-12-31T23:59 (2015-12-31 sans les heures si @param inclureHoraire faux)
	 * out : un calendar hydraté avec ces infos
	 */
	private Calendar stringToCalendar(String s, boolean inclureHoraire){
	    Calendar calendarTest = Calendar.getInstance();
	    SimpleDateFormat sdf;
	    if(inclureHoraire){
		    sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm", Locale.getDefault());
	    } else {
	    	sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	    }
	    try {
	    	calendarTest.setTime(sdf.parse(s.replace("T", " "))); /// car T est invalide
		} catch (ParseException e) {
//			 TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return calendarTest;
	}
	
	/**
	 * init le header une fois l'edt downloaded
	 */
	private void initHeader(){
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				TextView tvSemaine2  =(TextView)v.findViewById(R.id.tvSemaine2);
				Calendar dimancheActuel = (Calendar)lundiActuel.clone();
				dimancheActuel.add(Calendar.DATE, 6);
				String mois = new SimpleDateFormat("MMMM", Locale.getDefault()).format(dimancheActuel.getTime());
				try{
					if(mois.length()>3 && mois.length()!=4){
						mois = mois.substring(0, 3); // récupère les 3 premières lettres du mois, sauf si 4 lettres au total
					}
				} catch(Exception e){}
				tvSemaine2.setText(	"du "+lundiActuel.get(Calendar.DAY_OF_MONTH)+
									" au "+dimancheActuel.get(Calendar.DAY_OF_MONTH)+
									" "+mois);
				Switch sCm = (Switch)v.findViewById(R.id.sCm);
				sCm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				        SemainesPagerAdapter.definirCm(isChecked);
				    }
				});
			}
		});
	}
	
	/**
	 * Affiche :
	 * - les intervalles
	 * - les cours
	 * - la barre horizontale d'où on se trouve actuellement (barre now)
	 */
	private void initEdt(){
		final ArrayList<RelativeLayout> rls = new ArrayList<RelativeLayout>();
        final String jours[] = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
        // init la liste des jour_container
        // +
        // définit le texte des jour_header
        rls.add((RelativeLayout)v.findViewById(R.id.heures_edt));
	    for (int iJour=Calendar.MONDAY; iJour < jours.length; iJour++){
	    	String jour = jours[iJour];
	    	String jourRaccourci = jour.substring(0,3);
	    	// liste des jour_container
	    	String nomContainer = jour+"_edt";
	    	int resID = getActivity().getResources().getIdentifier(nomContainer, "id", getActivity().getPackageName());
	    	if(resID == 0){
            	Log.i(tag(), nomContainer+" Ressource introuvable (getLayoutJours)");
            } else {
            	rls.add((RelativeLayout)v.findViewById(resID));
            }
	    	// texte des jour_header
	    	String nomHeader = jour+"_header";
	    	resID = getActivity().getResources().getIdentifier(nomHeader, "id", getActivity().getPackageName());
	    	if(resID == 0){
            	Log.i(tag(), nomContainer+" Ressource introuvable (getLayoutJours)");
            } else {
            	((TextView)v.findViewById(resID)).setText(jourRaccourci);
            }
	    }
	    final RelativeLayout lundi_edt = (RelativeLayout)v.findViewById(R.id.lundi_edt);
	    final ViewTreeObserver vto = lundi_edt.getViewTreeObserver();
	    // création cours + intervalles + barre now
	    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				if(vueCompleteChargee){
					return;
				}
				// init cours
				lCoursVues = new ArrayList<CoursVue>();
				ArrayList<Cours> lCours = new ArrayList<Cours>();
	            try {
	            	JSONArray jsa = SemaineFragment.this.json.getJSONArray("cours");
	            	for(int i=0; i<jsa.length() ; i++){
	            		JSONObject jso = jsa.getJSONObject(i);
	            		String nom = jso.getString("title");
	            		nom = nom.replaceAll("\\(\\w+\\) ([\\w ]+)", "$1");
            		    String professeur = jso.getString("enseignant");
            		    String salle = jso.getString("salle");
            		    Type type;
            		    try{
            		    	type = Type.valueOf(jso.getString("type"));
            		    } catch (Exception e){
            		    	type = Type.AUTRE;
            		    }
            		    String commentaire = jso.getString("commentaire");
            		    Calendar horaireDebut = stringToCalendar(jso.getString("start"), true);
            		    Calendar horaireFin = stringToCalendar(jso.getString("end"), true);
            		    
            		    String dateId = jso.getString("start");
            		    
            		    // recherche des devoirs dans la db
            		    DbModele modele = new DbModele(mere);
            		    String devoirs = null;
						try {
							devoirs = modele.getDevoir(dateId);
						} catch (Exception e) {}
	            		
	            		Cours c = new Cours(dateId, nom, professeur, salle, horaireDebut, horaireFin, type, commentaire, devoirs);
	            		lCours.add(c);
	            	}
				} catch (JSONException e) {
					e.printStackTrace();
				}
	            // création boutons cours + intervalles
				vueCompleteChargee = true;
				final int HAUTEUR_EDT = lundi_edt.getHeight();
	            // pour chaque container
	            Calendar jourCourant = (Calendar)lundiActuel.clone();
	            boolean isContainerHeures = true;
	            int iJourCourant = Calendar.MONDAY;
	            String sJourCourant;
	            for(RelativeLayout edt : rls){
	            	// si les heures sont cachées dans les pref, je les cache
	            	if(!mere.getPrefs().getBoolean(PreferencesModele.KEY_HEURES, true) && isContainerHeures){
	            		((View)edt.getParent().getParent()).setVisibility(View.GONE);
	            		isContainerHeures = false;
	            		continue;
	            	}
	            	sJourCourant = jours[iJourCourant];
	            	// intervalles
	            	RelativeLayout parent = (RelativeLayout)edt.getParent();
		            RelativeLayout.LayoutParams paramsParent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		            final double HAUTEUR_INTERVALLE = (double)HAUTEUR_EDT/NB_TOTAL_INTERVALLE;
		            /// je crée un nouveau layout, container de mes intervalles
		            RelativeLayout intervalles_container = new RelativeLayout(mere);
		            parent.addView(intervalles_container, 0, paramsParent);
		            /// j'ajoute les intervalles dedans
		            for(int j=0 ; j<HAUTEUR_EDT/HAUTEUR_INTERVALLE ; j++){
		            	View v;
			        	RelativeLayout.LayoutParams paramsSeparateur;
			        	if(isContainerHeures && j%NB_INTERVALLE_PAR_HEURE==0 && j!=0){
			        		TextView tv =new TextView(getActivity());
			        		tv.setTextColor(getResources().getColor(R.color.heures));
			        		tv.setGravity(Gravity.CENTER_HORIZONTAL);
			        		tv.setText(Integer.toString(HEURE_MIN+j/NB_INTERVALLE_PAR_HEURE)+"h");
			        		paramsSeparateur = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				        	paramsSeparateur.topMargin = (int)(j*HAUTEUR_INTERVALLE-0.7*HAUTEUR_INTERVALLE);
			        		v = tv;
			        	} else {
		        			v = (View)getActivity().getLayoutInflater().inflate(R.drawable.separateur_view, null);
				        	int couleur = (j%NB_INTERVALLE_PAR_HEURE==0)?R.color.separateur_fonce:R.color.separateur_clair;
			        		v.setBackgroundColor(getResources().getColor(couleur));
				        	paramsSeparateur = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				        	paramsSeparateur.topMargin = (int)(j*HAUTEUR_INTERVALLE);
				        	paramsSeparateur.height = 1;
			        	}
			        	intervalles_container.addView(v, paramsSeparateur);
		            }
		            // boutons cours
		            /// on zappe le container heures
		            if(isContainerHeures){
		            	isContainerHeures = false;
		            	continue;
		            }
		            /// on fait défiler tous les cours de ce jour ci
		            for(Cours c : lCours){
		            	// si on est dans le même jour
		            	if(c.getCalendar().get(Calendar.DAY_OF_MONTH) == jourCourant.get(Calendar.DAY_OF_MONTH)){
		            		CoursVue b = new CoursVue(getActivity(), c);
                            int resID = getActivity().getResources().getIdentifier(sJourCourant+"_edt", "id", getActivity().getPackageName());
                            if(resID == 0){
                            	Log.i(tag(), sJourCourant+"_edt Ressource introuvable, cours non ajouté : "+c);
                            	continue;
                            }
                            RelativeLayout l = (RelativeLayout)v.findViewById(resID);
                            double nbIntervallesHauteurBouton = (double)(c.getHeureFin()-c.getHeureDebut())*60/15  + (double)(c.getMinutesFinInt()-c.getMinutesDebutInt())/15;
                            int hauteurBouton = (int)(nbIntervallesHauteurBouton*HAUTEUR_INTERVALLE);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hauteurBouton);
                            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                            double nbIntervallesMargeBouton = (double)(c.getHeureDebut()-SemaineFragment.HEURE_MIN)*60/15 + c.getMinutesDebutInt()/15;
                            int marge = (int)(nbIntervallesMargeBouton*HAUTEUR_INTERVALLE);
                            params.topMargin = marge;
                            lCoursVues.add(b);
                            l.addView(b, params);
		            	}
		            	
		            }
		            // barre now
		            Calendar now = Calendar.getInstance();
		            /// si on est sur le jour d'ajourd'hui
		            if(jourCourant.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)){
		            	/// je crée le container de la barre now
		            	RelativeLayout rlBarreNow = new RelativeLayout(mere);
		            	/// je l'ajoute au parent
		            	parent.addView(rlBarreNow, paramsParent);
		            	/// j'ajoute la barre
		            	View barreNow = (View)getActivity().getLayoutInflater().inflate(R.drawable.separateur_view, null);
		            	barreNow.setBackgroundColor(getResources().getColor(R.color.barre_now));
		            	RelativeLayout.LayoutParams paramsBarreNow = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		            	int heure = now.get(Calendar.HOUR_OF_DAY) - HEURE_MIN;
		            	int minutes = now.get(Calendar.MINUTE);
		            	paramsBarreNow.topMargin = (int)((heure*NB_INTERVALLE_PAR_HEURE+minutes/INTERVALLE)*HAUTEUR_INTERVALLE);
		            	paramsBarreNow.height = 5;
		            	rlBarreNow.addView(barreNow, paramsBarreNow);
		            }
		            jourCourant.add(Calendar.DATE, 1);
		            iJourCourant++;
	            }
	            onCoursDisplayed();
	        }	
	    });
	}
	
	void updateProchainSite(){
		// seulement sur le 1er frag
		if(indexFragment != 0){
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					((TextView)v.findViewById(R.id.tvLabelProchainSite)).setVisibility(View.GONE);
					((TextView)v.findViewById(R.id.tvProchainSite)).setVisibility(View.GONE);
				}
			});
			return;
		}
		Calendar now = Calendar.getInstance();
		Cours prochainCours = null;
		for(CoursVue cv : lCoursVues){
			if(cv.getVisibility() == View.GONE){
				continue;
			}
			Calendar horaireCourant =  cv.getCours().getCalendar();
			int heure = Calendar.HOUR_OF_DAY;
			int jour = Calendar.DAY_OF_MONTH;
			// soit c'est le même jour avec l'heure supérieur 
			// soit c'est le jour supérieur
			if(	(horaireCourant.get(heure)>now.get(heure) && horaireCourant.get(jour)==now.get(jour))
				||
				(horaireCourant.get(jour)>now.get(jour))){
				if(prochainCours == null){
					prochainCours = cv.getCours();
				} else {
					if(horaireCourant.get(heure)<prochainCours.getHeureFin() && horaireCourant.get(jour)<=prochainCours.getCalendar().get(jour)){
						prochainCours = cv.getCours();
					}
				}
			}
		}
		final Cours c = prochainCours;
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				if(c == null){
//					((TextView)v.findViewById(R.id.tvLabelProchainSite)).setVisibility(View.GONE);
					((TextView)v.findViewById(R.id.tvProchainSite)).setText("weekend !");
				} else {
//					((TextView)v.findViewById(R.id.tvLabelProchainSite)).setVisibility(View.VISIBLE);
					((TextView)v.findViewById(R.id.tvProchainSite)).setText(c.getSite()+" ("+c.getSalle()+")");
				}
			}
		});
	}
	
	/**
	 * affiche la vue complète
	 * +
	 * cache la vue défaut
	 */
	private void chargerVueComplete(){
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				v.findViewById(R.id.vueDefaut).setVisibility(View.GONE);
				v.findViewById(R.id.vueComplete).setVisibility(View.VISIBLE);
			}
		});
	}
	

	/**
	 * lance GetEdtSemaine @see GetEdtSemaine
	 * @param firstTime
	 * @param avancementUneSemaine
	 */
	private void lancerTelechargement(boolean firstTime, boolean avancementUneSemaine){
		new GetEdtSemaine().execute(firstTime, avancementUneSemaine);
	}
	private class GetEdtSemaine extends AsyncTask<Boolean, Void, Void>{
	    @Override
	    protected Void doInBackground(Boolean... params) {
	    	avancement("Attente connexion à my.epf", 15, false);
	    	while(!MainActivite.connecteAMyEpf || EdtFragment.telechargementEdtEnCours){
		        try {
		            Thread.sleep(500);
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
	    	}
	    	
	    	avancement("Téléchargement de l'edt", 30, false);
	    	EdtFragment.telechargementEdtEnCours=true;
	    	boolean firstTime = params[0];
	    	boolean avancementUneSemaine = params[1];
	    	
	        HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
			HttpContext localContext = new BasicHttpContext();
			String weekDirection = (firstTime)?URL_MAGIQUE_NONE:URL_MAGIQUE_NEXT;
			String url = URL_MAGIQUE;
			url += weekDirection;
			HttpGet httpGet = new HttpGet(url);
			String cookies = CookieManager.getInstance().getCookie(EdtFragment.URL_MYDATA);
			httpGet.setHeader(SM.COOKIE, cookies);
			avancement("Requête finale", 90, false);
			Log.i(tag(), "load get de l'url "+url);
			InputStream is;
			try {
				is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
			} catch (Exception e) {
				e.printStackTrace();
				avancement("Impossible de joindre le serveur EPF (étonnant à ce stade)", 0, false);
				return null;
			}
			avancement("Parsing de l'edt", 50, false);
			try {
				StringBuilder sb = new StringBuilder();
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				if(avancementUneSemaine){
					EdtFragment.setTelechargementEdtEnCours(false);
					return null;
				}
				try {
					mere.getPrefs().setJsonSemaine(indexSemaine, sb.toString());
					JSONObject json = new JSONObject(sb.toString());
					onEdtDownloaded(json);
				} catch (JSONException e) {
//					e.printStackTrace();
					avancement("Impossible de convertir en JSON", 0, false);
					Log.e(getTag(), sb.toString());
					return null;
				}
			} catch(IOException e){
//				e.printStackTrace();
				avancement("Impossible de lire la réponse finale", 0, false);
				return null;
			}
			return null;
	    }
	}
	
	private String tag(){
		return "frag n°"+indexFragment+" semaine n°"+indexSemaine;
	}
	
	/**
	 * Affiche l'avancement sur la progressBar et son textview correspondant
	 * Fait un print log également
	 */
	public void avancement(final String texte, final int pourcentage, final boolean appeleParLaMere){
		try{
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					if(pourcentage >= 100){
						if(!appeleParLaMere)
							Log.i(tag(), "Avancement terminé : "+texte);
						SemaineFragment.this.tvTelechargement.setText(texte);
						SemaineFragment.this.tvTelechargement.setVisibility(View.GONE);
						SemaineFragment.this.tvTelechargement2.setText(texte);
						SemaineFragment.this.tvTelechargement2.setVisibility(View.GONE);
						SemaineFragment.this.pbTelechargement.setVisibility(View.GONE);
						SemaineFragment.this.pbTelechargement2.setVisibility(View.GONE);
						
					} else if(pourcentage > 0){
						if(!appeleParLaMere)
							Log.i(tag(), "Avancement "+pourcentage+" : "+texte);
						SemaineFragment.this.tvTelechargement.setText(texte);
						SemaineFragment.this.tvTelechargement2.setVisibility(View.VISIBLE);
						SemaineFragment.this.pbTelechargement.setProgress(pourcentage);
						SemaineFragment.this.pbTelechargement.setVisibility(View.VISIBLE);
						SemaineFragment.this.tvTelechargement2.setText(texte);
						SemaineFragment.this.pbTelechargement2.setProgress(pourcentage);
						SemaineFragment.this.pbTelechargement2.setVisibility(View.VISIBLE);
						
					} else {
						if(!appeleParLaMere)
							Log.i(tag(), "Avancement erreur : "+texte);
						EdtFragment.setTelechargementEdtEnCours(false);
						SemaineFragment.this.tvTelechargement.setText(texte);
						SemaineFragment.this.pbTelechargement.setVisibility(View.GONE);
						SemaineFragment.this.tvTelechargement2.setText(texte);
						SemaineFragment.this.pbTelechargement2.setVisibility(View.GONE);
//						SemaineFragment.this.tvTelechargement2.setVisibility(View.GONE);
					}

				}
			});
		} catch (Exception e){
			e.printStackTrace();
			Log.w(getTag(), "Erreur inconnue dans avancement..");
		}
	}

	public ArrayList<CoursVue> getLCoursVues() {
		return lCoursVues;
	}
}
