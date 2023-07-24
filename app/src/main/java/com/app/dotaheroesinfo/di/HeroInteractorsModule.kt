package com.app.dotainfo.di

import android.app.Application
import android.content.Context
import com.app.hero_interactors.HeroInteractors
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HeroInteractorsModule {

    @Provides
    @Singleton
    @Named("heroAndroidSqlDriver") // in case you had another SQL Delight db
    fun provideAndroidDriver(@ApplicationContext app: Context): SqlDriver {
       return AndroidSqliteDriver(
           schema = HeroInteractors.schema,
           context = app,
           name = HeroInteractors.dbName
       )
    }

    /**
     * Provide all the interactors in hero-interactors module
     */
    @Provides
    @Singleton
    fun provideHeroInteractors(
        @Named("heroAndroidSqlDriver") sqlDriver: SqlDriver,
    ): HeroInteractors{
        return HeroInteractors.build(sqlDriver)
    }
}