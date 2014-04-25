package com.att.api.consentactivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.att.api.error.InAppMessagingError;
import com.att.api.aab.listener.ATTIAMListener;
import com.att.api.oauth.OAuthService;
import com.att.api.rest.RESTException;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class UserConsentActivity extends Activity {


	private String fqdn;
	private String clientId;
	private String clientSecret;
	private String appScope;
	private String redirectUri;
	OAuthService osrvc;
	WebView webView ;
	private ATTIAMListener iamListener;
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
		
		 osrvc = new OAuthService(fqdn, clientId, clientSecret);

		
		webView.clearFormData();
		webView.clearCache(true);
		webView.clearHistory();
		webView.clearView();
		webView.clearSslPreferences();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAppCacheEnabled(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.loadUrl(fqdn +"/oauth/authorize?client_id=" + clientId + "&scope=" + appScope + "&redirect_uri=" + redirectUri);
		webView.setWebViewClient(new myWebViewClient()); 	
	}
	private class myWebViewClient extends WebViewClient {
		
		InAppMessagingError errorObj = new InAppMessagingError();
		
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
			if(url.contains("code=")) {				
				String encodedURL;
				try {
					encodedURL = URLEncoder.encode(url, "UTF-8");
					Log.i("onPageStarted", "encodedURL: " + encodedURL);

					String encodedURLSplits[] = encodedURL.split("code%3D");
					if(encodedURLSplits.length > 1) {
						String oAuthCode = encodedURLSplits[1];
	
						Log.i("onPageStarted", "oAuthCode: " + oAuthCode);
						
						Intent returnIntent = new Intent();
						returnIntent.putExtra("oAuthCode", oAuthCode);
						setResult(RESULT_OK,returnIntent);
						finish();
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(url.contains("error=")) {
				try {
					throw new RESTException("Incorrect Url! Unable to open the Authorization page." +
											"Check the APP_KEY,APP_SECRET,APP_SCOPE and REDIRECT_URI");
				} catch (RESTException e) {
					// TODO Auto-generated catch block
					errorObj = new InAppMessagingError(e.getMessage());
				}																			
			} else if(url.contains("network")) {
				String oAuthCode = "abcd";
				Log.i("onPageStarted", "oAuthCode: " + oAuthCode);				
				Intent returnIntent = new Intent();
				returnIntent.putExtra("oAuthCode", oAuthCode);
				setResult(RESULT_OK,returnIntent);
				finish();
			}
    	}	
    }

	
}
