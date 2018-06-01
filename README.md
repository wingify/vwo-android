VWO Android SDK
======================================

This open source library allows you to A/B Test your Android app.

Getting Started
---------------

1. Download the latest [VWO Android SDK](https://github.com/wingify/vwo-android/releases).
2. Have a look at [integrating SDK article](https://vwo.com/knowledge/integrating-android-sdk/)
   on the VWO Knowledge Base Website.

## Requirements

* Android 4.0 (API 14) or later

## Credentials

This SDK requires an app key. You can sign up for an account at [VWO](https://vwo.com). 
Once there, you can add a new Android App, and use the generated app key in the app.


## Setting up VWO account
* Sign Up for VWO account at https://vwo.com
* Create a new android app from create menu
* Use the app generated app key, while integrating SDK into android app.
* Create and run campaigns.

## How to import in gradle:
In your top level build.gradle file add the following code under repositories.

    buildscript {
        ...
        repositories {
            ...
        }
    }
    
    allprojects {
        repositories {
            ...
            maven {
                url 'https://raw.githubusercontent.com/wingify/vwo-mobile-android/master/'
            }
            ...
        }
    }
	
Add dependencies to app/build.gradle file

	dependencies {
	    ...
	    implementation 'com.vwo:mobile:2.4.0@aar'
        implementation ('io.socket:socket.io-client:1.0.0') {
            // excluding org.json which is provided by Android
            exclude group: 'org.json', module: 'json'
        }
        
        // Skip this if you are already including support library in your app.
        implementation 'com.android.support:support-core-utils:27.1.1'
	    ...
	}
	
Update your project AndroidManifest.xml with following permissions:

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

## License

By using this SDK, you agree to abide by the [VWO Terms & Conditions](http://vwo.com/terms-conditions).
