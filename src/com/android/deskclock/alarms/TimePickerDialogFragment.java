/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.deskclock.alarms;

import android.text.format.DateFormat;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.deskclock.R;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

/**
 * Shows the Material 3 time picker, a dial with a keyboard entry mode, for setting alarms.
 */
public final class TimePickerDialogFragment {

    /**
     * Tag for timer picker fragment in FragmentManager.
     */
    private static final String TAG = "TimePickerDialogFragment";

    private TimePickerDialogFragment() {
    }

    public static void show(Fragment fragment) {
        show(fragment, -1 /* hour */, -1 /* minute */);
    }

    public static void show(Fragment parentFragment, int hourOfDay, int minute) {
        if (!(parentFragment instanceof OnTimeSetListener)) {
            throw new IllegalArgumentException("Fragment must implement OnTimeSetListener");
        }

        final FragmentManager manager = parentFragment.getChildFragmentManager();
        if (manager.isDestroyed()) {
            return;
        }

        // Make sure the dialog isn't already added.
        removeTimeEditDialog(manager);

        final Calendar now = Calendar.getInstance();
        final MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(DateFormat.is24HourFormat(parentFragment.requireContext())
                        ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H)
                .setHour(hourOfDay >= 0 && hourOfDay < 24
                        ? hourOfDay : now.get(Calendar.HOUR_OF_DAY))
                .setMinute(minute >= 0 && minute < 60 ? minute : now.get(Calendar.MINUTE))
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setTheme(R.style.ThemeOverlay_DeskClock_TimePicker)
                .build();
        attachListener(picker, (OnTimeSetListener) parentFragment);
        picker.show(manager, TAG);
    }

    private static void attachListener(MaterialTimePicker picker, OnTimeSetListener listener) {
        picker.addOnPositiveButtonClickListener(
                v -> listener.onTimeSet(picker.getHour(), picker.getMinute()));
    }

    public static void removeTimeEditDialog(FragmentManager manager) {
        if (manager != null) {
            final Fragment prev = manager.findFragmentByTag(TAG);
            if (prev != null) {
                manager.beginTransaction().remove(prev).commit();
            }
        }
    }

    /**
     * The callback interface used to indicate the user is done filling in the time (e.g. they
     * clicked on the 'OK' button).
     */
    public interface OnTimeSetListener {
        /**
         * Called when the user is done setting a new time and the dialog has closed.
         * @param hourOfDay the hour that was set
         * @param minute    the minute that was set
         */
        void onTimeSet(int hourOfDay, int minute);
    }
}
