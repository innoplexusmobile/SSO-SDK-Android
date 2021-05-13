package com.example.surajbokankar.ssomanager;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.surajbokankar.ssomanager.common.CommonListener;
import com.example.surajbokankar.ssomanager.common.Constant;
import com.example.surajbokankar.ssomanager.databinding.LoginWebViewBinding;
import com.example.surajbokankar.ssomanager.model.ErrorResponse;
import com.example.surajbokankar.ssomanager.network.NetworkManager;
import com.example.surajbokankar.ssomanager.prefrence.PreferenceManager;
import com.example.surajbokankar.ssomanager.ssomanager.SSOManager;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LoginWeb extends Fragment {

    LoginWebViewBinding loginWebViewBinding = null;
    private static final String TAG = "LoginWebView";
    private static Context mContext;
    public static LoginWeb loginWeb=null;
    private WebView webView;

    public static LoginWeb getInstance(Context context){
        if (loginWeb==null){
            loginWeb=new LoginWeb();
            mContext = context;
        }
        return loginWeb;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loginWebViewBinding = DataBindingUtil.inflate(
                inflater, R.layout.login_web_view, container, false);
        View view = loginWebViewBinding.getRoot();
        webView = (WebView) view.findViewById(R.id.webview);
        return view;
    }

    public void initViews(final LoginCallback callback) {
        setUrl(callback);
    }

    private void setUrl(LoginCallback callBack) {
        if(webView!=null){
            webView.setWebViewClient(new MyWebViewClient(callBack));
            String url = getLoginURL();
//            String url = "https://staging.sso.innoplexus.de/#/auth/login?redirect_url=https%253A%252F%252Fstaging.kplexus.net%252F&client_id=8mw9DF9xJSq9Kf6GXkQjjAb11rfOt6P7AoD3hUTR&scope=login%20info%20permissions%20filters%20roles%20plan";
            webView.loadUrl(url);
            WebSettings settings = webView.getSettings();
            settings.setDomStorageEnabled(true);
            settings.setJavaScriptEnabled(true);
            settings.setAllowFileAccess(true);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        LoginCallback callback;
        public MyWebViewClient(LoginCallback callBack) {
            this.callback = callBack;
        }

        @Override
        public void onPageFinished(WebView view, String url){
            String cookies = CookieManager.getInstance().getCookie(url);
            if (cookies!=null && cookies.contains("sso_sid")){
                List<String> items = Arrays.asList(cookies.split("="));
                cookies = items.get(items.indexOf("sso_sid")+1);
                Log.i(TAG, "onPageFinished: " + cookies);
                SSOManager.getInstance(mContext).setSessionKey(cookies);
                PreferenceManager.getInstance(mContext).storeSessionKey(cookies);
            }
        }

        @Override
        //show the web page in webview but not in web browser
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            getCallBackUrl(url);
            loginUser(mContext,"",callback);
            return true;
        }
    }

    private static void getCallBackUrl(String url) {
        String code = "";
        if (!TextUtils.isEmpty(url) && url.contains("=")) {
            code = url.split("=")[1];
            code = code.split("&")[0];
            Log.i(TAG, "getCallBackUrl: " + code);
            SSOManager.getInstance(mContext).setRedirectCode(code);
        }
    }

    private void loginUser(final Context context, String password, final LoginCallback callback){
        final ErrorResponse errorResponse=new ErrorResponse();
        if(NetworkManager.getInstance().isConnectingToInternet(context)) {
            SSOManager.getInstance(mContext).fetchTokenApi(password, new CommonListener() {
                @Override
                public void onSuccess() {
                    SSOManager.getInstance(mContext).setSessionID(SSOManager.getInstance(mContext).getSessionKey());
                    SSOManager.getInstance(mContext).setUserStatus(true);
                    SSOManager.getInstance(mContext).setUserSession(SSOManager.getInstance(mContext).getUserInfo(), mContext);
                    callback.onSuccess(SSOManager.getInstance(mContext).getUserInfo());
                }

                @Override
                public void onFailure(String error) {
                    errorResponse.errorMessageString=error;
                    callback.onError(errorResponse);
                }
            });
        }else{
            errorResponse.errorMessageString=mContext.getResources().getString(R.string.network_error);
            callback.onError(errorResponse);
        }
    }
    public  void reInit(){
        loginWeb=new LoginWeb();
    }



    private String getLoginURL(){

        StringBuilder builder=new StringBuilder();
        StringBuilder loginUrl=new StringBuilder();

        String url= SSOManager.getInstance(mContext).getCurrentSSOBaseURL();
        String redirectURL =  SSOManager.getInstance(mContext).getServerRedirectionUrl();

        Set<String> Scope= SSOManager.getInstance(mContext).getScope();

        if(Scope!=null){
            for(String s: Scope){
                builder.append(s).append(" ");
            }
            String scope=builder.toString();
            try {
                String clientId= SSOManager.getInstance(mContext).getClientID();
                loginUrl.append( url + "#/auth/login?redirect_url=").append(redirectURL).append("&client_id=").append(clientId).
                        append("&").append(Constant.LoginRequest.scope).
                        append(URLEncoder.encode(scope,"UTF-8").replace("+", "%20"));


            } catch (Exception e) {
                e.printStackTrace();

            }

        }
        Log.i(TAG, "getLoginUrl: LOgin="+loginUrl.toString());
        return loginUrl.toString();
    }
}
