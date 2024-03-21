package com.tjEnterprises.phase10Counter.data.network.services

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

data class LatestReleaseResponse (
    @SerializedName("tag_name")
    val tagName: String
)

interface GetLatestReleaseService {
    @GET("latest")
    suspend fun getLatestReleaseTag() : LatestReleaseResponse
}