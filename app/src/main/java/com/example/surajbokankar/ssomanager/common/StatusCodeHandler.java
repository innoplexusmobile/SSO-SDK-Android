package com.example.surajbokankar.ssomanager.common;

import android.util.Log;

import java.util.HashMap;


/**
 * Created by suraj.bokankar on 10/3/17.
 */

public class StatusCodeHandler {
    private static final String TAG = "StatusCodeHandler";
   public static HashMap<String,Boolean> statusCodeCheck(int statusCode){
        HashMap<String,Boolean> hashMap=new HashMap<>();

       Log.i(TAG, "statusCodeCheck: Code="+statusCode);
        switch (statusCode){

            case  200:
                hashMap.put(Constant.LoginRequest.isRequetSuccess,true);
                hashMap.put(Constant.LoginRequest.isAuthFailed,false);
                break;

            case  400:
                hashMap.put(Constant.LoginRequest.isRequetSuccess,false);
                hashMap.put(Constant.LoginRequest.isAuthFailed,false);
                break;
            case  401:
                hashMap.put(Constant.LoginRequest.isRequetSuccess,false);
                hashMap.put(Constant.LoginRequest.isAuthFailed,true);
                break;
            case  404:
                hashMap.put(Constant.LoginRequest.isRequetSuccess,false);
                hashMap.put(Constant.LoginRequest.isAuthFailed,false);
                break;

            case  500:
                hashMap.put(Constant.LoginRequest.isRequetSuccess,false);
                hashMap.put(Constant.LoginRequest.isAuthFailed,false);
                break;
            case  501:
                hashMap.put(Constant.LoginRequest.isRequetSuccess,false);
                hashMap.put(Constant.LoginRequest.isAuthFailed,false);
                break;


        }

        return  hashMap;
    }


    public static boolean isResponseCodeValidated(int responseCode,int responseCodeMin,int responseCodeMax){
        return responseCode>=responseCodeMin&&responseCode<responseCodeMax;
    }

}
