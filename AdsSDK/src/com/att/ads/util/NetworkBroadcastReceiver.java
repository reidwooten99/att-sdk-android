package com.att.ads.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.att.ads.controllers.NetworkController;


/**
 * Network Broadcast Receiver object is used to listen the network connectivity changes. 
 * Basically it intimates the Network Controller object to control the data loading process.  
 * Network connectivity changes can be due one of the following events:
 * 	1.Current network interface being disconnected
 *  2.A new network interface being connected
 *  3.Handover (also known as “fallover”) between two network interfaces (usually as a 
 *    result of one of the two events above), such as WiFi being activated as a result of 
 *    user coming home and all network traffic will be routed through WiFi as opposed to 3G.
 *    
 * @author ATT
 *
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "NetworkBroadcastReceiver";
	private NetworkController networkController;

	/**
	 * Creation of network broadcast receiver object. 
	 * @param networkController
	 */
	public NetworkBroadcastReceiver(NetworkController networkController) {
		this.networkController = networkController;
	}

	/** 
	 * Overriding the android.content.BroadcastReceiver's onReceive method to listen 
	 * only the network connectivity changes. 
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Network configuration changed.");
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			networkController.onConnectionChanged();
		}
	}
	
}
