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

package com.enniswu.ssshadow.graphics;

/**
 * @author Ennis
 * @date 11/18/20
 *
 * 阴影参数
 */
public final class ShadowParameter implements Cloneable {

    private int color;

    private int radius;

    private int offsetX;

    private int offsetY;

    private int extensionLeft;

    private int extensionTop;

    private int extensionRight;

    private int extensionBottom;

    private int width;

    private int height;

    @Shape
    private int shape;

    private int rectRadius;

    /**
     * 光源是否旋转
     */
    private boolean lightSourceRotation;

    public ShadowParameter() {
    }

    public ShadowParameter(int color, int radius, int offsetX, int offsetY, int extensionLeft,
                           int extensionTop, int extensionRight, int extensionBottom, int width,
                           int height, @Shape int shape, int rectRadius,
                           boolean lightSourceRotation) {
        this.color = color;
        this.radius = radius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.extensionLeft = extensionLeft;
        this.extensionTop = extensionTop;
        this.extensionRight = extensionRight;
        this.extensionBottom = extensionBottom;
        this.width = width;
        this.height = height;
        this.shape = shape;
        this.rectRadius = rectRadius;
        this.lightSourceRotation = lightSourceRotation;
    }

    public void set(int color, int radius, int offsetX, int offsetY, int extensionLeft,
                    int extensionTop, int extensionRight, int extensionBottom,
                    int width, int height, @Shape int shape, int rectRadius,
                    boolean lightSourceRotation) {
        this.color = color;
        this.radius = radius;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.extensionLeft = extensionLeft;
        this.extensionTop = extensionTop;
        this.extensionRight = extensionRight;
        this.extensionBottom = extensionBottom;
        this.width = width;
        this.height = height;
        this.shape = shape;
        this.rectRadius = rectRadius;
        this.lightSourceRotation = lightSourceRotation;
    }

    public void setEmpty() {
        set(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, Shape.RECT, 0, false);
    }

    public void setShadow(int color, int radius, int offsetX, int offsetY, @Shape int shape) {
        set(color, radius, offsetX, offsetY, extensionLeft, extensionTop, extensionRight,
                extensionBottom, width, height, shape, rectRadius, lightSourceRotation);
    }

    public void setShadow(int color, int radius, int offsetX, int offsetY, @Shape int shape,
                          int rectRadius) {
        set(color, radius, offsetX, offsetY, extensionLeft, extensionTop, extensionRight,
                extensionBottom, width, height, shape, rectRadius, lightSourceRotation);
    }

    public void setExtension(int extensionLeft, int extensionTop, int extensionRight,
                             int extensionBottom) {
        set(color, radius, offsetX, offsetY, extensionLeft, extensionTop, extensionRight,
                extensionBottom, width, height, shape, rectRadius, lightSourceRotation);
    }

    public int getSideShadowRange() {
        return radius * 2;
    }

    public void setScale(float scaleX, float scaleY) {
        offsetX = Math.round(scaleX * offsetX);
        offsetY = Math.round(scaleY * offsetY);
        extensionLeft = Math.round(scaleX * extensionLeft);
        extensionTop = Math.round(scaleY * extensionTop);
        extensionRight = Math.round(scaleX * extensionRight);
        extensionBottom = Math.round(scaleY * extensionBottom);
    }

    public void setTargetWidth(int width) {
        setWidth(width + getSideShadowRange() * 2 + extensionLeft + extensionRight);
    }

    public void setTargetHeight(int height) {
        setHeight(height + getSideShadowRange() * 2 + extensionTop + extensionBottom);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShadowParameter that = (ShadowParameter) o;
        return color == that.color &&
                radius == that.radius &&
                offsetX == that.offsetX &&
                offsetY == that.offsetY &&
                extensionLeft == that.extensionLeft &&
                extensionTop == that.extensionTop &&
                extensionRight == that.extensionRight &&
                extensionBottom == that.extensionBottom &&
                width == that.width &&
                height == that.height &&
                shape == that.shape &&
                rectRadius == that.rectRadius;
    }

    @Override
    public int hashCode() {
        return color + radius + offsetX + offsetY + extensionLeft + extensionTop + extensionRight
                + extensionBottom + width + height + shape + rectRadius + 1;
    }

    @Override
    public String toString() {
        return "ShadowParameter{" +
                "color=" + color +
                ", radius=" + radius +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", extensionLeft=" + extensionLeft +
                ", extensionTop=" + extensionTop +
                ", extensionRight=" + extensionRight +
                ", extensionBottom=" + extensionBottom +
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

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getExtensionLeft() {
        return extensionLeft;
    }

    public void setExtensionLeft(int extensionLeft) {
        this.extensionLeft = extensionLeft;
    }

    public int getExtensionTop() {
        return extensionTop;
    }

    public void setExtensionTop(int extensionTop) {
        this.extensionTop = extensionTop;
    }

    public int getExtensionRight() {
        return extensionRight;
    }

    public void setExtensionRight(int extensionRight) {
        this.extensionRight = extensionRight;
    }

    public int getExtensionBottom() {
        return extensionBottom;
    }

    public void setExtensionBottom(int extensionBottom) {
        this.extensionBottom = extensionBottom;
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

    @Shape
    public int getShape() {
        return shape;
    }

    public void setShape(@Shape int shape) {
        this.shape = shape;
    }

    public int getRectRadius() {
        return rectRadius;
    }

    public void setRectRadius(int rectRadius) {
        this.rectRadius = rectRadius;
    }

    public boolean isLightSourceRotation() {
        return lightSourceRotation;
    }

    public void setLightSourceRotation(boolean lightSourceRotation) {
        this.lightSourceRotation = lightSourceRotation;
    }
}
