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

package com.enniswu.ssshadow.widget;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.enniswu.ssshadow.Configuration;
import com.enniswu.ssshadow.graphics.ShadowPainter;
import com.enniswu.ssshadow.graphics.ShadowPainterImpl;
import com.enniswu.ssshadow.graphics.ShadowParameter;
import com.enniswu.ssshadow.graphics.Shape;

/**
 * @author Ennis
 * @date 11/19/20
 */
public class ShadowDecoration extends RecyclerView.ItemDecoration implements LifecycleEventObserver {

    /**
     * ItemDecoration 无法感知 RecyclerView 的生命周期从而无法准确控制阴影的回收
     * 通过添加 Lifecycle 感知对应 Lifecycle 的生命周期，阴影回收推迟到 Lifecycle 销毁时
     */
    @Nullable
    private Lifecycle lifecycle;

    @Nullable
    private RecyclerView recyclerView;

    private int color;

    private int radius;

    private int offsetX;

    private int offsetY;

    private int extensionLeft;

    private int extensionTop;

    private int extensionRight;

    private int extensionBottom;

    @Shape
    private int shape;

    private int rectRadius;

    private boolean lightSourceRotation;

    @Nullable
    private ItemShadow itemShadow;

    @NonNull
    private final ShadowParameter parameter;

    @NonNull
    private final ShadowPainter shadowPainter;

    public ShadowDecoration(int color, int radius, int offsetX, int offsetY, int extensionLeft,
                            int extensionTop, int extensionRight, int extensionBottom,
                            @Shape int shape, int rectRadius) {
        this(color, radius, offsetX, offsetY, extensionLeft, extensionTop,
                extensionRight, extensionBottom, shape, rectRadius, null, null);
    }

    public ShadowDecoration(int color, int radius, int offsetX, int offsetY, int extensionLeft,
                            int extensionTop, int extensionRight, int extensionBottom,
                            @Shape int shape) {
        this(color, radius, offsetX, offsetY, extensionLeft, extensionTop,
                extensionRight, extensionBottom, shape, 0, null, null);
    }

    public ShadowDecoration(@NonNull ItemShadow itemShadow) {
        this(0, 0, 0, 0, 0, 0, 0, 0, Shape.RECT, 0, itemShadow, null);
    }

