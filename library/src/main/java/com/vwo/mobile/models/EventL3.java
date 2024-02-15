package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.KEY_NAME;
import static com.vwo.mobile.constants.AppConstants.KEY_PROPS;
import static com.vwo.mobile.constants.AppConstants.KEY_TIME;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Level 3 event model.
 *
 * @author swapnilchaudhari
 */
public class EventL3 implements IEvent {
    private final EventProps props;
    private final String name;
    private final long time;

    /**
     * Create level 3 event.
     *
     * @param props Event properties
     * @param name  Event name
     * @param time  Current time in millis
     */
    public EventL3(EventProps props, String name, long time) {
        this.props = props;
        this.name = name;
        this.time = time;
    }

    public EventProps getProps() {
        return props;
    }

    public String getName() {
        return name;
    }

    public float getTime() {
        return time;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject()
                .put(KEY_PROPS, props.toJson())
                .put(KEY_NAME, name)
                .put(KEY_TIME, time);
    }
}
