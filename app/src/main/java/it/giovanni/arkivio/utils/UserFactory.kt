package it.giovanni.arkivio.utils

import it.giovanni.arkivio.model.Link
import it.giovanni.arkivio.model.LinkSide
import kotlin.collections.ArrayList

class UserFactory {

    companion object {

        private var user: User = User()
        fun getInstance(): User {
            return user
        }
    }

    class User {

        lateinit var email: String
        var listLink : ArrayList<Link>? = null
        var listLinkSide : ArrayList<LinkSide>? = null
        var matricola : String? = null
        var isLogged = false
        var contacts: String? = null

        var objectID: String? = null
        var givenName: String? = null
        var surname: String? = null
        var displayName: String? = null
        var jobTitle: String? = null
        var mail: String? = null
        var phone: String? = null
        var mobilePhone: String? = null
        var officeLocation: String? = null
        var avatarImageURL: String? = null
        var avatarImage: ByteArray? = null
        var currentDate: String? = null
        var ext: Boolean = false
        var initials: String? = null
        var lineManagerGivenName: String? = null
        var lineManagerDisplayName: String? = null
        var lineManagerMail: String? = null
        var inpsCode: String? = null
        var prescriptionDays: String? = null
        var diseaseWaiting: Boolean = false
        var covidInfoCommunication: String? = null
        var covidMessageCommunication: String? = null
        var smartworkingSubjectMail: String? = null
        var smartworkingContentMail: String? = null

        fun clear() {
            email = ""
            listLink = null
            listLinkSide = null
            matricola = ""
            isLogged = false
            contacts = ""

            objectID = ""
            givenName = ""
            surname = ""
            displayName = ""
            jobTitle = ""
            mail = ""
            phone = ""
            mobilePhone = ""
            officeLocation = ""
            avatarImageURL = ""
            avatarImage = null
            currentDate = ""
            ext = false
            initials = ""
            lineManagerGivenName = ""
            lineManagerDisplayName = ""
            lineManagerMail = ""
            inpsCode = ""
            prescriptionDays = ""
            diseaseWaiting = false
            covidInfoCommunication = ""
            covidMessageCommunication = ""
            smartworkingSubjectMail = ""
            smartworkingContentMail = ""
        }
    }
}