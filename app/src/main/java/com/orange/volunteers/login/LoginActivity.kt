package com.orange.volunteers.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.orange.volunteers.R
import com.orange.volunteers.extensions.afterTextChanged
import com.orange.volunteers.register.RegisterActivity
import com.orange.volunteers.register.RegisterFragment
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupToolbar()
        setupClickEvents()
        setupUsernameValidation()
        setupPasswordValidation()
    }

    private fun isUsernameValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(login_username_et.text.toString().trim())
            .matches()
    }

    private fun isPasswordValid(): Boolean {
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!?.])(?=\\S+$).{8,}$"
        val passwordPattern = Pattern.compile(PASSWORD_PATTERN)

        return  passwordPattern.matcher(login_password_et.text.toString().trim())
            .matches()
    }

    private fun setupUsernameValidation() {
        login_username_et.afterTextChanged {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isUsernameValid()
                ) {
                    login_username_et.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        getDrawable(R.drawable.ic_check),
                        null
                    )
                    enableLoginButton()
                } else {
                    login_username_et.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                         getDrawable(R.drawable.ic_alert),
                        null
                    )
                    disableLoginButton()
                }
            } else {
                if (Patterns.EMAIL_ADDRESS.matcher(login_username_et.text.toString().trim())
                        .matches()
                ) {
                    login_username_et.setCompoundDrawablesWithIntrinsicBounds(null,
                        null,
                        AppCompatResources.getDrawable(
                                this,
                                R.drawable.ic_check)

                ,
                        null
                    )
                    enableLoginButton()
                } else {
                    login_username_et.setCompoundDrawablesWithIntrinsicBounds(null, null,

                            AppCompatResources.getDrawable(
                                this,
                                R.drawable.ic_alert
                            )
                , null
                    )
                    disableLoginButton()
                }
            }
        }
    }

    private fun enableLoginButton() {
        if(isUsernameValid() && isPasswordValid())
            lgn_login_btn.setBackgroundResource(R.drawable.button_rounded_orange_fill)
    }

    private fun disableLoginButton() {
        if(!isUsernameValid() || !isPasswordValid())
            lgn_login_btn.setBackgroundResource(R.drawable.button_rounded_grey_fill)
    }

    private fun setupPasswordValidation() {
        login_password_et.afterTextChanged {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (isPasswordValid()) {
                    login_password_et.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        getDrawable(R.drawable.ic_check),
                        null
                    )
                    enableLoginButton()
                } else {
                    login_password_et.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        getDrawable(R.drawable.ic_alert),
                        null
                    )
                    disableLoginButton()
                }
            } else {
                if (isPasswordValid()) {
                    login_password_et.setCompoundDrawablesWithIntrinsicBounds(null,
                        null,
                            AppCompatResources.getDrawable(
                                this,
                                R.drawable.ic_check
                            )
                        ,
                        null
                    )
                    enableLoginButton()
                } else {
                    login_password_et.setCompoundDrawablesWithIntrinsicBounds(null, null,
                            AppCompatResources.getDrawable(
                                this,
                                R.drawable.ic_alert
                            )
                        , null
                    )
                    disableLoginButton()
                }
            }
        }
    }

    private fun setupClickEvents() {
        lgn_login_btn.setOnClickListener {
            //Proceed with login
            authenticateUser()
        }
        lgn_register_btn.setOnClickListener {
            openRegisterPage()
        }
    }

    private fun authenticateUser() {
        //Start auth
    }

    private fun openRegisterPage() {
//       openRegisterFragment()
        startActivity(Intent(this, RegisterActivity::class.java))

    }

    private fun setupToolbar() {
        lgn_toolbar.let {
            it.title = ""
            setSupportActionBar(it)
            it.setNavigationOnClickListener {
                onBackPressed()
            }

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
}