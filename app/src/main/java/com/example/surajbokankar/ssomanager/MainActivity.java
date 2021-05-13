package com.example.surajbokankar.ssomanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {



    AppCompatEditText userName,password,otpVerify;
    AppCompatButton login,otpButton;
    AppCompatTextView forgot,change,signUp,logout,authorize;
    LinearLayout otpView;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // initUI();




    }



   /* private void initUI() {
        userName= (AppCompatEditText) findViewById(R.id.userName);
        password= (AppCompatEditText) findViewById(R.id.password);
        login= (AppCompatButton) findViewById(R.id.login);
        forgot= (AppCompatTextView) findViewById(R.id.forgotPassword);
        change= (AppCompatTextView) findViewById(R.id.changePassword);
        signUp= (AppCompatTextView) findViewById(R.id.signupUser);
        logout= (AppCompatTextView) findViewById(R.id.logoutUser);
        otpVerify= (AppCompatEditText) findViewById(R.id.otpVerify);
        otpButton= (AppCompatButton) findViewById(R.id.otpButton);
        otpView= (LinearLayout) findViewById(R.id.otpView);
        authorize= (AppCompatTextView) findViewById(R.id.authorize);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserAuthentication();
            }
        });

authorize.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        SSOManager.getInstance(MainActivity.this).renewTokenApi(MainActivity.this, new LoginCallback() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                if(userInfo!=null){
                    showToast("Success Using RenewToken");
                }
            }

            @Override
            public void onError(ErrorResponse error) {
               showToast(error.errorMessageString);
            }
        });
    }
});

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SSOManager.getInstance(MainActivity.this).otpVerification(MainActivity.this, otpVerify.getText().toString(), password.getText().toString(),new LoginCallback() {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        if(userInfo!=null){
                            UserInfo info=SSOManager.getInstance(MainActivity.this) .getUserInfo();
                            showToast("Login Success from OTP"+info.first_name+" "+info.email);
                           // Toast.makeText(MainActivity.this,"",Toast.LENGTH_LONG).show();
                            otpView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(ErrorResponse error) {
                        showToast(error.errorMessageString);

                    }


                });
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SSOManager.getInstance(MainActivity.this).logoutUser(userName.getText().toString(), new AuthResponseInterface() {
                   @Override
                   public void onSuccess(Object response) {
                       AuthResponseData authResponseData= (AuthResponseData) response;
                       showToast(((AuthResponseData) response).message);
                   }

                   @Override
                   public void onFailureError(ErrorResponse error) {
                       showToast(error.errorMessageString);

                   }
               });
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SSOManager.getInstance(MainActivity.this).signUpInviteUser(true, userName.getText().toString(), new AuthResponseInterface() {
                   @Override
                   public void onSuccess(Object response) {
                       AuthResponseData authResponseData= (AuthResponseData) response;
                       Toast.makeText(MainActivity.this,"SignUp Success="+authResponseData.message,Toast.LENGTH_LONG).show();
                   }

                   @Override
                   public void onFailureError(ErrorResponse error) {
                          showToast(error.errorMessageString);
                   }
               });
            }
        });

        userName.setText("suraj@10host.top");
        password.setText("Suraj@123");
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SSOManager.getInstance(MainActivity.this).forgotPassword(userName.getText().toString(), new AuthResponseInterface() {
                    @Override
                    public void onSuccess(Object response) {
                      AuthResponseData responseData= (AuthResponseData) response;
                        Toast.makeText(MainActivity.this,responseData.message,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailureError(ErrorResponse error) {
                           showToast(error.errorMessageString);
                    }
                });
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SSOManager.getInstance(MainActivity.this).changePassword(password.getText().toString(), "innoplexus", "innoplexus", new AuthResponseInterface() {
                    @Override
                    public void onSuccess(Object response) {
                        AuthResponseData responseData= (AuthResponseData) response;
                        Toast.makeText(MainActivity.this,responseData.message,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailureError(ErrorResponse error) {
                        showToast(error.errorMessageString);
                    }
                });

            }
        });


        SSOManager.getInstance(MainActivity.this).setCurrentServerUrl("https://staging.sso.innoplexus.de/api/v0/");
        SSOManager.getInstance(MainActivity.this).setCurrentSSOLoginUrl("https://staging.sso.innoplexus.de/api/v0/oauth/");
        SSOManager.getInstance(MainActivity.this).setClientId("mOIkVH7HmrTnyUwpEBAhUUhJOPMEeNWPR0fwKMVR");
        SSOManager.getInstance(MainActivity.this).setResponseType("code");

        SSOManager.getInstance(MainActivity.this).setAuthUrl("https://staging.kplexus.net/api/v0/auth/");

        Set<String> values=new HashSet<>();
        values.add("login");
        values.add("permissions");
        values.add("info");
        values.add("plan");
        values.add("filters");

        SSOManager.getInstance(MainActivity.this).setScope(values);

        //Set This value in Config class of every app to Update Base Url
        Constant.BASE_URL=SSOManager.getInstance(MainActivity.this).getCurrentServerUrl();


        ConfigConstant.BASE_URL=SSOManager.getInstance(MainActivity.this).getCurrentServerUrl();
        ConfigConstant.CLIENT_ID =SSOManager.getInstance(MainActivity.this).getClientID();
        ConfigConstant.SCOPE=SSOManager.getInstance(MainActivity.this).getScope();
        ConfigConstant.RESPONSE_TYPE=SSOManager.getInstance(MainActivity.this).getResponseType();
        ConfigConstant.OAUTH_URL =SSOManager.getInstance(MainActivity.this).getCurrentSSOLoginUrl();

    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this,message,Toast.LENGTH_SHORT).show();
    }

    public  void getUserAuthentication(){
        SSOManager ssoManager=SSOManager.getInstance(MainActivity.this);
        ssoManager.login(userName.getText().toString(), password.getText().toString(), new LoginCallback() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                if(userInfo==null){
                    Toast.makeText(MainActivity.this,"Otp Send To Email Please login with same",Toast.LENGTH_LONG).show();
                    otpView.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(MainActivity.this,"Login Success",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(ErrorResponse error) {
                showToast(error.errorMessageString);
            }


        });
    }

    public void renewTokenApi(){
        SSOManager.getInstance(MainActivity.this).renewTokenApi(MainActivity.this, new LoginCallback() {
            @Override
            public void onSuccess(UserInfo userInfo) {
               showToast("renewToken Success");
            }

            @Override
            public void onError(ErrorResponse error) {

                showToast(error.errorMessageString);
            }



        });
    }*/


}
