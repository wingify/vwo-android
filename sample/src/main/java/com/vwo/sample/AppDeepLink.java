package com.vwo.sample;

import com.airbnb.deeplinkdispatch.DeepLinkSpec;

/**
 * Created by aman on 21/08/17.
 */

@DeepLinkSpec(prefix = {"app://vwo"})
public @interface AppDeepLink {
    String[] value();
}
