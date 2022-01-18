package it.giovanni.arkivio.fragments.detail.rubrica

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import it.giovanni.arkivio.R
import it.giovanni.arkivio.bean.user.User
import it.giovanni.arkivio.customview.popup.ListDialogPopup
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.utils.Utils
import it.giovanni.arkivio.utils.Utils.Companion.callContact
import it.giovanni.arkivio.utils.Utils.Companion.sendSimpleMail
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import kotlinx.android.synthetic.main.detail_layout.*
import kotlinx.android.synthetic.main.rubrica_detail_layout.*
import java.io.ByteArrayOutputStream

class RubricaDetailFragment : DetailFragment(), View.OnClickListener {

    private var viewFragment: View? = null
    private val labelEdit = "Aggiungi a contatto esistente"
    private val labelInsert = "Crea nuovo contatto"
    private val labelOpen = "Apri lista contatti"
    private val labelDelete = "Annulla"

    private var avatar: Bitmap? = null

    private lateinit var listDialogPopup: ListDialogPopup
    private lateinit var labels: ArrayList<String>
    private lateinit var contacts: ArrayList<String>
    private lateinit var label: String

    private val requestCodeContactsPermission = 1000
    private lateinit var lookupKey: String
    var id: Long = 0
    private var name: String? = null
    private lateinit var selectedContactUri: Uri

    private lateinit var user: User

    companion object {
        var KEY_RUBRICA = "KEY_RUBRICA"
        var KEY_COLOR = "KEY_COLOR"
    }

    override fun getLayout(): Int {
        return R.layout.rubrica_detail_layout
    }

    override fun getTitle(): Int {
        return R.string.rubrica_detail_title
    }

    override fun getActionTitle(): Int {
        return NO_TITLE
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewFragment = super.onCreateView(inflater, container, savedInstanceState)
        return viewFragment
    }

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TODO("Not yet implemented")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            user = requireArguments().getSerializable(KEY_RUBRICA) as User

            val contact = user.nome + " " + user.cognome
            user_name.text = contact

            if (user.fisso != "") value_numero_fisso.text = user.fisso
            else numero_fisso_container.visibility = View.GONE

            if (user.cellulare != "") value_cellulare.text = user.cellulare
            else cellulare_container.visibility = View.GONE

            if (user.emails!![0] != "") value_mail.text = Utils.turnArrayListToString(user.emails!!)
            else email_container.visibility = View.GONE

            if (user.indirizzo != "") value_indirizzo.text = user.indirizzo
            else indirizzo_container.visibility = View.GONE

