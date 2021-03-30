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

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * @author Ennis
 * @date 12/14/20
 */
public class ShadowResource {

    /**
     * 当前引用该阴影的个数
     */
    private int acquired;

    @NonNull
    private WeakReference<Bitmap> shadow;

    /**
     * 是否已被手动回收，手动回收后无法再使用
     */
    private boolean recycled;

    public ShadowResource(@NonNull Bitmap shadow) {
        this.shadow = new WeakReference<>(shadow);
    }

    /**
     * Bitmap 被回收后重新添加
     * @param shadow 阴影的 Bitmap
     */
    public void resume(@NonNull Bitmap shadow) {
        checkRecycled();
        this.shadow = new WeakReference<>(shadow);
    }

    /**
     * 增加引用数
     */
    public void increase() {
        checkRecycled();
        if (acquired < 0) {
            acquired = 0;
        }
        ++acquired;
    }

    /**
     * 减少引用数
     */
    public void reduce() {
        checkRecycled();
        --acquired;
    }

    /**
     * 当前资源是否可以被回收
     * @return 可被回收 {@code true}
     */
    public boolean isRecyclable() {
        return acquired <= 0 && !isRecycled();
    }

    public boolean isRecycled() {
        return recycled;
    }

    public void recycle() {
        checkRecycled();
        recycled = true;
        if (shadow.get() != null) {
            shadow.get().recycle();
        }
    }

    @Nullable
    public Bitmap getShadow() {
        checkRecycled();
        return shadow.get();
    }

    private void checkRecycled() {
        if (isRecycled()) {
            throw new RuntimeException("ShadowResource已被回收");
        }
    }
}
