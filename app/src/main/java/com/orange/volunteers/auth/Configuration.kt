package com.orange.volunteers.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.text.TextUtils
import com.orange.volunteers.R
import okio.Buffer
import okio.buffer
import okio.source
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.ref.WeakReference
import java.nio.charset.Charset


/**
 * Reads and validates the demo app configuration from `res/raw/auth_config.json`. Configuration
 * changes are detected by comparing the hash of the last known configuration to the read
 * configuration. When a configuration change is detected, the app state is reset.
 */
class Configuration(private val mContext: Context) {
    private val prefs: SharedPreferences
    private val resources: Resources
    private var configJson: JSONObject? = null
    private var configHash: String? = null

    /**
     * Returns a description of the configuration error, if the configuration is invalid.
     */
    var configurationError: String? = null
    var clientId: String? = null
        private set
    private var currentAccessType: String? = null
    private var currentScope: String? = null
    private var currentRedirectUri: Uri? = null
    var discoveryUri: Uri? = null
        private set
    var authEndpointUri: Uri? = null
        private set
    var tokenEndpointUri: Uri? = null
        private set
    var registrationEndpointUri: Uri? = null
        private set
    var userInfoEndpointUri: Uri? = null
        private set
    var isHttpsRequired = false
        private set

    /**
     * Indicates whether the configuration has changed from the last known valid state.
     */
    fun hasConfigurationChanged(): Boolean {
        val lastHash = lastKnownConfigHash
        return configHash != lastHash
    }

    /**
     * Indicates whether the current configuration is valid.
     */
    val isValid: Boolean
        get() = configurationError == null

    /**
     * Indicates that the current configuration should be accepted as the "last known valid"
     * configuration.
     */
    fun acceptConfiguration() {
        prefs.edit().putString(
            KEY_LAST_HASH,
            configHash
        ).apply()
    }

    val scope: String
        get() = currentScope!!

    val accessType: String
        get() = currentAccessType!!

    val redirectUri: Uri
        get() = currentRedirectUri!!

//    val connectionBuilder: ConnectionBuilder
//        get() = if (isHttpsRequired) {
//            DefaultConnectionBuilder.INSTANCE
//        } else ConnectionBuilderForTesting.INSTANCE

    private val lastKnownConfigHash: String?
        private get() = prefs.getString(
            KEY_LAST_HASH,
            null
        )

    @Throws(InvalidConfigurationException::class)
    private fun readConfiguration() {
        val configSource =
            resources.openRawResource(R.raw.auth_config_dev).source().buffer()
        val configData = Buffer()
        configJson = try {
            configSource.readAll(configData)
            JSONObject(configData.readString(Charset.forName("UTF-8")))
        } catch (ex: IOException) {
            throw InvalidConfigurationException(
                "Failed to read configuration: " + ex.message
            )
        } catch (ex: JSONException) {
            throw InvalidConfigurationException(
                "Unable to parse configuration: " + ex.message
            )
        }
        configHash = configData.sha256().base64()
        clientId = getConfigString("client_id")
        currentScope = getRequiredConfigString("authorization_scope")
        currentAccessType = getRequiredConfigString("access_type")
        currentRedirectUri = getRequiredConfigUri("redirect_uri")
        if (!isRedirectUriRegistered) {
            throw InvalidConfigurationException(
                "redirect_uri is not handled by any activity in this app! "
                        + "Ensure that the appAuthRedirectScheme in your build.gradle file "
                        + "is correctly configured, or that an appropriate intent filter "
                        + "exists in your app manifest."
            )
        }
        if (getConfigString("discovery_uri") == null) {
            authEndpointUri = getRequiredConfigWebUri("authorization_endpoint_uri")
            tokenEndpointUri = getRequiredConfigWebUri("token_endpoint_uri")
            userInfoEndpointUri = getRequiredConfigWebUri("user_info_endpoint_uri");
            if (clientId == null) {
                registrationEndpointUri = getRequiredConfigWebUri("registration_endpoint_uri")
            }
        } else {
            discoveryUri = getRequiredConfigWebUri("discovery_uri")
            //take user info url from config file
            userInfoEndpointUri = getRequiredConfigWebUri("user_info_endpoint_uri");

        }
        isHttpsRequired = configJson!!.optBoolean("https_required", true)
    }

    fun getConfigString(propName: String): String? {
        var value = configJson!!.optString(propName) ?: return null
        value = value.trim { it <= ' ' }
        return if (TextUtils.isEmpty(value)) {
            null
        } else value
    }

    @Throws(InvalidConfigurationException::class)
    private fun getRequiredConfigString(propName: String): String {
        return getConfigString(propName)
            ?: throw InvalidConfigurationException(
                "$propName is required but not specified in the configuration"
            )
    }

    @Throws(InvalidConfigurationException::class)
    fun getRequiredConfigUri(propName: String): Uri {
        val uriStr = getRequiredConfigString(propName)
        val uri: Uri
        uri = try {
            Uri.parse(uriStr)
        } catch (ex: Throwable) {
            throw InvalidConfigurationException(
                "$propName could not be parsed",
                ex
            )
        }
        if (!uri.isHierarchical || !uri.isAbsolute) {
            throw InvalidConfigurationException(
                "$propName must be hierarchical and absolute"
            )
        }
        if (!TextUtils.isEmpty(uri.encodedUserInfo)) {
            throw InvalidConfigurationException("$propName must not have user info")
        }
        if (!TextUtils.isEmpty(uri.encodedQuery)) {
            throw InvalidConfigurationException("$propName must not have query parameters")
        }
        if (!TextUtils.isEmpty(uri.encodedFragment)) {
            throw InvalidConfigurationException("$propName must not have a fragment")
        }
        return uri
    }

    @Throws(InvalidConfigurationException::class)
    fun getRequiredConfigWebUri(propName: String): Uri {
        val uri = getRequiredConfigUri(propName)
        val scheme = uri.scheme
        if (TextUtils.isEmpty(scheme) || !("http" == scheme || "https" == scheme)) {
            throw InvalidConfigurationException(
                "$propName must have an http or https scheme"
            )
        }
        return uri
    }

    // ensure that the redirect URI declared in the configuration is handled by some activity
    // in the app, by querying the package manager speculatively
    private val isRedirectUriRegistered: Boolean
        private get() {
            // ensure that the redirect URI declared in the configuration is handled by some activity
            // in the app, by querying the package manager speculatively
            val redirectIntent = Intent()
            redirectIntent.setPackage(mContext.packageName)
            redirectIntent.action = Intent.ACTION_VIEW
            redirectIntent.addCategory(Intent.CATEGORY_BROWSABLE)
            redirectIntent.data = currentRedirectUri
            return !mContext.packageManager.queryIntentActivities(redirectIntent, 0).isEmpty()
        }

    class InvalidConfigurationException : Exception {
        internal constructor(reason: String?) : super(reason) {}
        internal constructor(reason: String?, cause: Throwable?) : super(
            reason,
            cause
        ) {
        }
    }

    companion object {
        private const val TAG = "Configuration"
         const val PREFS_NAME = "config"
         const val KEY_LAST_HASH = "lastHash"

        private var instance =
            WeakReference<Configuration?>(null)

        fun getInstance(context: Context): Configuration {
            var config =
                instance.get()
            if (config == null) {
                config = Configuration(context)
                instance =
                    WeakReference(config)
            }
            return config
        }
    }

    init {
        prefs = mContext.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        resources = mContext.resources
        try {
            readConfiguration()
        } catch (ex: InvalidConfigurationException) {
            configurationError = ex.message
        }
    }
}