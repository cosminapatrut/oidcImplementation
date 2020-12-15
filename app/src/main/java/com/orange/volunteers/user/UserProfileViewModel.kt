package com.orange.volunteers.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.orange.domain.model.DomainModel
import com.orange.domain.model.UpdateUserModel
import com.orange.domain.model.UserResponseModel
import com.orange.domain.usecases.DeleteUserInfoUseCase
import com.orange.domain.usecases.GetDomainsUseCase
import com.orange.domain.usecases.GetUserInfoUseCase
import com.orange.domain.usecases.PutUserInfoUseCase
import com.orange.volunteers.orangeAuth.OrangeTokenManager
import com.orange.volunteers.util.AppRxEvents
import com.orange.volunteers.util.EventDeleteUserAccountFailed
import com.orange.volunteers.util.EventDeleteUserAccountSuccessful
import com.orange.volunteers.util.RxBus
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class UserProfileViewModel (
    private val executionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val putUserInfoUseCase: PutUserInfoUseCase,
    private val deleteUserInfoUseCase: DeleteUserInfoUseCase
) : ViewModel() {
    val data = MutableLiveData<UserResponseModel>()
    val isLoading = MutableLiveData<Boolean>()
    val showError = MutableLiveData<Boolean>(false)

    private val disposable: CompositeDisposable = CompositeDisposable()

    fun getUserInfo() {
        isLoading.value = true
        getUserInfoUseCase.getUserInfo()
            .subscribeOn(executionScheduler)
            .observeOn(postExecutionScheduler)
            .subscribe(
                { responseItems ->
                    data.postValue(responseItems)
                    isLoading.value = false
                    showError.value = false

                },
                {

                    Log.e("UserInfo", " error")
                    isLoading.value = false
                    showError.value = true
                }
            ).addTo(disposable)
    }

    fun updateUserInfo(user: UpdateUserModel) {
//        isLoading.value = true
        //TODO fix domains to be unique
        val gson = Gson()
        val json = gson.toJson(user)
        putUserInfoUseCase.updateUserInfo(user)
            .subscribeOn(executionScheduler)
            .observeOn(postExecutionScheduler)
            .subscribe(
                {
//                    isLoading.value = false
                    showError.value = false

                },
                {
                    Log.e("UserInfo update error:", " ${it.message}")
//                    isLoading.value = false
                    showError.value = true
                }
            ).addTo(disposable)
    }

    fun deleteUser() {
        deleteUserInfoUseCase.deleteUser()
            .subscribeOn(executionScheduler)
            .observeOn(postExecutionScheduler)
            .subscribe(
                {
//                    isLoading.value = false
                    RxBus.publish(EventDeleteUserAccountSuccessful("User Delete successful!"))
                    showError.value = false

                },
                {
                    Log.e("User delete error:", " ${it.message}")
                    RxBus.publish(EventDeleteUserAccountFailed("${it.message}"))
                    showError.value = true
//                    isLoading.value = false
                }
            ).addTo(disposable)
    }
}