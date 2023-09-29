package com.example.pedemo.presentation.di

import com.example.pedemo.data.api.SocketService
import com.example.pedemo.data.repository.RemoteDataSource
import com.example.pedemo.data.repository.RemoteDataSourceImpl
import com.example.pedemo.data.repository.SocketRepositoryImpl
import com.example.pedemo.domain.SocketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSocketRepository(remoteDataSource: RemoteDataSource):SocketRepository{
        return SocketRepositoryImpl(remoteDataSource)
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(
        socketService: SocketService
    ):RemoteDataSource{
        return RemoteDataSourceImpl(socketService)
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.nstock.tw/v2/")
            .build()
    }

    @Singleton
    @Provides
    fun provideNewsAPIService(retrofit: Retrofit): SocketService {
        return retrofit.create(SocketService::class.java)
    }
}