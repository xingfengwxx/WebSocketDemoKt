package com.wangxingxing.websocketdemo

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.gnepux.wsgo.EventListener
import com.gnepux.wsgo.WsConfig
import com.gnepux.wsgo.WsGo
import com.gnepux.wsgo.okwebsocket.OkWebSocket
import com.wangxingxing.websocketdemo.databinding.ActivityMainBinding
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    private val DEFAULT_URL = "wss://"
    private val DEFAULT_URL = "ws://192.168.20.65:12024"

    private val DEFAULT_HEADER: HashMap<String?, String?> = object : HashMap<String?, String?>() {}

    private val TIME_OUNT = 10 * 1000L

    private val logLiveData = MutableLiveData<String>()

    private lateinit var webSocketManager: WebSocketManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWebSocket(DEFAULT_URL)
//        initWebSocketWithOkHttp()
        initView()
        initObserver()
    }

    private fun initWebSocket(url: String) {
        val config = WsConfig.Builder()
            .debugMode(true)
            .setUrl(url)
            .setHttpHeaders(DEFAULT_HEADER)
            .setWebSocket(OkWebSocket.create())
            .setConnectTimeout(TIME_OUNT)
            .setReadTimeout(TIME_OUNT)
            .setWriteTimeout(TIME_OUNT)
            .setPingInterval(TIME_OUNT)
            .setRetryStrategy { retryCount ->
                // 重试策略
                if (retryCount < 2) {
                    0
                } else if (retryCount < 5) {
                    5 * 1000
                } else {
                    10 * 1000
                }
            }
            .setEventListener(object : EventListener {
                override fun onConnect() {
                    printLog("[connect] success")
                }

                override fun onDisConnect(throwable: Throwable) {
                    printLog("[disconnect] " + throwable.message)
                }

                override fun onClose(code: Int, reason: String) {
                    printLog("[close] code = $code, reason = $reason")
                }

                override fun onMessage(text: String) {
                    printLog("[receive] $text")
                }

                override fun onReconnect(retryCount: Long, delayMillSec: Long) {
                    printLog("[reconnect] " + retryCount + " times retry after " + delayMillSec + "ms")
                }

                override fun onSend(text: String, success: Boolean) {
                    printLog("[send] text = $text , success = $success")
                }
            })
            .build()

        WsGo.init(config)
    }

    private fun initWebSocketWithOkHttp() {
        webSocketManager = WebSocketManager(DEFAULT_URL)
        webSocketManager.addListener(object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                printLog("onOpen: ${response.message}")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                printLog("onMessage: $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                printLog("onMessage: $bytes")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                printLog("onClosing: code=$code, reason: $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                printLog("onClosed: code=$code, reason: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                printLog("onClosed: exception: ${t.message}, response: ${response?.message}")
            }
        })

        webSocketManager.connect()
    }

    private fun initView() {
        binding.tvLog.movementMethod = ScrollingMovementMethod()

        binding.btnConnect.setOnClickListener {
            WsGo.getInstance().connect()
        }

        binding.btnDisconnect.setOnClickListener {
            // disconnect
//            WsGo.getInstance().disconnect(1000, "close")
            WsGo.getInstance().disconnectNormal("close")
        }

        binding.btnSend.setOnClickListener {
            var msg = binding.etText.text.toString()
            if (msg.isNullOrEmpty()) {
                msg = "send msg ---${System.currentTimeMillis()}"
            }
            WsGo.getInstance().send(msg)

//            webSocketManager.sendMessage(msg)
        }
    }

    private fun initObserver() {
        val sb = StringBuilder()
        logLiveData.observe(this) {
            sb.append(it)
            sb.appendLine()
            binding.tvLog.text = sb.toString()
        }
    }

    private fun printLog(msg: String) {
        LogUtils.i(msg)
        logLiveData.postValue(msg)
    }



    override fun onDestroy() {
        super.onDestroy()
        webSocketManager.disconnect()
    }
}