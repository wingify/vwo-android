package com.vwo.mobile.models;

import static com.vwo.mobile.constants.AppConstants.KEY_PROPS;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Visitor for event model.
 *
 * @author swapnilchaudhari
 */
public class Visitor implements IEvent {
    private final VisitorProps visitorProps;

    public Visitor(VisitorProps visitorProps) {
        this.visitorProps = visitorProps;
    }

    // Getter Methods

    public VisitorProps getProps() {
        return visitorProps;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        return new JSONObject().put(KEY_PROPS, visitorProps.toJson());
    }
}