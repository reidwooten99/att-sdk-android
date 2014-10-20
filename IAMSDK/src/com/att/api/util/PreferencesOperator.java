package com.att.api.util;

import android.content.Context;

public class PreferencesOperator {

	Preferences prefs;

	/**
	 * This constructor Initialize and hold the contents of the preferences file
	 * 'name', returning a SharedPreferences through which you can retrieve and
	 * modify its values. Only one instance of the SharedPreferences object is
	 * returned to any callers for the same name, meaning they will see each
	 * other's edits as soon as they are made. *
	 * 
	 * @param context
	 */
	public PreferencesOperator(Context m_context) {
		
		    prefs = new Preferences(m_context);
	}
	
   public void groupTokenRetrieve(String m_AccessToken, String m_RefreshToken, 
		    							long m_AccessTokenExpiredTime) {
	        m_AccessToken = prefs.getString("AccessToken", SdkConfig.none );   
	        m_RefreshToken = prefs.getString("RefreshToken", SdkConfig.none );
	        m_AccessTokenExpiredTime = prefs.getLong("AccessTokenExpiry", 0L);
	}

    public void groupTokenUpdate(String m_AccessToken, String m_RefreshToken, 
		    							long m_AccessTokenExpiredTime) {
	        prefs.setString("Token", m_AccessToken );
			prefs.setString("RefreshToken", m_RefreshToken );
			prefs.setLong("AccessTokenExpiry", m_AccessTokenExpiredTime);
	}
    
    public void singleStrUpdate(String m_Key, String m_value) {
           prefs.setString(m_Key, m_value);
    }
    
    public void groupAccountUpdate(String m_fqdn, String m_clientID, 
		    									String m_secretKey){
	    prefs.setString("FQDN", m_fqdn);
		prefs.setString("clientID", m_clientID);
		prefs.setString("secretKey", m_secretKey);
    }
    
    public String singleStrRetrieve(String m_Key) {
          return prefs.getString(m_Key, SdkConfig.none);
    }
    
    public long singleLongRetrieve(String m_Key) {
        return prefs.getLong(m_Key, 0L);
    }
    
    public void clearPresets() {
    	prefs.setString(SdkConfig.preset, SdkConfig.none);  
   }
    
    public void setAccessTokenExpiry(long n_long) {
    	prefs.setLong("AccessTokenExpiry", n_long);  
   }
    
    public void clearEntirePreferences() {
    	prefs.setString(SdkConfig.preset, SdkConfig.none);  
    	prefs.setString("FQDN", SdkConfig.none);
 		prefs.setString("clientID", SdkConfig.none);
 		prefs.setString("secretKey", SdkConfig.none);
 		prefs.setString("Token", SdkConfig.none );
		prefs.setString("RefreshToken", SdkConfig.none );
		prefs.setLong("AccessTokenExpiry", 0L);
   }
    
}
