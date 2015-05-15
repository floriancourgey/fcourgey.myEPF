package com.fcourgey.myepfnew.entite;


public abstract class MyEpfUrl {
	
	// my.epf.fr
	/// connexion
	public static final String MY_EPF = "https://my.epf.fr/";
	public static final String LOGIN_REQUETE = MY_EPF;
	public static final String LOGIN_RESULTAT = "InternalSite/Login.asp";
	public static final String ACCUEIL_RESULTAT = MY_EPF+"default.aspx";
	/// edt
	public static final String EDT_REQUETE = MY_EPF+"_layouts/crypt/generer_cle.aspx?service=planning";
	/// bulletin
	public static final String BULLETIN_GENERATEUR = MY_EPF+"/_layouts/sharepointproject2/redirectionversbulletin.aspx";
	public static String BULLETIN = MY_EPF+"parcoursscolaire/_layouts/SharePointProject2/AffichageBulletin.aspx?ANNEE={ANNEE}&LOGIN_RESEAU={LOGIN}";
	
	// mydata.epf.fr
	/// edt
	public static final String MYDATA = "https://mydata.epf.fr/";
	public static final String EDT_RESULTAT = MYDATA;
	public static final String EDT_JSON = MYDATA+"pegasus/index.php?com=planning&job=get-cours"; // toutes les semaines
	public static final String EDT_JSON_KEY_DIRECTION = "direction";
	public static final String EDT_JSON_VALUE_NONE = "none"; // semaine actuelle
	public static final String EDT_JSON_VALUE_NEXT = "next"; // semaine suivante
	public static final String EDT_JSON_VALUE_PREVIOUS = "previous"; // semaine précédente
	/// profil
	public static final String PROFIL = MYDATA+"pegasus/index.php?com=tracking&job=tracking-etudiant";
	
	
}
