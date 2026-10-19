/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.deskclock;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/**
 * Material 3 Expressive motion: the emphasized easing curves and spring based spatial motion.
 */
public final class M3eMotion {

    /** Emphasized decelerate easing, for things entering the screen. */
    public static final Interpolator EMPHASIZED_DECELERATE =
            new PathInterpolator(0.05f, 0.7f, 0.1f, 1f);

    /** Emphasized accelerate easing, for things leaving the screen. */
    public static final Interpolator EMPHASIZED_ACCELERATE =
            new PathInterpolator(0.3f, 0f, 0.8f, 0.15f);

    /** Expressive fast spatial spring: quick, with a small overshoot. */
    public static final Interpolator SPRING_FAST = new SpringInterpolator(0.6f);

    /** Expressive default spatial spring: a gentle settle. */
    public static final Interpolator SPRING_DEFAULT = new SpringInterpolator(0.8f);

    public static final long DURATION_SHORT = 200;
    public static final long DURATION_MEDIUM = 350;
    public static final long DURATION_LONG = 500;

    private static final float PRESSED_SCALE = 0.92f;

    private M3eMotion() {
    }

    /**
     * Makes a view shrink while pressed and spring back on release. The listener never consumes
     * touches, so clicks keep working.
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void addPressSpring(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(PRESSED_SCALE).scaleY(PRESSED_SCALE)
                            .setInterpolator(EMPHASIZED_DECELERATE)
                            .setDuration(DURATION_SHORT).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f)
                            .setInterpolator(SPRING_FAST)
                            .setDuration(DURATION_LONG).start();
                    break;
            }
            return false;
        });
    }

    /** Fades a view in while it settles up into place, like the fade through transition. */
    public static void enter(View view) {
        view.setAlpha(0f);
        view.setTranslationY(view.getResources().getDimension(R.dimen.m3e_motion_offset));
        view.animate().alpha(1f).translationY(0f)
                .setInterpolator(EMPHASIZED_DECELERATE)
                .setDuration(DURATION_MEDIUM).start();
    }

    /** Spins an emblem a full turn with a small bounce, e.g. when its content changes. */
    public static void spin(View view) {
        view.animate().cancel();
        view.setRotation(-90f);
        view.setScaleX(0.6f);
        view.setScaleY(0.6f);
        view.animate().rotation(0f).scaleX(1f).scaleY(1f)
                .setInterpolator(SPRING_FAST)
                .setDuration(DURATION_LONG).start();
    }

    /**
     * Interpolates along an under-damped spring that settles at 1, approximating the expressive
     * motion scheme's spatial springs within a fixed duration.
     */
    public static final class SpringInterpolator implements Interpolator {
        private final float mDamping;
        private final float mOmega;
        private final float mDampedOmega;

        /** @param dampingRatio below 1 overshoots; lower values bounce more */
        public SpringInterpolator(float dampingRatio) {
            mDamping = dampingRatio;
            // Settle (to within ~0.1%) by the end of the animation.
            mOmega = 7f / dampingRatio;
            mDampedOmega = mOmega * (float) Math.sqrt(1 - dampingRatio * dampingRatio);
        }

        @Override
        public float getInterpolation(float t) {
            final double decay = Math.exp(-mDamping * mOmega * t);
            return (float) (1 - decay * (Math.cos(mDampedOmega * t)
                    + mDamping * mOmega / mDampedOmega * Math.sin(mDampedOmega * t)));
        }
    }
}
