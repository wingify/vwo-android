package com.vwo.mobile;

import android.support.annotation.NonNull;

import com.vwo.mobile.events.PreviewListener;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.vwo.mobile.Connection.FAILED;
import static com.vwo.mobile.Connection.NOT_STARTED;
import static com.vwo.mobile.Connection.OPTED_OUT;
import static com.vwo.mobile.Connection.STARTED;
import static com.vwo.mobile.Connection.STARTING;

public class VWOSocket {
    private static final String EMIT_DEVICE_CONNECTED = "register_mobile";
    private static final String EMIT_GOAL_TRIGGERED = "goal_triggered";
    private static final String EMIT_RECEIVE_VARIATION_SUCCESS = "receive_variation_success";

    private static final String ON_BROWSER_DISCONNECT = "browser_disconnect";
    private static final String ON_BROWSER_CONNECT = "browser_connect";
    private static final String ON_VARIATION_RECEIVED = "receive_variation";

    private static final String JSON_KEY_VARIATION_ID = "variationId";
    private static final String JSON_KEY_BROWSER_NAME = "name";
    private static final String JSON_KEY_DEVICE_NAME = "name";
    private static final String JSON_KEY_DEVICE_TYPE = "type";
    private static final String JSON_KEY_APP_KEY = "appKey";
    private static final String JSON_KEY_GOAL_NAME = "goal";

    private static final String DEVICE_TYPE = "android";

    private Socket mSocket;
    private PreviewListener mPreviewListener;
    private String mAppKey;

    private Map<String, Object> mVariationKeys;
    private JSONObject mVariation;

    @Connection.State
    private int mSocketConnectionState;

