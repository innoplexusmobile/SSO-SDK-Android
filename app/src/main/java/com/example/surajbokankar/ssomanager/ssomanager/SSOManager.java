package com.example.surajbokankar.ssomanager.ssomanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.surajbokankar.ssomanager.LoginCallback;
import com.example.surajbokankar.ssomanager.R;
import com.example.surajbokankar.ssomanager.common.AuthResponseInterface;
import com.example.surajbokankar.ssomanager.common.CommonCallback;
import com.example.surajbokankar.ssomanager.common.CommonListener;
import com.example.surajbokankar.ssomanager.common.Constant;
import com.example.surajbokankar.ssomanager.common.StatusCodeHandler;
import com.example.surajbokankar.ssomanager.common.UrlConstant;
import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.model.LoginParentPojo;
import com.example.surajbokankar.ssomanager.model.ResponseData;
import com.example.surajbokankar.ssomanager.model.UserInfo;
import com.example.surajbokankar.ssomanager.model.signup.AuthResponseData;
import com.example.surajbokankar.ssomanager.model.signup.RequestParentPojo;
import com.example.surajbokankar.ssomanager.model.userauth.AccessTokenParentPojo;
import com.example.surajbokankar.ssomanager.model.userauth.Filters;
import com.example.surajbokankar.ssomanager.model.userauth.Groups;
import com.example.surajbokankar.ssomanager.network.NetworkManager;
import com.example.surajbokankar.ssomanager.network.RetrofitRequestBuilder;
import com.example.surajbokankar.ssomanager.social.FacebookManager;

import com.example.surajbokankar.ssomanager.social.LinkedInManager;
import com.example.surajbokankar.ssomanager.social.SocialLoginCallback;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Suraj.Bokankar on 9/3/17.
 */

public class SSOManager {

    private static final String TAG = "SSOManager";
    private static SSOManager ssoManager = null;
    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;
    private static UserInfo userInfo;
    private static Context mContext;
    private ObjectMapper objectMapper;
    String tokenId;
    boolean isRenewTokenEnabled = false;

