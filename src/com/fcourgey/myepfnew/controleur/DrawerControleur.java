package com.fcourgey.myepfnew.controleur;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.BulletinFragment;
import com.fcourgey.myepfnew.activite.EdtFragment;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.activite.PreferencesActivite;
import com.fcourgey.myepfnew.outils.Android;
import com.fcourgey.myepfnew.vue.DrawerVue;

public class DrawerControleur {
	
	private MainActivite a;
	
	private DrawerVue vue;
	
	public DrawerControleur(MainActivite a, Bundle savedInstanceState) {
		this.a = a;
		vue = new DrawerVue(this, a.getIdentifiant());
		if(savedInstanceState == null){
			onEdtClicked();
		}
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

	private void fermerDrawer(){
		vue.getLayoutGeneral().closeDrawer(vue.getVue());
	}
	
	/**
	 * ouvre/ferme le drawer au clic sur le bouton menu
	 */
	public void clicSurBoutonMenu() {
		ouvrirFermerDrawer();
	}
	
	public void onTitresClicked(AdapterView<?> parent, View view,
			int position, long id, ListView lTitres){
		String titre = (String) lTitres.getItemAtPosition(position);
		if(titre.equals(a.getResources().getString(R.string.edt_titre))){
			onEdtClicked();
		} else if(titre.equals(a.getResources().getString(R.string.bulletin_titre))){
			onBulletinClicked();
		} else if(titre.equals(a.getResources().getString(R.string.pref_titre))){
			onPreferencesClicked();
		} else if(titre.equals(a.getResources().getString(R.string.apropos_titre))){
			onAProposClicked();
		} else if(titre.equals(a.getResources().getString(R.string.quitter_titre))){
			onQuitterClicked();
		}
		fermerDrawer();
	}

	
	public void ouvrirFermerDrawer() {
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

	public MainActivite getActivite() {
		return a;
	}
}
