package com.orange.volunteers.register

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.orange.volunteers.AuthActivity
import com.orange.volunteers.R
import kotlinx.android.synthetic.main.activity_register.*
import org.koin.android.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_register, container, false)
    }
    private val registerViewModel: RegisterViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                                context?.getDrawable(R.drawable.ic_eye_enabled),
                                null
                            )
                        } else {
                            editText.transformationMethod = PasswordTransformationMethod()
                            editText.setSelection(editText.text.length)
                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                context?.getDrawable(R.drawable.ic_eye_disabled),
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
        (activity as AuthActivity).openLoginFragment()
    }

    private fun setupToolbar() {
        reg_toolbar.let {
            it.title = ""
            (activity as AuthActivity).setSupportActionBar(it)
            it.setNavigationOnClickListener {
                (activity as AuthActivity).onBackPressed()
            }
        }

    }

    companion object {
        fun newInstance() = RegisterFragment()
    }
}