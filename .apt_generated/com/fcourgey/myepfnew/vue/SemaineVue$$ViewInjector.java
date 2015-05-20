// Generated code from Butter Knife. Do not modify!
package com.fcourgey.myepfnew.vue;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class SemaineVue$$ViewInjector<T extends com.fcourgey.myepfnew.vue.SemaineVue> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131099787, "field 'tvTelechargement'");
    target.tvTelechargement = finder.castView(view, 2131099787, "field 'tvTelechargement'");
    view = finder.findRequiredView(source, 2131099788, "field 'pbTelechargement'");
    target.pbTelechargement = finder.castView(view, 2131099788, "field 'pbTelechargement'");
  }

  @Override public void reset(T target) {
    target.tvTelechargement = null;
    target.pbTelechargement = null;
  }
}
