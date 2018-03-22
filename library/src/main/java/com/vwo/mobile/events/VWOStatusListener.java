package com.vwo.mobile.events;

public interface VWOStatusListener {

    /**
     * This method is called when VWO SDK is loaded successfully.
     */
    void onVWOLoaded();

    /**
     * This method is called when VWO SDK fails to load.
     *
     * @param reason the cause of the failure.
     */
    void onVWOLoadFailure(String reason);

}
