package it.giovanni.arkivio.fragments.detail.cardio

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import com.airbnb.paris.extensions.style
import io.card.payment.CardIOActivity
import io.card.payment.CardType
import io.card.payment.CreditCard
import io.card.payment.i18n.locales.LocalizedStringsList
import it.giovanni.arkivio.App
import it.giovanni.arkivio.R
import it.giovanni.arkivio.customview.AppCompatSpinnerCustom
import it.giovanni.arkivio.databinding.CardIoLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.SharedPreferencesManager
import java.text.DecimalFormat

class CardIOFragment : DetailFragment(), AppCompatSpinnerCustom.OnSpinnerEventsListener {

    private var layoutBinding: CardIoLayoutBinding? = null
    private val binding get() = layoutBinding

    private var creditCard: CreditCard? = null
    private var cardType : CardType? = null
    private var cardTypeImage: Bitmap? = null
    private var cardInfo: String? = null
    private var unblurredDigits: Int? = null

    private val interval = 4
    private val formatter = DecimalFormat("0")

    override fun getTitle(): Int {
        return R.string.card_io_title
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
        layoutBinding = CardIoLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewStyle()

        unblurredDigits = 0
        setPickerInterval()
        binding?.pickerUnblurredDigits?.setOnValueChangedListener { _, _, newVal ->
            unblurredDigits = newVal * 4
        }

        setupLanguageList()
        enableScanExpiry()

        binding?.expiry?.setOnClickListener {
            enableScanExpiry()
        }

        binding?.buttonScan?.setOnClickListener {
            scanCreditCard()
        }

        val libraryVersion = "card.io library:\n" + CardIOActivity.sdkVersion()
        binding?.libraryVersion?.text = libraryVersion

        val buildDate = "CardIOActivity build date:\n" + CardIOActivity.sdkBuildDate()
        binding?.buildDate?.text = buildDate
    }

