package com.wangxingxing.websocketdemo

import okhttp3.*
import okio.ByteString
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * author : 王星星
 * date : 2024/7/30 16:57
 * email : 1099420259@qq.com
 * description :
 */
class WebSocketManager(private val url: String) {

    private lateinit var client: OkHttpClient
    private var webSocket: WebSocket? = null
    private val listeners = mutableListOf<WebSocketListener>()

    fun addListener(listener: WebSocketListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: WebSocketListener) {
        listeners.remove(listener)
    }

    fun connect() {
        client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                listeners.forEach { it.onOpen(webSocket, response) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                listeners.forEach { it.onMessage(webSocket, text) }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                listeners.forEach { it.onMessage(webSocket, bytes) }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                listeners.forEach { it.onClosing(webSocket, code, reason) }
                webSocket.close(1000, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                listeners.forEach { it.onClosed(webSocket, code, reason) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                listeners.forEach { it.onFailure(webSocket, t, response) }
            }
        })
    }

    private fun initOkHttp() {
        // Example code for setting up a custom SSLSocketFactory
        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) { "Unexpected default trust managers:" + trustManagers.contentToString() }
        val trustManager = trustManagers[0] as X509TrustManager

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

        val client: OkHttpClient = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)
            .build()

    }

    fun disconnect() {
        webSocket?.close(1000, "Goodbye!")
        webSocket = null
        client.dispatcher.executorService.shutdown()
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun sendMessage(bytes: ByteString) {
        webSocket?.send(bytes)
    }
}

