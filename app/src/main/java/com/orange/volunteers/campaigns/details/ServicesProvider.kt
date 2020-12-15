package com.orange.volunteers.campaigns.details

import android.content.Context
import android.speech.tts.TextToSpeechService
import com.orange.data.service.user.UserService
import com.orange.volunteers.auth.LoginService
import com.orange.volunteers.util.SingletonHolder

class ServicesProvider private constructor(private val context: Context) {

    companion object : SingletonHolder<ServicesProvider, Context>(::ServicesProvider)

    private val enrollService: EnrollService

    init {
        enrollService = EnrollmentServiceImpl(
            context = context
        )
    }

    fun getEnrollService() = enrollService
}
