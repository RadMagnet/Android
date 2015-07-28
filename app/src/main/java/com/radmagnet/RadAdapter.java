package com.radmagnet;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.radmagnet.database.RadContract;

/**
 * Created by Nikhil on 5/21/15.
 */
public class RadAdapter extends SimpleCursorAdapter {

    private LayoutInflater mInflater;

    private List<Integer> selectedArray = new ArrayList<>();

    public RadAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.each_rad, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

//        LinearLayout typeBlock = (LinearLayout) view.findViewById(R.id.type_holder);
//        FrameLayout nonTypeBlock = (FrameLayout) view.findViewById(R.id.non_type_block);
//
//        typeBlock.setVisibility(View.GONE);
//        nonTypeBlock.setVisibility(View.VISIBLE);

        /**
         *  Identifying Views to Plug in Data
         * */
        final ImageView IMView          =   (ImageView) view.findViewById(R.id.banner_brace);
        LinearLayout postTypeTile       =   (LinearLayout) view.findViewById(R.id.post_type_tile);
        TextView postType               =   (TextView) view.findViewById(R.id.post_type);
        ImageView postTypeIcon          =   (ImageView) view.findViewById(R.id.post_type_icon);
        TextView postHeadline           =   (TextView) view.findViewById(R.id.post_headline);
        TextView postSubHeadline        =   (TextView) view.findViewById(R.id.post_sub_headline);
        TextView sourceName             =   (TextView) view.findViewById(R.id.source_name);
        TextView intentPasserId         =   (TextView) view.findViewById(R.id.intent_passer_id);
        TextView intentPasserCategory   =   (TextView) view.findViewById(R.id.intent_passer_category);
        ImageView newOld                =   (ImageView) view.findViewById(R.id.new_old);


        /**
         *  Identifying cursor data to Plug in
         * */
        String bannerImage_URL          = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_BANNER_IMAGE));
        String postTypeText             = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY));
        String postHeadlineText         = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_HEADLINE));
        String sourceNameText           = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CREATOR));
        String postId                   = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_SERVER_ID));
        String postCategory             = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY));
        String newOrOld                 = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_NEW_OLD));
        String subHeadline              = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_SUB_HEADLINE));

        Log.e("adapter-Sub", subHeadline);

        String categoryText;

        /**
         *  Plugging cursor data into Identified Views
         * */
        //  Banner Image
        Picasso.with(context).load(bannerImage_URL).into(IMView);

        switch (postTypeText) {
            case RadContract.SystemConstants.EVENTS:

        //  Category Tile Icon
                Picasso.with(context).load(R.drawable.ic_events).into(postTypeIcon);
        //  Category Tile Color
                postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.events));
        //  Category Tile Text
                Long startDateTimeStampSeconds = Long.parseLong(cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_START_DATE))) * 1000L;
                Date date = new Date(startDateTimeStampSeconds);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
                SimpleDateFormat sdfDay = new SimpleDateFormat("d");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                sdfDay.setTimeZone(TimeZone.getTimeZone("GMT+4"));
                String formattedStartDate = sdf.format(date) + getDayNumberSuffix(Integer.parseInt(sdfDay.format(date)));
                categoryText     =   "EVENTS // " + formattedStartDate;
                postType.setText(categoryText);

                break;
            case RadContract.SystemConstants.HOTSPOTS:

            //  Category Tile Icon
                Picasso.with(context).load(R.drawable.ic_hotspots).into(postTypeIcon);
            //  Category Tile Color
                postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.hotspots));
            //  Category Tile Text
                categoryText     =   "HOTSPOTS // " + cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_LOCATION));

                postType.setText(categoryText);

                break;
            case RadContract.SystemConstants.LIFE_HACKS:
            //  Category Tile Icon
                Picasso.with(context).load(R.drawable.ic_hotspots).into(postTypeIcon);
            //  Category Tile Color
                postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.lifeHacks));
            //  Category Tile Text
                categoryText     =   "LIFE HACKS";

                postType.setText(categoryText);

                break;
            case RadContract.SystemConstants.MOVIES:
            //  Category Tile Icon
                Picasso.with(context).load(R.drawable.movies).into(postTypeIcon);
            //  Category Tile Color
                postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.movies));
            //  Category Tile Text
                categoryText     =   "MOVIES";

                postType.setText(categoryText);

                break;
            case RadContract.SystemConstants.NEWS:

            //  Category Tile Icon
                Picasso.with(context).load(R.drawable.ic_news).into(postTypeIcon);
            //  Category Tile Color
                postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.news));
            //  Category Tile Text
                categoryText     =   "NEWS";

                postType.setText(categoryText);

                break;
            case RadContract.SystemConstants.DEALS:

            //  Category Tile Icon
                Picasso.with(context).load(R.drawable.movies).into(postTypeIcon);
            //  Category Tile Color
                postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.movies));
            //  Category Tile Text
                categoryText     =   "DEALS";

                postType.setText(categoryText);

                break;
            case RadContract.SystemConstants.BLOGS:

            //  Category Tile Icon
                Picasso.with(context).load(R.drawable.ic_blogs).into(postTypeIcon);
            //  Category Tile Color
                postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.blogs));
            //  Category Tile Text
                categoryText     =   "BLOGS";

                postType.setText(categoryText);

                break;
            default:    //  Broadcasts
            //  Category Tile Icon
                Picasso.with(context).load(R.drawable.ic_broadcast).into(postTypeIcon);
            //  Category Tile Color
                postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.broadcasts));
            //  Category Tile Text
                categoryText     =   "BROADCAST";

                postType.setText(categoryText);

                break;
        }

        //  Intent Data
        intentPasserId.setText(postId);
        intentPasserCategory.setText(postCategory);


        //  New or Old
        if (newOrOld.equals("1")) {
            newOld.setVisibility(View.INVISIBLE);
        } else {
            newOld.setVisibility(View.VISIBLE);
        }

        //  Headline
        postHeadline.setText(postHeadlineText);

        //  Sub Headline
        postSubHeadline.setText(subHeadline);

        //  Source Name
        sourceName.setText(sourceNameText);


        cursor.moveToNext();


    }


    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }


}
