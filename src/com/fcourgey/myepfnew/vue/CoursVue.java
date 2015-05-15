package com.fcourgey.myepfnew.vue;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.entite.Cours;
import com.fcourgey.myepfnew.entite.Matiere;
import com.fcourgey.myepfnew.modele.DbModele;
import com.fcourgey.myepfnew.modele.DbModele.Colonne;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;

public class CoursVue extends Button {
	
	private EditText etDevoirs;
	
	private Cours cours;

	@SuppressLint("InflateParams")
	public CoursVue(final Context context, final Cours cours) {
		super(context, null);
		this.cours = cours;
		
		setTextForDevoirs();
		initCouleur(context);
		
		setPadding(0, 0, 0, 0);
		
		this.setOnClickListener(new OnClickListener() {
			public void onClick(View vue) {
				final View v = (LayoutInflater.from(context)).inflate(R.layout.cours_popup, null);
				TextView tvNom = (TextView)v.findViewById(R.id.tvNom);
				tvNom.setText(cours.getNom()+" ("+cours.getType()+")");
				TextView tvHoraire = (TextView)v.findViewById(R.id.tvHoraire);
				tvHoraire.setText(cours.getHoraireDebut()+" > "+cours.getHoraireFin()+" ("+cours.getCalendar().get(Calendar.DAY_OF_MONTH)+"."+(cours.getCalendar().get(Calendar.MONTH)+1)+"."+cours.getCalendar().get(Calendar.YEAR)+")");
				TextView tvLieu = (TextView)v.findViewById(R.id.tvLieu);
				tvLieu.setText(cours.getSalle()+" ("+cours.getSite()+")");
				TextView tvProf = (TextView)v.findViewById(R.id.tvProf);
				tvProf.setText(cours.getProfesseur());
				TextView tvExtra = (TextView)v.findViewById(R.id.tvExtra);
				if(cours.getCommentaire() != null && cours.getCommentaire().length()>1){
					tvExtra.setText(cours.getCommentaire());
				} else {
					(v.findViewById(R.id.tvLabelExtra)).setVisibility(View.GONE);
					tvExtra.setVisibility(View.GONE);
				}
				etDevoirs = (EditText)v.findViewById(R.id.etDEvoirs);
				etDevoirs.setText(cours.getDevoirs());
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setView(v)
				.setTitle(cours.getNom())
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						DbModele modele = new DbModele(context);
						String devoirs = etDevoirs.getText().toString();
						if(devoirs != null && devoirs.length()>0){
							ContentValues values = new ContentValues();
							values.put(Colonne.DATE_ID, cours.getDateId());
							values.put(Colonne.DEVOIRS, devoirs);
							long lastId = modele.inserer(values);
							if(lastId > 0){
								Log.i("CoursVue", "devoir bien inséré (id "+lastId+")");
								cours.setDevoirs(devoirs);
							} else {
								Log.e("CoursVue", "IMPOSSIBLE D'INSERER LE DEVOIR");
							}
						} else {
							modele.supprimer(cours.getDateId());
							cours.setDevoirs(null);
						}
						CoursVue.this.setTextForDevoirs();
						initCouleur(context);
					}
				})

				.setNegativeButton(R.string.annuler, null)
				.create()
				.show();
			}
		});
	}
	
	private void setTextForDevoirs(){
		if(cours.getDevoirs() != null && cours.getDevoirs().length()>1){
			setText(cours.getNomForCoursVue()+" - "+cours.getDevoirs());
		} else {
			setText(cours.getNomForCoursVue());		
		}
	}
	
	private void initCouleur(Context context){
		MyEpfPreferencesModele modele = null;
		try {
			modele = new MyEpfPreferencesModele();
		} catch (Exception e) {
			e.printStackTrace(); // TODO prendre les valeurs par défaut
			return;
		}
		int couleurFond=0;
		int couleurTexte=0;
		boolean couleurDepuisPref = true;
		switch(cours.getType()){
		case CM :
			couleurFond = modele.getInt(MyEpfPreferencesModele.KEY_C_CM, 0);
			if(couleurFond == 0){
				couleurDepuisPref = false;
				couleurFond = R.color.fond_cm;
			}
			couleurTexte = R.color.texte_cm;
			break;
		case TD :
			couleurFond = modele.getInt(MyEpfPreferencesModele.KEY_C_TD, 0);
			if(couleurFond == 0){
				couleurDepuisPref = false;
				couleurFond = R.color.fond_td;
			}
			couleurTexte = R.color.texte_td;
			break;
		case TP :
			couleurFond = modele.getInt(MyEpfPreferencesModele.KEY_C_TP, 0);
			if(couleurFond == 0){
				couleurDepuisPref = false;
				couleurFond = R.color.fond_tp;
			}
			couleurTexte = R.color.texte_tp;
			break;
		case EXAM :
			couleurFond = modele.getInt(MyEpfPreferencesModele.KEY_C_EXAM, 0);
			if(couleurFond == 0){
				couleurDepuisPref = false;
				couleurFond = R.color.fond_exam;
			}
			couleurTexte = R.color.texte_exam;
			break;
		case AUTRE :
			couleurFond = modele.getInt(MyEpfPreferencesModele.KEY_C_AUTRES, 0);
			if(couleurFond == 0){
				couleurDepuisPref = false;
				couleurFond = R.color.fond_autre;
			}
			couleurTexte = R.color.texte_autre;
			break;
		}
		if(cours.getDevoirs() != null && cours.getDevoirs().length()>1){
			couleurFond = modele.getInt(MyEpfPreferencesModele.KEY_C_DEVOIRS, 0);
			if(couleurFond == 0){
				couleurDepuisPref = false;
				couleurFond = R.color.fond_devoirs;
			}
			couleurTexte = R.color.texte_devoirs;
		}
		try{
			if(couleurDepuisPref){
				setBackgroundColor(couleurFond);
			} else {
				setBackgroundColor(context.getResources().getColor(couleurFond));
			}
			setTextColor(context.getResources().getColor(couleurTexte));
			
			
		} catch(Exception e){}
	}

	public Cours getCours(){
		return cours;
	}
}
