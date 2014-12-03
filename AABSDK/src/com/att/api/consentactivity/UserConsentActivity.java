package com.att.api.consentactivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.att.api.error.AttSdkError;
import com.att.api.oauth.OAuthService;
import com.att.api.rest.RESTException;

@SuppressLint({ "InlinedApi", "SetJavaScriptEnabled" })
public class UserConsentActivity extends Activity {


	private String fqdn;
	private String clientId;
	private String clientSecret;
	private String appScope;
	private String redirectUri;
	private String customParam = ""; // Default to Blank
	OAuthService osrvc;
	WebView webView ;
	protected Handler handler = new Handler();
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		LayoutParams llParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		linearLayout.setLayoutParams(llParams);
		setContentView(linearLayout);
		
		WebView webView = new WebView(this);
		webView.setLayoutParams(llParams);
		linearLayout.addView(webView);
				
		 Intent i = getIntent();
		 fqdn = i.getStringExtra("fqdn");
		 clientId = i.getStringExtra("clientId");
		 clientSecret =  i.getStringExtra("clientSecret");
		 appScope = i.getStringExtra("appScope");
		 redirectUri = i.getStringExtra("redirectUri");
		 customParam = "";
		 String customParamValue = i.getStringExtra("customParam");
		 if (customParamValue != null && customParamValue.length() > 0) {
			 customParam = "&custom_param=" + customParamValue;
		 }
		
		 osrvc = new OAuthService(fqdn, clientId, clientSecret);

		
		webView.clearFormData();
		webView.clearCache(true);
		webView.clearHistory();
		webView.clearView();
		webView.clearSslPreferences();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAppCacheEnabled(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.loadUrl(fqdn +"/oauth/v4/authorize?client_id=" + clientId + "&scope=" + appScope + "&redirect_uri=" + redirectUri + customParam);
		webView.setWebViewClient(new myWebViewClient()); 	
	}
	private class myWebViewClient extends WebViewClient {
		
		@SuppressWarnings("unused")
		AttSdkError errorObj = new AttSdkError();
		
		 @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	Log.i("shouldOverrideUrlLoading", "shouldOverrideUrlLoading() in the MyWebViewClient "+ url);
	    	if(url.contains("sms:")) {
	    		String smsUrl = url;
				String[] splitNumber = smsUrl.split(":");
				String phNumber = splitNumber[1];
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setType("vnd.android-dir/mms-sms");
				intent.putExtra("address", phNumber);
				startActivity(intent);
				return true;
	    	} else 
	    		return false;
	    }
		
    	@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.i("onPageStarted", "Start : " + url);
			super.onPageStarted(view, url, favicon);
			String encodedURL;
			try {
				encodedURL = URLEncoder.encode(url, "UTF-8");
				Log.i("onPageStarted", "encodedURL: " + encodedURL);
	
				if(url.contains("code=")) {				
					String encodedURLSplits[] = encodedURL.split("code%3D");
					if(encodedURLSplits.length > 1) {
						String oAuthCode = encodedURLSplits[1];
	
						Log.i("onPageStarted", "oAuthCode: " + oAuthCode);
						
						Intent returnIntent = new Intent();
						returnIntent.putExtra("oAuthCode", oAuthCode);
						setResult(RESULT_OK,returnIntent);
						finish();
					}
				} else if(url.contains("error=")) {
					String encodedURLSplits[] = encodedURL.split("error%3D");
					if(encodedURLSplits.length > 1) {
						String errorMessage = encodedURLSplits[1];
	
						Log.i("onPageStarted", "Error: " + errorMessage);
						
						Intent returnIntent = new Intent();
						returnIntent.putExtra("ErrorMessage", errorMessage);
						setResult(RESULT_CANCELED,returnIntent);
						finish();
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}	
    }
}
