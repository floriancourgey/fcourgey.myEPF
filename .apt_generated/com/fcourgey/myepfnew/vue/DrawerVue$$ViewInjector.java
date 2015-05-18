// Generated code from Butter Knife. Do not modify!
package com.fcourgey.myepfnew.vue;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class DrawerVue$$ViewInjector<T extends com.fcourgey.myepfnew.vue.DrawerVue> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131099774, "field 'lTitres'");
    target.lTitres = finder.castView(view, 2131099774, "field 'lTitres'");
  }

  @Override public void reset(T target) {
    target.lTitres = null;
  }
}
