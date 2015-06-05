package com.att.ads;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.att.ads.controllers.NetworkController;
import com.att.ads.listeners.ATTAdViewListener;
import com.att.ads.model.AdServiceRequest;
import com.att.ads.util.EncryptDecrypt;
import com.att.ads.util.Preferences;
import com.att.ads.util.Utils;

/**
 * <p>Use the ATTAdView class to embed advertisement content in your application.  It is extending & customizing 
 * a standard web view control. To do so, you simply create an instance of the ATTAdView object and add it to 
 * a layout. The application key and secret must be configured. An instance of ATTAdView is the means for 
 * displaying advertisements from the ad publisher's site.
 *  
 * <p>Ads can be simple text or image types. Ad view handles user interactions with the advertisement content. 
 * Ads generally have links that allow users to visit web sites.  To control all interactions implement the
 * {@link ATTAdViewListener}.
 * 
 * <p>Advanced ad view customization is supported.  Ad content can be filtered using the premium property.  
 * Use the properties minSize and maxSize to configure ad content size in server response.  Also you can 
 * set the search parameters using the keywords. And there are variety of optional properties can be
 * set that assist the back-end in delivering content targeted to the application user.
 * 
 * <h3>Basic usage</h3>
 * 
 * <pre>
 * 		ATTAdView adView = new ATTAdView(this, appKey, secret, udid, category);
 * 		adFrameLayout.addView(adView);
 * 		adView.initOrRefresh();  
 * </pre>
 * 
 * <h3>Advanced usage </h3>
 * 
 * <p>Set the {@link ATTAdViewListener} listener object to an ATTAdView in order
 * to listen and customize the processing of ad content.
 * <pre>
 * 		adView.setAdViewListener(new ATTAdViewListener() {
 *			public void onSuccess(String adViewResponse) {
 *				Log.d(TAG, "onSuccess():"+adViewResponse);
 *			}
 *
 *			public void onError(ATTAdViewError error) {*				
 *				StringBuffer res = new StringBuffer();
 *				res.append(error.getType());
 *				res.append(": ");
 *				res.append(error.getMessage());
 *				Exception e = error.getException(); 
 *				if(null != e){
 *					res.append("\n Exception :\n ");
 *					res.append(e);
 *				}
 *				Log.d(TAG, "onError(): "+res.toString());		
 *			}
 *		});
 * </pre>
 * 
 * <p>Set the Application developer reload Ad time to change the default configured i.e 2 minutes.
 * <pre>
 * 		adView.setAdReloadPeriod(60);//means changed to 1 minute.
 * </pre>
 * @author ATT
 *
 */
@SuppressLint("SetJavaScriptEnabled")
public class ATTAdView extends WebView {

	private static final String TAG = "ATTAdView";
	private AdServiceRequest adSvcReq = null;
	private AdLog adLog = new AdLog(this);
	private Preferences pref = null;
	protected RefreshTask refreshTask;
	private Timer refreshTimer;
	private boolean isProd;
	public boolean isManualCall;
	private String htmlAdSvcResponse;
	private String udid;
	private ATTAdViewListener adViewListener;
	protected Handler handler = new Handler();
	private NetworkController networkController = null;
	private int adReloadPeriod = 0;
	private boolean showFullScreenAd = false;
	private String userAgent ;

	/*
	 * Constructor used for tooling only
	 */
	public ATTAdView(Context context) {
		super(context);
		init(context, "key", "secret", "udid", "category");
	}

	/**
	 * Creation of viewer of advertising. This signature is used when creating
	 * an ad view in java code.
	 *
	 * @param context
	 *            - The reference to the context of Activity.
	 * @param appKey
	 *            - The application key of the publisher.
	 * @param secret
	 *            - The secret of the publisher.
	 * @param udid
	 *            - The udid of the publisher.
	 * @param category
	 *            - Preferred category of the publisher.
	 */
	public ATTAdView(Context context, String appKey, String secret, String udid, String category) {
		super(context);
		init(context, appKey, secret, udid, category);
	}

