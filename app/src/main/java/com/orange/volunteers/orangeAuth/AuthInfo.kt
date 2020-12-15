package com.orange.volunteers.orangeAuth

import net.openid.appauth.AuthState

internal data class AuthInfo(
    var serviceId: String? = null,
    var authState: AuthState? = null,
    var tokenEndpointUrl: String? = null,
    var registrationEndpointUrl: String? = null,
    var authorizationEndpointUrl: String? = null,
    var discoveryUrl: String? = null,
    var clientId: String? = null,
    var redirectUrl: String? = null,
    var scope: String? = null,
    var requestUserInfo: Boolean = false,
    var userInfoUrl: String? = null
) {
    companion object {
        const val SERVICE_ID_SMARTHUB = "orangeService"
    }
}