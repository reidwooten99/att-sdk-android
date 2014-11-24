ADS Sample App can be downloaded from the following GitHub link:
https://github.com/attdevsupport/ATT_APIPlatform_SampleApps/tree/master/Android/ADS

Steps to add SDK to the application:
====================================
1.	Open the Android project for your application in Package Explorer.
2.	Select Project, Properties.
3.	Select the Java Build Path entry in the navigation bar.
4.	Click the Libraries tab.
5.	Click the Add External JARs button on the right side of the properties window.
6.	Browse to the folder where you extracted the Advertising files.
Select the library adsapi.jar.
7.	Click OK. The library should appear in Package Explorer.
8.  Update the AndroidManifest.xml
	Double-click AndroidManifest.xml and add the following permissions. 
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission. ACCESS_NETWORK_STATE " />
9.	Open the activity and layout that contain the UI elements of your application.
10.	Follow the instructions in the Using the ATTAdView Class section. 
11.	Build your application that includes the AT&T Ad SDK.
12.	Sample App - Update the following with respect to your application from the developer portal.
•	appKey
•	category
•	secret
•	udid

Revision:
=========
Version 2.0 - May 29, 2013
--------------------------
Ads size issue fixed in SDK and updated the jar file.

Version 2.0.1 - Aug 27, 2013
----------------------------
1. Ads spike error and oAuth 3.0 fixes updated.
2. JSON Exception error: Auth API spike violation issue is now handled properly.
3. Key Decryption Issue Fix: the library can now run on Android 4.2 and above.
4. Ads setReloadPeriod now defaults to a minimum of 30 seconds. If the application tries to set the reload period to less than 30 seconds, the SDK will override the setting, reverting to 30 seconds.
5. Ads cropped issue: when setting SetlayoutParams(), use pixels instead of dp value, i.e., px = dp*(dpi/160), or specify a height of "webview" in dp+(dp/2).

Version 2.0.2 - Nov 20, 2013
----------------------------
1. Interstial ADS launch on click of banner ad is made configurable, now onclick of the banner ads the user will be navigated to clickurl.
2. To enable the interstial ads onclick of banner ads is configurable by showInterstialViewOnBannerAdClick(true);
3. Invalid token scenario handled.

Version 2.0.3 - Dec 27, 2013
----------------------------
Fix for the issue - SF7411 - Thread warning output when displaying ads

Version 2.0.4 - Mar 10, 2014
----------------------------
Fix for the issue - QC7241 - Additional error message info returned during error callback.

Version 2.0.5 - April 2, 2014
-----------------------------
Fix for oAuth Content-Type enforcement from API, which was returning 415 response.

Version 2.0.6 - May 28, 2014
-----------------------------
1. Removed the following interfaces - isSizeRequired, Premium and over18 that is not supported by the API
2. Internal changes - Updated the clientSDK name parameter for Analytics.
3. Fix for Auth token request on application launches.

Procedure to update the library
===============================
1.Open the Android project for your app in Package Explorer.
2.Select Project > Properties.
3.Select Java Build Path in the navigation bar.
4.Click the Libraries tab.
5.Click the Add External JARs button on the right side of the properties window.
6.Browse to the folder where you extracted the Advertising SDK files, and select the library adsapi.jar.
7.Click OK. The library should appear in Package Explorer