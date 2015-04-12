package com.fcourgey.myepfnew.entite;

import java.util.Calendar;
import java.util.Locale;

import android.util.Log;

/**
 * Created by helene on 26/02/2015.
 */
public class Cours {
    
    public enum Type{
        TD,
        CM,
        TP,
        AUTRE,
        EXAM
    }
    

    private String nom;
    private String professeur;
    private String salle;
    private String site;
    private Type type;
    private String commentaire;

    private String devoirs;
    private String dateId; // date de début de cours, sert d'id : "2014-10-09T10:00"
    
    private Calendar horaireDebut; 
    private Calendar horaireFin;

    public Cours(String dateId, String nom, String professeur, String salle, Calendar horaireDebut, Calendar horaireFin, Type type, String commentaire, String devoir) {
    	this.dateId = dateId;
        this.nom = nom;
        this.professeur = professeur;
        this.salle = salle;
        this.devoirs = devoir;
        // correction du type (bug 10)
        String l = (nom+commentaire).toLowerCase(Locale.getDefault()); 
        if(l.contains("interro") || l.contains("rattrapage") || l.contains("exam") || l.contains("partiel")){
        	this.type = Type.EXAM;
        } else if(l.contains("présentation") || l.contains("réunion")){
        	this.type = Type.AUTRE;
        } else {
        	this.type = type;
        }
        
        this.commentaire = commentaire;
        if(salle.contains("Amphi") || salle.endsWith("L") || (salle.length()==2 && salle.charAt(0)=='I')){
        	site = "lakanal";
        } else if(salle.endsWith("T") || salle.toLowerCase(Locale.getDefault()).contains("labo") || salle.toLowerCase(Locale.getDefault()).contains("trévise")){
        	site = "trévise";
        } else if(salle.contains("P")){
        	site = "poinca";
        } else {
        	Log.w("myepf.entite.Cours", "salle sans site : "+salle);
        }
        this.horaireDebut = horaireDebut;
        this.horaireFin = horaireFin;
    }
    
    public String getDevoirs(){
    	return devoirs;
    }
    public String getDateId(){
    	return dateId;
    }
    /**
     * retourne vrai si CM
     */
    public boolean isCm(){
    	return (type==Type.CM);
    }
    
    /**
     * toString
     */
    public String toString(){
        return getHoraireDebut()+"->"+ getHoraireFin()+" "+nom+" avec "+professeur+" en "+salle+"("+site+")";
    }

    public String getNom() {
        return nom;
    }
    /**
     * Nom spécial si pas de prof (Exam, présentation, ...)
     * @return
     */
    public String getNomForCoursVue(){
    	// si c'est un exam, j'affiche le commentaire custom
    	if(type == Type.EXAM){ 
    		return commentaire.replace(" de", "");
    	} else if(professeur != null && professeur.length() > 1){
    		return nom;
    	} else {
    		return commentaire;
    	}
    }

    public Calendar getCalendar(){
    	return horaireDebut;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getProfesseur() {
        return professeur;
    }

    public void setProfesseur(String professeur) {
        this.professeur = professeur;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String lieu) {
        this.site = lieu;
    }

    public String getHoraireDebut() {
    	if(horaireDebut == null){
    		return "";
    	}
        return horaireDebut.get(Calendar.HOUR_OF_DAY)+":"+getMinutesDebutString();
    }
    
    public int getHeureDebut(){
    	return horaireDebut.get(Calendar.HOUR_OF_DAY);
    }
    public String getMinutesDebutString(){
    	Integer i = horaireDebut.get(Calendar.MINUTE);
    	String s = i.toString();
    	if(s.length() == 1){
    		s = "0"+s;
    	}
    	return s;
    }
    public int getMinutesDebutInt(){
    	return horaireDebut.get(Calendar.MINUTE);
    }
    
    public int getHeureFin(){
    	return horaireFin.get(Calendar.HOUR_OF_DAY);
    }
    public String getMinutesFinString(){
    	Integer i = horaireFin.get(Calendar.MINUTE);
    	String s = i.toString();
    	if(s.length() == 1){
    		s = "0"+s;
    	}
    	return s;
    }
    public int getMinutesFinInt(){
    	return horaireFin.get(Calendar.MINUTE);
    }

    public void setHoraireDebut(Calendar horaireDebut) {
        this.horaireDebut = horaireDebut;
    }

    public String getHoraireFin() {
    	if(horaireFin == null){
    		return "";
    	}
        return horaireFin.get(Calendar.HOUR_OF_DAY)+":"+getMinutesFinString();
    }

    public void setHoraireFin(Calendar horaireFin) {
        this.horaireFin = horaireFin;
    }

	public Cours.Type getType() {
		return type;
	}
	public String getCommentaire(){
		return commentaire;
	}
	/**
	 * toString formaté pour la Popup
	 * @return
	 */
	public String toStringForPopup() {
		String s = "";
		s += nom+" ("+type+")\n";
		s += "De "+getHoraireDebut()+" à "+getHoraireFin()+"\n";
		if(professeur != null && professeur.length() > 1){
			s += "Avec "+professeur+"\n";
		}
		if(salle != null && salle.length()>0){
			s += "En "+salle+" ("+site+")"+"\n";
		}
		if(commentaire != null && commentaire.length() > 1){
			s += "Commentaire : "+commentaire+"\n";
		}
		return s;
	}

	public void setDevoirs(String devoirs2) {
		this.devoirs = devoirs2;
	}
}
