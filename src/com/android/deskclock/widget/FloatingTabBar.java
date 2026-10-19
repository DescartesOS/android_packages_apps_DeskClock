/*
 * SPDX-FileCopyrightText: 2026 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.deskclock.widget;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.android.deskclock.M3eMotion;
import com.android.deskclock.R;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;

/**
 * Material 3 Expressive floating toolbar used as the tab bar: a pill holding one button per
 * tab. Unselected tabs show their icon; the selected tab grows into a pill with its label.
 */
public class FloatingTabBar extends LinearLayout {

    private final Menu mMenu;
    private NavigationBarView.OnItemSelectedListener mListener;
    private int mSelectedItemId = NO_ID;

    public FloatingTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        final LayoutTransition transition = new LayoutTransition();
        transition.enableTransitionType(LayoutTransition.CHANGING);
        transition.setInterpolator(LayoutTransition.CHANGING, M3eMotion.SPRING_DEFAULT);
        transition.setDuration(M3eMotion.DURATION_MEDIUM);
        setLayoutTransition(transition);

        // PopupMenu is just a convenient Menu host for inflating the menu resource.
        mMenu = new PopupMenu(context, this).getMenu();
        final TypedArray a = context.obtainStyledAttributes(attrs, new int[] {R.attr.menu});
        final int menuRes = a.getResourceId(0, 0);
        a.recycle();
        if (menuRes != 0) {
            new MenuInflater(context).inflate(menuRes, mMenu);
        }

        final LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < mMenu.size(); i++) {
            final MenuItem item = mMenu.getItem(i);
            final MaterialButton tab = (MaterialButton) inflater.inflate(
                    R.layout.floating_tab, this, false);
            tab.setId(item.getItemId());
            tab.setIcon(item.getIcon());
            tab.setContentDescription(item.getTitle());
            tab.setTooltipText(item.getTitle());
            tab.setOnClickListener(v -> select(item, true));
            addView(tab);
        }
    }

    public void setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener listener) {
        mListener = listener;
    }

    public int getSelectedItemId() {
        return mSelectedItemId;
    }

    /** Selects the tab with the given id, notifying the listener as a tap would. */
    public void setSelectedItemId(int itemId) {
        final MenuItem item = mMenu.findItem(itemId);
        if (item != null) {
            select(item, true);
        }
    }

    private void select(MenuItem item, boolean notify) {
        if (item.getItemId() == mSelectedItemId) {
            return;
        }
        if (notify && mListener != null && !mListener.onNavigationItemSelected(item)) {
            return;
        }
        mSelectedItemId = item.getItemId();
        for (int i = 0; i < mMenu.size(); i++) {
            final MenuItem each = mMenu.getItem(i);
            final MaterialButton tab = findViewById(each.getItemId());
            final boolean selected = each.getItemId() == mSelectedItemId;
            tab.setChecked(selected);
            tab.setSelected(selected);
            tab.setText(selected ? each.getTitle() : null);
        }
    }
}
