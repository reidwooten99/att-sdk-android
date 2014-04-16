/**
 * This file is used to Set/Configure New Environment & Clear the cache stored by the SDK
 */

package com.att.ads.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Initial class for showing the splash screen.
 * 
 */
public class EngineeringBuildActivity extends Activity {

    private static final String TAG = "Engineering Build Activity";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.engineering_build_config_layout);
    }

    protected void startMainActivity() {
    	Log.d(TAG, "Launch ADS Sample App");
        startActivity(new Intent(this, AdsMainActivity.class));
        finish();
    }
    
    // Clear the AT, RT, ExpiresIN & OldFQDN from Pref
    public void clearCache(View v){
    	prefs = getApplicationContext().getSharedPreferences("ADS_API", Context.MODE_PRIVATE);
    	boolean whatHappened = prefs.edit().clear().commit(); 
    	Log.d(TAG, "Did we clear cache - " + String.valueOf(whatHappened));
    }
    
    public void launchADSSampleApp(View v){
    	
    	EditText newFqdnWidget = (EditText)findViewById(R.id.editText1);
    	String newFqdn = newFqdnWidget.getText().toString();
    	if(newFqdn != null && newFqdn.length() > 0)
    		setFQDNtoPref(newFqdn);
    	
    	EditText appKeyWidget = (EditText)findViewById(R.id.editText2);
    	String appKey = appKeyWidget.getText().toString();
    	EditText secretKeyWidget = (EditText)findViewById(R.id.editText3);
    	String secretKey = secretKeyWidget.getText().toString();
    	if(null != appKey & appKey.length() > 0 &&  null != secretKey && secretKey.length() > 0)
    		setAppandSecretKeytoPref(appKey, secretKey);
    	
    	new Handler().postDelayed(new Runnable() {
            public void run() {
                startMainActivity();
            }
        }, 3000);
    }
    
    public void setAppandSecretKeytoPref(String appKey, String secretKey){
    	
    	prefs = getApplicationContext().getSharedPreferences("ADS_API", Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
		editor.putString("Engineering_appKey", appKey);
		editor.putString("Engineering_secretKey", secretKey);
		boolean whatHappened = editor.commit();
		Log.d(TAG, "Did we update new app key and secret key - " + String.valueOf(whatHappened));
    }
    
    // Set the new FQDN to pref
    public void setFQDNtoPref(String newFqdn){
    	prefs = getApplicationContext().getSharedPreferences("ADS_API", Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
		editor.putString("fqdn", newFqdn);
		boolean whatHappened = editor.commit();
		Log.d(TAG, "Did we update new FQDN - " + String.valueOf(whatHappened));
    }
}