    private fun setViewStyle() {
        isDarkMode = SharedPreferencesManager.loadDarkModeStateFromPreferences()

        val spinnerContainer = binding?.spinnerContainer
        val drawableBar: GradientDrawable?

        if (isDarkMode) {
            drawableBar = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(ContextCompat.getColor(requireContext(), R.color.verde), ContextCompat.getColor(requireContext(), R.color.verde)))
            binding?.buttonScan?.style(R.style.ButtonNormalDarkMode)
        }
        else {
            drawableBar = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(ContextCompat.getColor(requireContext(), R.color.rosso), ContextCompat.getColor(requireContext(), R.color.rosso)))
            binding?.buttonScan?.style(R.style.ButtonNormalLightMode)
        }

        drawableBar.cornerRadius = 100f
        spinnerContainer?.background = drawableBar
    }

    private fun enableScanExpiry() {
        if (binding?.expiry?.isChecked!!) {
            binding?.scanExpiry?.isEnabled = true
            binding?.scanExpiry?.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_3))
        } else {
            binding?.scanExpiry?.isEnabled = false
            binding?.scanExpiry?.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_2))
        }
    }

    private fun scanCreditCard() {
        val intent = Intent(context, CardIOActivity::class.java)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, binding?.expiry?.isChecked)
            .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, binding?.scanExpiry?.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, binding?.cvv?.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, binding?.postalCode?.isChecked)
            .putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, binding?.useCardIoLogo?.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, binding?.cardholderName?.isChecked)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, binding?.suppressConfirmation?.isChecked)
            .putExtra(CardIOActivity.EXTRA_NO_CAMERA, binding?.bypassTheScan?.isChecked)
            .putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, binding?.keepApplicationTheme?.isChecked)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, binding?.suppressKeyboardIcon?.isChecked)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, binding?.suppressScanInfo?.isChecked)
            .putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, binding?.postalCodeNumeric?.isChecked)
            .putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, binding?.showPaypalActionBarIcon?.isChecked)
            .putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, ContextCompat.getColor(App.context, R.color.colorPrimary)) // Color.BLUE (import android.graphics.Color)
            .putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, binding?.languageSpinner?.selectedItem as String)
            .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true)
        try {
            intent.putExtra(CardIOActivity.EXTRA_UNBLUR_DIGITS, unblurredDigits)
        } catch (ignored: NumberFormatException) {
            ignored.printStackTrace()
        }

        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data

            if ((data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT))) {

                creditCard = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)
                cardType = creditCard?.cardType
                cardTypeImage = cardType?.imageBitmap(context)

                cardInfo = "Card number: " + creditCard?.redactedCardNumber + "\n" +
                        "Card type: " + cardType.toString() + "\n" + "Display name: " + cardType?.getDisplayName(null) + "\n"

                if (binding?.expiry?.isChecked!!)
                    cardInfo += "Expiry: " + creditCard?.expiryMonth + "/" + creditCard?.expiryYear + "\n"

                if (binding?.cvv?.isChecked!!)
                    cardInfo += "CVV: " + creditCard?.cvv + "\n"

                if (binding?.postalCode?.isChecked!!)
                    cardInfo += "Postal Code: " + creditCard?.postalCode + "\n"

                if (binding?.cardholderName?.isChecked!!)
                    cardInfo += "Cardholder Name: " + creditCard?.cardholderName + "\n"
            }

            binding?.cardTypeImage?.setImageBitmap(cardTypeImage)
            binding?.cardInfo?.text = cardInfo

            val card = CardIOActivity.getCapturedCardImage(data)
            binding?.cardImage?.setImageBitmap(card)
        }
    }

    private fun setupLanguageList() {
        val languages: ArrayList<String> = ArrayList()
        for (locale in LocalizedStringsList.ALL_LOCALES) {
            if (locale.name == "de" ||
                locale.name == "en" ||
                locale.name == "es" ||
                locale.name == "fr" ||
                locale.name == "it" ||
                locale.name == "pt" ||
                locale.name == "ru")
                languages.add(locale.name)
        }

        // TODO) Potrei passare all'adapter anche l'Array arraylanguages invece dell'ArrayList languages.
        // var arraylanguages: Array<String> = arrayOf("de", "en", "es", "fr", "it", "pt", "ru")

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, languages) // arraylanguages

        // TODO) Nota: Per l'adapter ho definito un item custom (spinner_dropdown_item), ma avrei anche potuto utilizzare un item nativo di Android (simple_dropdown_item_1line):
        // val adapter = ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line, languages)

        binding?.languageSpinner?.adapter = adapter
        binding?.languageSpinner?.setSelection(adapter.getPosition("it"))
        binding?.languageSpinner?.setSpinnerEventsListener(this)

        /*
        binding?.languageSpinner?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                binding?.languageSpinner?.setBackgroundResource(R.drawable.spinner_background_up)
            }
            false
        }

        binding?.languageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 1) {
                    binding?.languageSpinner?.setBackgroundResource(R.drawable.spinner_background_down)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        */
    }

    private fun setPickerInterval() {

        val numValues = 20 / interval
        val displayedValues = arrayOfNulls<String>(numValues)
        for (i in 0 until numValues) {
            displayedValues[i] = formatter.format(i * interval)
        }

        binding?.pickerUnblurredDigits?.value = 0
        binding?.pickerUnblurredDigits?.minValue = 0
        binding?.pickerUnblurredDigits?.maxValue = numValues - 1
        binding?.pickerUnblurredDigits?.displayedValues = displayedValues
    }

    override fun onStop() {
        super.onStop()
        binding?.cardInfo?.text = ""
    }

    override fun onSpinnerOpened(spin: AppCompatSpinner?) {
        binding?.languageSpinner?.setBackgroundResource(R.drawable.spinner_background_up)
    }

    override fun onSpinnerClosed(spin: AppCompatSpinner?) {
        binding?.languageSpinner?.setBackgroundResource(R.drawable.spinner_background_down)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}