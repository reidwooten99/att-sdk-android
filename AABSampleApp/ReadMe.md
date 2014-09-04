# AT&T Address Book SDK for Android

The Address Book API manages a customer’s AT&T Wireless contacts. The Address Book API provides features to create, modify, retrieve, organize, and search a customer’s contacts using an easy-to-use Web service interface.

The Address Book SDK is an Android library for natively consuming the RESTful resources of the Address Book API. The SDK provides the wrapper file AabManager.java, which uses the Android public abstract class AsyncTask to handle network tasks (like HTTP requests) in the background, while providing an abstract layer with a common interface for accessing the RESTful resources in the Address Book API.
  
#Quick Start

#### Requirements:
 
* Git
	
* AndroidSDK
 
# Overview 

There are two main components to the library 

1. A manager (HTTP client) to handle all of the Address Book requests.

2. A set of request wrappers for the HTTP requests to access the RESTful resources of the Address Book API.

## Request wrappers
 
The SDK provides wrappers which expose the main request parameters for the following RESTful resources that can be accessed by an application.

	
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
 
The networking layer is abstracted away by providing the AabManager to handle the requests made by the application. AabManager calls the service, which creates the actual HTTP requests in the background tasks by using the Android public abstract class AsyncTask. Network calls are not allowed in the main UI thread from Android API level 11 onwards, hence the calls are handled in the AsyncTasks. The developer may define success and error callbacks using listeners
	
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
 
Run the AabSampleApp project, which demonstrates the basic functionalities of the Address Book API.

##### TestAAB project

Enter the OAuth credentials -
 
 * APP_KEY 
 * APP_SECRET 
 * APP_SCOPE
 * REDIRECT_URI

 
To test the functionality of the Address Book API methods, run TestAab project. Enter the test case numbers, 1:GetContacts; 2:GetContact etc.

Note: You can also add your own test cases


## Using the Address Book SDK in your App

##### Using the Binaries 

Add the jars[ aabsdk.jar, codekit.jar ], downloaded from the release folder to the build path of your projectDownload the jar files (aabsdk.jar, codekit.jar) from the release folder and add them to the build path of your project.

#####Using the AabSDK Source

* Download the AabSDK project. Import the project into eclipsethe Eclipse integrated development environment (IDE) .
* Build the project.
* Link the AabSDK project to your App.


#####Using the CodeKit Source

* Download the AT&T Java Codekit, which uses Maven, ATT Maven CodeKit Project from the following location:  [ATT CodeKit](https://github.com/attdevsupport/codekit-java)
* Download and Importinstall the m2eclipse plugin from the following location:[m2eclipse plugin](http://theopentutorials.com/tutorials/eclipse/installing-m2eclipse-maven-plugin-for-eclipse/)
http://eclipse.org/m2e/download/
* Import the Maven AT&T Java Codekit CodeKit project into your Eclipse IDE/ADT or Eclipse IDE with the Android Development Tools (ADT) plugin. 
* Add the CodeKit to the ProjectReferences in the Build Path of the AabSDK project.
* Add the AabSDK project as a reference in the build path of your project.


##Usage
 
Initialize the app client
 
 	final String domainName = "https://api.att.com";
 
	// Enter the value from 'App Key' field
	final String clientId = " appId";
 
	// Enter the value from 'Secret' field
	final String clientSecret = "appSecret"
	
Add the UserConsentActivity class to the ManifestFile

	<activity>	
	
	android:name=
	"com.att.api.consentactivity.UserConsentActivity"
	
 	android:label="@string/title_activity_user_consent” >
    </activity>
    
 Pass the domainName, appId and appSecret as extras in the UserConsentActivity class to obtain the OAuth code from the onActivityResult callback.
 
 	Intent i = new Intent
		(this,com.att.api.consentactivity.UserConsentActivity.class);
	i.putExtra("fqdn", Config.fqdn);
	i.putExtra("clientId", Config.clientID);
	i.putExtra("clientSecret", Config.secretKey);
	i.putExtra("redirectUri", Config.redirectUri);
	i.putExtra("appScope", Config.appScope);
           
	startActivityForResult(i, OAUTH_CODE);
	

Obtain the token by passing the oAuth code in onActivityResult

	aabManager = new AabBManager(Config.fqdn, Config.clientID,Config.secretKey,new getTokenListener());
	
	aabManager.getOAuthToken(oAuthCode);
	
You can call other APIs upon successful call back of the token listener, for example:

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
	
Note:  You may cache the AccessToken so that you do not have to receive authorization  every time the app is launched.      
 	

Legal Disclaimer
This document and the information contained herein (collectively, the "Information") is provided to you (both the individual receiving this document and any legal entity on behalf of which such individual is acting) ("You" and "Your") by AT&T, on behalf of itself and its affiliates ("AT&T") for informational purposes only. AT&T is providing the Information to You because AT&T believes the Information may be useful to You. The Information is provided to You solely on the basis that You will be responsible for making Your own assessments of the Information and are advised to verify all representations, statements and information before using or relying upon any of the Information. Although AT&T has exercised reasonable care in providing the Information to You, AT&T does not warrant the accuracy of the Information and is not responsible for any damages arising from Your use of or reliance upon the Information. You further understand and agree that AT&T in no way represents, and You in no way rely on a belief, that AT&T is providing the Information in accordance with any standard or service (routine, customary or otherwise) related to the consulting, services, hardware or software industries. 
AT&T DOES NOT WARRANT THAT THE INFORMATION IS ERROR-FREE.  AT&T IS PROVIDING THE INFORMATION TO YOU "AS IS" AND "WITH ALL FAULTS."  AT&T DOES NOT WARRANT, BY VIRTUE OF THIS DOCUMENT, OR BY ANY COURSE OF PERFORMANCE, COURSE OF DEALING, USAGE OF TRADE OR ANY COLLATERAL DOCUMENT HEREUNDER OR OTHERWISE, AND HEREBY EXPRESSLY DISCLAIMS, ANY REPRESENTATION OR WARRANTY OF ANY KIND WITH RESPECT TO THE INFORMATION, INCLUDING, WITHOUT LIMITATION, ANY REPRESENTATION OR WARRANTY OF DESIGN, PERFORMANCE, MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, OR ANY REPRESENTATION OR WARRANTY THAT THE INFORMATION IS APPLICABLE TO OR INTEROPERABLE WITH ANY SYSTEM, DATA, HARDWARE OR SOFTWARE OF ANY KIND. AT&T DISCLAIMS AND IN NO EVENT SHALL BE LIABLE FOR ANY LOSSES OR DAMAGES OF ANY KIND, WHETHER DIRECT, INDIRECT, INCIDENTAL, CONSEQUENTIAL, PUNITIVE, SPECIAL OR EXEMPLARY, INCLUDING, WITHOUT LIMITATION, DAMAGES FOR LOSS OF BUSINESS PROFITS, BUSINESS INTERRUPTION, LOSS OF BUSINESS INFORMATION, LOSS OF GOODWILL, COVER, TORTIOUS CONDUCT OR OTHER PECUNIARY LOSS, ARISING OUT OF OR IN ANY WAY RELATED TO THE PROVISION, NON-PROVISION, USE OR NON-USE OF THE INFORMATION, EVEN IF AT&T HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH LOSSES OR DAMAGES.
 	