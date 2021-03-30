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

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.enniswu.ssshadow.demo.BaseViewHolder
import com.enniswu.ssshadow.demo.R
import com.enniswu.ssshadow.demo.databinding.ItemListBinding
import com.enniswu.ssshadow.demo.databinding.LayoutRecyclerViewBinding
import com.enniswu.ssshadow.graphics.ShadowParameter
import com.enniswu.ssshadow.widget.ShadowDecoration
import com.enniswu.ssshadow.widget.space.GridBoundDecoration
import com.enniswu.ssshadow.widget.space.LinearBoundDecoration
import com.enniswu.ssshadow.widget.space.StaggeredGridBoundDecoration
import java.util.*

class ShadowDecorationActivity : AppCompatActivity(), ShadowDecoration.ItemShadow {

    private lateinit var boundDecoration: RecyclerView.ItemDecoration

    private lateinit var shadowDecoration: ShadowDecoration

    private val linearBoundDecoration by lazy { LinearBoundDecoration() }

    private val gridBoundDecoration by lazy { GridBoundDecoration() }

    private val staggeredGridBoundDecoration by lazy { StaggeredGridBoundDecoration() }

    private val viewModel: ShadowDecorationViewModel by viewModels()

    private val adapter by lazy { Adapter() }

    private val binding by lazy { LayoutRecyclerViewBinding.inflate(layoutInflater) }

    private val linearLayoutManager by lazy { LinearLayoutManager(this) }

    private val gridLayoutManager by lazy { GridLayoutManager(this, 4).apply {
        spanSizeLookup = SpanSizeLookUp()
    } }

    private val staggeredGridLayoutManager by lazy { StaggeredGridLayoutManager(4, RecyclerView.VERTICAL) }

    private val itemTouchHelper by lazy { ItemTouchHelper(ItemTouchHelperCallback()) }

    private val dp1 by lazy { resources.getDimensionPixelSize(R.dimen.dp_1) }

    private val dp5 by lazy { resources.getDimensionPixelSize(R.dimen.dp_5) }

    private val dp20 by lazy { resources.getDimensionPixelSize(R.dimen.dp_20) }

    private val dp50 by lazy { resources.getDimensionPixelSize(R.dimen.dp_50) }