    @SuppressLint("CommitPrefEdits")
    public static SSOManager getInstance(Context context) {
        if (ssoManager == null) {
            ssoManager = new SSOManager();
        }
        sharedPreferences = context.getSharedPreferences(Constant.PreferenceString.preferenceString, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mContext = context;
        return ssoManager;
    }

    private static boolean getCallBackUrl(String url) {
        boolean isCallBackUrl = false;
        String code = "";
        if (!TextUtils.isEmpty(url) && url.contains("=")) {
            code = url.split("=")[1];
            isCallBackUrl = true;
        }
        Log.i(TAG, "getCallBackUrl: Code="+code);
        SSOManager.getInstance(mContext).setRedirectCode(code);
        return isCallBackUrl;
    }

    //User returns user Token and triggers back with response that user is login Success.

    //Login user using Email and Password which return UserInfo  and gives a call to get AccessToken
    public void login(final String email, final String password, final LoginCallback loginCallback) {

        try {
            final ErrorResponse errorResponse=new ErrorResponse();
            if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {
                RetrofitRequestBuilder.getInstance(mContext).callUserAuthenticationApi(mContext, email, password, new Callback<LoginParentPojo>() {
                    @Override
                    public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {


                        onLoginOrSubscribeSuccess(response,mContext,loginCallback);

                        /* HashMap<String, Boolean> requestStatusMap = StatusCodeHandler.statusCodeCheck(response.code());
                        if (requestStatusMap.containsKey(Constant.LoginRequest.isRequetSuccess)) {

                            if (requestStatusMap.get(Constant.LoginRequest.isRequetSuccess)) {
                                //do your stuffs here
                                LoginParentPojo loginResponse = response.body();
                                if (loginResponse != null) {
                                    ResponseData data = loginResponse.responseData;
                                    if(data.otpEnabled){
                                        setOtpCheck(true);
                                        Log.i(TAG, "onResponse: Session="+response.headers().get(Constant.Authorization));
                                        setSessionKey(response.headers().get(Constant.Authorization));
                                        loginCallback.onSuccess(null);

                                    }else{
                                        if(data.callbackUrl!=null){
                                            callBackMethod(data,password,loginCallback);

                                        }else{
                                            setSessionKey(response.headers().get(Constant.Authorization));
                                            getCallBackUrlOTPEnabledState(mContext,loginCallback);
                                        }
                                    }


                                } else {
                                    errorResponse.errorMessageString=response.message();
                                    loginCallback.onError(errorResponse);
                                }

                            } else {
                                errorResponse.errorMessageString=response.message();
                                loginCallback.onError(errorResponse);
                            }
                        }else{
                            if(response.code()== Constant.SUBSCRIBE_ERROR){
                                setSessionKey(response.headers().get(Constant.Authorization));
                                callSubscribeUserApi(mContext,email,password,loginCallback);
                            }else{
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    seterrorResponse(jObjError,loginCallback);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }*/
                    }

                    @Override
                    public void onFailure(Call<LoginParentPojo> call, Throwable t) {
                        errorResponse.errorMessageString=t.getMessage();
                        loginCallback.onError(errorResponse);
                    }
                });
            } else {
                errorResponse.errorMessageString=mContext.getResources().getString(R.string.network_error);
                loginCallback.onError(errorResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "login: Error=", e);
        }


    }

    private void callBackMethod(ResponseData data,String password,final LoginCallback loginCallback) {
        final ErrorResponse errorResponse=new ErrorResponse();
        final UserInfo userInfo = data.info;
        boolean isCallBackPresent = getCallBackUrl(data.callbackUrl);
        //From Here we will fetch token for further usage with callback url from login api.
        if (isCallBackPresent) {
            SSOManager.getInstance(mContext).fetchTokenApi(password, new CommonListener() {
                @Override
                public void onSuccess() {
                    loginCallback.onSuccess(userInfo);
                    setUserStatus(true);
                }

                @Override
                public void onFailure(String error) {
                    errorResponse.errorMessageString=error;
                    loginCallback.onError(errorResponse);
                }
            });
        }
    }



    private void callBackUrlSignUpInvite(final AuthResponseData data, String password, final AuthResponseInterface listner) {
        final ErrorResponse errorResponse=new ErrorResponse();
        boolean isCallBackPresent = getCallBackUrl(data.callbackUrl);
        //From Here we will fetch token for further usage with callback url from login api.
        if (isCallBackPresent) {
            SSOManager.getInstance(mContext).fetchTokenApi(password, new CommonListener() {
                @Override
                public void onSuccess() {
                    UserInfo userInfo=null;
                    if(data.info!=null){
                        userInfo = data.info;
                    }
                    listner.onSuccess(userInfo);
                    setUserStatus(true);
                }

                @Override
                public void onFailure(String error) {
                    errorResponse.errorMessageString=error;
                    listner.onFailureError(errorResponse);
                }
            });
        }
    }





    // It Fetches user token adn store it into local shared preference
    public void fetchTokenApi(final String password, final CommonListener listener) {


        RetrofitRequestBuilder.getInstance(mContext).getAccessTokenApi(mContext, new Callback<AccessTokenParentPojo>() {
            @Override
            public void onResponse(Call<AccessTokenParentPojo> call, Response<AccessTokenParentPojo> response) {
                HashMap<String, Boolean> hashMap = StatusCodeHandler.statusCodeCheck(response.code());
                if (StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX)) {
                    AccessTokenParentPojo responseData = response.body();
                    if (responseData != null) {
                        setUserToken(responseData);
                        UserInfo userInfo = responseData.profile;
                        userInfo.passWord = password;
                        SSOManager.getInstance(mContext).setUserInfo(userInfo);
                        SSOManager.getInstance(mContext).setFilterInfo(responseData);
                        listener.onSuccess();
                    }

                } else {
                    listener.onFailure(response.message());
                }
            }

            @Override
            public void onFailure(Call<AccessTokenParentPojo> call, Throwable t) {
                Log.i(TAG, "onFailure: Access Token Error=" + t.getMessage());
                listener.onFailure(t.getMessage());
            }
        });


    }

    public void forgotPassword(String emailId,final AuthResponseInterface listener){
        try{
            final ErrorResponse errorResponse=new ErrorResponse();
            if(NetworkManager.getInstance().isConnectingToInternet(mContext)){
                RetrofitRequestBuilder.getInstance(mContext).forgotPassword(emailId, new Callback<RequestParentPojo>() {
                    @Override
                    public void onResponse(Call<RequestParentPojo> call, Response<RequestParentPojo> response) {
                        if(StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX)){
                            AuthResponseData responseData=response.body().authResponseData;
                            if(responseData!=null){
                                listener.onSuccess(responseData);
                            }

                        }else{
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                setError(jObjError,listener);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestParentPojo> call, Throwable t) {
                        errorResponse.errorMessageString=t.getMessage();
                        listener.onFailureError(errorResponse);
                    }
                });
            }else{
                errorResponse.errorMessageString=mContext.getResources().getString(R.string.network_error);
                listener.onFailureError(errorResponse);
            }
        }catch (Exception e){
            Log.i(TAG, "forgotPassword: Error="+e.getMessage());
        }
    }


    public void changePassword(String oldPassword,String newPassword ,String confirmPassword, final AuthResponseInterface listener){
        final ErrorResponse errorResponse=new ErrorResponse();
        if(NetworkManager.getInstance().isConnectingToInternet(mContext)){
            RetrofitRequestBuilder.getInstance(mContext).changeUserPassword(oldPassword, newPassword, confirmPassword, new Callback<RequestParentPojo>() {
                @Override
                public void onResponse(Call<RequestParentPojo> call, Response<RequestParentPojo> response) {

                    if(StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX)){
                        AuthResponseData responseData=response.body().authResponseData;
                        if(responseData!=null){
                            listener.onSuccess(responseData);
                        }
                    }else{
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            setError(jObjError,listener);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<RequestParentPojo> call, Throwable t) {
                    Log.i(TAG, "onResponse: ChangePassword="+t.getMessage());
                    errorResponse.errorMessageString=t.getMessage();
                    listener.onFailureError(errorResponse);
                }
            });
        }else{
            errorResponse.errorMessageString= mContext.getResources().getString(R.string.network_error);
            listener.onFailureError(errorResponse);

        }
    }




    public void logoutUser(final String emailId, final AuthResponseInterface listener){
        final ErrorResponse errorResponse=new ErrorResponse();
        if(NetworkManager.getInstance().isConnectingToInternet(mContext)){
            String tokenId=SSOManager.getInstance(mContext).getTokenId();
            if(isUserLoggedIn()){
                RetrofitRequestBuilder.getInstance(mContext).logoutUser(mContext,emailId, new Callback<RequestParentPojo>() {
                    @Override
                    public void onResponse(Call<RequestParentPojo> call, final Response<RequestParentPojo> response) {
                        if(StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX)){
                            AuthResponseData responseData=response.body().authResponseData;
                            Log.i(TAG, "onResponse: Logout");
                            logout();
                            listener.onSuccess(responseData);

                        }else{
                            if(response.code()==Constant.AUTH_FAIL){
                                SSOManager.getInstance(mContext).renewTokenApi(mContext, new LoginCallback() {
                                    @Override
                                    public void onSuccess(UserInfo userInfo) {
                                        if(StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX))  {
                                            logoutUser(emailId,listener);
                                        }else{
                                            AuthResponseData authResponseData=new AuthResponseData();
                                            authResponseData.info=null;
                                            authResponseData.message=mContext.getResources().getString(R.string.logout_success);
                                            logout();
                                            listener.onSuccess(authResponseData);
                                        }
                                    }

                                    @Override
                                    public void onError(ErrorResponse error) {
                                        AuthResponseData authResponseData=new AuthResponseData();
                                        authResponseData.info=null;
                                        authResponseData.message=mContext.getResources().getString(R.string.logout_success);
                                        logout();
                                        listener.onSuccess(authResponseData);
                                    }
                                });
                            }else{
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    setError(jObjError,listener);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }

                    }

                    @Override
                    public void onFailure(Call<RequestParentPojo> call, Throwable t) {
                        errorResponse.errorMessageString=t.getMessage();
                        listener.onFailureError(errorResponse);
                    }
                });

            }else{
                errorResponse.errorMessageString=mContext.getResources().getString(R.string.blankToken);
                listener.onFailureError(errorResponse);
                Log.i(TAG, "logoutUser: TokenId Blank");
            }
        }else{
            errorResponse.errorMessageString=mContext.getResources().getString(R.string.network_error);
            listener.onFailureError(errorResponse);
        }

    }



