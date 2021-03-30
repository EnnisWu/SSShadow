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
import androidx.collection.ArrayMap;

import com.enniswu.ssshadow.Configuration;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Ennis
 * @date 12/21/20
 *
 * 阴影缓存池
 */
public class ShadowCachePool {

    @NonNull
    private final Map<ShadowKey, ShadowResource> shadowCachePool;

    public static ShadowCachePool getDefaultInstance() {
        return DefaultInstanceHolder.INSTANCE;
    }

    public ShadowCachePool() {
        this(Configuration.initialCachePoolSize);
    }

    public ShadowCachePool(int initialCachePoolSize) {
        shadowCachePool = new ArrayMap<>(initialCachePoolSize);
    }

    private static final class DefaultInstanceHolder {
        private static final ShadowCachePool INSTANCE = new ShadowCachePool();
    }

    public void put(ShadowKey key, ShadowResource resource) {
        shadowCachePool.put(key, resource);
    }

    public ShadowResource get(ShadowKey key) {
        return shadowCachePool.get(key);
    }

    /**
     * 回收所有可被回收的资源
     */
    public void tryRecycle() {
        Iterator<Map.Entry<ShadowKey, ShadowResource>> iterator
                = shadowCachePool.entrySet().iterator();
        Map.Entry<ShadowKey, ShadowResource> entry;
        while (iterator.hasNext()) {
            entry = iterator.next();
            ShadowResource value = entry.getValue();
            if (value.isRecyclable()) {
                value.recycle();
                iterator.remove();
            }
        }
    }
}
