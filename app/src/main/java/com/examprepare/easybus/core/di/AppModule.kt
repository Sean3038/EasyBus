package com.examprepare.easybus.core.di

import android.content.Context
import androidx.room.Room
import com.examprepare.easybus.Const
import com.examprepare.easybus.core.database.PTXDataBase
import com.examprepare.easybus.core.database.PersonalDataBase
import com.examprepare.easybus.core.interceptor.NetworkInterceptor
import com.examprepare.easybus.core.interceptor.PtxRequestInterceptor
import com.examprepare.easybus.core.platform.NetworkHandler
import com.examprepare.easybus.core.repository.LikeRouteRepository
import com.examprepare.easybus.core.repository.RouteRepository
import com.examprepare.easybus.core.repository.StopRepository
import com.examprepare.easybus.core.service.PTXApi
import com.examprepare.easybus.core.service.PTXService
import com.examprepare.easybus.feature.home.domain.usecase.GetFavoriteRoutes
import com.examprepare.easybus.feature.route.domain.usecase.AddLikeRoute
import com.examprepare.easybus.feature.route.domain.usecase.GetRoute
import com.examprepare.easybus.feature.route.domain.usecase.RemoveLikeRoute
import com.examprepare.easybus.feature.searchnearstop.domain.usecase.GetNearStop
import com.examprepare.easybus.feature.searchroute.domain.usecase.GetAllRoute
import com.examprepare.easybus.feature.searchroute.domain.usecase.SearchRoute
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providerOKHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(PtxRequestInterceptor())
            .addInterceptor(NetworkInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun providerGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun providePtxApi(gson: Gson, client: OkHttpClient): PTXApi {
        return Retrofit.Builder()
            .baseUrl(Const.PTX_API_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(PTXApi::class.java)
    }

    @Provides
    @Singleton
    fun providerPtxService(api: PTXApi): PTXService {
        return PTXService(api)
    }

    @Provides
    @Singleton
    fun providerPtxDataBase(@ApplicationContext appContext: Context): PTXDataBase {
        return Room.inMemoryDatabaseBuilder(appContext, PTXDataBase::class.java).build()
    }

    @Provides
    @Singleton
    fun providerPersonalDataBase(@ApplicationContext appContext: Context): PersonalDataBase {
        return Room.databaseBuilder(
            appContext,
            PersonalDataBase::class.java,
            PersonalDataBase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providerNetworkHandler(@ApplicationContext appContext: Context): NetworkHandler {
        return NetworkHandler(appContext)
    }

    @Provides
    @Singleton
    fun providerStopRepository(
        ptxService: PTXService,
        ptxDataBase: PTXDataBase,
        networkHandler: NetworkHandler
    ): StopRepository {
        return StopRepository.Impl(networkHandler, ptxService, ptxDataBase)
    }

    @Provides
    @Singleton
    fun providerRouteRepository(
        ptxService: PTXService,
        ptxDataBase: PTXDataBase,
        networkHandler: NetworkHandler
    ): RouteRepository {
        return RouteRepository.Impl(networkHandler, ptxService, ptxDataBase)
    }

    @Provides
    @Singleton
    fun providerLikeRouteRepository(
        personalDataBase: PersonalDataBase
    ): LikeRouteRepository {
        return LikeRouteRepository.Impl(personalDataBase)
    }

    @Provides
    @Singleton
    fun providerGetRoute(routeRepository: RouteRepository): GetRoute {
        return GetRoute(routeRepository)
    }

    @Provides
    @Singleton
    fun providerGetAllRoute(routeRepository: RouteRepository): GetAllRoute {
        return GetAllRoute(routeRepository)
    }

    @Provides
    @Singleton
    fun providerSearchRoute(routeRepository: RouteRepository): SearchRoute {
        return SearchRoute(routeRepository)
    }

    @Provides
    @Singleton
    fun providerAddLikeRoute(routeRepository: LikeRouteRepository): AddLikeRoute {
        return AddLikeRoute(routeRepository)
    }

    @Provides
    @Singleton
    fun providerRemoveLikeRoute(routeRepository: LikeRouteRepository): RemoveLikeRoute {
        return RemoveLikeRoute(routeRepository)
    }

    @Provides
    @Singleton
    fun providerGetFavoriteRoutes(
        likeRouteRepository: LikeRouteRepository,
        routeRepository: RouteRepository
    ): GetFavoriteRoutes {
        return GetFavoriteRoutes(likeRouteRepository, routeRepository)
    }

    @Provides
    @Singleton
    fun providerGetNearStop(
        stopRepository: StopRepository,
    ): GetNearStop {
        return GetNearStop(stopRepository)
    }

}