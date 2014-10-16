package com.att.iamsampleapp;
import com.att.api.util.Preferences;
import com.att.api.util.SdkConfig;
import android.app.Activity;
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
	private TextView m_curACTime = null;
	private boolean OFF_NET = false;
	private boolean SUPPRESS = false;
	private boolean CLEAR_COOKIES = false;
	private boolean FORCE_AC_EXPIRE = false;
	private boolean tokenStr_masked = false;
	private boolean freshTokenStr_masked = false;
	Preferences pref = null;
	private String tokenStr = "";
	private String refreshStr = "";
	private String masked_tokenStr = "";
	private String masked_refreshStr = "";
	private String ACExpiredTime = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_settings_page);
		
		boolean suppressed = false;
		boolean bypass = false;
		tokenStr_masked = false;
		
		pref = new Preferences(getApplicationContext());
		
		m_curAC = (EditText)findViewById(R.id.curAC);
		tokenStr = pref.getString("Token", SdkConfig.none);
		
		if (!tokenStr.contentEquals(SdkConfig.none)){
			masked_tokenStr = middleMaskedStr(tokenStr);
			tokenStr_masked = true;
			m_curAC.setText(masked_tokenStr);
		}
		
		m_refreshToken = (EditText)findViewById(R.id.refreshToken);
        refreshStr = pref.getString("RefreshToken", SdkConfig.none);
		
		if (!refreshStr.contentEquals(SdkConfig.none)){
			masked_refreshStr = middleMaskedStr(refreshStr);
			freshTokenStr_masked = true;
			m_refreshToken.setText(masked_refreshStr);
		}
		
		m_curACTime = (TextView)findViewById(R.id.curACTime);
		ACExpiredTime = String.valueOf(pref.getLong("AccessTokenExpiry", 0L));
		m_curACTime.setText(ACExpiredTime);
		
		String presetedStr = pref.getString(SdkConfig.preset, SdkConfig.none);
		
		if (!presetedStr.contains(SdkConfig.none)){
			suppressed = presetedStr.contains(SdkConfig.suppressLndgPageStr);
			bypass  =    presetedStr.contains(SdkConfig.byPassOnNetStr);
		}
		
		m_forceOffNetCheckBox = (CheckBox) findViewById(R.id.forceOffNetCheckBox);
		if (bypass){
			m_forceOffNetCheckBox.setChecked(true);
			OFF_NET = true;
		}
		
		m_suppressCheckBox = (CheckBox) findViewById(R.id.forceSuppressCheckBox);
		if (suppressed){
			m_suppressCheckBox.setChecked(true);
			SUPPRESS = true;
		}
		
	   m_clearCookiesCheckBox = (CheckBox) findViewById(R.id.clearCookiesCheckBox);
	 
	   m_forceACExpiresCheckBox = (CheckBox) findViewById(R.id.forceACExpiresCheckBox);
	   
	   m_applyButton = (Button) findViewById(R.id.applyButton);
	   m_applyButton.setOnClickListener(new Button.OnClickListener(){
	   	 	 public void onClick(View v) {
	   
	   	 	 if (CLEAR_COOKIES){
	   	 	    CookieSyncManager.createInstance(getApplicationContext());
		        CookieManager cookieManager = CookieManager.getInstance();
		        cookieManager.removeAllCookie();
		        cookieManager.removeSessionCookie(); 
	   	 	    pref.setString("PRESET",SdkConfig.none);  
	   	 	    pref.setLong("AccessTokenExpiry", 0L);
  	 		    pref.setString("Token", SdkConfig.none);
			    pref.setString("RefreshToken", SdkConfig.none);
			    SdkConfig.tokenExpiredTime = 0L;
			    SdkConfig.refreshToken = "";
			    SdkConfig.token = ""; 
	   	 	 }
	   	 	 else {
			   	 if (OFF_NET && SUPPRESS){
			   	 		pref.setString("PRESET",SdkConfig.byPassOnNetANDsuppressLandingStr );
			   	 }
			   	 else
			   	   if ((!OFF_NET) && SUPPRESS){
			   		  pref.setString("PRESET", SdkConfig.suppressLndgPageStr);
			   	 }
			   	 else
				   if (OFF_NET && (!SUPPRESS)){
					   pref.setString("PRESET", SdkConfig.byPassOnNetStr);	 		 
				 }
				 else{
					   pref.setString("PRESET", SdkConfig.none);
				 }
			   	 
			     String ACToken = m_curAC.getText().toString().trim();
			     if (tokenStr_masked){
			    	  if (ACToken.contentEquals(masked_tokenStr)){
			    		  ACToken = tokenStr;
			    		  tokenStr_masked = false;
			    	  } 
			     }
	   	 		 pref.setString("Token", ACToken);
	   	 		 
	   	 	     String freshTokenEdt = m_curAC.getText().toString().trim();
	   	 	     if (freshTokenStr_masked){
		    	    if (freshTokenEdt.contentEquals(masked_tokenStr)){
		    	    	freshTokenEdt = refreshStr;
		    		    freshTokenStr_masked = false;
		    	   } 
		        }
				pref.setString("RefreshToken", freshTokenEdt );
				 
				SdkConfig.token = ACToken;
				SdkConfig.refreshToken = freshTokenEdt;  
			   	 
	   	 	 }
	   	 	 
	   	 	 if (FORCE_AC_EXPIRE){
	   	 		pref.setLong("AccessTokenExpiry", 0L);
	   	 		SdkConfig.tokenExpiredTime = 0L;
	   	 	 }

		     finish();	 
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
		Log.i("DebugSettingsPage", "Unmasked str: " + unmaskedStr );
		// Mask middle five characters
		int midPos = unmaskedStr.length()/2;
		String maskedStr = unmaskedStr.substring(0, midPos) + "*****" + unmaskedStr.substring(midPos + 5);
		Log.i("DebugSettingsPage", "Masked str: " + maskedStr );
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
