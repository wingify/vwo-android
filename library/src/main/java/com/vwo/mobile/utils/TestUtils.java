package com.vwo.mobile.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.vwo.mobile.constants.AppConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by abhishek on 10/06/15 at 5:44 PM.
 */
public class TestUtils {
    public static JSONObject loadTestData(Context context, boolean isEditMode) throws JSONException, IOException {


        String filePath;
        if (isEditMode) {
            filePath = AppConstants.CONFIG_FILE_EDIT;
        } else {
            filePath = AppConstants.CONFIG_FILE;
        }

        FileInputStream inputStream = context.openFileInput(filePath);

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;

        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr);
        }
        return new JSONObject(responseStrBuilder.toString());
    }

    public static void saveExperiments(Context context, JSONObject experiment, boolean isEditMode) throws IOException {

        String filePath;
        if (isEditMode) {
            filePath = AppConstants.CONFIG_FILE_EDIT;
        } else {
            filePath = AppConstants.CONFIG_FILE;
        }

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filePath, Context.MODE_PRIVATE));
        outputStreamWriter.write(experiment.toString());
        outputStreamWriter.close();

    }

    public static JSONObject mergeJsonObjects(JSONObject topLevelJson, JSONObject fromJson) throws JSONException {
        VWOLog.v(VWOLog.TEST_LOGS, String.format(Locale.ENGLISH, "JSON 1: %s, JSON 2: %s", topLevelJson.toString(), fromJson.toString()));

        JSONObject merged = new JSONObject();
        JSONObject[] objs = new JSONObject[]{fromJson, topLevelJson};
        for (JSONObject obj : objs) {
            Iterator it = obj.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                merged.put(key, obj.get(key));
            }
        }
        VWOLog.v(VWOLog.TEST_LOGS, String.format(Locale.ENGLISH, String.format(Locale.ENGLISH,
                "Merged JSON : %s", merged.toString()), fromJson.toString()));

        return merged;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        if (manufacturer.equalsIgnoreCase("HTC")) {
            // make sure "HTC" is fully capitalized.
            return "HTC " + model;
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }
        return phrase.toString();
    }


}