    public ShadowDecoration(int color, int radius, int offsetX, int offsetY, int extensionLeft,
                            int extensionTop, int extensionRight, int extensionBottom,
                            @Shape int shape, int rectRadius, @Nullable ItemShadow itemShadow,
                            @Nullable ShadowPainter shadowPainter) {
        this.color = color;
        this.radius = radius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.extensionLeft = extensionLeft;
        this.extensionTop = extensionTop;
        this.extensionRight = extensionRight;
        this.extensionBottom = extensionBottom;
        this.shape = shape;
        this.rectRadius = rectRadius;
        this.itemShadow = itemShadow;
        this.parameter = new ShadowParameter();
        this.shadowPainter = shadowPainter == null ? new ShadowPainterImpl() : shadowPainter;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent,
                       @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (lifecycle == null) {
            return;
        }
        for (int i = 0, len = parent.getChildCount(); i < len; ++i) {

            final View v = parent.getChildAt(i);
            final int position = parent.getChildAdapterPosition(v);
            if (position < 0 || position >= state.getItemCount()) {
                continue;
            }

            parameter.set(color, radius, offsetX, offsetY, extensionLeft, extensionTop,
                    extensionRight, extensionBottom, 0, 0, shape, rectRadius, lightSourceRotation);
            if (itemShadow != null && !itemShadow.getItemShadow(position, parameter)) {
                continue;
            }

            drawShadow(c, v, parameter);
        }
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            onDetach();
        }
    }

    public void attachToRecyclerView(@NonNull Lifecycle lifecycle,
                                     @NonNull RecyclerView recyclerView) {
        if (this.recyclerView != recyclerView) {
            this.recyclerView = recyclerView;
            recyclerView.addItemDecoration(this);
        }
        if (this.lifecycle == lifecycle) {
            return;
        }
        onDetach();
        onAttach(lifecycle);
    }

    public void animation() {
        recyclerView.invalidate();
    }

    protected void onAttach(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        lifecycle.addObserver(this);
        shadowPainter.onAttach();
    }

    protected void onDetach() {
        if (lifecycle != null) {
            lifecycle.removeObserver(this);
            lifecycle = null;
            shadowPainter.onDetach();
        }
    }

    private void drawShadow(@NonNull Canvas c, @NonNull View v, @NonNull ShadowParameter parameter) {
        parameter.setTargetWidth(Math.round(v.getWidth() * v.getScaleX()));
        parameter.setTargetHeight(Math.round(v.getHeight() * v.getScaleY()));
        if (parameter.getWidth() <= 0 || parameter.getHeight() <= 0) {
            return;
        }

        //计算放大后的偏移量
        float transformOffsetX = (v.getScaleX() - 1) * (v.getWidth() / 2f - v.getPivotX());
        float transformOffsetY = (v.getScaleY() - 1) * (v.getHeight() / 2f - v.getPivotY());

        float offsetX = (parameter.getExtensionRight() - parameter.getExtensionLeft()) / 2f
                + transformOffsetX;
        float offsetY = (parameter.getExtensionBottom() - parameter.getExtensionTop()) / 2f
                + transformOffsetY;
        if (parameter.isLightSourceRotation()) {
            //光源旋转时，直接加上阴影的偏移量
            offsetX += parameter.getOffsetX();
            offsetY += parameter.getOffsetY();
        }

        float left = v.getX() + v.getWidth() / 2f - parameter.getWidth() / 2f + offsetX;
        float top = v.getY() + v.getHeight() / 2f - parameter.getHeight() / 2f + offsetY;

        if (!parameter.isLightSourceRotation()) {
            //光源不旋转时，位移画布制造偏移量
            c.save();
            c.translate(parameter.getOffsetX(), parameter.getOffsetY());
        }
        float rotation = v.getRotation() % 360;
        if (rotation != 0) {
            c.save();
            c.rotate(rotation, v.getX() + v.getPivotX(), v.getY() + v.getPivotY());
        }
        boolean scaled = v.getScaleX() != 1 || v.getScaleY() != 1;
        boolean useCache = scaled && Configuration.animationCacheFlag || Configuration.cacheFlag;
        shadowPainter.drawShadow(c, parameter, Math.round(left), Math.round(top), useCache);
        if (rotation != 0) {
            c.restore();
        }
        if (!parameter.isLightSourceRotation()) {
            c.restore();
        }
    }

    public void setShadow(int color, int radius, int offsetX, int offsetY, @Shape int shape) {
        setShadow(color, radius, offsetX, offsetY, shape, 0);
    }

    public void setShadow(int color, int radius, int offsetX, int offsetY, @Shape int shape,
                          int rectRadius) {
        this.color = color;
        this.radius = radius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.shape = shape;
        this.rectRadius = rectRadius;
        recyclerView.invalidate();
    }

    public void setExtension(int extensionLeft, int extensionTop, int extensionRight,
                             int extensionBottom) {
        this.extensionLeft = extensionLeft;
        this.extensionTop = extensionTop;
        this.extensionRight = extensionRight;
        this.extensionBottom = extensionBottom;
        recyclerView.invalidate();
    }

    public void setLightSourceRotation(boolean lightSourceRotation) {
        this.lightSourceRotation = lightSourceRotation;
    }

    public void setItemShadow(@Nullable ItemShadow itemShadow) {
        this.itemShadow = itemShadow;
    }

    public int getColor() {
        return color;
    }

    public int getRadius() {
        return radius;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int getExtensionLeft() {
        return extensionLeft;
    }

    public int getExtensionTop() {
        return extensionTop;
    }

    public int getExtensionRight() {
        return extensionRight;
    }

    public int getExtensionBottom() {
        return extensionBottom;
    }

    public int getShape() {
        return shape;
    }

    public int getRectRadius() {
        return rectRadius;
    }

    public boolean isLightSourceRotation() {
        return lightSourceRotation;
    }

    @Nullable
    public ItemShadow getItemShadow() {
        return itemShadow;
    }

    public interface ItemShadow {

        boolean getItemShadow(int position, @NonNull ShadowParameter parameter);

    }
}
