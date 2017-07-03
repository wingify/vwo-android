package com.vwo.mobile;

import com.vwo.mobile.constants.ApiConstant;
import com.vwo.mobile.utils.TestUtils;
import com.vwo.mobile.utils.VWOLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by abhishek on 24/06/15 at 1:41 PM.
 */
public class VWOSocket {
    private static final Logger LOGGER = VWOLogger.getLogger(VWOSocket.class.getCanonicalName());

    private static final String EMIT_DEVICE_CONNECTED = "register_mobile";
    private static final String EMIT_GOAL_TRIGGERED = "goal_triggered";
    private static final String EMIT_RECEIVE_VARIATION_SUCCESS = "receive_variation_success";

    private static final String ON_BROWSER_DISCONNECT = "browser_disconnect";
    private static final String ON_BROWSER_CONNECT = "browser_connect";
    private static final String ON_VARIATION_RECEIVED = "receive_variation";
    private static final String ON_SERVER_DISCONNECTED = "disconnect";
    private static final String ON_SERVER_CONNECTED = "connect";

    private static final String JSON_KEY_VARIATION_ID = "variationId";
    private static final String JSON_KEY_BROWSER_NAME = "name";
    private static final String JSON_KEY_DEVICE_NAME = "name";
    private static final String JSON_KEY_DEVICE_TYPE = "type";
    private static final String JSON_KEY_APP_KEY = "appKey";
    private static final String JSON_KEY_GOAL_NAME = "goal";

    private static final String DEVICE_TYPE = "android";

    private Socket mSocket;
    private String mAppKey;
    private VWO mVWO;

    private Map<String, Object> mVariationKeys;
    private JSONObject mVariation;

    private Emitter.Listener mServerConnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            LOGGER.info("Socked Connected");
            registerDevice();
        }
    };
    private Emitter.Listener mServerDisconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mVWO.setIsEditMode(false);
            LOGGER.info("Finished device preview");
        }
    };

    public VWOSocket(VWO vwo) {
        this.mVWO = vwo;
        this.mAppKey = mVWO.getConfig().getAppKey();
    }

    /**
     * Connects to socket if the device is in debug mode ie., not a release build
     */
    public void connectToSocket() {
        if (!mVWO.getVwoUtils().isDebugMode()) {
            // Return do not connect socket
            return;
        }

        try {

            IO.Options opts = new IO.Options();
            opts.reconnection = true;

            mSocket = IO.socket(ApiConstant.SOCKET_URL, opts);
            mSocket.connect();

            mSocket.on(ON_SERVER_DISCONNECTED, mServerDisconnected);
            mSocket.on(ON_SERVER_CONNECTED, mServerConnected);

            mSocket.on(ON_VARIATION_RECEIVED, mVariationListener);
            mSocket.on(ON_BROWSER_CONNECT, mBrowserConnectedListener);
            mSocket.on(ON_BROWSER_DISCONNECT, mBrowserDisconnectedListener);


        } catch (URISyntaxException e) {
            LOGGER.throwing(VWOSocket.class.getSimpleName(), "connectToSocket()", e);
        }
    }

    private void registerDevice() {
        JSONObject deviceData = new JSONObject();
        try {
            deviceData.put(JSON_KEY_DEVICE_NAME, TestUtils.getDeviceName());
            deviceData.put(JSON_KEY_DEVICE_TYPE, DEVICE_TYPE);
            deviceData.put(JSON_KEY_APP_KEY, mAppKey);
        } catch (JSONException e) {
            LOGGER.throwing(VWOSocket.class.getSimpleName(), "registerDevice()", e);
        }

        mSocket.emit(EMIT_DEVICE_CONNECTED, deviceData);
    }

    public void triggerGoal(String goalName) {
        JSONObject goalTriggerData = new JSONObject();
        try {
            goalTriggerData.put(JSON_KEY_GOAL_NAME, goalName);
        } catch (JSONException e) {
            LOGGER.throwing(VWOSocket.class.getSimpleName(), "triggerGoal()", e);
        }
        mSocket.emit(EMIT_GOAL_TRIGGERED, goalTriggerData);

    }

    private Emitter.Listener mVariationListener = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                mVariation = data.getJSONObject("json");
                generateVariationHash();
            } catch (JSONException e) {
                LOGGER.throwing(VWOSocket.class.getSimpleName(), "variationListenerJsonException", e);
            }


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(JSON_KEY_VARIATION_ID, data.getInt(JSON_KEY_VARIATION_ID));
                LOGGER.info(jsonObject.toString());

            } catch (JSONException e) {
                LOGGER.throwing(VWOSocket.class.getSimpleName(), " Variation id cannot be parsed", e);
            }

            mSocket.emit(EMIT_RECEIVE_VARIATION_SUCCESS, jsonObject);
        }
    };

    private Emitter.Listener mBrowserConnectedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            try {
                String browserName = data.getString(JSON_KEY_BROWSER_NAME);
                LOGGER.info("Browser connected: " + browserName);

                mVWO.setIsEditMode(true);
                LOGGER.info("Started device preview with User: " + browserName);
            } catch (JSONException e) {
                LOGGER.throwing(VWOSocket.class.getSimpleName(), "Browser Name key not found or cannot be parsed", e);
            }

        }
    };

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
            } catch (JSONException e) {
                LOGGER.throwing(VWOSocket.class.getSimpleName(), "Issue generating hash", e);

            }
        }
    }

    private Emitter.Listener mBrowserDisconnectedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mVWO.setIsEditMode(false);
            LOGGER.info("Finished device preview");

        }
    };

    public JSONObject getVariation() {
        return mVariation;
    }

    public Object getObjectForKey(String key) {

        if (mVariationKeys != null && mVariationKeys.containsKey(key)) {
            return mVariationKeys.get(key);
        } else {
            return null;
        }

    }
}

