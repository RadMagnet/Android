package com.radmagnet;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;

/**
 * Created by Nikhil on 6/6/15.
 */
public class RadListView extends ListActivity {

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        String[] From  =   new String[]{
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
                RadContract.RadsEntry.COLUMN_NEW_OLD
        };

        int[] To =   new int[]{
                R.id.post_type,
                R.id.post_headline,
                R.id.source_name,
                R.id.banner_brace
        };

        String groupBy  =   RadContract.RadsEntry.COLUMN_HEADLINE;

        String orderBy  =   RadContract.RadsEntry.COLUMN_DATE_FETCHED + " DESC";

        SQLiteDatabase sqlDB = RadDbHelper.getInstance(getApplicationContext()).getWritableDatabase();

        Cursor cursor           =   sqlDB.query(RadContract.TABLE_RADS,
                From
                , null, null, groupBy, null, orderBy, null);

        final RadAdapter newRadAdapter    =   new RadAdapter(this, R.layout.each_rad, cursor, From, To, 0);

        setListAdapter(newRadAdapter);

    }



}
