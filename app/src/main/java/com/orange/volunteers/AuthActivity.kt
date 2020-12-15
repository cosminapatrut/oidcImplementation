package com.orange.volunteers

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.orange.volunteers.auth.AuthStateManager
import com.orange.volunteers.auth.Configuration
import com.orange.volunteers.login.LoginActivity
import com.orange.volunteers.login.LoginFragment
import com.orange.volunteers.register.RegisterActivity
import com.orange.volunteers.register.RegisterFragment
import net.openid.appauth.*
import net.openid.appauth.browser.AnyBrowserMatcher
import net.openid.appauth.browser.BrowserMatcher
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicReference


class AuthActivity : AppCompatActivity() {

    private val TAG = "AuthActivity"
    private val AUTH_TAG = "Authorization state"
    private val EXTRA_FAILED = "failed"
    private val RC_AUTH = 100

    private var authService: AuthorizationService? = null
    private var authStateManager: AuthStateManager? = null
    private var configuration: Configuration? = null

    private val clientId = AtomicReference<String>()
    private val authRequest = AtomicReference<AuthorizationRequest>()
    private val authIntent = AtomicReference<CustomTabsIntent>()
    private var authIntentLatch = CountDownLatch(1)
    private var executor: ExecutorService? = null
    private val usePendingIntents = false
    private val browserMatcher: BrowserMatcher = AnyBrowserMatcher.INSTANCE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_auth)
//        setupHomeRecyclerView()
//        setupDotsIndicator()
//        setupClickEvents()
//        checkLoginStatus()
    }

//    private fun checkLoginStatus() {
//        executor = Executors.newSingleThreadExecutor()
//        authStateManager = AuthStateManager.getInstance(this)
//        configuration = Configuration.getInstance(this)
//
//        if (authStateManager?.current?.isAuthorized!!
//            && !configuration!!.hasConfigurationChanged()
//        ) {
//            Log.i(TAG, "User is already authenticated, proceeding to home activity")
////            startActivity(Intent(this, TokenActivity::class.java))
//            startActivity(Intent(this, HomeActivity::class.java))
//            finish()
//            return
//        }
//
//        if (!configuration!!.isValid) {
//            return
//        }
//
//        if (configuration!!.hasConfigurationChanged()) {
//            // discard any existing authorization state due to the change of configuration
//            Log.i(TAG, "Configuration change detected, discarding old state")
//            authStateManager!!.replace(AuthState())
//            configuration!!.acceptConfiguration()
//        }
//
//        if (intent.getBooleanExtra(EXTRA_FAILED, false)) {
//            displayAuthCancelled()
//        }
//
//        executor?.submit {
//            this.initializeAppAuth()
//        }
//    }

