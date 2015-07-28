package com.radmagnet;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;

/**
 * Created by Nikhil on 7/12/15.
 */
public class LoveActivity extends Activity implements View.OnClickListener {
    Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.love_holder);

        myContext     =   getApplicationContext();

        /**
         *  Fetching Loved Rads
         * */
        RadDbHelper dbHelper        =   RadDbHelper.getInstance(getApplicationContext());

        Cursor allLoves             =   dbHelper.fetchLove();

        ListView loveFeeds   =   (ListView) findViewById(R.id.love_feeds);

        String[] From   =   new String[]{};
        int[] To        =   new int[]{};

        RadLoveAdapter radLoveAdapter   =   new RadLoveAdapter(this, R.layout.each_love, allLoves, From, To, 0);

        loveFeeds.setAdapter(radLoveAdapter);

        ImageView backTrigger   =   (ImageView) findViewById(R.id.back_trigger);
        backTrigger.setOnClickListener(this);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        LoveActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back_trigger:
                onBackPressed();
                break;
        }
    }
}
