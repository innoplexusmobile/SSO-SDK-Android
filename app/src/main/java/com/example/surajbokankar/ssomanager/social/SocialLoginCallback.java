package com.example.surajbokankar.ssomanager.social;

import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.model.UserInfo;

/**
 * Created by suraj.bokankar on 25/5/17.
 */

public interface SocialLoginCallback {

    public void onSocialLoginSuccess(UserInfo loginSuccess);

    public  void onSocialoLoginFailure(ErrorResponse errorResponse);


}
