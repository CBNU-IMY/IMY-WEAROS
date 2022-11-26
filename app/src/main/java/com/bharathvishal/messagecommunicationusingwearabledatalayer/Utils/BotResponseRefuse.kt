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

object BotResponseRefuse {
    fun basicResponses(_message: String, _corpuslist: List<CorpusDto>): String {

        val random = (0..2).random()
        val message =_message.toLowerCase()
        var corpuslist=_corpuslist
        println("corpuslist= ${corpuslist}")
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
            message.contains("안녕") -> {
                "반가워 나를 친구처럼 대해줄래?"
            }

            //친구 비하(1)
            message.contains("얘기") -> {
                "그냥 일상 이야기든 고민이든 너가 하고싶은 이야기를 하면 돼"
            }

            //친구 비하(2)
            message.contains("치료") -> {
                "그렇게 생각해주니 고마워"
            }

            //친구 비하(3)
            message.contains("다음") -> {
                "응 다음에 또 보자"
            }
            message.contains("고마워") -> {
                "별말씀을요 기분이 풀리셨다면 다행이에요"
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