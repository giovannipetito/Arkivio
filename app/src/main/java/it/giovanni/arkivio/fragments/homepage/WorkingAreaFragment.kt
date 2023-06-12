package it.giovanni.arkivio.fragments.homepage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.giovanni.arkivio.databinding.WorkingAreaLayoutBinding
import it.giovanni.arkivio.fragments.HomeFragment
import it.giovanni.arkivio.fragments.MainFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.Globals

class WorkingAreaFragment : HomeFragment() {

    companion object {
        private var caller: MainFragment? = null
        fun newInstance(c: MainFragment): WorkingAreaFragment {
            caller = c
            return WorkingAreaFragment()
        }
    }

    private var layoutBinding: WorkingAreaLayoutBinding? = null
    private val binding get() = layoutBinding

    override fun getTitle(): Int {
        return NO_TITLE
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = WorkingAreaLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this)
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var oldPostion = 0

        if (binding?.scrollView != null) {
            binding?.scrollView?.viewTreeObserver?.addOnScrollChangedListener {
                if (binding?.scrollView != null) {
                    if (binding?.scrollView?.scrollY!! > oldPostion) {
                        binding?.fab?.hide()
                    } else if (binding?.scrollView?.scrollY!! < oldPostion || binding?.scrollView?.scrollY!! <= 0) {
                        binding?.fab?.show()
                    }
                    oldPostion = binding?.scrollView?.scrollY!!
                }
            }
        }

        binding?.labelPuntonet?.setOnClickListener {
            currentActivity.openDetail(Globals.PUNTONET, null)
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
        binding?.labelFonts?.setOnClickListener {
            currentActivity.openDetail(Globals.FONTS, null)
        }
        binding?.labelNotificationHome?.setOnClickListener {
            currentActivity.openDetail(Globals.NOTIFICATION_HOME, null)
        }
        binding?.labelMachineLearning?.setOnClickListener {
            currentActivity.openDetail(Globals.MACHINE_LEARNING, null)
        }
        binding?.labelExoplayer?.setOnClickListener {
            currentActivity.openDetail(Globals.EXOPLAYER, null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}