package com.vwo.mobile.events;

import androidx.annotation.NonNull;

import com.vwo.mobile.VWO;
import com.vwo.mobile.models.EventL1;
import com.vwo.mobile.models.EventL2;
import com.vwo.mobile.models.EventL3;
import com.vwo.mobile.models.Goal;
import com.vwo.mobile.models.GoalProps;
import com.vwo.mobile.models.Metric;
import com.vwo.mobile.models.VwoMeta;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;

public class TrackGoalPostEvent extends PostEvent {

    private final Goal goal;

    public TrackGoalPostEvent(VWO vwo, Goal goal) {
        super(vwo);
        this.goal = goal;
    }

    public String getBody(long campaignId, Double goalValue) {

        try {
            String sdkVersion = String.valueOf(VWO.version());
            boolean isCustomEvent = true;
            JSONArray goalIds = new JSONArray().put(getFormat("g_%d", goal.getId()));
            Metric metric = new Metric(getFormat("id_%d", campaignId), goalIds);
            VwoMeta meta = new VwoMeta(metric, goal.getRevenueProp(), goalValue);
            GoalProps goalProps = new GoalProps(SDK_NAME, sdkVersion, isCustomEvent, meta);
            long time = eventTime;
            EventL3 eventL3 = new EventL3(goalProps, getEventName(), time);
            EventL2 eventL2 = new EventL2(getMessageId(), getVisitorId(), getSessionId(), eventL3, null);

            EventL1 eventL1 = new EventL1(eventL2);
            return eventL1.toJson().toString();
        } catch (JSONException exception) {
            //Will never happen
            return "{}";
        }
    }

    @NonNull
    private String getFormat(String format, Object value) {
        return String.format(Locale.US, format, value);
    }

    @Override
    String getEventName() {
        return goal.getIdentifier();
    }
}
