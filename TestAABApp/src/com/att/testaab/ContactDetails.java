package com.att.testaab;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import com.att.api.aab.service.Contact;


public class ContactDetails extends Activity {
	
	private String contactId;
	private String firstName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_details);
		
		 Bundle bundleObject = getIntent().getExtras();
		 ArrayList<Contact> classObject = (ArrayList<Contact>) bundleObject.getSerializable("key");
		 
		 Intent intent = getIntent();
		 contactId = intent.getStringExtra("contactId");
		 
		 for(int i = 0 ; i < classObject.size(); i++) {
			 firstName =  classObject.get(i).getFirstName();
		 }

			Log.i("ContactDetails","OnSuccess : firstName :  " + firstName);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contact_details, menu);
		return true;
	}

}
