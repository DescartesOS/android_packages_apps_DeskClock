/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.deskclock.settings;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroupAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.android.deskclock.R;
import com.android.deskclock.ThemeUtils;

/**
 * Shapes the rows of each preference category as one Material 3 Expressive group: tonal
 * pills with large outer corners and small inner corners, separated by a thin gap. Each row
 * gets its own rounded background, so the press ripple follows the shape too.
 */
public final class GroupedPreferenceDecoration extends RecyclerView.ItemDecoration {

    private final int mColor;
    private final ColorStateList mRippleColor;
    private final float mOuterRadius;
    private final float mInnerRadius;
    private final int mGap;

    public GroupedPreferenceDecoration(Context context) {
        mColor = ThemeUtils.resolveColor(context, R.attr.colorSurfaceContainer);
        mRippleColor = ColorStateList.valueOf(
                ThemeUtils.resolveColor(context, android.R.attr.colorControlHighlight));
        mOuterRadius = context.getResources().getDimension(R.dimen.m3e_group_outer_radius);
        mInnerRadius = context.getResources().getDimension(R.dimen.m3e_group_inner_radius);
        mGap = context.getResources().getDimensionPixelSize(R.dimen.m3e_group_gap);
    }

    /** Adds the decoration to the list of a preference fragment. */
    public static void install(RecyclerView list) {
        final int margin = list.getResources().getDimensionPixelSize(R.dimen.m3e_screen_margin);
        list.setPaddingRelative(margin, list.getPaddingTop(), margin, margin);
        list.setClipToPadding(false);
        list.addItemDecoration(new GroupedPreferenceDecoration(list.getContext()));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
            @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        if (!isRow(parent, position)) {
            return;
        }
        outRect.set(0, mGap / 2, 0, mGap - mGap / 2);

        final float top = isRow(parent, position - 1) ? mInnerRadius : mOuterRadius;
        final float bottom = isRow(parent, position + 1) ? mInnerRadius : mOuterRadius;
        final GradientDrawable shape = new GradientDrawable();
        shape.setColor(mColor);
        shape.setCornerRadii(new float[] {top, top, top, top, bottom, bottom, bottom, bottom});
        view.setBackground(new RippleDrawable(mRippleColor, shape, null));
    }

    /** Whether the adapter position holds a preference row (not a category title or hero). */
    private static boolean isRow(RecyclerView parent, int position) {
        if (!(parent.getAdapter() instanceof PreferenceGroupAdapter) || position < 0
                || position >= parent.getAdapter().getItemCount()) {
            return false;
        }
        final Preference preference =
                ((PreferenceGroupAdapter) parent.getAdapter()).getItem(position);
        return preference != null && !(preference instanceof PreferenceCategory)
                && preference.getLayoutResource() != R.layout.m3e_settings_hero;
    }
}
