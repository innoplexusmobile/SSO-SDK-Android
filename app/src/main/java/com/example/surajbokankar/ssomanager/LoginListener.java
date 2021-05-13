package com.example.surajbokankar.ssomanager;


import com.example.surajbokankar.ssomanager.model.LoginSuccessResponse;

/**
 * Created by suraj.bokankar on 15/10/18.
 */

public interface LoginListener {

    public void onError(String error);

    public void onLoginSuccess(LoginSuccessResponse userInfo);

    public void onSignUpForgotPasswordSuccess(String message);


}
