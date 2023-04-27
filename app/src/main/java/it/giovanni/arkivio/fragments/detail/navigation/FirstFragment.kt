package it.giovanni.arkivio.fragments.detail.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var layoutBinding: FragmentFirstBinding? = null
    private val binding get() = layoutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        layoutBinding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding?.root

        // val view = inflater.inflate(R.layout.fragment_first, container, false)
        // return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.firstButton?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_firstFragment_to_secondFragment)
        }
    }
}