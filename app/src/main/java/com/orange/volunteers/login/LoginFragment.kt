package com.orange.volunteers.login

import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.orange.volunteers.AuthActivity
import com.orange.volunteers.R
import com.orange.volunteers.extensions.afterTextChanged
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        context?.getDrawable(R.drawable.ic_check),
                        null
                    )
                    enableLoginButton()
                } else {
                    login_username_et.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        context?.getDrawable(R.drawable.ic_alert),
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
                        context?.let { context ->
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_check
                            )
                        },
                        null
                    )
                    enableLoginButton()
                } else {
                    login_username_et.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        context?.let { context ->
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_alert
                            )
                        }, null
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
                        context?.getDrawable(R.drawable.ic_check),
                        null
                    )
                    enableLoginButton()
                } else {
                    login_password_et.setCompoundDrawablesWithIntrinsicBounds(
                        null,
                        null,
                        context?.getDrawable(R.drawable.ic_alert),
                        null
                    )
                    disableLoginButton()
                }
            } else {
                if (isPasswordValid()) {
                    login_password_et.setCompoundDrawablesWithIntrinsicBounds(null,
                        null,
                        context?.let { context ->
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_check
                            )
                        },
                        null
                    )
                    enableLoginButton()
                } else {
                    login_password_et.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        context?.let { context ->
                            AppCompatResources.getDrawable(
                                context,
                                R.drawable.ic_alert
                            )
                        }, null
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
        (activity as AuthActivity).openRegisterFragment()
    }

    private fun setupToolbar() {
        lgn_toolbar.let {
            it.title = ""
            (activity as AuthActivity).setSupportActionBar(it)
            it.setNavigationOnClickListener {
                (activity as AuthActivity).onBackPressed()
            }
        }

    }

    companion object {
        const val ORANGE_LOGIN_REQUEST_CODE = 100

        fun newInstance() = LoginFragment()
    }
}