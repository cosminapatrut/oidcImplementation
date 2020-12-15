package com.orange.volunteers.auth

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.facebook.stetho.inspector.elements.ShadowDocument
import com.orange.volunteers.R
import com.orange.volunteers.orangeAuth.OrangeTokenManager
import net.openid.appauth.TokenRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.CompletableFuture

/**
 * Retries failed requests with 401 Unauthorized due to an issue with the access token we provided.
 */
class TokenRefreshAuthenticator(
    private val tokenManager: OrangeTokenManager
) : Authenticator {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun authenticate(route: Route?, response: Response): Request? {
        return when {
            // If the request is the one for the login URL, do no retry
            isFailedLoginRequest(response) ->
            {
                null
            }
            // If the request has already been retried once we will simply allow it to fail to avoid an infinite loop
            response.retryCount > 1 -> {
                tokenManager.clearToken()
                tokenManager.clearUserInfo()
                null
            }
            else -> {
                if (isUnauthorizedRequest(response)) {
                    Log.v("AUTH", "You have to login again")
                    response.createSignedRequest()
                }
                else
                    null
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun Response.createSignedRequest(): Request? {
        return try {

            var newToken = ""
//            val future = CompletableFuture<Request?>()
//
//            tokenManager.authState.needsTokenRefresh
            tokenManager.authState.performActionWithFreshTokens(tokenManager.authService) { accessToken, _, ex ->
                if (ex != null) {
                    Log.e("AppAuthAuthenticator", "Failed to authorize = $ex")
                }

//                if (response.request.header("Authorization") != null) {
//                    future.complete(null) // Give up, we've already failed to authenticate.
//                }

//                val response = response.request.newBuilder()
//                    .removeHeader("Authorization")
//                    .header("Authorization", "Bearer $accessToken")
//                    .build()
//
//                future.complete(response)

                if (accessToken != null) {
                    newToken = accessToken
//                    request.signWithToken(accessToken)
                } else {
                    Log.e("AppAuthAuthenticator", "New access token is NULL")
                }
            }
//            return future.get()
            request.signWithToken(newToken)


        } catch (error: Throwable) {
            Log.e("Token refresh Auth ", "${error.message}")
            null
        }
    }

    private val Response.retryCount: Int
        get() {
            var currentResponse = priorResponse
            var result = 0
            while (currentResponse != null) {
                result++
                currentResponse = currentResponse.priorResponse
            }
            return result
        }

    private fun isFailedLoginRequest(response: Response): Boolean {
        return response.code == 401 && response.request.url.encodedPath == "/auth"
    }

    private fun isUnauthorizedRequest(response: Response): Boolean {
        return response.code == 401
    }
}

fun Request.signWithToken(token: String): Request =
    newBuilder()
        .removeHeader("Authorization")
        .header("Authorization", "Bearer $token")
        .build()

