package com.vwo.mobile.data;

import android.text.TextUtils;

import com.vwo.mobile.Vwo;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by abhishek on 07/10/15 at 6:07 PM.
 */
public class VwoLocalData {

    @SuppressWarnings("SpellCheckingInspection")
    private static final String LOCAL_DATA = "truncDafavwo";

    private Vwo mVwo;

    public VwoLocalData(Vwo vwo) {
        mVwo = vwo;
    }

    public boolean isLocalDataPresent() {
        String data = mVwo.getVwoPreference().getString(LOCAL_DATA);
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
        mVwo.getVwoPreference().putString(LOCAL_DATA, data.toString());
    }

    public JSONArray getData() {
        String data = mVwo.getVwoPreference().getString(LOCAL_DATA);
        try {
            return new JSONArray(data);
        } catch (JSONException e) {
            return new JSONArray();
        }
    }
}
