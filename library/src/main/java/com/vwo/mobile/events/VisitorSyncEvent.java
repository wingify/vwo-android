package com.vwo.mobile.events;

import com.vwo.mobile.VWO;
import com.vwo.mobile.models.EventL1;
import com.vwo.mobile.models.EventL2;
import com.vwo.mobile.models.EventL3;
import com.vwo.mobile.models.Visitor;
import com.vwo.mobile.models.VisitorProps;
import com.vwo.mobile.models.VisitorSyncPushProps;
import com.vwo.mobile.utils.VWOUtils;

import org.json.JSONException;

public class VisitorSyncEvent extends PostEvent {

    public VisitorSyncEvent(VWO vwo) {
        super(vwo);
    }

    public String getBody(String tagKey, String tagValue) {
        try {
            VisitorProps visitorProps = new VisitorProps(tagKey, tagValue);
            Visitor visitor = new Visitor(visitorProps);

            String sdkVersion = String.valueOf(VWO.version());
            boolean isCustomEvent = true;
            VisitorSyncPushProps goalProps = new VisitorSyncPushProps(SDK_NAME, sdkVersion, isCustomEvent, visitor);
            long time = eventTime;
            EventL3 eventL3 = new EventL3(goalProps, getEventName(), time);
            EventL2 eventL2 = new EventL2(getMessageId(), getVisitorId(), getSessionId(), eventL3, visitor);

            EventL1 eventL1 = new EventL1(eventL2);
            return eventL1.toJson().toString();
        } catch (JSONException exception) {
            //Will never happen
            return "{}";
        }
    }

    @Override
    String getEventName() {
        return "vwo_syncVisitorProp";
    }
}
