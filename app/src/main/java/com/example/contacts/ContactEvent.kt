package com.example.contacts

sealed interface ContactEvent {
    object saveContact : ContactEvent
    data class setFirstName(val firstName: String) : ContactEvent
    data class setLastName(val lastName: String) : ContactEvent
    data class setPhoneNumber(val phoneNumber: String) : ContactEvent
    object showDialog : ContactEvent
    object hideDialog : ContactEvent
    data class deleteContact(val contact: Contact) : ContactEvent
    data class sortContacts(val sortType: SortType) : ContactEvent

}