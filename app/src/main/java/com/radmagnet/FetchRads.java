package com.radmagnet;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Nikhil on 7/3/15.
 */
public class FetchRads extends AsyncTask<String, Void, String[][]> {
    String userNumber;
    String userSource;
    Context myContext;
    RecyclerViewMaterialAdapter radPagerAdapter;
    public RecyclerView mViewPager;
    String requestString;
    SwipeRefreshLayout swipeToRefresh;
    String selectedCategory;
    FragmentStatePagerAdapter matPagerAdapter;
    MaterialViewPager matViewPager;
    Integer currentTab;
    String userPrefs;


    @Override
    protected String[][] doInBackground(String... params) {
        //  Connecting to the RadMagnet API to receive JSON data
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //  String to contain raw JSON response from RadMagnet API
        String radJSONstr;
        SharedPreferences settings;
        settings        =   myContext.getSharedPreferences(RadContract.SystemConstants.PREFS_NAME, myContext.MODE_PRIVATE);



        //  Try Code - Catch Error - Execute Finally regardless
        try {

            userPrefs       =   settings.getString(myContext.getResources().getString(R.string.sharedprefs_categoryprefs), "0");
            String standardRadRequestURL;
            String userChoices = "";

            try{
                if(!userPrefs.equals("0")){
                    userPrefs   =   userPrefs.substring(1, userPrefs.length() - 1);
                    if(userPrefs.contains(",")){
                        userChoices  =   userPrefs.replaceAll(",", "-").replaceAll("\\s", "");
                    }
                }else{
                    userChoices  =   userPrefs;
                }
            }catch(NullPointerException e){
                Log.e("UserChoices", "is null is adapter");
            }



            switch (params[0]) {
                case "0":

                    standardRadRequestURL = "http://www.radmagnet.com/GET/highlights/0/v4/" + userNumber + "/" + userSource + "/" + userChoices;

                    break;
                default:

                    standardRadRequestURL = "http://www.radmagnet.com/GET/highlights/" + requestString + "/v4/" + userNumber + "/" + userSource + "/" + userChoices;
//                    standardRadRequestURL = "http://www.radmagnet.com/GET/highlights/" + params[0] + "/v5/" + userNumber + "/" + userSource;

                    break;
            }

            Log.e("AdapterRequest", standardRadRequestURL);

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
            editor.putString(myContext.getResources().getString(R.string.sharedprefs_APIrequestString), userTimeString);
            editor.apply();


            //  Isolating the radsArray
            JSONArray radsArray = radInput.getJSONArray("data");


            //  Our Results Array
            String[][] resultsArray = new String[radsArray.length()][10];


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
        } catch(NullPointerException e){

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

                RadDbHelper manageDb = RadDbHelper.getInstance(myContext.getApplicationContext());
                Integer maxEntriesInDb = 75;
                manageDb.manageMaxEntries(maxEntriesInDb);


                Cursor cursor   =   manageDb.fetchNormal(selectedCategory);

                NewRadAdapter radAdapter    =   new NewRadAdapter();
                radPagerAdapter     =   radAdapter.getRecyclerViewMaterialAdapter(cursor, myContext);
                Log.e("Adapter Set", "FetchRads - After Fetching");
                mViewPager.setAdapter(radPagerAdapter);

                Toast.makeText(myContext, "Magnet attracted new Rads", Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(myContext, "No New Rads for Now", Toast.LENGTH_SHORT).show();

            }


        }else{

            Toast.makeText(myContext, "Internet Connection Lost!", Toast.LENGTH_SHORT).show();

        }

        if (swipeToRefresh.isRefreshing()) {
            swipeToRefresh.setRefreshing(false);
            swipeToRefresh.destroyDrawingCache();
            swipeToRefresh.clearAnimation();
        }



    }


}