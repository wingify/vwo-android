package com.vwo.mobile.v3;

import android.text.TextUtils;

import com.vwo.mobile.VWO;
import com.vwo.mobile.data.VWOLocalData;
import com.vwo.mobile.meg.CampaignGroupMapper;
import com.vwo.mobile.meg.Group;
import com.vwo.mobile.meg.MutuallyExclusiveGroups;
import com.vwo.mobile.utils.VWOLog;
import com.vwo.mobile.utils.VWOPreference;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by { Nabin Niroula } on 21/03/2023 for android-sdk.
 **/
public class MEGManager {

    private static final String EMPTY = "";

    private final VWO sSharedInstance;

    private final VWOLocalData mVWOLocalData;

    public MEGManager(VWO sSharedInstance) {
        this.sSharedInstance = sSharedInstance;
        this.mVWOLocalData = new VWOLocalData(sSharedInstance);
    }

    public String getCampaign(String userId, HashMap<String, String> args) {

        if (userId == null || TextUtils.isEmpty(userId)) {
            // use the (default | random) user id
            VWOPreference vwoPreference = new VWOPreference(sSharedInstance.getCurrentContext());
            userId = VWOUtils.getDeviceUUID(vwoPreference);
        }

        JSONArray campaignsData = mVWOLocalData.getData();
        if (campaignsData == null || campaignsData.length() == 0) return null; // MEG data not found

        JSONObject megGroupsData = getMEGData(campaignsData);
        if (megGroupsData == null) return null; // MEG data not found

        HashMap<String, Group> mappedData = CampaignGroupMapper.createAndGetGroups(megGroupsData);

        MutuallyExclusiveGroups meg = new MutuallyExclusiveGroups(userId);
        meg.addGroups(mappedData);
        return meg.getCampaign(args, campaignsData);
    }

    private JSONObject getMEGData(JSONArray campaignsData) {
        JSONObject megGroupsData = new JSONObject();

        // last item is always the MEG data
        int campaignDataLastIndex = (campaignsData.length() - 1);
        for (int i = campaignDataLastIndex; i >= 0; i--) {
            try {

                JSONObject groupDataItem = campaignsData.getJSONObject(i);

                if (!groupDataItem.has(VWO.Constants.CAMPAIGN_TYPE)) {
                    return null;
                }

                String cType = groupDataItem.optString(VWO.Constants.CAMPAIGN_TYPE, EMPTY);
                if (VWO.Constants.CAMPAIGN_GROUPS.equals(cType)) {
                    megGroupsData = groupDataItem;
                    break;
                }
            } catch (JSONException exception) {
                VWOLog.e(VWOLog.DATA_LOGS, exception, true, false);
            }
        }

        return megGroupsData;
    }

}
