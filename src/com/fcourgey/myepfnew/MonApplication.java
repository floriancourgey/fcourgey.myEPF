package com.fcourgey.myepfnew;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.os.Environment;

import com.fcourgey.android.mylib.a_mettre_dans_java_lib.exceptions.ReadOnlyException;
import com.fcourgey.android.mylib.a_mettre_dans_java_lib.outils.FichierOutils;
import com.fcourgey.android.mylib.exceptions.SDReadOnlydException;

@ReportsCrashes(
		customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },                
		mode = ReportingInteractionMode.SILENT,
		resToastText = R.string.crash_toast_text)
public class MonApplication extends Application {
	
	public static String DOSSIER_MY_EPF = "{SD}/{PACKAGE}/";

	@Override
	public void onCreate() {
		super.onCreate();		
		ACRA.init(this);
		initDossierMyEPF();
	}
	
	private void initDossierMyEPF(){
		try{
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				DOSSIER_MY_EPF = DOSSIER_MY_EPF.replace("{SD}", Environment.getExternalStorageDirectory().toString());
				DOSSIER_MY_EPF = DOSSIER_MY_EPF.replace("{PACKAGE}", getApplicationContext().getPackageName());
				if(!FichierOutils.creerDossier(DOSSIER_MY_EPF)){
					throw new ReadOnlyException();
				}
			} else {
				throw new SDReadOnlydException();
			}
		}catch(SDReadOnlydException e){
			e.printStackTrace();
			return;
		}catch(ReadOnlyException e){
			e.printStackTrace();
			return;
		}
	}
}
