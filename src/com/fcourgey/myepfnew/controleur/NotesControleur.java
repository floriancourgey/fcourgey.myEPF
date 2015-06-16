package com.fcourgey.myepfnew.controleur;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.TreeSet;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.SM;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fcourgey.android.mylib.framework.AsyncFragmentControleur;
import com.fcourgey.android.mylib.framework.Fragment;
import com.fcourgey.myepfnew.MonApplication;
import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.entite.Matiere;
import com.fcourgey.myepfnew.entite.Module;
import com.fcourgey.myepfnew.entite.MyEpfUrl;
import com.fcourgey.myepfnew.entite.Note;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;
import com.fcourgey.myepfnew.modele.MyEpfPreferencesModele;
import com.fcourgey.myepfnew.outils.XmlNoteMyEpf;
import com.fcourgey.myepfnew.vue.NotesVue;

@SuppressWarnings("deprecation")
public class NotesControleur extends AsyncFragmentControleur {
	
	private File xmlFile;
	private ArrayList<Module> lModules;
	
	public NotesControleur(Fragment f, LayoutInflater inflater, ViewGroup container){
		super(f, inflater, container);
		
		xmlFile = new File(MonApplication.DOSSIER_MY_EPF,"notes.xml");
		
		MyEpfPreferencesModele modele = (MyEpfPreferencesModele)getPrefs();
		String derniereActu = modele.getString(MyEpfPreferencesModele.KEY_NOTES_DERNIERE_ACTU); 
		vue = new NotesVue(this, container, derniereActu);
		
		mappingXmlModules(false);
		chargerVueComplete();
		
		// si on est connecté à my.epf
		if(ConnexionControleur.connecte){
			telechargementXmlNotes();
			chargerVueComplete();
		}
	}
	
	public void onMyEpfConnected(){
		telechargementXmlNotes();
	}
	
