package com.orange.volunteers.auth

import android.content.Context
import android.content.SharedPreferences
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.util.SingletonHolder
import net.openid.appauth.AuthState


class TokenManager private constructor(private val context: Context) {

    private val KEY_STATE = "state"
    private  val PREFS_NAME = "config"
    private  val KEY_LAST_HASH = "lastHash"
    val authStateManager = AuthStateManager.getInstance(context)


    fun signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
//        val currentState = authStateManager.current
//        val clearedState = AuthState(currentState.authorizationServiceConfiguration!!)
//        if (currentState.lastRegistrationResponse != null) {
//            clearedState.update(currentState.lastRegistrationResponse)
//        }
//        authStateManager.replace(clearedState)
//

        didUserLogOut = true

    }


    companion object : SingletonHolder<TokenManager, Context>(::TokenManager) {
        var didUserLogOut = false
    }
}