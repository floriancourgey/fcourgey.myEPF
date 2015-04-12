package com.fcourgey.myepfnew.outils;

public class Android {
	public static void quitter(){
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
