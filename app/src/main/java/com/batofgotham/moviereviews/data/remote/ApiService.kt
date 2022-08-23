package com.batofgotham.moviereviews.data.remote.movies

import com.batofgotham.moviereviews.data.model.Configuration

import com.batofgotham.moviereviews.data.model.MovieNetworkResponse

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

private const val BASE_URL = "https://api.themoviedb.org/3/"

private const val API_KEY = "db75be3f6da59e6c54d0b9f568d19d16"

private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val okHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(interceptor)
}.build()

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(okHttpClient)
    .build()

interface ApiService {

    @GET("movie/popular?api_key=$API_KEY")
    suspend fun getMoviesApiResponse(@Query("page") page: Int): MovieNetworkResponse

    @GET("/configuration?api_key=$API_KEY")
    suspend fun getApiConfig(): Configuration
}


@InstallIn(SingletonComponent::class)
@Module
object Network {

    @Singleton
    @Provides
    fun providesApiService(): ApiService {
        val apiService: ApiService by lazy {
            retrofit.create(ApiService::class.java)
        }
        return apiService
    }


}