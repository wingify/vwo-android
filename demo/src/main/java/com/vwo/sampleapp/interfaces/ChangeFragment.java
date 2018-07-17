package com.vwo.sampleapp.interfaces;

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


import android.os.Bundle;

import androidx.annotation.Nullable;

/**
 * <p> This interface is basically used for controlling different fragments from a central place
 * such as from an {@link android.app.Activity} or another {@link android.app.Fragment}
 * Controlling {@link android.app.Fragment} or {@link android.app.Activity}
 * need to implement this interface</p>
 */
public interface ChangeFragment {
    /**
     * <b> This function is used to load a particular {@link android.app.Fragment} from the
     * controlling {@link android.app.Activity} or {@link android.app.Fragment} </b>
     *
     * @param bundle     is the data to be passed to {@link android.app.Fragment}
     * @param fragmentId is the id that identifies, which {@link android.app.Fragment} is to be loaded
     * @param tag        is the tag that is attached to {@link android.app.Fragment} which is to be loaded
     */
    void loadFragment(@Nullable Bundle bundle, int fragmentId, @Nullable String tag);
}
