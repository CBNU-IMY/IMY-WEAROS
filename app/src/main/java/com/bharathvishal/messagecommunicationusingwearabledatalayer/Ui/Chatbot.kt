package com.bharathvishal.messagecommunicationusingwearabledatalayer.Ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Api.RetrofitBuilder
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Data.Message
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Dto.ChatbotDto
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Dto.CorpusDto
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Utils.BotResponseAccept
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Utils.BotResponseBargain
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Utils.Constants.OPEN_GOOGLE
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Utils.Constants.OPEN_SEARCH
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Utils.Constants.RECEIVE_ID
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Utils.Constants.SEND_ID
import com.bharathvishal.messagecommunicationusingwearabledatalayer.Utils.Time
import com.bharathvishal.messagecommunicationusingwearabledatalayer.databinding.ActivityChatbotBinding
import kotlinx.android.synthetic.main.activity_chatbot.*

/*
import com.codepalace.chatbot.Api.RetrofitBuilder
import com.codepalace.chatbot.Data.Message
import com.codepalace.chatbot.Dto.ChatbotDto
import com.codepalace.chatbot.Dto.CorpusDto
import com.codepalace.chatbot.Dto.CorpusDto2
import com.codepalace.chatbot.databinding.ActivityChatbotBinding
import com.codepalace.chatbot.utils.*
import com.codepalace.chatbot.utils.Constants.RECEIVE_ID
import com.codepalace.chatbot.utils.Constants.SEND_ID
import com.codepalace.chatbot.utils.Constants.OPEN_GOOGLE
import com.codepalace.chatbot.utils.Constants.OPEN_SEARCH
import kotlinx.android.synthetic.main.activity_chatbot.*  */

import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private lateinit var binding: ActivityChatbotBinding

