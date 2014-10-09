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
import android.widget.EditText;
import android.widget.TextView;

/************************************************
 * This Activity is for debugging only
 * 
 * @author am017p
 *
 */

public class DebugSettings extends Activity {
	
	private Button m_forceOffNetButton = null;
	private Button m_suppressButton = null;
	private Button m_clearCookiesButton = null;
	private Button m_applyButton = null;
	private Button m_forceCheckBox = null;
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
		setContentView(R.layout.preset_for_debug_page);
		
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
		
		m_forceOffNetButton = (Button) findViewById(R.id.forceOffNetCheckBox);
		if (bypass){
			m_forceOffNetButton.setBackgroundResource(R.drawable.check_mark);
			OFF_NET = true;
		}
		
		m_suppressButton = (Button) findViewById(R.id.forceSuppressCheckBox);
		if (suppressed){
			m_suppressButton.setBackgroundResource(R.drawable.check_mark);
			SUPPRESS = true;
		}
		
		m_forceOffNetButton.setOnClickListener(new Button.OnClickListener(){
     	 	 public void onClick(View v) {
     	 		 
	     	 		if (!OFF_NET){
		     	 		OFF_NET = true;
		     	 		m_forceOffNetButton.setBackgroundResource(R.drawable.check_mark);
	     	 		}
	     	 		else{
	     	 			OFF_NET = false;
		     	 		m_forceOffNetButton.setBackgroundColor(Color.LTGRAY);
	     	 		}
	     	 		
	     	 	   if (CLEAR_COOKIES){ 
	    	 		   m_clearCookiesButton.setBackgroundColor(Color.LTGRAY);
	    	 		   CLEAR_COOKIES = false;
	     	 	   }     	  
     	  	 }
        });
		
		
		m_suppressButton.setOnClickListener(new Button.OnClickListener(){
    	 	 public void onClick(View v) {
    	 
    	 		   if (!SUPPRESS){
    	 			  m_suppressButton.setBackgroundResource(R.drawable.check_mark);
	    	 		  SUPPRESS = true; 
    	 		   }
    	 		   else {
    	 			   m_suppressButton.setBackgroundColor(Color.LTGRAY);
	    	 		   SUPPRESS = false;
    	 		   }
    	 		   
    	 		  if (CLEAR_COOKIES){ 
    	 		     m_clearCookiesButton.setBackgroundColor(Color.LTGRAY);
    	 		     CLEAR_COOKIES = false;
    	 		  }  
    	 	}
       });
		
		
	   m_clearCookiesButton = (Button) findViewById(R.id.clearCookiesCheckBox);
	   m_clearCookiesButton.setOnClickListener(new Button.OnClickListener(){
		   	 public void onClick(View v) {
		   		 
		   		if (!CLEAR_COOKIES){
		   			m_clearCookiesButton.setBackgroundResource(R.drawable.check_mark);  
		   			CLEAR_COOKIES = true;
		   		    FORCE_AC_EXPIRE = false;
		   		    OFF_NET = false;
		   		    SUPPRESS = false;
		   		    m_forceCheckBox.setBackgroundColor(Color.LTGRAY);
		   		    m_curACTime.setText("0");
		   		    m_curAC.setText("");
		   		    m_refreshToken.setText("");
		   		    m_suppressButton.setBackgroundColor(Color.LTGRAY);
			   	    m_forceOffNetButton.setBackgroundColor(Color.LTGRAY);
			   	       
  	 		   }
  	 		   else {
  	 			    m_clearCookiesButton.setBackgroundColor(Color.LTGRAY);
  	 			    CLEAR_COOKIES = false;
  	 			    
  	 		   }
		   		
	   	 	}	 
	   });
	   
	   
	   m_forceCheckBox = (Button) findViewById(R.id.forceCheckBox);
	   m_forceCheckBox.setOnClickListener(new Button.OnClickListener(){
	   	     public void onClick(View v) {
	   	    	if (!FORCE_AC_EXPIRE){
	   		       FORCE_AC_EXPIRE = true;
	   		       m_forceCheckBox.setBackgroundResource(R.drawable.check_mark);
	   		       m_curACTime.setText("0");
	   	    	}
	   	    	else {
	   	    	     FORCE_AC_EXPIRE = false;
	   		         m_forceCheckBox.setBackgroundColor(Color.LTGRAY);
	   		         m_curACTime.setText(ACExpiredTime);	
	   	    	}
	   		    
	   	 	 }
	   });
		
	   
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
		m_forceCheckBox.setBackgroundColor(Color.LTGRAY);
	}
	
	public String middleMaskedStr( String unmaskedStr){
		
		int midPos = unmaskedStr.length()/2;
		String maskedStr = unmaskedStr.substring(0, midPos) + "*****" + unmaskedStr.substring(midPos + 5);
		
		return maskedStr;
	}	
	
}
