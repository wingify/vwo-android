package com.vwo.mobile.meg;

import android.text.TextUtils;
import android.util.Pair;

import com.vwo.mobile.VWO;
import com.vwo.mobile.models.Campaign;
import com.vwo.mobile.utils.VWOPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by { Nabin Niroula } on 02/05/2023 for android-sdk.
 **/
public class WinnerManager {

    public static class Response {

        private boolean isNewUser;

        private boolean shouldServePreviousWinnerCampaign;

        private String winnerCampaign;

        public boolean shouldServePreviousWinnerCampaign() {
            return shouldServePreviousWinnerCampaign;
        }

        public void setShouldServePreviousWinnerCampaign(boolean shouldServePreviousWinnerCampaign) {
            this.shouldServePreviousWinnerCampaign = shouldServePreviousWinnerCampaign;
        }

        public String getWinnerCampaign() {
            return winnerCampaign;
        }

        public void setWinnerCampaign(String winnerCampaign) {
            this.winnerCampaign = winnerCampaign;
        }

        public boolean isNewUser() {
            return isNewUser;
        }

        public void setNewUser(boolean newUser) {
            isNewUser = newUser;
        }

    }

    private static final String KEY_SAVED_ARRAY_OF_WINNER_CAMPAIGNS = "winner_mappings";

    private final VWOPreference vwoPreference;

    public WinnerManager(VWO sSharedInstance) {
        vwoPreference = sSharedInstance.getVwoPreference();
    }

    private boolean isEmpty(JSONArray root) {
        return (root.length() == 0);
    }

    public Response getSavedDetailsFor(String userId, HashMap<String, String> args) {

        try {

            // check if this user is present locally
            String previousWinnerLocalData = vwoPreference.getString(KEY_SAVED_ARRAY_OF_WINNER_CAMPAIGNS);
            int userIndex = getIndexIfUserExist(userId, previousWinnerLocalData);
            if (userIndex == -1) {

                Response response = new Response();
                response.setNewUser(true);
                return response;
            }

            JSONArray root = new JSONArray(previousWinnerLocalData);

            JSONObject user = root.getJSONObject(userIndex);
            Winner winner = Winner.fromJSONObject(user);

            // prepare groupId and test_key
            String groupId = args.get(MutuallyExclusiveGroups.ID_GROUP);
            String testKey = null;

            if (groupId == null) {
                // test_key will only be applicable when there is no groupId
                testKey = args.get(Campaign.TEST_KEY);
            }

            Winner.Mapping mapping = prepareWinnerMappingUsing(groupId, testKey, null);
            Pair<Winner.LocalUserSearchRemark, Object> remarkWithResult = winner.getRemarkForUserArgs(mapping, args);

            Winner.LocalUserSearchRemark remark = remarkWithResult.first;
            if (remark == Winner.LocalUserSearchRemark.SHOULD_RETURN_WINNER_CAMPAIGN) {

                Response response = new Response();
                response.setNewUser(false);
                response.setShouldServePreviousWinnerCampaign(true);
                response.setWinnerCampaign((String) remarkWithResult.second);
                return response;
            } else if (remark == Winner.LocalUserSearchRemark.SHOULD_RETURN_NULL) {

                Response response = new Response();
                response.setNewUser(false);
                response.setShouldServePreviousWinnerCampaign(true);
                response.setWinnerCampaign(null);
                return response;
            } else {

                // treat this block as -> (Winner.LocalUserSearchRemark.NOT_FOUND_FOR_PASSED_ARGS)
                // we did not find anything related to the provided args
                // we should treat this like a new user and MEG should be applied.
                Response response = new Response();
                response.setNewUser(true);
                response.setShouldServePreviousWinnerCampaign(false);
                response.setWinnerCampaign(null);
                return response;
            }

        } catch (JSONException e) {
            return null;
        }

    }

    public boolean save(String userId, String winnerCampaign, HashMap<String, String> args) {
        try {
            saveThrowingException(userId, winnerCampaign, args);
            return true;
        } catch (Exception ignore) {
            return false;
        }
    }

    private void saveThrowingException(String userId, String winnerCampaign, HashMap<String, String> args) throws JSONException {

        JSONArray root = new JSONArray();

        // check for existing data in the shared preferences
        String previousWinnerLocalData = vwoPreference.getString(KEY_SAVED_ARRAY_OF_WINNER_CAMPAIGNS);
        if (!TextUtils.isEmpty(previousWinnerLocalData)) {
            root = new JSONArray(previousWinnerLocalData);
        }

        // if there is groupId then campaign will be ignored altogether
        String groupId = args.get(MutuallyExclusiveGroups.ID_GROUP);
        String testKey = null;

        if (groupId == null) {
            // test_key will only be applicable when there is no groupId
            testKey = args.get(Campaign.TEST_KEY);
        }

        if (isEmpty(root)) {
            Winner firstWinner = prepareWinnerUsing(userId, winnerCampaign, groupId, testKey);
            root.put(firstWinner.getJSONObject());
            storeLocally(root);
            return;
        }

        int index = getIndexIfUserExist(userId, previousWinnerLocalData);
        if (index == -1) {
            // this user didn't exist treat as new
            Winner firstWinner = prepareWinnerUsing(userId, winnerCampaign, groupId, testKey);
            root.put(firstWinner.getJSONObject());
            storeLocally(root);
            return;
        }

        // existing user exist at index simply update that index
        JSONObject current = root.getJSONObject(index);
        Winner currentWinner = Winner.fromJSONObject(current);

        // try to add new values if it doesn't already exist
        Winner.Mapping mapping = prepareWinnerMappingUsing(groupId, testKey, winnerCampaign);
        currentWinner.addMapping(mapping);

        // replace with new value just in case
        root.put(index, currentWinner.getJSONObject());
        storeLocally(root);

    }

    private void storeLocally(JSONArray root) throws JSONException {
        vwoPreference.putString(KEY_SAVED_ARRAY_OF_WINNER_CAMPAIGNS, root.toString(0));
    }

    private int getIndexIfUserExist(String userId, String previousWinnerLocalData) {
        try {
            JSONArray jsonArray = new JSONArray(previousWinnerLocalData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject current = jsonArray.getJSONObject(i);
                if (current.getString(Winner.KEY_USER).equals(userId)) return i;
            }
        } catch (JSONException e) {
            return -1;
        }
        return -1;
    }

    private Winner prepareWinnerUsing(String userId, String winnerCampaign, String groupId, String testKey) {

        final Winner winner = new Winner();
        winner.setUser(userId);

        Winner.Mapping mapping = prepareWinnerMappingUsing(groupId, testKey, winnerCampaign);
        winner.addMapping(mapping);

        return winner;
    }

    private Winner.Mapping prepareWinnerMappingUsing(String groupId, String testKey, String winnerCampaign) {

        final Winner.Mapping mapping = new Winner.Mapping();
        mapping.setGroup(groupId);
        mapping.setTestKey(testKey);
        mapping.setWinnerCampaign(winnerCampaign);
        return mapping;
    }

}
