
/* vim: set expandtab tabstop=4 shiftwidth=4 softtabstop=4 foldmethod=marker */

/*
 * ====================================================================
 * LICENSE: Licensed by AT&T under the 'Software Development Kit Tools
 * Agreement.' 2014.
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTIONS:
 * http://developer.att.com/sdk_agreement/
 *
 * Copyright 2014 AT&T Intellectual Property. All rights reserved.
 * For more information contact developer.support@att.com
 * ====================================================================
 */

/**
 * 
 * The Address Book  SDK  is an android library for natively consuming the RESTful resources for [AT&amp;T&#8217;s Address Book API]. 
 * The SDK abstracts away all the networking tasks using the AsyncTasks in a wrapper file - AabManager.java  for each of the APIs.
 * <p>
 * There are two main components to the library:
 * <ul>
 * 
 * <li>A manager --HTTP client-- to handle all the Address Book requests
 * <li>A set of request wrappers for the HTTP requests to access the API&#8217;s resources.
 * 
 *</ul>
 *</p>
 *Request wrappers:
 *<p>
 *The SDK provides the wrappers for the actual REST resources that are accessed by the application.The main request parameters are exposed.
 <ul>

	<li>CreateContact
	<li>GetContacts
	<li>GetContact
	<li>GetContactGroups
	<li>UpdateContact
	<li>DeleteContact
	<li>CreateGroup
	<li>GetGroups
	<li>DeleteGroup
	<li>UpdateGroup
	<li>AddContactsToGroup
	<li>RemoveContactsFromGroup
	<li>GetGroupContacts
	<li>GetMyInfo
	<li>UpdateMyInfo
 * </ul>
 * </p>
 * <p>
 * The networking layer is abstracted away by providing the manager --AabManager-- to handle the request made by the application. 
 * The AabManager, calls the Service which creates the actual HTTP requests in the background tasks using Android&#8217;s AsyncTask. 
 * The network calls are not allowed in the main UI thread from the Android API level - 11 onwards,hence the calls are handled in the AsyncTask.  
 * The developer is allowed  to define success and error callbacks using listeners
 * </p>
 * <p> 
 * Using the Address Book SDK in your Android App: 
 *<ul>
 *<li>Add the jars[ aabsdk.jar, codekit.jar ], downloaded from the release folder to the build path of your project
 *</ul>
 *</p>
 *
 *<p><b>Usage</b>
 *<ul>
 *<li>Initialize the app client:
 *<pre>
 *<code>
 *final String domainName = "https://api.att.com";</i>
 *final String clientId = " appId"; // Enter the value from 'App Key' field </i>
 *final String clientSecret = "appSecret"; // Enter the value from 'Secret' field </i>
 *</code>
 *</pre>

 *<li> Add the UserConsent Activity in the ManifestFile:
 *<pre>
 *<code>
 * android:name="com.att.api.consentactivity.UserConsentActivity"
 * android:label="@string/title_activity_user_consent" >
 * </code>
 * </pre>  
 * <li> Start the UserconsentActivity for result from your MainActivity by passing the domainName, appId and appSecret as 
 * extras to get the oAuthCode on the onActivityResult callback
 * <pre>
 * <code>
 * Intent i = new Intent (this,com.att.api.consentactivity.UserConsentActivity.class);
 * i.putExtra("fqdn", Config.fqdn);
 * i.putExtra("clientId", Config.clientID);
 * i.putExtra("clientSecret", Config.secretKey);
 * i.putExtra("redirectUri", Config.redirectUri);
 * i.putExtra("appScope", Config.appScope);
 * startActivityForResult(i, OAUTH_CODE);
 * </pre>
 * </code>
 * <li>Obtain the token by passing the oAuthCode in onActivityResult
 * <pre>
 * <code>
 * aabManager = new AabManager(Config.fqdn, Config.clientID,Config.secretKey,new getTokenListener());
 * aabManager.getOAuthToken(oAuthCode);
 * </pre>
 * </code>
 * <li>OnSuccess call back of the token listener, you can call other APIs. An example can be found below:
 * <pre>
 * <code>
 * public class getTokenListener implements AttSdkListener {
 * 
 * public void onSuccess(Object response) {
 * OAuthToken authToken = (OAuthToken) response;
 * if (null != authToken) {
 * getAddressBookContacts();
 *  }
 * }
 *    
 *  public void onError(AttSdkError error) {
 *  //Your code for error        
 *  }
 * }   
 *  </pre>
 *  </code>   
 *  <i><b>Note : AccessToken can be cached so that the developer need not have to authorize everytime the app is launched.</b>
 *</ul>
 *</p>

 *
 *<p>
 *<b>Input Parameters used in Address Book API : </b>
 *</p>
 *<p>
* <i>Input parameters for some of the methods  require X-Fields, Pagination Parameters, Search Parameters.</i>
* <p>
* <p>
*  xFields Specifies String of field names that are expected on the response. 
	 * These field names should match the Contact object
	 * <p>
	 * Accepted options:
	 * <ul>
	 * <li>  x-fields: shallow -- Returns QuickContact Data model
			 If one or more search param are present in the request, then the x-fields value should set to shallow.
			 
		<li> x-fields: attribute names from Contact data structure  
			
		<li> x-fields:[empty value] -- No x-field header parameter Returns all contacts will all the attributes
	<p>		
		Note:
			a) Should use only one of the above options.
			b) Combining options 1, 2, 3 are invalid. Return full contact object for this condition.
			c) If a request contains search param and any value for x- fields param, 
			then default value for the x-fields is shallow [x-fields: shallow] should set in the request without returning any exception
	</p>
	</ul>		
	 *   Pagination Parameters :
	 * <ul>
	 * <li>	order Specifies the output sorting order. Valid values are
 			<li>	ASC: ascending (Default)
 			<li>	DESC: descending.
 	 *		
	 * <li>	orderBy Specifies the field name to be used for ordering the result set.
	 * <li>	limit Indicates limit value. Must be equal to or greater than 1, if not specifies default as 10.
	 * <li>	offset Indicates offset value . Must be greater than 0, if not specifies default as 1.
	 * 
	 * If pagination contains invalid parameters, this method will return an empty list
	 *</ul>
	 *   Search Parameters 
	 * <ul>
	 * <li> formattedName Specifies formatted name and it is derived information.
	 * <li> firstName Specifies first name of the contact.
	 * <li> lastName Specifies middle name of the contact
	 * <li> nickName Specifies alternate name or familiar form of proper name
	 * <li> email Specifies Email addresses associated with the contact such as official mail address, personal mail address etc.
	 * <li> phone Specifies Phone numbers associated with the contacts, such as Personal phone number, office phone number.
	 * <li> organization Specifies Organization name.
	 * <li> addressLine1 Specifies Street Address Line 1
	 * <li> addressLine2 Specifies Street Address Line 2
	 * <li> city Specifies City
	 * <li> zipcode Specifies State
	 * </ul>
 * </p>
 * 
 * <p>
 *<b>Data structures used in Address Book API : </b>
 *</p>
 *<p>
 *<i>AT&T Mobile Subscriber&#8217;s Contact object : if  x-fields: no-value or x-fields: attribute names.</i>
 *</p>
 *<p><b>Contact data structure:</b>
 *<ul>
 *<li><i>contactId:</i> Specifies Contact unique identifier. The contact id value should not be passed in the create/update contact operation
 *<li><i>creationDate:</i> Specifies date and time when the contact was created. This field  should not be passed in the create/update contact operation.
 *<li><i>modificationDate:</i> Specifies date and time when the contact was last modified. This field should not be passed in the create/update contact operation.
 *<li><i>formattedName:</i> Specifies formatted name and it is derived information. This field should not be passed in the create/update contact operation.
 *<li><i>firstName:</i> Specifies first name of the contact.
 *<li><i>middleName:</i> Specifies middle name of the contact.
 *<li><i>lastName: </i>Specifies last name of the contact
 *<li><i>prefix: </i>Specifies name prefix
 *<li><i>suffix:</i> Specifies name suffix
 *<li><i>nickname:</i> Specifies alternate name
 *<li><i>organization:</i> Specifies Organization name. 
 *<li><i>jobTitle:</i> Specifies Job title.
 *<li><i>anniversary:</i> Specifies date when a significant social commitment is honored. (Format: mm/dd/yyyy)
 *<li><i>gender:</i> Specifies gender of the contact.
 *<ul>
 *	<li>Male
 *	<li>Female
 *</ul> 
 *<li><i>spouse:</i> Specifies Name of the individual who functions as a domestic partner.
 *<li><i>children:</i>Specifies Comma separated children names.
 *<li><i>hobby:</i> Specifies Comma separated hobby
 *<li><i>assistant:</i> Specifies Comma separated assistant name
 *<li><i>phones:</i> Specifies list of Phone numbers associated with the contacts, such as Personal phone number, office phone number etc.
 *<li><i>addresses:</i> Specifies list of Addresses associated with Contact such as Home Address, Office address etc.
 *<li><i>emails:</i>  Specifies list of Email addresses associated with the contact such as official mail address, personal mail address etc. 
 *<li><i>ims:</i> Specifies list of Instance Messenger addresses. 
 *<li><i>weburls:</i>  Specifies Array of Web URL
 *<li><i>photo:</i> Specifies Contact&#8217;s Photo
 *</ul>
 *</p>
 *
 *
 *<p>
 *<i>AT&T Mobile Subscriber&#8217;s QuickContact object : if  x-fields: shallow </i>
 *</p>
 *
 *<p><b>Quick Contact data structure:</b>
 *<ul>
 *<li><i>contactId:</i> Specifies Contact unique identifier. 
 *<li><i>formattedName:</i> Specifies formatted name and it is derived information. 
 *<li><i>firstName:</i> Specifies first name of the contact.
 *<li><i>middleName:</i> Specifies middle name of the contact.
 *<li><i>lastName: </i>Specifies last name of the contact
 *<li><i>prefix: </i>Specifies name prefix
 *<li><i>suffix:</i> Specifies name suffix
 *<li><i>nickname:</i> Specifies alternate name
 *<li><i>organization:</i> Specifies Organization name. Mandatory if contact type is Business
 *<li><i>phone:</i> Specifies list of Phone numbers associated with the contacts, such as Personal phone number, office phone number etc.
 *<li><i>email:</i>  Specifies list of Email addresses associated with the contact such as official mail address, personal mail address etc. 
 *<li><i>im:</i> Specifies list of Instance Messenger addresses.
 *<li><i>address:</i> Specifies list of Addresses associated with Contact such as Home Address, Office address etc. 
 *</ul>
 *</p>
 *
 *
 *<p><b>Group data structure:</b>
 *<ul>
 *<li><i>groupId:</i> Specifies group identifier Id.
 *<li><i>groupName:</i> Specifies Name of the group.Group names are case insensitive. 
 *<li><i>groupType:</i> Specifies type of the group. This field is auto populated.Possible values are:
 *<ul>
 *<li>SYSTEM
 *<li>USER
 *</ul>
 *<p> Note : If a type different from USER is present in group, it will be ignored and reset to USER by default.</p> 
 *</ul>
 *</p>
 *
 *
 *<p><b>Address data structure:</b>
 *<ul>
 *<li><i>type:</i> Specifies the address type of a contact.Possible values are :
 *<ul>
 *<li>HOME
 *<li>WORK
 *<li>NO VALUE
 *</ul>
 *<li><i>preferred:</i> indicates True or false.
 *<li><i>poBox:</i> Specifies Post office Box
 *<li><i>addressLine1:</i> Specifies Street Address Line 1
 *<li><i>addressLine2:</i> Specifies Street Address Line 2
 *<li><i>city:</i> Specifies City
 *<li><i>state:</i> Specifies State
 *<li><i>zipcode:</i> Specifies Zip Code
 *<li><i>country:</i> Specifies Country
 *</ul>
 *</p>
 *
 *
 *<p><b>Email data structure:</b>
 *<ul>
 *<li><i>type:</i> Specifies email address type of a contact.The following type combinations are valid
 *<ul>
 *<li>INTERNET
 *<li>INTERNET,WORK
 *<li>INTERNET,HOME
 *<li>NO VALUE
 *</ul>
 *<li><i>emailAddress:</i> Specifies Email Address.
 *<li><i>preferred:</i> Indicates True or false
 *</ul>
 *</p>
 *
 *
 *<p><b>IM data structure:</b>
 *<ul>
 *<li><i>type:</i> Specifies IM type values of a contact.Pre-defined types
 *<li><i>imUri:</i> Specifies IM URI Identifier.
 *<li><i>preferred:</i> Indicates True or false
 *</ul>
 *</p>
 *
 *
 *<p><b>Phone data structure:</b>
 *<ul>
 *<li><i>type:</i> Specifies type of the phone number. The following combinations for types are valid:
 *<ul>
 *<li><i>CELL</i>
 *<li><i>HOME,CELL  </i>
 *<li><i>WORK,CELL </i>
 *<li><i>VOICE</i>
 *<li><i>HOME,VOICE</i>
 *<li><i>WORK,VOICE</i>
 *<li><i>FAX</i>
 *<li><i>HOME,FAX </i>
 *<li><i>WORK,FAX</i>
 *<li><i>VIDEO</i>
 *<li><i>HOME,VIDEO</i>
 *<li><i>WORK,VIDEO </i>
 *<li><i>PAGER</i>
 *<li><i>CAR</i>
 *<li><i>OTHER</i>
 *<li><i>NO VALUE</i>
 *</ul>
 *<li><i>number:</i> Specifies telephone number.
 *<li><i>preferred:</i> Indicates True or false
 *</ul>
 *</p>
 *
 *
 *<p><b>GroupResultSet data structure:</b>
 *<ul>
 *<li><i>totalRecords:</i> Specifies total number of group records
 *<li><i>totalPages:</i> Specifies total number of pages
 *<li><i>currentPageIndex:</i> Specifies the current page
 *<li><i>previousPage:</i> Specifies the previous page
 *<li><i>nextPage:</i>  Specifies the next page
 *<li><i>Group[]:</i> Group data structure
 *</ul>
 *</p>
 *
 *
 *<p><b>ContactResultSet data structure:</b>
 *<ul>
 *<li><i>totalRecords:</i> Specifies total number of  records
 *<li><i>totalPages:</i> Specifies total number of pages
 *<li><i>currentPageIndex:</i> Specifies the current page
 *<li><i>previousPage:</i> Specifies the previous page
 *<li><i>nextPage:</i>  Specifies the next page
 *<li><i>Contact[]:</i> Contact data structure
 *<li><i>QuickContact[]:</i> QuickContact data structure
 *</ul>
 *</p>
 *
 *
 * @author sm095n
 * @author ps350r
 * @since 1.0
 * @see com.att.api.aab.service
 */

package com.att.api.aab.manager;
