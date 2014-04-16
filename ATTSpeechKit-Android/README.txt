Licensed by AT&T under 'Software Development Kit Tools Agreement' 2012.
TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/sdk_agreement/
Copyright 2012 AT&T Intellectual Property. All rights reserved. 
For more information contact developer.support@att.com http://developer.att.com

# AT&T Speech SDK for Android

This SDK consists of a JAR file with the ATTSpeechKit library and the documentation in PDF format.

# Installation

To use the ATTSpeechKit library, link the JAR with your Android project.  We recommend putting the JAR file in a directory called `libs`, which the Android Build Tools will automatically link to your code.  If you write custom build script or Ant task, make sure it copies all resources inside the JAR.

# Usage

Refer to the full documentation in the PDF file.   A short summary follows:
* Create an application ID and secret in the AT&T Developer Portal.  Your application will use those as OAuth credentials.
* Add directives to the Android Manifest for your application to enable audio input and network I/O.
* Write code to perform OAuth client credential checking. The AT&T Developer Portal has sample code that performs this checking.
* Choose the ATTSpeechKit class (ATTSpeechActivity or ATTSpeechService) that works best in your application architecture.
               

