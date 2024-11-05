package it.giovanni.arkivio.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.fragments.homepage.TrainingFragment
import it.giovanni.arkivio.fragments.homepage.HomePageFragment
import it.giovanni.arkivio.fragments.homepage.LearningFragment

class HomeFragmentAdapter(fragmentManager: FragmentManager, private val homePageCounter: Int, private val caller: MainFragment) : FragmentPagerAdapter(fragmentManager) {

    private var homePageFragment: HomePageFragment? = null
    private var learningFragment: LearningFragment? = null
    private var trainingFragment: TrainingFragment? = null

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                if (homePageFragment == null) homePageFragment = HomePageFragment.newInstance(caller)
                return homePageFragment!!
            }
            1 -> {
                if (learningFragment == null) learningFragment = LearningFragment.newInstance(caller)
                return learningFragment!!
            }
            2 -> {
                if (trainingFragment == null) trainingFragment = TrainingFragment.newInstance(caller)
                return trainingFragment!!
            }
            else -> {
                if (homePageFragment == null) homePageFragment = HomePageFragment.newInstance(caller)
                return homePageFragment!!
            }
        }
    }

    override fun getCount(): Int {
        return homePageCounter
    }
}