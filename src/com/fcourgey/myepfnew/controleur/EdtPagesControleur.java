package com.fcourgey.myepfnew.controleur;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.fragment.SemaineFragment;
import com.fcourgey.myepfnew.vue.CoursVue;
import com.fcourgey.myepfnew.vue.SemaineVue;

public class EdtPagesControleur extends FragmentPagerAdapter {
	
	private static final String TAG = "SemainesPagerAdapter";
	
	private static SparseArray<Fragment> lFrags = new SparseArray<Fragment>();
	
	private static MainActivite mere;
	
	public static final int NOMBRE_DE_SEMAINES_MAX_DEFAUT = EdtControleur.semainesAvant + 1 + EdtControleur.semainesApres; // TODO

	
	public static int NOMBRE_DE_SEMAINES_MAX;

    public EdtPagesControleur(FragmentManager fm, MainActivite mere) {
        super(fm);
        EdtPagesControleur.mere = mere;
    	NOMBRE_DE_SEMAINES_MAX = mere.getPrefs().getNbSemainesToDl();
    }
    
    /**
	 * affiche/cache les CM pour tous les fragments
	 * 
	 * @param null : lit les param depuis les pref
	 * @param true/false : affiche/cache + enregistre dans les pref
	 */
	public static void definirCm(Boolean actif){
		if(actif == null){
			actif = mere.getPrefs().getCm();
		} else {
			mere.getPrefs().setCm(actif);
		}
		for(int i=0 ; i<NOMBRE_DE_SEMAINES_MAX ; i++){
			SemaineFragment frag = (SemaineFragment) lFrags.get(i);
			if(frag == null){
				continue;
			}
			// envoi à la vue
			SemaineControleur controleur = (SemaineControleur)frag.getControleur();
			SemaineVue vue = (SemaineVue)controleur.getVue();
			vue.definirCm(actif);
			
			// update prochain site
			controleur.updateProchainSite();
		}
	}
	
    @Override
    public Fragment getItem(int index) {
    	return SemaineFragment.newInstance(index);
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int indexFragment) {
        Fragment fragment = (Fragment) super.instantiateItem(container, indexFragment);
        lFrags.put(indexFragment, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        lFrags.remove(position);
        super.destroyItem(container, position, object);
    }
 
    @Override
    public int getCount() {
        return NOMBRE_DE_SEMAINES_MAX;
    }

	
    /**
     * appelle l'avancement sur chaque frag
     */
    public void avancement(String texte, int pourcentage) {
    	Log.i("SemainesPagerAdapter", "Avancement "+pourcentage+" : "+texte);
    	for(int i=EdtControleur.semainesAvant ; i<NOMBRE_DE_SEMAINES_MAX+EdtControleur.semainesAvant ; i++){
			int index = i%(NOMBRE_DE_SEMAINES_MAX);
			SemaineFragment frag = (SemaineFragment) lFrags.get(index);
			if(frag == null){
				continue;
			}
			SemaineControleur controleur = (SemaineControleur)frag.getControleur();
			controleur.avancement(texte, pourcentage);
    	}
	}

	public void onMyEPFConnected() {
		for(int i=EdtControleur.semainesAvant ; i<NOMBRE_DE_SEMAINES_MAX+EdtControleur.semainesAvant ; i++){
			int index = i%(NOMBRE_DE_SEMAINES_MAX);
			SemaineFragment frag = (SemaineFragment) lFrags.get(index);
			if(frag == null){
				continue;
			}
			((SemaineControleur)frag.getControleur()).onMyEPFConnected();
		}
	}
	
	public void onMapCoursMapped(){
		for(int i=0 ; i<NOMBRE_DE_SEMAINES_MAX ; i++){
			SemaineFragment frag = (SemaineFragment) lFrags.get(i);
			if(frag == null){
				continue;
			}
			((SemaineControleur)frag.getControleur()).onMapCoursMapped();
		}
	}
 
}
