package com.orange.volunteers.orangeAuth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.orange.domain.model.UserResponseModel
import com.orange.volunteers.auth.TokenManager
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.util.PreferenceHelper
import com.orange.volunteers.util.PreferenceHelper.edit
import com.orange.volunteers.util.SingletonHolder
import io.reactivex.Single
import net.openid.appauth.*
import java.util.HashMap

class OrangeTokenManager private constructor(private val context: Context) :
    ExternalAuthService {

    companion object : SingletonHolder<OrangeTokenManager, Context>(::OrangeTokenManager) {
        private const val PREFS_AUTH_STATE = "PREFS_AUTH_STATE"
        private const val PREFS_REFRESH_TOKEN = "PREFS_REFRESH_TOKEN"
        private const val PREFS_USER_INFO = "PREFS_USER_INFO"
        private const val PREFS_LAST_RESPONSE = "PREFS_LAST_RESPONSE"
    }

    private val prefs: SharedPreferences by lazy {
        PreferenceHelper.defaultPrefs(context)
    }

    val params = HashMap<String, String>()

    var refreshToken: String = ""
        get() {
            return prefs[PREFS_REFRESH_TOKEN, ""]
        }
        set(value) {
            field = value
            prefs[PREFS_REFRESH_TOKEN] = field
        }

    var authService: AuthorizationService = AuthorizationService(context)

    var authState: AuthState = AuthState()
        get() {
            params["access_type"] = "offline"
            val refreshToken = refreshToken
            val authStateString: String? = prefs.getString(PREFS_AUTH_STATE, null)
            if (refreshToken.isNullOrBlank() || authStateString.isNullOrBlank())
                return AuthState()
//            val savedTokenResponse = TokenResponse.jsonDeserialize(authStateString)
//
//            val restoredTokenResponse = TokenResponse.Builder(savedTokenResponse.request)
//                .setTokenType(savedTokenResponse.tokenType)
//                .setScope(savedTokenResponse.scope)
//                .setRefreshToken(refreshToken)
//                .setAccessToken(savedTokenResponse.accessToken)
//                .setAccessTokenExpirationTime(savedTokenResponse.accessTokenExpirationTime)
//                .setAdditionalParameters(params)
//                .build()
//            val authState = AuthState()
//            authState.update(restoredTokenResponse, null)

            val savedAuthState = AuthState.jsonDeserialize(authStateString)
            return savedAuthState
//            return authState
        }
        set(value) {
            field = value
            refreshToken = value.refreshToken ?: ""
            val scope="openid profile email phone"
            val lastTokenResponse = value.lastTokenResponse
            val lastAuthResponse = value.lastAuthorizationResponse
            if (lastTokenResponse != null) {
                // recreate the token response to leave out all tokens (refresh, access and id)
                val saveTokenResponse = TokenResponse.Builder(lastTokenResponse.request)
                    .setAccessToken(lastTokenResponse.accessToken)
                    .setTokenType(lastTokenResponse.tokenType)
                    .setScope(lastTokenResponse.scope)
                    .setAccessTokenExpirationTime(lastTokenResponse.accessTokenExpirationTime)
                    .setRefreshToken(lastTokenResponse.refreshToken)
                    .setAdditionalParameters(params)
                    .build()
//                prefs[PREFS_AUTH_STATE] = saveTokenResponse.jsonSerializeString()
                prefs[PREFS_AUTH_STATE] = value.jsonSerializeString()
            }
        }

//    var lastResponse

    var userInfo: UserResponseModel =
        UserResponseModel()
        set(value) {
            field = value
            prefs[PREFS_USER_INFO] = value.toJson<UserResponseModel>()
        }
        get() {
            val json = prefs[PREFS_USER_INFO, UserResponseModel()
                .toJson<UserResponseModel>()]
            return json.fromJson()
        }

    @JvmOverloads
    fun clearTokens(clearStorage: Boolean = true) {
        authState = AuthState()
        if (clearStorage) {
            refreshToken = ""
        }
    }

    override fun getTokenAsync(): Single<ExternalIdToken> {
        return Single.just(getTokenSync())
    }

    override fun getTokenSync(): ExternalIdToken {
        val externalToken = authState.accessToken
        return ExternalIdToken(
            id = "",
            externalToken = externalToken
        )
    }

    override fun getUserInfoAsync(): Single<UserResponseModel> {
        return Single.just(userInfo)
    }

    override fun getUserInfoSync(): UserResponseModel = userInfo

    override fun clearToken() {
        clearTokens()
    }

    override fun clearUserInfo() {
        userInfo = UserResponseModel()
    }

    fun isUserAuthorized(): Boolean {
        val authManager = OrangeTokenManager.getInstance(context)
        if (authManager.authState.isAuthorized
            && !TokenManager.didUserLogOut) {
//            Toast.makeText(context, "IS authorized", Toast.LENGTH_LONG).show()
            Log.i("TAG", "User is already authenticated, proceeding to home activity")
        } else {
//            Toast.makeText(context, "NOT authorized", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}

/**
 * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
 */
operator fun SharedPreferences.set(key: String, value: Any?) {
    when (value) {
        is String? -> {
            edit { it.putString(key, value) }
        }
        is Int -> {
            edit { it.putInt(key, value) }
        }
        is Boolean -> {
            edit { it.putBoolean(key, value) }
        }
        is Float -> {
            edit { it.putFloat(key, value) }
        }
        is Long -> {
            edit { it.putLong(key, value) }
        }
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}

/**
 * finds value on given key.
 * [T] is the type of value
 * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 */
inline operator fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T
        else -> throw UnsupportedOperationException("Not yet implemented")
    }
}


