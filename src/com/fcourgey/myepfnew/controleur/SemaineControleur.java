package com.fcourgey.myepfnew.controleur;

import org.json.JSONObject;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.AsyncFragmentControleur;
import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.vue.SemaineVue;

public class SemaineControleur extends AsyncFragmentControleur {
	
	// constantes
	public static final int HEURE_MIN = 8;
	public static final int HEURE_MAX = 18;
	public static final int INTERVALLE = 15;// minutes
	public static final int NB_INTERVALLE_PAR_HEURE = 60/INTERVALLE;
	public static final int NB_TOTAL_INTERVALLE = (HEURE_MAX-HEURE_MIN)*60/INTERVALLE;
	public static double HAUTEUR_INTERVALLE;
	public static double HAUTEUR_EDT;
	public static final String KEY_INDEX_FRAGMENT = "KEY_INDEX_FRAGMENT";
	
	//
	private int indexFragment;
	private int indexSemaine;


	public SemaineControleur(Fragment f, LayoutInflater inflater, ViewGroup container){
		super(f, inflater, container);
		
		indexFragment = getArguments().getInt(KEY_INDEX_FRAGMENT);
		
		// init design
		vue = new SemaineVue(this, inflater, container);
	}

	/**
	 * appelé quand l'edt a été téléchargé
	 */
	/*
	private void onEdtDownloaded(JSONObject json){
		((SemaineVue)vue).chargerVueComplete();

		a.runOnUiThread(new Runnable() {
			public void run() {
//				((SemaineVue)vue).initHeader();
//				initEdt();
			}
		});
	}
	*/
	/**
	 * Affiche :
	 * - les intervalles & les heures
	 * - les cours
	 * - la barre horizontale où on se trouve actuellement (barre now)
	 */
	/*
	public void initEdt(){
        final String jours[] = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
        final ArrayList<RelativeLayout> rls = new ArrayList<RelativeLayout>();
        initJoursContainer(rls, jours);
        ((SemaineVue)vue).initJoursHeader(rls, jours);
	    final RelativeLayout lundi_edt = (RelativeLayout)vue.getVue().findViewById(R.id.lundi_edt);
	    final ViewTreeObserver vto = lundi_edt.getViewTreeObserver();
	    // création cours + intervalles + barre now
	    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			public void onGlobalLayout() {
				if(vueCompleteChargee){
					return;
				}
				vueCompleteChargee = true;
				// chargement des cours
				lCoursVues = new ArrayList<CoursVue>();
				ArrayList<Cours> lCours = null;
	            try {
	            	JSONArray jsa = SemaineControleur.this.json.getJSONArray(JsonMyEPF.KEY_ARRAY_COURS);
	            	lCours = JsonMyEPF.jsonToListeCours(jsa, a);
				} catch (JSONException e) {
					e.printStackTrace(); // TODO
					return;
				}
	            // création boutons cours + intervalles
				HAUTEUR_EDT = lundi_edt.getHeight();
	            // pour chaque container
	            Calendar jourCourant = (Calendar)lundiTéléchargé.clone();
	            boolean isContainerHeures = true;
	            int iJourCourant = Calendar.MONDAY;
	            String sJourCourant;
	            for(RelativeLayout edt : rls){
	            	// si les heures sont cachées dans les pref, je les cache
	            	if(!a.getPrefs().getBoolean(MyEpfPreferencesModele.KEY_HEURES, true) && isContainerHeures){
	            		((View)edt.getParent().getParent()).setVisibility(View.GONE);
	            		isContainerHeures = false;
	            		continue;
	            	}
	            	sJourCourant = jours[iJourCourant];
	            	// intervalles
	            	RelativeLayout parent = (RelativeLayout)edt.getParent();
		            RelativeLayout.LayoutParams paramsParent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		            HAUTEUR_INTERVALLE = (double)HAUTEUR_EDT/NB_TOTAL_INTERVALLE;
		            /// je crée un nouveau layout, container de mes intervalles
		            RelativeLayout intervalles_container = new RelativeLayout(a);
		            parent.addView(intervalles_container, 0, paramsParent);
		            /// j'ajoute les intervalles dedans
		            ((SemaineVue)vue).creerIntervalles(isContainerHeures, intervalles_container);
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
		            		CoursVue b = new CoursVue(getActivite(), c);
                            int resID = getActivite().getResources().getIdentifier(sJourCourant+"_edt", "id", getActivite().getPackageName());
                            if(resID == 0){
                            	Log.i(tag(), sJourCourant+"_edt Ressource introuvable, cours non ajouté : "+c);
                            	continue;
                            }
                            RelativeLayout l = (RelativeLayout)vue.getVue().findViewById(resID);
                            double nbIntervallesHauteurBouton = (double)(c.getHeureFin()-c.getHeureDebut())*60/15  + (double)(c.getMinutesFinInt()-c.getMinutesDebutInt())/15;
                            int hauteurBouton = (int)(nbIntervallesHauteurBouton*HAUTEUR_INTERVALLE);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hauteurBouton);
                            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                            double nbIntervallesMargeBouton = (double)(c.getHeureDebut()-HEURE_MIN)*60/15 + c.getMinutesDebutInt()/15;
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
		            	RelativeLayout rlBarreNow = new RelativeLayout(a);
		            	/// je l'ajoute au parent
		            	parent.addView(rlBarreNow, paramsParent);
		            	/// j'ajoute la barre
		            	View barreNow = (View)getActivite().getLayoutInflater().inflate(R.drawable.separateur_view, null);
		            	barreNow.setBackgroundColor(getActivite().getResources().getColor(R.color.barre_now));
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
	*/
	
