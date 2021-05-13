package com.example.surajbokankar.ssomanager.common;

import com.example.surajbokankar.ssomanager.model.UserInfo;

/**
 * Created by suraj.bokankar on 24/3/17.
 */

public interface CommonCallback {

    public void onSuccess(UserInfo userInfo);

    public <T>  T onFailure(T callback);
}
