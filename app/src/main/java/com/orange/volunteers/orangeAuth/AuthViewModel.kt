package com.orange.volunteers.orangeAuth

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.orange.data.service.user.UserResponseDTO
import com.orange.domain.model.UserResponseModel
import com.orange.volunteers.*
import com.orange.volunteers.R
import com.orange.volunteers.auth.Configuration
import com.orange.volunteers.user.UserInfoApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.openid.appauth.*
import okio.Buffer
import okio.buffer
import okio.source
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.*

internal class AuthViewModel(app: Application) : BaseViewModel(app) {

    companion object {
        const val LOG_TAG = "AuthViewModel"
        private const val REQUEST_CODE_AUTHORIZATION = 0x0815
        lateinit var authorizationService: AuthorizationService
    }

    var loginListener: LoginListener? = null
    var tokenRefreshListener: TokenRefreshListener? = null
    private val tokenManager: OrangeTokenManager by lazy {
        OrangeTokenManager.getInstance(app)
    }

    private lateinit var authorizationRequest: AuthorizationRequest
    private var refreshSmarthubTokenIsExecuting = false
    private var activeAuthInfo: AuthInfo? = null
    private var resultUserInfo: UserResponseDTO? = null
    private lateinit var serviceConfiguration: AuthorizationServiceConfiguration
    private var userInfoUrl: String? = null
    private val authConfigFileId = R.raw.auth_config_dev

    val configSource = app.resources.openRawResource(authConfigFileId)
        .source()
        .buffer()
    private val configData = Buffer()
    private val configJson = try {
        configSource.readAll(configData)
        JSONObject(configData.readString(Charset.forName("UTF-8")))
    } catch (ex: java.io.IOException) {
        throw Configuration.InvalidConfigurationException(
            "Failed to read configuration: " + ex.message
        )
    } catch (ex: JSONException) {
        throw Configuration.InvalidConfigurationException(
            "Unable to parse configuration: " + ex.message
        )
    }
    private fun initServiceConfiguration(activity: Activity) {
        authorizationService = AuthorizationService(app)
        activeAuthInfo = AuthInfo().apply {
            clientId = configJson.getString("client_id")
            authorizationEndpointUrl = configJson.getString("authorization_endpoint_uri")
            tokenEndpointUrl = configJson.getString("token_endpoint_uri")
            registrationEndpointUrl = configJson.getString("registration_endpoint_uri")
            redirectUrl = configJson.getString("redirect_uri")
            scope = configJson.getString("authorization_scope")
            discoveryUrl = configJson.getString("discovery_uri")
            serviceId = AuthInfo.SERVICE_ID_SMARTHUB
            authState = tokenManager.authState
            requestUserInfo = true
            userInfoUrl = configJson.getString("user_info_endpoint_uri")
        }

        AuthorizationServiceConfiguration.fetchFromUrl(Uri.parse(activeAuthInfo?.discoveryUrl)) {
                serviceConfig: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
            if (serviceConfig != null) {
                Log.d(LOG_TAG, "discovery request finished")
                serviceConfiguration = serviceConfig
                activeAuthInfo?.authState = AuthState(serviceConfig)
//                userInfoUrl = serviceConfig.discoveryDoc?.userinfoEndpoint
                userInfoUrl = "https://www-dev.orange.ro/csr-backend/"
                requestAuthorizationCode( activity)
            } else {
                Log.i(
                    "TAG",
                    "Creating auth config from res/raw/auth_config.json"
                )

            }
        }
    }

    private fun initAuthorizationRequest() {
        val params = HashMap<String, String>()
        params["access_type"] = "offline"
        authorizationRequest = AuthorizationRequest.Builder(
            activeAuthInfo!!.authState!!.authorizationServiceConfiguration!!,
            activeAuthInfo!!.clientId!!,
            ResponseTypeValues.CODE,
            Uri.parse(activeAuthInfo!!.redirectUrl)
        )
            .setScope(activeAuthInfo!!.scope)
            .setAdditionalParameters(params)
            .setPrompt("login")
            .build()

    }

