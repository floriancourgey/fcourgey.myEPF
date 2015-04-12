package com.fcourgey.myepfnew.outils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fcourgey.myepfnew.activite.MainActivite;

@SuppressWarnings("deprecation")
public class MyEPFWebview  {
	
	private WebView wvCachee;
	
	private MainActivite a;
	private String premiereURL;
	private WebViewClient webViewClient;
	
	@SuppressLint("NewApi")
	public MyEPFWebview(MainActivite a, int idVueWebview, final String premiereURL, WebViewClient webViewClient){
		wvCachee = (WebView)a.findViewById(idVueWebview);
		
		initWebSettings();
		initCookies();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		      WebView.setWebContentsDebuggingEnabled(true);
		}
		wvCachee.setWebChromeClient(new WebChromeClient());
	}
	
	public void run(){
		a.runOnUiThread(new Runnable() {
			public void run() {
				wvCachee.setWebViewClient(webViewClient);
				wvCachee.loadUrl(premiereURL);				
			}
		});
	}
	
	/**
	 * initialise les cookies
	 */
	private void initCookies(){
		CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(wvCachee.getContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		cookieManager.setAcceptCookie(true);
		cookieSyncManager.sync();
	}

	/**
	 * initalise la webview
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebSettings(){
		WebSettings webSettings = wvCachee.getSettings();
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:35.0) Gecko/20100101 Firefox/35.0 Waterfox/35.0");
	}

}
