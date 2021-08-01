package com.example.nutritrack.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun stringToList_INT(input: String): List<Int> {
        return gson.fromJson(input, (object : TypeToken<List<Int>>() {}).type)
    }

    @TypeConverter
    fun listToString_INT(input: List<Int>): String {
        return gson.toJson(input)
    }

    @TypeConverter
    fun stringToList_FLOAT(input: String): List<Float> {
        return gson.fromJson(input, (object : TypeToken<List<Float>>() {}).type)
    }

    @TypeConverter
    fun listToString_FLOAT(input: List<Float>): String {
        return gson.toJson(input)
    }

    @TypeConverter
    fun stringToList_STRING(input: String): List<String> {
        return gson.fromJson(input, (object : TypeToken<List<String>>() {}).type)
    }

    @TypeConverter
    fun listToString_STRING(input: List<String>): String {
        return gson.toJson(input)
    }

}