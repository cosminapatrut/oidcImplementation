package com.orange.volunteers.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.orange.volunteers.AuthActivity
import com.orange.volunteers.R
import net.openid.appauth.*
import okio.buffer
import okio.source
import org.joda.time.format.DateTimeFormat
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

class TokenActivity : AppCompatActivity() {
    private var authService: AuthorizationService? = null
    private var authStateManager: AuthStateManager? = null
    private val userInfoJson =
        AtomicReference<JSONObject?>()
    private var executor: ExecutorService? = null
    private var authConfiguration: Configuration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authStateManager = AuthStateManager.getInstance(this)
        executor = Executors.newSingleThreadExecutor()
        authConfiguration = Configuration.getInstance(this)
        val config =
            Configuration.getInstance(this)
        if (config.hasConfigurationChanged()) {
            Toast.makeText(
                this,
                "Configuration change detected",
                Toast.LENGTH_SHORT
            )
                .show()
            signOut()
            return
        }
        authService = AuthorizationService(
            this,
            AppAuthConfiguration.Builder()
                .build()
        )
//        setContentView(R.layout.activity_token)
        displayLoading("Restoring state...")
        if (savedInstanceState != null) {
            try {if(savedInstanceState.getString(KEY_USER_INFO) != null )
                userInfoJson.set(JSONObject(savedInstanceState.getString(KEY_USER_INFO)))
            } catch (ex: JSONException) {
                Log.e(
                    TAG,
                    "Failed to parse saved user info JSON, discarding",
                    ex
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (executor!!.isShutdown) {
            executor = Executors.newSingleThreadExecutor()
        }
        if (authStateManager!!.current.isAuthorized) {
            displayAuthorized()
            return
        }

        // the stored AuthState is incomplete, so check if we are currently receiving the result of
        // the authorization flow from the browser.
        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)
        if (response != null || ex != null) {
            authStateManager!!.updateAfterAuthorization(response, ex)
        }
        if (response != null && response.authorizationCode != null) {
            // authorization code exchange is required
            authStateManager!!.updateAfterAuthorization(response, ex)
            exchangeAuthorizationCode(response)
        } else if (ex != null) {
            displayNotAuthorized("Authorization flow failed: " + ex.message)
        } else {
            displayNotAuthorized("No authorization state retained - reauthorization required")
        }
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        // user info is retained to survive activity restarts, such as when rotating the
        // device or switching apps. This isn't essential, but it helps provide a less
        // jarring UX when these events occur - data does not just disappear from the view.
        if (userInfoJson.get() != null) {
            state.putString(KEY_USER_INFO, userInfoJson.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authService!!.dispose()
        executor!!.shutdownNow()
    }

    @MainThread
    private fun displayNotAuthorized(explanation: String) {
//        findViewById<View>(R.id.not_authorized).visibility = View.VISIBLE
//        findViewById<View>(R.id.authorized).visibility = View.GONE
//        findViewById<View>(R.id.loading_container).visibility = View.GONE
//        (findViewById<View>(R.id.explanation) as TextView).text = explanation
//        findViewById<View>(R.id.reauth).setOnClickListener { view: View? -> signOut() }
    }

    @MainThread
    private fun displayLoading(message: String) {
//        findViewById<View>(R.id.loading_container).visibility = View.VISIBLE
//        findViewById<View>(R.id.authorized).visibility = View.GONE
//        findViewById<View>(R.id.not_authorized).visibility = View.GONE
//        (findViewById<View>(R.id.loading_description) as TextView).text = message
    }

    @MainThread
    private fun displayAuthorized() {
//        findViewById<View>(R.id.authorized).visibility = View.VISIBLE
//        findViewById<View>(R.id.not_authorized).visibility = View.GONE
//        findViewById<View>(R.id.loading_container).visibility = View.GONE
        val state = authStateManager!!.current
//        val refreshTokenInfoView =
//            findViewById<View>(R.id.refresh_token_info) as TextView
//        if (state.refreshToken == null)
//        refreshTokenInfoView.setText(
//            R.string.no_refresh_token_returned )
//        else  {
//            refreshTokenInfoView.setText("Refresh token: ${state.refreshToken}")
//        }
//            R.string.refresh_token_returned)
//        val idTokenInfoView =
//            findViewById<View>(R.id.id_token_info) as TextView
//        idTokenInfoView.setText(if (state.idToken == null) R.string.no_id_token_returned else R.string.id_token_returned)
//        val accessTokenInfoView =
//            findViewById<View>(R.id.access_token_info) as TextView
//        if (state.accessToken == null) {
//            accessTokenInfoView.setText(R.string.no_access_token_returned)
//        } else {
//            val expiresAt = state.accessTokenExpirationTime
//            if (expiresAt == null) {
//                accessTokenInfoView.setText(R.string.no_access_token_expiry)
//            } else if (expiresAt < System.currentTimeMillis()) {
//                accessTokenInfoView.setText(R.string.access_token_expired)
//            } else {
//                val template =
//                    resources.getString(R.string.access_token_expires_at)
//                accessTokenInfoView.text = String.format(
//                    template,
//                    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss ZZ").print(expiresAt)
//                )
//            }
//        }
//        val refreshTokenButton =
//            findViewById<View>(R.id.refresh_token) as Button
//        refreshTokenButton.visibility = if (state.refreshToken != null) View.VISIBLE else View.GONE
//        refreshTokenButton.setOnClickListener { view: View? -> refreshAccessToken() }
//        val viewProfileButton =
//            findViewById<View>(R.id.view_profile) as Button
//        val discoveryDoc =
//            state.authorizationServiceConfiguration!!.discoveryDoc
//        if ((discoveryDoc == null || discoveryDoc.userinfoEndpoint == null)
//            && authConfiguration!!.userInfoEndpointUri == null
//        ) {
//            viewProfileButton.visibility = View.GONE
//        } else {
//            viewProfileButton.visibility = View.VISIBLE
//            viewProfileButton.setOnClickListener { view: View? -> fetchUserInfo() }
//        }
//        (findViewById<View>(R.id.sign_out) as Button).setOnClickListener { view: View? -> signOut() }
//        val userInfoCard = findViewById<View>(R.id.userinfo_card)
//        val userInfo = userInfoJson.get()
//        if (userInfo == null) {
//            userInfoCard.visibility = View.INVISIBLE
//        } else {
//            try {
//                var name: String? = "???"
//                if (userInfo.has("name")) {
//                    name = userInfo.getString("name")
//                }
//                (findViewById<View>(R.id.userinfo_name) as TextView).text = name
//                if (userInfo.has("picture")) {
//                    Glide.with(this@TokenActivity)
//                        .load(Uri.parse(userInfo.getString("picture")))
//                        .fitCenter()
//                        .into((findViewById<View>(R.id.userinfo_profile) as ImageView))
//                }
//                (findViewById<View>(R.id.userinfo_json) as TextView).text = userInfoJson.toString()
//                userInfoCard.visibility = View.VISIBLE
//            } catch (ex: JSONException) {
//                Log.e(TAG, "Failed to read userinfo JSON", ex)
//            }
//        }
        finish()
    }

    @MainThread
    private fun refreshAccessToken() {
        displayLoading("Refreshing access token")
        performTokenRequest(
            authStateManager!!.current.createTokenRefreshRequest(),
            AuthorizationService.TokenResponseCallback { tokenResponse: TokenResponse?, authException: AuthorizationException? ->
                handleAccessTokenResponse(
                    tokenResponse,
                    authException
                )

                val accessTokenInfoView =findViewById<View>(R.id.access_token_info) as TextView
                accessTokenInfoView.text = "Access token : $tokenResponse"

            }
        )

    }

    @MainThread
    private fun exchangeAuthorizationCode(authorizationResponse: AuthorizationResponse) {
        displayLoading("Exchanging authorization code")
        performTokenRequest(
            authorizationResponse.createTokenExchangeRequest(),
            AuthorizationService.TokenResponseCallback { tokenResponse: TokenResponse?, authException: AuthorizationException? ->
                handleCodeExchangeResponse(
                    tokenResponse,
                    authException
                )
            }
        )
    }

    @MainThread
    private fun performTokenRequest(
        request: TokenRequest,
        callback: AuthorizationService.TokenResponseCallback
    ) {
        val clientAuthentication: ClientAuthentication
        clientAuthentication = try {
            authStateManager!!.current.clientAuthentication
        } catch (ex: ClientAuthentication.UnsupportedAuthenticationMethod) {
            Log.d(
                TAG,
                "Token request cannot be made, client authentication for the token "
                        + "endpoint could not be constructed (%s)",
                ex
            )
            displayNotAuthorized("Client authentication method is unsupported")
            return
        }
        authService!!.performTokenRequest(
            request,
            clientAuthentication,
            callback
        )
    }

    @WorkerThread
    private fun handleAccessTokenResponse(
        tokenResponse: TokenResponse?,
        authException: AuthorizationException?
    ) {
        authStateManager!!.updateAfterTokenResponse(tokenResponse, authException)
        runOnUiThread { displayAuthorized() }
    }

    @WorkerThread
    private fun handleCodeExchangeResponse(
        tokenResponse: TokenResponse?,
        authException: AuthorizationException?
    ) {
        authStateManager!!.updateAfterTokenResponse(tokenResponse, authException)
        if (!authStateManager!!.current.isAuthorized) {
            val message = ("Authorization Code exchange failed"
                    + if (authException != null) authException.error else "")

            // WrongThread inference is incorrect for lambdas
            runOnUiThread { displayNotAuthorized(message) }
        } else {
            runOnUiThread { displayAuthorized() }
        }
    }

    /**
     * Demonstrates the use of [AuthState.performActionWithFreshTokens] to retrieve
     * user info from the IDP's user info endpoint. This callback will negotiate a new access
     * token / id token for use in a follow-up action, or provide an error if this fails.
     */
    @MainThread
    private fun fetchUserInfo() {
        displayLoading("Fetching user info")
        authStateManager!!.current.performActionWithFreshTokens(
            authService!!
        ) { accessToken: String?, idToken: String?, ex: AuthorizationException? ->
            this.fetchUserInfo(
                accessToken,
                idToken,
                ex
            )
        }
    }

    @MainThread
    private fun fetchUserInfo(
        accessToken: String?,
        idToken: String?,
        ex: AuthorizationException?
    ) {
        if (ex != null) {
            Log.e(
                TAG,
                "Token refresh failed when fetching user info"
            )
            userInfoJson.set(null)
            runOnUiThread { displayAuthorized() }
            return
        }
        val discovery = authStateManager!!.current
            .authorizationServiceConfiguration!!.discoveryDoc
        val userInfoEndpoint: URL
        userInfoEndpoint = try {
//            if (mConfiguration!!.userInfoEndpointUri != null)
                URL(authConfiguration!!.userInfoEndpointUri.toString())
//            else
//                URL(discovery!!.userinfoEndpoint.toString())
        } catch (urlEx: MalformedURLException) {
            Log.e(
                TAG,
                "Failed to construct user info endpoint URL",
                urlEx
            )
            userInfoJson.set(null)
            runOnUiThread { displayAuthorized() }
            return
        }
        executor!!.submit {
            try {
                val conn =
                    userInfoEndpoint.openConnection() as HttpURLConnection
                conn.setRequestProperty("Authorization", "Bearer $accessToken")
                conn.instanceFollowRedirects = false
                val response = conn.inputStream.source().buffer()
                    .readString(Charset.forName("UTF-8"))
                userInfoJson.set(JSONObject(response))
            } catch (ioEx: IOException) {
                Log.e(
                    TAG,
                    "Network error when querying userinfo endpoint",
                    ioEx
                )
                showSnackbar("Fetching user info failed")
            } catch (jsonEx: JSONException) {
                Log.e(TAG, "Failed to parse userinfo response")
                showSnackbar("Failed to parse user info")
            }
            runOnUiThread { displayAuthorized() }
        }
    }

    @MainThread
    private fun showSnackbar(message: String) {
        Snackbar.make(
            findViewById(R.id.coordinator),
            message,
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

    @MainThread
     fun signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        val currentState = authStateManager!!.current
        val clearedState = AuthState(currentState.authorizationServiceConfiguration!!)
        if (currentState.lastRegistrationResponse != null) {
            clearedState.update(currentState.lastRegistrationResponse)
        }
        authStateManager!!.replace(clearedState)
        val mainIntent = Intent(this, AuthActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(mainIntent)
        finish()
    }

    companion object {
        private const val TAG = "TokenActivity"
        private const val KEY_USER_INFO = "userInfo"
    }
}