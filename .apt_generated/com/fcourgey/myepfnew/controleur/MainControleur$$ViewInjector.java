// Generated code from Butter Knife. Do not modify!
package com.fcourgey.myepfnew.controleur;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class MainControleur$$ViewInjector<T extends com.fcourgey.myepfnew.controleur.MainControleur> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131099771, "field 'photo_profil'");
    target.photo_profil = finder.castView(view, 2131099771, "field 'photo_profil'");
  }

  @Override public void reset(T target) {
    target.photo_profil = null;
  }
}
