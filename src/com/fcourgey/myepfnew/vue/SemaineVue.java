package com.fcourgey.myepfnew.vue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.EdtFragment;
import com.fcourgey.myepfnew.activite.SemainesPagerAdapter;
import com.fcourgey.myepfnew.controleur.SemaineControleur;
import com.fcourgey.myepfnew.entite.Cours;
import com.fcourgey.myepfnew.framework.FragmentVue;

public class SemaineVue extends FragmentVue {
	
	// design
	private ProgressBar pbTelechargement;
	private ProgressBar pbTelechargement2;
	private TextView tvTelechargement;
	private TextView tvTelechargement2;
	
	public SemaineVue(SemaineControleur controleur, LayoutInflater inflater, ViewGroup container){
		super(controleur, inflater, container, R.layout.semaine_fragment);
		pbTelechargement = (ProgressBar)vue.findViewById(R.id.pbTelechargement);
		pbTelechargement2 = (ProgressBar)vue.findViewById(R.id.pbTelechargement2);
		tvTelechargement = (TextView)vue.findViewById(R.id.tvTelechargement);
		tvTelechargement2 = (TextView)vue.findViewById(R.id.tvTelechargement2);
	}
	
	/**
	 * init le header une fois l'edt downloaded
	 */
	public void initHeader(){
		controleur.getActivite().runOnUiThread(new Runnable() {
			public void run() {
				TextView tvSemaine2  =(TextView)vue.findViewById(R.id.tvSemaine2);
				Calendar dimancheActuel = (Calendar)((SemaineControleur)controleur).getLundiTéléchargé().clone();
				dimancheActuel.add(Calendar.DATE, 6);
				String mois = new SimpleDateFormat("MMMM", Locale.getDefault()).format(dimancheActuel.getTime());
				try{
					if(mois.length()>3 && mois.length()!=4){
						mois = mois.substring(0, 3); // récupère les 3 premières lettres du mois, sauf si 4 lettres au total
					}
				} catch(Exception e){}
				tvSemaine2.setText(	"du "+((SemaineControleur)controleur).getLundiTéléchargé().get(Calendar.DAY_OF_MONTH)+
									" au "+dimancheActuel.get(Calendar.DAY_OF_MONTH)+
									" "+mois);
				Switch sCm = (Switch)vue.findViewById(R.id.sCm);
				sCm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				        SemainesPagerAdapter.definirCm(isChecked);
				    }
				});
			}
		});
	}
	
	/**
	 * affiche la vue complète
	 * +
	 * cache la vue défaut
	 */
	public void chargerVueComplete(){
		controleur.getActivite().runOnUiThread(new Runnable() {
			public void run() {
				vue.findViewById(R.id.vueDefaut).setVisibility(View.GONE);
				vue.findViewById(R.id.vueComplete).setVisibility(View.VISIBLE);
			}
		});
	}
	
	public void creerIntervalles(boolean isContainerHeures, RelativeLayout intervalles_container){
		for(int j=0 ; j<SemaineControleur.HAUTEUR_EDT/SemaineControleur.HAUTEUR_INTERVALLE ; j++){
        	View v;
        	RelativeLayout.LayoutParams paramsSeparateur;
        	// heure
        	if(isContainerHeures && j%SemaineControleur.NB_INTERVALLE_PAR_HEURE==0 && j!=0){
        		TextView tv =new TextView(getActivite());
        		tv.setTextColor(getActivite().getResources().getColor(R.color.heures));
        		tv.setGravity(Gravity.CENTER_HORIZONTAL);
        		tv.setText(Integer.toString(SemaineControleur.HEURE_MIN+j/SemaineControleur.NB_INTERVALLE_PAR_HEURE)+"h");
        		paramsSeparateur = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        	paramsSeparateur.topMargin = (int)(j*SemaineControleur.HAUTEUR_INTERVALLE-0.7*SemaineControleur.HAUTEUR_INTERVALLE);
        		v = tv;
        	} 
        	// intervalle
        	else {
    			v = (View)getActivite().getLayoutInflater().inflate(R.drawable.separateur_view, null);
	        	int couleur = (j%SemaineControleur.NB_INTERVALLE_PAR_HEURE==0)?R.color.separateur_fonce:R.color.separateur_clair;
        		v.setBackgroundColor(getActivite().getResources().getColor(couleur));
	        	paramsSeparateur = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	        	paramsSeparateur.topMargin = (int)(j*SemaineControleur.HAUTEUR_INTERVALLE);
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
						SemaineVue.this.tvTelechargement.setText(texte);
						SemaineVue.this.tvTelechargement.setVisibility(View.GONE);
						SemaineVue.this.tvTelechargement2.setText(texte);
						SemaineVue.this.tvTelechargement2.setVisibility(View.GONE);
						SemaineVue.this.pbTelechargement.setVisibility(View.GONE);
						SemaineVue.this.pbTelechargement2.setVisibility(View.GONE);
						
					} else if(pourcentage > 0){
						if(!appeleParLaMere)
							Log.i(tag(), "Avancement "+pourcentage+" : "+texte);
						SemaineVue.this.tvTelechargement.setText(texte);
						SemaineVue.this.tvTelechargement2.setVisibility(View.VISIBLE);
						SemaineVue.this.pbTelechargement.setProgress(pourcentage);
						SemaineVue.this.pbTelechargement.setVisibility(View.VISIBLE);
						SemaineVue.this.tvTelechargement2.setText(texte);
						SemaineVue.this.pbTelechargement2.setProgress(pourcentage);
						SemaineVue.this.pbTelechargement2.setVisibility(View.VISIBLE);
						
					} else {
						if(!appeleParLaMere)
							Log.i(tag(), "Avancement erreur : "+texte);
						EdtFragment.setTelechargementEdtEnCours(false);
						SemaineVue.this.tvTelechargement.setText(texte);
						SemaineVue.this.pbTelechargement.setVisibility(View.GONE);
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

	public void initJoursHeader(ArrayList<RelativeLayout> rls, String[] jours) {
        // définit le texte des jour_header
        rls.add((RelativeLayout)vue.findViewById(R.id.heures_edt));
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
