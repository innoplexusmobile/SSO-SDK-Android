package com.example.surajbokankar.ssomanager.prefrence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.surajbokankar.ssomanager.model.LoginParentPojo;
import com.example.surajbokankar.ssomanager.model.UserInfo;
import com.example.surajbokankar.ssomanager.sharesession.SessionModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


/**
 * Created by suraj.bokankar on 9/3/17.
 */

public class PreferenceManager {
    static PreferenceManager preferenceManager=null;
    static SharedPreferences sharedPreferences=null;
    static SharedPreferences.Editor editor=null;
    UserInfo userInfo;
    ObjectMapper objectMapper;
    LoginParentPojo parentResponse=null;
    SessionModel sessionModel=null;
    private static final String TAG = "PreferenceManager";


    public static PreferenceManager getInstance(Context context){
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
        String loginDetails="login";
        String session="session";
        String sessionKey="sessionKey";
        String password="password";
    }


    public void storeUserToken(String tokenId){
        editor.putString(PreferenceString.tokenString,tokenId);
        doCommit();
    }

    public String getUserToken(){
        return sharedPreferences.getString(PreferenceString.tokenString, PreferenceString.EmptyString);
    }


    public UserInfo getUserInfo() {
        String userDetails=sharedPreferences.getString(PreferenceString.UserDetails, PreferenceString.EmptyString);
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



    public SessionModel getSessionModel() {
        ObjectMapper objectMapper=new ObjectMapper();
        String userDetails=sharedPreferences.getString(PreferenceString.session, PreferenceString.EmptyString);
        try {
            sessionModel=objectMapper.readValue(userDetails,SessionModel.class);
        } catch (Exception e) {
            Log.i(TAG, "getSessionModel: Error="+e.getMessage());
            e.printStackTrace();
        }
        return sessionModel;
    }

    public void setSessionModel(SessionModel sessionModel) {
        ObjectMapper objectMapper=null;
        this.sessionModel = sessionModel;
        objectMapper=new ObjectMapper();
        try {
            String userInfoString=objectMapper.writeValueAsString(sessionModel);
            editor.putString(PreferenceString.session,userInfoString);
            doCommit();
        } catch (Exception e) {
            Log.i(TAG, "getSessionModel: Error="+e.getMessage());
            e.printStackTrace();
        }

    }

    private void doCommit() {
        if ( editor != null) {
            editor.commit();
            editor = null;
        }
    }

    public void storeSessionKey(String session){
        editor.putString(PreferenceString.sessionKey,session);
        doCommit();
    }

    public String getSessionKey(){
        return sharedPreferences.getString(PreferenceString.sessionKey, PreferenceString.EmptyString);
    }

    public void storeUserPassword(String password){
        editor.putString(PreferenceString.password,password);
        doCommit();
    }

    public String getPassword(){
        return sharedPreferences.getString(PreferenceString.password, PreferenceString.EmptyString);
    }



}
