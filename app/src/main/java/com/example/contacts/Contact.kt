package com.example.contacts

import androidx.room.*

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val firstName: String,
    val lastName: String,
    val number: String
)
