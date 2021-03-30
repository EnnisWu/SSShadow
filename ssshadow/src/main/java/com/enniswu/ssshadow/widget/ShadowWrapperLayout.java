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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import com.enniswu.ssshadow.Configuration;
import com.enniswu.ssshadow.R;
import com.enniswu.ssshadow.graphics.ShadowPainter;
import com.enniswu.ssshadow.graphics.ShadowPainterImpl;
import com.enniswu.ssshadow.graphics.ShadowParameter;
import com.enniswu.ssshadow.graphics.Shape;

import java.util.Collections;

/**
 * @author Ennis
 * @date 12/8/20
 */
public class ShadowWrapperLayout extends FrameLayout {

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

    /**
     * 当前待绘制阴影的 View 距离 ShadowWrapperLayout 的距离
     */
    private int relativeX;

    /**
     * 当前待绘制阴影的 View 距离 ShadowWrapperLayout 的距离
     */
    private int relativeY;

    @Nullable
    private ChildShadow childShadow;

    @NonNull
    private final ShadowParameter parameter;

    @NonNull
    private final ShadowPainter shadowPainter;

    private final SparseArray<View> needDrawShadow;

    private String ids;

    public ShadowWrapperLayout(@NonNull Context context) {
        this(context, (ShadowPainter) null);
    }

    public ShadowWrapperLayout(@NonNull Context context, @Nullable ShadowPainter shadowPainter) {
        super(context);
        setWillNotDraw(false);
        this.parameter = new ShadowParameter();
        this.shadowPainter = shadowPainter == null ? new ShadowPainterImpl() : shadowPainter;
        this.needDrawShadow = new SparseArray<>();
    }

