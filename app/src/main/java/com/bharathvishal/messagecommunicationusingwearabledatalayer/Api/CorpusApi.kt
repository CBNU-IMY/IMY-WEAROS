package com.bharathvishal.messagecommunicationusingwearabledatalayer.Api

import com.bharathvishal.messagecommunicationusingwearabledatalayer.Dto.CorpusDto
import retrofit2.Call
import retrofit2.http.*

public interface CorpusApi {


    @GET("/api/corpus/emotion_maincategory/{emotion_maincategory}")
    fun getAllByMaincategoryResponse(
        @Path("emotion_maincategory") emotion_maincategory: String,
    ) : Call<List<CorpusDto>>

    @GET("/api/corpus/{corpus_id}")
    fun getOneByCorpusidResponse(
        @Path("corpus_id") corpus_id: Long
    ) : Call<List<CorpusDto>>

    @GET("/api/corpus/status_keyword/{status_keyword}")
    fun getAllbyStatuskeywordResponse(
        @Path("status_keyword") status_keyword: String
    ) : Call<List<CorpusDto>>

}