    private Emitter.Listener mServerConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocketConnectionState = STARTED;
            VWOLog.v(VWOLog.SOCKET_LOGS, "Device connected to socket");
            registerDevice();
        }
    };
    private Emitter.Listener mServerDisconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mPreviewListener.onPreviewDisabled();
            mSocketConnectionState = NOT_STARTED;
            VWOLog.v(VWOLog.SOCKET_LOGS, "Finished device preview");
        }
    };
    private Emitter.Listener mBrowserConnectedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                String browserName = data.getString(JSON_KEY_BROWSER_NAME);
                VWOLog.v(VWOLog.SOCKET_LOGS, "Device connected to Server: " + browserName);

                mSocketConnectionState = STARTED;
                mPreviewListener.onPreviewEnabled();
                VWOLog.v(VWOLog.SOCKET_LOGS, "Started device preview with User: " + browserName);
            } catch (JSONException exception) {
                mSocketConnectionState = FAILED;
                VWOLog.e(VWOLog.SOCKET_LOGS, "Browser Name key not found or cannot be parsed", exception, false, true);
            } catch (Exception exception) {
                mSocketConnectionState = FAILED;
                VWOLog.e(VWOLog.SOCKET_LOGS, exception, false, true);
            }

        }
    };

    VWOSocket(@NonNull PreviewListener previewListener, String appKey) {
        this.mPreviewListener = previewListener;
        this.mSocketConnectionState = NOT_STARTED;
        this.mAppKey = appKey;
    }

    /**
     * Connects device to server
     */
    public void init() {
        if (mSocketConnectionState < OPTED_OUT) {
            try {
                mSocketConnectionState = STARTING;
                IO.Options opts = new IO.Options();
                opts.reconnection = true;
                enableSocketLogging(opts);

                mSocket = IO.socket(BuildConfig.SOCKET_URL, opts);
                mSocket.connect();

                mSocket.on(Socket.EVENT_DISCONNECT, mServerDisconnected);
                mSocket.on(Socket.EVENT_CONNECT, mServerConnected);

                mSocket.on(ON_VARIATION_RECEIVED, mVariationListener);
                mSocket.on(ON_BROWSER_CONNECT, mBrowserConnectedListener);
                mSocket.on(ON_BROWSER_DISCONNECT, mBrowserDisconnectedListener);
                VWOLog.v(VWOLog.SOCKET_LOGS, "Connecting to Socket.");
            } catch (URISyntaxException exception) {
                mSocketConnectionState = FAILED;
                VWOLog.e(VWOLog.SOCKET_LOGS, "Malformed url", exception, false, true);
            }
        } else if (mSocketConnectionState == STARTED) {
            VWOLog.v(VWOLog.SOCKET_LOGS, "Device already connected to server.");
        } else {
            VWOLog.v(VWOLog.SOCKET_LOGS, "Connection in progress...");
        }
    }

    public void triggerGoal(String goalName) {
        JSONObject goalTriggerData = new JSONObject();
        try {
            goalTriggerData.put(JSON_KEY_GOAL_NAME, goalName);
        } catch (JSONException exception) {
            VWOLog.e(VWOLog.SOCKET_LOGS, "Unable to build json object", exception, true, true);
        }
        mSocket.emit(EMIT_GOAL_TRIGGERED, goalTriggerData);

    }

    private Emitter.Listener mVariationListener = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                try {
                    mVariation = data.getJSONObject("json");
                    generateVariationHash();
                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SOCKET_LOGS, "Unable to parse json object", exception, true, true);
                }


                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(JSON_KEY_VARIATION_ID, data.getInt(JSON_KEY_VARIATION_ID));
                    VWOLog.v(VWOLog.SOCKET_LOGS, "Socket data :\n" + jsonObject.toString());

                } catch (JSONException exception) {
                    VWOLog.e(VWOLog.SOCKET_LOGS, "Variation id cannot be parsed", exception, true, true);
                }
                mSocket.emit(EMIT_RECEIVE_VARIATION_SUCCESS, jsonObject);
            } catch (Exception exception) {
                VWOLog.e(VWOLog.SOCKET_LOGS, exception, true, true);
            }
        }
    };

    private void enableSocketLogging(IO.Options options) {
        if (BuildConfig.ENABLE_SOCKET_LOGS && VWOUtils.checkIfClassExists("okhttp3.logging.HttpLoggingInterceptor") &&
                VWOUtils.checkIfClassExists("okhttp3.OkHttpClient")) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build();

            options.callFactory = client;
            options.webSocketFactory = client;
        }
    }

    private void registerDevice() {
        JSONObject deviceData = new JSONObject();
        try {
            deviceData.put(JSON_KEY_DEVICE_NAME, VWOUtils.getDeviceName());
            deviceData.put(JSON_KEY_DEVICE_TYPE, DEVICE_TYPE);
            deviceData.put(JSON_KEY_APP_KEY, mAppKey);
            VWOLog.v(VWOLog.SOCKET_LOGS, "Registering device to Server");
        } catch (JSONException exception) {
            VWOLog.e(VWOLog.SOCKET_LOGS, "Unable to build json object", exception, true, true);
        }

        mSocket.emit(EMIT_DEVICE_CONNECTED, deviceData);
    }

    private void generateVariationHash() {
        if (mVariationKeys == null) {
            mVariationKeys = new HashMap<>();
        }

        Iterator<?> keys;
        keys = mVariation.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                mVariationKeys.put(key, mVariation.get(key));
            } catch (JSONException exception) {
                VWOLog.e(VWOLog.SOCKET_LOGS, "Issue generating hash", exception, false, true);

            }
        }
    }

    private Emitter.Listener mBrowserDisconnectedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mPreviewListener.onPreviewDisabled();
            VWOLog.i(VWOLog.SOCKET_LOGS, "Finished device preview", true);
        }
    };

    public JSONObject getVariation() {
        return mVariation;
    }

    public Object getVariationForKey(String key) {

        if (mVariationKeys != null && mVariationKeys.containsKey(key)) {
            return mVariationKeys.get(key);
        } else {
            return null;
        }

    }
}