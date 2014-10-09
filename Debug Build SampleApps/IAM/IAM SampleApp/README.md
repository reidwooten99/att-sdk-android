## AT&T In-App Messaging(IAM) SampleApp Install Guide
**IAM** service allows a developer to send SMS or MMS message on behalf of an AT&T mobile subscriber.

1. Import the Android IAM SampleApp & IAM SDK source code to eclipse.
2. Update the following in the config.java
	APP_KEY
	APP_SECRET
	REDIRECT_URI
	These values will be available in the Application created in the AT&T developer portal.

3. Clean & Build both the IAM Sample App & IAM SDK
   _ Firstly, clean and build the IAM SDK. Set it as a library by checking the box "Is library" on its "Project Build Target" section.
   _ Secondly, access the "Project Build target" section in the IAM Sample App, add the "IAMSDK" as a reference. Apply, then clean and build the IAM Sample App. 

If you get an error with the message likes "Could not find SDK folder ... ", you need to point the SDK location to your SDK path.

4. Run the sample app.


