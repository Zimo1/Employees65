package ru.yodata.employees65.dto

import com.google.gson.annotations.SerializedName

data class PersonList (
    @SerializedName("response") val employees: List<Employees>
)