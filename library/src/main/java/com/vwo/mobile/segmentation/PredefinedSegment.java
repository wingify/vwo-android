package com.vwo.mobile.segmentation;

import com.vwo.mobile.VWO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class PredefinedSegment extends Segment {

    private static final String ID = "id";
    private static final String SEGMENT_CODE = "segment_code";

    private String mId;
    private String mSegmentKey;
    private Object mSegmentValue;
    private static final String TYPE = "predefined";


    public PredefinedSegment(VWO vwo, JSONObject segment) {
        super(vwo);
        try {
            mId = segment.getString(ID);
            JSONObject data = segment.getJSONObject(SEGMENT_CODE);
            Iterator<String> keys = data.keys();
            mSegmentKey = keys.next();
            mSegmentValue = data.get(mSegmentKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean evaluate() {
        return PredefinedSegmentEnum.getEvaluator(mSegmentKey).evaluate(vwo, mSegmentValue);
    }

    @Override
    public boolean isCustomSegment() {
        return false;
    }

}
