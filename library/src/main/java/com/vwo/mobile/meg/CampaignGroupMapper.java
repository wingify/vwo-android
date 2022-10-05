package com.vwo.mobile.meg;

import com.vwo.mobile.utils.VWOLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class CampaignGroupMapper {

    public static final String KEY_CAMPAIGN_GROUPS = "campaignGroups";

    public static final String KEY_GROUPS = "groups";

    public static final String KEY_NAME = "name";

    public static final String KEY_CAMPAIGNS = "campaigns";

    public static HashMap<String, Group> createAndGetGroups(JSONObject jsonObject) {

        HashMap<String, Group> groups = new HashMap<>();

        try {
            JSONObject jsonGroups = getGroups(jsonObject);

            if (jsonGroups == null) return groups;

            Iterator<String> itrJsonGroups = jsonGroups.keys();
            while (itrJsonGroups.hasNext()) {
                String key = itrJsonGroups.next();

                JSONObject objGroup = jsonGroups.getJSONObject(key);
                JSONArray arrCampaigns = objGroup.getJSONArray(KEY_CAMPAIGNS);

                String groupName = objGroup.getString(KEY_NAME);

                Group group = new Group();
                group.setName(groupName);
                group.setId(Integer.parseInt(key));

                for (int index = 0; index < arrCampaigns.length(); index++) {
                    group.addCampaign(arrCampaigns.getString(index));
                }

                groups.put(groupName, group);
            }
        } catch (JSONException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
        }

        return groups;
    }

    private static JSONObject getGroups(JSONObject jsonObject) {
        JSONObject jsonGroups = null;
        try {
            jsonGroups = jsonObject.getJSONObject(KEY_GROUPS);
        } catch (JSONException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
        }
        return jsonGroups;
    }

    private static JSONObject getCampaignGroups(JSONObject jsonObject) {
        JSONObject jsonCampaignGroups = null;
        try {
            jsonCampaignGroups = jsonObject.getJSONObject(KEY_CAMPAIGN_GROUPS);
        } catch (JSONException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
        }
        return jsonCampaignGroups;
    }

}
