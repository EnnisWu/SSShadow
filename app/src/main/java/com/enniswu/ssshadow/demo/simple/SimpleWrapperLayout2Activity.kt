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

package com.enniswu.ssshadow.demo.simple

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.enniswu.ssshadow.demo.R
import com.enniswu.ssshadow.demo.databinding.ActivitySimpleWrapperLayout2Binding
import com.enniswu.ssshadow.graphics.ShadowParameter
import com.enniswu.ssshadow.graphics.Shape
import com.enniswu.ssshadow.widget.ShadowWrapperLayout

class SimpleWrapperLayout2Activity : AppCompatActivity(), ShadowWrapperLayout.ChildShadow {

    private val dp10 by lazy { resources.getDimensionPixelSize(R.dimen.dp_10) }

    private val dp20 by lazy { resources.getDimensionPixelSize(R.dimen.dp_20) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySimpleWrapperLayout2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.childShadow = this
    }

    override fun getChildShadow(child: View, parameter: ShadowParameter): Boolean {
        if (child.id == R.id.tv1) {
            return false
        }
        parameter.setShadow(resources.getColor(R.color.color_9010a5ea), dp20, 0, dp10, Shape.RECT)
        return true
    }
}