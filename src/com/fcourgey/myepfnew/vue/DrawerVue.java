package com.fcourgey.myepfnew.vue;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.controleur.DrawerControleur;

@SuppressWarnings("deprecation")
public class DrawerVue {
	
	@SuppressWarnings("unused")
	private static final String TAG = "DrawerVue"; 
	
	private MainActivite a;
	private DrawerControleur controleur;
	
	private DrawerLayout layoutGeneral; // contient la vue principale et la vue du drawer
	private LinearLayout vue; //vue du drawer
	private ActionBarDrawerToggle toggleBouton; // bouton toggle en haut à gauche dans l'action bar
	private ListView lTitres; // listView des titres sur la gauche : Edt, bulletin, ...
	
	public DrawerVue(DrawerControleur controleur, String identifiant) {
		this.controleur = controleur;
		this.a = controleur.getActivite();
		initDrawer();
		initToggleButton();
		initContenu(identifiant);
	}
	
	private void initDrawer(){
		layoutGeneral = (DrawerLayout) a.findViewById(R.id.drawer_layout);
		vue = (LinearLayout) a.findViewById(R.id.drawer);
		
		// Set a custom shadow that overlays the main content when the drawer opens
		layoutGeneral.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	}
	
	private void initToggleButton(){
		toggleBouton = new ActionBarDrawerToggle(a, layoutGeneral, R.drawable.ic_drawer, 0, 0) {
            /** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
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
        // init titres
        lTitres = (ListView) a.findViewById(R.id.lTitres);
        String[] titres = a.getResources().getStringArray(R.array.titres);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(a, R.layout.drawer_list_item, android.R.id.text1, titres);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(a, android.R.layout.simple_list_item_activated_1, titres);
        lTitres.setAdapter(adapter);
        lTitres.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view,
        			int position, long id) {
        		controleur.onTitresClicked(parent, view,
        				position, id, lTitres);
        	}
        });
        lTitres.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
