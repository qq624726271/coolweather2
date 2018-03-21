package com.chenxiayan.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**发送请求
 * Created by chenxiaoyan on 2018/3/21.
 */

public class HttpUtil {
    public static void sendOkhttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
