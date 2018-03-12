package com.vwo.mobile.events;

public interface VWOStatusListener {

    void onVWOLoaded();

    void onVWOLoadFailure(String reason);

}
