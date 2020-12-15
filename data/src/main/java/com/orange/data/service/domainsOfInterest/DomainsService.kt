package com.orange.data.service.domainsOfInterest

import com.orange.data.service.domainsOfInterest.DomainItemDTO
import io.reactivex.Single
import retrofit2.http.GET

interface DomainsService {
    @GET("ngo/activity-domain")
    fun getDomains(): Single<List<DomainItemDTO>>
}