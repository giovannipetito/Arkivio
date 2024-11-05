package it.giovanni.arkivio.fragments.homepage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.archive.Toaster
import it.giovanni.arkivio.activities.NavigationActivity
import it.giovanni.arkivio.databinding.TrainingLayoutBinding
import it.giovanni.arkivio.fragments.HomeFragment
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class TrainingFragment : HomeFragment() {

    companion object {
        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): TrainingFragment {
            caller = c
            return TrainingFragment()
        }

        private var layoutBinding: TrainingLayoutBinding? = null
        val trainingLayoutBinding get() = layoutBinding
    }

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = TrainingLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        trainingLayoutBinding?.presenter = darkModePresenter
        trainingLayoutBinding?.temp = model

        return trainingLayoutBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trainingLayoutBinding?.labelArklib?.text = Toaster.getMessage()

        trainingLayoutBinding?.labelNavigationComponent?.setOnClickListener {
            startActivity(Intent(context, NavigationActivity::class.java))
        }
        trainingLayoutBinding?.labelUserInput?.setOnClickListener {
            currentActivity.openDetail(Globals.MVVM_USER_INPUT, null)
        }
        trainingLayoutBinding?.labelLoginInput?.setOnClickListener {
            currentActivity.openDetail(Globals.MVVM_LOGIN, null)
        }
        trainingLayoutBinding?.labelMvvmUsers?.setOnClickListener {
            currentActivity.openDetail(Globals.MVVM_USERS, null)
        }
        trainingLayoutBinding?.labelCoroutineHome?.setOnClickListener {
            currentActivity.openDetail(Globals.COROUTINE_HOME, null)
        }
        trainingLayoutBinding?.labelUsersHome?.setOnClickListener {
            currentActivity.openDetail(Globals.USERS_HOME, null)
        }
        trainingLayoutBinding?.labelRxHome?.setOnClickListener {
            currentActivity.openDetail(Globals.RX_HOME, null)
        }
        trainingLayoutBinding?.labelDependencyInjection?.setOnClickListener {
            currentActivity.openDetail(Globals.DEPENDENCY_INJECTION, null)
        }
        trainingLayoutBinding?.labelWorkManager?.setOnClickListener {
            currentActivity.openDetail(Globals.WORKMANAGER, null)
        }
        trainingLayoutBinding?.labelCleanArchitectureHome?.setOnClickListener {
            currentActivity.openDetail(Globals.CLEAN_ARCHITECTURE, null)
        }
        trainingLayoutBinding?.labelRoomCoroutines?.setOnClickListener {
            currentActivity.openDetail(Globals.ROOM_COROUTINES, null)
        }
        trainingLayoutBinding?.labelRoomRxjava?.setOnClickListener {
            currentActivity.openDetail(Globals.ROOM_RXJAVA, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}