	private void init(Context context, String appKey, String secret, String udid, String category) {
		String getPrefAppKey;
		String getPrefSecret;
		setId(R.id.menu_settings);
		adSvcReq = new AdServiceRequest(adLog);
		pref = new Preferences(context);
		
		String appKeyEnc = pref.getString("app_key", null);
		String secretEnc = pref.getString("app_secret", null);
		
		try {
			if (null != appKeyEnc && null != secretEnc){
				getPrefAppKey = EncryptDecrypt.getDecryptedValue(appKeyEnc,
						EncryptDecrypt.getSecretKeySpec("app_key"));
				getPrefSecret = EncryptDecrypt.getDecryptedValue(secretEnc, 
						EncryptDecrypt.getSecretKeySpec("app_secret"));
				if(!(getPrefAppKey.equals(appKey) && (getPrefSecret.equals(secret))))
					clearCache();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		userAgent = getSettings().getUserAgentString();
		setUserAgent(userAgent);
		setAppKey(appKey);
		setSecret(secret);
		this.udid = udid;
		setCategory(category);
		this.setWebViewClient(new MyWebViewClient());
		networkController = new NetworkController(this, context);
	}
	
	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	Log.i(TAG, "shouldOverrideUrlLoading() in the MyWebViewClient - view.getHeight()"+view.getHeight());
	    	if(showFullScreenAd) {	    		
	    		//It is a smaller preview, so need to open bigger preview on tap.
	    		openFullScreenAd(view);
	    	} else {
		        // Already its a bigger preview, so launch Activity that handles URLs
		        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		        view.getContext().startActivity(intent);
	    	}
	        return true;
	    }
	    
		private void openFullScreenAd(WebView view) {
			
			//show dialog
			final Dialog dialog;
			
			dialog = new Dialog(view.getContext(), android.R.style.Theme_NoTitleBar);

			final RelativeLayout mainLayout = new RelativeLayout(view.getContext());
			mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			RelativeLayout.LayoutParams adLayoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			adLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			adLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			final WebView tempView = new WebView(view.getContext());
			tempView.setLayoutParams(adLayoutParams);

			// XSS stands for "cross-site scripting" which is a form of hacking and by enabling
			// JavaScript in your WebView you are opening up your application to such attacks.
			// If you are sure that cross-site scripting is not possible (e.g. your webview
			// generates its own content via an internal resource and does not actually access
			// pages on the WWW then simply suppress the warning by adding the Android annotation
			// SuppressLint above the activity declaration, @SuppressLint("SetJavaScriptEnabled")
			// Since the webview content is generated internally by adservice provider enabling the
			// javascript.
			tempView.getSettings().setJavaScriptEnabled(true);
			tempView.setWebViewClient(new WebViewClient() {
			    public boolean shouldOverrideUrlLoading(WebView view, String url) {
			        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			        view.getContext().startActivity(intent);
				    return true;
			    }
			});
			
			handler.post(new Runnable() {
				@Override
				public void run() {
					tempView.loadDataWithBaseURL(null, htmlAdSvcResponse, "text/html",
							"UTF-8", null);
				}
			});			

			mainLayout.addView(tempView);
			
			//Below code base is to add the close image button at top right corner.
			/*ImageButton closeButton = new ImageButton(this);
			closeButton.setImageResource(R.drawable.popup_close_btn);
			RelativeLayout.LayoutParams closeLayoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			closeButton.setLayoutParams(closeLayoutParams);*/

			//Below code base is to add the close button at bottom.
			Button closeButton = new Button(view.getContext());
			closeButton.setText("Close");
			RelativeLayout.LayoutParams closeLayoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			closeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			closeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			closeButton.setLayoutParams(closeLayoutParams);

			closeButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View view) {
					Log.d(TAG, "Close Button onClick");
					dialog.dismiss();				
				}
			});
			mainLayout.addView(closeButton);
			closeButton.setVisibility(View.VISIBLE);

			dialog.setContentView(mainLayout);
			dialog.setOnDismissListener(new Dialog.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					mainLayout.removeAllViews();
				}
			});		

			dialog.show();
		}
	}

	private void setAppKey(String appKey) {
		try {
			pref.setString(
					"app_key",
					EncryptDecrypt.getEncryptedValue(appKey,
							EncryptDecrypt.getSecretKeySpec("app_key")));
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}

	private void setSecret(String secret) {
		try {
			pref.setString(
					"app_secret",
					EncryptDecrypt.getEncryptedValue(secret,
							EncryptDecrypt.getSecretKeySpec("app_secret")));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void clearCache(){
		try{
			pref.setString("access_token", "");
			pref.setString("refresh_token", "");
			pref.setString("expires_in", "-1");
		}catch(Exception e){
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}
	
	@Override
	// This will be invoked once Ad attached to window...
	protected void onAttachedToWindow() {		
		super.onAttachedToWindow();
		//startTimer(getContext());
		networkController.startNetworkListener();
	}

	/**
	 * Starts the timer to get the new Ad on certain interval time.
	 * 
	 * @param context
	 *            - The reference to the context of Activity.
	 */
	public void startTimer(Context context) {
		try {
			if (refreshTimer == null) {
				refreshTimer = new Timer();
			}

			if (refreshTask != null) {
				refreshTask.cancel();
				refreshTask = null;
			}

			if(isShown()) {
				RefreshTask newRefreshTask = new RefreshTask(context);
				
				long reloadPeriodInMS = Constants.AD_RELOAD_PERIOD;

				if(adReloadPeriod>0){
					//adReloadPeriod is in seconds and reloadPeriodInMS is in milliseconds 
					reloadPeriodInMS = adReloadPeriod*1000; 
				}
				refreshTimer.schedule(newRefreshTask, reloadPeriodInMS);
				refreshTask = newRefreshTask;
				adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_ERROR, "StartTimer",
						"timer started");
			}

		} catch (Exception e) {
			adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "StartTimer",
					e.getMessage());
		}

	}

	@Override
	protected void onDetachedFromWindow() {		
		super.onDetachedFromWindow();
		stopTimer(true);
		networkController.stopNetworkListener();
	}

	/**
	 * Stops the timer if the timer is in running and response in processing or
	 * network is not available.
	 * 
	 * @param remove
	 *            - tells to nullify the timer.
	 */
	// stopTimer will be invoked at network not available and app goes to next
	// page...
	public void stopTimer(boolean remove) {
		if (refreshTimer != null) {
			try {
				refreshTimer.cancel();
				if (remove) {
					refreshTimer = null;
				}
				adLog.log(AdLog.LOG_LEVEL_3, AdLog.LOG_TYPE_INFO, "stopTimer",
						"timer stopped");
			} catch (Exception e) {
				adLog.log(AdLog.LOG_LEVEL_1, AdLog.LOG_TYPE_ERROR, "stopTimer",
						e.getMessage());
			}
		}
	}

	/**
	 * Immediately initialize or refresh the process of ad view content.
	 */
	public void initOrRefresh() {
		isManualCall = true;
		StartLoadData(getContext());
	}
	
	/**
	 * Immediately stop the Ad timer to stops data loading.
	 */
	public void stopRefresh() {
		stopTimer(true);
		networkController.stopNetworkListener();		
	}
	
	/**
	 * Immediately start the Ad timer to starts data loading.
	 */
	public void startRefresh() {
		networkController.startNetworkListener();		
	}
	

	private class RefreshTask extends TimerTask {
		private Context context;

		public RefreshTask(Context context) {
			this.context = context;
		}

		@Override
		public void run() {
			StartLoadData(context);
		}
	}

	private void StartLoadData(Context context) {
		stopTimer(true);
		DataManager.getInstance(context).startLoadData(this,
				adSvcReq.createURL());
	}

	protected void setResult(String data, ATTAdViewError error) {
		
		if (null != error) {
			sendErrorEvent(error);			
			return;
		}
		
		processLoadData(data);
	}

	private void processLoadData(String data) {

		String type;
		String clickUrl;
		String imageUrl = null;
		String text = null;
		String content = null;
		String formattedJSONRes = null;
		
		try {
			JSONObject adsResponseObj = (JSONObject) new org.json.JSONTokener(
					data).nextValue();			
			JSONObject adResponseObj = adsResponseObj.getJSONObject(
					"AdsResponse").getJSONObject("Ads");

			formattedJSONRes = Utils.formattJSON(data);
			if (null == adResponseObj) {
				sendErrorEvent(
						new ATTAdViewError(
								ATTAdViewError.ERROR_ADSERVER_ERROR, 
								Constants.STR_EMPTY_SERVER_RESPONSE));
				return;
			}

			type = adResponseObj.getString("Type");
			clickUrl = adResponseObj.getString("ClickUrl");
			
			if(null == type){
				sendErrorEvent(
						new ATTAdViewError(
								ATTAdViewError.ERROR_ADSERVER_ERROR, 
								formattedJSONRes));
				return;
			}
			
			if (type.equals(Constants.ADS_THIRDPARTY_TYPE)) {
				content = adResponseObj.getString("Content");	
			} else if (type.equals(Constants.ADS_IMAGE_TYPE)) {
				imageUrl = adResponseObj.getJSONObject("ImageUrl").getString(
						"Image");
			} else if (type.equals(Constants.ADS_TEXT_TYPE)) {
				text = adResponseObj.getString("Text");
			} else {
				//Type undefined
				content = adResponseObj.getString("Content");
			}
		} catch (JSONException e) {
			Log.e(TAG, "Exception in JSON parsing:" + e.toString(), e);
			sendErrorEvent(
					new ATTAdViewError(
							ATTAdViewError.ERROR_ADSERVER_ERROR, 
							formattedJSONRes, e));
			return;
		} catch (Exception e) {
			Log.e(TAG, "ads Service Exception:" + e.toString(), e);
			sendErrorEvent(
					new ATTAdViewError(
							ATTAdViewError.ERROR_ADSERVER_ERROR, 
							formattedJSONRes, e));
			return;
		} 
		
		if (type.equals(Constants.ADS_THIRDPARTY_TYPE)) {
			if(null != content && content.length() != 0) {
				htmlAdSvcResponse = Utils.toHTML(content);
			}else {
				//error handling
				sendErrorEvent(
						new ATTAdViewError(
								ATTAdViewError.ERROR_ADSERVER_ERROR, 
								formattedJSONRes));
				return;
			}
		} else {
			htmlAdSvcResponse = Utils.toHTML(type, clickUrl, imageUrl, text);
		}

		handler.post(new Runnable() {
			@Override
			public void run() {
				loadDataWithBaseURL(null, htmlAdSvcResponse, "text/html",
						"UTF-8", null);
			}
		});
		sendSuccessEvent(formattedJSONRes);
	}

	public void sendErrorEvent(final ATTAdViewError error) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (null != adViewListener) {
					adViewListener.onError(error);
				}
			}
		});
	}

	public void sendSuccessEvent(final String adViewResponse) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (null != adViewListener) {
					adViewListener.onSuccess(adViewResponse);
				}
			}
		});
	}

	/**
	 * Optional. Set Keywords to search ad delimited by commas.
	 * 
	 * @param keywords the keywords associated with the ad viewer
	 */
	public void setKeywords(String keywords) {
		adSvcReq.setKeywords(keywords);
	}

	/**
	 * Optional. Set Gender to search ad.
	 * 
	 * @param gender gender of the ad viewer
	 */
	public void setGender(String gender) {

		adSvcReq.setGender(gender);

	}

	/**
	 * Optional. Set the age group of the demographic audience of the
	 * application.
	 * 
	 * @param ageGroup the age group of the ad viewer
	 */
	public void setAgeGroup(String ageGroup) {
		adSvcReq.setAgeGroup(ageGroup);
	}

	/**
	 * Optional. Set Country of visitor. See codes here
	 * (http://www.mojiva.com/docs/iso3166.csv). Will override country detected
	 * by IP.
	 * 
	 * @param country the country associated with the ad viewer
	 */
	public void setCountry(String country) {
		adSvcReq.setCountry(country);
	}

	/**
	 * Required. Set Category to search ad. Supported categories are sports,
	 * business......
	 * 
	 * @param category the product category to display to the ad viewer
	 */
	public void setCategory(String category) {
		adSvcReq.setCategory(category);
	}

	/**
	 * Optional. Set City of the device user (with state). For US only.
	 * 
	 * @param city the city associated with the ad viewer
	 */
	public void setCity(String city) {
		adSvcReq.setCity(city);
	}

	/**
	 * Optional. Set Area code of a user. For US only.
	 * 
	 * @param area the area code associated with the ad viewer
	 */
	public void setAreaCode(Integer area) {
		adSvcReq.setAreaCode(area);
	}

	/**
	 * Optional. Set Zip/Postal code of user. For US only.
	 * 
	 * @param zip the ZIP code associated with the ad viewer
	 */
	public void setZipCode(Integer zip) {
		adSvcReq.setZipCode(zip);

	}

	/**
	 * Optional. Set Latitude.
	 * 
	 * @param latitude the latitude location of the ad viewer
	 */
	public void setLatitude(Double latitude) {
		adSvcReq.setLatitude(latitude);
	}

	/**
	 * Optional. Set Longitude.
	 * 
	 * @param longitude the longitude location of the ad viewer
	 */
	public void setLongitude(Double longitude) {
		adSvcReq.setLongitude(longitude);
	}

	/**
	 * Optional. Set minimum width of advertising.
	 * 
	 * @param minSizeX the minimum requested ad width
	 */
	public void setMinWidth(Integer minSizeX) {
		adSvcReq.setMinWidth(minSizeX);

	}

	/**
	 * Optional. Set minimum height of advertising.
	 * 
	 * @param minSizeY the minimum requested ad height
	 */
	public void setMinHeight(Integer minSizeY) {
		adSvcReq.setMinHeight(minSizeY);
	}

	/**
	 * Optional. Set type of ads to be returned (1 - text, 2 - image). You can
	 * set different combinations with these values. For example, 3 = 1 + 2
	 * (text + image). Default is -1 means any.
	 * 
	 * @param type the type of ad being requested
	 */
	public void setType(Integer type) {
		adSvcReq.setType(type);
	}

	/**
	 * Optional. Set maximum width of advertising.
	 * 
	 * @param sizeX the maximum requested ad width
	 */
	public void setMaxWidth(Integer sizeX) {
		adSvcReq.setMaxWidth(sizeX);
	}

	/**
	 * Optional. Set maximum height of advertising.
	 * 
	 * @param sizeY the maximum requested ad height
	 */
	public void setMaxHeight(Integer sizeY) {
		adSvcReq.setMaxHeight(sizeY);
	}

	/**
	 * Get Keywords to search ad delimited by commas.
	 * 
	 * @return keywords
	 */
	public String getKeywords() {
		return adSvcReq.getKeywords();
	}

	/**
	 * Get Gender info.
	 * 
	 * @return Gender
	 */
	public String getGender() {

		return adSvcReq.getGender();

	}

	/**
	 * Get Age group ranges info
	 * 
	 * @return Age group range
	 */
	public String getAgeGroup() {

		return adSvcReq.getAgeGroup();
	}

	/**
	 * Get Country info.
	 * 
	 * @return Country
	 */
	public String getCountry() {
		return adSvcReq.getCountry();
	}

	/**
	 * Get Category type specified.
	 * 
	 * @return Category type
	 */
	public String getCategory() {
		return adSvcReq.getCategory();
	}

	/**
	 * Get City info.
	 * 
	 * @return City
	 */
	public String getCity() {
		return adSvcReq.getCity();
	}

	/**
	 * Get Area Code info.
	 * 
	 * @return Area Code
	 */
	public Integer getAreaCode() {
		return adSvcReq.getAreaCode();
	}

	/**
	 * Get Zip Code info.
	 * 
	 * @return Zip Code
	 */
	public Integer getZipCode() {
		return adSvcReq.getZipCode();
	}

	/**
	 * Get Latitude info.
	 * 
	 * @return Latitude
	 */
	public Double getLatitude() {

		return adSvcReq.getLatitude();
	}

	/**
	 * Get Longitude info.
	 * 
	 * @return Longitude
	 */
	public Double getLongitude() {
		return adSvcReq.getLongitude();
	}

	/**
	 * Get Minimum width info.
	 * 
	 * @return Minimum width
	 */
	public Integer getMinWidth() {
		return adSvcReq.getMinWidth();
	}

	/**
	 * Get Minimum height info.
	 * 
	 * @return Minimum height
	 */
	public Integer getMinHeight() {
		return adSvcReq.getMinHeight();
	}

	/**
	 * Get Type info.
	 * 
	 * @return Type
	 */
	public Integer getType() {
		return adSvcReq.getType();
	}

	/**
	 * Get Maximum width info.
	 * 
	 * @return Maximum width
	 */
	public Integer getMaxWidth() {
		return adSvcReq.getMaxWidth();
	}

	/**
	 * Get Maximum height info.
	 * 
	 * @return Maximum height
	 */
	public Integer getMaxHeight() {
		return adSvcReq.getMaxHeight();
	}

	/**
	 * Get Time out info.
	 * 
	 * @return Time out
	 */
	public Integer getTimeout() {
		return adSvcReq.getTimeout();
	}

	/**
	 * Optional. Set Time out to SDK, listens the response from Ad server till
	 * this time.
	 * 
	 * @param timeout
	 *            in milli seconds.
	 */
	public void setTimeout(Integer timeout) {
		adSvcReq.setTimeout(timeout);
	}

	/**
	 * Get is it production environment info.
	 * 
	 * @return true if we're running in a production environment
	 */
	public boolean isProd() {
		return isProd;
	}

	/**
	 * Optional. Set is Production environment.
	 * 
	 * @param isProd indicates whether we are running in a production environment
	 */
	public void setProd(boolean isProd) {
		this.isProd = isProd;
	}

	/**
	 * Get AdViewListener registered.
	 * 
	 * @return adViewListener
	 */
	public ATTAdViewListener getAdViewListener() {
		return adViewListener;
	}

	/**
	 * Optional. Register AdViewListener so that Application developer can track the
	 * status.
	 * 
	 * @param adViewListener object used to listen for events from the ad view
	 */
	public void setAdViewListener(ATTAdViewListener adViewListener) {
		this.adViewListener = adViewListener;
	}

	/**
	 * Get the udid provided. 
	 * @return the udid
	 */
	public String getUdid() {
		return udid;
	}

	/**
	 * Set the udid to track the application and application developer.
	 * @param udid the udid
	 */
	public void setUdid(String udid) {
		this.udid = udid;
	}

	/**
	 * Get the developer provided reload time in seconds.
	 * @return reload time
	 */
	public int getAdReloadPeriod() {
		return adReloadPeriod;
	}

	/**
	 * Optional. Set reload time in seconds. Default is 120 seconds.
	 * @param adReloadPeriod
	 * 				in seconds.
	 */
	public void setAdReloadPeriod(int adReloadPeriod) {
		this.adReloadPeriod = adReloadPeriod;
	}
	
	/**
	 * Get the userAgent provided. 
	 * @return the user-agent header to send with the ad request
	 */
	public String getUserAgent() {
		return userAgent;
	}
	
	/**
	 * Set the userAgent 
	 * @param userAgent the user-agent header to send with the ad request
	 */	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	/*
	 * Optional. Display the Ad in Full Screen after clicking the Ad. Default is false.
	 * @param showFullScreenAd
	 * 				true/false.
	 */
	public void showInterstitialViewOnBannerAdClick(boolean showFullScreenAd) {
		this.showFullScreenAd = showFullScreenAd;
	}
}
