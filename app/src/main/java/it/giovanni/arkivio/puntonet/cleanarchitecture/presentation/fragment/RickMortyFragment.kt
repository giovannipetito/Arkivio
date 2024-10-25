package it.giovanni.arkivio.puntonet.cleanarchitecture.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.RickMortyItemBinding
import it.giovanni.arkivio.databinding.RickMortyLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.puntonet.cleanarchitecture.data.model.RickMorty
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter

class RickMortyFragment : DetailFragment() {

    private var layoutBinding: RickMortyLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return R.string.clean_architecture_rxjava_title
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

    override fun onActionSearch(searchString: String) {
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = RickMortyLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        currentActivity.viewModel.characters.observe(viewLifecycleOwner) { characters ->
            hideProgressDialog()
            showCharacters(characters)
        }
    }

    private fun loadData() {
        showProgressDialog()
        currentActivity.viewModel.getCharacters(1)
    }

    private fun showCharacters(list: List<RickMorty?>?) {

        binding?.charactersContainer?.removeAllViews()

        if (list.isNullOrEmpty())
            return

        for (character in list) {

            val itemBinding: RickMortyItemBinding = RickMortyItemBinding.inflate(layoutInflater, binding?.charactersContainer, false)
            val itemView: View = itemBinding.root

            val characterName: TextView = itemBinding.characterName
            val characterImage: ImageView = itemBinding.characterImage

            characterName.text = character?.name

            val imageUrl: String? = character?.image
            Glide.with(requireContext())
                .load(imageUrl)
                .into(characterImage)

            binding?.charactersContainer?.addView(itemView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
        currentActivity.viewModel.disposable?.dispose()
    }
}