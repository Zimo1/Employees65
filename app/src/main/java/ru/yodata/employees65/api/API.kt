package ru.yodata.employees65.api

import retrofit2.Response
import retrofit2.http.*
import ru.yodata.employees65.dto.PersonList

const val GET_COMMAND = "testTask.json" //Команда на получение данных с сервера REST API

interface API {
    @GET(GET_COMMAND)
    suspend fun getData(): Response<PersonList>
}