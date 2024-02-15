package com.vwo.mobile.events;

import com.vwo.mobile.VWO;
import com.vwo.mobile.models.EventL1;
import com.vwo.mobile.models.EventL2;
import com.vwo.mobile.models.EventL3;
import com.vwo.mobile.models.TrackUserProps;
import com.vwo.mobile.models.Visitor;
import com.vwo.mobile.models.VisitorProps;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONException;

public class TrackUserPostEvent extends PostEvent {

    public TrackUserPostEvent(VWO vwo) {
        super(vwo);
    }

    public String getBody(long campaignId, int variationId) {

        try {
            String sdkVersion = String.valueOf(VWO.version());
            TrackUserProps trackUserProps = new TrackUserProps(SDK_NAME, sdkVersion, campaignId, variationId);
            long time = eventTime;
            EventL3 eventL3 = new EventL3(trackUserProps, getEventName(), time);
            EventL2 eventL2 = new EventL2(getMessageId(), getVisitorId(), getSessionId(), eventL3, null);

            EventL1 eventL1 = new EventL1(eventL2);
            return eventL1.toJson().toString();
        } catch (JSONException exception) {
            //Will never happen
            return "{}";
        }
    }

    @Override
    String getEventName() {
        return "vwo_variationShown";
    }
}
