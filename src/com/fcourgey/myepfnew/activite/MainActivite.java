package com.fcourgey.myepfnew.activite;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.fcourgey.android.mylib.framework.Activite;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.controleur.MainControleur;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
import com.fcourgey.myepfnew.vue.MainVue;

public class MainActivite extends Activite {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activite);
		controleur = new MainControleur(this, savedInstanceState);
	}
	
	/**
	 * au clic sur le bouton menu du téléphone => drawer
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e) {
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	((MainControleur)controleur).clicSurBoutonMenu();
	        return true;
	    }
	    return super.onKeyDown(keyCode, e);
	}
	
	public String getIdentifiant(){
		return ((MainControleur)controleur).getIdentifiant();
	}
	@Override
	public MyEpfPreferencesModele getPrefs() {
		return ((MainControleur)controleur).getPrefs();
	}
	
	/**
	 * pour le drawer
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
	    super.onPostCreate(savedInstanceState);
	    if(controleur != null)
	    	((MainControleur) controleur).onPostCreate(savedInstanceState);
	}
	/**
	 * pour le drawer
	 */
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(controleur != null)
        	((MainControleur) controleur).onConfigurationChanged(newConfig);
    }
	/**
	 * Menu et drawer
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (((MainVue)controleur.getVue()).getToggleBouton().onOptionsItemSelected(item)) {
	      return true;
	    }
		MainControleur controleur = (MainControleur)this.controleur;
		if (item.getItemId() == android.R.id.home) {
			controleur.ouvrirFermerDrawer();
		}
		
	    switch (item.getItemId()) {
		    case R.id.edt:
	            controleur.onEdtClicked();
	            return true;
		    case R.id.bulletin:
	            controleur.onBulletinClicked();
	            return true;
	        case R.id.preferences:
	            controleur.onPreferencesClicked();
	            return true;
	        case R.id.apropos:
	        	controleur.onAProposClicked();
	            return true;
	        case R.id.quitter:
	        	controleur.onQuitterClicked();
	            return true;
	        case android.R.id.home:
	        	controleur.ouvrirFermerDrawer();
	        	return true;
	        default:
	        	return super.onOptionsItemSelected(item);
	    }
	}
}