class Chatbot : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var speechRecognizer: SpeechRecognizer
    private var textToSpeech: TextToSpeech? = null


    //You can ignore this messageList if you're coming from the tutorial,
    // it was used only for my personal debugging
    var messagesList = mutableListOf<Message>()

    private lateinit var adapter: MessagingAdapter
    private val botList = listOf("?????????", "?????????", "?????????")
    private var corpuslist : List<CorpusDto> = listOf() //corpus ????????? ?????? ????????? ??????
    private lateinit var stage : String      //register??? ?????? ???????????? ????????? ??????
    private var chatresponse=""   //ai chatbot ??????

    override fun onCreate(savedInstanceState: Bundle?) {
        stage=intent.getStringExtra("stage")!!

        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        Corpuslist()

        // ?????? ??????
        requestPermission()

        setAlarm()

        // RecognizerIntent ??????
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // ????????? ???
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")         // ?????? ??????

        // <?????????> ?????? ????????? ???????????? ??????
        binding.btnSpeech.setOnClickListener {
            // ??? SpeechRecognizer ??? ????????? ????????? ?????????
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@Chatbot)
            speechRecognizer.setRecognitionListener(recognitionListener)    // ????????? ??????
            speechRecognizer.startListening(intent)                         // ?????? ??????
        }

        recyclerView()

        clickEvents()

        val random = (0..2).random()
        if(stage.equals("refuse")){
            customBotMessage("??????! ${botList[random]}, ?????? IMY???")
        }
        else if(stage.equals("bargain")){
            customBotMessage("??????! ${botList[random]}, ?????? ??? ???????")
        }
        else{
            customBotMessage("${botList[random]} ??????, ???????????? ??????????")
        }
    }

    private fun setAlarm() {
        textToSpeech = TextToSpeech(this@Chatbot, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS","??????????????? ???????????? ????????????.")
                    return@OnInitListener
                }
            }
        })
    }

    // ?????? ?????? ?????????
    private fun requestPermission() {
        // ?????? ??????, ?????? ??????????????? ??????
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this@Chatbot, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@Chatbot,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    // ????????? ??????
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        // ????????? ????????? ??????????????? ??????
        override fun onReadyForSpeech(params: Bundle) {
            Toast.makeText(applicationContext, "???????????? ??????", Toast.LENGTH_SHORT).show()
            binding.tvState.text = "?????? ???????????????!"
        }
        // ????????? ???????????? ??? ??????
        override fun onBeginningOfSpeech() {
            binding.tvState.text = "??? ?????? ?????????."
        }
        // ???????????? ????????? ????????? ?????????
        override fun onRmsChanged(rmsdB: Float) {}
        // ?????? ???????????? ????????? ??? ????????? buffer??? ??????
        override fun onBufferReceived(buffer: ByteArray) {}
        // ???????????? ???????????? ??????
        override fun onEndOfSpeech() {
            binding.tvState.text = "???!"
        }
        // ?????? ???????????? ??? ??????
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "????????? ??????"
                SpeechRecognizer.ERROR_CLIENT -> "??????????????? ??????"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "????????? ??????"
                SpeechRecognizer.ERROR_NETWORK -> "???????????? ??????"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "????????? ????????????"
                SpeechRecognizer.ERROR_NO_MATCH -> "?????? ??? ??????"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER ??? ??????"
                SpeechRecognizer.ERROR_SERVER -> "????????? ?????????"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "????????? ????????????"
                else -> "??? ??? ?????? ?????????"
            }
            binding.tvState.text = "?????? ??????: $message"
        }
        // ?????? ????????? ???????????? ??????
        override fun onResults(results: Bundle) {
            // ?????? ?????? ArrayList??? ????????? ?????? textView??? ????????? ?????????
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) binding.etMessage.setText(matches[i])
            sendMessage()

        }
        // ?????? ?????? ????????? ????????? ??? ?????? ??? ??????
        override fun onPartialResults(partialResults: Bundle) {}
        // ?????? ???????????? ???????????? ?????? ??????
        override fun onEvent(eventType: Int, params: Bundle) {}
    }

    private fun clickEvents() {

        //Send a message
        btn_send.setOnClickListener {
            sendMessage()
        }

        //Scroll back to correct position when user clicks on text view
        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(100)

                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)

                }
            }
        }
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)

    }

    override fun onStart() {
        super.onStart()
        //In case there are messages, scroll to bottom when re-opening app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun sendMessage() {
        val message = et_message.text.toString()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            //Adds it to our local list
            messagesList.add(Message(message, SEND_ID, timeStamp))
            et_message.setText("")

            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)

        }
    }

    private fun botResponse(message: String) {

        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            //Fake response delay
            delay(1000)
            var response=""

            withContext(Dispatchers.Main) {

                //Gets the response(3 case)
                if(stage.equals("refuse")) {
                    // response = BotResponseRefuse.basicResponses(message, corpuslist)
                    Chatbotlist(message)
                    response=chatresponse
                }
                else if(stage.equals("bargain")){
                    response = BotResponseBargain.basicResponses(message, corpuslist)
                }
                else{
                    response = BotResponseAccept.basicResponses(message, corpuslist)
                }
                //Adds it to our local list
                messagesList.add(Message(response, RECEIVE_ID, timeStamp))

                //Inserts our message into the adapter
                //adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))
                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.itemCount - 1)

                //Starts Google
                when (response) {
                    OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfterLast("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }

                }

                textToSpeech?.speak(response, TextToSpeech.QUEUE_FLUSH, null)
                textToSpeech?.playSilentUtterance(750,TextToSpeech.QUEUE_ADD,null) // deley?????? ??????
            }
        }
    }

    private fun customBotMessage(message: String) {

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                messagesList.add(Message(message, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount - 1)

                textToSpeech?.speak(message, TextToSpeech.QUEUE_FLUSH, null)
                textToSpeech?.playSilentUtterance(750,TextToSpeech.QUEUE_ADD,null) // deley?????? ??????
            }
        }
    }



    fun Corpuslist() {
        lateinit var call : Call<List<CorpusDto>>
        if(stage.equals("refuse")){
            call = RetrofitBuilder.corpusapi.getAllByMaincategoryResponse("??????")
        }
        else if(stage.equals("bargain")){
            call=RetrofitBuilder.corpusapi.getAllbyStatuskeywordResponse("??????, ??????, ??????")
        }
        else{
            call = RetrofitBuilder.corpusapi.getAllByMaincategoryResponse("??????")
        }


        Thread{
            call.enqueue(object : Callback<List<CorpusDto>> { // ????????? ?????? ?????? ?????????
                override fun onResponse( // ????????? ????????? ??????
                    call: Call<List<CorpusDto>>,
                    response: Response<List<CorpusDto>>
                ) {
                    if(response.isSuccessful()){ // ?????? ??? ?????? ??????
                        corpuslist= response.body()!!
                        println("corpuslist = ${corpuslist}")
                    }else{
                        // ?????? ?????? but ?????? ??????
                        Log.d("RESPONSE", "FAILURE")
                    }

                }

                override fun onFailure(call: Call<List<CorpusDto>>, t: Throwable) {
                    // ????????? ????????? ??????
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                }
            })
        }.start()

        try{
            Thread.sleep(50)
        } catch(e: Exception){
            e.printStackTrace()
        }

    }


    private suspend fun Chatbotlist(s: String) {
        withContext(Dispatchers.IO) {

            runCatching {
                val retrofit = RetrofitBuilder.chatbotapi.getKogpt2Response(s)
                val res = retrofit.execute().body()
                //res.code() == 200
                println("res = ${res}")
                chatresponse= res!!.answer
            }.getOrDefault(false)
        }

    }



}