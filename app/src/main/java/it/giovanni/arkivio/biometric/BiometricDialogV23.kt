package it.giovanni.arkivio.biometric

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import com.google.android.material.bottomsheet.BottomSheetDialog
import it.giovanni.arkivio.R
import it.giovanni.arkivio.databinding.BiometricBottomSheetBinding

class BiometricDialogV23 internal constructor(@NonNull context: Context, private val biometricCallback: BiometricCallback) :
    BottomSheetDialog(context, R.style.BottomSheetDialogTheme1), View.OnClickListener {

    private var textTitle: TextView? = null
    private var textDescription: TextView? = null
    private var textSubtitle: TextView? = null
    private var textStatus: TextView? = null
    private var buttonCancel: Button? = null

    init {
        setDialogView()
    }

    private fun setDialogView() {

        // val bottomSheetView: View = layoutInflater.inflate(R.layout.biometric_bottom_sheet, null)
        // setContentView(bottomSheetView)

        val binding: BiometricBottomSheetBinding = BiometricBottomSheetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textTitle = binding.textTitle
        textSubtitle = binding.textSubtitle
        textDescription = binding.textDescription
        textStatus = binding.textStatus
        buttonCancel = binding.buttonCancel
    }

    internal fun setTitle(title: String) {
        textTitle?.text = title
    }

    internal fun setSubtitle(subtitle: String) {
        textSubtitle?.text = subtitle
    }

    internal fun setDescription(description: String) {
        textDescription?.text = description
    }

    internal fun updateStatus(status: String) {
        textStatus?.text = status
    }

    internal fun setButtonText(cancel: String) {
        buttonCancel?.text = cancel
    }

    override fun onClick(view: View) {
        dismiss()
        biometricCallback.onAuthenticationCancelled()
    }
}