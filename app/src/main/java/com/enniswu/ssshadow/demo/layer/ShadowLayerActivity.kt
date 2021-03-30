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

package com.enniswu.ssshadow.demo.layer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.enniswu.ssshadow.demo.R
import com.enniswu.ssshadow.demo.databinding.ActivityShadowLayerBinding

class ShadowLayerActivity : AppCompatActivity() {

    private var reverse = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityShadowLayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val constraintSet1 = ConstraintSet().apply { clone(binding.root) }
        val constraintSet2 = ConstraintSet().apply {
            clone(this@ShadowLayerActivity, R.layout.activity_shadow_layer2)
        }

        binding.tvClick.setOnClickListener {
            TransitionManager.beginDelayedTransition(binding.root)
            if (reverse) {
                constraintSet1
            } else {
                constraintSet2
            }.applyTo(binding.root)
            reverse = !reverse
        }

    }
}