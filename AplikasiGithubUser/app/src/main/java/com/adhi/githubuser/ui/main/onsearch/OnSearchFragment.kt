package com.adhi.githubuser.ui.main.onsearch

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adhi.githubuser.R
import com.adhi.githubuser.data.local.entity.UserEntity
import com.adhi.githubuser.data.remote.Result
import com.adhi.githubuser.databinding.FragmentOnSearchBinding
import com.adhi.githubuser.ui.adapters.UserAdapter
import com.adhi.githubuser.ui.main.MainViewModel
import com.adhi.githubuser.utils.NavControllerHelper.safeNavigate
import com.adhi.githubuser.utils.SnackBarExt.showSnackBar
import com.adhi.githubuser.utils.ViewVisibilityUtil.setGone
import com.adhi.githubuser.utils.ViewVisibilityUtil.setInvisible
import com.adhi.githubuser.utils.ViewVisibilityUtil.setVisible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnSearchFragment : Fragment(), UserAdapter.UserClickCallback, SearchView.OnQueryTextListener {

    private var _binding: FragmentOnSearchBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnSearchBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(activity as AppCompatActivity) {
            setSupportActionBar(binding?.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.lg_kembali)

        }
        setHasOptionsMenu(true)
        setUpView()
    }

    private val observer = Observer<Result<List<UserEntity>>> { result ->
        when(result) {
            is Result.Success -> {
                showContent()
                result.data?.let {
                    userAdapter.submitList(it)
                }
            }
            is Result.Error -> {
                showContent()
                requireActivity().showSnackBar(
                    requireActivity().window.decorView.rootView,
                    result.message)
            }
        }
    }

    private fun setUpView() {
        userAdapter = UserAdapter(this)
        binding?.apply {
            this@OnSearchFragment.searchView = searchView
            rvMain.layoutManager = LinearLayoutManager(requireActivity())
            rvMain.setHasFixedSize(true)
            rvMain.adapter = userAdapter
        }
        showContent()
        setUpSearchView()
    }

    private fun setUpSearchView() {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        binding?.searchView?.apply {
            if (viewModel.searchUser.value == null) requestFocus()
            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            setOnQueryTextListener(this@OnSearchFragment)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            hideContent()
            viewModel.setQuery(query)
            viewModel.searchUser.observe(viewLifecycleOwner, observer)
        }
        searchView.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null && newText.isNotEmpty()) {
            hideContent()
            viewModel.setQuery(newText)
            viewModel.searchUser.observe(viewLifecycleOwner, observer)
        }
        return true
    }

    private fun showContent() = binding?.apply {
        shimmer.root.setGone()
        rvMain.setVisible()
    }

    private fun hideContent() = binding?.apply {
        shimmer.root.setVisible()
        rvMain.setInvisible()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onItemClicked(user: UserEntity) {
        val toDetail =
            com.adhi.githubuser.ui.main.onsearch.OnSearchFragmentDirections.actionOnSearchFragmentToDetailFragment()
        toDetail.username = user.login
        safeNavigate(toDetail, javaClass.name)
    }
}