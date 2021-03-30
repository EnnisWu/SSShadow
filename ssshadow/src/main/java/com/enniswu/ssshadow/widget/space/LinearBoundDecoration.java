/*
 * Copyright 2021 Ennis Wu. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.enniswu.ssshadow.widget.space;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Ennis
 * @date 2020/8/30
 *
 * 在 RecyclerView 的项之间开辟间距，适用于 LayoutManager 是 LinearLayoutManager 的情况
 */
public class LinearBoundDecoration extends RecyclerView.ItemDecoration {

    private int left;
    private int top;
    private int right;
    private int bottom;
    private int inner;
    private int halfInner;

    public LinearBoundDecoration() {
    }

    public LinearBoundDecoration(int left, int top, int right, int bottom, int inner) {
        setBound(left, top, right, bottom, inner);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }

        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        boolean isReverse = linearLayoutManager.getReverseLayout();
        boolean isFirst = position == 0;
        boolean isLast = position == itemCount - 1;

        if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            outRect.set(left, isFirst && !isReverse || isLast && isReverse ? top : halfInner,
                    right, isFirst && isReverse || isLast && !isReverse ? bottom : halfInner);
        } else {
            outRect.set(isFirst && !isReverse || isLast && isReverse ? left : halfInner, top ,
                    isFirst && isReverse || isLast && !isReverse ? right : halfInner, bottom);
        }
    }

    public void setBound(int left, int top, int right, int bottom, int inner) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.inner = inner;
        this.halfInner = inner / 2;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getInner() {
        return inner;
    }
}
