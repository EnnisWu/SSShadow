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

package com.enniswu.ssshadow.demo.layout

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.ArrayMap
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.enniswu.ssshadow.demo.databinding.ActivityShadowWrapperLayoutBinding

class ShadowWrapperLayoutActivity : AppCompatActivity(){

    private val binding by lazy { ActivityShadowWrapperLayoutBinding.inflate(layoutInflater) }

    private val mapping = ArrayMap<View, Pair<Float, Float>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val animator = AnimatorSet().apply {
            playTogether(ValueAnimator.ofFloat(1f, 2f, 1f).apply {
                interpolator = FastOutSlowInInterpolator()
                addUpdateListener {
                    val value = it.animatedValue as Float
                    binding.tv5.scaleX = value
                    binding.tv5.scaleY = value
                    binding.shadowWrapperLayout.animation()
                }
            }, ObjectAnimator.ofFloat(binding.tv5, "rotation", 0f, 360f, 0f))
            duration = 3000
        }

        binding.tv1.setOnTouchListener(this::drag)
        binding.tv2.setOnTouchListener(this::drag)
        binding.tv3.setOnTouchListener(this::drag)
        binding.tv4.setOnTouchListener(this::drag)
        binding.tv5.setOnClickListener {
            animator.start()
        }
    }

    private fun drag(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mapping[v] = Pair(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                v.translationX += event.x - (mapping[v]?.first ?: 0f)
                v.translationY += event.y - (mapping[v]?.second ?: 0f)
                binding.shadowWrapperLayout.animation()
            }
        }
        return true
    }
}