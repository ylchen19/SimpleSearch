package com.example.simplesearch.api

import com.example.simplesearch.data.Data
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface Service {
    @GET("/search/repositories")
    suspend fun search(
        @Query("q") queryString: String?,
        @Query("page")page: Int,
        @Query("per_page")perPage: Int
    ): Data

}