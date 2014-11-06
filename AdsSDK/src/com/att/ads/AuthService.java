package com.att.ads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.att.ads.util.EncryptDecrypt;
import com.att.ads.util.HttpClientFactory;
import com.att.ads.util.Preferences;
import com.att.ads.util.Utils;

/**
 * Authorization operation performed and access tokens are stored in shared
 * preferences.
 * 
 * @author ATT
 */
public class AuthService {

	private Context context = null;
	private static final String TAG = "AuthService";
	private boolean isAuthSuccess = false;

	/**
	 * Constructor sets the context object.
	 * 
	 * @param context
	 */
	public AuthService(Context context) {
		this.context = context;
	}

	/**
	 * Return the Authorization status.
	 * 
	 * @return isAuthSuccess true authentication success else failure.
	 */
	public boolean isAuthSuccess() {
		return isAuthSuccess;
	}

	/**
	 * Set the Authorization status value(true/false).
	 * 
	 * @param isAuthSuccess
	 */
	public void setAuthSuccess(boolean isAuthSuccess) {
		this.isAuthSuccess = isAuthSuccess;
	}

	/**
	 * Returns Access token which is calculated from App Key and secret key.
	 * These App key and secret key retrieved from preferences, and passed to
	 * the oauthService method which returns the JSON response and it parse JSON
	 * response and store access token, Refresh token and expires_in values in
	 * shared preference in encrypted format.
	 * 
	 * @param adView
	 *            reference of the ATTAdView object
	 * 
	 * @return accessToken
	 */
	public String getAccessToken(ATTAdView adView) {
		String appKey = null;
		String secret = null;
		StringBuffer oath_url = null;
		Preferences pref = new Preferences(context);
		String accessToken = null;
		String refreshToken = null;
		long expiresInd = Long.valueOf(pref.getString("expires_in", "-1"));

		//
		if (expiresInd != -1 && !Utils.isExpires(expiresInd)) {
			String accessTokenEnc = pref.getString("access_token", null);
			try {
				accessToken = EncryptDecrypt.getDecryptedValue(accessTokenEnc,
						EncryptDecrypt.getSecretKeySpec("access_token"));
			} catch (Exception e) {
				e.printStackTrace();
				if (adView != null)
					adView.setResult(null, new ATTAdViewError(
							ATTAdViewError.ERROR_OAUTH_ERROR, e.toString()
									+ ": " + e.getMessage(), e));
				return null;
			}

		} else {
			try {
				String appKeyEnc = pref.getString("app_key", null);
				if (null != appKeyEnc) {
					appKey = EncryptDecrypt.getDecryptedValue(appKeyEnc,
							EncryptDecrypt.getSecretKeySpec("app_key"));
				} else {
					Log.e(TAG, "Unable to get the app_key info from preferences");
					if (adView != null)
						adView.setResult(null, new ATTAdViewError(
								ATTAdViewError.ERROR_PARAMETER_ERROR,  Constants.STR_APP_KEY_PROBLEM));
					return null;
				}

				String secretEnc = pref.getString("app_secret", null);
				if (null != secretEnc) {
					secret = EncryptDecrypt.getDecryptedValue(secretEnc,
							EncryptDecrypt.getSecretKeySpec("app_secret"));
				} else {
					Log.e(TAG, "Unable to get the secret info from preferences");
					if (adView != null)
						adView.setResult(null, new ATTAdViewError(
								ATTAdViewError.ERROR_PARAMETER_ERROR,  Constants.STR_SECRET_PROBLEM));
					return null;
				}

				oath_url = new StringBuffer(Constants.OAUTH_URL)
						.append("?scope=").append(Constants.SCOPE)
						.append("&client_id=").append(appKey)
						.append("&client_secret=").append(secret);
			} catch (Exception e) {
				Log.e(TAG, "Exception in decrypt keys :" + e.getStackTrace());
				if (adView != null)
					adView.setResult(null, new ATTAdViewError(
							ATTAdViewError.ERROR_OAUTH_ERROR, e.toString()
									+ ": " + e.getMessage(), e));
				return null;
			}
			if (expiresInd == -1) {
				// First time
				oath_url.append("&grant_type=").append(Constants.GRANT_TYPE);
			} else if (Utils.isExpires(expiresInd)) {
				// Token Expires
				oath_url.append("&grant_type=").append(Constants.REFRESH_TOKEN);
				String refreshTokenEnc = pref.getString("refresh_token", null);
				try {
					refreshToken = EncryptDecrypt.getDecryptedValue(
							refreshTokenEnc,
							EncryptDecrypt.getSecretKeySpec("refresh_token"));
					oath_url.append("&refresh_token=").append(refreshToken);
				} catch (Exception e) {
					e.printStackTrace();
					if (adView != null)
						adView.setResult(null, new ATTAdViewError(
								ATTAdViewError.ERROR_OAUTH_ERROR, e.toString()
										+ ": " + e.getMessage(), e));
					return null;
				}
			}

			//
			JSONObject object = oauthService(oath_url.toString(), adView);
			if (null != object) {
				try {
					accessToken = object.getString("access_token");
					refreshToken = object.getString("refresh_token");
					// get expires_in in seconds.
					int expiresIn = object.getInt("expires_in");
					long expiresInSeconds = 0;
					if (expiresIn != 0) {
						expiresInSeconds = Utils.getExpiresTimeInSeconds(expiresIn);
					}
					// Stores in milliseconds of respective time
					pref.setString("expires_in", String.valueOf(expiresInSeconds));
				} catch (JSONException e) {
					e.printStackTrace();
					if (adView != null)
						adView.setResult(null, new ATTAdViewError(
								ATTAdViewError.ERROR_OAUTH_ERROR, e.toString()
										+ ": " + e.getMessage(), e));
				}

				if (accessToken != null && accessToken.length() > 0
						&& refreshToken != null && refreshToken.length() > 0) {
					try {
						pref.setString("access_token", EncryptDecrypt
								.getEncryptedValue(accessToken, EncryptDecrypt
										.getSecretKeySpec("access_token")));

						pref.setString("refresh_token", EncryptDecrypt
								.getEncryptedValue(refreshToken, EncryptDecrypt
										.getSecretKeySpec("refresh_token")));
					} catch (Exception e) {
						e.printStackTrace();
						if (adView != null)
							adView.setResult(
									null,
									new ATTAdViewError(
											ATTAdViewError.ERROR_OAUTH_ERROR, e
													.toString()
													+ ": "
													+ e.getMessage(), e));
					}
				}
			}
		}

		return accessToken;
	}

