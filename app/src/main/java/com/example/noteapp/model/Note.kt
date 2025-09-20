package com.example.noteapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes_table")
@Parcelize
data class Note (

    @PrimaryKey(autoGenerate = true)
    val id : Int,
    val title : String,
    val content : String,
    val time: String
) : Parcelable