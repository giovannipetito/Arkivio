package it.giovanni.arkivio.fragments.detail.puntonet.reactivex

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RxRetrofitLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.puntonet.retrofitgetpost.User
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class RxRetrofitFragment : DetailFragment(), UsersAdapter.OnItemViewClicked {

    private var layoutBinding: RxRetrofitLayoutBinding? = null
    private val binding get() = layoutBinding

    private lateinit var viewModel: RxRetrofitViewModel

    private lateinit var adapter: UsersAdapter

    override fun getTitle(): Int {
        return R.string.rx_retrofit_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return true
    }

    override fun closeAction(): Boolean {
        return false
    }

    override fun deleteAction(): Boolean {
        return false
    }

    override fun editAction(): Boolean {
        return false
    }

    override fun editIconClick() {
    }

    override fun onActionSearch(search_string: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = RxRetrofitLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RxRetrofitViewModel::class.java]

        setupAdapter()

        showProgressDialog()
        viewModel.fetchUsersV1(0)

        viewModel.users.observe(viewLifecycleOwner) { list ->
            hideProgressDialog()
            adapter.setList(list)
            adapter.notifyItemChanged(0)
            adapter.notifyDataSetChanged()
        }
    }

    private fun setupAdapter() {
        adapter = UsersAdapter(this)
        binding?.rxRetrofitRecyclerView?.setHasFixedSize(true)
        binding?.rxRetrofitRecyclerView?.layoutManager = LinearLayoutManager(activity)
        binding?.rxRetrofitRecyclerView?.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        viewModel.disposable?.dispose()
    }

    override fun onItemInfoClicked(user: User) {
        Toast.makeText(requireContext(), user.email, Toast.LENGTH_SHORT).show()
    }
}