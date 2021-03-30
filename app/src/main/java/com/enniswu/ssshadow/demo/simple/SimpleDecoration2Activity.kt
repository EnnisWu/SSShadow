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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.enniswu.ssshadow.demo.BaseViewHolder
import com.enniswu.ssshadow.demo.R
import com.enniswu.ssshadow.demo.databinding.ItemListBinding
import com.enniswu.ssshadow.demo.databinding.LayoutRecyclerViewBinding
import com.enniswu.ssshadow.graphics.ShadowParameter
import com.enniswu.ssshadow.graphics.Shape
import com.enniswu.ssshadow.widget.ShadowDecoration
import com.enniswu.ssshadow.widget.space.LinearBoundDecoration

class SimpleDecoration2Activity : AppCompatActivity(), ShadowDecoration.ItemShadow {

    private val dp5 by lazy { resources.getDimensionPixelSize(R.dimen.dp_5) }

    private val dp20 by lazy { resources.getDimensionPixelSize(R.dimen.dp_20) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = LayoutRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = Adapter()
        binding.recyclerView.addItemDecoration(LinearBoundDecoration(dp20, dp20, dp20, dp20, dp20))
        val shadowDecoration = ShadowDecoration(this)
        shadowDecoration.attachToRecyclerView(lifecycle, binding.recyclerView)
    }

    override fun getItemShadow(position: Int, parameter: ShadowParameter): Boolean {
        if (position == 0) {
            return false
        }
        if (position == 1) {
            parameter.setShadow(resources.getColor(R.color.color_9010a5ea), dp20, 0, dp5, Shape.RECT)
            return true
        }
        parameter.setShadow(resources.getColor(R.color.black_2f), dp20, 0, dp5, Shape.RECT)
        parameter.setExtension(-dp5, 0, -dp5, 0)
        return true
    }

    inner class Adapter: RecyclerView.Adapter<BaseViewHolder<ItemListBinding>>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                BaseViewHolder(ItemListBinding.inflate(LayoutInflater
                        .from(this@SimpleDecoration2Activity), parent, false))

        override fun onBindViewHolder(holder: BaseViewHolder<ItemListBinding>, position: Int) {
            holder.binding.textView.text = position.toString()
        }

        override fun getItemCount() = 20
    }
}