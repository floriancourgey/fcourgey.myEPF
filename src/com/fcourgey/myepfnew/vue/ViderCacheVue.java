package com.fcourgey.myepfnew.vue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.fcourgey.myepfnew.R;

public class ViderCacheVue {
	
	@SuppressLint("InflateParams")
	public static void show(final Activity activite, DialogInterface.OnClickListener onClickListener){
		View v = activite.getLayoutInflater().inflate(R.layout.vider_cache, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(activite);
		builder	.setView(v)
		.setTitle(R.string.titre_cache)
		.setPositiveButton(R.string.ok, onClickListener)  
		.setNegativeButton(R.string.annuler, null);
		final AlertDialog d = builder.create();
		// show bouton ok seulement si plus de 3 caractères
		d.show();
	}
}
