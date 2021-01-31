package com.yong.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yong.R
import com.yong.data.model.Store
import com.yong.databinding.ViewholderStoreBinding
import com.yong.ui.main.MainViewModel
import com.yong.ui.main.StoreViewModel
import com.yong.utils.OnItemClickListener

private const val VIEWTYPE_DEFAULT = 0
private const val VIEWTYPE_MORE = 1

class StoreAdapter(private val viewModel: MainViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    OnItemClickListener<StoreViewModel> {
    var items: MutableList<Store> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEWTYPE_DEFAULT) {
            StoreViewHolder.create(parent)
        } else {
            LoadMoreViewHolder(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (viewModel.hasMore && position == items.size) {
            VIEWTYPE_MORE
        } else {
            VIEWTYPE_DEFAULT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StoreViewHolder) {
            val item = items[position]
            val viewModel = StoreViewModel(item, this)
            holder.bind(viewModel)
        } else if (holder is LoadMoreViewHolder) {
            viewModel.fetchNextItems()
        }
    }

    override fun getItemCount(): Int = if (viewModel.hasMore) items.size + 1 else items.size

    override fun onClick(value: StoreViewModel) {
        viewModel.openDetail(value.item)
    }

}




class StoreViewHolder(private val binding: ViewholderStoreBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: StoreViewModel) {
        binding.viewmodel = viewModel
        binding.executePendingBindings()
    }

    companion object {
        fun create(parent: ViewGroup) = StoreViewHolder(ViewholderStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
}

class LoadMoreViewHolder(container: View) : RecyclerView.ViewHolder(container) {
    constructor(parent: ViewGroup) : this(LayoutInflater.from(parent.context).inflate(R.layout.viewholder_load_more_progress_bar, parent, false))
}