package com.example.surajbokankar.ssomanager.common;

/**
 * Created by suraj.bokankar on 9/3/17.
 */

public class Constant {

    public static String BASE_URL = null;

    public static int STATUS_OK = 200;

    public static int STATUS_SUCCESS = 201;

    public static int STATUS_OK_MAX = 299;

    public static int AUTH_FAIL=401;

    public static int SUBSCRIBE_ERROR=403;

    public static  String Authorization="Authorization";


    public static String errorCode="errorCode";

    public static String errorMessage="errorMessage";


    public interface PreferenceString {
        String preferenceString = "kplexus";
        String tokenString = "token";
        String EmptyString = "";
        String UserDetails = "userInfo";
        String refreshTokenString = "refresh";
        String code = "code";
        String UserStatus = "user";
        String apiUrl = "url";
        String loginUrl = "login";
        String clientID= "clientID";
        String responseType = "responseType";
        String scope= "scope";
        String Auth= "auth";
        String sessionKey= "session";
        String OTP= "otp";
        String RedirectionUrl= "redirectUrl";
        String appUrl="appUrl";
        String app = "app";
        String Filter = "filter";
    }


    public interface LoginRequest {
        String emailId = "email";
        String password = "password";
        String isRequetSuccess = "ok";
        String isAuthFailed = "auth";
        String response_type = "response_type=";
        String scope = "scope=";
        String client_id = "?client_id=";
        String connection = "connection=";
        String redirectUri = "redirect_uri=";
        String accessToken = "access_token";

    }

    public interface SSORequest{
                String confirmPassword="confirm_password";
                String newPassword="new_password";
                String oldPassword="password";
                String emailId="email";
                String isActive="is_invite";
                String clientId="client_id";
                String otp="otp";
    }


    public interface GoogleSignInRequest{
        String name="name";
        String email="email";
        String givenName="giveName";
        String token="token";
    }


public static String SUCCESS_MESSAGE="Profile created. Please verify your email.";

}

