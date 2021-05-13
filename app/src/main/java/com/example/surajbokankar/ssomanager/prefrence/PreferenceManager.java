package com.example.surajbokankar.ssomanager.prefrence;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.transition.ActionBarTransition;

import com.example.surajbokankar.ssomanager.model.UserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;



/**
 * Created by suraj.bokankar on 9/3/17.
 */

public class PreferenceManager {
    static PreferenceManager preferenceManager=null;
    SharedPreferences sharedPreferences=null;
    static SharedPreferences.Editor editor=null;
    UserInfo userInfo;
    ObjectMapper objectMapper;


    public PreferenceManager getInstance(Context context){
        if(preferenceManager==null){
            preferenceManager=new PreferenceManager();
        }
        sharedPreferences=context.getSharedPreferences(PreferenceString.preferenceString,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    return  preferenceManager;
    }


    public interface PreferenceString{
        String preferenceString="kplexus";
        String tokenString="token";
        String EmptyString="";
        String UserDetails="userInfo";
    }


    public void storeUserToken(String tokenId){
        editor.putString(PreferenceString.tokenString,tokenId);
        doCommit();
    }

    public String getUserToken(){
        return sharedPreferences.getString(PreferenceString.tokenString,PreferenceString.EmptyString);
    }


    public UserInfo getUserInfo() {
        String userDetails=sharedPreferences.getString(PreferenceString.UserDetails,PreferenceString.EmptyString);
        try {
            userInfo=objectMapper.readValue(userDetails,UserInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        objectMapper=new ObjectMapper();
        try {
            String userInfoString=objectMapper.writeValueAsString(userInfo);
            editor.putString(PreferenceString.UserDetails,userInfoString);
            doCommit();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private void doCommit() {
        if ( editor != null) {
            editor.commit();
            editor = null;
        }
    }



}
