package com.radmagnet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.radmagnet.database.RadContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Nikhil on 7/12/15.
 */
public class RadLoveAdapter extends SimpleCursorAdapter {

    private LayoutInflater mInflater;

    public RadLoveAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.each_love, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        /**
         *  Identifying fields to plug in data
         * */
        TextView loveHeadline                 =   (TextView) view.findViewById(R.id.love_headline);
        LinearLayout categoryColor            =   (LinearLayout) view.findViewById(R.id.category_color);
        ImageView categoryIcon                =   (ImageView) view.findViewById(R.id.category_icon);
        final TextView radId                  =   (TextView) view.findViewById(R.id.intent_passer_id);
        final TextView radCategory            =   (TextView) view.findViewById(R.id.intent_passer_category);

        if (cursor.getCount() == 0) {
            loveHeadline.setText("There is no love in your life :(");
            categoryColor.setBackgroundColor(context.getResources().getColor(R.color.black));
            Picasso.with(context).load(R.drawable.rad_love).into(categoryIcon);
        } else {

            /**
             *  Fetching Data from Cursor
             * */
            String postHeadlineText         =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_HEADLINE));
            String postTypeText             =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY));
            final String postId                   =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_SERVER_ID));
            final String postCategory             =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY));

            loveHeadline.setText(postHeadlineText);
            radId.setText(postId);
            radCategory.setText(postCategory);

            switch (postTypeText) {
                case RadContract.SystemConstants.EVENTS:
                    categoryColor.setBackgroundColor(context.getResources().getColor(R.color.events));
                    Picasso.with(context).load(R.drawable.ic_events).into(categoryIcon);

                    break;
                case RadContract.SystemConstants.BLOGS:
                    categoryColor.setBackgroundColor(context.getResources().getColor(R.color.blogs));
                    Picasso.with(context).load(R.drawable.ic_blogs).into(categoryIcon);

                    break;
                case RadContract.SystemConstants.HOTSPOTS:
                    categoryColor.setBackgroundColor(context.getResources().getColor(R.color.hotspots));
                    Picasso.with(context).load(R.drawable.ic_hotspots).into(categoryIcon);

                    break;
                case RadContract.SystemConstants.LIFE_HACKS:
                    categoryColor.setBackgroundColor(context.getResources().getColor(R.color.lifeHacks));
                    Picasso.with(context).load(R.drawable.ic_lifehacks).into(categoryIcon);

                    break;
                case RadContract.SystemConstants.MOVIES:
                    categoryColor.setBackgroundColor(context.getResources().getColor(R.color.movies));
                    Picasso.with(context).load(R.drawable.ic_movies).into(categoryIcon);

                    break;
                case RadContract.SystemConstants.NEWS:
                    categoryColor.setBackgroundColor(context.getResources().getColor(R.color.news));
                    Picasso.with(context).load(R.drawable.ic_news).into(categoryIcon);

                    break;
                case RadContract.SystemConstants.DEALS:
                    categoryColor.setBackgroundColor(context.getResources().getColor(R.color.deals));
                    Picasso.with(context).load(R.drawable.ic_deals).into(categoryIcon);

                    break;
                default:
                    categoryColor.setBackgroundColor(context.getResources().getColor(R.color.broadcasts));
                    Picasso.with(context).load(R.drawable.ic_broadcast).into(categoryIcon);

                    break;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle extras = new Bundle();
                    extras.putString("radId", postId);
                    extras.putString("radCategory", postCategory);

                    Intent i = new Intent(context, DetailsActivity.class);
                    i.putExtras(extras);
                    context.startActivity(i);

                }
            });

        }

        cursor.moveToNext();

    }


}
