package it.giovanni.arkivio.fragments.detail.favorites.drop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.dialog.CoreDialog
import it.giovanni.arkivio.databinding.FavoritesLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.fragments.detail.favorites.drop.DropFavoritesViewModel.EditState
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.model.favorite.Favorite
import it.giovanni.arkivio.presenter.DarkModePresenter

var personalsRecyclerView: RecyclerView? = null
var availablesRecyclerView: RecyclerView? = null

class DropFavoritesFragment : DetailFragment(), OnDropAdapterListener {

    private var layoutBinding: FavoritesLayoutBinding? = null
    private val binding get() = layoutBinding

    private val viewModel: DropFavoritesViewModel by viewModels()

    private lateinit var personalsAdapter: DropFavoritesAdapter
    private lateinit var availablesAdapter: DropFavoritesAdapter

    private var title: Int = R.string.drop_favorites_title

    override fun getTitle(): Int {
        return title
    }

    override fun getActionTitle(): Int {
        return R.string.action_label_title
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

    override fun onActionSearch(searchString: String) {
    }

    override fun onActionClickListener() {
        showSuccessDialog()
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        layoutBinding = FavoritesLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        setupRecyclerViews()

        binding?.personalsRecyclerview?.setOnDragListener(personalsAdapter.dragListener)
        binding?.availablesRecyclerview?.setOnDragListener(availablesAdapter.dragListener)

        viewModel.personals.observe(viewLifecycleOwner) { personals ->
            refreshAdapter(personalsAdapter)
            personalsAdapter.submitList(personals)
        }

        viewModel.availables.observe(viewLifecycleOwner) { availables ->
            refreshAdapter(availablesAdapter)
            availablesAdapter.submitList(availables)
        }

        viewModel.isPersonalsChanged.observe(viewLifecycleOwner) { isPersonalsChanged ->
            setActionLabelState(isPersonalsChanged)
        }

        viewModel.isMaxReached.observe(viewLifecycleOwner) { isMaxReached ->
            binding?.availablesRecyclerview?.visibility = if (isMaxReached) View.GONE else View.VISIBLE
            personalsAdapter.enableDragDrop(isMaxReached)
            refreshAdapter(personalsAdapter)

            if (isMaxReached) {
                title = R.string.favorites_title_max_reached
                binding?.favoritesDescription?.setText(R.string.favorites_description_max_reached)
                personalsAdapter.enableDragDrop(isMaxReached)
                binding?.personalsTitle?.visibility = View.GONE
                binding?.restoreToDefault?.visibility = View.GONE
            } else {
                title = R.string.drag_favorites_title
                binding?.favoritesDescription?.setText(R.string.favorites_description)
                binding?.personalsTitle?.visibility = View.VISIBLE
                binding?.restoreToDefault?.visibility = View.VISIBLE
            }
        }

        viewModel.editState.observe(viewLifecycleOwner) { editState ->
            if (editState == EditState.SUCCESS) {
                resetScreen()
                val message = requireContext().resources.getString(R.string.favorites_update_success_title)
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            } else if (editState == EditState.ERROR) {
                val message = requireContext().resources.getString(R.string.favorites_update_error_title)
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                showErrorDialog(message)
            }
        }

        binding?.restoreToDefault?.setOnClickListener {
            // val emptyList: List<Favorite?> = emptyList()
            val initList: MutableList<Favorite?> = viewModel.responsePersonals.filterNotNull().take(viewModel.initSize).toMutableList()
            viewModel.saveFavorites(initList) // emptyList.toMutableList()
        }

        return binding?.root
    }

    private fun setupRecyclerViews() {

        val flexboxPersonals = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }

        val flexboxAvailables = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
            justifyContent = JustifyContent.FLEX_START
        }

        personalsRecyclerView = binding?.personalsRecyclerview
        availablesRecyclerView = binding?.availablesRecyclerview

        personalsAdapter = DropFavoritesAdapter(true, onAdapterListener = this)
        personalsRecyclerView?.apply {
            setHasFixedSize(false)
            layoutManager = flexboxPersonals
            adapter = personalsAdapter
        }

        availablesAdapter = DropFavoritesAdapter(false, onAdapterListener = this)
        availablesRecyclerView?.apply {
            setHasFixedSize(false)
            layoutManager = flexboxAvailables
            adapter = availablesAdapter
        }
    }

    private fun refreshAdapter(adapter: DropFavoritesAdapter) {
        adapter.notifyDataSetChanged()
    }

    private fun resetScreen() {
        binding?.nestedScrollViewFavorites?.scrollTo(0, 0)
        binding?.favoritesDescription?.visibility = View.GONE
        setActionLabelState(false)
        personalsAdapter.setEditMode(false)
        availablesAdapter.setEditMode(false)
    }

    private fun showSuccessDialog() {
        val dialog = CoreDialog(currentActivity, R.style.CoreDialogTheme)
        dialog.setCancelable(false)
        dialog.setTitle("", "")

        dialog.setMessage(resources.getString(R.string.favorites_save_message_title))
        dialog.setButtons(
            resources.getString(R.string.button_confirm), {
                dialog.dismiss()
                viewModel.saveFavorites(viewModel.personals.value?.toMutableList()!!)
            },
            resources.getString(R.string.button_cancel), {
                dialog.dismiss()
            }
        )
        dialog.show()
    }

    override fun onSwap(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int) {
        viewModel.onSwap(isPersonal = isPersonal, sourcePosition = sourcePosition, targetPosition = targetPosition)
    }

    override fun onDrop(isPersonal: Boolean, sourcePosition: Int, targetPosition: Int) {
        viewModel.onDrop(isPersonal = isPersonal, sourcePosition = sourcePosition, targetPosition = targetPosition)
    }

    override fun onEditModeChanged(isEditMode: Boolean) {
        personalsAdapter.setEditMode(isEditMode)
        availablesAdapter.setEditMode(isEditMode)
    }

    override fun onEditModeRemoved(position: Int) {
        binding?.favoritesDescription?.visibility = View.VISIBLE
        viewModel.removeEditItem(position)
    }

    override fun onPause() {
        super.onPause()
        viewModel.initEditState(EditState.INIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}