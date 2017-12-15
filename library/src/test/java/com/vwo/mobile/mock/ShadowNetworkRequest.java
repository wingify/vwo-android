package com.vwo.mobile.mock;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.vwo.mobile.network.NetworkRequest;
import com.vwo.mobile.network.Response;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

/**
 * Created by aman on Tue 12/12/17 16:42.
 */

@Implements(value = NetworkRequest.class, inheritImplementationMethods = true)
@PrepareForTest(NetworkRequest.class)
public class ShadowNetworkRequest {

    @RealObject
    NetworkRequest request;

    public void __constructor__(@NonNull String url, @NonNull String method, Response.Listener<String> listener,
                                Response.ErrorListener errorListener) throws Exception {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.scheme("http").encodedAuthority("127.0.0.1:6666");
        request.setUrl(builder.build().toString());
    }
}
