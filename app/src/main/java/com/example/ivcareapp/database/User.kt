package com.example.ivcareapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "user_data_table")
data class User (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "subscriber_name")
    val id : Int,

    @ColumnInfo(name = "user_id")
    var name : String,

    @ColumnInfo(name = "user_email")
    var email : String,

    @ColumnInfo(name = "user_status")
    var status : String,

    @ColumnInfo(name = "user_role")
    var role : String
        )
