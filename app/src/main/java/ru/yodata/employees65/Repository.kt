package ru.yodata.employees65

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.yodata.employees65.api.API

object Repository {
    private var retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    private var API: API =
        retrofit.create(ru.yodata.employees65.api.API::class.java)

    suspend fun getData() = API.getData()
}