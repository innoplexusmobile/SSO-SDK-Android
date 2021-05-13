
package com.example.surajbokankar.ssomanager.social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.surajbokankar.ssomanager.LoginCallback;
import com.example.surajbokankar.ssomanager.LoginListener;
import com.example.surajbokankar.ssomanager.R;
import com.example.surajbokankar.ssomanager.common.Constant;
import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.ssomanager.SSOManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.gms.wearable.DataMap.TAG;


/**
 * Created by suraj.bokankar on 25/5/17.
 */


public class GoogleManager implements GoogleApiClient.OnConnectionFailedListener {


     static  GoogleManager googleManager=null;
    GoogleApiClient mGoogleApiClient=null;
    private static final int RC_SIGN_IN = 9001;
    LoginListener loginCallback;
    static  Context  mContext=null;
     public  static  GoogleManager getInstance(Context context){
         if(googleManager==null){
             googleManager=new GoogleManager();
             mContext=context;
         }
         return  googleManager;
     }


     public void initGoogleSignIn(Context context,String serverClientId){

         Activity activity= (Activity) context;
         GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                     .requestEmail().requestIdToken(serverClientId).requestScopes(new Scope(Scopes.PLUS_LOGIN)).requestScopes(new Scope(Scopes.PLUS_ME))
                     .build();
         mGoogleApiClient = new GoogleApiClient.Builder(context)            //.enableAutoManage(activity,this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                     .build();

         mGoogleApiClient.connect();

     }


     public void callGoogleSign(Context context, String serverClientId,LoginListener callback){
         initGoogleSignIn(context,serverClientId);
         Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
         Activity activity= (Activity) context;
         this.loginCallback=callback;
         activity.startActivityForResult(signInIntent,RC_SIGN_IN);
     }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.i(TAG, "handleSignInResult: Name="+acct.getDisplayName()+"\n"+acct.getEmail());
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put(Constant.GoogleSignInRequest.name,acct.getDisplayName());
                jsonObject.put(Constant.GoogleSignInRequest.givenName,acct.getGivenName());
                jsonObject.put(Constant.GoogleSignInRequest.email,acct.getEmail());
//                SocialPlugin.getInstance(mContext).callSocialLoginApi(mContext,acct.getIdToken(),"Google",jsonObject,loginCallback);
                SSOManager.getInstance(mContext).loginDirect(acct.getEmail(),"", SSOManager.getInstance(mContext).getDeviceId(), true, "google", acct.getIdToken(), loginCallback);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            // Signed out, show unauthenticated UI.
            ErrorResponse errorResponse=new ErrorResponse();
            errorResponse.errorMessageString="Login Error";
            loginCallback.onError( errorResponse.errorMessageString);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed: Error="+connectionResult.getErrorMessage());
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.errorMessageString=connectionResult.getErrorMessage();
        loginCallback.onError( errorResponse.errorMessageString);
    }

    public void logoutUser(Context context){
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
            if(mGoogleApiClient.isConnected()){
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        Log.i(TAG, "onResult: True");
                    }
                });
            }

        }

    }
}
