package com.orange.domain.model

data class UserResponseModel(
    val contact: UserContact?=null,
    var notificationsEnabled: Boolean = false,
    var locationSharingEnabled: Boolean = false,
    var location: UserLocation? = UserLocation(0.0, 0.0),
    @Transient val role: String?=null,//"ADMIN"
    var preferredDomains: ArrayList<DomainModel> = arrayListOf()
)