package it.giovanni.kotlin.fragments.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import it.giovanni.kotlin.R
import it.giovanni.kotlin.customview.popup.DialogListPopup
import it.giovanni.kotlin.fragments.DetailFragment
import it.giovanni.kotlin.utils.Utils
import it.giovanni.kotlin.utils.Utils.Companion.callContact
import it.giovanni.kotlin.utils.Utils.Companion.sendEmail
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.android.synthetic.main.detail_layout.*
import kotlinx.android.synthetic.main.rubrica_layout.*
import java.io.ByteArrayOutputStream

class RubricaFragment : DetailFragment(), View.OnClickListener {

    val LABEL_DELETE = "annulla"
    val LABEL_INSERT = "crea nuovo contatto"
    val LABEL_EDIT = "aggiungi a contatto esistente"

    private var avatar: Bitmap? = null

    private var handleContactPopup: DialogListPopup? = null
    private var labels: ArrayList<String>? = null
    private var label: String? = null

    private val REQUEST_CODE_CONTACTS_PERMISSION = 1000
    var lookupKey: String? = null
    var id: Long = 0
    var name: String? = null
    var selectedContactUri: Uri? = null

    override fun getLayout(): Int {
        return R.layout.rubrica_layout
    }

    override fun getTitle(): Int {
        return R.string.rubrica_title
    }

    override fun searchAction(): Boolean {
        return false
    }

    override fun backAction(): Boolean {
        return false
    }

