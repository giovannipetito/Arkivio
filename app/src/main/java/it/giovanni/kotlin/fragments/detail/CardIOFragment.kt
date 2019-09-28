package it.giovanni.kotlin.fragments.detail

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import io.card.payment.CardIOActivity
import io.card.payment.CardType
import io.card.payment.CreditCard
import io.card.payment.i18n.locales.LocalizedStringsList
import it.giovanni.kotlin.App
import it.giovanni.kotlin.R
import it.giovanni.kotlin.fragments.DetailFragment
import kotlinx.android.synthetic.main.card_io_layout.*

class CardIOFragment : DetailFragment() {

    private var creditCard: CreditCard? = null
    private var cardType : CardType? = null
    private var cardTypeImage: Bitmap? = null
    private var cardInfo: String? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLanguageList()

        val info = "card.io library: " + CardIOActivity.sdkVersion() + "\n" + "Build date: " + CardIOActivity.sdkBuildDate()
        version.text = info

        scan_credit_card.setOnClickListener {
            scanCreditCard()
        }
    }

    private fun scanCreditCard() {
        val intent = Intent(context, CardIOActivity::class.java)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, cvv!!.isChecked)
            .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, expiry!!.isChecked)
            // .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, expiry!!.isChecked)
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
            val unblurredDigits = Integer.parseInt(unblurred_digits!!.text.toString())
            intent.putExtra(CardIOActivity.EXTRA_UNBLUR_DIGITS, unblurredDigits)
        } catch (ignored:NumberFormatException) {}

        startActivityForResult(intent, REQUEST_SCAN)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (((requestCode == REQUEST_SCAN || requestCode == REQUEST_AUTOTEST) && data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT))) {

            creditCard = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT)
            cardType = creditCard?.cardType
            cardTypeImage = cardType?.imageBitmap(context)

            cardInfo = "Card number: " + creditCard?.redactedCardNumber + "\n" +
                    "Card type: " + cardType.toString() + "\n" + "DisplayName: " + cardType?.getDisplayName(null) + "\n"

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
            languages.add(locale.name)
        }

        val adapter = ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line, languages)
        language_spinner!!.adapter = adapter
        language_spinner!!.setSelection(adapter.getPosition("it"))
    }

    override fun onStop() {
        super.onStop()
        card_info!!.text = ""
    }

    companion object {
        private val REQUEST_SCAN = 100
        private val REQUEST_AUTOTEST = 200
    }
}