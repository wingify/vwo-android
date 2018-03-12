package com.vwo.mobile.data;

import android.text.TextUtils;

import com.vwo.mobile.VWO;

import org.json.JSONArray;
import org.json.JSONException;

public class VWOLocalData {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String LOCAL_DATA = "truncDafavwo";

    private VWO mVWO;

    public VWOLocalData(VWO vwo) {
        mVWO = vwo;
    }

    public boolean isLocalDataPresent() {
        String data = mVWO.getVwoPreference().getString(LOCAL_DATA);
        if (TextUtils.isEmpty(data)) {
            return false;
        } else {
            try {
                new JSONArray(data);
                return true;
            } catch (JSONException e) {
                return false;
            }
        }
    }

    public void saveData(JSONArray data) {
        mVWO.getVwoPreference().putString(LOCAL_DATA, data.toString());
    }

    public JSONArray getData() {
        String data = mVWO.getVwoPreference().getString(LOCAL_DATA);
        try {
            return new JSONArray(data);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }
}