    public ShadowWrapperLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        this.parameter = new ShadowParameter();
        this.shadowPainter = new ShadowPainterImpl();
        this.needDrawShadow = new SparseArray<>();

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowWrapperLayout);
        color = ta.getColor(R.styleable.ShadowWrapperLayout_shadow_color,
                getContext().getResources().getColor(R.color.default_shadow_color));
        radius = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_radius, 0);
        offsetX = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_offsetX, 0);
        offsetY = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_offsetY, 0);

        extensionLeft = extensionTop = extensionRight = extensionBottom
                = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_extension, 0);

        int p = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_extensionHorizontal, 0);
        if (p != 0) {
            extensionLeft = extensionRight = p;
        }
        p = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_extensionVertical, 0);
        if (p != 0) {
            extensionTop = extensionBottom = p;
        }

        p = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_extensionLeft, 0);
        extensionLeft = p == 0 ? extensionLeft : p;
        p = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_extensionTop, 0);
        extensionTop = p == 0 ? extensionTop : p;
        p = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_extensionRight, 0);
        extensionRight = p == 0 ? extensionRight : p;
        p = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_extensionBottom, 0);
        extensionBottom = p == 0 ? extensionBottom : p;

        shape = ta.getInt(R.styleable.ShadowWrapperLayout_shadow_shape, 0);
        rectRadius = ta.getDimensionPixelSize(R.styleable.ShadowWrapperLayout_shadow_rectRadius, 0);
        ids = ta.getString(R.styleable.ShadowWrapperLayout_shadow_referencedIds);
        lightSourceRotation = ta.getBoolean(R.styleable.ShadowWrapperLayout_shadow_lightSourceRotation, false);

        ta.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        parseIds(ids);
        shadowPainter.onAttach();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        shadowPainter.onDetach();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < needDrawShadow.size(); ++i) {
            int id = needDrawShadow.keyAt(i);
            View child = needDrawShadow.get(id);
            if (child == null) {
                child = findViewById(id);

                if (child == null) {
                    continue;
                } else {
                    needDrawShadow.put(id, child);
                }
            }

            parameter.set(color, radius, offsetX, offsetY, extensionLeft, extensionTop,
                    extensionRight, extensionBottom, 0, 0, shape, rectRadius, lightSourceRotation);
            if (childShadow != null && !childShadow.getChildShadow(child, parameter)) {
                continue;
            }

            drawShadow(canvas, child, parameter);
        }
        super.onDraw(canvas);
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        needDrawShadow.remove(child.getId());
    }

    public void animation() {
        invalidate();
    }

    public void addNeedShadowView(int... ids) {
        for (int id : ids) {
            View child = findViewById(id);
            if (child != null) {
                needDrawShadow.put(child.getId(), child);
            }
        }
    }

    public void addNeedShadowView(View... children) {
        for (View child : children) {
            if (child != null && child.getId() != NO_ID) {
                needDrawShadow.put(child.getId(), child);
            }
        }
    }

    public void removeNeedShadowView(int... ids) {
        for (int id : ids) {
            View child = findViewById(id);
            if (child != null) {
                needDrawShadow.remove(child.getId());
            }
        }
    }

    public void removeNeedShadowView(View... children) {
        for (View child : children) {
            if (child != null && child.getId() != NO_ID) {
                needDrawShadow.remove(child.getId());
            }
        }
    }

    private void drawShadow(@NonNull Canvas c, @NonNull View v, @NonNull ShadowParameter parameter) {
        parameter.setTargetWidth(Math.round(v.getWidth() * v.getScaleX()));
        parameter.setTargetHeight(Math.round(v.getHeight() * v.getScaleY()));
        if (parameter.getWidth() <= 0 || parameter.getHeight() <= 0) {
            return;
        }

        //除了这里计算 X，Y 的距离，其他和 ShadowDecoration 相同
        calculatePoint(v);

        float transformOffsetX = (v.getScaleX() - 1) * (v.getWidth() / 2f - v.getPivotX());
        float transformOffsetY = (v.getScaleY() - 1) * (v.getHeight() / 2f - v.getPivotY());

        float offsetX = (parameter.getExtensionRight() - parameter.getExtensionLeft()) / 2f
                + transformOffsetX;
        float offsetY = (parameter.getExtensionBottom() - parameter.getExtensionTop()) / 2f
                + transformOffsetY;
        if (parameter.isLightSourceRotation()) {
            offsetX += parameter.getOffsetX();
            offsetY += parameter.getOffsetY();
        }

        float left = relativeX + v.getWidth() / 2f - parameter.getWidth() / 2f + offsetX;
        float top = relativeY + v.getHeight() / 2f - parameter.getHeight() / 2f + offsetY;

        if (!parameter.isLightSourceRotation()) {
            c.save();
            c.translate(parameter.getOffsetX(), parameter.getOffsetY());
        }
        float rotation = v.getRotation() % 360;
        if (rotation != 0) {
            c.save();
            c.rotate(rotation, relativeX + v.getPivotX(), relativeY + v.getPivotY());
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

    private void parseIds(String ids) {
        String[] stringIds = ids.split(",");
        ArraySet<String> idSet = new ArraySet<>(stringIds.length);
        Collections.addAll(idSet, stringIds);
        traverseFindId(idSet, this);
    }

    private void traverseFindId(ArraySet<String> idSet, ViewGroup group) {
        for (int i = 0, len = group.getChildCount(); i < len; ++i) {
            View child = group.getChildAt(i);
            findId(idSet, child);
            if (child instanceof ViewGroup) {
                traverseFindId(idSet, (ViewGroup) child);
            }
        }
    }

    private void findId(ArraySet<String> idSet, View view) {
        int id = view.getId();
        if (id <= 0) {
            return;
        }
        String stringId = getResources().getResourceEntryName(id);
        if (idSet.contains(stringId)) {
            needDrawShadow.put(id, null);
        }
    }

    /**
     * 计算子 View 相对于 ShadowWrapperLayout 的距离
     * @param v 子 View
     */
    private void calculatePoint(View v) {
        float totalX = v.getX();
        float totalY = v.getY();
        v = (View) v.getParent();
        while (v != this) {
            totalX += v.getX();
            totalY += v.getY();
            v = (View) v.getParent();
        }
        relativeX = Math.round(totalX);
        relativeY = Math.round(totalY);
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
        invalidate();
    }

    public void setExtension(int extensionLeft, int extensionTop, int extensionRight,
                             int extensionBottom) {
        this.extensionLeft = extensionLeft;
        this.extensionTop = extensionTop;
        this.extensionRight = extensionRight;
        this.extensionBottom = extensionBottom;
        invalidate();
    }

    public void setChildShadow(@Nullable ChildShadow childShadow) {
        this.childShadow = childShadow;
        invalidate();
    }

    @Nullable
    public ChildShadow getChildShadow() {
        return childShadow;
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

    @Shape
    public int getShape() {
        return shape;
    }

    public int getRectRadius() {
        return rectRadius;
    }

    public interface ChildShadow {

        boolean getChildShadow(@NonNull View child, @NonNull ShadowParameter parameter);

    }
}
