package com.bankprotection.platform.di

import com.bankprotection.android_common.di.PlatformModuleEntryPoint
import dagger.Component

@Component(dependencies = [PlatformModuleEntryPoint::class])
interface PlatformComponent {
}