package com.fcourgey.myepfnew.controleur;

import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.android.mylib.framework.FragmentControleur;
import com.fcourgey.myepfnew.vue.AProposVue;

public class AProposControleur extends FragmentControleur {

	public AProposControleur(Fragment f, LayoutInflater inflater, ViewGroup container) {
		super(f, inflater, container);
		
		String versionName;
		try {
			versionName = getActivite().getPackageManager().getPackageInfo(getActivite().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "?";
		}
		
		vue = new AProposVue(this, container, versionName);
	}

}
