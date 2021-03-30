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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.enniswu.ssshadow.Configuration;
import com.enniswu.ssshadow.R;
import com.enniswu.ssshadow.graphics.ShadowPainter;
import com.enniswu.ssshadow.graphics.ShadowPainterImpl;
import com.enniswu.ssshadow.graphics.ShadowParameter;
import com.enniswu.ssshadow.graphics.Shape;

/**
 * @author Ennis
 * @date 2020/11/10
 */
public class ShadowLayer extends ConstraintHelper {

    @NonNull
    private final ShadowParameter parameter;

    @NonNull
    private final ShadowPainter shadowPainter;

    private boolean scaled;

    public ShadowLayer(Context context) {
        this(context, (ShadowPainter) null);
    }

    public ShadowLayer(Context context, @Nullable ShadowPainter shadowPainter) {
        super(context);
        this.parameter = new ShadowParameter();
        this.shadowPainter = shadowPainter == null ? new ShadowPainterImpl() : shadowPainter;
    }

    public ShadowLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.parameter = new ShadowParameter();
        this.shadowPainter = new ShadowPainterImpl();
        initAttr(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        shadowPainter.onAttach();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        shadowPainter.onDetach();
    }

    private void initAttr(@NonNull AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayer);
        parameter.setColor(ta.getColor(R.styleable.ShadowLayer_shadow_color,
                getContext().getResources().getColor(R.color.default_shadow_color)));
        parameter.setRadius(ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_radius, 0));
        parameter.setOffsetX(ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_offsetX, 0));
        parameter.setOffsetY(ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_offsetY, 0));

        if (ta.hasValue(R.styleable.ShadowLayer_shadow_extension)) {
            int e = ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_extension, 0);
            parameter.setExtension(e, e, e, e);
        }
        if (ta.hasValue(R.styleable.ShadowLayer_shadow_extensionHorizontal)) {
            int e = ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_extensionHorizontal, 0);
            parameter.setExtensionLeft(e);
            parameter.setExtensionRight(e);
        }
        if (ta.hasValue(R.styleable.ShadowLayer_shadow_extensionVertical)) {
            int e = ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_extensionVertical, 0);
            parameter.setExtensionTop(e);
            parameter.setExtensionBottom(e);
        }
        if (ta.hasValue(R.styleable.ShadowLayer_shadow_extensionLeft)) {
            int e = ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_extensionLeft, -1);
            parameter.setExtensionLeft(e);
        }
        if (ta.hasValue(R.styleable.ShadowLayer_shadow_extensionTop)) {
            int e = ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_extensionTop, -1);
            parameter.setExtensionTop(e);
        }
        if (ta.hasValue(R.styleable.ShadowLayer_shadow_extensionRight)) {
            int e = ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_extensionRight, -1);
            parameter.setExtensionRight(e);
        }
        if (ta.hasValue(R.styleable.ShadowLayer_shadow_extensionBottom)) {
            int e = ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_extensionBottom, -1);
            parameter.setExtensionBottom(e);
        }

        parameter.setShape(ta.getInt(R.styleable.ShadowLayer_shadow_shape, 0));
        parameter.setRectRadius(ta.getDimensionPixelSize(R.styleable.ShadowLayer_shadow_rectRadius, 0));
        parameter.setLightSourceRotation(ta.getBoolean(R.styleable.ShadowLayer_shadow_lightSourceRotation, false));

        ta.recycle();

    }

    @Override
    public void onDraw(Canvas canvas) {
        if (parameter.getWidth() <= 0 || parameter.getHeight() <= 0) {
            return;
        }
        boolean useCache = scaled && Configuration.animationCacheFlag || Configuration.cacheFlag;
        shadowPainter.drawShadow(canvas, parameter, 0, 0, useCache);
    }

    @Override
    public void updatePostLayout(ConstraintLayout container) {
        super.updatePostLayout(container);
        View view;
        if (container != null && mIds.length > 1 && (view = container.getViewById(mIds[0])) != null) {
            scaled = view.getScaleX() != 1 || view.getScaleY() != 1;
            setRotation(view.getRotation());
            setTranslationX(view.getTranslationX());
            setTranslationY(view.getTranslationY());

            parameter.setTargetWidth(Math.round(view.getWidth() * view.getScaleX()));
            parameter.setTargetHeight(Math.round(view.getHeight() * view.getScaleY()));

            float pivotOffsetX = view.getWidth() / 2f - view.getPivotX();
            float pivotOffsetY = view.getHeight() / 2f - view.getPivotY();

            //计算旋转和放大后的偏移量
            double radians = Math.toRadians(view.getRotation());
            float sin = (float) Math.sin(radians);
            float cos = (float) Math.cos(radians);
            float transformOffsetX = view.getScaleX() * cos * pivotOffsetX
                    - view.getScaleY() * sin * pivotOffsetY - pivotOffsetX;
            float transformOffsetY = view.getScaleX() * sin * pivotOffsetX
                    + view.getScaleY() * cos * pivotOffsetY - pivotOffsetY;

            //计算总偏移量
            float offsetX = (parameter.getExtensionRight() - parameter.getExtensionLeft()) / 2f
                    + transformOffsetX;
            float offsetY = (parameter.getExtensionBottom() - parameter.getExtensionTop()) / 2f
                    + transformOffsetY;
            if (parameter.isLightSourceRotation()) {
                //光源旋转时，计算对应的 X 轴，Y 轴偏移量
                offsetY += sin * parameter.getOffsetX() + cos * parameter.getOffsetY();
                offsetX += cos * parameter.getOffsetX() - sin * parameter.getOffsetY();
            } else {
                //光源不旋转时，直接加上阴影的偏移量
                offsetX += parameter.getOffsetX();
                offsetY += parameter.getOffsetY();
            }

            float left = view.getLeft() + view.getWidth() / 2f - parameter.getWidth() / 2f + offsetX;
            float top = view.getTop() + view.getHeight() / 2f - parameter.getHeight() / 2f + offsetY;
            float right = left + parameter.getWidth();
            float bottom = top + parameter.getHeight();
            layout(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
        }
    }

    public void animation() {
        requestLayout();
    }

    public void setShadow(int color, int radius, int offsetX, int offsetY, @Shape int shape) {
        setShadow(color, radius, offsetX, offsetY, shape, 0);
    }

    public void setShadow(int color, int radius, int offsetX, int offsetY, @Shape int shape,
                          int rectRadius) {
        parameter.setShadow(color, radius, offsetX, offsetY, shape, rectRadius);
        requestLayout();
    }

    public void setExtension(int extensionLeft, int extensionTop, int extensionRight,
                             int extensionBottom) {
        parameter.setExtension(extensionLeft, extensionTop, extensionRight, extensionBottom);
        requestLayout();
    }

    public int getColor() {
        return parameter.getColor();
    }

    public int getRadius() {
        return parameter.getRadius();
    }

    public int getOffsetX() {
        return parameter.getOffsetX();
    }

    public int getOffsetY() {
        return parameter.getOffsetY();
    }

    public int getExtensionLeft() {
        return parameter.getExtensionLeft();
    }

    public int getExtensionTop() {
        return parameter.getExtensionTop();
    }

    public int getExtensionRight() {
        return parameter.getExtensionRight();
    }

    public int getExtensionBottom() {
        return parameter.getExtensionBottom();
    }

    public int getShape() {
        return parameter.getShape();
    }
}
