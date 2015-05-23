package com.fcourgey.myepfnew.outils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParserException;

import com.fcourgey.myepfnew.entite.Module;
import com.fcourgey.myepfnew.entite.Note;

public class XmlNoteMyEpf {
	public static final String ID_REPORT = "Report";
	public static final String ID_TABLIX2 = "Tablix2";
	public static final String ID_LISTE_ANNEES = "ETR_REFERENCE_PRODUIT_FC_Collection";
	public static final String ID_ANNEE = "ETR_REFERENCE_PRODUIT_FC";
	public static final String ID_LISTE_MODULES = "MODULE_Collection";
	public static final String ID_MODULE = "MODULE";
	public static final String KEY_MODULE_NOM = "MODULE";
	public static final String ID_LISTE_NOTES = "Détails_Collection";
	public static final String ID_NOTE = "Détails";
	public static final String KEY_NOTE_TYPE = "TYPE_DE_NOTE";
	public static final String KEY_NOTE_DATE = "DATE_CONTROLE";
	public static final String KEY_NOTE_NOTE = "NOTE";
	public static final String KEY_NOTE_COEFF = "COEF";
	public static final String KEY_NOTE_MOY = "MOYENNE_PROMO";

	public static ArrayList<Module> xmlToLModules(File xml) throws NullPointerException, FileNotFoundException, IOException, XmlPullParserException {
		if(xml==null){
			throw new NullPointerException("xml==null");
		}if(!xml.exists()){
			throw new FileNotFoundException("xml introuvable");
		}
		ArrayList<Module> lModules = new ArrayList<Module>();
		Document doc = Jsoup.parse(xml, "utf-8");
		if(doc.select(ID_MODULE) == null){
			throw new XmlPullParserException("Impossible de trouver le noeud "+ID_MODULE);
		}
	    for (Element module : doc.select(ID_MODULE)) {
//		    	System.out.println("MODULE "+module.attr(KEY_MODULE_NOM));
	    	String nomModule = module.attr(KEY_MODULE_NOM);
	    	Module m = new Module(nomModule);
	    	ArrayList<Note> lNotes = new ArrayList<Note>();
	    	for(Element note : module.select(ID_NOTE)){
//		    		System.out.println(note.attr(KEY_NOTE_TYPE)+" : "+note.attr(KEY_NOTE_NOTE));
	    		Calendar date = StringOutils.toCalendar(note.attr(KEY_NOTE_DATE), "dd/MM/yyyy");
	    		String type = note.attr(KEY_NOTE_TYPE);
	    		float valeur = readFloat(note, KEY_NOTE_NOTE);
	    		float coeff = readFloat(note, KEY_NOTE_COEFF);
	    		float moyenne = readFloat(note, KEY_NOTE_MOY);
	    		lNotes.add(new Note(m, date, type, valeur, coeff, moyenne));
	    	}
	    	m.setLNotes(lNotes);
	    	lModules.add(m);
	    }
		return lModules;
	}
	
	private static float readFloat(Element e, String key){
		float f;
		try{
			f = Float.parseFloat(e.attr(key).replace(",", "."));
		} catch (NumberFormatException nfe){
			f = 0;
		}
		return f;
	}
	
}
