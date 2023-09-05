package com.example.simplesearch.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client {

    private val base_url = "https://api.github.com/"

    private val apiManager = Retrofit.Builder()
        .baseUrl(base_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val githubConnect: Service = apiManager.create(Service::class.java)
}