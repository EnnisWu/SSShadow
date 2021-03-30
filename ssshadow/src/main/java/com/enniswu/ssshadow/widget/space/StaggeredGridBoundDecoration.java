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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @author Ennis
 * @date 12/29/20
 *
 * 在 RecyclerView 的项之间开辟间距，适用于 LayoutManager 是 StaggeredGridLayoutManager 的情况
 */
public class StaggeredGridBoundDecoration extends RecyclerView.ItemDecoration {

    private int left;
    private int top;
    private int right;
    private int bottom;
    private int innerVertical;
    private int innerHorizontal;

    public StaggeredGridBoundDecoration() {
    }

    public StaggeredGridBoundDecoration(int left, int top, int right, int bottom, int innerVertical,
                                        int innerHorizontal) {
        setBound(left, top, right, bottom, innerVertical, innerHorizontal);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (!(layoutManager instanceof StaggeredGridLayoutManager)) {
            return;
        }

        //获取所有需要的参数
        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();
        int spanCount = staggeredGridLayoutManager.getSpanCount();
        int spanIndex = lp.getSpanIndex();
        int orientation = staggeredGridLayoutManager.getOrientation();

        if (itemCount <= 0 || spanCount <= 0) {
            return;
        }

        //主轴方向开始边界，主轴方向中间边界
        //正向布局时主轴方向无法判断底部最低的那个，所以没有主轴方向的结尾边界
        //逆向布局时主轴方向无法判断顶部最高的那个，所以没有主轴方向的开始边界
        int mainAxisStartBound, mainAxisEndBound, mainAxisInnerBound,
                //副轴方向开始边界，副轴方向结尾边界，副轴方向中间边界
                crossAxisStartBound, crossAxisEndBound, crossAxisInnerBound;
        //根据方向初始化主轴副轴所有边界
        if (orientation == StaggeredGridLayoutManager.VERTICAL) {
            mainAxisStartBound = top;
            mainAxisEndBound = bottom;
            mainAxisInnerBound = innerHorizontal;
            crossAxisStartBound = left;
            crossAxisEndBound = right;
            crossAxisInnerBound = innerVertical;
        } else {
            mainAxisStartBound = left;
            mainAxisEndBound = right;
            mainAxisInnerBound = innerVertical;
            crossAxisStartBound = top;
            crossAxisEndBound = bottom;
            crossAxisInnerBound = innerHorizontal;
        }

        //该项副轴方向开始+结束边界
        int itemCrossAxisBound = (int) ((crossAxisStartBound + crossAxisEndBound +
                crossAxisInnerBound * (spanCount - 1)) / (float) spanCount);

        //该项项副轴方向开始边界
        int itemCrossAxisStartBound = crossAxisStartBound(spanCount, spanIndex, itemCrossAxisBound,
                crossAxisStartBound, crossAxisEndBound, crossAxisInnerBound);

        //该项副轴方向结尾边界
        int itemCrossAxisEndBound = isCrossAxisEnd(spanCount, spanIndex)
                ? crossAxisEndBound : crossAxisEndBound(spanCount, spanIndex,
                itemCrossAxisBound, crossAxisStartBound, crossAxisEndBound, crossAxisInnerBound);

        boolean isReverse = staggeredGridLayoutManager.getReverseLayout();
        boolean isMainAxisStart = isMainAxisStart(position, spanCount);

        //该项主轴方向开始边界
        int itemMainAxisStartBound = isMainAxisStart && !isReverse
                ? mainAxisStartBound : mainAxisInnerBound / 2;

        //该项主轴方向结尾边界
        int itemMainAxisEndBound = isMainAxisStart && isReverse
                ? mainAxisEndBound : mainAxisInnerBound / 2;

        //坐边界，上边界，右边界，下边界
        int itemLeftBound, itemTopBound, itemRightBound, itemBottomBound;
        //将主副轴所有边界根据方向转化成上下左右边界
        if (orientation == StaggeredGridLayoutManager.VERTICAL) {
            itemLeftBound = itemCrossAxisStartBound;
            itemTopBound = itemMainAxisStartBound;
            itemRightBound = itemCrossAxisEndBound;
            itemBottomBound = itemMainAxisEndBound;
        } else {
            itemLeftBound = itemMainAxisStartBound;
            itemTopBound = itemCrossAxisStartBound;
            itemRightBound = itemMainAxisEndBound;
            itemBottomBound = itemCrossAxisEndBound;
        }

        outRect.set(itemLeftBound, itemTopBound, itemRightBound, itemBottomBound);
    }

    private int crossAxisStartBound(int spanCount, int spanIndex, int itemCrossAxisBound,
                                    int crossAxisStartBound, int crossAxisEndBound,
                                    int crossAxisInnerBound) {
        if (isCrossAxisStart(spanIndex)) {
            return crossAxisStartBound;
        } else if (spanIndex >= spanCount / 2) {
            return itemCrossAxisBound - crossAxisEndBound(spanCount, spanIndex, itemCrossAxisBound,
                    crossAxisStartBound, crossAxisEndBound, crossAxisInnerBound);
        } else {
            return crossAxisInnerBound - crossAxisEndBound(spanCount, spanIndex - 1,
                    itemCrossAxisBound, crossAxisStartBound, crossAxisEndBound, crossAxisInnerBound);
        }
    }

    private int crossAxisEndBound(int spanCount, int spanIndex, int itemCrossAxisBound,
                                  int crossAxisStartBound, int crossAxisEndBound,
                                  int crossAxisInnerBound) {
        if (isCrossAxisEnd(spanCount, spanIndex)) {
            return crossAxisEndBound;
        } else if (spanIndex >= spanCount / 2) {
            return crossAxisInnerBound
                    - crossAxisStartBound(spanCount, spanIndex + 1, itemCrossAxisBound,
                    crossAxisStartBound, crossAxisEndBound, crossAxisInnerBound);
        } else {
            return itemCrossAxisBound - crossAxisStartBound(spanCount, spanIndex, itemCrossAxisBound,
                    crossAxisStartBound, crossAxisEndBound, crossAxisInnerBound);
        }
    }

    private boolean isMainAxisStart(int position, int spanCount) {
        return position < spanCount;
    }

    private boolean isCrossAxisStart(int spanIndex) {
        return spanIndex == 0;
    }

    private boolean isCrossAxisEnd(int spanCount, int spanIndex) {
        return spanIndex == spanCount - 1;
    }

    public void setBound(int left, int top, int right, int bottom, int innerVertical,
                         int innerHorizontal) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.innerVertical = innerVertical;
        this.innerHorizontal = innerHorizontal;
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

    public int getInnerVertical() {
        return innerVertical;
    }

    public int getInnerHorizontal() {
        return innerHorizontal;
    }
}
