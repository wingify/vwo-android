package com.vwo.mobile.v3;

import androidx.annotation.Nullable;

import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOUrlBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by { Nabin Niroula } on 21/03/2023 for android-sdk.
 **/

public class ServerResponse {

    public static void log(String message) {
        VWOLog.d("ServerResponse", message, true);
    }

    private static final char STANDARD_API_VERSION_3 = 3;

    private static final char LEGACY_API_VERSION_2 = 2;

    private static final char START_ARRAY_JSON = '[';

    private static final char END_ARRAY_JSON = ']';

    private static final char START_OBJECT_JSON = '{';

    private static final char END_OBJECT_JSON = '}';

    private final String data;

    private JSONObject v3Json;

    public ServerResponse(@Nullable String data) {
        this.data = data;
    }

    public JSONObject getV3Json() {
        return v3Json;
    }

    /**
     * Checks if the passed data string is a valid JSON Object.
     *
     * @return true - when the passed json during the instantiation is valid else false.
     */
    public boolean isJsonValid() {
        if (data == null) return false;

        try {
            // check if valid JSON Object
            new JSONObject(data);
            return true;
        } catch (JSONException ignore) {
        }

        try {
            // check if valid JSON Array
            new JSONArray(data);
            return true;
        } catch (JSONException ignore) {
        }

        return false;
    }

    /**
     * Changes made to accommodate the EU region clients
     * more documentation here:
     * <a href="https://confluence.wingify.com/display/VWOENG/Mobile+Testing+-+EU+account+handling">EU Account Handling</a>
     *
     * @return true if the JSON follows new standard mentioned in the link above.
     */
    public boolean isNewStandardApi() {

        if (!isJsonValid()) return false;

        try {

            v3Json = new JSONObject(data);

            if (!v3Json.has(VWOUrlBuilder.API_VERSION)) return false;

            final int apiVersion = v3Json.getInt(VWOUrlBuilder.API_VERSION);

            return (STANDARD_API_VERSION_3 == apiVersion);
        } catch (JSONException ex) {

            return false;
        }
    }

    public boolean isLegacyApi() {
        try {
            new JSONArray(data);
            final int localApiVersion = (int) Integer.parseInt(VWOUrlBuilder.VALUE_API_VERSION);
            return (LEGACY_API_VERSION_2 == localApiVersion);
        } catch (JSONException e) {
            return false;
        }
    }

    public String getData() {
        return data;
    }

    public boolean isEventArchEnabled() {
        if (v3Json == null)
            return false;

        return v3Json.optBoolean("isEventArchEnabled", false);
    }

    public boolean isMobile360Enabled() {
        if (v3Json == null)
            return false;

        return v3Json.optBoolean("isMobile360Enabled", false);
    }
}
