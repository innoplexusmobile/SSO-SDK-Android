package com.example.surajbokankar.ssomanager.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.surajbokankar.ssomanager.LoginCallback;
import com.example.surajbokankar.ssomanager.R;
import com.example.surajbokankar.ssomanager.common.Constant;
import com.example.surajbokankar.ssomanager.common.StatusCodeHandler;
import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.model.LoginParentPojo;
import com.example.surajbokankar.ssomanager.network.NetworkManager;
import com.example.surajbokankar.ssomanager.network.RetrofitRequestBuilder;


import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by suraj.bokankar on 25/5/17.
 */

public class LinkedInManager {


    static  LinkedInManager manager=null;
    private static final String TAG = "LinkedInManager";
    static  Context mContext;
    public static LinkedInManager getInstance(Context context){
        if(manager==null){
            manager=new LinkedInManager();
            mContext=context;
        }
        return manager;

    }


   /* public void initiateLinkedInSDK(final Context context, final LoginCallback callBack){
        final Activity thisActivity = (Activity) context;

        LISessionManager.getInstance(context).init(thisActivity, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                LISession session=LISessionManager.getInstance(thisActivity).getSession();
                if(session.getAccessToken()!=null){
                    String token=session.getAccessToken().toString();
                    AccessToken accessToken=session.getAccessToken();
                    Log.i(TAG, "onAuthSuccess: Access Token="+accessToken+"\n"+token);
                    getProfileDetails(accessToken,context,callBack);
                }
                Log.i(TAG, "onAuthSuccess: session"+session.getAccessToken());

            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.i(TAG, "onAuthError: Error="+error.toString());
                ErrorResponse errorResponse=new ErrorResponse();
                errorResponse.errorMessageString=error.toString();
                callBack.onError(errorResponse);
            }
        }, true);
    }



    private static Scope buildScope() {
        return Scope.build(Scope.R_EMAILADDRESS, Scope.W_SHARE);
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Activity activity= (Activity) mContext;
        LISessionManager.getInstance(mContext).onActivityResult(activity, requestCode, resultCode, data);
    }


    public  void getProfileDetails(final AccessToken accessToken, final Context context, final LoginCallback  callback){
        if(NetworkManager.getInstance().isConnectingToInternet(context)){
            String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";

            APIHelper apiHelper = APIHelper.getInstance(context);
            apiHelper.getRequest(context, url, new ApiListener() {
                @Override
                public void onApiSuccess(ApiResponse apiResponse) {
                    String token=accessToken.getValue();
                    JSONObject responseJson=apiResponse.getResponseDataAsJson();
                    SocialPlugin.getInstance(context).callSocialLoginApi(context,token,"linkedIn",responseJson,callback);
                }

                @Override
                public void onApiError(LIApiError liApiError) {
                    ErrorResponse errorResponse=new ErrorResponse();
                    errorResponse.errorMessageString=liApiError.getMessage();
                    callback.onError(errorResponse);
                }
            });
        }
    }

*/
}
