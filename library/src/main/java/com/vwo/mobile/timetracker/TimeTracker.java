package com.vwo.mobile.timetracker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TimeTracker {

    public final static String KEY_TOTAL_INIT_DURATION = "totalInitDuration";

    public static final String KEY_BEFORE_API_INIT_DURATION = "beforeAPIInitDuration";
    public static final String KEY_AFTER_API_INIT_DURATION = "afterAPIInitDuration";

    public final static String KEY_API_INIT_DURATION = "APIInitDuration";

    private final static HashMap<String, TimeData> timeDataHashMap = new HashMap<>();

    public static void startTracking(String key) {
        long time = System.nanoTime();
        timeDataHashMap.remove(key);

        TimeData timeData = new TimeData();
        timeData.timeOne = time;
        timeDataHashMap.put(key, timeData);
    }

    public static void updateTracking(String key) {
        long time = System.nanoTime();
        TimeData timeData = timeDataHashMap.get(key);
        if (timeData == null) return;
        timeData.timeTwo = time;
        timeDataHashMap.put(key, timeData);
    }

    public static TimeData getTimeData(String key) {
        return timeDataHashMap.get(key);
    }

    /**
     * Method to get duration of before execution of api-call in sdk init process.
     *
     * @return duration in ms
     */
    public static long getBeforeApiInitDuration() {
        if (timeDataHashMap.containsKey(KEY_BEFORE_API_INIT_DURATION)) {
            return timeDataHashMap.get(KEY_BEFORE_API_INIT_DURATION).getDiffInMs();
        }

        return 0;
    }

    /**
     * Method to get duration of after execution of api-call in sdk init process.
     *
     * @return duration in ms
     */
    public static long getAfterApiInitDuration() {
        if (timeDataHashMap.containsKey(KEY_AFTER_API_INIT_DURATION)) {
            return timeDataHashMap.get(KEY_AFTER_API_INIT_DURATION).getDiffInMs();
        }

        return 0;
    }

    /**
     * Method to get duration of api-call in sdk init process.
     *
     * @return duration in ms
     */
    public static long getApiInitDuration() {
        if (timeDataHashMap.containsKey(KEY_API_INIT_DURATION)) {
            return timeDataHashMap.get(KEY_API_INIT_DURATION).getDiffInMs();
        }

        return 0;
    }

    /**
     * Method to get total duration of sdk init process.
     *
     * @return duration in ms
     */
    public static long getTotalInitDuration() {
        if (timeDataHashMap.containsKey(KEY_TOTAL_INIT_DURATION)) {
            return timeDataHashMap.get(KEY_TOTAL_INIT_DURATION).getDiffInMs();
        }

        return 0;
    }


    private static class TimeData {
        long timeOne = 0;
        long timeTwo = 0;

        public long getDiff() {
            return timeTwo - timeOne;
        }

        public long getDiffInMs() {
            return TimeUnit.NANOSECONDS.toMillis(getDiff());
        }

    }

}
