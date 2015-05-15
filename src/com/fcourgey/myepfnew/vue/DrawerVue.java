package com.fcourgey.myepfnew.vue;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.Activite;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.controleur.MainControleur;
import com.joanzapata.android.iconify.Iconify;

@SuppressWarnings("deprecation")
public class DrawerVue {
	
	@SuppressWarnings("unused")
	private static final String TAG = "DrawerVue"; 
	
	private MainActivite a;
	private MainControleur controleur;
	
	private DrawerLayout layoutGeneral; // contient la vue principale et la vue du drawer
	private LinearLayout vue; //vue du drawer
	private ActionBarDrawerToggle toggleBouton; // bouton toggle en haut à gauche dans l'action bar
	private ListView lTitres; // listView des titres sur la gauche : Edt, bulletin, ...
	
	public DrawerVue(MainControleur controleur, String identifiant) {
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
		android.support.v7.app.ActionBar ab = a.getSupportActionBar();
		
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE 
		        | ActionBar.DISPLAY_SHOW_HOME 
		        | ActionBar.DISPLAY_HOME_AS_UP);
		
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

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
	}
	
	private void initContenu(String identifiant) {
        // init textview
        ((TextView)a.findViewById(R.id.nom_profil)).setText(identifiant);
        // init titres
        lTitres = (ListView) a.findViewById(R.id.lTitres);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(a, R.layout.drawer_list_item, android.R.id.text1, titres);
        DrawerAdapter adapter = new DrawerAdapter(a);
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

	
	private class DrawerAdapter extends ArrayAdapter<String>{
		private final Activite a;
		private final String[] titres;
		private final String[] logos;
		
		public DrawerAdapter(Activite a){
			super(a, R.layout.drawer_list_item);
			titres = a.getResources().getStringArray(R.array.titres);
			logos = a.getResources().getStringArray(R.array.logos);
			this.a = a;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater) a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.drawer_list_item, parent, false);
			}
			
			
			TextView tvTitre = (TextView) convertView.findViewById(R.id.tvTitre);
			tvTitre.setText(titres[position]);
			TextView tvLogo = (TextView) convertView.findViewById(R.id.tvLogo);
			tvLogo.setText("{fa-"+logos[position]+"}");
			Iconify.addIcons(tvLogo);

			return convertView;
		}
		
		@Override
        public int getCount() {
            return titres.length;
        }

		@Override
		public String getItem(int position) {
			return titres[position];
		}
	}
}
