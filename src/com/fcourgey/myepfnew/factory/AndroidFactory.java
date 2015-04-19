package com.fcourgey.myepfnew.factory;

import android.app.Activity;
import android.app.AlertDialog;

import com.fcourgey.myepfnew.R;

public class AndroidFactory {
	public static void showOkPopup(Activity a, String titre, String message){
		// init popup
		AlertDialog.Builder builder = new AlertDialog.Builder(a);
    	builder.setMessage(message)
    	       .setTitle(titre)
    	       .setPositiveButton(a.getResources().getString(R.string.ok), null);
        // lancement popup
        builder.create().show();
	}
}