	/**
	 * Returns {@link JSONObject} for provided authentication URL. This
	 * operation is handled in thread-safe client to server communication.
	 * 
	 * @param oath_url
	 * @param adView
	 * @return JSONObject
	 */
	public JSONObject oauthService(String oath_url, ATTAdView adView) {
		HttpPost post = null;
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		InputStream instream = null;
		BufferedReader reader = null;
		HttpClient client = HttpClientFactory.getThreadSafeClient();
		JSONObject object = null;

		try {
			Log.d(TAG, "OATH Service Reuest: " + oath_url);
			post = new HttpPost(oath_url);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			response = client.execute(post);
			httpEntity = response.getEntity();
			StringBuffer tempBuffer = new StringBuffer();
			if (httpEntity != null) {
				instream = httpEntity.getContent();
				reader = new BufferedReader(new InputStreamReader(instream));
				String str = null;
				while ((str = reader.readLine()) != null) {
					tempBuffer.append(str);
				}
				if (response.getStatusLine().getStatusCode() == 200) {
					setAuthSuccess(true);
					Log.d(TAG, "OAUTH status :success");

					object = (JSONObject) new org.json.JSONTokener(
							tempBuffer.toString()).nextValue();
					Log.d(TAG, "OATH Service Response:" + object.toString());
				} else {
					StringBuffer errInfo = new StringBuffer(
							"OAUTH service unsuccessful due to the code :"
									+ response.getStatusLine().getStatusCode());
					errInfo.append(" and response: " + tempBuffer.toString());
					Log.e(TAG, errInfo.toString());
					if (adView != null)
						adView.clearCache();
						adView.setResult(null,
								new ATTAdViewError(
										ATTAdViewError.ERROR_OAUTH_ERROR,
										errInfo.toString()));
				}

			} else {
				if (adView != null)
					adView.clearCache();
					adView.setResult(null, new ATTAdViewError(
							ATTAdViewError.ERROR_OAUTH_ERROR,
							Constants.STR_EMPTY_SERVER_RESPONSE));
			}

		} catch (Exception e) {
			Log.e(TAG, "Exception status :" + e.fillInStackTrace());
			if (adView != null){
			
				adView.clearCache();
				adView.setResult(null, new ATTAdViewError(
						ATTAdViewError.ERROR_OAUTH_ERROR, e.toString() + ": "
								+ e.getMessage(), e));
			}
		} finally {
			post.abort();
			try {
				if (instream != null) {
					instream.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage() + e.fillInStackTrace());
				if (adView != null)
					adView.setResult(null, new ATTAdViewError(
							ATTAdViewError.ERROR_OAUTH_ERROR, e.toString()
									+ ": " + e.getMessage(), e));
			}
			//client = null;
		}

		return object;
	}
	/**
	 * Revokes the current access token and refresh token stored in preferences.
	 * The app key, secret key and refresh token are retrieved from preferences, and 
	 * passed to the oauthService method which makes the revoke refresh token call.
	 * The access token, refresh token and expires_in values are cleaned from the
	 * shared preference.
	 * 
	 * @param none
	 * 
	 * @return true or false
	 */
	public boolean revokeToken() {
		String appKey = null;
		String secret = null;
		StringBuffer oath_url = null;
		Preferences pref = new Preferences(context);
		String refreshToken = null;
		HttpPost post = null;
		HttpResponse response = null;
		HttpEntity httpEntity = null;
		InputStream instream = null;
		BufferedReader reader = null;
		HttpClient client = HttpClientFactory.getThreadSafeClient();

		try {
			String appKeyEnc = pref.getString("app_key", null);
			if (null != appKeyEnc) {
				appKey = EncryptDecrypt.getDecryptedValue(appKeyEnc,
						EncryptDecrypt.getSecretKeySpec("app_key"));
			} else {
				Log.e(TAG, "Unable to get the app_key info from preferences");
				return false;
			}

			String secretEnc = pref.getString("app_secret", null);
			if (null != secretEnc) {
				secret = EncryptDecrypt.getDecryptedValue(secretEnc,
						EncryptDecrypt.getSecretKeySpec("app_secret"));
			} else {
				Log.e(TAG, "Unable to get the secret info from preferences");
				return false;
			}

			oath_url = new StringBuffer(Constants.OAUTH_URL)
					.append("?client_id=").append(appKey)
					.append("&client_secret=").append(secret);
		} catch (Exception e) {
			Log.e(TAG, "Exception in decrypt keys :" + e.getStackTrace());
			return false;
		}
		
		String refreshTokenEnc = pref.getString("refresh_token", null);
		try {
			refreshToken = EncryptDecrypt.getDecryptedValue(
					refreshTokenEnc,
					EncryptDecrypt.getSecretKeySpec("refresh_token"));
			oath_url.append("&token=").append(refreshToken)
					.append("&token_type_hint=refresh_token");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		try {
			Log.d(TAG, "Revoke Token Request: " + oath_url);
			post = new HttpPost(oath_url.toString());
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			response = client.execute(post);
			httpEntity = response.getEntity();
			StringBuffer tempBuffer = new StringBuffer();
			if (httpEntity != null) {
				instream = httpEntity.getContent();
				reader = new BufferedReader(new InputStreamReader(instream));
				String str = null;
				while ((str = reader.readLine()) != null) {
					tempBuffer.append(str);
				}
				if (response.getStatusLine().getStatusCode() == 200) {
					Log.d(TAG, "Revoke Token status :success");					
					// Store expiresIn as -1, make access token and refresh token blank.
					pref.setString("expires_in", "-1"); // get new token next time.
					pref.setString("access_token", EncryptDecrypt
							.getEncryptedValue("", EncryptDecrypt
									.getSecretKeySpec("access_token")));
					pref.setString("refresh_token", EncryptDecrypt
							.getEncryptedValue("", EncryptDecrypt
									.getSecretKeySpec("refresh_token")));

				} else {
					StringBuffer errInfo = new StringBuffer(
							"Revoke Token unsuccessful due to the code :"
									+ response.getStatusLine().getStatusCode());
					errInfo.append(" and response: " + tempBuffer.toString());
					Log.e(TAG, errInfo.toString());
				}

			} else {
				Log.d(TAG, "Revoke Token: failed to create HttpEntity.");
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception status :" + e.fillInStackTrace());
			return false;
		} finally {
			post.abort();
			try {
				if (instream != null) {
					instream.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage() + e.fillInStackTrace());
			}
		}		

		return true;
	}
}
