
package com.example.surajbokankar.ssomanager.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;

import com.example.surajbokankar.ssomanager.LoginCallback;
import com.example.surajbokankar.ssomanager.R;
import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.social.model.SocialLoginSuccess;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by suraj.bokankar on 25/5/17.
 */

public class FacebookManager {


    static FacebookManager facebookManager = null;
    CallbackManager callbackManager = null;
    static Context mContext;
    String public_profile = "public_profile";

    public void initializeRegisterCallback(final Context context, final LoginCallback callBack) {

        Activity activity = (Activity) context;

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList(public_profile));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (loginResult != null) {
                    getUserProfile(loginResult.getAccessToken(), callBack);
                }

            }

            @Override
            public void onCancel() {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.errorMessageString = context.getResources().getString(R.string.fb_requestCancel);
                callBack.onError(errorResponse);
            }

            @Override
            public void onError(FacebookException error) {
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.errorMessageString = error.getMessage();
                callBack.onError(errorResponse);
            }
        });

    }


    public static FacebookManager getInstance(Context context) {
        if (facebookManager == null) {
            facebookManager = new FacebookManager();
            mContext = context;
        }
        return facebookManager;
    }

    public void getUserProfile(final AccessToken token, final LoginCallback callBack) {
        try {
            GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    if (response.getError() == null) {
                        try {
                            //String email = object.getString("email");
                            //  Log.i(TAG, "onCompleted: Email of Facebook=" + email+"\n"+object);
                            //This callback depends upon social login api success from here onWards.
                            //callBack.onSocialLoginSuccess("Success");
                            String accessToken = token.getToken();
                            SocialPlugin.getInstance(mContext).callSocialLoginApi(mContext, accessToken, "Facebook", object, callBack);


                        } catch (Exception e) {
                            ErrorResponse errorResponse = new ErrorResponse();
                            errorResponse.errorMessageString = e.getMessage();
                            callBack.onError(errorResponse);
                            Log.i(TAG, "onCompleted: Error=" + e.getMessage());
                        }
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.errorMessageString = e.getMessage();
            callBack.onError(errorResponse);
            Log.i(TAG, "getUserProfile: Error=" + e.getMessage());
        }


    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void logoutUser() {
        LoginManager.getInstance().logOut();
    }
}
