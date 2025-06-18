package com.doyoonkim.common.di

import com.doyoonkim.common.BitmapHandler
import com.doyoonkim.common.BitmapHandlerImpl
import dagger.Module
import dagger.Binds

/**
 * @author kimdoyoon
 * Created 6/17/25 at 12:14 AM
 */
@Module
abstract class CommonModule {

    @Binds
    abstract fun bindsBitmapHandler(
        impl: BitmapHandlerImpl
    ): BitmapHandler

}