package dev.logickoder.currencycalculator.di

import com.google.gson.Gson
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TIMEOUT = 20L

val Retrofit: Retrofit.Builder = retrofit2.Retrofit.Builder().client(
    OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectionPool(ConnectionPool(0, 1, TimeUnit.MICROSECONDS))
        .protocols(listOf(Protocol.HTTP_1_1))
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()
).addConverterFactory(
    GsonConverterFactory.create(Gson().newBuilder().setLenient().create())
)