    private val dp100 by lazy { resources.getDimensionPixelSize(R.dimen.dp_100) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter
        boundDecoration = linearBoundDecoration
        shadowDecoration = ShadowDecoration(this)
        shadowDecoration.attachToRecyclerView(lifecycle, binding.recyclerView)

        viewModel.layoutManager.observe(this) {
            changeLayoutManager(it)
        }
        viewModel.orientation.observe(this) {
            changeOrientation(it)
        }
        viewModel.reverse.observe(this) {
            reverseLayout(it)
        }
        viewModel.bound.observe(this) {
            changeBound(it)
        }

        viewModel.changeLayoutManager(LayoutManager.LINEAR)
        viewModel.setBound(20, 20, 20, 20, 20, 20, 20, 10)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemSetting) {
            BoundPanelFragment().show(supportFragmentManager, null)
        } else {
            return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun getItemShadow(position: Int, parameter: ShadowParameter): Boolean {
        parameter.apply {
            color = resources.getColor(viewModel.list[position].colorRes)
            radius = dp20
            offsetY = dp5
        }
        return true
    }

    private fun changeLayoutManager(@LayoutManager it: Int) {
        binding.recyclerView.apply {
            removeItemDecoration(boundDecoration)
            layoutManager = when(it) {
                LayoutManager.GRID -> {
                    itemTouchHelper.attachToRecyclerView(null)
                    boundDecoration = gridBoundDecoration
                    gridLayoutManager
                }
                LayoutManager.STAGGERED_GRID -> {
                    itemTouchHelper.attachToRecyclerView(null)
                    boundDecoration = staggeredGridBoundDecoration
                    staggeredGridLayoutManager
                }
                else -> {
                    itemTouchHelper.attachToRecyclerView(binding.recyclerView)
                    boundDecoration = linearBoundDecoration
                    linearLayoutManager
                }
            }
            addItemDecoration(boundDecoration)
        }
    }

    private fun changeOrientation(it: Int) {
        linearLayoutManager.orientation = it
        gridLayoutManager.orientation = it
        staggeredGridLayoutManager.orientation = it
        adapter.notifyDataSetChanged()
    }

    private fun reverseLayout(it: Boolean) {
        linearLayoutManager.reverseLayout = it
        gridLayoutManager.reverseLayout = it
        staggeredGridLayoutManager.reverseLayout = it
        adapter.notifyDataSetChanged()
    }

    private fun changeBound(it: Bound) {
        linearBoundDecoration.setBound(it.left * dp1, it.top * dp1, it.right * dp1,
                it.bottom * dp1, it.inner * dp1)
        gridBoundDecoration.setBound(it.left * dp1, it.top * dp1, it.right * dp1,
                it.bottom * dp1, it.innerVertical * dp1, it.innerHorizontal * dp1)
        staggeredGridBoundDecoration.setBound(it.left * dp1, it.top * dp1, it.right * dp1,
                it.bottom * dp1, it.innerVertical * dp1, it.innerHorizontal * dp1)

        if (viewModel.layoutManager.value == LayoutManager.STAGGERED_GRID) {
            if (viewModel.orientation.value == RecyclerView.VERTICAL) {
                if (viewModel.reverse.value == true) {
                    binding.recyclerView.setPadding(0, it.mainAxisPaddingEnd * dp1, 0, 0)
                } else {
                    binding.recyclerView.setPadding(0, 0, 0, it.mainAxisPaddingEnd * dp1)
                }
            } else {
                if (viewModel.reverse.value == true) {
                    binding.recyclerView.setPadding(0, 0, it.mainAxisPaddingEnd * dp1, 0)
                } else {
                    binding.recyclerView.setPadding(it.mainAxisPaddingEnd * dp1, 0, 0, 0)
                }
            }
        } else {
            binding.recyclerView.setPadding(0, 0, 0, 0)
        }
        adapter.notifyDataSetChanged()
    }

    inner class Adapter: RecyclerView.Adapter<BaseViewHolder<ItemListBinding>>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                BaseViewHolder<ItemListBinding> {
            val binding = ItemListBinding.inflate(LayoutInflater.from(this@ShadowDecorationActivity),
                    parent, false)
            val holder = BaseViewHolder(binding)
            binding.root.setOnClickListener { view ->
                view.pivotX = view.width / 3f
                view.pivotY = view.height / 3f
                AnimatorSet().apply {
                    play(ValueAnimator.ofFloat(1f, 0.1f, 1f).apply {
                        addUpdateListener {
                            val value = it.animatedValue as Float
                            view.scaleX = value
                            view.scaleY = value
                            shadowDecoration.animation()
                        }
                    }).with(ObjectAnimator.ofFloat(view, "rotation", 0f, 360f))
                    duration = 5000
                    start()
                }
            }
            return holder
        }

        override fun onBindViewHolder(holder: BaseViewHolder<ItemListBinding>, position: Int) {
            val lp = holder.binding.root.layoutParams
            val textLp = holder.binding.textView.layoutParams
            val staggeredLength =
                    if (viewModel.layoutManager.value == LayoutManager.STAGGERED_GRID)
                        dp100 * viewModel.list[position].staggeredLength
                    else ViewGroup.LayoutParams.WRAP_CONTENT
            if (viewModel.orientation.value == RecyclerView.VERTICAL) {
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
                textLp.width = ViewGroup.LayoutParams.MATCH_PARENT
                textLp.height = staggeredLength
            } else {
                lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT
                textLp.width = staggeredLength
                textLp.height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            holder.binding.textView.text = viewModel.list[position].value
        }

        override fun getItemCount(): Int {
            return viewModel.list.size
        }
    }

    inner class SpanSizeLookUp: GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int = viewModel.list[position].spanSize
    }

    inner class ItemTouchHelperCallback: ItemTouchHelper.Callback() {
        override fun isLongPressDragEnabled() = true

        override fun isItemViewSwipeEnabled() = true

        override fun getMovementFlags(recyclerView: RecyclerView,
                                      viewHolder: RecyclerView.ViewHolder) =
                makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT)

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition
            viewModel.swapData(from, to)
            adapter.notifyItemMoved(from, to)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            viewModel.removeData(position)
            adapter.notifyItemRemoved(position)
        }
    }
}