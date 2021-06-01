package com.example.surajbokankar.ssomanager.ssomanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.surajbokankar.ssomanager.BuildConfig;
import com.example.surajbokankar.ssomanager.LoginCallback;
import com.example.surajbokankar.ssomanager.LoginListener;
import com.example.surajbokankar.ssomanager.R;
import com.example.surajbokankar.ssomanager.common.AuthResponseInterface;
import com.example.surajbokankar.ssomanager.common.CommonListener;
import com.example.surajbokankar.ssomanager.common.Constant;
import com.example.surajbokankar.ssomanager.common.ErrorHandler;
import com.example.surajbokankar.ssomanager.common.StatusCodeHandler;
import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.model.LoginParentPojo;
import com.example.surajbokankar.ssomanager.model.LoginSuccessResponse;
import com.example.surajbokankar.ssomanager.model.ResponseData;
import com.example.surajbokankar.ssomanager.model.SuccessModel;
import com.example.surajbokankar.ssomanager.model.UserInfo;
import com.example.surajbokankar.ssomanager.model.signup.AuthResponseData;
import com.example.surajbokankar.ssomanager.model.signup.RequestParentPojo;
import com.example.surajbokankar.ssomanager.model.userauth.AccessTokenParentPojo;
import com.example.surajbokankar.ssomanager.model.userauth.Filters;
import com.example.surajbokankar.ssomanager.model.userauth.Groups;
import com.example.surajbokankar.ssomanager.network.NetworkManager;
import com.example.surajbokankar.ssomanager.network.RetrofitRequestBuilder;
import com.example.surajbokankar.ssomanager.prefrence.PreferenceManager;
import com.example.surajbokankar.ssomanager.sharesession.SessionManager;
import com.example.surajbokankar.ssomanager.sharesession.SessionModel;
import com.example.surajbokankar.ssomanager.webview.TermsConditionWebview;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    public String userName, passWord;
    String tokenId;
    boolean isRenewTokenEnabled = false;
    boolean isAllowed = false;
    private ObjectMapper objectMapper;

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
        if(BuildConfig.DEBUG) Log.i(TAG, "getCallBackUrl: Code=" + code);
        SSOManager.getInstance(mContext).setRedirectCode(code);
        return isCallBackUrl;
    }

    //User returns user Token and triggers back with response that user is login Success.

    //Login user using Email and Password which return UserInfo  and gives a call to get AccessToken
    public void login(final String email, final String password, final LoginCallback loginCallback) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse();
            if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {
                RetrofitRequestBuilder.getInstance(mContext).callUserAuthenticationApi(mContext, email, password, new Callback<LoginParentPojo>() {
                    @Override
                    public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {

                        userName = email;
                        SSOManager.getInstance(mContext).setUserEmail(email);
                        passWord = password;
                        onLoginOrSubscribeSuccess(response, mContext, loginCallback);


                    }

                    @Override
                    public void onFailure(Call<LoginParentPojo> call, Throwable t) {
                        errorResponse.errorMessageString = t.getMessage();
                        loginCallback.onError(errorResponse);
                    }
                });
            } else {
                errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
                loginCallback.onError(errorResponse);
            }

        } catch (Exception e) {
            if(BuildConfig.DEBUG)  Log.e(TAG, "login: Error=", e);
        }


    }

    public void loginDirect(String email, String password, String deviceID, Boolean isSocialLogin, String socialPlatform, String socialAccessToken, final LoginListener loginCallback) {

        final ErrorResponse errorResponse = new ErrorResponse();

        SSOManager.getInstance(mContext).setUserEmail(email);

        if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {

            RetrofitRequestBuilder.getInstance(mContext).loginDirect(mContext, email, password, deviceID, isSocialLogin, socialPlatform, socialAccessToken, new Callback<LoginSuccessResponse>() {
                @Override
                public void onResponse(Call<LoginSuccessResponse> call, Response<LoginSuccessResponse> response) {

                    if (response.code() == 200) {
                        LoginSuccessResponse userInfo = response.body();
                        String token = userInfo.getData().getAccessTokenType() + " " + userInfo.getData().getAccessToken();
                        SSOManager.getInstance(mContext).setTokenId(token);
                        SSOManager.getInstance(mContext).setUserStatus(true);
                        loginCallback.onLoginSuccess(userInfo);
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if(BuildConfig.DEBUG)  Log.i(TAG, "onResponseFailure: " + jObjError.getString("message"));
                            if (loginCallback != null) {
                                loginCallback.onError(jObjError.getString("message"));
                            }
                            Toast.makeText(mContext, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<LoginSuccessResponse> call, Throwable t) {
                    errorResponse.errorMessageString = t.getMessage();
                    loginCallback.onError(errorResponse.errorMessageString);
                }
            });


        } else {
            errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
            loginCallback.onError(errorResponse.errorMessageString);
        }
    }

    public void signupDirect(String email, String password, String name, String env, final LoginListener callBack) {
        final ErrorResponse errorResponse = new ErrorResponse();

        if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {

            RetrofitRequestBuilder.getInstance(mContext).signupDirect(mContext, email, password, name, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.code() == 200) {
                        SuccessModel error = ErrorHandler.getInstance().getErrorMessage(response.body());
                        ResponseBody userInfo = response.body();
                        callBack.onSignUpForgotPasswordSuccess(error.message);
                    } else {
                        SuccessModel error = ErrorHandler.getInstance().getErrorMessage(response.errorBody());
                        callBack.onError(error.message);
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    errorResponse.errorMessageString = t.getMessage();
                    callBack.onError(errorResponse.errorMessageString);
                }
            });

        } else {
            errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
            callBack.onError(errorResponse.errorMessageString);
        }
    }

    public void forgotPassword(String email, String env, final LoginListener callBack) {

        final ErrorResponse errorResponse = new ErrorResponse();

        if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {

            RetrofitRequestBuilder.getInstance(mContext).forgotPasswordDirect(mContext, email, new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        SuccessModel error = ErrorHandler.getInstance().getErrorMessage(response.body());
                        ResponseBody userInfo = response.body();
                        callBack.onSignUpForgotPasswordSuccess(error.message);
                    } else {
                        SuccessModel error = ErrorHandler.getInstance().getErrorMessage(response.errorBody());
                        callBack.onError(error.message);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    errorResponse.errorMessageString = t.getMessage();
                    callBack.onError(errorResponse.errorMessageString);
                }
            });

        } else {
            errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
            callBack.onError(errorResponse.errorMessageString);
        }

    }

    public void callBackMethod(ResponseData data, String password, final LoginCallback loginCallback) {
        final ErrorResponse errorResponse = new ErrorResponse();
        final UserInfo userInfo = data.info;
        boolean isCallBackPresent = getCallBackUrl(data.callbackUrl);
        //From Here we will fetch token for further usage with callback url from login api.
        if (isCallBackPresent) {
            SSOManager.getInstance(mContext).fetchTokenApi(password, new CommonListener() {
                @Override
                public void onSuccess() {
                    SSOManager.getInstance(mContext).setSessionID(SSOManager.getInstance(mContext).getSessionKey());
                    if (SSOManager.getInstance(mContext).getUserConsent()) {
                        loginCallback.onSuccess(userInfo);
                        setUserStatus(true);
                    } else {
                        callUserConsent(mContext, userInfo, loginCallback);
                    }

                }

                @Override
                public void onFailure(String error) {
                    errorResponse.errorMessageString = error;
                    loginCallback.onError(errorResponse);
                }
            });
        }
    }

    public void callUserConsent(final Context context, final UserInfo userInfo, final LoginCallback callback) {
        final Dialog dialog = new Dialog(context);
        try {
            // Include dialog.xml file

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.layout_user_consent_dialog);
            final AppCompatButton continueButton = (AppCompatButton) dialog.findViewById(R.id.user_continue_button);
            AppCompatButton cancelButton = (AppCompatButton) dialog.findViewById(R.id.user_cancel_button);
            final ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);

            Window window = dialog.getWindow();
            window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            AppCompatCheckBox userCheckBox = (AppCompatCheckBox) dialog.findViewById(R.id.user_agree_check_box);
            AppCompatTextView termCondition = (AppCompatTextView) dialog.findViewById(R.id.agree_text_view);


            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isAllowed) {
                        progressBar.setVisibility(View.VISIBLE);
                        RetrofitRequestBuilder.getInstance(context).steUserConsent(context, new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                progressBar.setVisibility(View.GONE);
                                if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                                    //Toast.makeText(context,"User Allowed",Toast.LENGTH_SHORT).show();
                                    callback.onSuccess(userInfo);
                                    setUserStatus(true);
                                } else {
                                    setUserStatus(false);
                                    dialog.dismiss();
                                    final ErrorResponse errorResponse = new ErrorResponse();
                                    errorResponse.errorMessageString = "Failure" + response.code();
                                    callback.onError(errorResponse);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                setUserStatus(false);
                                progressBar.setVisibility(View.GONE);
                                final ErrorResponse errorResponse = new ErrorResponse();
                                errorResponse.errorMessageString = "No User Consent";
                                callback.onError(errorResponse);

                            }
                        });
                    } else {
                        Toast.makeText(context, "Please agree to Terms and Conditions", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = SSOManager.getInstance(context).getFilePath();
                    String name = SSOManager.getInstance(context).getFileName();
                    onLoggedOut(context, path, name);
                    dialog.dismiss();
                    final ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.errorMessageString = "Data Usage Policy Rejected";
                    callback.onError(errorResponse);
                }
            });

            userCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isSelected) {
                    if (isSelected) {
                        continueButton.setAlpha(1.0f);
                        isAllowed = true;
                    } else {
                        continueButton.setAlpha(0.30f);
                        isAllowed = false;
                    }
                }
            });


            termCondition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //call web page
                    // Toast.makeText(context,"Call Web Page",Toast.LENGTH_SHORT).show();
                    String url = "https://www.innoplexus.com/index.php/terms-and-conditions/";
                    TermsConditionWebview fragment = new TermsConditionWebview();
                    Activity activity = (Activity) context;
                    Bundle bundle = new Bundle();
                    bundle.putString("Consent", url);
                    fragment.setArguments(bundle);
                    fragment.show(activity.getFragmentManager(), "User Consent");
                }
            });

            dialog.show();

        } catch (Exception e) {
            final ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.errorMessageString = "No User Consent";
            callback.onError(errorResponse);
            dialog.dismiss();
            if(BuildConfig.DEBUG)   Log.i(TAG, "getCustomDialog: Error=" + e.getMessage());
        }


    }


    private void callBackUrlSignUpInvite(final AuthResponseData data, String password, final AuthResponseInterface listner) {
        final ErrorResponse errorResponse = new ErrorResponse();
        boolean isCallBackPresent = getCallBackUrl(data.callbackUrl);
        //From Here we will fetch token for further usage with callback url from login api.
        if (isCallBackPresent) {
            SSOManager.getInstance(mContext).fetchTokenApi(password, new CommonListener() {
                @Override
                public void onSuccess() {
                    UserInfo userInfo = null;
                    if (data.info != null) {
                        userInfo = data.info;
                    }
                    listner.onSuccess(userInfo);
                    setUserStatus(true);
                }

                @Override
                public void onFailure(String error) {
                    errorResponse.errorMessageString = error;
                    listner.onFailureError(errorResponse);
                }
            });
        }
    }


    // It Fetches user token adn store it into local shared preference
    public void fetchTokenApi(final String password, final CommonListener listener) {
        try {
            RetrofitRequestBuilder.getInstance(mContext).getAccessTokenApi(mContext, new Callback<AccessTokenParentPojo>() {
                @Override
                public void onResponse(Call<AccessTokenParentPojo> call, Response<AccessTokenParentPojo> response) {
                    HashMap<String, Boolean> hashMap = StatusCodeHandler.statusCodeCheck(response.code());
                    if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                        AccessTokenParentPojo responseData = response.body();
                        if (responseData != null) {
                            setUserToken(responseData);
                            UserInfo userInfo = responseData.profile;
                            userInfo.passWord = password;
                            userInfo.email = SSOManager.getInstance(mContext).getUserEmail();
                            //setUserSession(userInfo);
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
                    if(BuildConfig.DEBUG) Log.i(TAG, "onFailure: Access Token Error=" + t.getMessage());
                    listener.onFailure(t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void forgotPassword(String lang,String emailId, final AuthResponseInterface listener) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse();
            if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {
                RetrofitRequestBuilder.getInstance(mContext).forgotPassword(lang,emailId, new Callback<RequestParentPojo>() {
                    @Override
                    public void onResponse(Call<RequestParentPojo> call, Response<RequestParentPojo> response) {
                        if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                            AuthResponseData responseData = response.body().authResponseData;
                            if (responseData != null) {
                                listener.onSuccess(responseData);
                            }

                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                setError(jObjError, listener);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestParentPojo> call, Throwable t) {
                        errorResponse.errorMessageString = t.getMessage();
                        listener.onFailureError(errorResponse);
                    }
                });
            } else {
                errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
                listener.onFailureError(errorResponse);
            }
        } catch (Exception e) {
            if(BuildConfig.DEBUG)Log.i(TAG, "forgotPassword: Error=" + e.getMessage());
        }
    }


    public void changePassword(String oldPassword, String newPassword, String confirmPassword, final AuthResponseInterface listener) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse();
            if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {
                RetrofitRequestBuilder.getInstance(mContext).changeUserPassword(oldPassword, newPassword, confirmPassword, new Callback<RequestParentPojo>() {
                    @Override
                    public void onResponse(Call<RequestParentPojo> call, Response<RequestParentPojo> response) {

                        if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                            AuthResponseData responseData = response.body().authResponseData;
                            if (responseData != null) {
                                listener.onSuccess(responseData);
                            }
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                setError(jObjError, listener);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<RequestParentPojo> call, Throwable t) {
                        if(BuildConfig.DEBUG)  Log.i(TAG, "onResponse: ChangePassword=" + t.getMessage());
                        errorResponse.errorMessageString = t.getMessage();
                        listener.onFailureError(errorResponse);
                    }
                });
            } else {
                errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
                listener.onFailureError(errorResponse);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void logoutUser(final String emailId, final AuthResponseInterface listener) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse();
            if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {
                String tokenId = SSOManager.getInstance(mContext).getTokenId();
                if (isUserLoggedIn()) {
                    RetrofitRequestBuilder.getInstance(mContext).logoutUser(mContext, emailId, new Callback<RequestParentPojo>() {
                        @Override
                        public void onResponse(Call<RequestParentPojo> call, final Response<RequestParentPojo> response) {
                            if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                                AuthResponseData responseData = response.body().authResponseData;
                                logout();
                                listener.onSuccess(responseData);

                            } else {
                                if (response.code() == Constant.AUTH_FAIL) {
                                    SSOManager.getInstance(mContext).renewTokenApi(mContext, new LoginCallback() {
                                        @Override
                                        public void onSuccess(UserInfo userInfo) {
                                            if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                                                logoutUser(emailId, listener);
                                            } else {
                                                AuthResponseData authResponseData = new AuthResponseData();
                                                authResponseData.info = null;
                                                authResponseData.message = mContext.getResources().getString(R.string.logout_success);
                                                logout();
                                                listener.onSuccess(authResponseData);
                                            }
                                        }

                                        @Override
                                        public void onError(ErrorResponse error) {
                                            AuthResponseData authResponseData = new AuthResponseData();
                                            authResponseData.info = null;
                                            authResponseData.message = mContext.getResources().getString(R.string.logout_success);
                                            logout();
                                            listener.onSuccess(authResponseData);
                                        }
                                    });
                                } else {
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        setError(jObjError, listener);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                        }

                        @Override
                        public void onFailure(Call<RequestParentPojo> call, Throwable t) {
                            errorResponse.errorMessageString = t.getMessage();
                            listener.onFailureError(errorResponse);
                        }
                    });

                } else {
                    errorResponse.errorMessageString = mContext.getResources().getString(R.string.blankToken);
                    listener.onFailureError(errorResponse);
                    if(BuildConfig.DEBUG)Log.i(TAG, "logoutUser: TokenId Blank");
                }
            } else {
                errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
                listener.onFailureError(errorResponse);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void signUpInviteUser(boolean isActive, String emailId, JSONObject input, final AuthResponseInterface listener) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse();
            if (NetworkManager.getInstance().isConnectingToInternet(mContext)) {
                RetrofitRequestBuilder.getInstance(mContext).signUpUser(isActive, emailId, input, new Callback<RequestParentPojo>() {
                    @Override
                    public void onResponse(Call<RequestParentPojo> call, Response<RequestParentPojo> response) {
                        if (response.code() == Constant.STATUS_OK) {
                            AuthResponseData responseData = response.body().authResponseData;
                            if (responseData != null) {
                                UserInfo data = responseData.info;

                                if (responseData != null) {
                                    if (responseData != null && responseData.otpEnabled != null) {
                                        setOtpCheck(true);
                                    }
                                    if (responseData.callbackUrl != null) {
                                        setSessionKey(response.headers().get(Constant.Authorization));
                                        callBackUrlSignUpInvite(responseData, "", listener);
                                    } else {
                                        listener.onSuccess(data);
                                    }
                                } else {
                                    errorResponse.errorMessageString = "User Info blank";
                                    listener.onFailureError(errorResponse);
                                }

                            }


                        } else {
                            if (response.code() == Constant.STATUS_SUCCESS) {
                                if (response.body().errorMessage != null) {
                                    errorResponse.errorMessageString = response.body().errorMessage;
                                } else {
                                    errorResponse.errorMessageString = Constant.SUCCESS_MESSAGE;
                                }
                                listener.onFailureError(errorResponse);
                            } else {
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    setError(jObjError, listener);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<RequestParentPojo> call, Throwable t) {
                        errorResponse.errorMessageString = t.getMessage();
                        listener.onFailureError(errorResponse);
                    }
                });
            } else {
                errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
                listener.onFailureError(errorResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //For user login through OTP Verification
    public void otpVerification(Context context, String otp, final String password, final LoginCallback callback) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse();
            if (NetworkManager.getInstance().isConnectingToInternet(context)) {
                RetrofitRequestBuilder.getInstance(context).verifyOtp(otp, new Callback<LoginParentPojo>() {
                    @Override
                    public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {
                        if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                            LoginParentPojo loginResponse = response.body();
                            if (loginResponse.responseData.info != null) {
                                callBackMethod(loginResponse.responseData, password, callback);
                            }
                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                seterrorResponse(jObjError, callback);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<LoginParentPojo> call, Throwable t) {
                        errorResponse.errorMessageString = t.getMessage();
                        callback.onError(errorResponse);
                    }
                });
            } else {
                errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
                callback.onError(errorResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //THis method will once user is allowed for OTP Verification and once Token Is Expired.
    public void getCallBackUrlOTPEnabledState(final Context context, final LoginCallback callback) {
        final ErrorResponse errorResponse = new ErrorResponse();
        if (NetworkManager.getInstance().isConnectingToInternet(context)) {
            RetrofitRequestBuilder.getInstance(context).getCallBackUrlOnOtpEnabled(context, new Callback<LoginParentPojo>() {
                @Override
                public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {
                    if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                        if (response != null) {
                            LoginParentPojo data = response.body();

                            if (data.responseData.callbackUrl != null) {
                                SSOManager.getInstance(context).setUserConsent(data.responseData.is_user_consent);
                                callBackMethod(data.responseData, "", callback);
                            }
                        }
                    } else {
                        if (response.code() == Constant.SUBSCRIBE_ERROR) {

                            JSONObject jObjError = null;
                            try {
                                jObjError = new JSONObject(response.errorBody().string());
                                if(BuildConfig.DEBUG) Log.i(TAG, "onLoginOrSubscribeSuccess: " + jObjError);
                                String erroMessage = jObjError.getString("errorMessage");
                                if (erroMessage != null && erroMessage.equalsIgnoreCase("User haven't right access to application")) {
                                    try {
                                        jObjError.put(Constant.errorCode, "403");
                                        jObjError.put(Constant.errorMessage, "User Deactivated");
                                        seterrorResponse(jObjError, callback);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    //callSubscribeUserApi(mContext, callback);
                                    setSessionKey(response.headers().get(Constant.Authorization));
                                    callSubscribeUserApi(mContext, callback);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                seterrorResponse(jObjError, callback);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<LoginParentPojo> call, Throwable t) {
                    errorResponse.errorMessageString = t.getMessage();
                    callback.onError(errorResponse);
                }
            });
        } else {
            errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
            callback.onError(errorResponse);
        }

    }


    //If user token expired then this particular method call  again login api and fetch api to get token.
    public void renewTokenApi(Context context, final LoginCallback callback) {

        if (getRenewTokenConfig()) {
            if (SSOManager.getInstance(context).checkIsOtpEnabled()) {
                getCallBackUrlOTPEnabledState(context, callback);
            } else {
                if (SSOManager.getInstance(context).getUserInfo() != null) {

                    final UserInfo userInfo = SSOManager.getInstance(context).getUserInfo();
                    if (!TextUtils.isEmpty(userInfo.email)) {
                        String email = userInfo.email;
                        String password = userInfo.passWord;

                        SSOManager.getInstance(mContext).fetchTokenApi(password, new CommonListener() {
                            @Override
                            public void onSuccess() {
                                callback.onSuccess(userInfo);

                            }

                            @Override
                            public void onFailure(String error) {
                                ErrorResponse errorResponse = new ErrorResponse();
                                errorResponse.errorMessageString = error;
                                callback.onError(errorResponse);
                            }
                        });
                    } else {

                    }

                }
            }
        } else {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.errorMessageString = "";
            callback.onError(errorResponse);
        }


    }


    //This method store userToken in local storage.
    private void setUserToken(AccessTokenParentPojo accessTokenModel) {
        if (accessTokenModel.accessToken != null && !TextUtils.isEmpty(accessTokenModel.accessToken)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Bearer").append(" ").append(accessTokenModel.accessToken);
            String token = builder.toString();
            SSOManager.getInstance(mContext).setTokenId(token);
        }


        if (accessTokenModel.refreshToken != null && !TextUtils.isEmpty(accessTokenModel.refreshToken)) {
            StringBuilder builder = new StringBuilder();
            builder.append("Bearer").append(" ").append(accessTokenModel.refreshToken);
            String refreshToken = builder.toString();
            SSOManager.getInstance(mContext).setRefreshToken(refreshToken);
        }

    }

    //Get  all  UserInfo from local storage.
    public UserInfo getUserInfo() {
        UserInfo info = null;
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
            ObjectMapper objectMapper = new ObjectMapper();
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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        editor.putString(Constant.PreferenceString.TimeStamp, String.valueOf(timestamp.getTime()));
        if(BuildConfig.DEBUG)Log.i(TAG, "AccessToken TimeStamp=" + timestamp.getTime());
        doCommit();
    }

    private String getTokenTime() {
        return sharedPreferences.getString(Constant.PreferenceString.TimeStamp, Constant.PreferenceString.EmptyString);
    }

    public void checkTokenExpire(Context applicationContext, LoginCallback loginCallback) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String tokenSetTime = getTokenTime();
        if (tokenSetTime != null && !TextUtils.isEmpty(tokenSetTime)) {
            Long timeDiff = timestamp.getTime() - Long.parseLong(tokenSetTime);
            if (timeDiff != null) {
                if(BuildConfig.DEBUG) Log.i(TAG, "Token time difference = " + (((timeDiff / 1000) / 60)));
                if (((timeDiff / 1000) / 60) >= 1380) {
                    if(BuildConfig.DEBUG) Log.i(TAG, "Token time difference = " + (((timeDiff / 1000) / 60)));
                    getCallBackUrlOTPEnabledState(applicationContext, loginCallback);
                } else {
                    loginCallback.onSuccess(getUserInfo());
                }
            }
        }


    }


    //Store User Email
    public String getUserEmail() {
        return sharedPreferences.getString(Constant.PreferenceString.userEmail, Constant.PreferenceString.EmptyString);
    }

    //Store Token into local storage
    public void setUserEmail(String email) {
        editor.putString(Constant.PreferenceString.userEmail, email);
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

    //Get Current  config  url from  local storage.
    public String getCurrentSSOServerUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.apiUrl, Constant.PreferenceString.EmptyString);
    }

    //Set Current Config  url into local storage.
    public void setCurrentSSOServerUrl(String tokenId) {
        editor.putString(Constant.PreferenceString.apiUrl, tokenId);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getCurrentSSOLoginUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.loginUrl, Constant.PreferenceString.EmptyString);
    }

    //Set Current Config  url into local storage.
    public void setCurrentSSOLoginUrl(String tokenId) {
        editor.putString(Constant.PreferenceString.loginUrl, tokenId);
        doCommit();
    }

    public String getCurrentSSOBaseURL() {
        return sharedPreferences.getString(Constant.PreferenceString.baseLoginUrl, Constant.PreferenceString.EmptyString);
    }

    public void setCurrentSSOBaseURL(String url) {
        editor.putString(Constant.PreferenceString.baseLoginUrl, url);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getAuthUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.Auth, Constant.PreferenceString.EmptyString);
    }

    //Set Current Config  url into local storage.
    public void setAuthUrl(String url) {
        editor.putString(Constant.PreferenceString.Auth, url);
        doCommit();
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
        Set<String> blankScope = new HashSet<>();
        return sharedPreferences.getStringSet(Constant.PreferenceString.scope, blankScope);
    }


    //Set Scope Config  values into local storage.
    public void setScope(Set<String> clientId) {
        editor.putStringSet(Constant.PreferenceString.scope, clientId);
        doCommit();
    }


    //Get Scope  config  values from  local storage.
    public String getResponseType() {
        return sharedPreferences.getString(Constant.PreferenceString.responseType, Constant.PreferenceString.EmptyString);
    }


    //Set Scope Config  values into local storage.
    public void setResponseType(String responseType) {
        editor.putString(Constant.PreferenceString.responseType, responseType);
        doCommit();
    }


    //Get isOTPEnabled Method .
    public boolean checkIsOtpEnabled() {
        return sharedPreferences.getBoolean(Constant.PreferenceString.OTP, false);
    }


    //Set OTP Status.
    public void setOtpCheck(boolean isOtpEnabled) {
        editor.putBoolean(Constant.PreferenceString.OTP, isOtpEnabled);
        doCommit();
    }


    //Get Session Key Method .
    public String getSessionKey() {
        return sharedPreferences.getString(Constant.PreferenceString.sessionKey, Constant.PreferenceString.EmptyString);
    }


    //Set Session Key.
    public void setSessionKey(String sessionKey) {
        editor.putString(Constant.PreferenceString.sessionKey, sessionKey);
        doCommit();
    }

    //Get Server Redirection url.
    public String getServerRedirectionUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.RedirectionUrl, Constant.PreferenceString.EmptyString);
    }

    //Set ServerRedirection Url.
    public void setServerRedirectionUrl(String url) {
        editor.putString(Constant.PreferenceString.RedirectionUrl, url);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getAppBaseUrl() {
        return sharedPreferences.getString(Constant.PreferenceString.appUrl, Constant.PreferenceString.EmptyString);
    }

    //Set Current Config  url into local storage.
    public void setAppBaseUrl(String url) {
        editor.putString(Constant.PreferenceString.appUrl, url);
        doCommit();
    }

    //Get Current  config  url from  local storage.
    public String getAppName() {
        return sharedPreferences.getString(Constant.PreferenceString.app, Constant.PreferenceString.EmptyString);
    }

    public void setAppName(String url) {
        editor.putString(Constant.PreferenceString.app, url);
        doCommit();
    }

    public void doCommit() {
        if (editor != null) {
            editor.apply();

        }
    }


    public void seterrorResponse(JSONObject json, LoginCallback callBack) {
        try {
            ErrorResponse errorResponse = new ErrorResponse();
            if(json.has(Constant.errorCode)){
                String errorCode = json.getString(Constant.errorCode);
                errorResponse.errorCode = errorCode;
            }
            errorResponse.errorMessageString = json.getString(Constant.errorMessage);
            callBack.onError(errorResponse);
        } catch (Exception e) {
            Log.e(TAG, "seterrorResponse: Error", e);
            ErrorResponse errorResponse = new ErrorResponse();
            try {
                errorResponse.errorMessageString = json.getString(Constant.errorMessage);
                callBack.onError(errorResponse);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }
    }

    public void setError(JSONObject json, AuthResponseInterface callBack) {
        try {
            ErrorResponse errorResponse = new ErrorResponse();
            if (json.has(Constant.errorMessage)) {
                if(json.has(Constant.errorCode)){
                    String errorCode = json.getString(Constant.errorCode);
                    errorResponse.errorCode = errorCode;
                }
                errorResponse.errorMessageString = json.getString(Constant.errorMessage);
            }
            callBack.onFailureError(errorResponse);
        } catch (Exception e) {
            Log.e(TAG, "seterrorResponse: Error", e);
            ErrorResponse errorResponse = new ErrorResponse();
            try {
                errorResponse.errorMessageString = json.getString(Constant.errorMessage);
                callBack.onFailureError(errorResponse);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
    }


    public void callFBLogin(Context context, String fbId, LoginListener loginCallback) {
        //FacebookManager.getInstance(context).initializeRegisterCallback(context, loginCallback);
    }


   /* public void callLinkedInLogin(Context context,LoginCallback loginCallback){
        LinkedInManager.getInstance(context).initiateLinkedInSDK(context,loginCallback);
    }*/

    public void callGoogleSignIn(Context context, String serverClientId, LoginListener loginCallback) {
        //  GoogleManager.getInstance(context).callGoogleSign(context, serverClientId, loginCallback);
    }


    public void callSubscribeUserApi(final Context context, final LoginCallback loginCallback) {
        ErrorResponse errorResponse = new ErrorResponse();
        if (NetworkManager.getInstance().isConnectingToInternet(context)) {
            RetrofitRequestBuilder.getInstance(context).subscribeUserApi(context, new Callback<LoginParentPojo>() {
                @Override
                public void onResponse(Call<LoginParentPojo> call, Response<LoginParentPojo> response) {
                    if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
                        SessionModel model = new SessionModel();
                        onLoginOrSubscribeSuccess(response, context, loginCallback);

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
                    try {
                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.errorMessageString = t.getMessage();
                        loginCallback.onError(errorResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            errorResponse.errorMessageString = mContext.getResources().getString(R.string.network_error);
            loginCallback.onError(errorResponse);
        }
    }


    //On Subcribe and Login Success
    public void onLoginOrSubscribeSuccess(Response<LoginParentPojo> response, Context mContext, LoginCallback loginCallback) {
        ErrorResponse errorResponse = new ErrorResponse();
        if (StatusCodeHandler.isResponseCodeValidated(response.code(), Constant.STATUS_OK, Constant.STATUS_OK_MAX)) {
            //do your stuffs here
            LoginParentPojo loginResponse = response.body();
            if (loginResponse != null) {
                ResponseData data = loginResponse.responseData;
                if (data.otpEnabled) {
                    setOtpCheck(true);
                    String session = response.headers().get(Constant.Authorization);
                    SSOManager.getInstance(mContext).setSessionKey(session);
                    PreferenceManager.getInstance(mContext).storeSessionKey(session);
                    PreferenceManager.getInstance(mContext).storeUserPassword(passWord);
                    loginCallback.onSuccess(null);
                    setSessionModel(response, mContext);

                } else {
                    if (data.callbackUrl != null) {
                        //setSessionKey(response.headers().get(Constant.Authorization));
                        callBackMethod(data, "", loginCallback);
                        //setSessionModel(response);
                    } else {
                        String session = response.headers().get(Constant.Authorization);
                        SSOManager.getInstance(mContext).setSessionKey(session);
                        PreferenceManager.getInstance(mContext).storeSessionKey(session);
                        PreferenceManager.getInstance(mContext).storeUserPassword(passWord);
                        getCallBackUrlOTPEnabledState(mContext, loginCallback);
                        setSessionModel(response, mContext);
                    }
                }


            } else {
                errorResponse.errorMessageString = response.message();
                loginCallback.onError(errorResponse);
            }
        } else {
            if (response.code() == Constant.SUBSCRIBE_ERROR) {
                //This logic need to change in upComing builds.
                JSONObject jObjError = null;
                try {
                    jObjError = new JSONObject(response.errorBody().string());
                    if(BuildConfig.DEBUG) Log.i(TAG, "onLoginOrSubscribeSuccess: " + jObjError);
                    String erroMessage = jObjError.getString("errorMessage");
                    if (erroMessage != null && erroMessage.equalsIgnoreCase("User haven't right access to application")) {
                        try {
                            jObjError.put(Constant.errorCode, "403");
                            jObjError.put(Constant.errorMessage, "User Deactivated");
                            seterrorResponse(jObjError, loginCallback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        callSubscribeUserApi(mContext, loginCallback);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    loginCallback.onError(new ErrorResponse());
                }
            } else {
                try {
                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                    String erroMessage = jObjError.getString("errorMessage");
                    jObjError.put(Constant.errorCode, String.valueOf(response.code()));
                    jObjError.put(Constant.errorMessage,erroMessage );
                    seterrorResponse(jObjError, loginCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorResponse errorRes = new ErrorResponse();
                    if (response != null && response.code() > 0) {
                        errorRes.errorCode = String.valueOf(response.code());
                    } else {
                        errorRes.errorCode = "";
                    }
                    errorRes.errorMessageString = "Something went wrong , please wait";
                    loginCallback.onError(errorResponse);
                }
            }

        }
    }


    //Get  all  UserInfo from local storage.
    public AccessTokenParentPojo getFilterInfo() {
        AccessTokenParentPojo info = null;
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
            ObjectMapper objectMapper = new ObjectMapper();
            String userString = objectMapper.writeValueAsString(userInfo);
            editor.putString(Constant.PreferenceString.Filter, userString);
            doCommit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<HashMap<String, Object>> getFilterObject(Context context) {
        ArrayList<HashMap<String, Object>> mFilterList = new ArrayList<>();
        AccessTokenParentPojo accessTokenParentPojo = SSOManager.getInstance(context).getFilterInfo();
        if (accessTokenParentPojo.groupsArrayList != null && accessTokenParentPojo.groupsArrayList.size() > 0) {
            ArrayList<Groups> list = accessTokenParentPojo.groupsArrayList;

            for (Groups group : list) {
                if (group.filtersArrayList != null && group.filtersArrayList.size() > 0) {
                    ArrayList<Filters> filterList = group.filtersArrayList;
                    for (Filters filterObject : filterList) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(filterObject.name, filterObject.valuesList);
                        mFilterList.add(map);
                    }
                }

            }
        }

        if(BuildConfig.DEBUG) Log.i(TAG, "getFilterObject: Filter List=" + mFilterList);
        return mFilterList;

    }


    public ArrayList<String> getFilter(Context context, String filterName) {

        ArrayList<String> filters = null;
        try {
            ArrayList<HashMap<String, Object>> list = getFilterObject(context);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    HashMap<String, Object> map = list.get(i);
                    String key = getKey(map);
                    if (key.equalsIgnoreCase(filterName)) {
                        filters = (ArrayList<String>) map.get(filterName);

                        break;
                    }

                }
            }

        } catch (Exception e) {
            Log.i(TAG, "getFilter: Error=" + e.getMessage());
        }

        return filters;
    }

    public boolean getRenewTokenConfig() {
        return isRenewTokenEnabled;
    }

    public void setRenewTokenConfig(boolean isRenewToken) {
        this.isRenewTokenEnabled = isRenewToken;
    }

    public String getKey(HashMap<String, Object> map) {
        Map.Entry<String, Object> entry = map.entrySet().iterator().next();
        String key = entry.getKey();
        return key;
    }


    public Integer getValue(HashMap<String, Integer> map) {
        Map.Entry<String, Integer> entry = map.entrySet().iterator().next();
        Integer value = entry.getValue();
        return value;
    }


    public void setInfoToShare(SessionModel infoToShare, String path, String fileName) {
        try {
            JSONObject userResponse = new JSONObject();
            String userName = infoToShare.firstName + " " + infoToShare.lastName;
            userResponse.put(Constant.SHARE_SESSION.userName, userName);
            userResponse.put(Constant.SHARE_SESSION.email, infoToShare.email);
            userResponse.put(Constant.SHARE_SESSION.password, infoToShare.password);
            userResponse.put(Constant.SHARE_SESSION.sessionKey, infoToShare.sessionKey);
            SessionManager.getManager().writeContentToFile(Constant.SHARE_SESSION.dummy, userResponse, fileName, path);
        } catch (Exception e) {
            Log.i(TAG, "setInfoToShare: Error=" + e.getMessage());
        }

    }

    public void setSessionModel(Response<LoginParentPojo> model, Context context) {
        try {
            //json.put(Constant.SHARE_SESSION.use)

            LoginParentPojo parentPojo = model.body();
            UserInfo info = parentPojo.responseData.info;
            SessionModel sessionModel = new SessionModel();

            if (info.first_name != null)
                sessionModel.firstName = info.first_name;
            if (info.last_name != null)
                sessionModel.lastName = info.last_name;
            if (info.email != null)
                sessionModel.email = info.email;

            sessionModel.password = PreferenceManager.getInstance(context).getPassword();
            sessionModel.sessionKey = PreferenceManager.getInstance(context).getSessionKey();
            sessionModel.sessionKey = AESCrypt.encrypt(Constant.SHARE_SESSION.dummy, sessionModel.sessionKey);
            PreferenceManager.getInstance(mContext).setSessionModel(sessionModel);
        } catch (Exception e) {
            Log.i(TAG, "setSessionModel: Error=" + e.getMessage());
        }


    }

    public void setUserSession(UserInfo info, Context mContext) {
        SessionModel sessionModel = new SessionModel();
        sessionModel.firstName = info.first_name;
        sessionModel.lastName = info.last_name;
        sessionModel.email = info.email;
        sessionModel.password = info.passWord;
        sessionModel.sessionKey = PreferenceManager.getInstance(mContext).getSessionKey();
        if(BuildConfig.DEBUG)Log.i(TAG, "Getting SessionKey from preference manager=" + sessionModel.sessionKey);
        try {
            sessionModel.sessionKey = AESCrypt.encrypt(Constant.SHARE_SESSION.dummy, sessionModel.sessionKey);
            PreferenceManager.getInstance(SSOManager.mContext).setSessionModel(sessionModel);
        } catch (Exception e) {
            Log.i(TAG, "setSessionModel: Error=" + e.getMessage());
        }
    }

    public boolean getUserConsent() {
        return sharedPreferences.getBoolean(Constant.PreferenceString.userConsent, false);
    }

    public void setUserConsent(boolean isConsent) {
        editor.putBoolean(Constant.PreferenceString.userConsent, isConsent);
        doCommit();
    }

    public String getFilePath() {
        return sharedPreferences.getString(Constant.PreferenceString.path, " ");
    }

    public void setFilePath(String isConsent) {
        editor.putString(Constant.PreferenceString.path, isConsent);
        doCommit();
    }

    public String getFileName() {
        return sharedPreferences.getString(Constant.PreferenceString.name, "");
    }

    public void setFileName(String isConsent) {
        editor.putString(Constant.PreferenceString.name, isConsent);
        doCommit();
    }

    public String getSessionID() {
        return sharedPreferences.getString(Constant.PreferenceString.sessionID, " ");
    }

    public void setSessionID(String key) {
        editor.putString(Constant.PreferenceString.sessionID, key);
        doCommit();
    }

    public void setDeviceID(String value) {
        editor.putString(Constant.PreferenceString.deviceId, value);
        doCommit();
    }

    public String getDeviceId() {
        return sharedPreferences.getString(Constant.PreferenceString.deviceId, "");
    }

    public void onLoggedOut(Context context, String path, String fileName) {
        if (SessionManager.getManager().checkShareSession(path, fileName)) {
            File file = new File(path, fileName);
            //file.delete();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("isLoggedIn", false);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "onLoggedOut: Error=" + e.getMessage());
            }
            file.delete();
            SessionManager.getManager().createFile(context, path, fileName);
            String key = com.example.surajbokankar.ssomanager.common.Constant.SHARE_SESSION.dummy;
            SessionManager.getManager().writeContentToFile(key, jsonObject, fileName, path);
            SessionModel sessionModel = new SessionModel();
            com.example.surajbokankar.ssomanager.prefrence.PreferenceManager.getInstance(context).setSessionModel(sessionModel);
        }
    }

    public void initInstance() {
        ssoManager = null;
    }


    //Set String

    public void setUserString(String value, String key) {
        editor.putString(key, value);
        doCommit();
    }

    public String getUserString(String key) {
        return sharedPreferences.getString(key, "");
    }



}