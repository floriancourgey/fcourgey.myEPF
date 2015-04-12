package com.fcourgey.myepfnew.activite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.SM;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.TextView;

import com.fcourgey.myepfnew.R;
import com.fcourgey.myepfnew.factory.MySSLSocketFactory;

public class BulletinFragment extends Fragment {
	
	public static String CHEMIN_BULLETIN;
	
	public static final String URL_MY_EPF = EdtFragment.URL_MY_EPF;
	public static final String URL_BULLETIN2 = URL_MY_EPF+"/_layouts/sharepointproject2/redirectionversbulletin.aspx";
	public static final String URL_BULLETIN = "https://my.epf.fr/parcoursscolaire/_layouts/SharePointProject2/AffichageBulletin.aspx?ANNEE=2014&LOGIN_RESEAU=fcourgey";
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View vue = inflater.inflate(R.layout.bulletin_fragment, container, false);
		
		CHEMIN_BULLETIN = getActivity().getFilesDir()+"/bulletin.pdf";
		
		((TextView)getActivity().findViewById(R.id.bTelechargerBulletin)).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onTelechargerBulletinClicked();
			}
		});
		
		return vue;
	}
	
	private void onTelechargerBulletinClicked() {
		telechargerEtAfficherBulletin();
	}
	
	/**
	 * télécharge le bulletin si connecté à myepf
	 * dans 
	 */
	private void telechargerEtAfficherBulletin() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				HttpClient httpClient = MySSLSocketFactory.getNewHttpClient();
				HttpContext localContext = new BasicHttpContext();
				HttpGet httpGet = new HttpGet(URL_BULLETIN);
				String cookies = CookieManager.getInstance().getCookie(URL_MY_EPF);
				httpGet.setHeader(SM.COOKIE, cookies);
				InputStream is = null;
				OutputStream output = null;
				try {
					is = httpClient.execute((HttpUriRequest) httpGet, localContext).getEntity().getContent();
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Impossible de joindre le serveur EPF (étonnant à ce stade 2)");
					return;
				} 
				try {
//					// Lecture du PDF, décommenter pour debug
//					String line;
//					BufferedReader br = new BufferedReader(new InputStreamReader(is));
//					while ((line = br.readLine()) != null) {
//						System.out.print(line);
//						Log.e("telechargerBulletin", line);
//					}
//					br.close();
					
					// écriture du fichier
					final File file = new File(CHEMIN_BULLETIN);
					output = new FileOutputStream(file);
					final byte[] buffer = new byte[1024];
		            int read;

		            while ((read = is.read(buffer)) != -1){
		                output.write(buffer, 0, read);
		            }

		            output.flush();
		            output.close();
		            
		            onTelechargerBulletin();
				} catch(IOException e){
					e.printStackTrace();
					System.out.println("Impossible de lire la réponse finale");
				}
			}
		});
	}
	
	private void onTelechargerBulletin(){
		File file = new File(CHEMIN_BULLETIN);
		Intent target = new Intent(Intent.ACTION_VIEW);
		target.setDataAndType(Uri.fromFile(file),"application/pdf");
		target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

		Intent intent = Intent.createChooser(target, "Open File");
		try {
		    startActivity(intent);
		} catch (ActivityNotFoundException e) {
			// aucune appli pour ouvrir un PDF
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder	
			.setTitle(R.string.bulletin_nopdf_titre)
			.setMessage(R.string.bulletin_nopdf_message)
			.setPositiveButton(R.string.ok, null);
		}  
	}
}
