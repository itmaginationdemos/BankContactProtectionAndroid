package com.bankprotection.android_common.di

import com.bankprotection.common.domain.repository.ISystemResourcesRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlatformModuleEntryPoint {
    fun provideSystemResourcesRepository(): ISystemResourcesRepository
}