//    private fun setupHomeRecyclerView() {
//        val linearLayoutManager = LinearLayoutManager(
//            this,
//            LinearLayoutManager.HORIZONTAL,
//            false)
//        home_horizontal_rv.layoutManager = linearLayoutManager
//
//        val homeAdapter = this?.let {
//            MainAdapter(
//                it,
//                items = mockHomeRecyclerItems()
//            )
//        }
//        home_horizontal_rv.adapter = homeAdapter
//        home_horizontal_rv.scrollToPosition(1)
//    }
//
//    private fun setupDotsIndicator() {
//        val snapHelper =  PagerSnapHelper()
//        snapHelper.attachToRecyclerView(home_horizontal_rv)
//        //Setup dots indicator
//        val circleIndicator = circle_indicator
//        circleIndicator.attachToRecyclerView(home_horizontal_rv, snapHelper)
//    }
//
//    private fun setupClickEvents() {
//        login_btn.setOnClickListener {
//            openLoginPage()
//        }
//
//        register_btn.setOnClickListener {
//            openRegisterPage()
//        }
//
//        start_auth.setOnClickListener {
//            startAuth()
////            val intent = Intent(this, OrangeAuthActivity::class.java)
////            startActivity(intent)
//        }
//
//    }
//
//    private fun mockHomeRecyclerItems(): List<com.orange.domain.model.HomeRecyclerItem> = listOf(
//        com.orange.domain.model.HomeRecyclerItem(
//            R.drawable.ic_view_pager_main,
//            "#PentruMaine",
//            "Devino voluntar in campania noastra pentru a ajuta prima linie in lupta cu COVID-19"
//        ),
//        com.orange.domain.model.HomeRecyclerItem(
//            R.drawable.ic_view_pager_main,
//            "#PentruMaine",
//            "Devino voluntar in campania noastra pentru a ajuta prima linie in lupta cu COVID-19"
//        ),
//        com.orange.domain.model.HomeRecyclerItem(
//            R.drawable.ic_view_pager_main,
//            "#PentruMaine",
//            "Devino voluntar in campania noastra pentru a ajuta prima linie in lupta cu COVID-19"
//        )
//    )

    private fun openLoginPage() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun authenticateUser() {
//        startActivity(Intent(this, AuthorizationActivity::class.java))
//        startActivityForResult(
//            Intent(context, AuthActivity::class.java),
//            LoginFragment.ORANGE_LOGIN_REQUEST_CODE
//        )
    }

    private fun openRegisterPage() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun openLoginFragment() {
        val fragmentPopped: Boolean = supportFragmentManager.popBackStackImmediate(
            "LoginFragment", 0
        )
        if(!fragmentPopped) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, LoginFragment.newInstance())
                .addToBackStack("LoginFragment")
                .commit()
        }
    }

    fun openRegisterFragment() {
        val fragmentPopped: Boolean = supportFragmentManager.popBackStackImmediate(
            "RegisterFragment", 0
        )
        if(!fragmentPopped) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, RegisterFragment.newInstance())
                .addToBackStack("RegisterFragment")
                .commit()
        }
    }

//    override fun onBackPressed() {
//        if (  supportFragmentManager.backStackEntryCount > 0) {
//            supportFragmentManager.popBackStackImmediate()
//        } else {
//            super.onBackPressed()
//        }
//    }

//    override fun onStart() {
//        super.onStart()
//        if (executor!!.isShutdown) {
//            executor = Executors.newSingleThreadExecutor()
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        executor!!.shutdownNow()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        authService?.dispose()
//    }

//    override fun onActivityResult(
//        requestCode: Int,
//        resultCode: Int,
//        data: Intent?
//    ) {
//        super.onActivityResult(requestCode, resultCode, data)
//        displayAuthOptions()
//        if (resultCode == Activity.RESULT_CANCELED) {
//            displayAuthCancelled()
//        } else {
////            val intent = Intent(this, TokenActivity::class.java)
//            val intent = Intent(this, HomeActivity::class.java)
//            intent.putExtras(data?.extras!!)
//            startActivity(intent)
//        }
//    }

//    @MainThread
//    fun startAuth() {
//        // WrongThread inference is incorrect for lambdas
//        // noinspection WrongThread
//        executor!!.submit { doAuth() }
//    }

//    /**
//     * Initializes the authorization service configuration if necessary, either from the local
//     * static values or by retrieving an OpenID discovery document.
//     */
//    @WorkerThread
//    private fun initializeAppAuth() {
//        Log.i(TAG, "Initializing AppAuth")
//        recreateAuthorizationService()
//        if (authStateManager!!.current.authorizationServiceConfiguration != null) {
//            // configuration is already created, skip to client initialization
//            Log.i(TAG, "auth config already established")
//            initializeClient()
//            return
//        }
//
//        // if we are not using discovery, build the authorization service configuration directly
//        // from the static configuration values.
//        if (configuration!!.discoveryUri == null) {
//            Log.i(
//                TAG,
//                "Creating auth config from res/raw/auth_config.json"
//            )
//            val config = AuthorizationServiceConfiguration(
//                configuration!!.authEndpointUri!!,
//                configuration!!.tokenEndpointUri!!,
//                configuration!!.registrationEndpointUri
//            )
//            authStateManager!!.replace(AuthState(config))
//            initializeClient()
//            return
//        }
//
//        // WrongThread inference is incorrect for lambdas
//        // noinspection WrongThread
//        Log.i(TAG, "Retrieving OpenID discovery doc")
//        AuthorizationServiceConfiguration.fetchFromUrl(configuration!!.discoveryUri!!) {
//                config: AuthorizationServiceConfiguration?, ex: AuthorizationException? ->
//                handleConfigurationRetrievalResult(
//                    config,
//                    ex
//                )
//        }
//    }

