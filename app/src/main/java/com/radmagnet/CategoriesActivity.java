package com.radmagnet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;

/**
 * Created by Nikhil on 7/6/15.
 */
public class CategoriesActivity extends Activity implements View.OnClickListener {

    Context myContext;
    RadDbHelper dbHelper;
    SharedPreferences settings;
    String oldPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories);

        myContext   =   getBaseContext();
        dbHelper    =   RadDbHelper.getInstance(getApplicationContext());
        settings    =   myContext.getSharedPreferences(RadContract.SystemConstants.PREFS_NAME, myContext.MODE_PRIVATE);
        oldPrefs    =   settings.getString(getResources().getString(R.string.sharedprefs_categoryprefs), "0");
//        oldPrefs    =   settings.getString("categoryPrefs", "0");

        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;
        RecyclerView.Adapter mAdapter;
        SQLiteDatabase sqlDB;
        sqlDB   =   RadDbHelper.getInstance(getApplicationContext()).getWritableDatabase();

        String[] categories     =   dbHelper.fetchCategories(sqlDB);
        String[] categoriesID   =   dbHelper.fetchCategoriesID(sqlDB);

        // Calling the RecyclerView
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager  =   new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new GridAdapter(categoriesID, categories, getApplicationContext(), oldPrefs);
        mRecyclerView.setAdapter(mAdapter);



        /**
         *  Back and Home actions
         * */
        ImageView backTrigger   =   (ImageView) findViewById(R.id.back_trigger);
        ImageView homeTrigger   =   (ImageView) findViewById(R.id.home);
        backTrigger.setOnClickListener(this);
        homeTrigger.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        finish();
        String categoriesAction         =   settings.getString(getResources().getString(R.string.sharedprefs_modifiedchoices), "0");
        SharedPreferences.Editor editor =   settings.edit();
        editor.putString(getResources().getString(R.string.sharedprefs_modifiedchoices), "0");
        editor.apply();
        String newPrefsString           =   dbHelper.changeCategoryChoices(categoriesAction, oldPrefs);
        try{
            if(!newPrefsString.equals("0")){
                if(newPrefsString.equals("{}")){

                    editor.putString(getResources().getString(R.string.sharedprefs_categoryprefs), "0");

                }else{

                    editor.putString(getResources().getString(R.string.sharedprefs_categoryprefs), newPrefsString);

                }

                editor.apply();
            }
        }catch (NullPointerException e){
            Log.e("newPrefsString", "No Changes Made");
        }

        switch (view.getId()){

            case R.id.back_trigger:
                Intent h = new Intent(myContext, MainActivity.class);
                h.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myContext.startActivity(h);
                break;
            case R.id.home:
                Intent i = new Intent(myContext, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myContext.startActivity(i);
                break;
        }

    }
}
