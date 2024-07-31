package com.gnepux.wsgo.okwebsocket;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @author gnepux
 */
public class OkUtils {

    /**
     * generate an OkHttp Client
     */
    public static OkHttpClient generateClient(long interval, long connectTimeout, long readTimeout, long writeTimeout) {
        try {
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
            SSLSocketFactory socketFactory = sslContext.getSocketFactory();
            return new OkHttpClient.Builder()
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .pingInterval(interval, TimeUnit.MILLISECONDS)
                    .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                    .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(socketFactory)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OkHttpClient generateUnsafeClient(long interval, long connectTimeout, long readTimeout, long writeTimeout) {
        try {
            // 获取默认的TrustManagerFactory实例
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            // 确保TrustManager数组只包含一个X509TrustManager实例
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + java.util.Arrays.toString(trustManagers));
            }

            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

            // 设置SSL上下文
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // 构建OkHttpClient实例
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .build();
            return client;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * generate an OkHttp Request
     */
    public static Request generateRequest(HashMap<String, String> headers, String url) throws IllegalArgumentException, NullPointerException {
        Request.Builder requestBuilder = new Request.Builder();
        if (headers != null) {
            for (HashMap.Entry<String, String> entry : headers.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                requestBuilder.addHeader(key, value);
            }
        }
        requestBuilder.url(url);
        return requestBuilder.build();
    }
}
