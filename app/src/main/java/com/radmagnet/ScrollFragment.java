package com.radmagnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Nikhil on 7/1/15.
 */
public class ScrollFragment extends Fragment {


    public static ScrollFragment newInstance() {
        return new ScrollFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.highlights_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         *  Identifying all the recycler views
         * */
        RecyclerView highlightsRecycler          =   (RecyclerView) view.findViewById(R.id.highlightsRecycler);

        /** declaring layout managers
         * */
        RecyclerView.LayoutManager linearLayout =   new LinearLayoutManager(getActivity());

        /** assigning layout managers to recycler views
         * */
        highlightsRecycler.setLayoutManager(linearLayout);
        highlightsRecycler.setHasFixedSize(true);


        /** Important Constants
         * */
        final Context myContext                       =   getActivity().getApplicationContext();
        SharedPreferences settings                    =   myContext.getSharedPreferences(RadContract.SystemConstants.PREFS_NAME, myContext.MODE_PRIVATE);
        String userPrefs                              =   settings.getString(getResources().getString(R.string.sharedprefs_categoryprefs), "0");
        Boolean loginPrefs                            =   settings.getBoolean(getResources().getString(R.string.sharedprefs_loginstatus), false);
        NewRadAdapter radAdapter                      =   new NewRadAdapter();


        RadDbHelper radDbHelper         =   RadDbHelper.getInstance(myContext);
        SQLiteDatabase fetchSqlDB       =   radDbHelper.getWritableDatabase();

        String[] From                   = new String[]{
                RadContract.RadsEntry._ID,
                RadContract.RadsEntry.COLUMN_HEADLINE,
                RadContract.RadsEntry.COLUMN_SUB_HEADLINE,
                RadContract.RadsEntry.COLUMN_BANNER_IMAGE,
                RadContract.RadsEntry.COLUMN_CATEGORY,
                RadContract.RadsEntry.COLUMN_LOCATION,
                RadContract.RadsEntry.COLUMN_START_DATE,
                RadContract.RadsEntry.COLUMN_CREATOR,
                RadContract.RadsEntry.COLUMN_CREATOR_DP,
                RadContract.RadsEntry.COLUMN_DATE_FETCHED,
                RadContract.RadsEntry.COLUMN_SERVER_ID,
                RadContract.RadsEntry.COLUMN_NEW_OLD
        };

        /** Fetching Notifications
         * */
        MatrixCursor notificationsCursor   =   new MatrixCursor(From);
        if(userPrefs.equals("0")){
            notificationsCursor.addRow(new Object[] { "0","Personalize", "RadMagnet gets a lot more handy when you choose what you wish to explore", null, "notif", null, null, null, null, null, null, "category_missing" });
        }
        if(!loginPrefs){
            notificationsCursor.addRow(new Object[] { "1","Synchronize", "Save your preferences and the Rads you love, across multiple devices, in one touch", null, "notif", null, null, null, null, null, null, "login_missing" });
        }
        Cursor cursorOne                    =   radDbHelper.fetchLove();
        if(cursorOne.getCount() == 0){
            notificationsCursor.addRow(new Object[] { "2","Read, Love, Share", "Love the Rads you read? Save them so that you don't lose them", null, "notif", null, null, null, null, null, null, "love_missing" });
        }

        /** Fetching Suggested Categories
         * */
        Cursor cursorRemainingCategories    =   radDbHelper.fetchRemainingCategories(userPrefs, From);

        /** Fetching Featured Rads
         * */
//        Will introduce later
//        Cursor fetchFeaturedRads            =   radDbHelper.fetchFeaturedRads(fetchSqlDB, userPrefs, From);
        /** Fetching Tips
         * */
//        Will introduce later

        /** Converting cursors to lists
         * */
        List<String[]>  notificationList    =  this.covertCursorToListStringArray(notificationsCursor, From);
        List<String[]>  categoriesList      =  this.covertCursorToListStringArray(cursorRemainingCategories, From);

        notificationList.addAll(categoriesList);

        /** Converting list to map
         * */
        LinkedHashMap<Integer, String[]> linkedHashMap = new LinkedHashMap<>();
        for (int i=0;i < notificationList.size();i++)
        {
            linkedHashMap.put(i, notificationList.get(i));
        }



        /**
         *  Invoking Adapters for all recycler views
         * */
        RecyclerView.Adapter uRadAdapter            =   new RecyclerViewMaterialAdapter(new UltraRadAdapter(linkedHashMap, myContext));


        /** Setting Adapter to relevant recyclers
         * */
        highlightsRecycler.setAdapter(uRadAdapter);

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), highlightsRecycler, null);

    }

    public List<String[]> covertCursorToListStringArray(Cursor cursor, String[] From){

        List<String[]> returnList = new ArrayList<>();

        String[] data;
        if (cursor != null) {
            while(cursor.moveToNext()) {
                data = new String[12];

                data[0] =   cursor.getString(cursor.getColumnIndex(From[0]));
                data[1] =   cursor.getString(cursor.getColumnIndex(From[1]));
                data[2] =   cursor.getString(cursor.getColumnIndex(From[2]));
                data[3] =   cursor.getString(cursor.getColumnIndex(From[3]));
                data[4] =   cursor.getString(cursor.getColumnIndex(From[4]));
                data[5] =   cursor.getString(cursor.getColumnIndex(From[5]));
                data[6] =   cursor.getString(cursor.getColumnIndex(From[6]));
                data[7] =   cursor.getString(cursor.getColumnIndex(From[7]));
                data[8] =   cursor.getString(cursor.getColumnIndex(From[8]));
                data[9] =   cursor.getString(cursor.getColumnIndex(From[9]));
                data[10] =   cursor.getString(cursor.getColumnIndex(From[10]));
                data[11] =   cursor.getString(cursor.getColumnIndex(From[11]));

                returnList.add(data);
            }
            cursor.close();
        }

        return returnList;

    }


}
