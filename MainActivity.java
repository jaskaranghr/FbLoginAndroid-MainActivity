package com.example.fbngooglelogin;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.jar.Attributes;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "facebook";

    LoginButton btnLogin;

    TextView tvUsername;

    TextView tvEmail;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // FacebookSdk.sdkInitialize(getApplicationContext());

        tvUsername = findViewById(R.id.tvUsername);

        tvEmail = findViewById(R.id.tvEmail);

        btnLogin = findViewById(R.id.btnLogin);

        Boolean loggedOut = AccessToken.getCurrentAccessToken() == null;

        if (!loggedOut) {

            tvUsername.setText(Profile.getCurrentProfile().getName());

           // tvEmail.setVisibility(View.INVISIBLE);

        }

        final AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                if (currentAccessToken == null) {

                    Log.d(TAG,"Logged out");

                    tvEmail.setVisibility(View.INVISIBLE);

                    tvUsername.setText("Login");



                }

            }
        };

        btnLogin.setReadPermissions(Arrays.asList("email","public_profile"));

        btnLogin.setAuthType("rerequest");

        callbackManager = CallbackManager.Factory.create();

        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Log.d(TAG, "onSuccess: Login Successful");

//                        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
//
//                        startActivity(intent);

                        String username = Profile.getCurrentProfile().getName();

                        tvUsername.setText(username);

                        AccessToken accessToken = loginResult.getAccessToken();

                        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                Log.v("response",response.toString());

                                try {

                                    String email = object.getString("email");

                                    //String link = object.getString("link");

                                    tvEmail.setText(email);

                                    tvEmail.setVisibility(View.VISIBLE);

                                    accessTokenTracker.startTracking();


                                } catch (JSONException e) {

                                }

                            }
                        });

                        Bundle parameters = new Bundle();

                        parameters.putString("fields","email,id");

                        graphRequest.setParameters(parameters);

                        graphRequest.executeAsync();
                        
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                        System.out.println("error"+error);

                    }
                }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        callbackManager.onActivityResult(requestCode,resultCode,data);

        super.onActivityResult(requestCode, resultCode, data);
    }
}
