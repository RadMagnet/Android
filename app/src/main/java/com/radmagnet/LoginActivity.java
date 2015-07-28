package com.radmagnet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.radmagnet.database.RadContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nikhil on 6/18/15.
 */
public class LoginActivity extends Activity {

    private static Context myContext;
    private boolean loginStatus;

    private SharedPreferences settings;
    private SharedPreferences userInterests;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;
    private ProfileTracker profileTracker;
    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = loginResult.getAccessToken();
            Log.e("FB-Login", "onSuccess - callback");
            if (!loginStatus) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("loginStatus", true);
                editor.apply();
            }
            finish();
            startActivity(getIntent());
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences(RadContract.SystemConstants.PREFS_NAME, MODE_PRIVATE);
        loginStatus = settings.getBoolean("loginStatus", false);
        myContext = getApplicationContext();

        facebookLoginMethods();

        if (loginStatus) {
            //  User is logged in
            setContentView(R.layout.loggedin_view);
            Log.e("LoggedIn", "works");
            displayLogged();
            ImageView selectCategories  =   (ImageView) findViewById(R.id.select_categories);
            selectCategories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent j = new Intent(myContext, CategoriesActivity.class);
                    startActivity(j);
                    LoginActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }
            });
        } else {
            //  User is not logged in
            setContentView(R.layout.login_view);

            Log.e("LoginActivity", "This is the Login Page");


            /**
             *  Initialize and Start Tracking Facebook Login
             *
             * */
            final List<String> permissions = new ArrayList<>();
            permissions.add("email");
            permissions.add("user_birthday");
            permissions.add("user_likes");

            LinearLayout sampleFB   =   (LinearLayout) findViewById(R.id.sampleFB);

            sampleFB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, permissions);
                }
            });
        }


        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_view);
        LinearLayout backButton = (LinearLayout) findViewById(R.id.back_trigger);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                finish();
                LoginActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

    }

    private void displayLogged() {

        Log.e("FB-Login", "onSuccess - displayLogged");
        FacebookSdk.sdkInitialize(myContext);
        Profile profile = Profile.getCurrentProfile();

        if (profile != null) {
            String userName = profile.getName();
            Uri userPic = profile.getProfilePictureUri(400, 400);

            TextView profileName = (TextView) findViewById(R.id.profile_name);
            ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);

            Picasso.with(myContext).load(userPic).into(profilePic);
            profileName.setText(userName);
        } else {
            profileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                    Log.e("FB-Login", "Profile Tracker");
                    if (newProfile != null) {
                        String userName = newProfile.getName();
                        Uri userPic = newProfile.getProfilePictureUri(400, 400);

                        TextView profileName = (TextView) findViewById(R.id.profile_name);
                        ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);

                        Picasso.with(myContext).load(userPic).into(profilePic);
                        profileName.setText(userName);
                    }
                }
            };
        }


    }

    public void facebookLoginMethods() {

        FacebookSdk.sdkInitialize(myContext);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, callback);
        Log.e("FB-Login", "facebookLoginMethods");
        Profile profile = Profile.getCurrentProfile();
        displayUserProfile(profile);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                accessToken = newToken;
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.e("FB-Login", "Profile Tracker");
                displayUserProfile(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();


    }

    private void displayUserProfile(Profile profile) {

        if (profile != null) {

            Log.e("FB-Login", "onSuccess -> " + profile.getName());
            SharedPreferences.Editor editor = settings.edit();
            if (!loginStatus) {

                editor.putBoolean("loginStatus", true);
            }else{

                editor.putString("loginName", profile.getName());
                editor.putString("loginPic", String.valueOf(profile.getProfilePictureUri(400, 400)));
                editor.putString("loginSource", "fb");
                editor.putString("loginId", profile.getId());
            }
            editor.apply();
//            profileName.setText(userName);
//
//            Picasso.with(myContext).load(userPic).into(profilePic);

            try {

                userInterests = getSharedPreferences(RadContract.SystemConstants.PREFS_NAME, Context.MODE_PRIVATE);
                String sentInterests = userInterests.getString("userInterestsFB", "0");
                Log.e("sentInterests", sentInterests);
                assert (sentInterests) != null;
                if ((sentInterests).equals("0")) {

                    final GraphRequest request = GraphRequest.newMeRequest(
                            accessToken,
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {

                                    if (response != null) {

                                        try {

                                            JSONArray likes = object.getJSONObject("likes").getJSONArray("data");
                                            Log.e("userLikes", String.valueOf(object));

                                            for (int i = 0; i < likes.length(); i++) {
                                                String like = likes.getJSONObject(i).getString("name");
                                                Log.e("eachLike", like);
                                            }

                                            final String nextRequest = object.getJSONObject("likes").getJSONObject("paging").getString("next");

                                            if (nextRequest != null) {

                                                getRemainingLikes(nextRequest);
                                                SharedPreferences.Editor editor = userInterests.edit();
                                                editor.putString("userInterestsFB", "1");
                                                editor.apply();

                                            }

                                        } catch (NullPointerException | JSONException e) {
//                                            Log.e("FBPaginationError", e.getMessage());
                                        }
                                    }

                                }
                            });


                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,link,likes");
                    parameters.putString("limit", "999");
                    request.setParameters(parameters);
                    request.executeAsync();



                }
            } catch (NullPointerException e) {
                Log.e("checkingFBinterestSent", e.getMessage());
            }


        } else {
            Log.e("profileTracker", "No user logged in");
        }

    }

    public void getRemainingLikes(final String nextRequest) {

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                URL url = null;
                String radJSONstr;
                try {
                    url = new URL(nextRequest);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();

                    if (inputStream == null) {
                        return null;
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }

                    radJSONstr = buffer.toString();

                    JSONObject likesObject = new JSONObject(radJSONstr);

                    JSONArray likes = likesObject.getJSONArray("data");

                    for (int i = 0; i < likes.length(); i++) {
                        String like = likes.getJSONObject(i).getString("name");
                        Log.e("eachLike-Remaining", like);
                    }

                    String newRequest = likesObject.getJSONObject("paging").getString("next");

                    if (newRequest != null) {
                        getRemainingLikes(newRequest);
                    }


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }


            @Override
            protected void onPostExecute(String result) {


            }
        }.execute();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        LoginActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

}
