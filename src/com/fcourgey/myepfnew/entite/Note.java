package com.fcourgey.myepfnew.entite;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Note {
	private Module module;
	
	private Calendar date;
	
	private String type;
	
	private float note;
	private float coeff; 
	
	private float moyenne;
	
	public Note(Module m, Calendar date, String type, float note, float coeff, float moyenne) {
		super();
		this.module = m;
		this.date = date;
		this.type = type;
		this.note = note;
		this.coeff = coeff;
		this.moyenne = moyenne;
	}

	public Module getModule(){
		return module;
	}
	public Calendar getDate() {
		return date;
	}
	public String getType(){
		return type;
	}

	public float getNote() {
		return note;
	}

	public float getCoeff() {
		return coeff;
	}

	public float getMoyenne() {
		return moyenne;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		return "Note [date=" + sdf.format(date.getTime()) + ", note=" + note + ", coeff=" + coeff
				+ ", moyenne=" + moyenne + "]";
	}
}
