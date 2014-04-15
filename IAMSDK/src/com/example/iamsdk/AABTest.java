package com.example.iamsdk;

import org.apache.commons.lang3.builder.Builder;

import com.att.api.immn.service.*;



import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class AABTest extends Activity {
	
	IAMManager iamManager;
	PageParams pageParams;
	SearchParams searchParams;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aabtest);
		pageParams = new PageParams("ASC", "firstName", "2", "0");
		SearchParams.Builder builder = new SearchParams.Builder();
		searchParams = new SearchParams(builder);
		iamManager.GetContacts("", pageParams, searchParams );

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.aabtest, menu);
		return true;
	}

}
