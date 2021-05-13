package com.example.surajbokankar.ssomanager.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.example.surajbokankar.ssomanager.common.Constant;
import com.example.surajbokankar.ssomanager.common.ConfigConstant;
import com.example.surajbokankar.ssomanager.common.UrlConstant;
import com.example.surajbokankar.ssomanager.model.LoginParentPojo;
import com.example.surajbokankar.ssomanager.model.signup.RequestParentPojo;
import com.example.surajbokankar.ssomanager.model.userauth.AccessTokenParentPojo;
import com.example.surajbokankar.ssomanager.social.model.SocialLoginSuccess;
import com.example.surajbokankar.ssomanager.ssomanager.SSOManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by suraj.bokankar on 10/3/17.
 */

public class RetrofitRequestBuilder {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String TAG = "RetrofitRequestBuilder";
    static RetrofitRequestBuilder retrofitRequestBuilder=null;
    static  Context mContext=null;


    public static RetrofitRequestBuilder getInstance(Context context){
        if(retrofitRequestBuilder==null){
            retrofitRequestBuilder=new RetrofitRequestBuilder();
            mContext=context;
        }

        return retrofitRequestBuilder;
    }




    public  Call<LoginParentPojo> callUserAuthenticationApi(Context context, String email, String password, Callback<LoginParentPojo> listener) {
        Call<LoginParentPojo> call = null;

        JSONObject requestJson = new JSONObject();

        try {
            requestJson.put(Constant.LoginRequest.emailId, email);
            requestJson.put(Constant.LoginRequest.password, password);

            RequestBody requestBody = RequestBody.create(JSON, requestJson.toString());


            String endPoint="login/";
            String urlMap=getLoginUrl(mContext,endPoint,false);

            Retrofit retrofit = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(urlMap).builder();
            RestApiService apiService = retrofit.create(RestApiService.class);

            call = apiService.callLoginApi(urlMap,requestBody);

            call.enqueue(listener);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "callUserAuthenticationApi: Request Url=" + call.request().headers() + "\n" + call.request().url() + "\n" + requestJson);

        return call;
    }


    public  Call<AccessTokenParentPojo> getAccessTokenApi(Context context, Callback<AccessTokenParentPojo> listener) {
        Call<AccessTokenParentPojo> call = null;

        try {
            String authUrl=SSOManager.getInstance(context).getAuthUrl();
            Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(authUrl).builder();
            RestApiService apiService = retrofitBuilder.create(RestApiService.class);
            String codeString = SSOManager.getInstance(context).getRedirectCode();
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put(Constant.PreferenceString.code, codeString);
            String appName=SSOManager.getInstance(context).getAppName();
            if(appName!=null&&!TextUtils.isEmpty(appName)){
                requestMap.put(Constant.PreferenceString.app, appName);
            }


            call = apiService.callAuthorizationApi(requestMap);

            call.enqueue(listener);
            Log.i(TAG, "getAccessTokenApi: Request  Url=" + call.isExecuted() + "\n" + call.request().body() + "\n" + call.request().url());
        } catch (Exception e) {
            Log.i(TAG, "getAccessTokenApi: Error=" + e.getMessage());
        }

        return call;
    }



    public  Call<RequestParentPojo> changeUserPassword(String oldPassword,String newPassword,String confirmPassword,Callback<RequestParentPojo> listener) {
        Call<RequestParentPojo> call = null;

        try {
            String tokenId=SSOManager.getInstance(mContext).getTokenId();
            String url=SSOManager.getInstance(mContext).getCurrentSSOLoginUrl();

            Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(url+UrlConstant.OAUTH_URL).builder();
            RestApiService apiService = retrofitBuilder.create(RestApiService.class);

            JSONObject requestMap=new JSONObject();
            requestMap.put(Constant.SSORequest.oldPassword, oldPassword);
            requestMap.put(Constant.SSORequest.newPassword, newPassword);
            requestMap.put(Constant.SSORequest.confirmPassword, confirmPassword);
            RequestBody requestBody=RequestBody.create(JSON,requestMap.toString());
            call = apiService.changePassword(tokenId, requestBody);
            call.enqueue(listener);
            Log.i(TAG, "changeUserPassword: Token="+tokenId);

                Log.i(TAG, "changeUserPassword: Request  Url=" + tokenId + "\n" + requestMap + "\n" + call.request().url());
        } catch (Exception e) {
            Log.i(TAG, "changeUserPassword: Error=" + e.getMessage());
        }

        return call;
    }



