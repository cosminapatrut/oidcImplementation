package com.orange.volunteers.campaigns.details

import android.util.Log
import com.orange.domain.model.UserEnrollmentsModel
import com.orange.volunteers.campaigns.details.BaseClient.Companion.apiFactory
import io.reactivex.Completable
import io.reactivex.Single

class EnrollmentClient(private val api: EnrollmentApi) {
    companion object {
        fun create(): EnrollmentClient {
            val factory = apiFactory()
            return EnrollmentClient(
                api = factory.create(EnrollmentApi::class.java)
            )
        }
    }

    fun getEnrollments(isActive: Boolean = false): Single<UserEnrollmentsModel> = api.getEnrollments(isActive)
        .doOnSubscribe {
            Log.v("ENROLL GET API ", "SUCCESS")
        }
        .doOnError {
            Log.v("ENROLL GET API ", "FAILED")
        }

    fun cancelEnrollment(enrollmentId: String): Completable = api.cancelEnrollment(enrollmentId)
        .doOnSubscribe {
            Log.v("CANCEL Enrollment API ", "SUCCESS")
        }
        .doOnError {
            Log.v("CANCEL API ", "FAILED")
        }

    fun enrollToCampaign(campaignId: String): Completable = api.enrollToCampaign(campaignId)
        .doOnSubscribe {
            Log.v("ENROLL API ", "SUCCESS")
        }
        .doOnError {
            Log.v("ENROLL API ", "FAILED")
        }

}