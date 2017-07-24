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
	
Add vwo and socket.io dependency to app/build.gradle file

	dependencies {
	    ...
	    compile 'com.vwo:mobile:2.0.0-beta1@aar'
        compile ('io.socket:socket.io-client:1.0.0') {
            // excluding org.json which is provided by Android
            exclude group: 'org.json', module: 'json'
        }
        compile 'io.sentry:sentry-android:1.1.0'
	    ...
	}
	
## Initializing SDK

##### Launching VWO SDK in Asynchronous mode.
```java
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.vwo.mobile.VWO;

public class MainActivity extends ActionBarActivity {
    private static final String VWO_API_KEY = "YOUR_VWO_API_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      // Start VWO SDK in Async mode
      VWO.with(this, VWO_APP_KEY).launch();
    }
}
```

##### Launching VWO SDK in Asynchronous mode with callback

```java
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.vwo.mobile.VWO;

public class MainActivity extends ActionBarActivity {
    private static final String VWO_API_KEY = "YOUR_VWO_API_KEY";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      // Start VWO SDK in Async mode with callback
      VWO.with(this, VWO_API_KEY).launch(new VWOStatusListener() {
          @Override
          public void onVWOLoaded() {
              // VWO loaded successfully
          }
          @Override
          public void onVWOLoadFailure() {
              // VWO not loaded
          }
      });
    }
  }
```

##### Launching VWO SDK in synchronous mode
**(NOT RECOMMENDED)**

```java
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.vwo.mobile.VWO;

public class MainActivity extends ActionBarActivity {
    private static final String VWO_API_KEY = "YOUR_VWO_API_KEY";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      // Start VWO SDK in Sync mode
      VWO.with(this, VWO_API_KEY).launchSynchronously();
    }
  }
```

**Note:** There is timeout of 2000 milliseconds for launching 
VWO SDK in synchronous mode in order to avoid ANRs.

## Advanced SDK configuration

You can setup VWO Config while initialising SDK
    
    // Config for adding custom user segmentation parameters before launch.
    Map<String, String> userSegmentationMapping = new HashMap<>();
    customKeys.put("key", "value");
    
    VWOConfig vwoConfig = new VWOConfig
            .Builder()
            .setCustomSegmentationMapping(userSegmentationMapping)
            .build();
                
This config can be set during the VWO SDK launch:

    VWO.with(this, VWO_APP_KEY).config(vwoConfig).launch();

## Using Campaigns
For fetching variation for a given key inside a campaign, use the following code.
if key is not found in any of the running campaigns, null value is returned, Otherwise an object is
returned corresponding to given key.

```
String key1 = "your-campaign-key";
Object variation = VWO.getVariationForKey(key1);
```

Or

You can also use following method for fetching a variation and passing a default value which is 
returned back in case no key matches.

```
String key2 = "another-campaign-key";
Object variation2 = VWO.getVariationForKey(key2, "default_value");
```

## Triggering goals

You can mark a goal conversion using following methods:

```
VWO.markConversionForGoal("conversionGoal");

VWO.markConversionForGoal("conversionGoal", 133.25);
```

second method is for marking a revenue goals you can pass the revenue value in double as second 
parameter to the function.


## License

By using this SDK, you agree to abide by the [VWO Terms & Conditions](http://vwo.com/terms-conditions).
