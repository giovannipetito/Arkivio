package it.giovanni.arkivio.fragments.detail.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.FragmentSecondBinding
import it.giovanni.arkivio.fragments.detail.navigation.model.User

class SecondFragment : Fragment(), View.OnClickListener {

    private var layoutBinding: FragmentSecondBinding? = null
    private val binding get() = layoutBinding

    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        layoutBinding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding?.secondButton?.setOnClickListener(this)
        binding?.thirdButton?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.second_button -> {
                val firstName: String = binding?.editFirstName?.text.toString()
                val lastName: String = binding?.editLastName?.text.toString()
                val user = User(firstName, lastName)

                val action = SecondFragmentDirections.actionSecondFragmentToArgsFragment(user)
                navController.navigate(action)
            }
            R.id.third_button -> {
                val action = SecondFragmentDirections.actionSecondFragmentToListFragment()
                findNavController().navigate(action)
            }
        }
    }
}