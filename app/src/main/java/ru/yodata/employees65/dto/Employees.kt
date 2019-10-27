package ru.yodata.employees65.dto

import com.google.gson.annotations.SerializedName

data class Employees (
    @SerializedName("f_name") val fName : String,
    @SerializedName("l_name") val lName : String,
    @SerializedName("birthday") val birthday : String,
    @SerializedName("avatr_url") val avatarURL : String,
    @SerializedName("specialty") val specialty : List<Specialty>
)
