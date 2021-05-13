package com.example.surajbokankar.ssomanager.social;

import android.content.Context;

import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.model.UserInfo;
import com.example.surajbokankar.ssomanager.social.model.SocialLoginSuccess;

/**
 * Created by suraj.bokankar on 25/5/17.
 */

public interface SocialLoginCallback {

    public void onSocialLoginSuccess(UserInfo loginSuccess);

    public  void onSocialoLoginFailure(ErrorResponse errorResponse);


}
