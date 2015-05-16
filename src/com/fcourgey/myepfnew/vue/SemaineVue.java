package com.fcourgey.myepfnew.vue;

import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.AsyncFragmentVue;
import com.fcourgey.android.mylib.outils.Date;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.controleur.SemaineControleur;
import com.fcourgey.myepfnew.entite.Cours;
import com.fcourgey.myepfnew.fragment.SemainesPagerAdapter;
import com.fcourgey.myepfnew.outils.JsonMyEPF;

public class SemaineVue extends AsyncFragmentVue {
	
	// constantes
	public static double HAUTEUR_INTERVALLE;	// px
	public static double HAUTEUR_EDT;			// px
	
	// design
//	private ProgressBar pbTelechargement;
	private ProgressBar pbTelechargement2;
//	private TextView tvTelechargement;
	private TextView tvTelechargement2;
	private RelativeLayout heuresContainer;
	private ArrayList<RelativeLayout> lJoursContainer;
	
	// String des jours de la semaine
	private String[] jours;
	
	// layout heures actif ?
	private boolean heuresActives;
	
	// flag
	private boolean vueInitialisée = false;
	
	public SemaineVue(SemaineControleur controleur, LayoutInflater inflater, ViewGroup container){
		super(controleur, inflater, container, R.layout.semaine_fragment);
		
		jours = SemaineControleur.getJours();
		heuresActives = controleur.isHeuresActives();
		
//		pbTelechargement = (ProgressBar)vue.findViewById(R.id.pbTelechargement);
		pbTelechargement2 = (ProgressBar)vue.findViewById(R.id.pbTelechargement2);
//		tvTelechargement = (TextView)vue.findViewById(R.id.tvTelechargement);
		tvTelechargement2 = (TextView)vue.findViewById(R.id.tvTelechargement2);
		
		initHeader();
		
		initContainers();
		
		// lignes moches mais
		// nécessaires pour le getHeight
		final RelativeLayout lundi_edt = (RelativeLayout)getVue().findViewById(R.id.lundi_edt);
		ViewTreeObserver vto = lundi_edt.getViewTreeObserver();													
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {							
			public void onGlobalLayout() {
				if(vueInitialisée)
					return;
				
				HAUTEUR_EDT = lundi_edt.getHeight();
				
				initJoursHeader(jours);
				
				HAUTEUR_INTERVALLE = (double)HAUTEUR_EDT/SemaineControleur.NB_TOTAL_INTERVALLE;
				
				initIntervalles();
				
				initBarreNow();
				
				vueInitialisée = true;
				
				initCours();
			}
		});
	}
	
	public void initCours(){
		Log.d(tag(), "affichage des cours");
		
		SemaineControleur controleur = (SemaineControleur)this.controleur;
		
		ArrayList<JSONObject> lJsonCours = controleur.getLJsonCours();
		if(lJsonCours == null){
			return; // TODO vacances ou erreur
		}
		
		final ArrayList<CoursVue> lCoursVues = new ArrayList<CoursVue>();
		ArrayList<Cours> lCours = new ArrayList<Cours>();
		
		try {
			for(JSONObject jsonCours : lJsonCours){
				lCours.add(JsonMyEPF.jsoToListeCours(jsonCours, getActivite()));
			}
		} catch (JSONException e) {
			e.printStackTrace(); // TODO
			return;
		}
		// création boutons cours
		// pour chaque container
		Calendar jourCourant = (Calendar)controleur.getLundiDeCetteSemaine().clone();
		int iJourCourant = Calendar.MONDAY;
		String sJourCourant;
		for(@SuppressWarnings("unused") RelativeLayout jour_container : lJoursContainer){
			sJourCourant = jours[iJourCourant];
			// boutons cours
			/// on fait défiler tous les cours de ce jour ci
			for(final Cours c : lCours){
				// si on est dans le même jour
				if(c.getCalendar().get(Calendar.DAY_OF_MONTH) == jourCourant.get(Calendar.DAY_OF_MONTH)){
					int resID = getActivite().getResources().getIdentifier(sJourCourant+"_edt", "id", getActivite().getPackageName());
					if(resID == 0){
						Log.e(tag(), sJourCourant+"_edt Ressource introuvable, cours non ajouté : "+c);
						break;
					}
					final RelativeLayout jour_edt = (RelativeLayout)getVue().findViewById(resID);
					getActivite().runOnUiThread(new Runnable() {
						public void run() {
							jour_edt.removeAllViews();
							double nbIntervallesHauteurBouton = (double)(c.getHeureFin()-c.getHeureDebut())*60/15  + (double)(c.getMinutesFinInt()-c.getMinutesDebutInt())/15;
							int hauteurBouton = (int)(nbIntervallesHauteurBouton*HAUTEUR_INTERVALLE);
							final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, hauteurBouton);
							params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
							double nbIntervallesMargeBouton = (double)(c.getHeureDebut()-SemaineControleur.HEURE_MIN)*60/15 + c.getMinutesDebutInt()/15;
							int marge = (int)(nbIntervallesMargeBouton*HAUTEUR_INTERVALLE);
		
							params.topMargin = marge;
							final CoursVue b = new CoursVue(getActivite(), c);
							lCoursVues.add(b);
							jour_edt.addView(b, params);
						}
					});
				}
			}
			jourCourant.add(Calendar.DATE, 1);
			iJourCourant++;
		}
	}

	/**
	 * ajout de la barre now
	 */
	private void initBarreNow(){
        Calendar now = Calendar.getInstance();
        Calendar jourCourant = (Calendar)((SemaineControleur)controleur).getLundiDeCetteSemaine().clone();
        for(RelativeLayout edt : lJoursContainer){
        	// intervalles
        	RelativeLayout parent = (RelativeLayout)edt.getParent();
        	RelativeLayout.LayoutParams paramsParent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT); 
	        /// si on est sur le jour d'ajourd'hui
	        if(jourCourant.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)){
	        	/// je crée le container de la barre now
	        	RelativeLayout rlBarreNow = new RelativeLayout(getActivite());
	        	/// je l'ajoute au parent
	        	parent.addView(rlBarreNow, paramsParent);
	        	/// j'ajoute la barre
	        	View barreNow = (View)getActivite().getLayoutInflater().inflate(R.drawable.separateur_view, null);
	        	barreNow.setBackgroundColor(getActivite().getResources().getColor(R.color.barre_now));
	        	RelativeLayout.LayoutParams paramsBarreNow = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        	int heure = now.get(Calendar.HOUR_OF_DAY) - SemaineControleur.HEURE_MIN;
	        	int minutes = now.get(Calendar.MINUTE);
	        	paramsBarreNow.topMargin = (int)((heure*SemaineControleur.NB_INTERVALLE_PAR_HEURE+minutes/SemaineControleur.INTERVALLE)*HAUTEUR_INTERVALLE);
	        	paramsBarreNow.height = 5;
	        	rlBarreNow.addView(barreNow, paramsBarreNow);
	        	return;
	        }
	        jourCourant.add(Calendar.DATE, 1);
        }
	}
	
	/**
	 * init le header
	 */
	public void initHeader(){
		controleur.getActivite().runOnUiThread(new Runnable() {
			public void run() {
				// texte de la date de la semaine
				TextView tvSemaine2  =(TextView)vue.findViewById(R.id.tvSemaine2);
				SemaineControleur sControleur = ((SemaineControleur)controleur);
				Calendar lundiDeCetteSemaine = sControleur.getLundiDeCetteSemaine();
				Calendar dimancheDeCetteSemaine = sControleur.getDimancheDeCetteSemaine();
				String texte = getResources().getString(R.string.edt_header_date);
				texte = texte.replace("{START}", Integer.toString(lundiDeCetteSemaine.get(Calendar.DAY_OF_MONTH)));
				texte = texte.replace("{END}", Integer.toString(dimancheDeCetteSemaine.get(Calendar.DAY_OF_MONTH)));
				texte = texte.replace("{MOIS}", Date.abreMois(dimancheDeCetteSemaine));
				tvSemaine2.setText(texte);
				// activation switch CM
				Switch sCm = (Switch)vue.findViewById(R.id.sCm);
				sCm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				        SemainesPagerAdapter.definirCm(isChecked);
				    }
				});
				// affichage "next"
				SemaineControleur controleur = (SemaineControleur)SemaineVue.this.controleur;
				Calendar now = Calendar.getInstance();
				if(controleur.getIndexSemaine() != now.get(Calendar.WEEK_OF_YEAR)){
					TextView tvLabelProchainSite  =(TextView)vue.findViewById(R.id.tvLabelProchainSite);
					tvLabelProchainSite.setVisibility(View.GONE);
					TextView tvProchainSite  =(TextView)vue.findViewById(R.id.tvProchainSite);
					tvProchainSite.setVisibility(View.GONE);
				}
			}
		});
	}
	
	/**
	 * init la liste des relative layout container de chaque jour 
	 */
	private void initContainers() {
		// heures container
		heuresContainer = ((RelativeLayout)vue.findViewById(R.id.heures_edt));
		// si les heures sont cachées dans les pref, je les cache
    	if(!heuresActives){
    		((View)heuresContainer.getParent().getParent()).setVisibility(View.GONE);
    	}
		// jours container
		lJoursContainer = new ArrayList<RelativeLayout>();
		for (int iJour=Calendar.MONDAY; iJour < jours.length; iJour++){
	    	String jour = jours[iJour];
			// liste des jour_container
	    	String nomContainer = jour+"_edt";
	    	int resID = getResources().getIdentifier(nomContainer, "id", getActivite().getPackageName());
	    	if(resID == 0){
            	Log.i(tag(), nomContainer+" Ressource introuvable (getLayoutJours)");
            	continue;
            } else {
            	lJoursContainer.add((RelativeLayout)getVue().findViewById(resID));
            }
		}
	}
	
	/**
	 * crée les intervalles
	 * +
	 * les heures
	 * 
	 * pour tous les jours + les heures
	 */
	private void initIntervalles(){
		initIntervalles(heuresContainer, true);
		for(RelativeLayout edt : lJoursContainer){
			initIntervalles(edt, false);
		}
	}
	
	/**
	 * crée les intervalles
	 * +
	 * les heures
	 * 
	 * pour 1 layout
	 */
	private void initIntervalles(RelativeLayout edt, boolean isContainerHeures){
    	// intervalles
    	RelativeLayout parent = (RelativeLayout)edt.getParent();
        RelativeLayout.LayoutParams paramsParent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        /// je crée un nouveau layout, container de mes intervalles
        RelativeLayout intervalles_container = new RelativeLayout(getActivite());
        parent.addView(intervalles_container, 0, paramsParent);
        /// j'ajoute les intervalles dedans
		for(int j=0 ; j<HAUTEUR_EDT/HAUTEUR_INTERVALLE ; j++){
        	View v;
        	RelativeLayout.LayoutParams paramsSeparateur;
        	// heures
        	if(isContainerHeures && j%SemaineControleur.NB_INTERVALLE_PAR_HEURE==0 && j!=0){
        		TextView tv =new TextView(getActivite());
        		tv.setTextColor(getActivite().getResources().getColor(R.color.heures));
        		tv.setGravity(Gravity.CENTER_HORIZONTAL);
        		tv.setText(Integer.toString(SemaineControleur.HEURE_MIN+j/SemaineControleur.NB_INTERVALLE_PAR_HEURE)+"h");
        		paramsSeparateur = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        	paramsSeparateur.topMargin = (int)(j*HAUTEUR_INTERVALLE-0.7*HAUTEUR_INTERVALLE);
        		v = tv;
        	} 
        	// intervalle
        	else {
    			v = (View)getActivite().getLayoutInflater().inflate(R.drawable.separateur_view, null);
	        	int couleur = (j%SemaineControleur.NB_INTERVALLE_PAR_HEURE==0)?R.color.separateur_fonce:R.color.separateur_clair;
        		v.setBackgroundColor(getActivite().getResources().getColor(couleur));
	        	paramsSeparateur = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        	paramsSeparateur.topMargin = (int)(j*HAUTEUR_INTERVALLE);
	        	paramsSeparateur.height = 1;
        	}
        	intervalles_container.addView(v, paramsSeparateur);
        }
	}
	
	/**
	 * Affiche l'avancement sur la progressBar et son textview correspondant
	 * Fait un print log également
	 */
	public void avancement(final String texte, final int pourcentage, final boolean appeleParLaMere){
		try{
			controleur.getActivite().runOnUiThread(new Runnable() {
				public void run() {
					if(pourcentage >= 100){
						if(!appeleParLaMere)
							Log.i(tag(), "Avancement terminé : "+texte);
//						SemaineVue.this.tvTelechargement.setText(texte);
//						SemaineVue.this.tvTelechargement.setVisibility(View.GONE);
						SemaineVue.this.tvTelechargement2.setText(texte);
						SemaineVue.this.tvTelechargement2.setVisibility(View.GONE);
//						SemaineVue.this.pbTelechargement.setVisibility(View.GONE);
						SemaineVue.this.pbTelechargement2.setVisibility(View.GONE);
						
					} else if(pourcentage > 0){
						if(!appeleParLaMere)
							Log.i(tag(), "Avancement "+pourcentage+" : "+texte);
//						SemaineVue.this.tvTelechargement.setText(texte);
						SemaineVue.this.tvTelechargement2.setVisibility(View.VISIBLE);
//						SemaineVue.this.pbTelechargement.setProgress(pourcentage);
//						SemaineVue.this.pbTelechargement.setVisibility(View.VISIBLE);
						SemaineVue.this.tvTelechargement2.setText(texte);
						SemaineVue.this.pbTelechargement2.setProgress(pourcentage);
						SemaineVue.this.pbTelechargement2.setVisibility(View.VISIBLE);
						
					} else {
						if(!appeleParLaMere)
							Log.i(tag(), "Avancement erreur : "+texte);
//						EdtControleur.setTelechargementEdtEnCours(false);
//						SemaineVue.this.tvTelechargement.setText(texte);
//						SemaineVue.this.pbTelechargement.setVisibility(View.GONE);
						SemaineVue.this.tvTelechargement2.setText(texte);
						SemaineVue.this.pbTelechargement2.setVisibility(View.GONE);
					}

				}
			});
		} catch (Exception e){
			e.printStackTrace();
			Log.w(tag(), "Erreur inconnue dans avancement..");
		}
	}
	
	private String tag(){
		return "frag n°"+((SemaineControleur)controleur).getIndexFragment()+" semaine n°"+((SemaineControleur)controleur).getIndexSemaine();
	}

	public void updateProchainSite(final Cours c) {
		controleur.getActivite().runOnUiThread(new Runnable() {
			public void run() {
				if(c == null){
					((TextView)vue.findViewById(R.id.tvProchainSite)).setText("weekend !");
				} else {
					((TextView)vue.findViewById(R.id.tvProchainSite)).setText(c.getSite()+" ("+c.getSalle()+")");
				}
			}
		});
	}

	public void initJoursHeader(String[] jours) {
        // définit le texte des jour_header
	    for (int iJour=Calendar.MONDAY; iJour < jours.length; iJour++){
	    	String jour = jours[iJour];
	    	String jourRaccourci = jour.substring(0,3);
	    	
	    	// texte des jour_header
	    	String nomHeader = jour+"_header";
	    	int resID = getResources().getIdentifier(nomHeader, "id", getActivite().getPackageName());
	    	if(resID == 0){
            	Log.i(tag(), nomHeader+" Ressource introuvable (getLayoutJours)");
            	continue;
            } else {
            	((TextView)vue.findViewById(resID)).setText(jourRaccourci);
            }
	    }
	}
}