            if (user.occupazione != "") value_occupazione.text = user.occupazione
            else occupazione_container.visibility = View.GONE
        }

        if (user.nome == "Giovanni" && user.cognome == "Petito") {
            avatar = BitmapFactory.decodeResource(requireContext().resources, R.drawable.giovanni)
            val roundAvatar : Bitmap = Utils.getRoundBitmap(avatar!!, avatar?.width!!)
            ico_profile.setImageBitmap(roundAvatar)
        } else {
            val color = requireArguments().getInt(KEY_COLOR)
            ico_profile.setColorFilter(ResourcesCompat.getColor(context?.resources!!, color, null))
        }

        numero_fisso_container.setOnClickListener {
            callContact(requireContext(), value_numero_fisso.text.toString())
        }

        cellulare_container.setOnClickListener {
            callContact(requireContext(), value_cellulare.text.toString())
        }

        email_container.setOnClickListener {
            sendSimpleMail(requireContext(), value_mail.text.toString())
        }

        rubrica_icon.visibility = View.VISIBLE
        rubrica_icon.setOnClickListener {
            requestContactPermission()
        }

        show_contacts.setOnClickListener {
            requestContactsPermission()
        }

        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun requestContactPermission() {
        if (context?.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
            context?.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), requestCodeContactsPermission)
        } else {
            handleContact()
        }
    }

    private fun requestContactsPermission() {
        if (context?.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
            context?.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), requestCodeContactsPermission)
        } else {
            showContactsDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestCodeContactsPermission -> {
                if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    handleContact()
                }
            }
        }
    }

    private fun handleContact() {

        val contentResolver = context?.contentResolver

        val cursor = contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "lower(" + ContactsContract.Contacts.DISPLAY_NAME + ") = lower('" + user_name.text.toString().replace("'", "''") + "')",
            null,
            null
        )

        if (cursor?.count!! > 0) {
            while (cursor.moveToNext()) {

                val mId = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val mDisplayName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val mLookupKey = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)

                id = cursor.getLong(mId)
                name = cursor.getString(mDisplayName)
                lookupKey = cursor.getString(mLookupKey)
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

    private fun getContacts(): ArrayList<String> {
        val list = ArrayList<String>()
        var phone: String? = null
        val contentResolver: ContentResolver = currentActivity.contentResolver
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cursor?.count ?: 0 > 0) {
            while (cursor != null && cursor.moveToNext()) {

                val mId = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val mDisplayName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val mHasPhoneNumber = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)

                val id = cursor.getString(mId)
                val name = cursor.getString(mDisplayName)
                // list.add(name)
                if (cursor.getInt(mHasPhoneNumber) > 0) {
                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (phoneCursor?.moveToNext()!!) {
                        val mNumber = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        phone = phoneCursor.getString(mNumber)
                    }
                    list.add("$name: $phone")
                    phoneCursor.close()
                } else {
                    list.add(name)
                }
            }
        }
        cursor?.close()
        return list
    }

    private fun showContactsDialog() {
        contacts = getContacts()
        contacts.add(labelDelete)

        listDialogPopup = ListDialogPopup(currentActivity, R.style.PopupTheme)
        listDialogPopup.setCancelable(false)
        listDialogPopup.setMessage(resources.getString(R.string.rubrica_title))
        listDialogPopup.setLabels(contacts, this)
        listDialogPopup.setButtons(resources.getString(R.string.button_cancel)) {}
        listDialogPopup.setGravityBottom(false)
        listDialogPopup.show()
    }

    private fun showInsertContactDialog() {

        labels = ArrayList()
        labels.add(labelInsert)
        labels.add(labelDelete)

        listDialogPopup = ListDialogPopup(currentActivity, R.style.PopupTheme)
        listDialogPopup.setCancelable(false)
        listDialogPopup.setMessage(resources.getString(R.string.rubrica_message_dialog))
        listDialogPopup.setLabels(labels, this)
        listDialogPopup.setButtons(resources.getString(R.string.button_cancel)) {}
        listDialogPopup.setGravityBottom(true)
        listDialogPopup.show()
    }

    private fun showInsertEditContactDialog() {

        labels = ArrayList()
        labels.add(labelEdit)
        labels.add(labelInsert)
        labels.add(labelOpen)
        labels.add(labelDelete)

        listDialogPopup = ListDialogPopup(currentActivity, R.style.PopupTheme)
        listDialogPopup.setCancelable(false)
        listDialogPopup.setMessage(resources.getString(R.string.rubrica_message_dialog))
        listDialogPopup.setLabels(labels, this)
        listDialogPopup.setButtons(resources.getString(R.string.button_cancel)) {}
        listDialogPopup.setGravityBottom(true)
        listDialogPopup.show()
    }

    override fun onClick(view: View?) {

        label = requireView().tag as String
        listDialogPopup.dismiss()

        when (label) {
            labelEdit -> {
                editContact(selectedContactUri)
            }
            labelInsert -> {
                insertContact()
            }
            labelOpen -> {
                insertEditContact(selectedContactUri)
            }
            else -> {
                if (label != labelDelete) {
                    callContact(requireContext(), label)
                }
            }
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

        intent.apply {
            setDataAndType(contactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
        }

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
            putExtra(ContactsContract.Intents.Insert.NAME, user_name.text.toString())

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