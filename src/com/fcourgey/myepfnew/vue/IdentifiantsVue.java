package com.fcourgey.myepfnew.vue;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.AccueilActivite;

public class IdentifiantsVue {
	
	@SuppressLint("InflateParams")
	public static void show(final AccueilActivite activite){
		View v = activite.getLayoutInflater().inflate(R.layout.identifiants, null);
		final EditText etIdentifiant = (EditText)v.findViewById(R.id.etIdentifiant);
		final EditText etMdp = (EditText)v.findViewById(R.id.etMdp);
		AlertDialog.Builder builder = new AlertDialog.Builder(activite);
		builder	.setView(v)
		.setTitle(R.string.popup_identifiants_titre)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				activite.getPrefs().setLogin(etIdentifiant.getText().toString());
				activite.getPrefs().setMdp(etMdp.getText().toString());
				activite.lancerSemainesActivite();
				dialog.dismiss();
			}
		});  
		// show keyboard
		final AlertDialog d = builder.create();
		d.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		// show bouton ok seulement si plus de 3 caractères
		ListenerAfficherBoutonOK listener =new ListenerAfficherBoutonOK(d); 
		etIdentifiant.addTextChangedListener(listener);
		etMdp.addTextChangedListener(listener);
		d.show();
		d.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
	}
	
	private static class ListenerAfficherBoutonOK implements TextWatcher{
		private static AlertDialog d;
		public ListenerAfficherBoutonOK(AlertDialog pd){
			d = pd;
		}
		@Override public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {}
        @Override public void onTextChanged(CharSequence c, int i, int i2, int i3) {}
        @Override
        public void afterTextChanged(Editable editable) {
        	EditText etIdentifiant = (EditText) d.findViewById(R.id.etIdentifiant);
    		EditText etMdp = (EditText) d.findViewById(R.id.etMdp);
            if (etIdentifiant.getText().toString().length() < 3 || etMdp.getText().toString().length() < 3){
                d.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
            } else {
                d.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
            }
        }
	}
}
