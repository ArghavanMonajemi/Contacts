package com.example.contacts

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Upsert
    suspend fun upsertContact(contact: Contact)

    @Query(value = "SELECT * FROM contact WHERE id = :id")
    fun getContactById(id: Long): Contact?

    @Query(value = "SELECT * FROM contact ORDER BY firstName ASC")
    fun getContactOrderByFirstName(): Flow<List<Contact>>

    @Query(value = "SELECT * FROM contact ORDER BY lastName ASC")
    fun getContactOrderByLastName(): Flow<List<Contact>>

    @Query(value = "SELECT * FROM contact ORDER BY number ASC")
    fun getContactOrderByPhoneNumber(): Flow<List<Contact>>

    @Delete
    suspend fun deleteContact(contact: Contact)
}