    public  Call<RequestParentPojo> forgotPassword(String emailId,Callback<RequestParentPojo> listener) {
        Call<RequestParentPojo> call = null;

        try {
            String url=SSOManager.getInstance(mContext).getCurrentSSOLoginUrl();

            Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(url+UrlConstant.OAUTH_URL).builder();
            RestApiService apiService = retrofitBuilder.create(RestApiService.class);
            String clientId=SSOManager.getInstance(mContext).getClientID();
            JSONObject jsonObject=null;

            jsonObject=new JSONObject();
            jsonObject.put(Constant.SSORequest.emailId, emailId);
            jsonObject.put(Constant.SSORequest.clientId,clientId);

            RequestBody requestBody=RequestBody.create(JSON,jsonObject.toString());
            call = apiService.forgotPassword(requestBody);
            call.enqueue(listener);
            Log.i(TAG, "forgotPassword: Request  Url=" + call.request().headers() + "\n" + jsonObject + "\n" + call.request().url());
        } catch (Exception e) {
            Log.i(TAG, "forgotPassword: Error=" + e.getMessage());
        }

        return call;
    }



    public  Call<RequestParentPojo> logoutUser(Context context,String emailId,Callback<RequestParentPojo> listener) {
        Call<RequestParentPojo> call = null;

        try {
            String url=SSOManager.getInstance(mContext).getCurrentSSOLoginUrl();

            Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(url+UrlConstant.OAUTH_URL).builder();
            RestApiService apiService = retrofitBuilder.create(RestApiService.class);
            String tokenId=SSOManager.getInstance(context).getTokenId();
            call = apiService.logoutUser(tokenId);
            call.enqueue(listener);
            Log.i(TAG, "logoutUser: Request  Url=" + tokenId+ "\n" + tokenId + "\n" + call.request().url());
        } catch (Exception e) {
            Log.i(TAG, "logoutUser: Error=" + e.getMessage());
        }

        return call;
    }



    public  Call<RequestParentPojo> signUpUser(boolean isActive,String emailId,Callback<RequestParentPojo> listener) {
        Call<RequestParentPojo> call = null;

        try {



            JSONObject jsonObject=null;
            String url= getSignUpInvite(mContext);
            jsonObject=new JSONObject();
            jsonObject.put(Constant.SSORequest.emailId, emailId);
            Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(url).builder();
            RestApiService apiService = retrofitBuilder.create(RestApiService.class);
            RequestBody requestBody=RequestBody.create(JSON,jsonObject.toString());
            call = apiService.signUpUser(url,requestBody);
            call.enqueue(listener);
            Log.i(TAG, "signUpUser: Request  Url=" + call.isExecuted() + "\n" + jsonObject + "\n" + call.request().url());
        } catch (Exception e) {
            Log.i(TAG, "signUpUser: Error=" + e.getMessage());
        }

        return call;
    }


