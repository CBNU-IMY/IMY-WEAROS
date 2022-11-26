package com.bharathvishal.messagecommunicationusingwearabledatalayer.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitBuilder {
    var userapi: UserApi
    var corpusapi: CorpusApi
    var chatbotapi: ChatbotApi
    val nestnetip= "http://113.198.137.188:81/"
    val houseip="http://113.198.137.200:4511"
    val localhost="http://10.0.2.2:5000"
    init{
        val retrofit= Retrofit.Builder()
            .baseUrl(houseip)   //요청 보내는 API 서버 url /로 끝나야 함
            .addConverterFactory(ScalarsConverterFactory.create() )
            .addConverterFactory(GsonConverterFactory.create())//Gson을 역직렬화
            .build()
        userapi=retrofit.create(UserApi::class.java)
        corpusapi=retrofit.create(CorpusApi::class.java)
        chatbotapi=retrofit.create(ChatbotApi::class.java)

    }
}