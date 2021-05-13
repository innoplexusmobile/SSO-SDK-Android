package com.example.surajbokankar.ssomanager.common;

import android.util.Log;

import com.example.surajbokankar.ssomanager.model.SuccessModel;

import org.json.JSONObject;

import okhttp3.ResponseBody;

/**
 * Created by suraj.bokankar on 05/11/18.
 */

public class ErrorHandler<T> {
    public static  ErrorHandler errorHandler=null;
    private static final String TAG = "ErrorHandler";



    public static ErrorHandler getInstance(){
        if(errorHandler==null){
            errorHandler=new ErrorHandler();
        }
        return errorHandler;
    }




    public SuccessModel getErrorMessage(ResponseBody data){
        SuccessModel model=new SuccessModel();

        try{
            JSONObject object= new JSONObject(data.string());
            model.error=object.getString("isError");
            model.message=object.getString("message");
        }catch (Exception e){
            Log.i(TAG, "getErrorMessage: Error="+e.getMessage());
        }
        return model;
    }





}
