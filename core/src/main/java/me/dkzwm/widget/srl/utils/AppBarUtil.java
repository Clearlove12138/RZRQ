/*
 * MIT License
 *
 * Copyright (c) 2017 dkzwm
 * Copyright (c) 2015 liaohuqiu.net
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.dkzwm.widget.srl.utils;

import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;
import me.dkzwm.widget.srl.ILifecycleObserver;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;

/**
 * Created by dkzwm on 2017/12/18.
 *
 * @author dkzwm
 */
public class AppBarUtil
        implements ILifecycleObserver,
                SmoothRefreshLayout.OnHeaderEdgeDetectCallBack,
                SmoothRefreshLayout.OnFooterEdgeDetectCallBack {
    private boolean mFullyExpanded;
    private boolean mFullCollapsed;
    private boolean mFound = false;
    private AppBarLayout.OnOffsetChangedListener mListener;

    @Override
    public void onAttached(SmoothRefreshLayout layout) {
        try {
            AppBarLayout appBarLayout = findAppBarLayout(layout);
            if (appBarLayout == null) return;
            if (mListener == null) {
                mListener =
                        new AppBarLayout.OnOffsetChangedListener() {
                            @Override
                            public void onOffsetChanged(
                                    AppBarLayout appBarLayout, int verticalOffset) {
                                mFullyExpanded = verticalOffset >= 0;
                                mFullCollapsed =
                                        appBarLayout.getTotalScrollRange() + verticalOffset <= 0;
                            }
                        };
            }
            appBarLayout.addOnOffsetChangedListener(mListener);
            mFound = true;
        } catch (Exception e) {
            // ignored
            mFound = false;
        }
    }

    @Override
    public void onDetached(SmoothRefreshLayout layout) {
        try {
            AppBarLayout appBarLayout = findAppBarLayout(layout);
            if (appBarLayout == null) return;
            if (mListener != null) appBarLayout.removeOnOffsetChangedListener(mListener);
        } catch (Exception e) {
            // ignored
        }
        mFound = false;
    }

    private AppBarLayout findAppBarLayout(ViewGroup group) {
        AppBarLayout bar = null;
        final int count = group.getChildCount();
        for (int i = 0; i < count; i++) {
            final View view = group.getChildAt(i);
            if (view instanceof CoordinatorLayout) {
                CoordinatorLayout layout = (CoordinatorLayout) view;
                final int subCount = layout.getChildCount();
                for (int j = 0; j < subCount; j++) {
                    final View subView = layout.getChildAt(j);
                    if (subView instanceof AppBarLayout) {
                        bar = (AppBarLayout) subView;
                        break;
                    }
                }
            }
        }
        return bar;
    }

    @Override
    public boolean isNotYetInEdgeCannotMoveHeader(
            SmoothRefreshLayout parent, @Nullable View child, @Nullable IRefreshView header) {
        View targetView = parent.getScrollTargetView();
        if (targetView == null) targetView = child;
        return !mFullyExpanded || ScrollCompat.canChildScrollUp(targetView);
    }

    @Override
    public boolean isNotYetInEdgeCannotMoveFooter(
            SmoothRefreshLayout parent, @Nullable View child, @Nullable IRefreshView footer) {
        View targetView = parent.getScrollTargetView();
        if (targetView == null) targetView = child;
        return !mFullCollapsed || ScrollCompat.canChildScrollDown(targetView);
    }

    public boolean hasFound() {
        return mFound;
    }
}
