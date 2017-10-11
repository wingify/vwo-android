package com.vwo.sampleapp.activities;

/**
 * Created by aman on 14/08/17.
 */

/*
 * Copyright 2015 Amandeep Anguralla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.vwo.sampleapp.interfaces.ChangeFragment;

import java.util.logging.Logger;

/**
 * This is the base Activity extending {@link android.support.v7.app.AppCompatActivity}.
 * <p>
 * All other activities extends from this activity, following an inheritance pattern.
 * </p>
 */
public abstract class BaseActivity extends AppCompatActivity implements ChangeFragment {

    private static final Logger LOGGER = Logger.getLogger(BaseActivity.class.getCanonicalName());
    private static final String ARG_CURRENT_FRAGMENT_ID = "current_fragment_id";
    private boolean activityDestroyed;
    private boolean activityStopped;
    private int currentFragmentID;


    @CallSuper
    @Override
    public void loadFragment(@Nullable Bundle bundle, int fragmentId, @Nullable String tag) {
        currentFragmentID = fragmentId;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            currentFragmentID = savedInstanceState.getInt(ARG_CURRENT_FRAGMENT_ID);
        } else {
            currentFragmentID = -1;
        }
        setActivityDestroyed(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setActivityStopped(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        setActivityStopped(true);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        setActivityDestroyed(true);
        super.onDestroy();
    }

    /**
     * Method to check for whether the activity is destroyed or not.
     *
     * @return a {@link Boolean} value based on current state of activity.
     */
    public boolean isActivityDestroyed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return isDestroyed();
        else
            return activityDestroyed;
    }

    /**
     * Value is automatically set when {@link BaseActivity#onDestroy()} method
     * is called and automatically unset when {@link BaseActivity#onCreate(Bundle)} method is called.
     *
     * @param activityDestroyed a {@link Boolean} to set if activity is destroyed or not.
     */
    private void setActivityDestroyed(boolean activityDestroyed) {
        this.activityDestroyed = activityDestroyed;
    }

    /**
     * Method to check state of the activity that if it is stopped or not.
     *
     * @return a {@link Boolean} value based on current state of activity
     */
    protected boolean isActivityStopped() {
        return activityStopped;
    }

    /**
     * Value is automatically set when {@link BaseActivity#onStop()} ()} method
     * is called and automatically unset when {@link BaseActivity#onResume()} method is called.
     *
     * @param activityStopped a {@link Boolean} to set if activity is stopped or not
     */
    private void setActivityStopped(boolean activityStopped) {
        this.activityStopped = activityStopped;
    }

    /**
     * This function shows a {@link android.widget.Toast}.
     * <p/>
     * Toast will be visible for a limited period of time even if this function is called multiple
     * times.
     *
     * @param parentView is the parent view with respect to which {@link Snackbar} is shown.
     * @param message    is the {@link String} to be shown in toast message.
     * @param buttonText is the {@link String} for actionable button in {@link Snackbar}.
     * @param listener   is the {@link View.OnClickListener} for the actionable button.
     * @param duration   {@link Snackbar.Duration}
     */
    protected Snackbar showToast(View parentView, String message, String buttonText,
                                 View.OnClickListener listener, int duration) {
        try {
            Snackbar snackbar = Snackbar.make(parentView, message, duration);
            if (listener != null) {
                snackbar.setAction(buttonText, listener);
            }
            snackbar.show();

            return snackbar;

        } catch (Exception exception) {
            LOGGER.throwing(BaseActivity.class.getSimpleName(), "showToast()", exception);
        }

        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CURRENT_FRAGMENT_ID, currentFragmentID);
    }

    /**
     * This function shows a {@link android.widget.Toast}.
     * <p/>
     * Toast will be visible for a limited period of time even if this function is called multiple
     * times.
     *
     * @param parentView is the parent view with respect to which {@link Snackbar} is shown.
     * @param message    is the {@link String} to be shown in toast message.
     * @param duration   {@see Snackbar.Duration}
     */
    protected void showToast(View parentView, String message, int duration) {
        showToast(parentView, message, null, null, duration);
    }

    /**
     * Returns the current loaded fragment id
     *
     * @return an {@link Integer} specifying current loaded fragment id, else returns -1
     */
    protected int getCurrentFragmentID() {
        return currentFragmentID;
    }
}

