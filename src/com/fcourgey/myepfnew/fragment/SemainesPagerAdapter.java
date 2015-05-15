package com.fcourgey.myepfnew.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.fcourgey.myepfnew.activite.MainActivite;
import com.fcourgey.myepfnew.controleur.EdtControleur;
import com.fcourgey.myepfnew.controleur.SemaineControleur;

public class SemainesPagerAdapter extends FragmentPagerAdapter {
	
	private static SparseArray<Fragment> lFrags = new SparseArray<Fragment>();
	
	private static MainActivite mere;
	
	public static final int NOMBRE_DE_SEMAINES_MAX_DEFAUT = EdtControleur.semainesAvant + 1 + EdtControleur.semainesApres; // TODO
	public static int NOMBRE_DE_SEMAINES_MAX;

    public SemainesPagerAdapter(FragmentManager fm, MainActivite mere) {
        super(fm);
        SemainesPagerAdapter.mere = mere;
    	NOMBRE_DE_SEMAINES_MAX = mere.getPrefs().getNbSemainesToDl();
    }
    
    /**
	 * affiche/cache les CM pour tous les fragments
	 * 
	 * @param null : lit les param depuis les pref
	 * @param true/false : affiche/cache + enregistre dans les pref
	 */
	public static void definirCm(Boolean actif){
		/*
		if(actif == null){
			actif = mere.getPrefs().getCm();
		} else {
			mere.getPrefs().setCm(actif);
		}
		for(int i=0 ; i<NOMBRE_DE_SEMAINES_MAX ; i++){
			SemaineFragment frag = (SemaineFragment) lFrags.get(i);
			SemaineControleur controleur = (SemaineControleur)frag.getControleur();
			if(frag == null || controleur.getLCoursVues() == null){
				continue;
			}
			// set checked
			if(frag.getView() != null)
				((Switch)frag.getView().findViewById(R.id.sCm)).setChecked(actif);
			// afficher/cacher cours (si CM && si pas de devoirs
			for(CoursVue cv : controleur.getLCoursVues()){
				if(cv.getCours().isCm() && cv.getCours().getDevoirs()==null){
					int visibility = (actif)?View.VISIBLE:View.GONE;
					cv.setVisibility(visibility);
				}
			}
			// update prochain site
//			controleur.updateProchainSite();
		}
		*/
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
    	for(int i=0 ; i<NOMBRE_DE_SEMAINES_MAX ; i++){
			SemaineFragment frag = (SemaineFragment) lFrags.get(i);
			if(frag == null){
				continue;
			}
			SemaineControleur controleur = (SemaineControleur)frag.getControleur();
			controleur.avancement(texte, pourcentage, true);
    	}
	}

	public void onMyEPFConnected() {
		for(int i=0 ; i<NOMBRE_DE_SEMAINES_MAX ; i++){
			SemaineFragment frag = (SemaineFragment) lFrags.get(i);
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
