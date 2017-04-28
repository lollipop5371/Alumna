package com.example.alumna.utils;



import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/28.
 */

public class HttpConnectUtil {

    String Url;
    String Data;

    public HttpConnectUtil(String url,String data){
        Url=url;
        Data=data;

    }

    public void HttpPost(){

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, Data);

        OkHttpClient client=new OkHttpClient();


        Request request = new Request.Builder()
                .post(body)
                .url(Url)
                .build();

        try {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //失败
                    System.out.println("failed");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println("success");

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }


    }

}
