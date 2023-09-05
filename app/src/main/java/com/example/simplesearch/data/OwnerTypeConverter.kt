package com.example.simplesearch.data

import androidx.room.TypeConverter
import com.google.gson.Gson

class OwnerTypeConverter {

    @TypeConverter
    fun toOwner(value: String): Owner {
        return Gson().fromJson(value, Owner::class.java)
    }

    @TypeConverter
    fun fromOwner(value: Owner): String {
        return Gson().toJson(value)
    }
}