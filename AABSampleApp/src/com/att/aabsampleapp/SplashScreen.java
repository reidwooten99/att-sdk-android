package com.att.aabsampleapp;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;

public class SplashScreen extends Activity {

	private final int SPLASH_DISPLAY_LENGTH = 2000;
	protected Handler handler = new Handler();
	Runnable myRunnable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		myRunnable = new Runnable() {
			@Override
			public void run() {
				/* Create an Intent that will start the Menu-Activity. */
				Intent mainIntent = new Intent(getApplicationContext(),
						AddressBookLaunch.class);
				startActivity(mainIntent);
				finish();
			}
		};
		handler.postDelayed(myRunnable, SPLASH_DISPLAY_LENGTH);
	}
	
	@Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	 if (null != myRunnable) {
	    		 handler.removeCallbacks(myRunnable);
	    		 finish();
	    	 }
	 		return true;
	     }
	     return super.onKeyDown(keyCode, event);
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

}
