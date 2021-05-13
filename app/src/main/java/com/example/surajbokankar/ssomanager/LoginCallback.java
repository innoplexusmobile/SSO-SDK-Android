package com.example.surajbokankar.ssomanager;

import com.example.surajbokankar.ssomanager.common.CommonListener;
import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.model.UserInfo;

import org.json.JSONException;

/**
 * Created by suraj.bokankar on 9/3/17.
 */

public interface  LoginCallback  {

      public void onSuccess(UserInfo userInfo);

      public void onError(ErrorResponse error) ;


}
