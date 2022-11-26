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
    private val botList = listOf("상우야", "정주야", "승규야")
    private var corpuslist : List<CorpusDto> = listOf() //corpus 데이터 받을 리스트 변수
    private lateinit var stage : String      //register로 부터 입력받은 우울증 단계
    private var chatresponse=""   //ai chatbot 답변

    override fun onCreate(savedInstanceState: Bundle?) {
        stage=intent.getStringExtra("stage")!!

        super.onCreate(savedInstanceState)
        binding = ActivityChatbotBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        Corpuslist()

        // 권한 설정
        requestPermission()

        setAlarm()

        // RecognizerIntent 생성
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)    // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")         // 언어 설정

        // <말하기> 버튼 눌러서 음성인식 시작
        binding.btnSpeech.setOnClickListener {
            // 새 SpeechRecognizer 를 만드는 팩토리 메서드
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this@Chatbot)
            speechRecognizer.setRecognitionListener(recognitionListener)    // 리스너 설정
            speechRecognizer.startListening(intent)                         // 듣기 시작
        }

        recyclerView()

        clickEvents()

        val random = (0..2).random()
        if(stage.equals("refuse")){
            customBotMessage("안녕! ${botList[random]}, 나는 IMY야")
        }
        else if(stage.equals("bargain")){
            customBotMessage("안녕! ${botList[random]}, 무슨 일 있어?")
        }
        else{
            customBotMessage("${botList[random]} 안녕, 무슨일이 있었니?")
        }
    }

    private fun setAlarm() {
        textToSpeech = TextToSpeech(this@Chatbot, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                val result = textToSpeech!!.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS","해당언어는 지원되지 않습니다.")
                    return@OnInitListener
                }
            }
        })
    }

    // 권한 설정 메소드
    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (Build.VERSION.SDK_INT >= 23 &&
            ContextCompat.checkSelfPermission(this@Chatbot, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this@Chatbot,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0)
        }
    }

    // 리스너 설정
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        // 말하기 시작할 준비가되면 호출
        override fun onReadyForSpeech(params: Bundle) {
            Toast.makeText(applicationContext, "음성인식 시작", Toast.LENGTH_SHORT).show()
            binding.tvState.text = "이제 말씀하세요!"
        }
        // 말하기 시작했을 때 호출
        override fun onBeginningOfSpeech() {
            binding.tvState.text = "잘 듣고 있어요."
        }
        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}
        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}
        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            binding.tvState.text = "끝!"
        }
        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트웍 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            binding.tvState.text = "에러 발생: $message"
        }
        // 인식 결과가 준비되면 호출
        override fun onResults(results: Bundle) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) binding.etMessage.setText(matches[i])
            sendMessage()

        }
        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {}
        // 향후 이벤트를 추가하기 위해 예약
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
                textToSpeech?.playSilentUtterance(750,TextToSpeech.QUEUE_ADD,null) // deley시간 설정
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
                textToSpeech?.playSilentUtterance(750,TextToSpeech.QUEUE_ADD,null) // deley시간 설정
            }
        }
    }



    fun Corpuslist() {
        lateinit var call : Call<List<CorpusDto>>
        if(stage.equals("refuse")){
            call = RetrofitBuilder.corpusapi.getAllByMaincategoryResponse("분노")
        }
        else if(stage.equals("bargain")){
            call=RetrofitBuilder.corpusapi.getAllbyStatuskeywordResponse("연애, 결혼, 출산")
        }
        else{
            call = RetrofitBuilder.corpusapi.getAllByMaincategoryResponse("슬픔")
        }


        Thread{
            call.enqueue(object : Callback<List<CorpusDto>> { // 비동기 방식 통신 메소드
                override fun onResponse( // 통신에 성공한 경우
                    call: Call<List<CorpusDto>>,
                    response: Response<List<CorpusDto>>
                ) {
                    if(response.isSuccessful()){ // 응답 잘 받은 경우
                        corpuslist= response.body()!!
                        println("corpuslist = ${corpuslist}")
                    }else{
                        // 통신 성공 but 응답 실패
                        Log.d("RESPONSE", "FAILURE")
                    }

                }

                override fun onFailure(call: Call<List<CorpusDto>>, t: Throwable) {
                    // 통신에 실패한 경우
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