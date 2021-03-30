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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import com.enniswu.ssshadow.cache.ShadowCachePool;
import com.enniswu.ssshadow.cache.ShadowKey;
import com.enniswu.ssshadow.cache.ShadowResource;

/**
 * @author Ennis
 * @date 12/14/20
 *
 * 协调阴影的创建 {@link ShadowCreator} 和回收复用 {@link ShadowCachePool}
 * View 只需要引用 ShadowPainter 即可，不需要关心具体的创建和回收复用的流程
 */
public class ShadowPainterImpl implements ShadowPainter {

    @NonNull
    private final ShadowCreator shadowCreator;

    @NonNull
    private final ShadowCachePool shadowCachePool;

    @NonNull
    private final ArraySet<ShadowKey> keyCache;

    @NonNull
    private final ShadowKey shadowKey;

    public ShadowPainterImpl() {
        this(null, null);
    }

    public ShadowPainterImpl(@Nullable ShadowCreator shadowCreator,
                             @Nullable ShadowCachePool shadowCachePool) {
        this.shadowCreator = shadowCreator == null ? new ShadowCreator() : shadowCreator;
        this.shadowCachePool = shadowCachePool == null
                ? ShadowCachePool.getDefaultInstance() : shadowCachePool;
        this.keyCache = new ArraySet<>();
        this.shadowKey = new ShadowKey();
    }

    @Override
    public void onAttach() {
    }

    @Override
    public void onDetach() {
        recycle();
    }

    /**
     * 绘制阴影，当不使用缓存时直接绘制在画布上，使用缓存时先获取缓存，将缓存的 Bitmap 绘制在画布上
     * @param canvas 画布
     * @param parameter 阴影参数
     * @param left 阴影左边缘距离画板的距离
     * @param top 阴影上边缘距离画板的距离
     * @param useCache 是否使用缓存
     */
    @Override
    public void drawShadow(@NonNull Canvas canvas, @NonNull ShadowParameter parameter,
                           int left, int top, boolean useCache) {
        if (!useCache) {
            shadowCreator.drawShadow(parameter.getColor(), parameter.getRadius(),
                    0, 0, parameter.getWidth(), parameter.getHeight(), parameter.getShape(),
                    parameter.getRectRadius(), parameter.getSideShadowRange(), canvas, left, top);
            return;
        }
        Bitmap shadow = acquireShadow(parameter);
        canvas.drawBitmap(shadow, left, top, null);
    }

    /**
     * 获取阴影 Bitmap
     * @param parameter 阴影参数
     * @return 阴影 Bitmap
     */
    @NonNull
    private Bitmap acquireShadow(@NonNull ShadowParameter parameter) {
        ShadowResource shadowResource = shadowCachePool.get(shadowKey.convert(parameter));
        if (shadowResource == null) {
            //没有缓存，创建阴影 Bitmap
            return createShadow(parameter);
        } else if (shadowResource.getShadow() == null) {
            //有缓存，但因为内存不足已被回收，重建阴影 Bitmap
            return recreateShadow(parameter, shadowResource);
        } else {
            //有缓存
            return shadowResource.getShadow();
        }
    }

    @NonNull
    private Bitmap createShadow(@NonNull ShadowParameter parameter) {
        Bitmap shadowBitmap = shadowCreator.createShadow(parameter.getColor(),
                parameter.getRadius(), parameter.getWidth(), parameter.getHeight(),
                parameter.getShape(), parameter.getRectRadius(), parameter.getSideShadowRange());
        ShadowResource shadowResource = new ShadowResource(shadowBitmap);
        shadowResource.increase();
        ShadowKey shadowKey = this.shadowKey.convert(parameter).clone();
        shadowCachePool.put(shadowKey, shadowResource);
        keyCache.add(shadowKey);
        return shadowBitmap;
    }

    @NonNull
    private Bitmap recreateShadow(@NonNull ShadowParameter parameter,
                                  @NonNull ShadowResource shadowResource) {
        Bitmap shadowBitmap = shadowCreator.createShadow(parameter.getColor(),
                parameter.getRadius(), parameter.getWidth(), parameter.getHeight(),
                parameter.getShape(), parameter.getRectRadius(), parameter.getSideShadowRange());
        shadowResource.resume(shadowBitmap);
        keyCache.add(shadowKey.convert(parameter).clone());
        return shadowBitmap;
    }

    private void recycle() {
        clearCache();
        shadowCachePool.tryRecycle();
    }

    private void clearCache() {
        for (ShadowKey key : keyCache) {
            ShadowResource shadowResource = shadowCachePool.get(key);
            shadowResource.reduce();
        }
        keyCache.clear();
    }
}
