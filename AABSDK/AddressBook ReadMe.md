# AT&T AddressBook (AAB) SDK for Android

AT&T Address Book API enables the client applications and other service consumers with a programmable interface to manage subscriber’s contacts in AT&T Address Book (AAB). AT&T Address Book provides features to create, modify, retrieve, organize and search subscriber’s contacts using the easy to use web services based interfaces.

The Address Book  SDK is an android library for natively consuming the RESTful resources for [AT&T’s Address Book API]. 

The SDK abstracts away all the networking tasks using the AsyncTasks in a wrapper file - AabManager.java  for each of the APIs 
 
#Quick Start

#### Requirements:
 
* Git
	
* AndroidSDK
 
# Overview 

There are two main components to the library 

1.A manager --HTTP client-- to handle all the Addres Book requests

2.A set of request wrappers for the HTTP requests to access the API's resources.

## Request wrappers
 
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
	
##Request Management
 
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
	

##Using the SampleApp
 
##### AABSampleApp project 
 
Enter the OAuth credentials -
 
 * APP_KEY 
 * APP_SECRET 
 * APP_SCOPE
 * REDIRECT_URI
 
Run the AabSampleApp project which demonstrates the basic functionalities of all the Address Book APIs

##### TestAAB project

Enter the OAuth credentials -
 
 * APP_KEY 
 * APP_SECRET 
 * APP_SCOPE
 * REDIRECT_URI

 
Run the TestAab project which can be used to test the functionality of the individual Address Book APIs by entering the test case numbers.[1:GetContacts ; 2:GetContact etc]

Note : User can also add their own test cases 
 
 
## Using the Address Book SDK in your App

##### Using the Binaries 

Add the jars[ aabsdk.jar, codekit.jar ], downloaded from the release folder to the build path of your project

#####Using the AabSDK Source

* Download the AabSDK project. Import the project into eclipse .
* Build the project
* Link the AabSDK project to your App


#####Using the CodeKit Source

* Download the ATT Maven CodeKit Project from [ATT CodeKit](https://github.com/attdevsupport/codekit-java)
* Import m2eclipse plugin from [m2eclipse plugin](http://theopentutorials.com/tutorials/eclipse/installing-m2eclipse-maven-plugin-for-eclipse/)
* Import the Maven CodeKit project into Eclipse/ADT 
* Add CodeKit to the ProjectReferences in the Build Path of AabSDK project.
* Add the AabSDK project as a reference in the build path of your project

Note : User can create CodeKit jar by following the steps given in [Maven Jar in Eclipse](http://www.beingjavaguys.com/2013/08/create-java-project-with-maven-eclipse.html)

##Usage
 
Initialize the app client
 
 	final String domainName = "https://api.att.com";
 
	// Enter the value from 'App Key' field
	final String clientId = " appId";
 
	// Enter the value from 'Secret' field
	final String clientSecret = "appSecret"
	
Add the UserConsent Activity in the ManifestFile

	<activity>	
	
	android:name=
	"com.att.api.consentactivity.UserConsentActivity"
	
 	android:label="@string/title_activity_user_consent” >
    </activity>
    
 Start the UserconsentActivity for result by passing the domainName, appId and appSecret as extras to get the oAuthCode on the onActivityResult callback
 
 	Intent i = new Intent
		(this,com.att.api.consentactivity.UserConsentActivity.class);
	i.putExtra("fqdn", Config.fqdn);
	i.putExtra("clientId", Config.clientID);
	i.putExtra("clientSecret", Config.secretKey);
	i.putExtra("redirectUri", Config.redirectUri);
	i.putExtra("appScope", Config.appScope);
           
	startActivityForResult(i, OAUTH_CODE);
	

Obtain the token by passing the oAuthCode in onActivityResult

	aabManager = new AabBManager(Config.fqdn, Config.clientID,Config.secretKey,new getTokenListener());
	
	aabManager.getOAuthToken(oAuthCode);
	
OnSuccess call back of the token listener, you can call other APIs
Example :

	public class getTokenListener implements AttSdkListener {
	@Override
	public void onSuccess(Object response) {
	OAuthToken authToken = (OAuthToken) response;
	if (null != authToken) {
 
	getAddressBookContacts();
      }
	}
 
	@Override
	public void onError(AttSdkError error) {
      //Your code for error        
      }
	}    
	
Note : AccessToken can be cached so that the developer need not have to authorize everytime the app is launched.      
 	