package com.example.contacts

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Upsert
    fun upsertContact(contact: Contact)

    @Query(value = "SELECT * FROM contact WHERE id = :id")
    fun getContactById(id: Long): Contact?

    @Query(value = "SELECT * FROM contact ORDER BY :sortType ASC")
    fun getContactOrderByFirstName(sortType: SortType): Flow<List<Contact>>

    @Delete
    fun deleteContact(contact: Contact)
}