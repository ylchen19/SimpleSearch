package com.example.simplesearch.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplesearch.R
import com.example.simplesearch.databinding.FragmentMainBinding
import com.example.simplesearch.viewmodel.MainViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding by lazy { _binding!! }
    private val mainViewModel: MainViewModel by viewModel()
    private var _repoAdapter: CardRecyclerViewAdapter? = null
    private val repoAdapter: CardRecyclerViewAdapter get() = _repoAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        return binding.root
    }

    private fun FragmentMainBinding.bindState(
        uiState: StateFlow<MainViewModel.UiState>,
        pagingData: Flow<PagingData<MainViewModel.UiModel>>,
        uiAction: (MainViewModel.UiAction) -> Unit
    ) {
        val header = LoadStateAdapter {repoAdapter.retry()}
        val footer = LoadStateAdapter {repoAdapter.retry()}
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = repoAdapter.withLoadStateHeaderAndFooter(
                header, footer
            )
        }

        bindSearch(
            uiState = uiState,
            onQueryTextChange = uiAction
        )

        bindList(
            header = header,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiAction
        )
    }

    private fun FragmentMainBinding.bindSearch(
        uiState: StateFlow<MainViewModel.UiState>,
        onQueryTextChange: (MainViewModel.UiAction.Search) -> Unit
    ){

        searchView.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput(onQueryTextChange)
                true
            } else {
                false
            }
        }

        searchView.editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput(onQueryTextChange)
                true
            }else {
                false
            }
        }

        searchView.editText.doAfterTextChanged {
            updateRepoListFromInput(onQueryTextChange)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchView::setText)
        }
    }

    private fun FragmentMainBinding.updateRepoListFromInput(
        onQueryTextChanged : (MainViewModel.UiAction.Search) -> Unit
    ) {
        searchView.text?.trim().let {
            if (it!!.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
                onQueryTextChanged(MainViewModel.UiAction.Search(query = it.toString()))
            }
            searchView.editText.setSelection(searchView.text.toString().length)
        }
    }

    private fun FragmentMainBinding.bindList(
        header: LoadStateAdapter,
        uiState: StateFlow<MainViewModel.UiState>,
        pagingData: Flow<PagingData<MainViewModel.UiModel>>,
        onScrollChanged: (MainViewModel.UiAction.Scroll) -> Unit
    ) {

        swipe.setOnRefreshListener {
            repoAdapter.retry()
            swipe.isRefreshing = false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(MainViewModel.UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = repoAdapter.loadStateFlow
            .distinctUntilChangedBy { it.source.refresh }
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        viewLifecycleOwner.lifecycleScope.launch {
            pagingData.collectLatest(repoAdapter::submitData)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) recyclerView.scrollToPosition(0)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            repoAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty = loadState.refresh is LoadState.NotLoading
                        && repoAdapter.itemCount == 0
                emptyState.isVisible = isListEmpty
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                        || loadState.mediator?.refresh is LoadState.NotLoading || searchView.text.toString().isNotEmpty()
                progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                errorHint.isVisible = loadState.mediator?.refresh is LoadState.Error
                        && repoAdapter.itemCount == 0

                header.loadState = loadState.mediator
                    ?.refresh
                    ?.takeIf { it is LoadState.Error && repoAdapter.itemCount > 0 }
                    ?:loadState.prepend

                val errorState = loadState.source.append as? LoadState.Error
                    ?:loadState.source.prepend as? LoadState.Error
                    ?:loadState.append as? LoadState.Error
                    ?:loadState.prepend as? LoadState.Error

                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Wooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _repoAdapter = CardRecyclerViewAdapter()
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.bindState(
            uiState = mainViewModel.state,
            pagingData = mainViewModel.pagingDataFlow,
            uiAction = mainViewModel.accept
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        _repoAdapter = null
    }

}