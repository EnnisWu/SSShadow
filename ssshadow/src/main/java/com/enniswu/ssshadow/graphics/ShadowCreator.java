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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;

/**
 * @author Ennis
 * @date 11/18/20
 *
 * 真正绘制阴影的类
 */
public class ShadowCreator {

    @NonNull
    private final Canvas canvas;

    @NonNull
    private final Paint shadowPaint;

    @NonNull
    private final RectF shadowRect;

    public ShadowCreator() {
        canvas = new Canvas();
        shadowPaint = new Paint();
        shadowRect = new RectF();
    }

    /**
     * 创建一个 Bitmap 将阴影绘制在这个 Bitmap 上并返回
     * @param color 颜色
     * @param radius 模糊半径
     * @param width 整个 Bitmap 的宽（包括阴影的范围）
     * @param height 整个 Bitmap 的高（包括阴影的范围）
     * @param shape 形状
     * @param rectRadius 圆角半径（只在形状为圆角矩形时有效）
     * @param shadowRange 阴影的范围
     * @return 绘制的阴影
     */
    @NonNull
    public Bitmap createShadow(int color, int radius, int width, int height, @Shape int shape,
                               int rectRadius, int shadowRange) {
        Bitmap shadowBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(shadowBitmap);

        drawShadow(color, radius, 0, 0, width, height, shape, rectRadius, shadowRange,
                canvas, 0, 0);

        return shadowBitmap;
    }

    /**
     * 真正绘制阴影的方法
     * @param color 颜色
     * @param radius 模糊半径
     * @param offsetX 阴影的 X 轴偏移量
     * @param offsetY 阴影的 Y 轴偏移量
     * @param width 整个 Bitmap 的宽（包括阴影的范围）
     * @param height 整个 Bitmap 的高（包括阴影的范围）
     * @param shape 形状
     * @param rectRadius 圆角半径（只在形状为圆角矩形时有效）
     * @param shadowRange 阴影的范围
     * @param canvas 画板
     * @param left 阴影左边缘距离画板的距离
     * @param top 阴影上边缘距离画板的距离
     */
    public void drawShadow(int color, int radius, int offsetX, int offsetY, int width, int height,
                           @Shape int shape, int rectRadius, int shadowRange, @NonNull Canvas canvas,
                           int left, int top) {
        //绘制的边框要剪掉阴影的范围，实际绘制的时候是不算阴影的位置的
        shadowRect.set(left + shadowRange, top + shadowRange,
                left + width - shadowRange, top + height - shadowRange);

        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.TRANSPARENT);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setShadowLayer(radius, offsetX, offsetY, color);

        if (shape == Shape.RECT) {
            canvas.drawRect(shadowRect, shadowPaint);
        } else if (shape == Shape.OVAL) {
            canvas.drawOval(shadowRect, shadowPaint);
        } else if (shape == Shape.ROUND_RECT) {
            canvas.drawRoundRect(shadowRect, rectRadius, rectRadius, shadowPaint);
        }
    }
}
