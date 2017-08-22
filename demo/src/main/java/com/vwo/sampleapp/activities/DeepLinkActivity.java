package com.vwo.sampleapp.activities;

import android.os.Bundle;

import com.airbnb.deeplinkdispatch.DeepLinkHandler;
import com.vwo.sampleapp.interfaces.AppDeepLinkModule;
import com.vwo.sampleapp.interfaces.AppDeepLinkModuleLoader;

/**
 * Created by aman on 21/08/17.
 */

@DeepLinkHandler({AppDeepLinkModule.class})
public class DeepLinkActivity extends BaseActivity {
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
