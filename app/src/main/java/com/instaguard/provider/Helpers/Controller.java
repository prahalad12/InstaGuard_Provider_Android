package com.instaguard.provider.Helpers;

import static com.facebook.FacebookSdk.setAdvertiserIDCollectionEnabled;
import static com.facebook.FacebookSdk.setAutoLogAppEventsEnabled;
import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import androidx.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.instaguard.provider.Helpers.FrescoCache.ImagePipelineConfigFactory;
import com.instaguard.provider.Helpers.Localizationactivity.core.LocalizationApplicationDelegate;

public class Controller extends Application {

	public static final String TAG = Controller.class.getSimpleName();

	LocalizationApplicationDelegate localizationDelegate = new LocalizationApplicationDelegate(this);

	private static Controller mInstance;

	private static Controller mInstanceApplication;
	//Geocoder
	private static Context mContextApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		//Geocoder
		mContextApplication = getApplicationContext();
		FirebaseApp.initializeApp(this);
		mInstance = this;
		Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));

		FacebookSdk.sdkInitialize(mContextApplication);
		AppEventsLogger.activateApp(this);
		FacebookSdk.setAutoInitEnabled(true);
		FacebookSdk.fullyInitialize();

		AudienceNetworkAds.initialize(mContextApplication);
		//AdSettings.setMultiprocessSupportMode(AdSettings.MultiprocessSupportMode.MULTIPROCESS_SUPPORT_MODE_OFF);
		AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);

		setAdvertiserIDCollectionEnabled(true);
		setAutoLogAppEventsEnabled(true);

	}

	public static synchronized Controller getInstance() {
		return mInstance;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		localizationDelegate.onConfigurationChanged(this);
	}

	@Override
	public Context getApplicationContext() {
		return localizationDelegate.getApplicationContext(super.getApplicationContext());
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(localizationDelegate.attachBaseContext(base));
		MultiDex.install(this);
	}
	//Geocoder
	public static Context getAppContext(){
		return mContextApplication;
	}
}