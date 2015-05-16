package com.fcourgey.myepfnew.controleur;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONObject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.AsyncFragmentControleur;
import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.entite.Cours;
import com.fcourgey.myepfnew.fragment.SemainesPagerAdapter;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
import com.fcourgey.myepfnew.vue.CoursVue;
import com.fcourgey.myepfnew.vue.SemaineVue;

public class SemaineControleur extends AsyncFragmentControleur {
	
	// constantes
	public static final int HEURE_MIN = 8;	// heures
	public static final int HEURE_MAX = 18;	// heures
	public static final int INTERVALLE = 15;// minutes
	public static final int NB_INTERVALLE_PAR_HEURE = 60/INTERVALLE;
	public static final int NB_TOTAL_INTERVALLE = (HEURE_MAX-HEURE_MIN)*60/INTERVALLE;
	
	// index
	private int indexFragment;
	private int indexSemaine;
	
	// Calendar
	private Calendar lundiDeCetteSemaine;
	private Calendar dimancheDeCetteSemaine;
	
	// cours
	private ArrayList<CoursVue> lCoursVues; 
	private ArrayList<JSONObject> lJsonCours;
	
	// String des jours de la semaine
	private static String[] jours;

	// booléens
	private boolean vueCompleteChargee = false;
	private static boolean heuresActives;

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
		
		Log.i(tag(), "onCreate");
		
		// init json des pref
		MyEpfPreferencesModele prefs = (MyEpfPreferencesModele)getActivite().getPrefs();
		lJsonCours = prefs.getCoursSemaine(indexSemaine);
		
		// activer les heures ?
		heuresActives = getPrefs().getBoolean(MyEpfPreferencesModele.KEY_HEURES, true);
		
		// init design
		jours = new DateFormatSymbols(Locale.getDefault()).getWeekdays();
		vue = new SemaineVue(this, inflater, container);
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
		((SemaineVue)vue).initCours(true);
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

	public boolean isHeuresActives() {
		return heuresActives;
	}
	public static String[] getJours(){
		return jours;
	}

	public ArrayList<JSONObject> getLJsonCours() {
		return lJsonCours;
	}
}
