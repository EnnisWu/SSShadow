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

package com.enniswu.ssshadow.cache;

import androidx.annotation.NonNull;

import com.enniswu.ssshadow.graphics.ShadowParameter;
import com.enniswu.ssshadow.graphics.Shape;

/**
 * @author Ennis
 * @date 1/21/21
 *
 * 阴影资源的关键参数，不同于 {@link ShadowParameter}，阴影参数不同但最终绘制出来的样式可能是相同的。
 * 这个类标识了不同的阴影样式
 */
public final class ShadowKey implements Cloneable {

    private int color;

    private int radius;

    private int width;

    private int height;

    @Shape
    private int shape;

    private int rectRadius;

    public ShadowKey() {
    }

    public ShadowKey(int color, int radius, int width, int height, int shape, int rectRadius) {
        this.color = color;
        this.radius = radius;
        this.width = width;
        this.height = height;
        this.shape = shape;
        this.rectRadius = rectRadius;
    }

    public ShadowKey convert(ShadowParameter parameter) {
        setColor(parameter.getColor());
        setRadius(parameter.getRadius());
        setWidth(parameter.getWidth());
        setHeight(parameter.getHeight());
        setShape(parameter.getShape());
        setRectRadius(parameter.getRectRadius());
        return this;
    }

    public boolean equals(int color, int radius, int width, int height, @Shape int shape,
                          int rectRadius) {
        boolean equals = color == this.color &&
                radius == this.radius &&
                width == this.width &&
                height == this.height &&
                shape == this.shape;
        //只有形状是圆角矩形时圆角半径才有效
        if (shape == this.shape && shape == Shape.ROUND_RECT) {
            equals = (equals && rectRadius == this.rectRadius);
        }
        return equals;
    }

    public ShadowKey copy() {
        return new ShadowKey(color, radius, width, height, shape, rectRadius);
    }

    @NonNull
    @Override
    public ShadowKey clone() {
        try {
            return (ShadowKey) super.clone();
        } catch (CloneNotSupportedException ignore) {
            return copy();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShadowKey that = (ShadowKey) o;
        return equals(that.color, that.radius, that.width, that.height, that.shape, that.rectRadius);
    }

    @Override
    public int hashCode() {
        int hash = color + radius + width + height + shape + 1;
        if (shape == Shape.ROUND_RECT) {
            hash += rectRadius;
        }
        return hash;
    }

    @Override
    public String toString() {
        return "ShadowKey{" +
                "color=" + color +
                ", radius=" + radius +
                ", width=" + width +
                ", height=" + height +
                ", shape=" + shape +
                ", rectRadius=" + rectRadius +
                '}';
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public int getRectRadius() {
        return rectRadius;
    }

    public void setRectRadius(int rectRadius) {
        this.rectRadius = rectRadius;
    }
}
