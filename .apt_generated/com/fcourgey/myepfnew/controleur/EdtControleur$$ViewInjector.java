// Generated code from Butter Knife. Do not modify!
package com.fcourgey.myepfnew.controleur;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class EdtControleur$$ViewInjector<T extends com.fcourgey.myepfnew.controleur.EdtControleur> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131099778, "field 'accueil_pager'");
    target.accueil_pager = finder.castView(view, 2131099778, "field 'accueil_pager'");
  }

  @Override public void reset(T target) {
    target.accueil_pager = null;
  }
}
