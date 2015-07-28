package com.radmagnet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;
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


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public MaterialViewPager mViewPager;
    String[] categoriesArray;
    String[] categoriesTitleArray;
    SharedPreferences settings;
    String userNumber;
    String userSource;
    FragmentStatePagerAdapter radPagerAdapter;
    private static Context myContext;
    public boolean firstStart;
    String userPrefs;


    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };


    public MaterialViewPager getMaterialPager(){
        return mViewPager;
    }

    public String[] getCategoriesArray(){
        return categoriesArray;
    }

    public String[] getCategoriesTitleArray(){
        return categoriesTitleArray;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         *  Setting up Activity Constants
         * */

        SQLiteDatabase sqlDB = RadDbHelper.getInstance(getApplicationContext()).getWritableDatabase();
        myContext = getApplicationContext();
        settings = getSharedPreferences(RadContract.SystemConstants.PREFS_NAME, MODE_PRIVATE);
        userNumber = settings.getString(getResources().getString(R.string.sharedprefs_loginid), "notset");
        userSource          = settings.getString(getResources().getString(R.string.sharedprefs_loginsource), "notset");
        userPrefs           = settings.getString(getResources().getString(R.string.sharedprefs_categoryprefs), "0");
        final DrawerLayout mDrawerLayout               =   (DrawerLayout) findViewById(R.id.drawer_layout);
        Integer welcomeNumber    =   settings.getInt(getResources().getString(R.string.sharedprefs_welcome_number), 1);
        firstStart          =   settings.getBoolean(getResources().getString(R.string.sharedprefs_first_start), true);
        FrameLayout welcomeMsg     =   (FrameLayout) findViewById(R.id.welcome_message);

        setWelcomeMessage(welcomeNumber);

        try{
            if(!userPrefs.equals("0")){
                userPrefs   =   userPrefs.substring(1, userPrefs.length()-1);
            }
        }catch(NullPointerException e){

        }


        if (firstStart) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstStart", false);
            editor.putString(getResources().getString(R.string.sharedprefs_APIrequestString), "0");
            editor.apply();
            Intent k = new Intent(this, Tutorial.class);
            startActivity(k);
        }

        /**
         *  Fetching required Database-Data
         * */
        categoriesArray = fetchCategories(sqlDB, userPrefs);


        /**
         *  Inflating Navigation Drawer
         * */
        /*
      Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /**
         *  Setting up ViewPagerAdapter
         * */
        mViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);

        radPagerAdapter =   new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                if(position == 0){

                    return new ScrollFragment();

                }else{
                    Bundle args = new Bundle();
                    args.putString("selectedCategory", categoriesArray[position]);

                    RecyclerViewFragment selectedFragment = new RecyclerViewFragment();
                    selectedFragment.setArguments(args);
                    return selectedFragment;
                }

            }

            @Override
            public int getCount() {
                return categoriesArray.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return categoriesTitleArray[position];
            }

        };

        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.MaterialViewPagerListener() {


            @Override
            public HeaderDesign getHeaderDesign(int page) {

                Integer currentTab = page;
                String currentCategory = categoriesArray[currentTab];

                return null;

            }
        });



        Toolbar toolbar = mViewPager.getToolbar();

        if (toolbar != null) {
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);

            actionBar.hide();
        }


        Log.e("Adapter Set", "OnCreate");
        if (isOnline()) { //  Connected to internet
            fetchMoreRads();
        } else {  //  Not Connected to Internet

            NewRadAdapter newRadAdapter =   new NewRadAdapter();
            newRadAdapter.establishMaterialPager(myContext, mViewPager, radPagerAdapter);

            welcomeMsg.setVisibility(View.GONE);

            Toast.makeText(myContext, "No Internet!", Toast.LENGTH_LONG).show();
        }

        facebookLoginMethods();
        LinearLayout logout = (LinearLayout) findViewById(R.id.logout_trigger);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("activity", "logout");
                FacebookSdk.sdkInitialize(myContext);
                LoginManager.getInstance().logOut();
            }
        });


        View menuTrigger    =   findViewById(R.id.menu_trigger);
        menuTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("clicked", "oh yeah");
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });



    }

    public void facebookLoginMethods() {

        FacebookSdk.sdkInitialize(myContext);
        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, callback);
        Log.e("FB-Login", "facebookLoginMethods");

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                Log.e("FB-Login", "Profile Tracker");
                try {
                    if(newProfile   ==  null){
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean(getResources().getString(R.string.sharedprefs_loginstatus), false);
                        editor.apply();
                        displayLogged();
                    }else{
                        Log.e("logoutVerdict", "userLoggedIn");
                    }
                }catch(Exception e){
                    Log.e("FB error", e.getMessage());
                }
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();



    }

    public String[] fetchCategories(SQLiteDatabase sqlDB, String userPrefs) {

        RadDbHelper dbHelper    =   RadDbHelper.getInstance(getApplicationContext());
        String Where            =   dbHelper.fetchUserPreferencesCategories(userPrefs);

        String queryString;
        if(Where.length() > 5){

            queryString =   "SELECT * FROM " + RadContract.TABLE_CATEGORIES + " WHERE " + Where;

        }else{

            queryString =   "SELECT * FROM " + RadContract.TABLE_CATEGORIES;

        }

        Cursor cursor   =   sqlDB.rawQuery(queryString, null);

        ArrayList<String> categoriesArray = new ArrayList<>();
        ArrayList<String> categoriesNameArray = new ArrayList<>();

        try{
            cursor.moveToFirst();
            categoriesArray.add("pop_rads");
            categoriesNameArray.add("Highlights");
            while (!cursor.isAfterLast()) {
                categoriesArray.add(cursor.getString(cursor.getColumnIndex(RadContract.Categories.COLUMN_CATEGORY_NAME)));
                categoriesNameArray.add(cursor.getString(cursor.getColumnIndex(RadContract.Categories.COLUMN_CATEGORY_TITLE)));
                cursor.moveToNext();
            }
        }finally{
            cursor.close();
//            sqlDB.close();
        }

        categoriesTitleArray = categoriesNameArray.toArray(new String[categoriesArray.size()]);

        return categoriesArray.toArray(new String[categoriesArray.size()]);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void fetchMoreRads() {

        FetchRads fetchRads = new FetchRads();

        String requestString = settings.getString(getResources().getString(R.string.sharedprefs_APIrequestString), "");

        fetchRads.execute(requestString);

    }

    public class FetchRads extends AsyncTask<String, Void, String[][]> {

        @Override
        protected String[][] doInBackground(String... params) {
            //  Connecting to the RadMagnet API to receive JSON data
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //  String to contain raw JSON response from RadMagnet API
            String radJSONstr;

            //  Try Code - Catch Error - Execute Finally regardless
            try {

                String standardRadRequestURL;
                String userChoices;

                if(userPrefs.contains(",")){
                    userChoices  =   userPrefs.replaceAll(",", "-").replaceAll("\\s", "");
                }else{
                    userChoices  =   userPrefs;
                }


                switch (params[0]) {
                    case "0":

                        standardRadRequestURL = "http://www.radmagnet.com/GET/highlights/0/v4/" + userNumber + "/" + userSource + "/" + userChoices;

                        break;
                    default:

                        standardRadRequestURL = "http://www.radmagnet.com/GET/highlights/" + params[0] + "/v4/" + userNumber + "/" + userSource + "/" + userChoices;

                        break;
                }

                Log.e("APIRequestString", standardRadRequestURL);

                URL url = new URL(standardRadRequestURL);

                //  Creating the connection and opening the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //  Read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    // If no data is received ... Nothing to do
                    return null;

                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while ((line = reader.readLine()) != null) {

                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }


                //  If everything is fine, we will receive our JSON response from the RadMagnet server
                radJSONstr = buffer.toString();


                //  Parsing JSON response
                JSONObject radInput = new JSONObject(radJSONstr);

                //  Isolating the userTimeString
                String userTimeString = radInput.getString("date");

                //  Setting userTimeString as the next requestString setting
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(getResources().getString(R.string.sharedprefs_APIrequestString), userTimeString);
                editor.apply();


                //  Isolating the radsArray
                JSONArray radsArray = radInput.getJSONArray("data");


                //  Our Results Array
                String[][] resultsArray = new String[radsArray.length()][10];


                Log.e("IncomingCount", String.valueOf(radsArray.length()));
                //  Extracting rads from radsArray
                for (int i = 0; i < radsArray.length(); i++) {

                    JSONObject eachRad = radsArray.getJSONObject(i);

                    //  Extracting "category" of the rad
                    String category = eachRad.getString("category");
                    //  Extracting "post_id" of the rad
                    String post_id = eachRad.getString("post_id");
                    //  Extracting "image_url" of the rad
                    String image_url = eachRad.getString("image_url");
                    //  Extracting "creator" of the rad
                    String creator = eachRad.getString("creator");
                    //  Extracting "creator_dp" of the rad
                    String creator_dp = eachRad.getString("creator_dp");
                    //  Extracting "headline" of the rad
                    String headline = eachRad.getString("headline");
                    //  Extracting "headline_sub" of the rad
                    String headline_sub = eachRad.getString("headline_sub");

                    String location = null;
                    String start_date = null;

                    switch (category) {
                        case "hotspots":
                            //  Extracting location of hotspot
                            location = eachRad.getString("location");

                            break;
                        case "events":
                            //  Extracting start_date of event
                            start_date = eachRad.getString("start_date");

                            break;
                        case "jobs_internships":
                            //  start_date is expiry date
                            //  location is job/intenrship
                            start_date  =   eachRad.getString("expiry_date");
                            location    =   eachRad.getString("job_internship");

                            break;
                        default:

                            break;
                    }


                    //  Populating the resultsArray
                    resultsArray[i][0] = userTimeString; // timestamp for the next query
                    resultsArray[i][1] = category;
                    resultsArray[i][2] = post_id;
                    resultsArray[i][3] = image_url;
                    resultsArray[i][4] = creator;
                    resultsArray[i][5] = creator_dp;
                    resultsArray[i][6] = location;
                    resultsArray[i][7] = start_date;
                    resultsArray[i][8] = headline;
                    resultsArray[i][9] = headline_sub;

                }

                return resultsArray;

            } catch (IOException e) {

                //  If there was an error, there's no point in parsing data, IF there is data that is.
                //  So we just exit the process by returning null
                return null;

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(String[][] result) {

            RadDbHelper dbHelper = RadDbHelper.getInstance(myContext.getApplicationContext());

            SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            if(dbHelper.isOnline(myContext)){

                if(result!=null){

                    for (String[] aResult : result) {

                        values.clear();

                        long unixTime = System.currentTimeMillis() / 1000L;

                        int appOrder = 0;

                        values.put(RadContract.RadsEntry.COLUMN_HEADLINE, aResult[8]);
                        values.put(RadContract.RadsEntry.COLUMN_SUB_HEADLINE, aResult[9]);
                        values.put(RadContract.RadsEntry.COLUMN_BANNER_IMAGE, aResult[3]);
                        values.put(RadContract.RadsEntry.COLUMN_CATEGORY, aResult[1]);
                        values.put(RadContract.RadsEntry.COLUMN_SERVER_ID, aResult[2]);
                        values.put(RadContract.RadsEntry.COLUMN_LOCATION, aResult[6]);
                        values.put(RadContract.RadsEntry.COLUMN_START_DATE, aResult[7]);
                        values.put(RadContract.RadsEntry.COLUMN_CREATOR, aResult[4]);
                        values.put(RadContract.RadsEntry.COLUMN_CREATOR_DP, aResult[5]);
                        values.put(RadContract.RadsEntry.COLUMN_DATE_FETCHED, unixTime);
                        values.put(RadContract.RadsEntry.COLUMN_NEW_OLD, appOrder);
                        /**
                         *
                         * */
                        sqlDB.insertWithOnConflict(RadContract.TABLE_RADS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

                    }

                    Integer maxEntriesInDb = 75;
                    dbHelper.manageMaxEntries(maxEntriesInDb);

//            Log.e("Adapter Set", "OnCreate - FetchRads");

                    Toast.makeText(myContext, "Magnet attracted new Rads", Toast.LENGTH_SHORT).show();

                }else{

                    Toast.makeText(myContext, "No New Rads for Now", Toast.LENGTH_SHORT).show();

                }

            }else{

                Toast.makeText(myContext, "Internet Connection Lost!", Toast.LENGTH_SHORT).show();

            }

            NewRadAdapter newRadAdapter     =   new NewRadAdapter();
            newRadAdapter.establishMaterialPager(myContext, mViewPager, radPagerAdapter);

            FrameLayout welcomeMsg     =   (FrameLayout) findViewById(R.id.welcome_message);
            welcomeMsg.setVisibility(View.GONE);

//            sqlDB.close();

//            Toast.makeText(myContext, "Magnet attracted new Rads", Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        displayLogged();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SwipeRefreshLayout mSwipeRefreshLayout  =   (SwipeRefreshLayout) findViewById(R.id.swipe_for_rads);

        if (mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }

        MainActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private void displayLogged() {

        Boolean loggedIn    =   settings.getBoolean(getResources().getString(R.string.sharedprefs_loginstatus), false);

        LinearLayout loginBox   =   (LinearLayout) findViewById(R.id.login_box);
        ImageView loginPic      =   (ImageView) findViewById(R.id.login_pic);
        ImageView loginmissing  =   (ImageView) findViewById(R.id.login_missing);
        TextView loginName      =   (TextView) findViewById(R.id.login_text);
        LinearLayout loginLauncher  =   (LinearLayout) findViewById(R.id.login_launcher);
        LinearLayout logoutLauncher =   (LinearLayout) findViewById(R.id.logout_trigger);

        if(loggedIn){
            // user is logged in
            try{
                String userName =   settings.getString(getResources().getString(R.string.sharedprefs_loginname), "notset");
                String userPic  =   settings.getString(getResources().getString(R.string.sharedprefs_loginpic), "notset");
                String userId   =   settings.getString(getResources().getString(R.string.sharedprefs_loginid), "notset");
                String source   =   settings.getString(getResources().getString(R.string.sharedprefs_loginsource), "notset");

                if(userName.equals("notset")){
                    FacebookSdk.sdkInitialize(myContext);
                    LoginManager.getInstance().logOut();
                }else{

                    loginmissing.setVisibility(View.GONE);
                    loginPic.setVisibility(View.VISIBLE);
                    logoutLauncher.setVisibility(View.VISIBLE);
                    loginLauncher.setVisibility(View.GONE);

                    Picasso.with(myContext).load(userPic).into(loginPic);
                    loginName.setText(userName);
                    loginBox.setBackground(getResources().getDrawable(R.drawable.drawer_logged_in));

                }


            }catch(NullPointerException e){

            }
        }else{
            //  user is not logged in
            loginmissing.setVisibility(View.VISIBLE);
            loginPic.setVisibility(View.GONE);
            loginLauncher.setVisibility(View.VISIBLE);
            logoutLauncher.setVisibility(View.GONE);

            loginName.setText("GUEST");
            loginBox.setBackgroundResource(0);
            loginBox.setBackgroundColor(getResources().getColor(R.color.black));
        }

    }

    private void setWelcomeMessage(Integer welcomeNumber){

        int number   = welcomeNumber + 1;

        TextView subWelcomeMsg      =   (TextView) findViewById(R.id.welcome_subtag_line);
        ImageView welcomeSplash     =   (ImageView) findViewById(R.id.welcome_splash);
        welcomeSplash.setBackground(null);

        switch (number){
            case 1:

                welcomeSplash.setBackground(getResources().getDrawable(R.drawable.splash_one));
                subWelcomeMsg.setText(getResources().getString(R.string.welcomesubone));

                break;
            case 2:

                welcomeSplash.setBackground(getResources().getDrawable(R.drawable.splash_two));
                subWelcomeMsg.setText(getResources().getString(R.string.welcomesubtwo));

                break;
            case 3:

                welcomeSplash.setBackground(getResources().getDrawable(R.drawable.splash_three));
                subWelcomeMsg.setText(getResources().getString(R.string.welcomesubthree));

                break;
            default:

                welcomeSplash.setBackground(getResources().getDrawable(R.drawable.splash_one));
                subWelcomeMsg.setText(getResources().getString(R.string.welcomesubone));

                break;

        }

        SharedPreferences.Editor editor = settings.edit();
        if(number > 3){
            editor.putInt(getResources().getString(R.string.sharedprefs_welcome_number), 0);
        }else{
            editor.putInt(getResources().getString(R.string.sharedprefs_welcome_number), number);
        }
        editor.apply();

    }

}
