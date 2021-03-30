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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.enniswu.ssshadow.demo.databinding.LayoutBoundPanelBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
@author Ennis
@date 2/2/21
 */
class BoundPanelFragment: BottomSheetDialogFragment() {

    private val shadowDecorationViewModel: ShadowDecorationViewModel by lazy { ViewModelProvider(requireActivity()).get(ShadowDecorationViewModel::class.java) }

    private lateinit var binding: LayoutBoundPanelBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = LayoutBoundPanelBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rgManager.setChecked(when(shadowDecorationViewModel.layoutManager.value) {
            LayoutManager.GRID -> binding.tvGrid
            LayoutManager.STAGGERED_GRID -> binding.tvStaggeredGrid
            else -> binding.tvLinear
        })
        binding.rgOrientation.setChecked(if (shadowDecorationViewModel.orientation.value == RecyclerView.VERTICAL)
            binding.tvVertical else binding.tvHorizontal)
        binding.switchReverse.isChecked = shadowDecorationViewModel.reverse.value == true

        shadowDecorationViewModel.bound.observe(this) {
            binding.sBoundLeft.value = it.left.toFloat()
            binding.sBoundTop.value = it.top.toFloat()
            binding.sBoundRight.value = it.right.toFloat()
            binding.sBoundBottom.value = it.bottom.toFloat()
            binding.sBoundInner.value = it.inner.toFloat()
            binding.sBoundInnerHorizontal.value = it.innerHorizontal.toFloat()
            binding.sBoundInnerVertical.value = it.innerVertical.toFloat()
            binding.sMainAxisPaddingEnd.value = it.mainAxisPaddingEnd.toFloat()
        }
        shadowDecorationViewModel.layoutManager.observe(this) {
            if (it < LayoutManager.STAGGERED_GRID) {
                binding.tvMainAxisPaddingEnd.visibility = View.GONE
                binding.sMainAxisPaddingEnd.visibility = View.GONE
            } else {
                binding.tvMainAxisPaddingEnd.visibility = View.VISIBLE
                binding.sMainAxisPaddingEnd.visibility = View.VISIBLE
            }
            if (it < LayoutManager.GRID) {
                binding.tvBoundInnerHorizontal.visibility = View.GONE
                binding.sBoundInnerHorizontal.visibility = View.GONE
                binding.tvBoundInnerVertical.visibility = View.GONE
                binding.sBoundInnerVertical.visibility = View.GONE
            } else {
                binding.tvBoundInnerHorizontal.visibility = View.VISIBLE
                binding.sBoundInnerHorizontal.visibility = View.VISIBLE
                binding.tvBoundInnerVertical.visibility = View.VISIBLE
                binding.sBoundInnerVertical.visibility = View.VISIBLE
            }
            if (it == LayoutManager.LINEAR) {
                binding.tvBoundInner.visibility = View.VISIBLE
                binding.sBoundInner.visibility = View.VISIBLE
            } else {
                binding.tvBoundInner.visibility = View.GONE
                binding.sBoundInner.visibility = View.GONE
            }
        }


        binding.rgManager.listenerOnCheckedChange = { _, button ->
            when (button) {
                binding.tvLinear -> shadowDecorationViewModel.changeLayoutManager(LayoutManager.LINEAR)
                binding.tvGrid -> shadowDecorationViewModel.changeLayoutManager(LayoutManager.GRID)
                binding.tvStaggeredGrid -> shadowDecorationViewModel.changeLayoutManager(LayoutManager.STAGGERED_GRID)
            }
        }
        binding.rgOrientation.listenerOnCheckedChange = { _, button ->
            shadowDecorationViewModel.changeOrientation(if (button == binding.tvVertical) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL)
        }
        binding.switchReverse.setOnCheckedChangeListener { _, isChecked ->
            shadowDecorationViewModel.changeReverse(isChecked)
        }
        binding.sBoundLeft.addOnChangeListener { _, value, _ ->
            shadowDecorationViewModel.setBound(left = value.toInt())
        }
        binding.sBoundTop.addOnChangeListener { _, value, _ ->
            shadowDecorationViewModel.setBound(top = value.toInt())
        }
        binding.sBoundRight.addOnChangeListener { _, value, _ ->
            shadowDecorationViewModel.setBound(right = value.toInt())
        }
        binding.sBoundBottom.addOnChangeListener { _, value, _ ->
            shadowDecorationViewModel.setBound(bottom = value.toInt())
        }
        binding.sBoundInner.addOnChangeListener { _, value, _ ->
            shadowDecorationViewModel.setBound(inner = value.toInt())
        }
        binding.sBoundInnerHorizontal.addOnChangeListener { _, value, _ ->
            shadowDecorationViewModel.setBound(innerHorizontal = value.toInt())
        }
        binding.sBoundInnerVertical.addOnChangeListener { _, value, _ ->
            shadowDecorationViewModel.setBound(innerVertical = value.toInt())
        }
        binding.sMainAxisPaddingEnd.addOnChangeListener { _, value, _ ->
            shadowDecorationViewModel.setBound(mainAxisPaddingEnd = value.toInt())
        }
    }
}