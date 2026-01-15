package com.example.landmarkrecognitioner

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaApi {
    @GET("api/rest_v1/page/summary/{title}")
    suspend fun getPageSummary(
        @Path("title") title: String
    ): Response<WikipediaSummary>
}