    override fun closeAction(): Boolean {
        return true
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

        avatar = BitmapFactory.decodeResource(context!!.resources, R.drawable.giovanni)
        val roundAvatar : Bitmap = Utils.getRoundBitmap(avatar!!, avatar!!.width)
        ico_profile.setImageBitmap(roundAvatar)

        numero_fisso_container.setOnClickListener {
            callContact(context!!, value_numero_fisso.text.toString())
        }

        cellulare_container.setOnClickListener {
            callContact(context!!, value_cellulare.text.toString())
        }

        email_container.setOnClickListener {
            sendEmail(context!!, value_mail.text.toString())
        }

        rubrica_icon.visibility = View.VISIBLE
        rubrica_icon.setOnClickListener {
            requestContactsPermission()
        }

        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun requestContactsPermission() {
        if (context?.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
            context?.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), REQUEST_CODE_CONTACTS_PERMISSION)
        } else {
            handleContact()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_CONTACTS_PERMISSION -> {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    handleContact()
                }
            }
        }
    }

    @SuppressLint("Recycle")
    private fun handleContact() {

        val cr = context?.contentResolver

        // val cur = cr?.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        val cur = cr?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "lower(" + ContactsContract.Contacts.DISPLAY_NAME + ") = lower('" + contact_name.text.toString().replace("'", "''") + "')",
            null,
            null
        )

        // selection: "lower(" + ContactsContract.Contacts.DISPLAY_NAME + ") = lower('" + employee.nome.replace("'", "''") + " " + employee.cognome.replace("'", "''") + "')"

        if (cur?.count!! > 0) {
            while (cur.moveToNext()) {
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                id = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID))
                lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                selectedContactUri = ContactsContract.Contacts.getLookupUri(id, lookupKey)
            }
        }

        if (name == null) {
            showInsertContactDialog()
        } else {
            showInsertEditContactDialog()

            if (BottomSheetBehavior.from(bottom_sheet).state != BottomSheetBehavior.STATE_EXPANDED) {
                BottomSheetBehavior.from(bottom_sheet).setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                BottomSheetBehavior.from(bottom_sheet).setState(BottomSheetBehavior.STATE_COLLAPSED)
            }

            bottom_sheet_delete.setOnClickListener {
                BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_HIDDEN
            }
            bottom_sheet_edit.setOnClickListener {
                editContact(selectedContactUri)
            }
            bottom_sheet_insert.setOnClickListener {
                insertContact()
            }
        }
    }

    private fun showInsertContactDialog() {

        labels = ArrayList()
        labels?.add(LABEL_INSERT)
        labels?.add(LABEL_DELETE)

        handleContactPopup = DialogListPopup(currentActivity, R.style.PopupTheme)
        handleContactPopup!!.setCancelable(true)
        handleContactPopup!!.setMessage(resources.getString(R.string.rubrica_message_dialog))
        handleContactPopup!!.setLabels(labels!!, this)
        handleContactPopup!!.setButton(resources.getString(R.string.popup_button_cancel), View.OnClickListener {})

        // Il codice seguente mostra il popup in basso alla view, ma non funziona.
        // val window = handleContactPopup?.getDialog().window
        // val wlp : WindowManager.LayoutParams? = window.attributes
        // wlp?.gravity = Gravity.BOTTOM
        // window.attributes = wlp

        handleContactPopup!!.show()
    }

    private fun showInsertEditContactDialog() {

        labels = ArrayList()
        labels?.add(LABEL_EDIT)
        labels?.add(LABEL_INSERT)
        labels?.add(LABEL_DELETE)

        handleContactPopup = DialogListPopup(currentActivity, R.style.PopupTheme)
        handleContactPopup!!.setCancelable(true)
        handleContactPopup!!.setMessage(resources.getString(R.string.rubrica_message_dialog))
        handleContactPopup!!.setLabels(labels!!, this)
        handleContactPopup!!.setButton(resources.getString(R.string.popup_button_cancel), View.OnClickListener {})
        handleContactPopup!!.show()
    }

    override fun onClick(v: View?) {

        label = v!!.tag as String
        handleContactPopup!!.dismiss()

        if (label!! == LABEL_EDIT) {

            // editContact(selectedContactUri)

            insertEditContact(selectedContactUri)

        } else if (label!! == LABEL_INSERT) {
            insertContact()
        }
    }

    private fun insertContact() {

        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            type = ContactsContract.RawContacts.CONTENT_TYPE
        }

        fillContactFields(intent)

        startActivity(intent)
    }

    // Apre direttamente il dettaglio del contatto da modificare.
    private fun editContact(contactUri: Uri? = null) {
        // Creates a new Intent to edit a contact
        val intent = Intent(Intent.ACTION_EDIT)

        fillContactFields(intent)

        intent.putExtra("finishActivityOnSaveCompleted", true)
        startActivity(intent)
    }

    // Apre l'elenco dei contatti, l'utente dovrà poi scegliere quale contatto modificare.
    private fun insertEditContact(contactUri: Uri? = null) {
        // Creates a new Intent to insert or edit a contact
        val intent = Intent(Intent.ACTION_INSERT_OR_EDIT)

        fillContactFields(intent)

        intent.apply {
            setDataAndType(contactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
        }

        intent.putExtra("finishActivityOnSaveCompleted", true)
        startActivity(intent) // Sends the Intent with an request ID
    }

    private fun fillContactFields(intent : Intent) {

        intent.apply {
            putExtra(ContactsContract.Intents.Insert.NAME, contact_name.text.toString())

            putExtra(ContactsContract.Intents.Insert.PHONE, value_numero_fisso.text.toString())
            putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)

            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, value_cellulare.text.toString())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)

            putExtra(ContactsContract.Intents.Insert.EMAIL, value_mail.text.toString())
            putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)

            putExtra(ContactsContract.Intents.Insert.COMPANY, value_occupazione.text.toString())

            putExtra(ContactsContract.Intents.Insert.POSTAL, value_indirizzo.text.toString())
            putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
        }

        if (avatar != null) {
            // Sets the contact's avatar
            val data = ArrayList<ContentValues>()
            val row = ContentValues()
            row.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
            val stream = ByteArrayOutputStream()
            avatar?.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            row.put(ContactsContract.CommonDataKinds.Photo.PHOTO, image)
            data.add(row)
            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)
        }
    }
}