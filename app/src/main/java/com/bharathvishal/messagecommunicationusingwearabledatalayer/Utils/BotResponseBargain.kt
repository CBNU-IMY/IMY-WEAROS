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

object BotResponseBargain {
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


            //되묻기
            message.contains("헤어") -> {
                "여자친구와 헤어지셨군요, 오랜기간 만났나요?"
            }

            //되묻기
            message.contains("만났어") -> {
                "지금 기분은 어떠신가요?"
            }

            message.contains("올게") -> {
                "네 기다리고 있을게요"
            }

            //
            message.contains("그럭") -> {
                "우울증이 의심되는 것 같아요"
            }



            //How are you?
            message.contains("how are you") -> {

                when (random) {
                    0 -> "I'm doing fine, thanks!"
                    1 -> "I'm hungry..."
                    2 -> "Pretty good! How about you?"
                    else -> "error"
                }
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