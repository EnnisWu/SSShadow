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

package com.enniswu.ssshadow.demo.decoration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.enniswu.ssshadow.demo.R
import java.util.*

/**
@author Ennis
@date 2/2/21
 */
class ShadowDecorationViewModel: ViewModel() {

    companion object {
        const val DEFAULT_ORIENTATION = RecyclerView.VERTICAL
        const val DEFAULT_REVERSE = false
    }

    private val _reverse = MutableLiveData(DEFAULT_REVERSE)
    val reverse: LiveData<Boolean> = _reverse

    private val _orientation = MutableLiveData(DEFAULT_ORIENTATION)
    val orientation: LiveData<Int> = _orientation

    private val _list: MutableList<Entity> = mutableListOf()
    val list: List<Entity> = _list

    private val _boundData = Bound()

    private val _bound = MutableLiveData(_boundData)
    val bound: LiveData<Bound> = _bound

    private val _layoutManager = MutableLiveData(0)
    val layoutManager: LiveData<Int> = _layoutManager

    init {
        for (i in 0..60) {
            _list.add(Entity(i.toString(), when (i % 3) {
                0 -> R.color.red_3f
                1 -> R.color.green_3f
                else -> R.color.blue_3f
            }, when(i % 11) {
                3 -> 2
                7 -> 3
                9 -> 4
                else -> 1
            }, when(i % 4) {
                2 -> 2
                3 -> 3
                else -> 1
            }))
        }
    }

    fun changeLayoutManager(@LayoutManager layoutManager: Int) {
        _boundData.mainAxisPaddingEnd = if (layoutManager != LayoutManager.STAGGERED_GRID) 0 else 10
        _bound.value = _bound.value
        _layoutManager.value = layoutManager
    }

    fun changeReverse(reverse: Boolean) {
        _reverse.value = reverse
    }

    fun changeOrientation(@RecyclerView.Orientation orientation: Int) {
        _orientation.value = orientation
    }

    fun setBound(left: Int = _boundData.left, top: Int = _boundData.top,
                 right: Int = _boundData.right, bottom: Int = _boundData.bottom,
                 inner: Int = _boundData.inner,
                 innerHorizontal: Int = _boundData.innerHorizontal,
                 innerVertical: Int = _boundData.innerVertical,
                 mainAxisPaddingEnd: Int = _boundData.mainAxisPaddingEnd) {
        _boundData.left = left
        _boundData.top = top
        _boundData.right = right
        _boundData.bottom = bottom
        _boundData.inner = inner
        _boundData.innerHorizontal = innerHorizontal
        _boundData.innerVertical = innerVertical
        _boundData.mainAxisPaddingEnd = mainAxisPaddingEnd
        _bound.value = _bound.value
    }

    fun swapData(from: Int, to: Int) = Collections.swap(_list, from, to)

    fun removeData(index: Int) = _list.removeAt(index)
}