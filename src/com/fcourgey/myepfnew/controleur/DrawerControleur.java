package com.fcourgey.myepfnew.controleur;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.BulletinFragment;
import com.fcourgey.myepfnew.activite.EdtFragment;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.activite.PreferencesActivite;
import com.fcourgey.myepfnew.outils.Android;
import com.fcourgey.myepfnew.vue.DrawerVue;

public class DrawerControleur extends Fragment {
	
	private boolean activerDrawer = false;
	
	private MainActivite a;
	
	private DrawerVue vue;
	
	public DrawerControleur(MainActivite a) {
		this.a = a;
//		vue = new DrawerVue(a, a.getIdentifiant());
		if(activerDrawer)
			initOnDrawerListeners();
	}
	
	/**
	 * au clic sur l'emploi du temps
	 */
	public void onEdtClicked(){
		Fragment newFragment = new EdtFragment();
        FragmentTransaction transaction = a.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
	}
	
	/**
	 * au clic sur le bulletin
	 */
	public void onBulletinClicked(){
		Fragment newFragment = new BulletinFragment();
        FragmentTransaction transaction = a.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
		// init popup
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
    	builder = new AlertDialog.Builder(a);
		String versionName;
		try {
			versionName = "version "+a.getPackageManager().getPackageInfo(a.getPackageName(), 0).versionName+" ";
		} catch (NameNotFoundException e) {
			versionName = "";
		}
    	builder.setMessage("myEPF "+versionName+"développé par Florian Courgey pour l'EPF École d'ingénieur-e-s.")
    	       .setTitle("A propos")
    	       .setPositiveButton("OK", null);
        // lancement popup
        builder.create().show();
	}
	/**
	 * au clic sur Quitter
	 */
	public void onQuitterClicked(){
		Android.quitter();
	}
	
	/**
	 * initialise les listeners
	 */
	private void initOnDrawerListeners() {
		((TextView)a.findViewById(R.id.edt)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onEdtClicked();
			}
		});
		((TextView)a.findViewById(R.id.bulletin)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onBulletinClicked();
			}
		});
		((TextView)a.findViewById(R.id.preferences)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onPreferencesClicked();
			}
		});
        ((TextView)a.findViewById(R.id.apropos)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onAProposClicked();
			}
		});
        ((TextView)a.findViewById(R.id.quitter)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onQuitterClicked();
			}
		});
	}

	/**
	 * ouvre/ferme le drawer au clic sur le bouton menu
	 */
	public void clicSurBoutonMenu() {
		if (!vue.getLayoutGeneral().isDrawerOpen(vue.getVue())) {
			vue.getLayoutGeneral().openDrawer(vue.getVue());
        } else {
        	vue.getLayoutGeneral().closeDrawer(vue.getVue());
        }
	}

	/**
	 * ?
	 */
	@SuppressWarnings("deprecation")
	public void onPostCreate(Bundle savedInstanceState) {
		// Sync the toggle state after onRestoreInstanceState has occurred.
		vue.getToggleBouton().syncState();
	}

	/**
	 * ? 
	 */
	@SuppressWarnings("deprecation")
	public void onConfigurationChanged(Configuration newConfig) {
		vue.getToggleBouton().onConfigurationChanged(newConfig);
	}

	public DrawerVue getVue() {
		return vue;
	}

	
}
