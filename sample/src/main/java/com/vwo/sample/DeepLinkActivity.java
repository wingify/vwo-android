package com.vwo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.airbnb.deeplinkdispatch.DeepLinkHandler;

/**
 * Created by aman on 21/08/17.
 */

@DeepLinkHandler({AppDeepLinkModule.class})
public class DeepLinkActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // DeepLinkDelegate, LibraryDeepLinkModuleLoader and AppDeepLinkModuleLoader
        // are generated at compile-time.
        DeepLinkDelegate deepLinkDelegate =
                new DeepLinkDelegate(new AppDeepLinkModuleLoader());
        // Delegate the deep link handling to DeepLinkDispatch.
        // It will start the correct Activity based on the incoming Intent URI
        deepLinkDelegate.dispatchFrom(this);
        // Finish this Activity since the correct one has been just started
        finish();
    }
}
