package it.giovanni.arkivio.fragments.detail.rubrica

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
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
import it.giovanni.arkivio.databinding.RubricaDetailLayoutBinding
import it.giovanni.arkivio.fragments.DetailFragment
import it.giovanni.arkivio.model.DarkModeModel
import it.giovanni.arkivio.presenter.DarkModePresenter
import it.giovanni.arkivio.utils.PermissionManager
import it.giovanni.arkivio.utils.Utils
import it.giovanni.arkivio.utils.Utils.Companion.callContact
import it.giovanni.arkivio.utils.Utils.Companion.sendSimpleMail
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import java.io.ByteArrayOutputStream

class RubricaDetailFragment : DetailFragment(), View.OnClickListener, PermissionManager.PermissionListener {

    private var layoutBinding: RubricaDetailLayoutBinding? = null
    private val binding get() = layoutBinding

    private val labelEdit = "Aggiungi a contatto esistente"
    private val labelInsert = "Crea nuovo contatto"
    private val labelOpen = "Apri lista contatti"
    private val labelDelete = "Annulla"

    private var avatar: Bitmap? = null

    private lateinit var listDialogPopup: ListDialogPopup
    private lateinit var labels: ArrayList<String>
    private lateinit var contacts: ArrayList<String>
    private lateinit var label: String

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
        return NO_LAYOUT
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

    override fun onCreateBindingView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        layoutBinding = RubricaDetailLayoutBinding.inflate(inflater, container, false)

        val darkModePresenter = DarkModePresenter(this, requireContext())
        val model = DarkModeModel(requireContext())
        binding?.presenter = darkModePresenter
        binding?.temp = model

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            user = requireArguments().getSerializable(KEY_RUBRICA) as User

            val contact = user.nome + " " + user.cognome
            binding?.userName?.text = contact

            if (user.fisso != "") binding?.valueNumeroFisso?.text = user.fisso
            else binding?.numeroFissoContainer?.visibility = View.GONE

            if (user.cellulare != "") binding?.valueCellulare?.text = user.cellulare
            else binding?.cellulareContainer?.visibility = View.GONE

            if (user.emails!![0] != "") binding?.valueMail?.text = Utils.turnArrayListToString(user.emails!!)
            else binding?.emailContainer?.visibility = View.GONE

            if (user.indirizzo != "") binding?.valueIndirizzo?.text = user.indirizzo
            else binding?.indirizzoContainer?.visibility = View.GONE

            if (user.occupazione != "") binding?.valueOccupazione?.text = user.occupazione
            else binding?.occupazioneContainer?.visibility = View.GONE
        }

        if (user.nome == "Giovanni" && user.cognome == "Petito") {
            avatar = BitmapFactory.decodeResource(requireContext().resources, R.drawable.giovanni)
            val roundAvatar : Bitmap = Utils.getRoundBitmap(avatar!!, avatar?.width!!)
            binding?.icoProfile?.setImageBitmap(roundAvatar)
        } else {
            val color = requireArguments().getInt(KEY_COLOR)
            binding?.icoProfile?.setColorFilter(ResourcesCompat.getColor(context?.resources!!, color, null))
        }

        binding?.numeroFissoContainer?.setOnClickListener {
            callContact(requireContext(), binding?.valueNumeroFisso?.text.toString())
        }

        binding?.cellulareContainer?.setOnClickListener {
            callContact(requireContext(), binding?.valueCellulare?.text.toString())
        }

        binding?.emailContainer?.setOnClickListener {
            sendSimpleMail(requireContext(), binding?.valueMail?.text.toString())
        }

        detailLayoutBinding?.rubricaIcon?.visibility = View.VISIBLE
        detailLayoutBinding?.rubricaIcon?.setOnClickListener {
            askContactPermissions()
        }

        binding?.showContacts?.setOnClickListener {
            askContactsPermissions()
        }

        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun askContactPermissions() {
        checkPermissions()
        if (checkPermissions()) {
            handleContact()
            return
        }
        PermissionManager.requestPermission(requireContext(), this, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS))
    }

    private fun askContactsPermissions() {
        if (checkPermissions()) {
            showContactsDialog()
            return
        }
        PermissionManager.requestPermission(requireContext(), this, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS))
    }

    private fun checkPermissions(): Boolean {
        return PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS) &&
                PermissionManager.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS)
    }

    override fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        if (checkPermissions()) {
            handleContact()
        }
    }

    private fun handleContact() {

        val contentResolver = context?.contentResolver

        val cursor = contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "lower(" + ContactsContract.Contacts.DISPLAY_NAME + ") = lower('" + binding?.userName?.text.toString().replace("'", "''") + "')",
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

        label = view?.tag.toString()
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
            putExtra(ContactsContract.Intents.Insert.NAME, binding?.userName?.text.toString())

            putExtra(ContactsContract.Intents.Insert.PHONE, binding?.valueNumeroFisso?.text.toString())
            putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)

            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, binding?.valueCellulare?.text.toString())
            putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)

            putExtra(ContactsContract.Intents.Insert.EMAIL, binding?.valueMail?.text.toString())
            putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)

            putExtra(ContactsContract.Intents.Insert.COMPANY, binding?.valueOccupazione?.text.toString())

            putExtra(ContactsContract.Intents.Insert.POSTAL, binding?.valueIndirizzo?.text.toString())
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

    override fun onDestroyView() {
        super.onDestroyView()
        layoutBinding = null
    }
}