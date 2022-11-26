package com.bharathvishal.messagecommunicationusingwearabledatalayer.Utils

import android.util.Log
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Dto.CorpusDto
/*
import com.codepalace.chatbot.Api.RetrofitBuilder
import com.codepalace.chatbot.Dto.CorpusDto
import com.codepalace.chatbot.Dto.CorpusDto2*/
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponseAccept {
    fun basicResponses(_message: String, _corpuslist: List<CorpusDto>): String {

        val random = (0..2).random()
        val message =_message.toLowerCase()
        var corpuslist=_corpuslist

        return when {

            //Flips a coin
            message.contains("flip") && message.contains("coin") -> {
                val r = (0..1).random()
                val result = if (r == 0) "heads" else "tails"

                "I flipped a coin and it landed on $result"
            }

            //Math calculations
            message.contains("solve") -> {
                val equation: String? = message.substringAfterLast("solve")
                return try {
                    val answer = SolveMath.solveMath(equation ?: "0")
                    "$answer"

                } catch (e: Exception) {
                    "Sorry, I can't solve that."
                }
            }

            //Hello
            message.contains("추천") -> {
                corpuslist.get(7).system_response1
            }



            //학교폭력(1)
            message.contains("학점") -> {
                corpuslist.get(7).system_response2
            }

            //학교폭력(2)
            message.contains("교수") -> {
                corpuslist.get(7).system_response3
            }

            //학교폭력(3)
            message.contains("긍정")  -> {
                corpuslist.get(2).system_response3
            }

            //학교폭력(3)
            message.contains("고마워")  -> {
                "다행이네요 우울할때마다 저를 찾아주세요"
            }



            //What time is it?
            message.contains("time") && message.contains("?")-> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }

            //Open Google
            message.contains("open") && message.contains("google")-> {
                Constants.OPEN_GOOGLE
            }

            //Search on the internet
            message.contains("search")-> {
                Constants.OPEN_SEARCH
            }

            //When the programme doesn't understand...
            else -> {
                when (random) {
                    0 -> "무슨 말인지 잘 모르겠어..."
                    1 -> "미안 다시 한 번 말해줄래??"
                    2 -> "뭐라고 했지??"
                    else -> "error"
                }
            }
        }
    }


}