package ru.yodata.employees65.dto

data class EmployeeCard(
    val fName : String,
    val lName : String,
    val birthday : String,
    val age: String,
    val avatarURL : String?,
    val specialty : String
)
