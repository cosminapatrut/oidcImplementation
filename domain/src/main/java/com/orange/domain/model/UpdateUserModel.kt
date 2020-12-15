package com.orange.domain.model

data class UpdateUserModel(
    var domainsOfInterest: List<String?> = arrayListOf(),
    var notificationsEnabled: Boolean = false,
    var locationSharingEnabled: Boolean = false,
    var gpsLocation: UserLocation = UserLocation(0.0, 0.0)
)