package com.orange.volunteers.util

import com.orange.domain.model.DomainModel
import com.orange.domain.model.UserLocation
import com.orange.domain.model.UserResponseModel

sealed class AppRxEvents
data class EventSubscribeToCampaign(val campaignId: String) : AppRxEvents()
data class EventUnsubscribeFromEnrollment(val enrollmentId: String) : AppRxEvents()
data class EventSetUserLocation(val location: UserLocation) : AppRxEvents()
data class EventDeleteUserAccountSuccessful(val message: String) : AppRxEvents()
data class EventDeleteUserAccountFailed(val message: String) : AppRxEvents()
//data class EventDomainsUpdated(val prefferedDomains: ArrayList<DomainModel>) : AppRxEvents()