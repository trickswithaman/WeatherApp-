package com.example.weatherapp.core.utils

import com.example.weatherapp.data.ApiInterface
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



object RetrofitInstance {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(loggingInterceptor)
    }.build()

    val api : ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}