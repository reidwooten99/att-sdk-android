AT&T AddressBook (AAB) SDK for Android

Install Guide

1.	Open the Android project for your application in Package Explorer.
2.	Select Project, Properties.
3.	Select the Java Build Path entry in the navigation bar.
4.	Click the Libraries tab.
5.	Click the Add External JARs button on the right side of the properties window.
6.	Browse to the folder where you extracted the AAB SDK files.
	Select the library aabsdk.jar & codekit-1.0.jar.
7.	Click OK. The library should appear in Package Explorer.
8.	Sample App - Update the following with respect to your application from the developer portal.
•	appKey
•	secret
•	redirect url as registered in AT&T developer portal app.

AT&T Address Book API enables the client applications and other service consumers with a programmable interface to manage subscriber’s contacts in AT&T Address Book (AAB). AT&T Address Book provides features to create, modify, retrieve, organize and search subscriber’s contacts using the easy to use web services based interfaces.

The Address Book  SDK is an android library for natively consuming the RESTful resources for [AT&T’s Address Book API]. 

The SDK abstracts away all the networking tasks using the AsyncTasks in a wrapper file - AabManager.java  for each of the APIs
 
Quick Start
-----------
Requirements:
 
* Git
	
* AndroidSDK
 
Overview:

There are two main components to the library 

1.A manager --HTTP client-- to handle all the Addres Book requests

2.A set of request wrappers for the HTTP requests to access the API's resources.

Request wrappers:
 
The SDK provides the wrappers for the actual REST resources that are accessed by the application.The main request parameters are exposed.

	CreateContact
	GetContacts
	GetContact
	GetContactGroups
	UpdateContact
	DeleteContact
	CreateGroup
	GetGroups
	DeleteGroup
	UpdateGroup
	AddContactsToGroup
	RemoveContactsFromGroup
	GetGroupContacts
	GetMyInfo
	UpdateMyInfo
	
Request Management
 
The networking layer is abstracted away by providing the manager—AabManager-- to handle the request made by the application. The AabManager, calls the Service which creates the actual HTTP requests in the background tasks using Android’s AsyncTask. The network calls are not allowed in the main UI thread from the Android API level - 11 onwards , hence the calls are handled in the AsyncTasks.  The developer is allowed  to define success and error callbacks using listeners)
	
	aabManager = 
	new AabManager(... new getContactsListener());
	
	aabManager.GetContacts(…,…,…);
	
	private class getContactsListener implements AttSdkListener {
 
	@Override
	public void onSuccess(Object response) {
 
	// Your code for success
	 
	}
 
	@Override
	public void onError(AttSdkError error) {
 
	// Your code for error
 
	}
	
Using the SampleApp
-------------------
 
AABSampleApp project 
 
Enter the OAuth credentials -
 
 * APP_KEY 
 * APP_SECRET 
 * APP_SCOPE
 * REDIRECT_URI
 
Run the AabSampleApp project which demonstrates the basic functionalities of all the Address Book APIs

ChangeLog
---------

Release 1.0 - July 3rd 2014

Initial release of AAB SDK.