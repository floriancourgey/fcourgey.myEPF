package com.fcourgey.myepfnew.entite;

import java.util.ArrayList;

public class Module {
	private String nom;
	private ArrayList<Note> lNotes;
	public Module(String nom) {
		super();
		this.nom = nom;
	}
	public String getNom() {
		return nom;
	}
	public void setLNotes(ArrayList<Note> lNotes){
		this.lNotes = lNotes;
	}
	public ArrayList<Note> getlNotes() {
		return lNotes;
	}
	@Override
	public String toString() {
		String s = "";
		s += "Module "+nom;
		if(lNotes != null){
			for(Note note : lNotes){
				s += "\n\t"+note;
			}
		}
		return s;
	}
	
}
