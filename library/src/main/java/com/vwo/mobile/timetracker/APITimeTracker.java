package com.vwo.mobile.timetracker;

public class APITimeTracker {

    private static final String PATTERN = "/mobile";

    public static void trackFor(String url) {
        if (!url.contains(PATTERN)) return;
        TimeTracker.startTracking(TimeTracker.KEY_API_INIT_DURATION);
    }

    public static void updateFor(String url) {
        if (!url.contains(PATTERN)) return;
        TimeTracker.updateTracking(TimeTracker.KEY_API_INIT_DURATION);
    }

}
