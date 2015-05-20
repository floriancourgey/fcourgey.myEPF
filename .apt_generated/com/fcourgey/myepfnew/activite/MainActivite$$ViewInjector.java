// Generated code from Butter Knife. Do not modify!
package com.fcourgey.myepfnew.activite;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.Injector;

public class MainActivite$$ViewInjector<T extends com.fcourgey.myepfnew.activite.MainActivite> implements Injector<T> {
  @Override public void inject(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131099783, "field 'pbConnexionMyEpf'");
    target.pbConnexionMyEpf = finder.castView(view, 2131099783, "field 'pbConnexionMyEpf'");
    view = finder.findRequiredView(source, 2131099782, "field 'wvCachee'");
    target.wvCachee = finder.castView(view, 2131099782, "field 'wvCachee'");
  }

  @Override public void reset(T target) {
    target.pbConnexionMyEpf = null;
    target.wvCachee = null;
  }
}
