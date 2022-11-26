package com.bharathvishal.messagecommunicationusingwearabledatalayer.Api


import com.bharathvishal.messagecommunicationusingwearabledatalayer.Dto.ChatbotDto
import retrofit2.Call
import retrofit2.http.*

interface ChatbotApi {


    @GET("/chatbot/g")
    fun getKogpt2Response(
        @Query("s") s:String
    ): Call<ChatbotDto>


    @GET("/")
    fun getHomeResponse(
    ): Call<String>
}