VWO Android SDK
======================================

This open source library allows you to A/B Test your Android app.

**IMPORTANT! VWO Android SDK is currently a beta release; its content
and functionality are likely to change significantly and without warning.**

Getting Started
---------------

1. Download the latest [VWO Android SDK](https://github.com/wingify/vwo-android/releases).
2. Have a look at [integrating SDK article](https://vwo.com/knowledge/integrating-android-sdk/)
   on the VWO Knowledge Base Website.

## Requirements

* Android 4.0 (API 14) or later

## Credentials

This SDK requires an app key. You can sign up for an account at [VWO](https://vwo.com). Once there, you can add a new Android App, and use the generated app key in the app.


## Setting up VWO account
* Sign Up for VWO account at https://vwo.com
* Create a new android app from create menu
* Use the app generated app key, while integrating SDK into android app.
* Create and run campaigns.

## How to import in gradle:
In your top level build.gradle file add the following code under dependencies.

	maven { url 'https://raw.githubusercontent.com/wingify/vwo-mobile-android/0.2.0/' }
	
Add vwo and socket.io dependency to app/build.gradle file

	dependencies {
	    ...
	    compile io.socket:io-client:0.6.1'
	    compile 'com.vwo:mobile:0.2.0'
	    ...
	}


## License

By using this SDK, you agree to abide by the [VWO Terms & Conditions](http://vwo.com/terms-conditions).
