package com.fcourgey.myepfnew.entite;

import java.util.Locale;


public class Matiere {
    public static String abre(String pMatiere) {
    	String matiere = pMatiere.toLowerCase(Locale.getDefault());
        if (matiere.contains("anglais".toLowerCase(Locale.getDefault()))) {
            return "LV1";
        }
        if (matiere.contains("espagnol".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("allemand".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("Algèbre".toLowerCase(Locale.getDefault()))) {
            return "Alg.";
        }
        if (matiere.contains("biochimie".toLowerCase(Locale.getDefault()))) {
            return "Biochimie";
        }
        if (matiere.contains("chimie".toLowerCase(Locale.getDefault()))) {
            return "Chimie";
        }
        if (matiere.contains("Convertisseurs Electriques".toLowerCase(Locale.getDefault()))) {
            return "Conv Elec";
        }
        if (matiere.contains("commande des systèmes".toLowerCase(Locale.getDefault()))) {
            return "CdS";
        }
        if (matiere.contains("transferts thermiques".toLowerCase(Locale.getDefault()))) {
            return "Tr Therm";
        }
        if (matiere.contains("cao") || matiere.contains("catia".toLowerCase(Locale.getDefault()))) {
            return "CATIA";
        }
        if (matiere.contains("ingénierie sy".toLowerCase(Locale.getDefault()))) {
            return "IIS";
        }
        if (matiere.contains("ingénierie bio".toLowerCase(Locale.getDefault()))) {
            return "Ingé Bio";
        }
        if (matiere.contains("thermo".toLowerCase(Locale.getDefault()))) {
            return "Thermo";
        }
        if (matiere.contains("propriété intellectuelle".toLowerCase(Locale.getDefault()))) {
            return "PI";
        }
        if (matiere.contains("technologie des systèmes".toLowerCase(Locale.getDefault()))) {
            return "TSA";
        }
        if (matiere.contains("systèmes d'information".toLowerCase(Locale.getDefault()))) {
            return "SI";
        }
        if (matiere.contains("ingénierie système / managemen".toLowerCase(Locale.getDefault()))) {
            return "ISM";
        }
        if (matiere.contains("systems engineering and projec".toLowerCase(Locale.getDefault()))) {
            return "SEP";
        }
        if (matiere.contains("Matériels Mobiles, Systèmes et".toLowerCase(Locale.getDefault()))) {
            return "MMS";
        }
        if (matiere.contains("Serious Games en Médecine (Tec".toLowerCase(Locale.getDefault()))) {
            return "SGM";
        }
        if (matiere.contains("Ingénierie du Traitement Stati".toLowerCase(Locale.getDefault()))) {
            return "ITS";
        }
        if (matiere.contains("L'informatique chez Airbus".toLowerCase(Locale.getDefault()))) {
            return "Airbus";
        }
        if (matiere.contains("Geopolitics and Country Risks'".toLowerCase(Locale.getDefault()))) {
            return "GCR";
        }
        if (matiere.contains("Gestion de Projet dans l'Unive".toLowerCase(Locale.getDefault()))) {
            return "GPU";
        }
        if (matiere.contains("L'AMOA en Mode Projet".toLowerCase(Locale.getDefault()))) {
            return "AMOA";
        }
        if (matiere.contains("Sécurité du S.I.".toLowerCase(Locale.getDefault()))) {
            return "Sécu SI";
        }
        if (matiere.contains("Analyse 2".toLowerCase(Locale.getDefault()))) {
            return "Ana.";
        }
        if (matiere.contains("Allemand 2".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("Ana.".toLowerCase(Locale.getDefault()))) {
            return "Analyse";
        }
        if (matiere.contains("Commandes des Systèmes 2".toLowerCase(Locale.getDefault()))) {
            return "CdS";
        }
        if (matiere.contains("Techniques Calculatoires 2".toLowerCase(Locale.getDefault()))) {
            return "TC";
        }
        if (matiere.contains("Techniques Calculatoires 1".toLowerCase(Locale.getDefault()))) {
            return "TC";
        }
        if (matiere.contains("Projet Sciences de l'Ingénieur".toLowerCase(Locale.getDefault()))) {
            return "PSI";
        }
        if (matiere.contains("Advanced Excel / VBA".toLowerCase(Locale.getDefault()))) {
            return "VBA";
        }
        if (matiere.contains("Cost Drivers".toLowerCase(Locale.getDefault()))) {
            return "CD";
        }
        if (matiere.contains("Commandes des Systèmes 1".toLowerCase(Locale.getDefault()))) {
            return "CdS";
        }
        if (matiere.contains("Mécanique des Fluides".toLowerCase(Locale.getDefault()))) {
            return "Méca Flu";
        }
        if (matiere.contains("Examens 2ème Semestre".toLowerCase(Locale.getDefault()))) {
            return "Exam";
        }
        if (matiere.contains("Examens 1er Semestre".toLowerCase(Locale.getDefault()))) {
            return "Exam";
        }
        if (matiere.contains("Electricité Générale 2".toLowerCase(Locale.getDefault()))) {
            return "Elec G";
        }
        if (matiere.contains("Electricité Générale 1".toLowerCase(Locale.getDefault()))) {
            return "Elec G";
        }
        if (matiere.contains("Thermodynamique".toLowerCase(Locale.getDefault()))) {
            return "Thermo";
        }
        if (matiere.contains("Conception et Programmation Ob".toLowerCase(Locale.getDefault()))) {
            return "CPO";
        }
        if (matiere.contains("Chimie Avancée".toLowerCase(Locale.getDefault()))) {
            return "Chimie";
        }
        if (matiere.contains("Droit des Sociétés".toLowerCase(Locale.getDefault()))) {
            return "Droit";
        }
        if (matiere.contains("Projet Présentation Publique 2".toLowerCase(Locale.getDefault()))) {
            return "PPP";
        }
        if (matiere.contains("Travaux Pratiques Physique 2".toLowerCase(Locale.getDefault()))) {
            return "TP";
        }
        if (matiere.contains("TP Physique 2".toLowerCase(Locale.getDefault()))) {
            return "TP";
        }
        if (matiere.contains("Systèmes de Transmission".toLowerCase(Locale.getDefault()))) {
            return "SdT";
        }
        if (matiere.contains("Chaîne de Mesure et TDS".toLowerCase(Locale.getDefault()))) {
            return "CdM TDS";
        }
        if (matiere.contains("Probabilitès et Applications".toLowerCase(Locale.getDefault()))) {
            return "Proba";
        }
        if (matiere.contains("Projet Personnel Associatif 2".toLowerCase(Locale.getDefault()))) {
            return "PPA";
        }
        if (matiere.contains("Modélisation Pollution Acciden".toLowerCase(Locale.getDefault()))) {
            return "MPA";
        }
        if (matiere.contains("Systèmes d'Informations Géogra".toLowerCase(Locale.getDefault()))) {
            return "SIG";
        }
        if (matiere.contains("Géologie, Hydrogéologie, Hydro".toLowerCase(Locale.getDefault()))) {
            return "GHH";
        }
        if (matiere.contains("Examens 2ème Semestre".toLowerCase(Locale.getDefault()))) {
            return "Exam";
        }
        if (matiere.contains("Territoires et Stratégies".toLowerCase(Locale.getDefault()))) {
            return "TeS";
        }
        if (matiere.contains("Gestion Economique et Financiè".toLowerCase(Locale.getDefault()))) {
            return "GEF";
        }
        if (matiere.contains("Projet Personnel Encadrè 2".toLowerCase(Locale.getDefault()))) {
            return "PPE";
        }
        if (matiere.contains("Algèbre 2".toLowerCase(Locale.getDefault()))) {
            return "Algèbre";
        }
        if (matiere.contains("Algorithmique 2".toLowerCase(Locale.getDefault()))) {
            return "Algo";
        }
        if (matiere.contains("Mathématiques")||matiere.contains("Abstraction".toLowerCase(Locale.getDefault()))) {
            return "Maths";
        }
        if (matiere.contains("Projet Présentation Publique 1".toLowerCase(Locale.getDefault()))) {
            return "PPP";
        }
        if (matiere.contains("Travaux Pratiques Physique 1".toLowerCase(Locale.getDefault()))) {
            return "TP";
        }
        if (matiere.contains("Projet Personnel Associatif 1".toLowerCase(Locale.getDefault()))) {
            return "PPA";
        }
        if (matiere.contains("Projet Personnel Encadré 1".toLowerCase(Locale.getDefault()))) {
            return "PPE";
        }
        if (matiere.contains("Algèbre 1".toLowerCase(Locale.getDefault()))) {
            return "Alg.";
        }
        if (matiere.contains("Algorithmique 1".toLowerCase(Locale.getDefault()))) {
            return "Algo";
        }
        if (matiere.contains("Algorithmique".toLowerCase(Locale.getDefault()))) {
            return "Algo";
        }
        if (matiere.contains("Mécanique Générale".toLowerCase(Locale.getDefault()))) {
            return "Méca";
        }
        if (matiere.contains("Introduction au Droit".toLowerCase(Locale.getDefault()))) {
            return "Droit";
        }
        if (matiere.contains("Circuits Electriques 2".toLowerCase(Locale.getDefault()))) {
            return "Elec";
        }
        if (matiere.contains("Circuits Electriques 1".toLowerCase(Locale.getDefault()))) {
            return "Elec";
        }
        if (matiere.contains("Fonctions de l'Electronique 2".toLowerCase(Locale.getDefault()))) {
            return "Fonc Elec";
        }
        if (matiere.contains("Coaching Scolaire 2".toLowerCase(Locale.getDefault()))) {
            return "\"Coaching\"";
        }
        if (matiere.contains("Fonctions de l'Electronique 1".toLowerCase(Locale.getDefault()))) {
            return "Fonc Elec";
        }
        if (matiere.contains("Coaching Scolaire 1".toLowerCase(Locale.getDefault()))) {
            return "\"Coaching\"";
        }
        if (matiere.contains("Droit du Travail".toLowerCase(Locale.getDefault()))) {
            return "Droit";
        }
        if (matiere.contains("Analyse Numérique pour l'Ingén".toLowerCase(Locale.getDefault()))) {
            return "Anal";
        }
        if (matiere.contains("Gestion des Risques".toLowerCase(Locale.getDefault()))) {
            return "G.Risques";
        }
        if (matiere.contains("Ingénierie Qualité".toLowerCase(Locale.getDefault()))) {
            return "Ingé. Qualité";
        }
        if (matiere.contains("Ingénierie du Traitement Stati".toLowerCase(Locale.getDefault()))) {
            return "ITS";
        }
        if (matiere.contains("Gestion de Projet".toLowerCase(Locale.getDefault()))) {
            return "G.Projet";
        }
        if (matiere.contains("Ingénierie des Systèmes d'Info".toLowerCase(Locale.getDefault()))) {
            return "ISI";
        }
        if (matiere.contains("Introduction aux Outils de  l'".toLowerCase(Locale.getDefault()))) {
            return "IOI";
        }
        if (matiere.contains("Analyse Fonctionnelle des Syst".toLowerCase(Locale.getDefault()))) {
            return "AFS";
        }
        if (matiere.contains("Optique Géométrique".toLowerCase(Locale.getDefault()))) {
            return "OG";
        }
        if (matiere.contains("Signaux et Mesures Physiques".toLowerCase(Locale.getDefault()))) {
            return "SMP";
        }
        if (matiere.contains("Introduction aux TIC".toLowerCase(Locale.getDefault()))) {
            return "TIC";
        }
        if (matiere.contains("Projet WEB".toLowerCase(Locale.getDefault()))) {
            return "P. WEB";
        }
        if (matiere.contains("Intégration dans la Vie Profes".toLowerCase(Locale.getDefault()))) {
            return "IVP";
        }
        if (matiere.contains("Ingénierie Système".toLowerCase(Locale.getDefault()))) {
            return "Ingé. Sys.";
        }
        if (matiere.contains("Ingénierie Système / Managemen".toLowerCase(Locale.getDefault()))) {
            return "Ingé/Mngt";
        }
        if (matiere.contains("Evaluation Environnementale (A".toLowerCase(Locale.getDefault()))) {
            return "Connard";
        }
        if (matiere.contains("Ondes et Optique Ondulatoire".toLowerCase(Locale.getDefault()))) {
            return "Onde";
        }
        if (matiere.contains("Conception et Manipulation de".toLowerCase(Locale.getDefault()))) {
            return "BDD";
        }
        if (matiere.contains("Langage Java".toLowerCase(Locale.getDefault()))) {
            return "Java";
        }
        if (matiere.contains("Démarche d'Architecture des SI".toLowerCase(Locale.getDefault()))) {
            return "DASI";
        }
        if (matiere.contains("Ingénierie du Besoin".toLowerCase(Locale.getDefault()))) {
            return "Ingé. Bes.";
        }
        if (matiere.contains("Matériels Mobiles, Systèmes et".toLowerCase(Locale.getDefault()))) {
            return "MMS";
        }
        if (matiere.contains("Réseaux Informatiques".toLowerCase(Locale.getDefault()))) {
            return "Réseaux";
        }
        if (matiere.contains("Gouvernance de l'Information".toLowerCase(Locale.getDefault()))) {
            return "Gouv. Inf.";
        }
        if (matiere.contains("SI et Fonctions de l'Entrepris".toLowerCase(Locale.getDefault()))) {
            return "SIFE";
        }
        if (matiere.contains("Protection de l'Information".toLowerCase(Locale.getDefault()))) {
            return "Protec. Inf.";
        }
        if (matiere.contains("Le journal de bord électroniqu".toLowerCase(Locale.getDefault()))) {
            return "JBE";
        }
        if (matiere.contains("Modernisation du système de Pa".toLowerCase(Locale.getDefault()))) {
            return "MSP";
        }
        if (matiere.contains("Dimensionnement d'Eléments de".toLowerCase(Locale.getDefault()))) {
            return "Dimension";
        }
        if (matiere.contains("Modélisation 3D".toLowerCase(Locale.getDefault()))) {
            return "3D";
        }
        if (matiere.contains("Quality Engineering".toLowerCase(Locale.getDefault()))) {
            return "Quality Eng.";
        }
        if (matiere.contains("Risk Management".toLowerCase(Locale.getDefault()))) {
            return "Risk Man.";
        }
        if (matiere.contains("Project Management".toLowerCase(Locale.getDefault()))) {
            return "Project Man.";
        }
        if (matiere.contains("Systems Engineering".toLowerCase(Locale.getDefault()))) {
            return "Systems Eng.";
        }
        if (matiere.contains("ERP (Architecture, Security an".toLowerCase(Locale.getDefault()))) {
            return "ERP";
        }
        if (matiere.contains("Mise en Forme des Matériaux".toLowerCase(Locale.getDefault()))) {
            return "MFM";
        }
        if (matiere.contains("Caractérisation des Matériaux".toLowerCase(Locale.getDefault()))) {
            return "Matériaux";
        }
        if (matiere.contains("Modélisation des Structures en".toLowerCase(Locale.getDefault()))) {
            return "MSEF";
        }
        if (matiere.contains("Modélisation d'un Système Comp".toLowerCase(Locale.getDefault()))) {
            return "MCM";
        }
        if (matiere.contains("Initiation \u2021 la Méthode des El".toLowerCase(Locale.getDefault()))) {
            return "EI";
        }
        if (matiere.contains("Plasticité Matériaux".toLowerCase(Locale.getDefault()))) {
            return "Plasticité";
        }
        if (matiere.contains("Modélisation des Ecoulements".toLowerCase(Locale.getDefault()))) {
            return "Ecoulement";
        }
        if (matiere.contains("Statistical Data Processing".toLowerCase(Locale.getDefault()))) {
            return "SDP";
        }
        if (matiere.contains("Contract and Labor Law".toLowerCase(Locale.getDefault()))) {
            return "CLL";
        }
        if (matiere.contains("Professional Integration".toLowerCase(Locale.getDefault()))) {
            return "PI";
        }
        if (matiere.contains("Information Systems Engineerin".toLowerCase(Locale.getDefault()))) {
            return "ISE";
        }
        if (matiere.contains("Recruiting Processes".toLowerCase(Locale.getDefault()))) {
            return "RP";
        }
        if (matiere.contains("Geopolitics and Country Risks'".toLowerCase(Locale.getDefault()))) {
            return "GCR";
        }
        if (matiere.contains("Corporate Finance and Financia".toLowerCase(Locale.getDefault()))) {
            return "CFF";
        }
        if (matiere.contains("Management of Innovative Techn".toLowerCase(Locale.getDefault()))) {
            return "MIT";
        }
        if (matiere.contains("Industrial Marketing".toLowerCase(Locale.getDefault()))) {
            return "Indus. M";
        }
        if (matiere.contains("Operational Research".toLowerCase(Locale.getDefault()))) {
            return "OR";
        }
        if (matiere.contains("Growth Strategy & Business Mod".toLowerCase(Locale.getDefault()))) {
            return "GSBM";
        }
        if (matiere.contains("Informatique Industrielle".toLowerCase(Locale.getDefault()))) {
            return "Info. Indu.";
        }
        if (matiere.contains("Organisation du Travail".toLowerCase(Locale.getDefault()))) {
            return "OdT";
        }
        if (matiere.contains("Propriété des Matériaux".toLowerCase(Locale.getDefault()))) {
            return "Prop. Matériaux";
        }
        if (matiere.contains("Mathématiques 2".toLowerCase(Locale.getDefault()))) {
            return "Math";
        }
        if (matiere.contains("Sciences Humaines Champ".toLowerCase(Locale.getDefault()))) {
            return "SHC";
        }
        if (matiere.contains("Electromagnétisme".toLowerCase(Locale.getDefault()))) {
            return "Magn.";
        }
        if (matiere.contains("Technologie Mécanique (BE) 2".toLowerCase(Locale.getDefault()))) {
            return "TM";
        }
        if (matiere.contains("Construction Mécanique 2".toLowerCase(Locale.getDefault()))) {
            return "Cons. Méca.";
        }
        if (matiere.contains("Mécanique 2".toLowerCase(Locale.getDefault()))) {
            return "Méca";
        }
        if (matiere.contains("Physique 2".toLowerCase(Locale.getDefault()))) {
            return "Physique";
        }
        if (matiere.contains("Mathématiques 1".toLowerCase(Locale.getDefault()))) {
            return "Maths";
        }
        if (matiere.contains("Construction Mécanique 1".toLowerCase(Locale.getDefault()))) {
            return "Cons. Méca.";
        }
        if (matiere.contains("Mécanique 1".toLowerCase(Locale.getDefault()))) {
            return "Méca";
        }
        if (matiere.contains("Physique 1".toLowerCase(Locale.getDefault()))) {
            return "Physique";
        }
        if (matiere.contains("Logiciel Libre".toLowerCase(Locale.getDefault()))) {
            return "Freeware";
        }
        if (matiere.contains("Relation MOE/MOA".toLowerCase(Locale.getDefault()))) {
            return "MOE/MOA";
        }
        if (matiere.contains("Relation MOE/MOA".toLowerCase(Locale.getDefault()))) {
            return "MOE/MOA";
        }
        if (matiere.contains("Gouvernance des SI".toLowerCase(Locale.getDefault()))) {
            return "G.SI";
        }
        if (matiere.contains("Démarche d'Architecture des Sy".toLowerCase(Locale.getDefault()))) {
            return "DAS";
        }
        if (matiere.contains("Webmarketing".toLowerCase(Locale.getDefault()))) {
            return "WebMkg";
        }
        if (matiere.contains("Process Modeling".toLowerCase(Locale.getDefault()))) {
            return "Modeling";
        }
        if (matiere.contains("Lean Management, Lean Manufact".toLowerCase(Locale.getDefault()))) {
            return "LM&M";
        }
        if (matiere.contains("Projet ABAQUS".toLowerCase(Locale.getDefault()))) {
            return "ABAQUS";
        }
        if (matiere.contains("Tolérancement".toLowerCase(Locale.getDefault()))) {
            return "Tolé.";
        }
        if (matiere.contains("Climatologie".toLowerCase(Locale.getDefault()))) {
            return "Clim.";
        }
        if (matiere.contains("Géologie, Hydrogéologie, Hydro".toLowerCase(Locale.getDefault()))) {
            return "GHH";
        }
        if (matiere.contains("Modélisation Pollution Acciden".toLowerCase(Locale.getDefault()))) {
            return "MPA";
        }
        if (matiere.contains("Systèmes d'Informations Géogra".toLowerCase(Locale.getDefault()))) {
            return "SIG";
        }
        if (matiere.contains("Droit de l'environnement".toLowerCase(Locale.getDefault()))) {
            return "Droit Env.";
        }
        if (matiere.contains("Analyse Environnementale".toLowerCase(Locale.getDefault()))) {
            return "Analyse E.";
        }
        if (matiere.contains("Modélisation  ACV".toLowerCase(Locale.getDefault()))) {
            return "ACV";
        }
        if (matiere.contains("Management of Innovation".toLowerCase(Locale.getDefault()))) {
            return "MoI";
        }
        if (matiere.contains("Connaissance de l'Entreprise".toLowerCase(Locale.getDefault()))) {
            return "CE";
        }
        if (matiere.contains("Culture du Canada 2".toLowerCase(Locale.getDefault()))) {
            return "Canada";
        }
        if (matiere.contains("Culture du Canada 1".toLowerCase(Locale.getDefault()))) {
            return "Canada";
        }
        if (matiere.contains("Techniques de Fabrication".toLowerCase(Locale.getDefault()))) {
            return "Tech. Fabrication";
        }
        if (matiere.contains("Portugais".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("Chinois".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("Italien".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("Russe".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("Japonais".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("Anglais 1".toLowerCase(Locale.getDefault()))) {
            return "LV1";
        }
        if (matiere.contains("Anglais 2".toLowerCase(Locale.getDefault()))) {
            return "LV1";
        }
        if (matiere.contains("Espagnol 1".toLowerCase(Locale.getDefault()))) {
            return "LV2";
        }
        if (matiere.contains("Technologie Mécanique (BE) 1".toLowerCase(Locale.getDefault()))) {
            return "TM";
        }
        if (matiere.contains("Technologie Mécanique (BE)".toLowerCase(Locale.getDefault()))) {
            return "TM";
        }
        if (matiere.contains("Ingéniérie Biomédicale".toLowerCase(Locale.getDefault()))) {
            return "Ingé Bio";
        }
        if (matiere.contains("Mathématiques avancés 2".toLowerCase(Locale.getDefault()))) {
            return "Math STI";
        }
        if (matiere.contains("Outils Mathématiques pour l'In".toLowerCase(Locale.getDefault()))) {
            return "OMI";
        }
        if (matiere.contains("Soutien Admis sur titres 2".toLowerCase(Locale.getDefault()))) {
            return "SAT";
        }
        if (matiere.contains("Soutien Admis sur titres 1".toLowerCase(Locale.getDefault()))) {
            return "SAT";
        }
        if (matiere.contains("Formation Généraliste 3ème Ann".toLowerCase(Locale.getDefault()))) {
            return "Fg";
        }
        if (matiere.contains("Formation Généraliste 2ème Ann".toLowerCase(Locale.getDefault()))) {
            return "Fg";
        }
        if (matiere.contains("Formation Généraliste 1ère Ann".toLowerCase(Locale.getDefault()))) {
            return "Fg";
        }
        if (matiere.contains("Système Ecolonomique de Récupé".toLowerCase(Locale.getDefault()))) {
            return "SER";
        }
        if (matiere.contains("Electronique".toLowerCase(Locale.getDefault()))) {
            return "Elec";
        }
        if (matiere.contains("Marketing Industriel".toLowerCase(Locale.getDefault()))) {
            return "MI";
        }
        if (matiere.contains("Bâtiment de Logistique en Acie".toLowerCase(Locale.getDefault()))) {
            return "BLA";
        }
        if (matiere.contains("Bilan Thermique d'un Habitat".toLowerCase(Locale.getDefault()))) {
            return "BTH";
        }
        if (matiere.contains("Projet Architecture Avion".toLowerCase(Locale.getDefault()))) {
            return "PAA";
        }
        if (matiere.contains("Création d'Entreprise".toLowerCase(Locale.getDefault()))) {
            return "CE";
        }
        if (matiere.contains("Rénovation d'un Radar de Traje".toLowerCase(Locale.getDefault()))) {
            return "RRT";
        }
        if (matiere.contains("Evolution d'un Système d'Infor".toLowerCase(Locale.getDefault()))) {
            return "ESI";
        }
        if (matiere.contains("Etude d'un Système Automatisé".toLowerCase(Locale.getDefault()))) {
            return "EDA";
        }
        if (matiere.contains("Base Tournante pour Siège de V".toLowerCase(Locale.getDefault()))) {
            return "BTSV";
        }
        return pMatiere;
    }
}
