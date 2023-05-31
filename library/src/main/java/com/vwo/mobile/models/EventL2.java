package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.KEY_EVENT;
import static com.vwo.mobile.constants.AppConstants.KEY_MSG_ID;
import static com.vwo.mobile.constants.AppConstants.KEY_SESSION_ID;
import static com.vwo.mobile.constants.AppConstants.KEY_UUID;
import static com.vwo.mobile.constants.AppConstants.KEY_VISITOR;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Level 2 event model.
 *
 * @author swapnilchaudhari
 */
public class EventL2 implements IEvent {
    private final String msgId;
    private final String visId;
    private final long sessionId;
    private final EventL3 eventL3;
    private final Visitor visitor;

    public EventL2(String msgId, String visId, long sessionId, EventL3 eventL3, Visitor visitor) {
        this.msgId = msgId;
        this.visId = visId;
        this.sessionId = sessionId;
        this.eventL3 = eventL3;
        this.visitor = visitor;
    }

    // Getter Methods

    public String getMsgId() {
        return msgId;
    }

    public String getVisId() {
        return visId;
    }

    public float getSessionId() {
        return sessionId;
    }

    public EventL3 getEvent() {
        return eventL3;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject()
                .put(KEY_MSG_ID, msgId)
                .put(KEY_UUID, visId)
                .put(KEY_SESSION_ID, sessionId)
                .put(KEY_EVENT, eventL3.toJson());

        if (visitor != null)
            jsonObject.put(KEY_VISITOR, visitor.toJson());

        return jsonObject;
    }
}
