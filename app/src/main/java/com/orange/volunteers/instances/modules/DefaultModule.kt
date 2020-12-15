package com.orange.volunteers.instances.modules

import com.orange.data.defaults.CampaignsRepositoryImpl
import com.orange.data.defaults.DomainsRepositoryImpl
import com.orange.data.defaults.EnrollmentRepositoryImpl
import com.orange.data.defaults.UserRepositoryImpl
import com.orange.data.service.campaign.CampaignsService
import com.orange.data.service.domainsOfInterest.DomainsService
import com.orange.data.service.enrollment.EnrollmentService
import com.orange.data.service.user.UserService
import com.orange.domain.repository.CampaignsRepository
import com.orange.domain.repository.DomainsRepository
import com.orange.domain.repository.EnrollmentRepository
import com.orange.domain.repository.UserRepository
import com.orange.domain.usecases.*
import com.orange.volunteers.campaigns.details.CampaignDetailsViewModel
import com.orange.volunteers.campaigns.overview.CampaignsViewModel
import com.orange.volunteers.history.activeCampaigns.HistoryActiveCampaignsViewModel
import com.orange.volunteers.history.pastCampaigns.HistoryPastCampaignsViewModel
import com.orange.volunteers.orangeAuth.AuthViewModel
import com.orange.volunteers.register.RegisterViewModel
import com.orange.volunteers.user.UserInfoApiClient
import com.orange.volunteers.user.UserProfileViewModel
import com.orange.volunteers.userdomains.DomainsViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val defaultModule = module {

    //Rx
    single(named("IO_SCHEDULER")) { Schedulers.io() }
    single(named("MAIN_SCHEDULER")) { AndroidSchedulers.mainThread() }

    //Services
    single<CampaignsService> {
        Retrofit.Builder()
            .baseUrl("https://www-dev.orange.ro/csr-backend/")
//            .baseUrl("https://raw.githubusercontent.com/")
//                Mock
//            .baseUrl("https://8e674774-d63b-46bf-bc8b-2822e258b119.mock.pstmn.io")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(CampaignsService::class.java)
    }

    single<DomainsService> {
        Retrofit.Builder()
            .baseUrl("https://www-dev.orange.ro/csr-backend/")
//                Mock
//            .baseUrl("https://2915d3ad-03ed-4649-b744-7780f2afadaa.mock.pstmn.io")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(DomainsService::class.java)
    }

    single<EnrollmentService> {
        Retrofit.Builder()
            .baseUrl("https://www-dev.orange.ro/csr-backend/")
            .client(ClientProvider.getOAuthOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(EnrollmentService::class.java)
    }

    single<UserService> {
        Retrofit.Builder()
            .baseUrl("https://www-dev.orange.ro/csr-backend/")
//                Mock
//            .baseUrl("https://7b7d64da-5fd6-4c19-add1-7b31dddc6b17.mock.pstmn.io")
            .client(ClientProvider.getOAuthOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(UserService::class.java)
    }
    //Repos
    single<CampaignsRepository> { CampaignsRepositoryImpl(get()) }
    single<DomainsRepository> { DomainsRepositoryImpl(get()) }
    single<EnrollmentRepository> { EnrollmentRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }

    //Use cases
    single<GetCampaignsUseCase> { GetCampaignsUseCaseImpl(get()) }
    single<GetPastCampaignsUseCase> { GetPastCampaignsUseCaseImpl(get()) }
    single<GetActiveCampaignsUseCase> { GetActiveCampaignsUseCaseImpl(get()) }
    single<GetDomainsUseCase> { GetDomainsUseCaseImpl(get()) }
    single<EnrollToCampaignUseCase> { EnrollToCampaignUseCaseImpl(get()) }
    single<UnSubscribeFromCampaignUseCase> { UnSubscribeFromCampaignUseCaseImpl(get()) }
    single<IsEnrolledToCampaignUseCase> { IsEnrolledToCampaignUseCaseImpl(get()) }
    single<GetUserInfoUseCase> { GetUserInfoUseCaseImpl(get()) }
    single<GetInterestedDomainsUseCase> { GetInterestedDomainsUseCaseImpl(get()) }
    single<PutUserInfoUseCase> { PutUserInfoUseCaseImpl(get()) }
    single<DeleteUserInfoUseCase> { DeleteUserInfoUseCaseImpl(get()) }

    //Viewmodels
    viewModel {
        CampaignsViewModel(
            get(named("IO_SCHEDULER")),
            get(named("MAIN_SCHEDULER")),
            get()
        )
    }

    viewModel {
        RegisterViewModel(
            get(named("IO_SCHEDULER")),
            get(named("MAIN_SCHEDULER"))
        )
    }

    viewModel {
        HistoryPastCampaignsViewModel(
            get(named("IO_SCHEDULER")),
            get(named("MAIN_SCHEDULER")),
            get()
        )
    }

    viewModel {
        HistoryActiveCampaignsViewModel(
            get(named("IO_SCHEDULER")),
            get(named("MAIN_SCHEDULER")),
            get()
        )
    }

    viewModel {
        DomainsViewModel(
            get(named("IO_SCHEDULER")),
            get(named("MAIN_SCHEDULER")),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        AuthViewModel(
            get()
        )
    }

    viewModel {
        CampaignDetailsViewModel(
            get(named("IO_SCHEDULER")),
            get(named("MAIN_SCHEDULER")),
            get(),
            get(),
            get()
        )
    }

    viewModel {
        UserProfileViewModel(
            get(named("IO_SCHEDULER")),
            get(named("MAIN_SCHEDULER")),
            get(),
            get(),
            get()
        )
    }

}