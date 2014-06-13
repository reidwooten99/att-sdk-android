package com.att.ads.controllers;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.att.ads.ATTAdView;
import com.att.ads.util.NetworkBroadcastReceiver;

/**
 * Class to control the timers respective to the network availability. 
 * Listens the network changes by registering the {@link NetworkBroadcastReceiver}. 
 * 
 * @author ATT 
 */
public class NetworkController {
	private static final String TAG = "NetworkController";
	private ConnectivityManager connectivityManager;
	private NetworkBroadcastReceiver broadCastReceiver;
	private IntentFilter filter;
	private Context context;
	private ATTAdView adView;

	/**
	 * Constructor set the {@link ATTAdView} and {@link Context}.
	 * Also construct a reference for {@link ConnectivityManager}. 
	 * @param adView
	 * @param context
	 */
	public NetworkController(ATTAdView adView, Context context) {
		this.adView = adView;
		this.context = context;
		connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * Checks the availability of Network.
	 * @return true - if network is available
	 * false - if network is not available
	 */
	public boolean isOnline() {
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

		if (ni == null) {
			return false;
		} else {
			return ni.isConnected();
		}
	}

	/**
	 * Registers the Network Listener {@link NetworkBroadcastReceiver} to SYSTEM services.
	 */
	public void startNetworkListener() {		
		if(null != broadCastReceiver)
			return;
		Log.d(TAG, "startNetworkListener");
		broadCastReceiver = new NetworkBroadcastReceiver(this);
		filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		try {
			context.registerReceiver(broadCastReceiver, filter);
		} catch (Exception e) {
		}
	}

	/**
	 * Unregisters the Network Listener {@link NetworkBroadcastReceiver} to SYSTEM services.
	 */
	public void stopNetworkListener() {
		if(null == broadCastReceiver)
			return;	
		
		Log.d(TAG, "stopNetworkListener");
		try {
			context.unregisterReceiver(broadCastReceiver);
		} catch (Exception e) {
		}
		broadCastReceiver = null;
		filter = null;
	}

	/**
	 * It performs when triggers a change in the network.
	 * Simply it starts the Timer when network state changes to available. 
	 */
	public void onConnectionChanged() {

		if(adView.isShown() && isOnline()) {
			if(!adView.isManualCall)
				adView.startTimer(context);
		}else {
			adView.stopTimer(true);			
		}
	}

}