    public  void signUpInviteUser(boolean isActive,String emailId,final AuthResponseInterface listener){
        final ErrorResponse errorResponse=new ErrorResponse();
        if(NetworkManager.getInstance().isConnectingToInternet(mContext)){
            RetrofitRequestBuilder.getInstance(mContext).signUpUser(isActive, emailId, new Callback<RequestParentPojo>() {
                @Override
                public void onResponse(Call<RequestParentPojo> call, Response<RequestParentPojo> response) {
                    if(response.code()==Constant.STATUS_OK){
                        AuthResponseData responseData=response.body().authResponseData;
                        if(responseData!=null){
                            UserInfo data=responseData.info;

                            if(responseData!=null){
                                if(responseData.otpEnabled)
                                    setOtpCheck(true);

                                Log.i(TAG, "onResponse: Session="+response.headers().get(Constant.Authorization));
                                setSessionKey(response.headers().get(Constant.Authorization));
                                callBackUrlSignUpInvite(responseData,"",listener);

                            }else{
                                errorResponse.errorMessageString="User Info blank";
                                listener.onFailureError(errorResponse);
                            }

                        }


                    }
                    else{
                        if(response.code()==Constant.STATUS_SUCCESS){
                            if(response.body().errorMessage!=null){
                                errorResponse.errorMessageString=response.body().errorMessage;
                            } else {
                                errorResponse.errorMessageString=Constant.SUCCESS_MESSAGE;
                            }
                             listener.onFailureError(errorResponse);
                        }else{
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                setError(jObjError,listener);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<RequestParentPojo> call, Throwable t) {
                    errorResponse.errorMessageString=t.getMessage();
                    listener.onFailureError(errorResponse);
                }
            });
        }else{
            errorResponse.errorMessageString=mContext.getResources().getString(R.string.network_error);
            listener.onFailureError(errorResponse);
        }
    }



    //For user login through OTP Verification
    public void otpVerification(Context context, String otp, final String password, final LoginCallback callback){
        final ErrorResponse errorResponse=new ErrorResponse();
        if(NetworkManager.getInstance().isConnectingToInternet(context)){
            RetrofitRequestBuilder.getInstance(context).verifyOtp(otp, new Callback<LoginParentPojo>() {
                @Override
                public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {
                    if(StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX)){
                        LoginParentPojo loginResponse = response.body();
                        if(loginResponse.responseData.info!=null){
                            callBackMethod(loginResponse.responseData,password,callback);
                        }
                    }else{
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            seterrorResponse(jObjError,callback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<LoginParentPojo> call, Throwable t) {
                    errorResponse.errorMessageString=t.getMessage();
                    callback.onError(errorResponse);
                }
            });
        }else {
            errorResponse.errorMessageString=mContext.getResources().getString(R.string.network_error);
            callback.onError(errorResponse);
        }

    }


    //THis method will once user is allowed for OTP Verification and once Token Is Expired.
    public void getCallBackUrlOTPEnabledState(final Context context,final LoginCallback callback){
        final ErrorResponse errorResponse=new ErrorResponse();
        if(NetworkManager.getInstance().isConnectingToInternet(context)){
            RetrofitRequestBuilder.getInstance(context).getCallBackUrlOnOtpEnabled(context, new Callback<LoginParentPojo>() {
                @Override
                public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {
                    if(StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX)){
                        if(response!=null){
                            LoginParentPojo data=response.body();
                            if(data.responseData.callbackUrl!=null){
                                callBackMethod(data.responseData,"",callback);
                            }
                        }
                    }else{
                        if(response.code()== Constant.SUBSCRIBE_ERROR){
                            setSessionKey(response.headers().get(Constant.Authorization));
                            callSubscribeUserApi(mContext,callback);
                        }else{
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                seterrorResponse(jObjError,callback);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<LoginParentPojo> call, Throwable t) {
                    errorResponse.errorMessageString=t.getMessage();
                    callback.onError(errorResponse);
                }
            });
        }else {
            errorResponse.errorMessageString=mContext.getResources().getString(R.string.network_error);
            callback.onError(errorResponse);
        }

    }



    //If user token expired then this particular method call  again login api and fetch api to get token.
    public void renewTokenApi(Context context, final LoginCallback callback) {

        if(getRenewTokenConfig()){
            if(SSOManager.getInstance(context).checkIsOtpEnabled()){
                getCallBackUrlOTPEnabledState(context,callback);
            }else{
                if (SSOManager.getInstance(context).getUserInfo() != null) {

                    final UserInfo userInfo = SSOManager.getInstance(context).getUserInfo();
                    if(!TextUtils.isEmpty(userInfo.email)){
                        String email = userInfo.email;
                        String password = userInfo.passWord;

                        SSOManager.getInstance(mContext).fetchTokenApi(password, new CommonListener() {
                            @Override
                            public void onSuccess() {
                                callback.onSuccess(userInfo);

                            }

                            @Override
                            public void onFailure(String error) {
                                ErrorResponse errorResponse=new ErrorResponse();
                                errorResponse.errorMessageString=error;
                                callback.onError(errorResponse);
                            }
                        });
                    }else{

                    }

                }
            }
        }else{
            ErrorResponse errorResponse=new ErrorResponse();
            errorResponse.errorMessageString="";
            callback.onError(errorResponse);
        }


    }


    //This method store userToken in local storage.
    private void setUserToken(AccessTokenParentPojo accessTokenModel) {
        if (accessTokenModel.accessToken != null && !TextUtils.isEmpty(accessTokenModel.accessToken)){
            StringBuilder builder=new StringBuilder();
            builder.append("Bearer").append(" ").append(accessTokenModel.accessToken);
            String token=builder.toString();
            SSOManager.getInstance(mContext).setTokenId(token);
        }


        if (accessTokenModel.refreshToken != null && !TextUtils.isEmpty(accessTokenModel.refreshToken)) {
            StringBuilder builder=new StringBuilder();
            builder.append("Bearer").append(" ").append(accessTokenModel.refreshToken);
            String refreshToken=builder.toString();
            SSOManager.getInstance(mContext).setRefreshToken(refreshToken);
        }

    }

    //Get  all  UserInfo from local storage.
    public UserInfo getUserInfo() {
        UserInfo info=null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String userString = sharedPreferences.getString(Constant.PreferenceString.UserDetails, Constant.PreferenceString.EmptyString);
            info = objectMapper.readValue(userString, UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    //Store User Info inn local storage
    public void setUserInfo(UserInfo userInfo) {
        try {
            ObjectMapper  objectMapper = new ObjectMapper();
            String userString = objectMapper.writeValueAsString(userInfo);
            editor.putString(Constant.PreferenceString.UserDetails, userString);
            doCommit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Get User Token from local storage.
    public String getTokenId() {
        return sharedPreferences.getString(Constant.PreferenceString.tokenString, Constant.PreferenceString.EmptyString);
    }

    //Store Token into local storage
    public void setTokenId(String tokenId) {
        editor.putString(Constant.PreferenceString.tokenString, tokenId);
        doCommit();
    }

    //Get Refreshed token from local storage.
    public String getRefreshToken() {
        return sharedPreferences.getString(Constant.PreferenceString.refreshTokenString, Constant.PreferenceString.EmptyString);
    }

    //Set Refreshed token into local storage.
    public void setRefreshToken(String tokenId) {
        editor.putString(Constant.PreferenceString.refreshTokenString, tokenId);
        doCommit();
    }

    //Get CallBack url from local storage.
    public String getRedirectCode() {
        return sharedPreferences.getString(Constant.PreferenceString.code, Constant.PreferenceString.EmptyString);
    }


    //Get CallBack url from local storage.
    public void setRedirectCode(String code) {
        editor.putString(Constant.PreferenceString.code, code);
        doCommit();
    }


    //Check User status.
    public boolean checkUserStatus() {
        return sharedPreferences.getBoolean(Constant.PreferenceString.UserStatus, false);
    }

    //Store  user Status
    public void setUserStatus(boolean isUserLoggedIn) {
        editor.putBoolean(Constant.PreferenceString.UserStatus, isUserLoggedIn);
        doCommit();
    }

    //Check whether user loggedIn or not.
    public boolean isUserLoggedIn() {
        boolean isLoggedIn = false;

        if (checkUserStatus()) {
            isLoggedIn = true;
        }

        return isLoggedIn;
    }


    //Logout user from app.
    public void logout() {

        setUserInfo(new UserInfo());
        setRedirectCode(Constant.PreferenceString.EmptyString);
        setUserStatus(false);
        setTokenId(Constant.PreferenceString.EmptyString);
        setRefreshToken(Constant.PreferenceString.EmptyString);

    }

    //Set Current Config  url into local storage.
    public void setCurrentSSOServerUrl(String tokenId) {
        editor.putString(Constant.PreferenceString.apiUrl, tokenId);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getCurrentSSOServerUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.apiUrl, Constant.PreferenceString.EmptyString);
    }


    //Set Current Config  url into local storage.
    public void setCurrentSSOLoginUrl(String tokenId) {
        editor.putString(Constant.PreferenceString.loginUrl, tokenId);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getCurrentSSOLoginUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.loginUrl, Constant.PreferenceString.EmptyString);
    }



    //Set Current Config  url into local storage.
    public void setAuthUrl(String url) {
        editor.putString(Constant.PreferenceString.Auth, url);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getAuthUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.Auth, Constant.PreferenceString.EmptyString);
    }


    //Get Current  config  url from  local storage.
    public String getClientID() {
        return sharedPreferences.getString(Constant.PreferenceString.clientID, Constant.PreferenceString.EmptyString);
    }


    //Set Current Config  url into local storage.
    public void setClientId(String clientId) {
        editor.putString(Constant.PreferenceString.clientID, clientId);
        doCommit();
    }




    //Get Scope  config  values from  local storage.
    public Set<String> getScope() {
        Set<String> blankScope=new HashSet<>();
        return sharedPreferences.getStringSet(Constant.PreferenceString.scope,blankScope);
    }


    //Set Scope Config  values into local storage.
    public void setScope(Set<String> clientId) {
        editor.putStringSet(Constant.PreferenceString.scope, clientId);
        doCommit();
    }


    //Get Scope  config  values from  local storage.
    public String getResponseType() {
        return sharedPreferences.getString(Constant.PreferenceString.responseType,Constant.PreferenceString.EmptyString);
    }


    //Set Scope Config  values into local storage.
    public void setResponseType(String responseType) {
        editor.putString(Constant.PreferenceString.responseType, responseType);
        doCommit();
    }




    //Get isOTPEnabled Method .
    public boolean checkIsOtpEnabled() {
        return sharedPreferences.getBoolean(Constant.PreferenceString.OTP,false);
    }


    //Set OTP Status.
    public void setOtpCheck(boolean isOtpEnabled) {
        editor.putBoolean(Constant.PreferenceString.OTP, isOtpEnabled);
        doCommit();
    }



    //Get Session Key Method .
    public String getSessionKey() {
        return sharedPreferences.getString(Constant.PreferenceString.sessionKey,Constant.PreferenceString.EmptyString);
    }


    //Set Session Key.
    public void setSessionKey(String sessionKey) {
        editor.putString(Constant.PreferenceString.sessionKey, sessionKey);
        doCommit();
    }


    //Set ServerRedirection Url.
    public void setServerRedirectionUrl(String url) {
        editor.putString(Constant.PreferenceString.RedirectionUrl, url);
        doCommit();
    }

    //Get Server Redirection url.
    public String getServerRedirectionUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.RedirectionUrl, Constant.PreferenceString.EmptyString);
    }


    //Set Current Config  url into local storage.
    public void setAppBaseUrl(String url) {
        editor.putString(Constant.PreferenceString.appUrl, url);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getAppBaseUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.appUrl, Constant.PreferenceString.EmptyString);
    }



    public void setAppName(String url) {
        editor.putString(Constant.PreferenceString.app, url);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getAppName() {
        return sharedPreferences.getString(Constant.PreferenceString.app, Constant.PreferenceString.EmptyString);
    }


    public void doCommit() {
        if (editor != null) {
            editor.apply();

        }
    }


    public void seterrorResponse(JSONObject json,LoginCallback callBack) {
        try{
            ErrorResponse errorResponse=new ErrorResponse();
            String errorCode=json.getString(Constant.errorMessage);
            errorResponse.errorCode=errorCode;
            errorResponse.errorMessageString=json.getString(Constant.errorMessage);
            callBack.onError(errorResponse);
        }catch (Exception e){
            Log.e(TAG, "seterrorResponse: Error", e);
        }
    }

    public void setError(JSONObject json,AuthResponseInterface callBack) {
        try{
            ErrorResponse errorResponse=new ErrorResponse();
            if(json.has(Constant.errorMessage)){
                String errorCode=json.getString(Constant.errorMessage);
                errorResponse.errorCode=errorCode;
                errorResponse.errorMessageString=json.getString(Constant.errorMessage);
            }

            callBack.onFailureError(errorResponse);
        }catch (Exception e){
            Log.e(TAG, "seterrorResponse: Error", e);
        }
    }



    public void callFBLogin(Context context, String fbId, LoginCallback loginCallback){
        FacebookManager.getInstance(context).initializeRegisterCallback(context,loginCallback);
    }


   /* public void callLinkedInLogin(Context context,LoginCallback loginCallback){
        LinkedInManager.getInstance(context).initiateLinkedInSDK(context,loginCallback);
    }*/

    public void callGoogleSignIn(Context context,String serverClientId,LoginCallback loginCallback){
        //GoogleManager.getInstance(context).callGoogleSign(context,serverClientId,loginCallback);
    }


    public void callSubscribeUserApi(final Context context,final LoginCallback loginCallback){
        ErrorResponse errorResponse=new ErrorResponse();
        if(NetworkManager.getInstance().isConnectingToInternet(context)){
            RetrofitRequestBuilder.getInstance(context).subscribeUserApi(context, new Callback<LoginParentPojo>() {
                @Override
                public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {
                    if(StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX)){
                        onLoginOrSubscribeSuccess(response,context,loginCallback);

                    }else{
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            seterrorResponse(jObjError,loginCallback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginParentPojo> call, Throwable t) {
                    try {
                        ErrorResponse errorResponse=new ErrorResponse();
                        errorResponse.errorMessageString=t.getMessage();
                        loginCallback.onError(errorResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }else{
            errorResponse.errorMessageString=mContext.getResources().getString(R.string.network_error);
            loginCallback.onError(errorResponse);
        }
    }





    //On Subcribe and Login Success
    public void onLoginOrSubscribeSuccess(Response<LoginParentPojo> response,Context mContext,LoginCallback loginCallback){
        ErrorResponse errorResponse=new ErrorResponse();
        if (StatusCodeHandler.isResponseCodeValidated(response.code(),Constant.STATUS_OK,Constant.STATUS_OK_MAX)) {
            //do your stuffs here
            LoginParentPojo loginResponse = response.body();
            if (loginResponse != null) {
                ResponseData data = loginResponse.responseData;
                if(data.otpEnabled){
                    setOtpCheck(true);
                    Log.i(TAG, "onResponse: Session="+response.headers().get(Constant.Authorization));
                    setSessionKey(response.headers().get(Constant.Authorization));
                    loginCallback.onSuccess(null);

                }else{
                    if(data.callbackUrl!=null){
                        callBackMethod(data,"",loginCallback);

                    }else{
                        setSessionKey(response.headers().get(Constant.Authorization));
                        getCallBackUrlOTPEnabledState(mContext,loginCallback);
                    }
                }


            } else {
                errorResponse.errorMessageString=response.message();
                loginCallback.onError(errorResponse);
            }


        }else{
            if(response.code()==Constant.SUBSCRIBE_ERROR){
                callSubscribeUserApi(mContext,loginCallback);
            }else{
                try {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    seterrorResponse(jObjError,loginCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }


    //Get  all  UserInfo from local storage.
    public AccessTokenParentPojo getFilterInfo() {
        AccessTokenParentPojo info=null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String userString = sharedPreferences.getString(Constant.PreferenceString.Filter, Constant.PreferenceString.EmptyString);
            info = objectMapper.readValue(userString, AccessTokenParentPojo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    //Store User Info inn local storage
    public void setFilterInfo(AccessTokenParentPojo userInfo) {
        try {
            ObjectMapper  objectMapper = new ObjectMapper();
            String userString = objectMapper.writeValueAsString(userInfo);
            editor.putString(Constant.PreferenceString.Filter, userString);
            doCommit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<HashMap<String,Object>> getFilterObject(Context context){
         ArrayList<HashMap<String,Object>> mFilterList=new ArrayList<>();
        AccessTokenParentPojo accessTokenParentPojo=SSOManager.getInstance(context).getFilterInfo();
        if(accessTokenParentPojo.groupsArrayList!=null&&accessTokenParentPojo.groupsArrayList.size()>0){
            ArrayList<Groups> list=accessTokenParentPojo.groupsArrayList;

            for(Groups group:list){
                if(group.filtersArrayList!=null&&group.filtersArrayList.size()>0){
                    ArrayList<Filters> filterList=group.filtersArrayList;
                    for(Filters filterObject:filterList){
                        HashMap<String,Object> map=new HashMap<>();
                        map.put(filterObject.name,filterObject.valuesList);
                        mFilterList.add(map);
                    }
                }

            }
        }

        Log.i(TAG, "getFilterObject: Filter List="+mFilterList);
        return mFilterList;

    }



    public ArrayList<String> getFilter(Context context,String filterName){

        ArrayList<String> filters=null;
            try{
                ArrayList<HashMap<String,Object>> list=getFilterObject(context);
                if(list!=null&&list.size()>0){
                    for(int i=0;i<list.size();i++){
                        HashMap<String,Object> map=list.get(i);
                        String key=getKey(map);
                        if (key.equalsIgnoreCase(filterName)){
                            filters= (ArrayList<String>) map.get(filterName);
                            break;
                        }

                    }
                }

            }catch (Exception e){
                Log.i(TAG, "getFilter: Error="+e.getMessage());
            }

        return filters;
    }



    public void setRenewTokenConfig(boolean isRenewToken){
        this.isRenewTokenEnabled=isRenewToken;
    }

    public boolean getRenewTokenConfig(){
        return isRenewTokenEnabled;
    }


    public String getKey(HashMap<String,Object> map){
        Map.Entry<String,Object> entry=map.entrySet().iterator().next();
        String key= entry.getKey();
        return key;
    }


    public Integer getValue(HashMap<String,Integer> map){
        Map.Entry<String,Integer> entry=map.entrySet().iterator().next();
        Integer value= entry.getValue();
        return value;
    }





}