//    @MainThread
//    private fun handleConfigurationRetrievalResult(
//        config: AuthorizationServiceConfiguration?,
//        ex: AuthorizationException?
//    ) {
//        if (config == null) {
//            Log.i(
//                TAG,
//                "Failed to retrieve discovery document",
//                ex
//            )
//            return
//        }
//        Log.i(TAG, "Discovery document retrieved")
//        authStateManager!!.replace(AuthState(config))
//        executor!!.submit { initializeClient() }
//    }

//    /**
//     * Initiates a dynamic registration request if a client ID is not provided by the static
//     * configuration.
//     */
//    @WorkerThread
//    private fun initializeClient() {
//        if (configuration!!.clientId != null) {
//            Log.i(
//                TAG,
//                "Using static client ID: " + configuration!!.clientId
//            )
//            // use a statically configured client ID
//            clientId.set(configuration!!.clientId)
//            runOnUiThread { initializeAuthRequest() }
//            return
//        }
//        val lastResponse =
//            authStateManager!!.current.lastRegistrationResponse
//        if (lastResponse != null) {
//            Log.i(
//                TAG,
//                "Using dynamic client ID: " + lastResponse.clientId
//            )
//            // already dynamically registered a client ID
//            clientId.set(lastResponse.clientId)
//            runOnUiThread { initializeAuthRequest() }
//            return
//        }
//
//        // WrongThread inference is incorrect for lambdas
//        // noinspection WrongThread
//        Log.i(TAG, "Dynamically registering client")
//        val registrationRequest = RegistrationRequest.Builder(
//            authStateManager!!.current.authorizationServiceConfiguration!!,
//            listOf(configuration!!.redirectUri)
//        )
//            .setTokenEndpointAuthenticationMethod(ClientSecretBasic.NAME)
//            .build()
//        authService!!.performRegistrationRequest(
//            registrationRequest
//        ) { response: RegistrationResponse?, ex: AuthorizationException? ->
//            handleRegistrationResponse(
//                response,
//                ex
//            )
//        }
//    }

//    @MainThread
//    private fun handleRegistrationResponse(
//        response: RegistrationResponse?,
//        ex: AuthorizationException?
//    ) {
//        authStateManager!!.updateAfterRegistration(response, ex)
//        if (response == null) {
//            Log.i(
//                TAG,
//                "Failed to dynamically register client",
//                ex
//            )
//            return
//        }
//        Log.i(
//            TAG,
//            "Dynamically registered client: " + response.clientId
//        )
//        clientId.set(response.clientId)
//        initializeAuthRequest()
//    }

//    /**
//     * Performs the authorization request, using the browser selected in the spinner,
//     * and a user-provided `login_hint` if available.
//     */
//    @WorkerThread
//    private fun doAuth() {
//        try {
//            authIntentLatch.await()
//        } catch (ex: InterruptedException) {
//            Log.w(
//                TAG,
//                "Interrupted while waiting for auth intent"
//            )
//        }
//        if (usePendingIntents) {
////            val completionIntent = Intent(this, TokenActivity::class.java)
//            val completionIntent = Intent(this, HomeActivity::class.java)
//
//            val cancelIntent = Intent(this, AuthActivity::class.java)
//            cancelIntent.putExtra(EXTRA_FAILED, true)
//            cancelIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            authService!!.performAuthorizationRequest(
//                authRequest.get(),
//                PendingIntent.getActivity(this, 0, completionIntent, 0),
//                PendingIntent.getActivity(this, 0, cancelIntent, 0),
//                authIntent.get()
//            )
//        } else {
//            val intent = authService!!.getAuthorizationRequestIntent(
//                authRequest.get(),
//                authIntent.get()
//            )
//            startActivityForResult(intent, RC_AUTH)
//        }
//    }

