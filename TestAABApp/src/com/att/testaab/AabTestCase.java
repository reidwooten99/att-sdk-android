package com.att.testaab;

import android.test.AndroidTestCase;
import android.widget.TextView;
import com.att.api.aab.manager.AabManager;
import com.att.api.oauth.OAuthToken;

public class AabTestCase extends AndroidTestCase {

	protected OAuthToken authToken = new OAuthToken(Config.token, Config.accessTokenExpiry, Config.refreshToken);
	protected TextView display = null;
	protected String strLogFilePath = null;
	protected AabManager aabManager = null;
	public String strText;
	
	public AabTestCase(TextView textView, String strLogFilePath) {
		display = textView;
		this.strLogFilePath = strLogFilePath;
	}
	
    protected void setUp() throws Exception {
        super.setUp();
	}
	
	protected void tearDown() throws Exception {
	        super.tearDown();
	}
}
