package com.tjEnterprises.phase10Counter.data.network.repositories

import com.tjEnterprises.phase10Counter.BuildConfig
import com.tjEnterprises.phase10Counter.data.network.services.GetLatestReleaseService
import javax.inject.Inject

object UpdateCheckerCodes {
    const val ERROR_GETTING_LATEST_VERSION_NUMBER: Int = -1
    const val NO_RESPONSE: Int = -2
}

interface UpdateCheckerRepository {

    suspend fun getLatestReleaseVersionNumber(): Int

    class UpdateCheckerRepositoryImpl @Inject constructor(private val getLatestReleaseService: GetLatestReleaseService) :
        UpdateCheckerRepository {
        override suspend fun getLatestReleaseVersionNumber(): Int {
            return if (BuildConfig.BUILD_TYPE != "release") {
                try {
                    getLatestReleaseService.getLatestReleaseTag().tagName.filter { it.isDigit() }
                        .toInt()
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    UpdateCheckerCodes.ERROR_GETTING_LATEST_VERSION_NUMBER
                }
            } else {
                UpdateCheckerCodes.ERROR_GETTING_LATEST_VERSION_NUMBER
            }
        }
    }
}