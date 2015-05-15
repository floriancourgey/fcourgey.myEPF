package com.fcourgey.myepfnew.controleur;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle.Control;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.AsyncFragmentControleur;
import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.entite.Cours;
import com.fcourgey.myepfnew.fragment.SemainesPagerAdapter;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
import com.fcourgey.myepfnew.outils.JsonMyEPF;
import com.fcourgey.myepfnew.vue.CoursVue;
import com.fcourgey.myepfnew.vue.SemaineVue;

public class SemaineControleur extends AsyncFragmentControleur {
	
	// constantes
	public static final int HEURE_MIN = 8;	// heures
	public static final int HEURE_MAX = 18;	// heures
	public static final int INTERVALLE = 15;// minutes
	public static final int NB_INTERVALLE_PAR_HEURE = 60/INTERVALLE;
	public static final int NB_TOTAL_INTERVALLE = (HEURE_MAX-HEURE_MIN)*60/INTERVALLE;
	public static double HAUTEUR_INTERVALLE;	// px
	public static double HAUTEUR_EDT;			// px
	
	// index
	private int indexFragment;
	private int indexSemaine;
	
	// Calendar
	private Calendar lundiDeCetteSemaine;
	private Calendar dimancheDeCetteSemaine;
	
	// cours
	private ArrayList<CoursVue> lCoursVues; 
	private ArrayList<JSONObject> lJsonCours;

	// booléens
	private boolean vueCompleteChargee = false;

	public SemaineControleur(Fragment f, LayoutInflater inflater, ViewGroup container, int indexFragment){
		super(f, inflater, container);
		// init index
		this.indexFragment = indexFragment;
		Calendar now = Calendar.getInstance();
		this.indexSemaine = now.get(Calendar.WEEK_OF_YEAR) - EdtControleur.semainesAvant + indexFragment;
		lundiDeCetteSemaine = Calendar.getInstance();
		lundiDeCetteSemaine.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		lundiDeCetteSemaine.add(Calendar.DATE, (indexSemaine-EdtControleur.indexSemaineActuelle)*7);
		dimancheDeCetteSemaine = Calendar.getInstance();
		dimancheDeCetteSemaine.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		dimancheDeCetteSemaine.add(Calendar.DATE, (indexSemaine-EdtControleur.indexSemaineActuelle)*7);
		
		System.out.println(tag());
		
		// init design
		vue = new SemaineVue(this, inflater, container);
		
		chargerVueComplete();
	}
	
	@Override
	public void chargerVueComplete() {
		super.chargerVueComplete();
		// récupération des cours si existent
		MyEpfPreferencesModele prefs = (MyEpfPreferencesModele)getActivite().getPrefs();
		lJsonCours = prefs.getCoursSemaine(indexSemaine);
		if(lJsonCours == null){
			return;
		} else {
			a.runOnUiThread(new Runnable() {
				public void run() {
					initEdt();
				}
			});
		}
		
	}

	/**
	 * appelé quand l'edt a été téléchargé
	 */
	public void onMyEPFConnected() {
		Log.d(tag(), "onMyEPFConnected");
	}
	
	public void onMapCoursMapped() {
		Log.d(tag(), "onMapCoursMapped");
		// TODO comparaison avec l'ancien
		// si == alors pas de chgt
		chargerVueComplete();
	}
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
				if(lJsonCours == null){
					return; // TODO vacances ou erreur
				}
				vueCompleteChargee = true;
				// chargement des cours
				lCoursVues = new ArrayList<CoursVue>();
				ArrayList<Cours> lCours = new ArrayList<Cours>();
	            try {
//	            	JSONArray jsa = SemaineControleur.this.json.getJSONArray(JsonMyEPF.KEY_ARRAY_COURS);
	            	for(JSONObject jsonCours : lJsonCours){
	            		lCours.add(JsonMyEPF.jsoToListeCours(jsonCours, a));
	            	}
//	            	lCours = JsonMyEPF.jsaToListeCours(jsa, a);
				} catch (JSONException e) {
					e.printStackTrace(); // TODO
					return;
				}
	            // création boutons cours + intervalles
				HAUTEUR_EDT = lundi_edt.getHeight();
	            // pour chaque container
	            Calendar jourCourant = (Calendar)lundiDeCetteSemaine.clone();
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
	
	/**
	 * init la liste des relative layout container de chaque jour 
	 */
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

	
	public void onCoursDisplayed(){
		avancement("Edt affiché", 100, false);
		SemainesPagerAdapter.definirCm(null);
		updateProchainSite();
	}


	/**
	 * Définit le prochain site où l'on a cours
	 */
	public void updateProchainSite(){
		// seulement sur le 1er frag
		if(indexFragment != 0 + EdtControleur.semainesAvant){
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

	public Calendar getLundiDeCetteSemaine() {
		return lundiDeCetteSemaine;
	}

	public Calendar getDimancheDeCetteSemaine() {
		return dimancheDeCetteSemaine;
	}

	public ArrayList<CoursVue> getLCoursVues() {
		return lCoursVues;
	}
}