    public  Call<LoginParentPojo> verifyOtp(String otp,Callback<LoginParentPojo> listener) {
        Call<LoginParentPojo> call = null;

        try {
            String url=SSOManager.getInstance(mContext).getCurrentSSOLoginUrl();

            Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(url+UrlConstant.OAUTH_URL).builder();
            RestApiService apiService = retrofitBuilder.create(RestApiService.class);
            JSONObject jsonObject=null;
            jsonObject=new JSONObject();
            jsonObject.put(Constant.SSORequest.otp, otp);
            RequestBody requestBody=RequestBody.create(JSON,jsonObject.toString());
            String sessionKey=SSOManager.getInstance(mContext).getSessionKey();
            call = apiService.otpVerification(sessionKey,requestBody);
            call.enqueue(listener);
            Log.i(TAG, "verifyOtp: Request  Url=" + jsonObject + "\n" + call.request().headers() + "\n" + call.request().url());
        } catch (Exception e) {
            Log.i(TAG, "verifyOtp: Error=" + e.getMessage());
        }

        return call;
    }



    public  Call<LoginParentPojo> getCallBackUrlOnOtpEnabled(Context context,Callback<LoginParentPojo> listener) {
        Call<LoginParentPojo> call = null;

        try {

            String endPoint="authorize/";
            String url= getLoginUrl(mContext,endPoint,true);
            String sessionId=SSOManager.getInstance(context).getSessionKey();
            Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(url).builder();
            RestApiService apiService = retrofitBuilder.create(RestApiService.class);
            call = apiService.authorizeUser(sessionId,url);
            call.enqueue(listener);
            Log.i(TAG, "getCallBackUrlOnOtpEnabled: Request  Url=" + call.isExecuted() + "\n" + sessionId + "\n" + call.request().url());
        } catch (Exception e) {
            Log.i(TAG, "getCallBackUrlOnOtpEnabled: Error=" + e.getMessage());
        }
        return call;
    }



