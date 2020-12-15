package com.orange.volunteers.register

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.orange.volunteers.R
import com.orange.volunteers.login.LoginActivity
import com.orange.volunteers.login.LoginFragment
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity :AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupToolbar()
        setupClickEvents()

        showOrHidePassword(login_password_et)
        showOrHidePassword(login_confirm_password_et)
        //TODO validate user name, password, password and confirm passwords are the same
        //TODO
        setupConfirmationPassValidAndEnableRegisterButton()
    }

    private fun setupConfirmationPassValidAndEnableRegisterButton() {
        //TODO is password valid

        agree_cb.setOnClickListener {
            if (agree_cb.isChecked )
                rgt_register_btn.setBackgroundResource(R.drawable.button_rounded_orange_fill)
            else {
                rgt_register_btn.setBackgroundResource(R.drawable.button_rounded_grey_fill)

            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showOrHidePassword(editText: EditText) {
        editText.setOnTouchListener(object: View.OnTouchListener {
            val DRAWABLE_RIGHT = 2
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                view?.requestFocus()

                if(event?.action == MotionEvent.ACTION_UP) {
                    if(event.rawX >=
                        (editText.right - editText
                            .compoundDrawables[DRAWABLE_RIGHT].bounds.width())
                    ) {
                        //If password is visible
                        if(editText.transformationMethod is PasswordTransformationMethod) {
                            editText.transformationMethod = null

                            editText.setSelection(editText.text.length)
                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                getDrawable(R.drawable.ic_eye_enabled),
                                null
                            )
                        } else {
                            editText.transformationMethod = PasswordTransformationMethod()
                            editText.setSelection(editText.text.length)
                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                getDrawable(R.drawable.ic_eye_disabled),
                                null
                            )
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun setupClickEvents() {
        rgt_login_btn.setOnClickListener {
            openLoginPage()
        }
    }

    private fun openLoginPage() {
        startActivity(Intent(this, LoginActivity::class.java))
//        openLoginFragment()
    }

    private fun setupToolbar() {
        reg_toolbar.let {
            it.title = ""
            setSupportActionBar(it)
            it.setNavigationOnClickListener {
                onBackPressed()
            }
        }

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


}