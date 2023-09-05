package com.example.simplesearch.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplesearch.R
import com.example.simplesearch.data.Item
import com.example.simplesearch.databinding.InfoCardBinding
import com.example.simplesearch.viewmodel.MainViewModel

class CardRecyclerViewAdapter: PagingDataAdapter<MainViewModel.UiModel, CardRecyclerViewAdapter.CardViewHolder>(COMPARATOR) {

    inner class CardViewHolder(private val cardViewBinding: InfoCardBinding) :
        RecyclerView.ViewHolder(cardViewBinding.root) {

        init {
            cardViewBinding.card.setOnClickListener {
                val html_url = cardViewBinding.repo!!.html_url
                html_url.let { url ->
                    val action = MainFragmentDirections.showDetail(url!!)
                    Navigation.findNavController(it).navigate(action)
                }
            }
        }

        fun bind(cardItems: Item?) {
            if (cardItems == null) {
                val resources = itemView.resources
                cardViewBinding.name.text = resources.getString(R.string.loading)
                cardViewBinding.desc.visibility = View.GONE
                cardViewBinding.repoLanguage.visibility = View.GONE
                cardViewBinding.stars.text = resources.getString(R.string.unknown)
                cardViewBinding.ownerAvatar.visibility = View.VISIBLE
            } else {
                fetchData(cardItems)
            }
        }

        private fun fetchData(item: Item) {
            cardViewBinding.repo = item
            cardViewBinding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = InfoCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {

        val uiModel = getItem(position)
        uiModel?.let {
            when (uiModel) {
                is MainViewModel.UiModel.RepoItem ->holder.bind(uiModel.repo)
            }
        }
    }

    companion object COMPARATOR : DiffUtil.ItemCallback<MainViewModel.UiModel>() {

        override fun areItemsTheSame(oldItem: MainViewModel.UiModel, newItem: MainViewModel.UiModel): Boolean {
            return (oldItem is MainViewModel.UiModel.RepoItem && newItem is MainViewModel.UiModel.RepoItem &&
                    oldItem.repo.id == newItem.repo.id)
        }
        override fun areContentsTheSame(oldItem: MainViewModel.UiModel, newItem: MainViewModel.UiModel): Boolean {
            return oldItem == newItem
        }
    }


}