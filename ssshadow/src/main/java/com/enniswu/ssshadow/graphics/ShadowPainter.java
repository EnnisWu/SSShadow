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

import android.graphics.Canvas;

import androidx.annotation.NonNull;

/**
 * @author Ennis
 * @date 1/28/21
 */
public interface ShadowPainter {

    /**
     * 生命周期
     */
    void onAttach();

    /**
     * 生命周期
     */
    void onDetach();

    /**
     * 绘制阴影
     * @param canvas 画布
     * @param parameter 阴影参数
     * @param left 阴影左边缘距离画板的距离
     * @param top 阴影上边缘距离画板的距离
     * @param useCache 是否使用缓存
     */
    void drawShadow(@NonNull Canvas canvas, @NonNull ShadowParameter parameter, int left, int top,
                    boolean useCache);
}
