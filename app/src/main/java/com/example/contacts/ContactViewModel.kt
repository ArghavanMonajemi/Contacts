package com.example.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(private val dao: ContactDao) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _state = MutableStateFlow(ContactState())

    private val _contacts = _sortType.flatMapLatest { sortType ->
        when (sortType) {
            SortType.FIRST_NAME -> dao.getContactOrderByFirstName()
            SortType.LAST_NAME -> dao.getContactOrderByLastName()
            SortType.PHONE_NUMBER -> dao.getContactOrderByPhoneNumber()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.deleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }

            ContactEvent.hideDialog -> {
                _state.update { it.copy(isAddingContact = false) }
            }

            ContactEvent.saveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber
                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    return
                }
                val contact =
                    Contact(firstName = firstName, lastName = lastName, number = phoneNumber)
                viewModelScope.launch {
                    dao.upsertContact(contact)
                }
                _state.update {
                    it.copy(
                        isAddingContact = false,
                        firstName = "",
                        lastName = "",
                        phoneNumber = ""
                    )
                }
            }

            is ContactEvent.setFirstName -> {
                _state.update { it.copy(firstName = event.firstName) }
            }

            is ContactEvent.setLastName -> {
                _state.update { it.copy(lastName = event.lastName) }
            }

            is ContactEvent.setPhoneNumber -> {
                _state.update { it.copy(phoneNumber = event.phoneNumber) }
            }

            ContactEvent.showDialog -> {
                _state.update { it.copy(isAddingContact = true) }
            }

            is ContactEvent.sortContacts -> {
                _sortType.value = event.sortType
            }
        }
    }
}