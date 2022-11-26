package com.bharathvishal.messagecommunicationusingwearabledatalayer.Api


import com.bharathvishal.messagecommunicationusingwearabledatalayer.Data.User
import retrofit2.Call
import retrofit2.http.*

public interface UserApi {
    @POST("api/user")
    fun postSignupResponse(@Body user: User): Call<String>

    @GET("api/user")
    fun getUserlistResponse(): Call<String>

    @FormUrlEncoded
    @PUT("api/user/{user_id}")
    fun postUpdateResponse(
        @Path("user_id") user_id: String?,
        @Field("user_pw") user_pw: String,
        @Field("user_phone") user_phone: String,
        @Field("user_name") user_name: String
    ): Call<String>

    @DELETE("api/user/{user_id}")
    fun deleteUserResponse(@Path("user_id") user_id: String): Call <String>


}