	/**
	 * lance le téléchargement du xml de notes
	 * +
	 * lance le parsing @seemappingXmlNotes
	 */
	public void telechargementXmlNotes(){
		avancement("Requête notes", 55);
		getActivite().runOnUiThread(new Runnable() {
			public void run() {
				StrictMode.ThreadPolicy policy = new
						StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
						StrictMode.setThreadPolicy(policy);
				HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpGet httpGet = new HttpGet(MyEpfUrl.REPORT_NOTES);
				String cookies = CookieManager.getInstance().getCookie(MyEpfUrl.MYDATA);
				httpGet.setHeader(SM.COOKIE, cookies);
				InputStream is = null;
				try {
					is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
				} catch (Exception e) {
					e.printStackTrace();
					chargerVueErreur("HttpClient impossible", "Impossible de joindre le serveur EPF (étonnant à ce stade)");
					return;
				}
				String line = null;
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					while ((line = br.readLine()) != null) {
//						System.out.println(line);
						if(line.contains("ReportSession")){
//							Log.e("lol", line);
							break;
						}
					}
					br.close();
				} catch(Exception e){
					e.printStackTrace();
				}
				if(!line.contains("ReportSession")){
					chargerVueErreur("HTML parse", "Erreur dans la source html ("+line+")");
					return;
				}
				// ajustements
				line = line.replace("$create(Microsoft.Reporting.WebFormsClient._InternalReportViewer, ", "");
				line = line.replace(", null, null, $get(\"m_sqlRsWebPart_ctl00_ReportViewer_ctl03\"));", "");
				line = line.replace("function(){__doPostBack('m_sqlRsWebPart$ctl00$ReportViewer$ctl03','');}", "\"\"");
//				Log.e("lol", line);
				// conversion json
				String urlXml = "";
				try {
					JSONObject json = new JSONObject(line);
					urlXml = json.getString("ExportUrlBase");
				} catch (JSONException e) {
					e.printStackTrace();
					chargerVueErreur("JSON parse", "Erreur dans la source json ("+line+")");
					return;
				}
				urlXml = urlXml.replace("parcoursscolaireReserved", "parcoursscolaire/Reserved");
				urlXml = MyEpfUrl.MY_EPF+ urlXml + "XML";
//				Log.e("lol", urlXml);
				// sauvegarde SD
				try{
					httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		            httpGet = new HttpGet(urlXml);
		            HttpResponse response = httpClient.execute(httpGet);
		            HttpEntity entity = response.getEntity();
		            Header[] headers = response.getAllHeaders();
		            for(int i=0;i<headers.length;i++){
		                System.out.println("Header: "+headers[i].toString());
		            }
		            System.out.println(response.toString());

		            System.out.println("File get: " + response.getStatusLine());

		            if(MonApplication.DOSSIER_MY_EPF.contains("{")){
		            	chargerVueErreur("XML save", "Impossible d'accéder au dossier de sauvegarde ("+MonApplication.DOSSIER_MY_EPF+")");
		            	return;
		            }
		            
					FileOutputStream fileOutput = new FileOutputStream(xmlFile);
					InputStream inputStream = entity.getContent();
					byte[] buffer = new byte[1024];
					int bufferLength = 0;
					while ( (bufferLength = inputStream.read(buffer)) > 0 ){
						fileOutput.write(buffer, 0, bufferLength);
					}
					fileOutput.close();
					
					// update texte dernière actualisation
					Calendar now = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
					((NotesVue)vue).setDerniereActualisation(sdf.format(now.getTime()));
					// ajout dans les pref
					MyEpfPreferencesModele modele = (MyEpfPreferencesModele)getPrefs();
					modele.putString(MyEpfPreferencesModele.KEY_NOTES_DERNIERE_ACTU, sdf.format(now.getTime()));
					
					mappingXmlModules(true);
				} catch(Exception e){
					e.printStackTrace();
					chargerVueErreur("XML save", "Erreur dans la sauvegarde XML");
					return;
				}
			}
		});
	}
	/**
	 * parse le xml et le mappe à notre objet java
	 * +
	 * lance l'affichage des notes @see affichageXmlNotes
	 */
	private void mappingXmlModules(boolean suiteAuTelechargement){
		try{
			lModules = XmlNoteMyEpf.xmlToLModules(xmlFile);
		}catch(NullPointerException npe){
			chargerVueErreur("Chargement des notes impossible", npe.getMessage());
			return;
		} catch(FileNotFoundException fnfe){
			chargerVueErreur("Chargement des notes impossible", fnfe.getMessage());
			return;
		} catch(IOException ioe){
			if(suiteAuTelechargement){
				
			} else {
				chargerVueErreur("Chargement des notes impossible", ioe.getMessage());
				return;
			}
		} catch(XmlPullParserException xppe){
			chargerVueErreur("Chargement des notes impossible", xppe.getMessage());
			return;
		}
		
		affichageXmlNotes();
	}
	/**
	 * affiche les notes depuis l'objet java
	 */
	private void affichageXmlNotes(){
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		NotesAdapter mAdapter = new NotesAdapter(getActivite());
		mAdapter.addSectionHeaderItem("Les 5 dernières notes");
		Note[] lastNotes = get5DernieresNotes();
		for(int i=0 ; i<lastNotes.length ; i++){
			Note n = lastNotes[i];
			mAdapter.addItem(n.getType()+" de "+Matiere.abre(n.getModule().getNom())+" : "+n.getNote()+"    ("+sdf.format(n.getDate().getTime())+")");
		}
		for(Module m : lModules){
			mAdapter.addSectionHeaderItem(m.getNom());
			for(Note n : m.getlNotes()){
				mAdapter.addItem(n.getType()+" : "+n.getNote()+"    ("+sdf.format(n.getDate().getTime())+")");
			}
		}

		ListView lvNotes = (ListView)vue.findViewById(R.id.lvNotes);
		lvNotes.setAdapter(mAdapter);
		onNotesAffichees();
	}
	
	private void onNotesAffichees(){
	}
	
	public void avancement(String t, int p){
		Log.i("notescontroleur", "avt "+p+" : "+t);
	}
	
	class NotesAdapter extends BaseAdapter {

		private static final int TYPE_ITEM = 0;
		private static final int TYPE_SEPARATOR = 1;

		private ArrayList<String> mData = new ArrayList<String>();
		private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

		private LayoutInflater mInflater;

		public NotesAdapter(Context context) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void addItem(final String item) {
			mData.add(item);
			notifyDataSetChanged();
		}

		public void addSectionHeaderItem(final String item) {
			mData.add(item);
			sectionHeader.add(mData.size() - 1);
			notifyDataSetChanged();
		}

		@Override
		public int getItemViewType(int position) {
			return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public String getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			int rowType = getItemViewType(position);

			if (convertView == null) {
				holder = new ViewHolder();
				switch (rowType) {
				case TYPE_ITEM:
					convertView = mInflater.inflate(R.layout.notes_row, null);
					holder.textView = (TextView) convertView.findViewById(R.id.text);
					break;
				case TYPE_SEPARATOR:
					convertView = mInflater.inflate(R.layout.notes_row_sep, null);
					holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
					break;
				}
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(mData.get(position));

			return convertView;
		}

	}
	public static class ViewHolder {
		public TextView textView;
	}
	private Note[] get5DernieresNotes(){
		// on met toutes les notes dans la même liste
		ArrayList<Note> combined = new ArrayList<Note>();
		for(Module m : lModules){
			combined.addAll(m.getlNotes());
		}
		Collections.sort(combined, new Comparator<Note>() {
			public int compare(Note n1, Note n2) {
				return n1.getDate().getTime().compareTo(n2.getDate().getTime());
			}
		});
		Note[] retour = new Note[5];
		for(int i=4 ; i>=0; i--){
			retour[i] = combined.get(i+combined.size()-5-1);
		}
		return retour;
	}
}
