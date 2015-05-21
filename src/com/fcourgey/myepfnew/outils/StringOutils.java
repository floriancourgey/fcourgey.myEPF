package com.fcourgey.myepfnew.outils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StringOutils {
	/**
	 * in :  2015-12-31T23:59 (2015-12-31 sans les heures si @param inclureHoraire false)
	 * out : un calendar hydraté avec ces infos
	 */
	public static Calendar toCalendar(String s, String format){
	    Calendar c = Calendar.getInstance();
	    SimpleDateFormat simpleDateFormat;
    	simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
	    s = s.replace("T", " "); // car T est invalide
	    try {
	    	c.setTime(simpleDateFormat.parse(s));
		} catch (ParseException e) {
//			 TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return c;
	}
}
