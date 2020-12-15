package com.orange.data.defaults

import android.util.Log
import com.orange.data.service.user.UserService
import com.orange.domain.model.CampaignModel
import com.orange.domain.model.DomainModel
import com.orange.domain.model.UpdateUserModel
import com.orange.domain.model.UserResponseModel
import com.orange.domain.repository.UserRepository
import io.reactivex.Completable
import io.reactivex.Single

class UserRepositoryImpl(private val userService: UserService) : UserRepository {
//    override fun getUserInfo(): Single<UserResponseModel> {
//        return userService.getUserInfo()
//            .map { userResponseDTO ->
//                UserResponseModel(
//                    uuid = userResponseDTO.uuid,
//                    contact = userResponseDTO.contact,
//                    role = userResponseDTO.role,
//                    notificationsEnabled = userResponseDTO.notificationsEnabled,
//                    preferredDomains = userResponseDTO.preferredDomains
//                )
//            }
//    }
    //Mock
    override fun getUserInfo(): Single<UserResponseModel> {
        return userService.getUserInfo()
            .map { userResponseDTO ->
                UserResponseModel(
                    contact = userResponseDTO.contact,
                    role = userResponseDTO.role,
                    notificationsEnabled = userResponseDTO.notificationsEnabled,
                    locationSharingEnabled = userResponseDTO.locationSharingEnabled,
                    location = userResponseDTO.location,
                    preferredDomains = userResponseDTO.preferredDomains
                )
            }
    }

    override fun updateUserInfo(user: UpdateUserModel): Completable {
        return userService.updateUserInfo(user)
             .doOnComplete {
            Log.v("User Update","success")
            }
            .doOnError {
                Log.e("User Update error:","${it.message}")
            }
//            .map { userResponseDTO ->
//                //TODO
//                UserResponseModel(
//                    contact = userResponseDTO.contact,
//                    role = userResponseDTO.role,
//                    notificationsEnabled = userResponseDTO.notificationsEnabled,
//                    location = userResponseDTO.location,
//                    preferredDomains = userResponseDTO.preferredDomains
//                )
//            }
    }

    override fun deleteUser(): Completable {
        return userService.deleteUser()
            .doOnComplete {
                Log.v("User Delete","success")
            }
            .doOnError {
                Log.e("User Delete error:","${it.message}")
            }
    }


}
