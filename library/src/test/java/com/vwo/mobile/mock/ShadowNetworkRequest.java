package com.vwo.mobile.mock;

import android.net.Uri;
import androidx.annotation.NonNull;

import com.vwo.mobile.network.NetworkRequest;
import com.vwo.mobile.network.Response;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.lang.reflect.Field;
import java.net.URL;

/**
 * Created by aman on Tue 12/12/17 16:42.
 */

@Implements(value = NetworkRequest.class)
@PrepareForTest(NetworkRequest.class)
public class ShadowNetworkRequest {

    @RealObject
    private NetworkRequest request;

    public void __constructor__(@NonNull String url, @NonNull String method, Response.Listener<String> listener,
                                Response.ErrorListener errorListener) throws Exception {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Field privateURL = NetworkRequest.class.getDeclaredField("url");
        privateURL.setAccessible(true);
        builder.scheme("http").encodedAuthority("127.0.0.1:6666");
        privateURL.set(request, new URL(builder.build().toString()));
    }
}
