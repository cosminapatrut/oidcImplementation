package com.orange.volunteers.orangeAuth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.orange.data.service.user.UserResponseDTO
import com.orange.volunteers.R
import com.orange.volunteers.home.HomeActivity
import com.orange.volunteers.util.getViewModel


internal class OrangeAuthActivity :
    AppCompatActivity(),
    AuthViewModel.LoginListener,
    AuthViewModel.TokenRefreshListener {

    companion object {
        private const val AUTH_STATE = "AUTH_STATE"
        private const val USED_INTENT = "USED_INTENT"
    }

    private val viewModel by lazy {
        getViewModel { AuthViewModel(application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_orange_auth)

        viewModel.apply {
            tokenRefreshListener = this@OrangeAuthActivity
        }

        viewModel.login(this, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_CANCELED) {
            displayAuthCancelled()
        } else {
//            val intent = Intent(this, HomeActivity::class.java)
//            intent.putExtras(data?.extras!!)
//            startActivity(intent)
            data?.let {
                if (viewModel.handleAuthorizationActivityResult(requestCode, it)) return
            }
        }
    }

    private fun displayAuthCancelled() {
        Log.v("AUTH_TAG", "Authorization canceled")
        //TODO Go back to AuthFragment
        finish()
    }

    override fun onLoginCanceled() {
        Log.e("Auth", "Login canceled")
    }

    override fun onLoginFailed(exception: Exception) {
        Log.e("Auth", "Login failed ---> $exception")
    }

    override fun onLoginSuccess(authInfo: AuthInfo, userInfo: UserResponseDTO) {
        Log.v("Auth", "Login success ---> $authInfo")

    }

    override fun onLoginSuccess(authInfo: AuthInfo) {
        Log.v("Auth", "Login success ---> $authInfo")
    }

    override fun onTokenRefreshFailed(exception: Throwable) {
        Log.e("Auth", "Token refresh failed failed ---> $exception")
    }

    override fun onTokenRefreshSuccess() {
        Log.e("Auth", "Token refresh success")
        setResult(Activity.RESULT_OK)
        finish()
    }
}
