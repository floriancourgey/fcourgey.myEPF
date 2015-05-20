// Generated code from Butter Knife. Do not modify!
package com.fcourgey.myepfnew.controleur;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class BulletinControleur$$ViewInjector<T extends com.fcourgey.myepfnew.controleur.BulletinControleur> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131099760, "field 'pbAvancementBulletin'");
    target.pbAvancementBulletin = finder.castView(view, 2131099760, "field 'pbAvancementBulletin'");
    view = finder.findRequiredView(source, 2131099761, "field 'tvAvancementBulletin'");
    target.tvAvancementBulletin = finder.castView(view, 2131099761, "field 'tvAvancementBulletin'");
  }

  @Override public void reset(T target) {
    target.pbAvancementBulletin = null;
    target.tvAvancementBulletin = null;
  }
}
