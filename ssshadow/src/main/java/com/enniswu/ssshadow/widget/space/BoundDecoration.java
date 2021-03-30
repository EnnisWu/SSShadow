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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @author Ennis
 * @date 2/3/21
 *
 * 包装类
 */
public class BoundDecoration extends RecyclerView.ItemDecoration {

    private final LinearBoundDecoration linearBoundDecoration;

    private final GridBoundDecoration gridBoundDecoration;

    private final StaggeredGridBoundDecoration staggeredGridBoundDecoration;

    public BoundDecoration() {
        linearBoundDecoration = new LinearBoundDecoration();
        gridBoundDecoration = new GridBoundDecoration();
        staggeredGridBoundDecoration = new StaggeredGridBoundDecoration();
    }

    public BoundDecoration(int left, int top, int right, int bottom, int inner, int innerVertical,
                           int innerHorizontal) {
        this();
        setBound(left, top, right, bottom, inner, innerVertical, innerHorizontal);
    }

    public BoundDecoration(int left, int top, int right, int bottom, int inner) {
        this(left, top, right, bottom, inner, 0, 0);
    }

    public BoundDecoration(int left, int top, int right, int bottom, int innerVertical,
                           int innerHorizontal) {
        this(left, top, right, bottom, 0, innerVertical, innerHorizontal);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            staggeredGridBoundDecoration.getItemOffsets(outRect, view, parent, state);
        } else if (layoutManager instanceof GridLayoutManager) {
            gridBoundDecoration.getItemOffsets(outRect, view, parent, state);
        } else if (layoutManager instanceof LinearLayoutManager) {
            linearBoundDecoration.getItemOffsets(outRect, view, parent, state);
        }
    }

    public void setBound(int left, int top, int right, int bottom, int inner, int innerVertical,
                         int innerHorizontal) {
        linearBoundDecoration.setBound(left, top, right, bottom, inner);
        gridBoundDecoration.setBound(left, top, right, bottom, innerVertical, innerHorizontal);
        staggeredGridBoundDecoration.setBound(left, top, right, bottom, innerVertical, innerHorizontal);
    }

    public void setBound(int left, int top, int right, int bottom, int inner) {
        linearBoundDecoration.setBound(left, top, right, bottom, inner);
        gridBoundDecoration.setBound(left, top, right, bottom,
                gridBoundDecoration.getInnerVertical(), gridBoundDecoration.getInnerHorizontal());
        staggeredGridBoundDecoration.setBound(left, top, right, bottom,
                staggeredGridBoundDecoration.getInnerVertical(),
                staggeredGridBoundDecoration.getInnerHorizontal());
    }

    public void setBound(int left, int top, int right, int bottom, int innerVertical,
                         int innerHorizontal) {
        linearBoundDecoration.setBound(left, top, right, bottom, linearBoundDecoration.getInner());
        gridBoundDecoration.setBound(left, top, right, bottom, innerVertical, innerHorizontal);
        staggeredGridBoundDecoration.setBound(left, top, right, bottom, innerVertical, innerHorizontal);
    }
}