	/**
	 * init la liste des relative layout container de chaque jour 
	 */
	/*
	private void initJoursContainer(ArrayList<RelativeLayout> rls, String[] jours) {
		for (int iJour=Calendar.MONDAY; iJour < jours.length; iJour++){
	    	String jour = jours[iJour];
			// liste des jour_container
	    	String nomContainer = jour+"_edt";
	    	int resID = getResources().getIdentifier(nomContainer, "id", getActivite().getPackageName());
	    	if(resID == 0){
            	Log.i(tag(), nomContainer+" Ressource introuvable (getLayoutJours)");
            	continue;
            } else {
            	rls.add((RelativeLayout)vue.getVue().findViewById(resID));
            }
		}
	}
	*/

	/*
	public void onCoursDisplayed(){
		SemainesPagerAdapter.definirCm(null);
		updateProchainSite();
		avancement("Edt téléchargé", 100, false);
	}
*/

	/**
	 * Définit le prochain site où l'on a cours
	 */
	/*
	public void updateProchainSite(){
		// seulement sur le 1er frag
		if(indexFragment != 0){
			a.runOnUiThread(new Runnable() {
				public void run() {
					((TextView)vue.getVue().findViewById(R.id.tvLabelProchainSite)).setVisibility(View.GONE);
					((TextView)vue.getVue().findViewById(R.id.tvProchainSite)).setVisibility(View.GONE);
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
		((SemaineVue)vue).updateProchainSite(prochainCours);
	}
*/

	/**
	 * lance GetEdtSemaine @see GetEdtSemaine
	 * @param firstTime
	 * @param avancementUneSemaine
	 */
	/*
	private void lancerTelechargement(boolean firstTime, boolean avancementUneSemaine){
		new GetEdtSemaine().execute(firstTime, avancementUneSemaine);
	}
	private class GetEdtSemaine extends AsyncTask<Boolean, Void, Void>{
		@Override
		protected Void doInBackground(Boolean... params) {
			avancement("Attente connexion à my.epf", 15, false);
			while(!MainActivite.connecteAMyEpf || EdtControleur.telechargementEdtEnCours){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			avancement("Téléchargement de l'edt", 30, false);
			EdtControleur.telechargementEdtEnCours=true;
			boolean firstTime = params[0];
			boolean avancementUneSemaine = params[1];

			HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
			HttpContext localContext = new BasicHttpContext();
			String weekDirection = (firstTime)?Url.EDT_JSON_VALUE_NONE:Url.EDT_JSON_VALUE_NEXT;
			String url = Url.EDT_JSON+"&"+Url.EDT_JSON_KEY_DIRECTION+"=";
			url += weekDirection;
			HttpGet httpGet = new HttpGet(url);
			String cookies = CookieManager.getInstance().getCookie(Url.MYDATA);
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
					EdtControleur.setTelechargementEdtEnCours(false);
					return null;
				}
				try {
					MyEpfPreferencesModele pref = (MyEpfPreferencesModele)a.getPrefs();
					pref.setJsonSemaine(indexSemaine, sb.toString());
					JSONObject json = new JSONObject(sb.toString());
					onEdtDownloaded(json);
				} catch (JSONException e) {
					//						e.printStackTrace();
					avancement("Impossible de convertir en JSON", 0, false);
					Log.e(tag(), sb.toString());
					return null;
				}
			} catch(IOException e){
				//					e.printStackTrace();
				avancement("Impossible de lire la réponse finale", 0, false);
				return null;
			}
			return null;
		}
	}
	*/

	private String tag(){
		return "frag n°"+indexFragment+" semaine n°"+indexSemaine;
	}

	/**
	 * Affiche l'avancement sur la progressBar et son textview correspondant
	 * Fait un print log également
	 */
	public void avancement(final String texte, final int pourcentage, final boolean appeleParLaMere){
		((SemaineVue)vue).avancement(texte, pourcentage, appeleParLaMere);
	}

	public int getIndexFragment() {
		return indexFragment;
	}
	public int getIndexSemaine() {
		return indexSemaine;
	}
}
