package com.radmagnet;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;

/**
 * Created by Nikhil on 6/2/15.
 */
public class DetailsActivity extends Activity {

    private String shareData;
    public String resourceId;
    public String resourceCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_holder);

        final Context myContext     =   getApplicationContext();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            resourceId = extras.getString("radId");
            resourceCategory = extras.getString("radCategory");
        }



        final ImageView backButton                  =   (ImageView) findViewById(R.id.back_trigger);
        final ImageView shareTrigger                =   (ImageView) findViewById(R.id.share_trigger);
        final ImageView loveTrigger                 =   (ImageView) findViewById(R.id.love_trigger);
        final Animation animation                   =   AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bounce_view);

        shareTrigger.setColorFilter(myContext.getResources().getColor(R.color.allTheRads));

        fetchEntryFromDb(myContext, resourceId, resourceCategory, loveTrigger);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                DetailsActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });

        shareTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareContent     =  getResources().getString(R.string.share_text)+ "\n" + shareData;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

//        openSource.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LinearLayout openSourceIcon =   (LinearLayout) findViewById(R.id.view_original_source);
//                openSource.startAnimation(animation);
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(originalUrl));
//                startActivity(browserIntent);
//            }
//        });

        final RadDbHelper radDbHelper       =   RadDbHelper.getInstance(getApplicationContext());
        final SQLiteDatabase sqlDB          =   radDbHelper.getWritableDatabase();

        loveTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.startAnimation(animation);

                Integer insertSuccess   =   radDbHelper.addAsLove(sqlDB, resourceCategory, resourceId);

                switch (insertSuccess){

                    case 1:
                        v.startAnimation(animation);
                        Picasso.with(myContext).load(R.drawable.love).into(loveTrigger);
                        break;
                    case 3:
                        v.startAnimation(animation);
                        Picasso.with(myContext).load(R.drawable.rad_love).into(loveTrigger);
                        break;
                    default:
                        Toast.makeText(myContext, "Your Magnet is Weak! " + insertSuccess.toString(), Toast.LENGTH_SHORT).show();
                        break;

                }

            }

        });




    }

    public void fetchEntryFromDb(Context context, String id, String category, ImageView loveTrigger){

        SQLiteDatabase sqlDB    =   RadDbHelper.getInstance(getApplicationContext()).getWritableDatabase();

        //  Fetch From appropriate table - Rads or Love - Check if this rad is a Loved Rad

        Cursor cursor;
        RadDbHelper radDbHelper     =   RadDbHelper.getInstance(getApplicationContext());


        if(radDbHelper.checkAsLove(sqlDB, resourceCategory, resourceId) == 1){
            Picasso.with(context).load(R.drawable.love).into(loveTrigger);

            cursor  =   radDbHelper.fetchFromLoveDB(sqlDB, category, id);

        }else{
            cursor  =   radDbHelper.fetchFromNormalDB(sqlDB, category, id);
            loveTrigger.setColorFilter(context.getResources().getColor(R.color.allTheRads));
        }

        if(cursor.getCount() == 0){

            Toast.makeText(this, "Your Magnet is Weak!", Toast.LENGTH_SHORT).show();

        }else{

            cursor.moveToFirst();

            String url = "http://www.radmagnet.com/GET/details/" + cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY)) + "/" + cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_SERVER_ID));
            String originalUrl = "http://www.radmagnet.com/GET/details/" + cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY)) + "/" + cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_SERVER_ID)) + "/external";
            shareData   =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_HEADLINE)) + "\n http://www.radmagnet.com/GET/share/" + cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY)) + "/" + cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_SERVER_ID));

            final WebView detailsView     =   (WebView) findViewById(R.id.my_rad);
            detailsView.getSettings().setJavaScriptEnabled(true);
            detailsView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    //hide loading image
//                    findViewById(R.id.loading_notice).setVisibility(View.GONE);
                    //show webview
                    detailsView.setVisibility(View.VISIBLE);
                }
            });

            detailsView.loadUrl(url);

            fillOtherFields(context, cursor);

        }

        radDbHelper.markAsOld(sqlDB, id);

    }

    public void fillOtherFields(Context context, Cursor cursor){

        final ImageView IMView                  =   (ImageView) findViewById(R.id.banner_brace);
        TextView postHeadline                   =   (TextView) findViewById(R.id.post_headline);
        TextView sourceName                     =   (TextView) findViewById(R.id.source_name);


        String  bannerImage_URL     =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_BANNER_IMAGE));
        Picasso.with(context).load(bannerImage_URL).into(IMView);


        String postTypeText         =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY));


        String postHeadlineText     =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_HEADLINE));
        postHeadline.setText(postHeadlineText);

        String sourceNameText       =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CREATOR));
        sourceName.setText(sourceNameText);

        //  Modifications to tile based on TYPE of post
        switch (postTypeText){
            case RadContract.SystemConstants.HOTSPOTS:

                sourceName.setText(cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_LOCATION)));

                break;
            default:    //  Broadcasts

                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        DetailsActivity.this.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    
}