    public  String  getLoginUrl(Context context,String endPoint,boolean isClientID) {

        StringBuilder builder=new StringBuilder();
        StringBuilder loginUrl=new StringBuilder();
        String url=SSOManager.getInstance(mContext).getCurrentSSOLoginUrl();
       String ResponseType=SSOManager.getInstance(context).getResponseType();
        Set<String> Scope=SSOManager.getInstance(context).getScope();

        if(Scope!=null&& ResponseType!=null){
            for(String s: Scope){
                builder.append(s).append(" ");
            }
            String scope=builder.toString();
            try {
                String clientId=SSOManager.getInstance(mContext).getClientID();
                if(isClientID){
                    loginUrl.append( url+UrlConstant.OAUTH_URL).append(endPoint).append(Constant.LoginRequest.client_id).append(clientId).
                            append("&").append(Constant.LoginRequest.response_type).append(ResponseType).append("&").append(Constant.LoginRequest.scope).
                            append(URLEncoder.encode(scope,"UTF-8").replace("+", "%20"));
                }else{
                    loginUrl.append(url+UrlConstant.OAUTH_URL).append(endPoint);
                }


            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        Log.i(TAG, "getLoginUrl: LOgin="+loginUrl.toString());
        return loginUrl.toString();
    }



    public  String  getSignUpInvite(Context context) {

        StringBuilder builder=new StringBuilder();
        StringBuilder loginUrl=new StringBuilder();
        String ResponseType=SSOManager.getInstance(context).getResponseType();
        String url=SSOManager.getInstance(mContext).getCurrentSSOLoginUrl();
        Set<String> Scope=SSOManager.getInstance(context).getScope();

        if(Scope!=null&& ResponseType!=null) {
            for (String s : Scope) {
                builder.append(s).append(" ");
            }
            String scope = builder.toString();
            if (ResponseType != null) {
                try {
                    String clientId = SSOManager.getInstance(context).getClientID();
                    loginUrl.append(url + UrlConstant.SIGN_UP).append(Constant.LoginRequest.client_id).append(clientId).
                            append("&").append(Constant.LoginRequest.response_type).append(ResponseType).append("&").append(Constant.LoginRequest.scope).
                            append(URLEncoder.encode(scope, "UTF-8").replace("+", "%20"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return loginUrl.toString();
    }



public Call<LoginParentPojo> socialLogin(Context context, String  accessToken, JSONObject socialProfileJson,String type,Callback<LoginParentPojo> listener){

    Call<LoginParentPojo> call = null;

    try {

        String endPoint="social_auths/authorize/";
        String url= getSocialLoginUrl(mContext,endPoint,type);
        Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(url).builder();
        RestApiService apiService = retrofitBuilder.create(RestApiService.class);


        socialProfileJson.put(Constant.LoginRequest.accessToken,accessToken);

        RequestBody requestBody=RequestBody.create(JSON,socialProfileJson.toString());
        call = apiService.socialLogin(url,requestBody);
        call.enqueue(listener);
        Log.i(TAG, "socialLogin: Request  Url=" + call.isExecuted()  + "\n" + call.request().url());
    } catch (Exception e) {
        Log.i(TAG, "socialLogin: Error=" + e.getMessage());
    }
    return call;

}

    private String getSocialLoginUrl(Context mContext, String endPoint,String connectionType) {
        String url=null;
        ///oauth/social_auths/authorize/?client_id=\(clientID)&response_type=\(responseType.rawValue)&connection=\(facebook or linkedin or google)&redirect_uri=\(redirectURL)
        StringBuilder stringBuilder=new StringBuilder();
        url=SSOManager.getInstance(mContext).getCurrentSSOLoginUrl();
        String ResponseType=SSOManager.getInstance(mContext).getResponseType();
        if (ResponseType != null) {
                try {
                    String clientId = SSOManager.getInstance(mContext).getClientID();
                    String redirectionUri=SSOManager.getInstance(mContext).getServerRedirectionUrl();
                    stringBuilder.append(url+UrlConstant.OAUTH_URL).append(endPoint).append(Constant.LoginRequest.client_id).append(clientId).
                            append("&").append(Constant.LoginRequest.response_type).append(ResponseType).append("&").append(Constant.LoginRequest.connection).
                            append(connectionType).append("&").append(Constant.LoginRequest.redirectUri).append(redirectionUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


         url=stringBuilder.toString();
        return  url;
    }


 /*   public Call<LoginParentPojo> linkedInProfileApi(Context context, AccessToken accessToken, Callback<LoginParentPojo> listener){

        Call<LoginParentPojo> call = null;

        try {
            String url= "https://developer.linkedin.com/docs/android-sdk-auth";
            Retrofit retrofitBuilder = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).builder();
            RestApiService apiService = retrofitBuilder.create(RestApiService.class);
            call = apiService.linkedInProfile(accessToken,url);
            call.enqueue(listener);
            Log.i(TAG, "socialLogin: Request  Url=" + call.isExecuted()  + "\n" + call.request().url());
        } catch (Exception e) {
            Log.i(TAG, "socialLogin: Error=" + e.getMessage());
        }
        return call;
    }

*/


    public Call<LoginParentPojo> subscribeUserApi(Context context, Callback<LoginParentPojo> listener) {
        Call<LoginParentPojo> call = null;

        JSONObject requestJson = new JSONObject();

        try {
            /*requestJson.put(Constant.LoginRequest.emailId, email);
            requestJson.put(Constant.LoginRequest.password, password);*/
            RequestBody requestBody = RequestBody.create(JSON, requestJson.toString());

            String endPoint="subscribe/";

            String urlMap =getLoginUrl(context,endPoint,true);

            Retrofit retrofit = RetrofitClientBuilder.getInstance().setHTTPClient(mContext).setBaseUrl(urlMap).builder();
            RestApiService apiService = retrofit.create(RestApiService.class);
            String sessionId=SSOManager.getInstance(context).getSessionKey();
            call = apiService.subscribeUser(urlMap, sessionId);

            call.enqueue(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG, "subscribeUserApi: Request Url=" + call.request().headers() + "\n" + call.request().url() + "\n" + requestJson);

        return call;
    }


}
