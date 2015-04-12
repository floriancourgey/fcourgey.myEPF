package com.fcourgey.myepfnew.vue;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.MainActivite;

@SuppressWarnings("deprecation")
public class DrawerVue {
	
	private static final String TAG = "DrawerVue"; 
	
	private MainActivite a;
	
	private DrawerLayout layoutGeneral; // contient la vue principale et la vue du drawer
	private LinearLayout vue; //vue du drawer
	private ActionBarDrawerToggle toggleBouton; // bouton toggle en haut à gauche dans l'action bar
	
	public DrawerVue(MainActivite a, String identifiant) {
		this.a = a;
		initDrawer();
		initToggleButton();
		initContenu(identifiant);
	}
	
	private void initDrawer(){
		layoutGeneral = (DrawerLayout) a.findViewById(R.id.drawer_layout);
		vue = (LinearLayout) a.findViewById(R.id.drawer);
	}
	
	private void initToggleButton(){
		toggleBouton = new ActionBarDrawerToggle(a, layoutGeneral, R.drawable.ic_drawer, 0, 0) {
            /** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Log.i(TAG, "fermeture");
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                vue.bringToFront();
                Log.i(TAG, "ouverture");
            }
        };
        // Set the drawer toggle as the DrawerListener
        layoutGeneral.setDrawerListener(toggleBouton);

        a.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        a.getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	private void initContenu(String identifiant) {
        // init textview
        ((TextView)a.findViewById(R.id.nom_profil)).setText(identifiant);
	}

	public DrawerLayout getLayoutGeneral() {
		return layoutGeneral;
	}

	public LinearLayout getVue() {
		return vue;
	}

	public ActionBarDrawerToggle getToggleBouton() {
		return toggleBouton;
	}
	
}
