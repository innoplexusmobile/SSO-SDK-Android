package com.example.surajbokankar.ssomanager.social;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.surajbokankar.ssomanager.LoginCallback;
import com.example.surajbokankar.ssomanager.R;
import com.example.surajbokankar.ssomanager.common.CommonListener;
import com.example.surajbokankar.ssomanager.common.Constant;
import com.example.surajbokankar.ssomanager.common.StatusCodeHandler;
import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.model.LoginParentPojo;
import com.example.surajbokankar.ssomanager.model.ResponseData;
import com.example.surajbokankar.ssomanager.model.UserInfo;
import com.example.surajbokankar.ssomanager.network.NetworkManager;
import com.example.surajbokankar.ssomanager.network.RetrofitRequestBuilder;
import com.example.surajbokankar.ssomanager.ssomanager.SSOManager;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;


import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suraj.bokankar on 24/5/17.
 */

public class SocialPlugin {

    private static final String TAG = "SocialPlugin";
    static SocialPlugin socialPlugin = null;
    static Context mContext = null;

    public static SocialPlugin getInstance(Context context) {
        if (socialPlugin == null) {
            socialPlugin = new SocialPlugin();
            mContext = context;
        }
        return socialPlugin;
    }



    public void setFBConfig(Context context, String fbAppId) {

        FacebookSdk.sdkInitialize(context);
        FacebookSdk.setApplicationId(fbAppId);

    }

    public void setGoogleConfig(Context context, String fbAppId) {
        FacebookSdk.sdkInitialize(context);
        FacebookSdk.setApplicationId(fbAppId);

    }

    public void setLinkedInConfig(Context context) {
        //LISessionManager.getInstance(context);
    }

    public void callSocialLoginApi(Context context, String accessToken, String loginType, JSONObject responseJson, final LoginCallback loginCallback) {
        if (NetworkManager.getInstance().isConnectingToInternet(context)) {
            RetrofitRequestBuilder.getInstance(context).socialLogin(context, accessToken, responseJson, loginType, new Callback<LoginParentPojo>() {
                @Override
                public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {
                    ErrorResponse errorResponse = new ErrorResponse();
                    if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                        //do your stuffs here
                        LoginParentPojo loginResponse = response.body();
                        if (loginResponse != null) {
                            ResponseData data = loginResponse.responseData;
                            if (data.otpEnabled) {
                                SSOManager.getInstance(mContext).setOtpCheck(true);
                                SSOManager.getInstance(mContext).setSessionKey(response.headers().get(Constant.Authorization));
                                loginCallback.onSuccess(null);

                            } else {
                                if (data.info != null) {
                                    callBackMethod(data, null, loginCallback);

                                } else {
                                    errorResponse.errorMessageString = "User Info blank";
                                    loginCallback.onError(errorResponse);
                                }
                            }


                        } else {
                            errorResponse.errorMessageString = response.message();
                            loginCallback.onError(errorResponse);
                        }


                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            seterrorResponse(jObjError, loginCallback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }

                @Override
                public void onFailure(Call<LoginParentPojo> call, Throwable t) {

                }
            });

        } else {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.errorMessageString = context.getResources().getString(R.string.network_error);
            loginCallback.onError(errorResponse);
        }
    }

    private void callBackMethod(ResponseData data, String password, final LoginCallback loginCallback) {
        final ErrorResponse errorResponse = new ErrorResponse();
        final UserInfo userInfo = data.info;
        boolean isCallBackPresent = getCallBackUrl(data.callbackUrl);
        //From Here we will fetch token for further usage with callback url from login api.
        if (isCallBackPresent) {
            SSOManager.getInstance(mContext).fetchTokenApi(password, new CommonListener() {
                @Override
                public void onSuccess() {
                    loginCallback.onSuccess(userInfo);
                    SSOManager.getInstance(mContext).setUserStatus(true);
                }

                @Override
                public void onFailure(String error) {
                    errorResponse.errorMessageString = error;
                    loginCallback.onError(errorResponse);
                }
            });
        }
    }

    private static boolean getCallBackUrl(String url) {
        boolean isCallBackUrl = false;
        String code = "";
        if (!TextUtils.isEmpty(url) && url.contains("=")) {
            code = url.split("=")[1];
            isCallBackUrl = true;
        }
        Log.i(TAG, "getCallBackUrl: Code=" + code);
        SSOManager.getInstance(mContext).setRedirectCode(code);
        return isCallBackUrl;
    }


    public void seterrorResponse(JSONObject json, LoginCallback callBack) {
        try {
            ErrorResponse errorResponse = new ErrorResponse();
            String errorCode = json.getString(Constant.errorMessage);
            errorResponse.errorCode = errorCode;
            errorResponse.errorMessageString = json.getString(Constant.errorMessage);
            callBack.onError(errorResponse);
        } catch (Exception e) {
            Log.e(TAG, "seterrorResponse: Error", e);
        }
    }

}
