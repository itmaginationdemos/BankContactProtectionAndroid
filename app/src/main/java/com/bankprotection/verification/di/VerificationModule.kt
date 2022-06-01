package com.bankprotection.verification.di

import com.bankprotection.authentication.di.AuthRetrofit
import com.bankprotection.verification.data.source.IVerificationRemoteDataSource
import com.bankprotection.verification.data.source.VerificationRemoteDataSource
import com.bankprotection.verification.domain.repository.IVerificationRepository
import com.bankprotection.verification.domain.repository.VerificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class VerificationModule {

    @Singleton
    @Provides
    fun provideVerificationRemoteDataSource(
        @AuthRetrofit retrofit: Retrofit
    ): IVerificationRemoteDataSource {
        return retrofit.create(VerificationRemoteDataSource::class.java)
    }

    @Singleton
    @Provides
    fun provideVerificationRepository(
        verificationRepository: VerificationRepository
    ): IVerificationRepository {
        return verificationRepository
    }
}