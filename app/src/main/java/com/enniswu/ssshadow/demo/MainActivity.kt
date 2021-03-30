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

package com.enniswu.ssshadow.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.enniswu.ssshadow.demo.databinding.ActivityMainBinding
import com.enniswu.ssshadow.demo.decoration.ShadowDecorationActivity
import com.enniswu.ssshadow.demo.layer.ShadowLayerActivity
import com.enniswu.ssshadow.demo.layout.ShadowWrapperLayoutActivity
import com.enniswu.ssshadow.demo.simple.*

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvSimpleShadowLayer.setOnClickListener {
            startActivity(Intent (this, SimpleLayerActivity::class.java))
        }
        binding.tvSimpleShadowDecoration.setOnClickListener {
            startActivity(Intent (this, SimpleDecorationActivity::class.java))
        }
        binding.tvSimpleShadowDecoration2.setOnClickListener {
            startActivity(Intent (this, SimpleDecoration2Activity::class.java))
        }
        binding.tvSimpleShadowWrapperLayout.setOnClickListener {
            startActivity(Intent (this, SimpleWrapperLayoutActivity::class.java))
        }
        binding.tvSimpleShadowWrapperLayout2.setOnClickListener {
            startActivity(Intent (this, SimpleWrapperLayout2Activity::class.java))
        }

        binding.tvShadowLayer.setOnClickListener {
            startActivity(Intent (this, ShadowLayerActivity::class.java))
        }
        binding.tvShadowDecoration.setOnClickListener {
            startActivity(Intent (this, ShadowDecorationActivity::class.java))
        }
        binding.tvShadowWrapperLayout.setOnClickListener {
            startActivity(Intent (this, ShadowWrapperLayoutActivity::class.java))
        }
    }

}