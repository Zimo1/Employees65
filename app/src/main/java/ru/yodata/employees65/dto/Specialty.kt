package ru.yodata.employees65.dto

import com.google.gson.annotations.SerializedName

data class Specialty (
    @SerializedName("specialty_id") val specialtyId : Int,
    @SerializedName("name") val specialtyName : String
)