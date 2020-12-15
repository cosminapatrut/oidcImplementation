package com.orange.domain.model

data class UserEnrollmentsModel(
    var content: List<UserEnrollmentModel>?
)

data class UserEnrollmentModel(
    var status: UserEnrollmentStatus? = UserEnrollmentStatus.UNKNOWN,
    var uuid: String,
    var campaign: CampaignModel
)

enum class UserEnrollmentStatus(val niceName: String) {
    UNKNOWN("UNKNOWN"),
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELLED("CANCELLED"),
    WAITING("WAITING")
}