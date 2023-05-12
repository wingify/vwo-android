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

                Group group = new Group();
                group.setId(Integer.parseInt(key));

                String groupName = objGroup.getString(KEY_NAME);
                group.setName(groupName);

                prepareWeight(objGroup, group);
                prepareCampaigns(objGroup, group);
                prepareEt(objGroup, group);
                preparePriority(objGroup, group);

                groups.put(groupName, group);
            }
        } catch (JSONException exception) {
            VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
        }

        return groups;
    }

    private static void preparePriority(JSONObject source, Group destination) throws JSONException {
        if (!source.has(Group.KEY_PRIORITY)) return;

        JSONArray priority = source.getJSONArray(Group.KEY_PRIORITY);
        MutuallyExclusiveGroups.log("priority should be given to these campaigns -> " + priority);
        for (int pIndex = 0; pIndex < priority.length(); ++pIndex) {
            destination.addPriority(priority.getString(pIndex));
        }
    }

    private static void prepareEt(JSONObject source, Group destination) throws JSONException {
        if (!source.has(Group.KEY_ET)) return;

        int et = source.getInt(Group.KEY_ET);
        destination.addEt(et);
    }

    private static void prepareCampaigns(JSONObject source, Group destination) throws JSONException {
        if (!source.has(KEY_CAMPAIGNS)) return;

        JSONArray arrCampaigns = source.getJSONArray(KEY_CAMPAIGNS);
        for (int index = 0; index < arrCampaigns.length(); index++) {
            destination.addCampaign(arrCampaigns.getString(index));
        }
    }

    private static void prepareWeight(JSONObject source, Group destination) throws JSONException {
        if (!source.has(Group.KEY_WEIGHT)) return;

        MutuallyExclusiveGroups.log("------------------------------------------------------------");
        MutuallyExclusiveGroups.log("preparing for -> " + destination.getName());
        MutuallyExclusiveGroups.log("found weight sent from server, preparing the weight value for later usage.");
        MutuallyExclusiveGroups.log("NOTE: these weight will only be applied if no priority campaign exist.");
        JSONObject weights = source.getJSONObject(Group.KEY_WEIGHT);
        Iterator<String> campaigns = weights.keys();
        while (campaigns.hasNext()) {
            String c = campaigns.next();
            int w = weights.getInt(c);
            destination.addWeight(c, w);
        }
        MutuallyExclusiveGroups.log("------------------------------------------------------------");
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
