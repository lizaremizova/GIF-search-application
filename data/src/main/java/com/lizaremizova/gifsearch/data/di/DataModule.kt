package com.lizaremizova.gifsearch.data.di

import com.lizaremizova.gifsearch.data.remote.GiphyApi
import com.lizaremizova.gifsearch.data.repository.GifRepositoryImpl
import com.lizaremizova.gifsearch.domain.repository.GifRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindGifRepository(impl: GifRepositoryImpl): GifRepository

    companion object {
        @Provides
        @Singleton
        fun provideGiphyApi(retrofit: Retrofit): GiphyApi = retrofit.create(GiphyApi::class.java)
    }
}
