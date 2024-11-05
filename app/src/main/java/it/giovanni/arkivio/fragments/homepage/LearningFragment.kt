package it.giovanni.arkivio.fragments.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.databinding.LearningLayoutBinding
import it.giovanni.arkivio.fragments.HomeFragment
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class LearningFragment : HomeFragment() {

    companion object {
        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): LearningFragment {
            caller = c
            return LearningFragment()
        }
    }

    private var layoutBinding: LearningLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = LearningLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.labelFonts?.setOnClickListener {
            currentActivity.openDetail(Globals.FONTS, null)
        }
        binding?.labelLink?.setOnClickListener {
            currentActivity.openDetail(Globals.LINK, null)
        }
        binding?.labelLogcatProjects?.setOnClickListener {
            currentActivity.openDetail(Globals.LOGCAT_PROJECTS, null)
        }
        binding?.labelDate?.setOnClickListener {
            currentActivity.openDetail(Globals.DATE, null)
        }
        binding?.labelEmail?.setOnClickListener {
            currentActivity.openDetail(Globals.EMAIL, null)
        }
        binding?.labelSimpleRetrofit?.setOnClickListener {
            currentActivity.openDetail(Globals.SIMPLE_RETROFIT, null)
        }
        binding?.labelAsyncHttp?.setOnClickListener {
            currentActivity.openDetail(Globals.ASYNC_HTTP, null)
        }
        binding?.labelVolley?.setOnClickListener {
            currentActivity.openDetail(Globals.VOLLEY, null)
        }
        binding?.labelRubrica?.setOnClickListener {
            currentActivity.openDetail(Globals.RUBRICA_REALTIME, null)
        }
        binding?.labelPermissions?.setOnClickListener {
            currentActivity.openDetail(Globals.PERMISSIONS, null)
        }
        binding?.labelLayoutManager?.setOnClickListener {
            currentActivity.openDetail(Globals.LAYOUT_MANAGER, null)
        }
        binding?.labelChecklist?.setOnClickListener {
            currentActivity.openDetail(Globals.CHECKLIST, null)
        }
        binding?.labelStickyHeader?.setOnClickListener {
            currentActivity.openDetail(Globals.STICKY_HEADER, null)
        }
        binding?.labelCardIo?.setOnClickListener {
            currentActivity.openDetail(Globals.CARD_IO, null)
        }
        binding?.labelMachineLearning?.setOnClickListener {
            currentActivity.openDetail(Globals.MACHINE_LEARNING, null)
        }
        binding?.labelExoplayer?.setOnClickListener {
            currentActivity.openDetail(Globals.EXOPLAYER, null)
        }
        binding?.labelFavorites?.setOnClickListener {
            currentActivity.openDetail(Globals.FAVORITES, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}