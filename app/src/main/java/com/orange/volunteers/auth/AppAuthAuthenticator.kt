package com.orange.volunteers.auth

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.orange.volunteers.orangeAuth.OrangeTokenManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.CompletableFuture

class AppAuthAuthenticator(private val tokenManager: OrangeTokenManager
) : Authenticator {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun authenticate(route: Route?, response: Response): Request? {
        val future = CompletableFuture<Request?>()

        tokenManager.authState.performActionWithFreshTokens(tokenManager.authService) { accessToken, _, ex ->
            if (ex != null) {
                Log.e("AppAuthAuthenticator", "Failed to authorize = $ex")
            }

            if (response.request.header("Authorization") != null) {
                future.complete(null) // Give up, we've already failed to authenticate.
            }

            val response = response.request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()

            future.complete(response)
        }

        return future.get()
    }

}