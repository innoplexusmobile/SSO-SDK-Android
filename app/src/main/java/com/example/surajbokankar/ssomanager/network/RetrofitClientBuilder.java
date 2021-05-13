package com.example.surajbokankar.ssomanager.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Created by suraj.bokankar on 9/3/17.
 */

public class RetrofitClientBuilder {

    private static final String TAG = "RetrofitBuilder";

   private static RetrofitClientBuilder retrofitClientBuilder=null;

   private OkHttpClient okHttpClient=null;

   private String Url=null;

   private String TokenId=null;


    public RetrofitClientBuilder setHTTPClient(final  Context context){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();

                if(TokenId!=null){
                    Log.i(TAG, "intercept: Token="+TokenId);
                    requestBuilder.header("Authorization",TokenId);
                }

                Request request = requestBuilder.build();


                return chain.proceed(request);
            }
        }).retryOnConnectionFailure(true).connectTimeout(40, TimeUnit.SECONDS).readTimeout(40, TimeUnit.SECONDS).build();

        this.okHttpClient=okHttpClient;

        return this;
    }




    public RetrofitClientBuilder setBaseUrl(String url){
        this.Url=url;
     return this;
    }



    public RetrofitClientBuilder setAuthorizationToken(String token){
        if(!TextUtils.isEmpty(token)){
                    Log.i(TAG, "intercept: Token="+token);
                    this.TokenId=token;
                }
        return  this;
    }


    public  Retrofit builder(){
        Retrofit retrofit=null;
        if(Url==null){
            Url="";
        }

        retrofit=new Retrofit.Builder().baseUrl(Url).addConverterFactory(JacksonConverterFactory.create()).client(okHttpClient).build();


        /*if(Url!=null){

        }else{
            retrofit=new Retrofit.Builder().addConverterFactory(JacksonConverterFactory.create()).client(okHttpClient).build();
        }*/
        return  retrofit;
    }



    public static RetrofitClientBuilder getInstance(){
        if(retrofitClientBuilder==null){
            retrofitClientBuilder=new RetrofitClientBuilder();
        }
       return  retrofitClientBuilder;
    }

}
