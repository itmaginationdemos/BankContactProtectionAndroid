package com.bankprotection.authentication.di

import com.bankprotection.application.BuildConfig
import com.bankprotection.authentication.data.*
import com.bankprotection.authentication.domain.repositories.*
import com.bankprotection.common.domain.repository.IPreferencesDataSource
import com.google.gson.GsonBuilder
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AuthenticationModule {
    @Singleton
    @Provides
    fun provideRemoteAuthenticationDataSource(
        @OtherRetrofit retrofit: Retrofit
    ): IRemoteAuthenticationDataSource {
        return retrofit.create(RemoteAuthenticationDataSource::class.java)
    }

    @Provides
    fun provideRemoteUserDetailsDataSource(
        @AuthRetrofit retrofit: Retrofit
    ): IRemoteUserDetailsDataSource {
        return retrofit.create(RemoteUserDetailsDataSource::class.java)
    }

    @Singleton
    @Provides
    fun provideLocalAuthenticationDataSource(preferencesDataSource: IPreferencesDataSource):
            ILocalAuthenticationDataSource {
        return LocalAuthenticationDataSource(preferencesDataSource)
    }

    @Singleton
    @OtherRetrofit
    @Provides
    fun provideOtherRetrofit(
        @OtherInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.REST_API_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Singleton
    @AuthRetrofit
    @Provides
    fun provideAuthRetrofit(
        @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit {
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .create()
        return Retrofit.Builder().baseUrl(BuildConfig.REST_API_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
    }

    @OtherInterceptorOkHttpClient
    @Provides
    fun provideOtherInterceptorOkHttpsClient(otherInterceptor: OtherInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(otherInterceptor)
            .build()
    }

    @AuthInterceptorOkHttpClient
    @Provides
    fun provideAuthInterceptorOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    fun provideUserDetailsRepository(userDetailsRepository: UserDetailsRepository): IUserDetailsRepository {
        return userDetailsRepository
    }
}