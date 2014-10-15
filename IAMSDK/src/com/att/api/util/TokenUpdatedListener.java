package com.att.api.util;

import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.att.api.immn.service.IAMManager;
import com.att.api.oauth.OAuthToken;
import com.att.api.util.Preferences;
import com.att.api.immn.listener.AttSdkTokenUpdater;

// This is a sample implementation of AttSdkTokenUpdater.
// This implementation uses SharedPreferences to store the Token.
// You may choose to implement your own Token Persistence Logic or extend this class.
public class TokenUpdatedListener implements AttSdkTokenUpdater {	
	
	// Static names of setting stored in Preferences
	public static final String accessTokenSettingName = "AccessToken";
	public static final String refreshTokenSettingName = "RefreshToken";
	public static final String tokenExpirySettingName = "AccessTokenExpiry";
	public static final String customParamSettingName = "CustomParam";
	public static final String reduceExpiryBySettingName = "ReduceExpiryBy";
	
	public static String tokenDisplayString (final String tokenString) {
		int len = 0;
		String tokenDisplay = "";
		len = tokenString.length();
		if (len > 10) {
			tokenDisplay = tokenString.substring(0,10) + "********";
		}
		if (len > tokenDisplay.length()) {
			tokenDisplay += tokenString.substring(tokenDisplay.length());
		}
		
		return tokenDisplay;		
	}

	public static Context m_applicationContext = null;
	
	public TokenUpdatedListener(Context appContext) {
		m_applicationContext = appContext;
	}
	
	@Override
	public void onTokenUpdate(OAuthToken newToken) {
		UpdateSavedToken(newToken);
	}
	
	@Override
	public void onTokenDelete() {
		DeleteSavedToken();
	}
	
	public static void UpdateSavedToken(OAuthToken token) {
		
		Preferences prefs = new Preferences(m_applicationContext);
		if (prefs != null && token != null) {
			prefs.setString(accessTokenSettingName, token.getAccessToken());
			prefs.setString(refreshTokenSettingName, token.getRefreshToken());
			prefs.setLong(tokenExpirySettingName, token.getAccessTokenExpiry());

			Log.i("updateSavedToken", "Saved Token: " + tokenDisplayString(token.getAccessToken()));
			Log.i("ActualTokenExpiry", new Date(token.getAccessTokenExpiry()*1000).toString());
			Log.i("AdjustedTokenExpiry", new Date((token.getAccessTokenExpiry() - IAMManager.GetReduceTokenExpiryInSeconds_Debug())*1000).toString());
		}		
	}
	
	public static void DeleteSavedToken() {		
		Preferences prefs = new Preferences(m_applicationContext);
		if (prefs != null) {
			prefs.setString(accessTokenSettingName, "");
			prefs.setString(refreshTokenSettingName, "");
			prefs.setLong(tokenExpirySettingName, 0);
			Log.i("deleteSavedToken", "Deleted Saved Token.");
		}	
		// Logout from the application and restart.
		Log.e("Invalid Token", "Restarting the application");
		
		System.exit(0);
	}	
}