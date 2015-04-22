package com.fcourgey.myepfnew;

import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;

import com.fcourgey.myepfnew.R;

@ReportsCrashes(
		/*mailTo = "cocobananasplit@gmail.com",*/
		customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },                
		mode = ReportingInteractionMode.SILENT,
		resToastText = R.string.crash_toast_text)
public class MonApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init(this);
	}
}
