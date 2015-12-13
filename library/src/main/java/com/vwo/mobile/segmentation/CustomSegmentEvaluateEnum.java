package com.vwo.mobile.segmentation;

import com.vwo.mobile.Vwo;
import com.vwo.mobile.constants.AppConstants;
import com.vwo.mobile.utils.VwoLog;
import com.vwo.mobile.utils.VwoUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by abhishek on 22/09/15 at 4:48 PM.
 */
public enum CustomSegmentEvaluateEnum {


    ANDROID_VERSION_EQUAL_TO(AppConstants.ANDROID_VERSION, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (Integer.parseInt(VwoUtils.androidVersion()) == Integer.parseInt(data.getString(i))) {
                        return true;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VwoLog.e("CustomSegmentEvaluateEnum", ex);
                }
            }
            return false;
        }
    }),

    ANDROID_VERSION_NOT_EQUAL_TO(AppConstants.ANDROID_VERSION, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (Integer.parseInt(VwoUtils.androidVersion()) == Integer.parseInt(data.getString(i))) {
                        return false;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VwoLog.e("CustomSegmentEvaluateEnum", ex);
                }
            }
            return true;
        }
    }),

    DAY_OF_WEEK_EQUAL_TO(AppConstants.DAY_OF_WEEK, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {

            Calendar c = GregorianCalendar.getInstance();
            int dayOfWeek = c.get(GregorianCalendar.DAY_OF_WEEK) - 1;
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (dayOfWeek == Integer.parseInt(data.getString(i))) {
                        return true;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VwoLog.e("CustomSegmentEvaluateEnum", ex);
                }
            }
            return false;
        }
    }),

    DAY_OF_WEEK_NOT_EQUAL_TO(AppConstants.DAY_OF_WEEK, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {

            Calendar c = GregorianCalendar.getInstance();
            int dayOfWeek = c.get(GregorianCalendar.DAY_OF_WEEK) - 1;
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (dayOfWeek == Integer.parseInt(data.getString(i))) {
                        return false;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VwoLog.e("CustomSegmentEvaluateEnum", ex);
                }
            }
            return true;
        }
    }),

    HOUR_OF_DAY_EQUAL_TO(AppConstants.HOUR_OF_DAY, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            Calendar c = GregorianCalendar.getInstance();
            int hourOfTheDay = c.get(GregorianCalendar.HOUR_OF_DAY);
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (hourOfTheDay == Integer.parseInt(data.getString(i))) {
                        return true;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VwoLog.e("CustomSegmentEvaluateEnum", ex);
                }
            }
            return false;
        }
    }),

    HOUR_OF_DAY_NOT_EQUAL_TO(AppConstants.HOUR_OF_DAY, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            Calendar c = GregorianCalendar.getInstance();
            int hourOfTheDay = c.get(GregorianCalendar.HOUR_OF_DAY);
            for (int i = 0; i < data.length(); i++) {
                try {
                    if (hourOfTheDay == Integer.parseInt(data.getString(i))) {
                        return false;
                    }
                } catch (NumberFormatException | JSONException ex) {
                    VwoLog.e("CustomSegmentEvaluateEnum", ex);
                }
            }
            return true;
        }
    }),

    LOCATION_EQUAL_TO(AppConstants.LOCATION, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            String locale = VwoUtils.getLocale();
            if (locale.contains("_")) {
                String[] tempLocale = locale.split("_");
                locale = tempLocale[1];
            }

            for (int i = 0; i < data.length(); i++) {
                try {
                    if (data.getString(i).equalsIgnoreCase(locale)) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }),

    LOCATION_NOT_EQUAL_TO(AppConstants.LOCATION, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            String locale = VwoUtils.getLocale();
            if (locale.contains("_")) {
                String[] tempLocale = locale.split("_");
                locale = tempLocale[1];
            }

            for (int i = 0; i < data.length(); i++) {
                try {
                    if (data.getString(i).equalsIgnoreCase(locale)) {
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }),

    APP_VERSION_EQUAL_TO(AppConstants.APP_VERSION, AppConstants.EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            String appVersion = VwoUtils.applicationVersion(vwo);
            for (int i = 0; i < data.length(); i++) {
                try {
                    String version = data.getString(i);
                    if (version.equals(appVersion)) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }),

    APP_VERSION_NOT_EQUAL_TO(AppConstants.APP_VERSION, AppConstants.NOT_EQUAL_TO, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            String appVersion = VwoUtils.applicationVersion(vwo);
            for (int i = 0; i < data.length(); i++) {
                try {
                    String version = data.getString(i);
                    if (version.equals(appVersion)) {
                        return false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }),

    APP_VERSION_MATCHES_REGEX(AppConstants.APP_VERSION, AppConstants.MATCHES_REGEX, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            String appVersion = VwoUtils.applicationVersion(vwo);
            for (int i = 0; i < data.length(); i++) {
                try {
                    Pattern pattern = Pattern.compile(data.getString(i));
                    Matcher matcher = pattern.matcher(appVersion);
                    if (matcher.find()) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }),

    APP_VERSION_CONTAINS(AppConstants.APP_VERSION, AppConstants.CONTAINS, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            String appVersion = VwoUtils.applicationVersion(vwo);
            for (int i = 0; i < data.length(); i++) {
                try {
                    String version = data.getString(i);
                    if (appVersion.contains(version)) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }),

    APP_VERSION_STARTS_WITH(AppConstants.APP_VERSION, AppConstants.STARTS_WITH, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            String appVersion = VwoUtils.applicationVersion(vwo);
            for (int i = 0; i < data.length(); i++) {
                try {
                    String version = data.getString(i);
                    if (appVersion.startsWith(version)) {
                        return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }),
    DEFAULT("", -11, new EvaluateSegment() {
        @Override
        public boolean evaluate(Vwo vwo, JSONArray data) {
            return false;
        }
    });


    private int mOperator;
    private String mType;
    private EvaluateSegment mSegmentFunction;

    CustomSegmentEvaluateEnum(String type, int operator, EvaluateSegment segmentFunction) {
        mOperator = operator;
        mType = type;
        mSegmentFunction = segmentFunction;
    }

    public static EvaluateSegment getEvaluator(String type, int operator) {
        CustomSegmentEvaluateEnum[] evaluators = CustomSegmentEvaluateEnum.values();
        for(CustomSegmentEvaluateEnum evaluator : evaluators) {
            if (type.equals(evaluator.getType()) && (operator == evaluator.getOperator())) {
                return evaluator.getSegmentFunction();
            }
        }
        return DEFAULT.getSegmentFunction();
    }

    public int getOperator() {
        return mOperator;
    }

    public String getType() {
        return mType;
    }

    public EvaluateSegment getSegmentFunction() {
        return mSegmentFunction;
    }

    public interface EvaluateSegment {
        boolean evaluate(Vwo vwo, JSONArray data);
    }
}
