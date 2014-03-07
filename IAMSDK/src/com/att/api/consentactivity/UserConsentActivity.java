package com.att.api.consentactivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.att.api.error.InAppMessagingError;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthService;
import com.att.api.oauth.OAuthToken;
//import com.example.iamsdk.MessageAppActivity;
import com.example.iamsdk.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class UserConsentActivity extends Activity implements ATTIAMListener{

	private String fqdn;
	private String clientId;
	private String clientSecret;
	OAuthService osrvc;
	WebView webView ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_consent);
		
		 webView = (WebView) findViewById(R.id.userConsentView);
			
		 Intent i = getIntent();
		 fqdn = i.getStringExtra("fqdn");
		 clientId = i.getStringExtra("clientId");
		 clientSecret =  i.getStringExtra("clientSecret");
		
		 osrvc = new OAuthService(fqdn, clientId, clientSecret);

		
		webView.clearFormData();
		webView.clearCache(true);
		webView.clearHistory();
		webView.clearView();
		webView.clearSslPreferences();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAppCacheEnabled(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.loadUrl("https://api.att.com/oauth/authorize?client_id=" + clientId + "&scope=IMMN,MIM&redirect_uri=https://developer.att.com");
		//webView.loadUrl("https://ewr1-auth-api.att.com/permissions");
		webView.setWebViewClient(new myWebViewClient()); 	
	}
	private class myWebViewClient extends WebViewClient {
		
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
			// TODO Auto-generated method stub
			Log.i("onPageStarted", "Start : " + url);
			super.onPageStarted(view, url, favicon);
			if(url.contains("code")) {
			//if(url.contains("xxxx")) {
				
				String encodedURL;
				OAuthToken accessToken ;
				try {
					encodedURL = URLEncoder.encode(url, "UTF-8");
					Log.i("onPageStarted", "encodedURL: " + encodedURL);

					String encodedURLSplits[] = encodedURL.split("code%3D");
					String oAuthCode = encodedURLSplits[1];

					Log.i("onPageStarted", "oAuthCode: " + oAuthCode);
					
					Intent returnIntent = new Intent();
					returnIntent.putExtra("oAuthCode", oAuthCode);
					setResult(RESULT_OK,returnIntent);
					finish();
											
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} 
    	}	
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_consent, menu);
		return true;
	}

	@Override
	public void onSuccess(Object adViewResponse) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(InAppMessagingError error) {
		// TODO Auto-generated method stub
		
	}
}