    fun login(activity: Activity, loginListener: LoginListener?) {
        resultUserInfo = null
        val serviceConfiguration = activeAuthInfo?.authState?.authorizationServiceConfiguration
        this.loginListener = loginListener
        if (activeAuthInfo?.authState?.authorizationServiceConfiguration == null) {
            initServiceConfiguration(activity)
        } else {
            serviceConfiguration?.let { requestAuthorizationCode(activity) }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun createServiceTokenRequest(authState: AuthState, scope: String): TokenRequest {
        val lastTokenResponse = authState.lastTokenResponse
        val refreshToken = authState.refreshToken

        require(!(refreshToken == null || lastTokenResponse == null)) {
            "authorization state is not ready to request service tokens"
        }

        val params = HashMap<String, String>()
        params["access_type"] = "offline"

        return TokenRequest.Builder(
            lastTokenResponse.request.configuration,
            lastTokenResponse.request.clientId
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setScope(scope)
            .setRefreshToken(refreshToken)
            .setAdditionalParameters(params)
            .build()
    }

    private fun requestAuthorizationCode(activity: Activity) {
        initAuthorizationRequest()
        val builder: CustomTabsIntent.Builder = authorizationService.createCustomTabsIntentBuilder(authorizationRequest.redirectUri)

        val authIntent: Intent = authorizationService.getAuthorizationRequestIntent(
            authorizationRequest,
            builder.build()
        )

        activity.startActivityForResult(authIntent, REQUEST_CODE_AUTHORIZATION)
    }

    /**
     * Exchanges the code, for the [TokenResponse].
     *
     * @param intent represents the [Intent] from the Custom Tabs or the System Browser.
     */
    fun handleAuthorizationActivityResult(requestCode: Int, data: Intent): Boolean {
        if (requestCode == REQUEST_CODE_AUTHORIZATION) {
            if (activeAuthInfo != null) {
                val resp = AuthorizationResponse.fromIntent(data)
                val ex = AuthorizationException.fromIntent(data)
                activeAuthInfo?.authState?.update(resp, ex)
                if (resp != null) {
                    Log.d(LOG_TAG, "Authorization finished")
                    requestTokensAfterAuthorization(resp)
                } else {
                    if (ex != null) {
                        Log.e(LOG_TAG, "error while authorization:${ex.errorDescription}".trimIndent())
                        if (ex.type == AuthorizationException.TYPE_GENERAL_ERROR && ex.code == 1) {
                            notifyCanceled()
                            return true
                        }
                    }
                    ex?.let {
                        notifyFailed(it)
                    }
                }
            } else {
                notifyFailed(java.lang.IllegalArgumentException("activeAuthInfo is not set"))
            }
            return true
        }
        return false
    }

    private fun requestTokensAfterAuthorization(authorizationResponse: AuthorizationResponse) {
            authorizationService.performTokenRequest(
            authorizationResponse.createTokenExchangeRequest()
            ) { resp, ex ->
                activeAuthInfo!!.authState?.update(resp, ex)
                if (resp != null) {
                    Log.d(LOG_TAG, "Token request finished")
                    activeAuthInfo!!.authState!!.update(resp, ex)
                    refreshSmarthubTokenIsExecuting = false
                    tokenManager.authState = activeAuthInfo!!.authState!!

                    Log.d(LOG_TAG, "Service Token refresh finished")
                    Log.d(LOG_TAG, "Access token = ${tokenManager.authState.accessToken}")

                    tokenManager.authState.needsTokenRefresh
                    tokenManager.authState.performActionWithFreshTokens(tokenManager.authService) { newAccessToken, _, ex ->
                        if (ex != null) {
                            Log.e("AppAuthAuthenticator", "Failed to authorize = $ex")
                        }
                        Log.d(LOG_TAG, "New access token = $newAccessToken")
                        Log.d(LOG_TAG, "Exception = ${ex}")
                    }
                    tokenRefreshListener?.onTokenRefreshSuccess()

                    //                    if (activeAuthInfo?.requestUserInfo == true) {
//                    tokenRefreshListener?.let {
//                        requestNewAccessToken(activeAuthInfo!!.authState!!, it)
//                    }
                    //                        requestUserInfo()
                    //                    }
                    //                else {
                    //                        notifySuccess(activeAuthInfo!!)
                    //                        notifySuccess(activeAuthInfo!!, resultUserInfo!!)
                    //                    }
                } else {
                    if (ex != null) {
                        Log.e(
                            LOG_TAG,
                            "error while service token refresh:${ex.errorDescription}".trimIndent()
                        )
                    }
                    tokenRefreshListener?.onTokenRefreshFailed(ex!!)
                    tokenRefreshListener = null
                    notifyFailed(ex!!)
                }
            }
    }

    private fun requestNewAccessToken(authState: AuthState, tokenListener: TokenRefreshListener) {
        tokenRefreshListener = tokenListener
        if (!refreshSmarthubTokenIsExecuting) {
            refreshSmarthubTokenIsExecuting = true
            val serviceTokenRequest: TokenRequest
            try {
                val scope = "openid profile email phone"
                serviceTokenRequest = createServiceTokenRequest(authState, scope)
            } catch (e: Throwable) {
                tokenRefreshListener?.onTokenRefreshFailed(e)
                refreshSmarthubTokenIsExecuting = false
                return
            }
            authState.performActionWithFreshTokens(
                authorizationService
            ) { accessToken, idToken, ex ->
                Log.v("ACTION", "$accessToken ")
                Log.v("IDTOKEN", " $idToken")
                Log.v("EX", " $ex")
            }
            authorizationService.performTokenRequest(
                serviceTokenRequest
            ) { response, ex ->
                authState.update(response, ex)
                refreshSmarthubTokenIsExecuting = false
                tokenManager.authState = authState
                if (response != null) {
                    Log.d(LOG_TAG, "Service Token refresh finished")
                    tokenRefreshListener?.onTokenRefreshSuccess()
                } else {
                    if (ex != null) {
                        Log.e(
                            LOG_TAG,
                            "Error while service token refresh:${ex.errorDescription}".trimIndent()
                        )
                    }
                    tokenRefreshListener?.onTokenRefreshFailed(ex!!)
                    tokenRefreshListener = null
                }
            }
        }
    }

     fun requestUserInfo() {
        userInfoUrl?.let {
            val baseUrl = it
            val userInfoApiClient = UserInfoApiClient(baseUrl)
            userInfoApiClient.getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val userInfo = UserResponseModel(
                            contact = it.contact,
                            locationSharingEnabled = it.locationSharingEnabled,
                            notificationsEnabled= it.notificationsEnabled,
                            role = it.role,
                            preferredDomains = it.preferredDomains,
                            location = it.location
                        )
                        tokenManager.userInfo = userInfo
                    },
                    {
                        Log.e(LOG_TAG, "${it.message}")
                    }
                )

        }
    }

    private fun notifyCanceled() {
        activeAuthInfo = null
        resultUserInfo = null
        if (loginListener != null) {
            loginListener!!.onLoginCanceled()
        }
        loginListener = null
    }

    private fun notifyFailed(e: java.lang.Exception) {
        activeAuthInfo = null
        resultUserInfo = null
        if (loginListener != null) {
            loginListener!!.onLoginFailed(e)
        }
        loginListener = null
    }

    private fun notifySuccess(resultAuthInfo: AuthInfo, userInfo: UserResponseDTO) {
        activeAuthInfo = null
        resultUserInfo = null
        if (loginListener != null) {
            loginListener!!.onLoginSuccess(resultAuthInfo, userInfo)
        }
        loginListener = null
    }

    private fun notifySuccess(resultAuthInfo: AuthInfo) {
        activeAuthInfo = null
        if (loginListener != null) {
            loginListener!!.onLoginSuccess(resultAuthInfo)
        }
        loginListener = null
    }

    interface LoginListener {
        fun onLoginCanceled()
        fun onLoginFailed(exception: Exception)
        fun onLoginSuccess(authInfo: AuthInfo, userInfo: UserResponseDTO)
        fun onLoginSuccess(authInfo: AuthInfo)
    }

    interface TokenRefreshListener {
        fun onTokenRefreshFailed(exception: Throwable)
        fun onTokenRefreshSuccess()
    }

}
