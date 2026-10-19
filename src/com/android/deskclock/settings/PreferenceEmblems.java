/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.deskclock.settings;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;

import com.android.deskclock.R;
import com.android.deskclock.ThemeUtils;

/**
 * Puts preference icons inside Material 3 Expressive cookie emblems, cycling through the
 * primary, secondary and tertiary container colors like the Contacts settings do.
 */
public final class PreferenceEmblems {

    private static final int[][] COLORS = {
            {R.attr.colorPrimaryContainer, R.attr.colorOnPrimaryContainer},
            {R.attr.colorSecondaryContainer, R.attr.colorOnSecondaryContainer},
            {R.attr.colorTertiaryContainer, R.attr.colorOnTertiaryContainer},
    };

    private PreferenceEmblems() {
    }

    /** Wraps the icon of every preference in the group, recursively. */
    public static void apply(PreferenceGroup group) {
        apply(group, new int[] {0});
    }

    private static void apply(PreferenceGroup group, int[] index) {
        for (int i = 0; i < group.getPreferenceCount(); i++) {
            final Preference preference = group.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                apply((PreferenceGroup) preference, index);
            } else if (preference.getIcon() != null) {
                preference.setIcon(emblem(preference.getContext(), preference.getIcon(),
                        COLORS[index[0]++ % COLORS.length]));
            }
        }
    }

    private static Drawable emblem(Context context, Drawable icon, int[] colors) {
        final Resources res = context.getResources();
        final Drawable cookie = context.getDrawable(R.drawable.m3e_cookie).mutate();
        cookie.setTint(ThemeUtils.resolveColor(context, colors[0]));
        final Drawable glyph = icon.mutate();
        glyph.setTint(ThemeUtils.resolveColor(context, colors[1]));

        final LayerDrawable emblem = new LayerDrawable(new Drawable[] {cookie, glyph});
        final int size = res.getDimensionPixelSize(R.dimen.m3e_pref_emblem_size);
        final int inset = res.getDimensionPixelSize(R.dimen.m3e_pref_emblem_inset);
        emblem.setLayerSize(0, size, size);
        emblem.setLayerSize(1, size - 2 * inset, size - 2 * inset);
        emblem.setLayerInset(1, inset, inset, inset, inset);
        return emblem;
    }
}
