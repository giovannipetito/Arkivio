package it.giovanni.kotlin.fragments.detail.cardio

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import io.card.payment.CardIOActivity
import io.card.payment.CardType
import io.card.payment.CreditCard
import io.card.payment.i18n.locales.LocalizedStringsList
import it.giovanni.kotlin.App
import it.giovanni.kotlin.R
import it.giovanni.kotlin.customview.AppCompatSpinnerCustom
import it.giovanni.kotlin.fragments.DetailFragment
import kotlinx.android.synthetic.main.card_io_layout.*
import java.text.DecimalFormat

class CardIOFragment : DetailFragment(), AppCompatSpinnerCustom.OnSpinnerEventsListener {

    companion object {
        private val REQUEST_SCAN = 100
        private val REQUEST_AUTOTEST = 200
    }

    private var viewFragment: View? = null
    private var creditCard: CreditCard? = null
    private var cardType : CardType? = null
    private var cardTypeImage: Bitmap? = null
    private var cardInfo: String? = null
    private var unblurredDigits: Int? = null

    private val interval = 4
    private val formatter = DecimalFormat("0")

    override fun getLayout(): Int {
        return R.layout.card_io_layout
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerContainer = viewFragment?.findViewById(R.id.spinner_container) as RelativeLayout
        val drawableBar = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(resources.getColor(R.color.verde_1), resources.getColor(R.color.verde_3))
        )
        drawableBar.cornerRadius = 100f
        spinnerContainer.setBackgroundDrawable(drawableBar)

        unblurredDigits = 0
        setPickerInterval()
        picker_unblurred_digits.setOnValueChangedListener { _, _, newVal ->
            unblurredDigits = newVal * 4
        }

        setupLanguageList()
        enableScanExpiry()

        expiry.setOnClickListener {
            enableScanExpiry()
        }

        button_scan.setOnClickListener {
            scanCreditCard()
        }

        val libraryVersion = "card.io library:\n" + CardIOActivity.sdkVersion()
        library_version.text = libraryVersion

        val buildDate = "CardIOActivity build date:\n" + CardIOActivity.sdkBuildDate()
        build_date.text = buildDate
    }

    @Suppress("DEPRECATION")
    private fun enableScanExpiry() {
        if (expiry.isChecked) {
            scan_expiry.isEnabled = true
            scan_expiry.setTextColor(context?.resources!!.getColor(R.color.grey_3))
        } else {
            scan_expiry.isEnabled = false
            scan_expiry.setTextColor(context?.resources!!.getColor(R.color.grey_2))
        }
    }

    private fun scanCreditCard() {
        val intent = Intent(context, CardIOActivity::class.java)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, expiry!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, scan_expiry!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, cvv!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, postal_code!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, use_card_io_logo!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, cardholder_name!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, suppress_confirmation!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_NO_CAMERA, bypass_the_scan!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, keep_application_theme!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, suppress_keyboard_icon!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_SUPPRESS_SCAN, suppress_scan_info!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, postal_code_numeric!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, show_paypal_action_bar_icon!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, ContextCompat.getColor(App.context, R.color.colorPrimary)) // Color.BLUE (import android.graphics.Color)
            .putExtra(CardIOActivity.EXTRA_LANGUAGE_OR_LOCALE, language_spinner!!.selectedItem as String)
            .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true)
        try {
            intent.putExtra(CardIOActivity.EXTRA_UNBLUR_DIGITS, unblurredDigits)
        } catch (ignored: NumberFormatException) {}

        startActivityForResult(intent,
            REQUEST_SCAN
        )
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (((requestCode == REQUEST_SCAN || requestCode == REQUEST_AUTOTEST) && data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT))) {

            creditCard = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)
            cardType = creditCard?.cardType
            cardTypeImage = cardType?.imageBitmap(context)

            cardInfo = "Card number: " + creditCard?.redactedCardNumber + "\n" +
                    "Card type: " + cardType.toString() + "\n" + "Display name: " + cardType?.getDisplayName(null) + "\n"

            if (expiry!!.isChecked)
                cardInfo += "Expiry: " + creditCard?.expiryMonth + "/" + creditCard?.expiryYear + "\n"

            if (cvv!!.isChecked)
                cardInfo += "CVV: " + creditCard?.cvv + "\n"

            if (postal_code!!.isChecked)
                cardInfo += "Postal Code: " + creditCard?.postalCode + "\n"

            if (cardholder_name!!.isChecked)
                cardInfo += "Cardholder Name: " + creditCard?.cardholderName + "\n"
        }

        card_type_image!!.setImageBitmap(cardTypeImage)

        card_info!!.text = cardInfo

        val card = CardIOActivity.getCapturedCardImage(data)
        card_image!!.setImageBitmap(card)
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
        var arraylanguages: Array<String> = arrayOf("de", "en", "es", "fr", "it", "pt", "ru")

        val adapter = ArrayAdapter(context!!, R.layout.spinner_dropdown_item, languages) // arraylanguages

        // TODO) Nota: Per l'adapter ho definito un item custom (spinner_dropdown_item), ma avrei anche potuto utilizzare un item nativo di Android (simple_dropdown_item_1line):
        // val adapter = ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line, languages)

        language_spinner!!.adapter = adapter
        language_spinner!!.setSelection(adapter.getPosition("it"))
        language_spinner.setSpinnerEventsListener(this)

        /*
        language_spinner.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                language_spinner.setBackgroundResource(R.drawable.spinner_background_up)
            }
            false
        }

        language_spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 1) {
                    language_spinner.setBackgroundResource(R.drawable.spinner_background_down)
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

        picker_unblurred_digits.value = 0
        picker_unblurred_digits!!.minValue = 0
        picker_unblurred_digits!!.maxValue = numValues - 1
        picker_unblurred_digits!!.displayedValues = displayedValues
    }

    override fun onStop() {
        super.onStop()
        card_info!!.text = ""
    }

    override fun onSpinnerOpened(spin: AppCompatSpinner?) {
        language_spinner.setBackgroundResource(R.drawable.spinner_background_up)
    }

    override fun onSpinnerClosed(spin: AppCompatSpinner?) {
        language_spinner.setBackgroundResource(R.drawable.spinner_background_down)
    }
}