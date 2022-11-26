package com.bharathvishal.messagecommunicationusingwearabledatalayer.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitBuilder2 {
    var userapi: UserApi
    val localhost="http://113.198.137.200:8080/"
    init{
        val retrofit= Retrofit.Builder()
            .baseUrl(localhost)   //요청 보내는 API 서버 url /로 끝나야 함
            .addConverterFactory(ScalarsConverterFactory.create() )
            .addConverterFactory(GsonConverterFactory.create())//Gson을 역직렬화
            .build()
        userapi =retrofit.create(UserApi::class.java)
    }
}