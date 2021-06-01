package com.example.surajbokankar.ssomanager.network;


import com.example.surajbokankar.ssomanager.model.LoginParentPojo;
import com.example.surajbokankar.ssomanager.model.LoginSuccessResponse;
import com.example.surajbokankar.ssomanager.model.signup.RequestParentPojo;
import com.example.surajbokankar.ssomanager.model.userauth.AccessTokenParentPojo;

import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by suraj.bokankar on 9/3/17.
 */

public interface RestApiService {

    @POST
    public Call<LoginParentPojo> callLoginApi(@Url String url, @Body RequestBody requestBody);

    @GET("callback")
    public Call<AccessTokenParentPojo> callAuthorizationApi(@QueryMap HashMap<String, Object> userRequest);

    @POST("forgot_password/")
    public Call<RequestParentPojo> forgotPassword(
            @Header("language") String language,
            @Body RequestBody requestBody);

    @PUT("change_password/")
    public Call<RequestParentPojo> changePassword(@Header("Authorization") String token, @Body RequestBody requestBody);

    @DELETE("logout/")
    public Call<RequestParentPojo> logoutUser(@Header("Authorization") String token);

    @POST
    public Call<RequestParentPojo> signUpUser(@Url String url, @Body RequestBody requestBody);

    @POST("otp_verification/")
    public Call<LoginParentPojo> otpVerification(@Header("Authorization") String token, @Body RequestBody requestBody);

    @POST
    public Call<LoginParentPojo> authorizeUser(@Header("Authorization") String sessionKey, @Url String url);

    @POST
    public Call<LoginParentPojo> socialLogin(@Url String url, @Body RequestBody requestBody);


    @GET
    public Call<LoginParentPojo> linkedInProfile(@Header("Authorization") Object token, @Url String url);

    @POST
    public Call<LoginParentPojo> subscribeUser(@Url String url, @Header("Authorization") String sessionKey);

    @PUT
    public Call<ResponseBody> setUserConsent(@Url String url, @Header("Authorization") String sessionKey, @Body RequestBody requestBody);

    @POST
    Call<LoginSuccessResponse> loginUser(@Url String url,
                                         @Body RequestBody body);

    @DELETE
    Call<ResponseBody> logoutUser(@Url String url,@Header("apienv") String env,
                                  @Header("Authorization") String token);

    @POST
    Call<ResponseBody> singUpForgot(@Url String url,
                                    @Body RequestBody body);

}