//    private fun recreateAuthorizationService() {
//        if (authService != null) {
//            Log.i(
//                TAG,
//                "Discarding existing AuthService instance"
//            )
//            authService!!.dispose()
//        }
//        authService = createAuthorizationService()
//        authRequest.set(null)
//        authIntent.set(null)
//    }

//    private fun createAuthorizationService(): AuthorizationService {
//        Log.i(TAG, "Creating authorization service")
//        val builder = AppAuthConfiguration.Builder()
//        builder.setBrowserMatcher(browserMatcher)
////        builder.setConnectionBuilder(mConfiguration!!.connectionBuilder)
//        return AuthorizationService(this, builder.build())
//    }
//
//    @MainThread
//    private fun initializeAuthRequest() {
//        createAuthRequest(getLoginHint())
//        warmUpBrowser()
//        displayAuthOptions()
//    }

//    @MainThread
//    private fun displayAuthOptions() {
//        val state = authStateManager!!.current
//        val config =
//            state.authorizationServiceConfiguration
//        var authEndpointStr: String
//        authEndpointStr = if (config!!.discoveryDoc != null) {
//            "Discovered auth endpoint: \n"
//        } else {
//            "Static auth endpoint: \n"
//        }
//        authEndpointStr += config.authorizationEndpoint
//        var clientIdStr: String
//        clientIdStr = if (state.lastRegistrationResponse != null) {
//            "Dynamic client ID: \n"
//        } else {
//            "Static client ID: \n"
//        }
//        clientIdStr += clientId
//    }

//    private fun displayAuthCancelled() {
//        Log.v(AUTH_TAG, "Authorization canceled")
//    }
//
//    private fun warmUpBrowser() {
//        try {
//            authIntentLatch = CountDownLatch(1)
//            executor?.execute {
//                Log.i(
//                    TAG,
//                    "Warming up browser instance for auth request"
//                )
//                val intentBuilder =
//                    authService?.createCustomTabsIntentBuilder(authRequest.get().toUri())
//                intentBuilder?.setToolbarColor(getColorCompat(R.color.brandOrange))
//                intentBuilder?.setSecondaryToolbarColor(getColorCompat(R.color.white))
//                authIntent.set(intentBuilder?.build())
//                authIntentLatch.countDown()
//            }
//        } catch (e: RejectedExecutionException) {
//            e.printStackTrace()
//        }
//    }

//    private fun createAuthRequest(loginHint: String?) {
//        val params = HashMap<String, String>()
//        params["access_type"] = "offline"
//        Log.i(
//            TAG,
//            "Creating auth request for login hint: $loginHint"
//        )
//        val authRequestBuilder = AuthorizationRequest.Builder(
//            authStateManager!!.current.authorizationServiceConfiguration!!,
//            clientId.get(),
//            ResponseTypeValues.CODE,
//            configuration!!.redirectUri
//        )
//        if(TokenManager.didUserLogOut){
//            authRequestBuilder
//                .setScope(configuration!!.scope)
//                .setAdditionalParameters(params)
//            .setPrompt("login")
//        } else {
//             authRequestBuilder
//                .setScope(configuration!!.scope)
//                .setAdditionalParameters(params)
//        }
//
////            .setPrompt("login")
//        if (!TextUtils.isEmpty(loginHint)) {
//            authRequestBuilder.setLoginHint(loginHint)
//        }
//        authRequest.set(authRequestBuilder.build())
//    }
//
//    private fun getLoginHint(): String? {
//        return "".trim { it <= ' ' }
//    }

//    @TargetApi(Build.VERSION_CODES.M)
//    private fun getColorCompat(@ColorRes color: Int): Int {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getColor(color)
//        } else {
//            resources.getColor(color)
//        }
//    }
}