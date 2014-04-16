package com.att.ads;

import java.util.HashMap;

import com.att.ads.controllers.NetworkController;

import android.content.Context;
import android.util.Log;

/**
 * DataManager is a Singleton class and manages the data loading requests.
 * Handles all the third party requests asynchronously. Controlling the 
 * start and stop loading processes by various factors to improve the battery 
 * utilization.
 * 
 * @author ATT
 *
 */
public class DataManager {
	public static final String TAG = "DataManager";
	private static DataManager instance;
	private HashMap<ATTAdView, DataParameters> senderParameters = new HashMap<ATTAdView, DataParameters>();
	private Context context;

	/**
	 * Returns the Data Manager object.
	 * @param context
	 * @return DataManager
	 */
	static public DataManager getInstance(Context context) {
		if (instance == null)
			instance = new DataManager(context);

		return instance;
	}

	/**
	 * Private Constructor make sure that only one object exists in the Runtime. 
	 * @param context
	 */
	private DataManager(Context context) {
		this.context = context;
	}

	/**
	 * Core method to initiate the data loading process asynchronously. 
	 * It expects the ATTAdView object and Ad service request URL.
	 * 
	 * @param attAdView
	 * @param url
	 */
	public void startLoadData(ATTAdView attAdView, String url) {
		if (senderParameters.containsKey(attAdView))
			stopLoadData(attAdView);

		DataParameters parameters = new DataParameters();
		parameters.sender = attAdView;
		parameters.url = url;

		senderParameters.put(attAdView, parameters);

		DataThread dTh = new DataThread(parameters);
		parameters.dTh = dTh;
		dTh.setName("[DataManager] LoadData");
		Log.i(TAG, "Data Thread starting: " + attAdView);
		dTh.start();
	}

	/**
	 * Core method to stop the data loading process. 
	 * It expects the ATTAdView object.
	 * 
	 * @param attAdView
	 * @param url
	 */
	public void stopLoadData(ATTAdView sender) {
		if(sender.isManualCall)
			sender.isManualCall = false;
		if (null != sender) {
			Log.i(TAG, "Data Thread stopped: " + sender);
			if (sender.isShown()) {
				sender.startTimer(context);
			}
			if (senderParameters.containsKey(sender)) {
				senderParameters.get(sender).sender = null;
				senderParameters.get(sender).dTh.cancel();
				senderParameters.remove(sender);
			}
		}
	}

	/**
	 * Inner class to encapsulates the core required data for processing. 
	 * Such as Ad service request URL, ATTAdView and Data Thread.  
	 * @author ATT
	 *
	 */
	private class DataParameters {
		public String url;
		public ATTAdView sender;
		DataThread dTh;
	};

	/**
	 * Runnable class to initiate the data loading process asynchronously.
	 * 
	 * @author ATT
	 *
	 */
	private class DataThread extends Thread {
		DataParameters parameters;
		boolean isCanceled = false;

		public DataThread(DataParameters parameters) {
			this.parameters = parameters;
		}

		@Override
		public void run() {
			ATTAdView adView = parameters.sender;
			//Network Availability check
			NetworkController nController = new NetworkController(adView, context);
			if(nController.isOnline()){
				AuthService authSvc = new AuthService(context);
				String accessToken = authSvc.getAccessToken(adView);
				if (null != accessToken) {
					AdService adSvc = new AdService(accessToken);
					adSvc.getAd(parameters.url, parameters.sender, isCanceled);
				}
			} else {
				if (adView != null)
					adView.setResult(null, new ATTAdViewError(
							ATTAdViewError.ERROR_NETWORK_ERROR,
							Constants.STR_NETWORK_PROBLEM));				
			}
			stopLoadData(adView);
		}

		/**
		 * Used to avoid multiple parallel requests processing for the same data.
		 */
		public void cancel() {
			isCanceled = true;
		}
	}

}
