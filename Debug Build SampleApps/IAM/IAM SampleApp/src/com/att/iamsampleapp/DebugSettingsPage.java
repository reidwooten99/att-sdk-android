package com.att.iamsampleapp;
import com.att.api.util.Preferences;
import com.att.api.util.Sdk_Config;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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
	private TextView m_refreshToken = null;
	private TextView m_curACTime = null;
	private boolean OFF_NET = false;
	private boolean SUPPRESS = false;
	private boolean CLEAR_COOKIES = false;
	private boolean FORCE_AC_EXPIRE = false;
	private boolean tokenStr_masked = false;
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
		tokenStr = pref.getString("Token", Sdk_Config.none);
		
		if (!tokenStr.contentEquals(Sdk_Config.none)){
			masked_tokenStr = middleMaskedStr(tokenStr);
			tokenStr_masked = true;
			m_curAC.setText(masked_tokenStr);
		}
		
		m_refreshToken = (TextView)findViewById(R.id.refreshToken);
        refreshStr = pref.getString("RefreshToken", Sdk_Config.none);
		
		if (!refreshStr.contentEquals(Sdk_Config.none)){
			masked_refreshStr = middleMaskedStr(refreshStr);
			m_refreshToken.setText(masked_refreshStr);
		}
		
		m_curACTime = (TextView)findViewById(R.id.curACTime);
		ACExpiredTime = String.valueOf(pref.getLong("AccessTokenExpiry", 0L));
		m_curACTime.setText(ACExpiredTime);
		
		String presetedStr = pref.getString(Sdk_Config.preset, Sdk_Config.none);
		
		if (!presetedStr.contains(Sdk_Config.none)){
			suppressed = presetedStr.contains(Sdk_Config.suppressLndgPageStr);
			bypass  =    presetedStr.contains(Sdk_Config.byPassOnNetStr);
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
	   	 	    pref.setString("PRESET",Sdk_Config.none);  
	   	 	    pref.setLong("AccessTokenExpiry", 0L);
  	 		    pref.setString("Token", Sdk_Config.none);
			    pref.setString("RefreshToken", Sdk_Config.none);
			    Sdk_Config.tokenExpiredTime = 0L;
			    Sdk_Config.refreshToken = "";
			    Sdk_Config.token = ""; 
	   	 	 }
	   	 	 else {
			   	 if (OFF_NET && SUPPRESS){
			   	 		pref.setString("PRESET",Sdk_Config.byPassOnNetANDsuppressLandingStr );
			   	 }
			   	 else
			   	   if ((!OFF_NET) && SUPPRESS){
			   		  pref.setString("PRESET", Sdk_Config.suppressLndgPageStr);
			   	 }
			   	 else
				   if (OFF_NET && (!SUPPRESS)){
					   pref.setString("PRESET", Sdk_Config.byPassOnNetStr);	 		 
				 }
				 else{
					   pref.setString("PRESET", Sdk_Config.none);
				 }
			   	 
			     String ACToken = m_curAC.getText().toString().trim();
			     if (tokenStr_masked){
			    	  if (ACToken.contentEquals(masked_tokenStr)){
			    		  ACToken = tokenStr;
			    		  tokenStr_masked = false;
			    	  } 
			     }
			     
	   	 		 pref.setString("Token", ACToken);
	   	 		 
	   	 		 String freshTokenEdt = refreshStr;
				 pref.setString("RefreshToken", freshTokenEdt );
				 Sdk_Config.token = ACToken;
				 Sdk_Config.refreshToken = freshTokenEdt;  
			   	 
	   	 	 }
	   	 	 
	   	 	 if (FORCE_AC_EXPIRE){
	   	 		pref.setLong("AccessTokenExpiry", 0L);
	   	 		Sdk_Config.tokenExpiredTime = 0L;
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
		// Mask middle five characters
		int midPos = unmaskedStr.length()/2;
		String maskedStr = unmaskedStr.substring(0, midPos) + "*****" + unmaskedStr.substring(midPos + 5);
		
		return maskedStr;
	}	
	
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
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
