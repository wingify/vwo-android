package com.vwo.mobile.meg;

import android.text.TextUtils;
import android.util.Pair;

import com.vwo.mobile.models.Campaign;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.CRC32;

/**
 * Holds the local winner info.
 */
public class Winner {

    public enum LocalUserSearchRemark {
        SHOULD_RETURN_NULL, NOT_FOUND_FOR_PASSED_ARGS, SHOULD_RETURN_WINNER_CAMPAIGN
    }

    public static final String KEY_USER = "user";

    private static final String KEY_MAPPING = "mapping";

    private String user;

    private final ArrayList<Mapping> mappings = new ArrayList<>();

    public static Winner fromJSONObject(JSONObject jsonObject) {

        Winner winner = new Winner();

        try {

            winner.setUser(jsonObject.getString(KEY_USER));

            JSONArray jMappings = jsonObject.getJSONArray(KEY_MAPPING);
            int jMappingSize = jMappings.length();
            for (int i = 0; i < jMappingSize; i++) {

                JSONObject jMapping = jMappings.getJSONObject(i);

                Mapping _mapping = new Mapping();
                _mapping.setTestKey(jMapping.getString(Mapping.KEY_TEST_KEY));
                _mapping.setGroup(jMapping.getString(Mapping.KEY_GROUP));
                _mapping.setWinnerCampaign(jMapping.getString(Mapping.KEY_WINNER_CAMPAIGN));

                winner.addMapping(_mapping);
            }

        } catch (JSONException ignore) {
        }

        return winner;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<Mapping> getMappings() {
        return mappings;
    }

    public void addMapping(Mapping mapping) {

        MutuallyExclusiveGroups.log(mapping.getAsJson().toString());
        boolean found = false;
        for (Mapping m : mappings) {
            if (m.isSameAs(mapping)) {
                found = true;
                break;
            }
        }

        if (!found) {
            mappings.add(mapping);
        }
    }

    public JSONObject getJSONObject() {
        JSONObject json = new JSONObject();
        try {
            json.put(KEY_USER, user);

            JSONArray mappingArray = new JSONArray();
            for (Mapping mapping : mappings) {
                // map each mapping object
                mappingArray.put(mapping.getAsJson());
            }
            json.put(KEY_MAPPING, mappingArray);
        } catch (JSONException ignore) {
        }
        return json;
    }

    public Pair<LocalUserSearchRemark, Object> getRemarkForUserArgs(Mapping mapping, HashMap<String, String> args) {

        boolean isGroupIdPresent = (!TextUtils.isEmpty(args.get(MutuallyExclusiveGroups.ID_GROUP)));
        boolean isTestKeyPresent = (!TextUtils.isEmpty(args.get(Campaign.TEST_KEY)));

        if (!isGroupIdPresent && !isTestKeyPresent) {
            // there's no point in evaluating the stored values if both are null
            // as this is a user error
            return new Pair<>(LocalUserSearchRemark.NOT_FOUND_FOR_PASSED_ARGS, null);
        }

        final String EMPTY = "";

        for (Mapping m : mappings) {

            // because "" = null for mappings
            String _group = EMPTY.equals(m.getGroup()) ? null : m.getGroup();

            boolean isGroupSame = Objects.equals(_group, mapping.getGroup());
            boolean isTestKeySame = Objects.equals(m.getTestKey(), mapping.getTestKey());

            if (isGroupIdPresent && isGroupSame) {
                // cond 1. if { groupId } is PRESENT then there is no need to check for the { test_key }
                if (EMPTY.equals(m.winnerCampaign)) {
                    return new Pair<>(LocalUserSearchRemark.SHOULD_RETURN_NULL, null);
                }
                return new Pair<>(LocalUserSearchRemark.SHOULD_RETURN_WINNER_CAMPAIGN, (Object) m.winnerCampaign);
            } else if (!isGroupIdPresent && isTestKeySame) {
                // cond 2. if { groupId } is NOT PRESENT then then check for the { test_key }
                if (EMPTY.equals(m.winnerCampaign)) {
                    return new Pair<>(LocalUserSearchRemark.SHOULD_RETURN_NULL, null);
                }
                return new Pair<>(LocalUserSearchRemark.SHOULD_RETURN_WINNER_CAMPAIGN, (Object) m.winnerCampaign);
            }

        }

        return new Pair<>(LocalUserSearchRemark.NOT_FOUND_FOR_PASSED_ARGS, null);
    }

    /**
     * Holds the meta for the parent class.
     */
    public static class Mapping {

        protected static final String KEY_GROUP = "group";
        private static final String KEY_TEST_KEY = Campaign.TEST_KEY;

        private static final String KEY_WINNER_CAMPAIGN = "winner_campaign";

        private String group;

        private String testKey;

        private String winnerCampaign;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getTestKey() {
            return testKey;
        }

        public void setTestKey(String testKey) {
            this.testKey = testKey;
        }

        public String getWinnerCampaign() {
            return winnerCampaign;
        }

        public void setWinnerCampaign(String winnerCampaign) {
            this.winnerCampaign = winnerCampaign;
        }

        public boolean isSameAs(Mapping mapping) {
            return (crc32() == mapping.crc32());
        }

        private long crc32() {
            CRC32 crc32 = new CRC32();
            if (group != null) {
                byte[] bGroup = group.getBytes();
                crc32.update(bGroup);
            }
            if (testKey != null) {
                byte[] bTestKey = testKey.getBytes();
                crc32.update(bTestKey);
            }
            if (winnerCampaign != null) {
                byte[] bWinnerCampaign = winnerCampaign.getBytes();
                crc32.update(bWinnerCampaign);
            }

            long value = crc32.getValue();
            MutuallyExclusiveGroups.log("generated CRC32 is -> " + value + " for -> group: " + group + ", testKey: " + testKey + ", winnerCampaign:" + winnerCampaign);
            return value;
        }

        public JSONObject getAsJson() {

            JSONObject json = new JSONObject();

            try {
                if (group == null) {
                    json.put(KEY_GROUP, "");
                } else {
                    json.put(KEY_GROUP, group);
                }

                if (testKey == null) {
                    json.put(KEY_TEST_KEY, "");
                } else {
                    json.put(KEY_TEST_KEY, testKey);
                }

                if (winnerCampaign == null) {
                    json.put(KEY_WINNER_CAMPAIGN, "");
                } else {
                    json.put(KEY_WINNER_CAMPAIGN, winnerCampaign);
                }
            } catch (JSONException ignore) {
            }

            return json;
        }

    }

}
