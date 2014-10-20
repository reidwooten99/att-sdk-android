package com.att.iamsampleapp;
import com.att.api.util.Preferences;
import com.att.api.util.PreferencesOperator;
import com.att.api.util.SdkConfig;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/************************************************
 * This Activity is intended for debugging propose only
 * 
 * @author am017p
 *
 */

public class DebugSettingsPage extends Activity {
	
	private CheckBox m_forceOffNetCheckBox = null;
	private CheckBox m_suppressCheckBox = null;
	private CheckBox m_clearCookiesCheckBox = null;
	private Button m_applyButton = null;
	private CheckBox m_forceACExpiresCheckBox = null;
	private EditText m_curAC = null;
	private EditText m_refreshToken = null;
	private EditText m_curACTime = null;
	private boolean CLEAR_COOKIES = false;
	private boolean FORCE_AC_EXPIRE = false;
	private boolean OFF_NET = false;
	private boolean SUPPRESS = false;
	private boolean tokenStr_masked = false;
	private boolean freshTokenStr_masked = false;
	PreferencesOperator m_pref = null;
	private String tokenStr = "";
	private String refreshStr = "";
	private String masked_tokenStr = "";
	private String masked_refreshStr = "";
	private String ACExpiredTime = "";
	final String TAG = "DebugSettingsPage";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_settings_page);
		
		tokenStr_masked = false;
		m_pref = new PreferencesOperator(getApplicationContext());
		m_clearCookiesCheckBox = (CheckBox) findViewById(R.id.clearCookiesCheckBox);
		m_forceACExpiresCheckBox = (CheckBox) findViewById(R.id.forceACExpiresCheckBox);
		
		m_curAC = (EditText)findViewById(R.id.curAC);
		tokenStr = m_pref.singleStrRetrieve("Token");
		
		// if valid Access token then mask it
		if (!tokenStr.contentEquals(SdkConfig.none)){
			masked_tokenStr = middleMaskedStr(tokenStr);
			tokenStr_masked = true;
			m_curAC.setText(masked_tokenStr);
		}
		
		m_refreshToken = (EditText)findViewById(R.id.refreshToken);
        refreshStr = m_pref.singleStrRetrieve("RefreshToken");
		
        // If valid refresh token then mask it
		if (!refreshStr.contentEquals(SdkConfig.none)){
			masked_refreshStr = middleMaskedStr(refreshStr);
			freshTokenStr_masked = true;
			m_refreshToken.setText(masked_refreshStr);
		}
		
		// Display Access token expired time
		m_curACTime = (EditText)findViewById(R.id.curACTime);
		ACExpiredTime = String.valueOf(m_pref.singleLongRetrieve("AccessTokenExpiry"));
		m_curACTime.setText(ACExpiredTime);
		
		// Check for preset of by pass on-net and suppress landing page
		String presetedStr = m_pref.singleStrRetrieve(SdkConfig.preset);
		if (!presetedStr.contains(SdkConfig.none)){
			SUPPRESS = presetedStr.contains(SdkConfig.suppressLndgPageStr);
			OFF_NET  = presetedStr.contains(SdkConfig.byPassOnNetStr);
		}
		
		// If preset by pass on-net then check the box
		m_forceOffNetCheckBox = (CheckBox) findViewById(R.id.forceOffNetCheckBox);
		if (OFF_NET){
			m_forceOffNetCheckBox.setChecked(true);
		}
		
		// If preset by suppress landing page then check the box
		m_suppressCheckBox = (CheckBox) findViewById(R.id.forceSuppressCheckBox);
		if (SUPPRESS){
			m_suppressCheckBox.setChecked(true);
		}
		
	    // Update the Preference storage when user press "APPLY" button
	    m_applyButton = (Button) findViewById(R.id.applyButton);
	    m_applyButton.setOnClickListener(new Button.OnClickListener(){
	   	 	 public void onClick(View v) {
	   	 		 
	         // If clear cookies and preference box is checked, clear all cookies and preset values
	   	 	 if (CLEAR_COOKIES){
	   	 	    CookieSyncManager.createInstance(getApplicationContext());
		        CookieManager cookieManager = CookieManager.getInstance();
		        cookieManager.removeAllCookie();
		        cookieManager.removeSessionCookie(); 
		        m_pref.clearEntirePreferences();
	   	 	 }    
	   	 	 else {
	   	 	     // If no clear cookies box is checked
			   	 if (OFF_NET && SUPPRESS){
			   		    // record that off-net suppesslanding page is preset
			   	 		m_pref.singleStrUpdate(SdkConfig.preset, SdkConfig.byPassOnNetANDsuppressLandingStr );
			   	 }
			   	 else
			   	   if ((!OFF_NET) && SUPPRESS){
			   		 // record that only suppesslanding page is preset
			   		  m_pref.singleStrUpdate(SdkConfig.preset, SdkConfig.suppressLndgPageStr);
			   	 }
			   	 else
				   if (OFF_NET && (!SUPPRESS)){
					   // record that only by pass on-net os preset is preset
					   m_pref.singleStrUpdate(SdkConfig.preset, SdkConfig.byPassOnNetStr);	 		 
				 }
				 else{
					   // record that no box is checked
					   m_pref.singleStrUpdate(SdkConfig.preset, SdkConfig.none);
				 }
			   	 
			   	 // If the user changed the Access token, it is now invalid. So we record the masked Access token.
			   	 // If the user didn't change the Access token, we record the original Access token 
			     String ACToken = m_curAC.getText().toString().trim();
			     if (tokenStr_masked){
			    	  if (ACToken.contentEquals(masked_tokenStr)){
			    		  ACToken = tokenStr;
			    	  } 
			     }
	   	 		 m_pref.singleStrUpdate("Token", ACToken);
	   	 		 
	   	 	     // If the user changed the Refresh token, it is now invalid. So we record the masked Refresh token.
			   	 // If the user didn't change the Refresh token, we record the original Refresh token 
	   	 	     String freshTokenEdt = m_refreshToken.getText().toString().trim();
	   	 	     if (freshTokenStr_masked){
		    	    if (freshTokenEdt.contentEquals(masked_refreshStr)){
		    	    	freshTokenEdt = refreshStr;
		    	   } 
		        }
				m_pref.singleStrUpdate("RefreshToken", freshTokenEdt );
	   	 	 }
	   	 	 
	   	 	 // If box for force Access token to expire is checked, reset its value to 0
	   	 	 if (FORCE_AC_EXPIRE){
	   	 		m_pref.setAccessTokenExpiry(0L);
	   	 	    freshTokenStr_masked = false;
	   	 	    tokenStr_masked = false;
	   	 	    finish();
	   	 	 }
	   	 	 else {
	   	 		  // If box of force access token is not checked, record its current value
	   	 		  String ACTimeStr =  m_curACTime.getText().toString().trim();
	   	 		  if (ACTimeStr.length() > 0){
		   	 		  long ACTime = Long.parseLong(ACTimeStr);
		   	 		  m_pref.setAccessTokenExpiry(ACTime);
		   	 		  freshTokenStr_masked = false;
		   	 	      tokenStr_masked = false;
		   	 		  finish();
	   	 		  }
	   	 		  else {
	   	 			   // case of no value of access token expired time is entered.
	   	 			   Utils.toastHere(getApplicationContext(), "", "Access-token-expired-time block is empty");
	   	 			   m_curACTime.setText(ACExpiredTime);
	   	 		
	   	 		  }
	   	 	 }    	 
   	 	  }	  	 
      });		
	}	
	
	@Override
	public void onResume() {
		super.onResume();
		
		FORCE_AC_EXPIRE = false;
		m_forceACExpiresCheckBox.setChecked(false);
	}
	
	
	public String middleMaskedStr( String unmaskedStr){
		Log.i(TAG, "Unmasked str: " + unmaskedStr );
		// Mask middle five characters
		int midPos = unmaskedStr.length()/2;
		String maskedStr = unmaskedStr.substring(0, midPos) + "*****" + unmaskedStr.substring(midPos + 5);
		Log.i(TAG, "Masked str: " + maskedStr );
		return maskedStr;
	}	
	
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which check box was clicked
	    switch(view.getId()) {
	        case R.id.forceOffNetCheckBox:
	            if (checked){
		     	 	OFF_NET = true;  
		     	    if (CLEAR_COOKIES){ 
	     	 		   m_clearCookiesCheckBox.setChecked(false);
	    	 		   CLEAR_COOKIES = false;
	     	 	    }     
	            }
	            else {
	            	OFF_NET = false;	
	            }
	            break;
	            
	        case R.id.forceSuppressCheckBox:
	        	
	            if (checked){
		    	 	SUPPRESS = true; 
		    	 	if (CLEAR_COOKIES){ 
		 	    	 		m_clearCookiesCheckBox.setChecked(false);
		 	    	 		CLEAR_COOKIES = false;
		 	       }  
	    	    }
	    	    else {
		    	 		   SUPPRESS = false;
	    	 	}
	    	 		   
	            break;
	            
	        case R.id.forceACExpiresCheckBox:
	        	if (checked){
	 	   		       FORCE_AC_EXPIRE = true;
	 	   		       m_curACTime.setText("0");
	 	   	    }
	 	   	    else {
	 	   	    	     FORCE_AC_EXPIRE = false;
	 	   		         m_curACTime.setText(ACExpiredTime);	
	 	   	    	}
	        		
	        	break;
	        	
	        case R.id.clearCookiesCheckBox:
	        	if (checked){
			   			CLEAR_COOKIES = true;
			   		    FORCE_AC_EXPIRE = false;
			   		    OFF_NET = false;
			   		    SUPPRESS = false;
			   		    m_forceACExpiresCheckBox.setChecked(false);
			   		    m_curACTime.setText("0");
			   		    m_curAC.setText("");
			   		    m_refreshToken.setText("");
			   		    m_suppressCheckBox.setChecked(false);
			   		    m_forceOffNetCheckBox.setChecked(false);
	  	 		   
	        	}
	        	else {
	        		 m_curAC.setText(masked_tokenStr);
	        		 m_curACTime.setText(ACExpiredTime);
	        		 m_refreshToken.setText(masked_refreshStr);
	        		
	        	}
	        	break;
	    }
	}
}
