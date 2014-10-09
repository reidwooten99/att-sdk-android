package com.att.api.consentactivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

import com.att.api.error.InAppMessagingError;
import com.att.api.immn.listener.ATTIAMListener;
import com.att.api.oauth.OAuthService;
import com.att.api.rest.RESTException;

public class UserConsentActivity extends Activity implements ATTIAMListener{

	private String fqdn;
	private String fqdn_extend;
	private String clientId;
	private String clientSecret;
	private String appScope;
	private String redirectUri;
	private String byPassAndSuppress = "";
	
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
		 fqdn_extend = i.getStringExtra("fqdn_extend");
		 clientId = i.getStringExtra("clientId");
		 clientSecret =  i.getStringExtra("clientSecret");
		 appScope = i.getStringExtra("appScope");
		 redirectUri = i.getStringExtra("redirectUri");
		 byPassAndSuppress = i.getStringExtra("byPassAndsuppress");
		 
		 osrvc = new OAuthService(fqdn, clientId, clientSecret);

		 webView.clearFormData();
		 webView.clearCache(true);
		 webView.clearHistory();
		 webView.loadUrl("about:blank");
		 webView.clearSslPreferences();
		 webView.getSettings().setJavaScriptEnabled(true);
		 webView.getSettings().setAppCacheEnabled(false);
		 webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		 Log.i("--- DEBUG ---", fqdn + fqdn_extend + "?client_id=" + clientId + "&scope=" + appScope + "&redirect_uri=" + redirectUri + byPassAndSuppress);
		 webView.loadUrl(fqdn + fqdn_extend + "?client_id=" + clientId + "&scope=" + appScope + "&redirect_uri=" + redirectUri + byPassAndSuppress);
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
					onError(errorObj);	;
				}																			
			}
    	}	
    }

	@Override
	public void onSuccess(Object response) {
	}

	@Override
	public void onError(final InAppMessagingError error) {
		
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (null != iamListener) {
					iamListener.onError(error);
				}
			}
		});	
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("ErrorMessage", error.getErrorMessage());
		setResult(RESULT_CANCELED,returnIntent);
		finish();
	}	
}
