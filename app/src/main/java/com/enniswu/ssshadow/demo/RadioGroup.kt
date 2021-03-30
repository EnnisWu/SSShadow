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

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.CompoundButton
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*

/**
@author Ennis
@date 3/17/21
 */
class RadioGroup: ConstraintHelper {

    var listenerOnCheckedChange: ((radioGroup: RadioGroup, button: CompoundButton) -> Unit)? = null

    private val buttons = ArrayList<CompoundButton>()

    private var current: View? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun updatePreLayout(container: ConstraintLayout) {
        super.updatePreLayout(container)
        mIds.forEach { id ->
            val view = container.getViewById(id) as? CompoundButton ?: return@forEach
            buttons.add(view)
            view.setOnClickListener { v ->
                if (v === current) {
                    return@setOnClickListener
                }
                current = v
                buttons.forEach { button ->
                    if (v === button) {
                        button.isChecked = true
                        listenerOnCheckedChange?.invoke(this, button)
                    } else {
                        button.isChecked = false
                    }
                }
            }
        }
    }

    fun setChecked(button: CompoundButton) {
        post {
            buttons.forEach {
                it.isChecked = it === button
            }
        }
    }
}