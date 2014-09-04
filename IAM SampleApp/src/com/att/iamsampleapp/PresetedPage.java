package com.att.iamsampleapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class PresetedPage extends Activity {
	
	private Button m_forceOffNetButton = null;
	private Button m_suppressButton = null;
	private Button m_startButton = null;
	public static boolean OFF_NET = false;
	public static boolean SUPPRESS = false;
	public static boolean OFF_NET_CHECKBOX = false;
	public static boolean FIRST_START = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preseted_page);
		
		boolean suppressed = false;
		boolean bypass = false;
		String byPaddOnNet = "";
		
		File file = getFileStreamPath(Config.backUpPreseted);
		
		if (file.exists()){
			 FileInputStream fis = null;
			try {
				fis = openFileInput(Config.backUpPreseted);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			byte[] presetedData;
			try {
				presetedData = new byte[fis.available()];
				fis.read(presetedData);
				String ret_str = new String(presetedData);
				suppressed = ret_str.contains(Config.suppressLndgPageStr);
				bypass  = ret_str.contains(Config.byPassOnNetStr);
				Log.d("OUTPUT_  PRESETED DATA:   ", ret_str);
				fis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		else{
			 FIRST_START = true;
		}
		
		m_forceOffNetButton = (Button) findViewById(R.id.forceOffNetCheckBox);
		if (bypass){
			m_forceOffNetButton.setBackgroundResource(R.drawable.check_mark);
			OFF_NET_CHECKBOX = true;
		}
		
		m_suppressButton = (Button) findViewById(R.id.forceSuppressCheckBox);
		if (suppressed){
			m_suppressButton.setBackgroundResource(R.drawable.check_mark);
			SUPPRESS = true;
		}
		
		m_forceOffNetButton.setOnClickListener(new Button.OnClickListener(){
     	 	 public void onClick(View v) {
     	 		 
     	 		if (FIRST_START){
	     	 		if (!OFF_NET){
		     	 		OFF_NET = true;
		     	 		OFF_NET_CHECKBOX = true;
		     	 		m_forceOffNetButton.setBackgroundResource(R.drawable.check_mark);
	     	 		}
	     	 		else{
	     	 			OFF_NET = false;
		     	 		OFF_NET_CHECKBOX = false;
		     	 		m_forceOffNetButton.setBackgroundColor(Color.LTGRAY);
	     	 		}
	     	 	}
     	 		else {
     	 			   if (OFF_NET_CHECKBOX){
     	 				 Utils.toastHere(getApplicationContext(), "", "Sorry. Can not change to on-network");
     	 			   }
     	 			   else {
     	 			         Utils.toastHere(getApplicationContext(), "", "Off-network can be set at the first start only");
     	 			   }
     	 		}
     	 		
     	  	 }
        });
		
		m_suppressButton.setOnClickListener(new Button.OnClickListener(){
    	 	 public void onClick(View v) {
    	 
    	 		   if (!SUPPRESS){
    	 			  m_suppressButton.setBackgroundResource(R.drawable.check_mark);
	    	 		  SUPPRESS = true;
    	 		   }
    	 		   else {
    	 			   m_suppressButton.setBackgroundColor(Color.LTGRAY);
	    	 		   SUPPRESS = false;
    	 		   }
    	 		}
       });
		
	   m_startButton = (Button) findViewById(R.id.startButton);
	   m_startButton.setOnClickListener(new Button.OnClickListener(){
	   	 	 public void onClick(View v) {
	   	 		 
	   	 	 Intent splashScreenIntent = new Intent(getApplicationContext(), SplashScreen.class);
	   	 	 startActivity(splashScreenIntent);
   	 	}	 
      });
				
	}		
}
