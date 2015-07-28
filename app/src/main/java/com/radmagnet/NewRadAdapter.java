package com.radmagnet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Nikhil on 7/5/15.
 */
public class NewRadAdapter {

    public RecyclerViewMaterialAdapter getRecyclerViewMaterialAdapter(Cursor cursor, final Context context) {

        return new RecyclerViewMaterialAdapter(new CursorRecyclerAdapter(cursor) {
            View view;
            final Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce_view);


            @Override
            public void onBindViewHolderCursor(RecyclerView.ViewHolder holder, Cursor cursor) {

                final Picasso mPicasso = Picasso.with(context);

                /**
                 *  Identifying Views to Plug in Data
                 * */
                final ImageView IMView = (ImageView) view.findViewById(R.id.banner_brace);
                LinearLayout postTypeTile = (LinearLayout) view.findViewById(R.id.post_type_tile);
                TextView postType = (TextView) view.findViewById(R.id.post_type);
                ImageView postTypeIcon = (ImageView) view.findViewById(R.id.post_type_icon);
                final TextView postHeadline = (TextView) view.findViewById(R.id.post_headline);
                final TextView postSubHeadline = (TextView) view.findViewById(R.id.post_sub_headline);
                TextView sourceName = (TextView) view.findViewById(R.id.source_name);
                final TextView intentPasserId = (TextView) view.findViewById(R.id.intent_passer_id);
                final TextView intentPasserCategory = (TextView) view.findViewById(R.id.intent_passer_category);
                TextView newOld = (TextView) view.findViewById(R.id.new_old);
                final ImageView shareTrigger = (ImageView) view.findViewById(R.id.share_trigger);
                final ImageView loveTrigger = (ImageView) view.findViewById(R.id.love_trigger);

                /**
                 * resetting recyclerview element
                 * */
                postHeadline.setText("");
                postSubHeadline.setText("");
                sourceName.setText("");
                IMView.setBackground(null);

                /**
                 *  Identifying cursor data to Plug in
                 * */
                String bannerImage_URL = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_BANNER_IMAGE));
                String postTypeText = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY));
                String postHeadlineText = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_HEADLINE));
                String sourceNameText = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CREATOR));
                String postId = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_SERVER_ID));
                String postCategory = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY));
                String newOrOld = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_NEW_OLD));
                final String subHeadline = cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_SUB_HEADLINE));

                String categoryText;

                /**
                 *  Plugging cursor data into Identified Views
                 * */

                //  New or Old
                if (newOrOld.equals("1")) {
                    newOld.setVisibility(View.INVISIBLE);
                } else {
                    newOld.setVisibility(View.VISIBLE);
                }


                //  Banner Image
                mPicasso.load(bannerImage_URL).error(R.drawable.events).into(IMView);

                switch (postTypeText) {
                    case RadContract.SystemConstants.EVENTS:

                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_events).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.events));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.events));
                        //  Category Tile Text

                        String formattedStartDate = getNormalDate(cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_START_DATE)));

                        categoryText = "EVENTS // " + formattedStartDate;
                        postType.setText(categoryText);

                        break;
                    case RadContract.SystemConstants.HOTSPOTS:

                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_hotspots).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.hotspots));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.hotspots));
                        //  Category Tile Text
                        categoryText = "HOTSPOTS // " + cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_LOCATION));

                        postType.setText(categoryText);

                        break;
                    case RadContract.SystemConstants.LIFE_HACKS:
                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_lifehacks).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.lifeHacks));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.lifeHacks));
                        //  Category Tile Text
                        categoryText = "LIFE HACKS";

                        postType.setText(categoryText);

                        break;
                    case RadContract.SystemConstants.MOVIES:
                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_movies).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.movies));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.movies));
                        //  Category Tile Text
                        categoryText = "MOVIES";

                        postType.setText(categoryText);

                        break;
                    case RadContract.SystemConstants.NEWS:

                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_news).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.news));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.news));
                        //  Category Tile Text
                        categoryText = "NEWS";

                        postType.setText(categoryText);

                        break;
                    case RadContract.SystemConstants.DEALS:

                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_deals).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.deals));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.deals));
                        //  Category Tile Text
                        categoryText = "DEALS";

                        postType.setText(categoryText);

                        break;
                    case RadContract.SystemConstants.BLOGS:

                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_blogs).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.blogs));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.blogs));
                        //  Category Tile Text
                        categoryText = "BLOGS";

                        postType.setText(categoryText);

                        break;
                    case RadContract.SystemConstants.JOBS:

                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_jobs_internships).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.jobs_internships));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.jobs_internships));

                        String formattedExpiryDate  =   getNormalDate(cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_START_DATE)));
                        String job_internship       =   cursor.getString(cursor.getColumnIndex(RadContract.RadsEntry.COLUMN_LOCATION));

                        //  Category Tile Text
                        categoryText = job_internship + "// " + formattedExpiryDate;

                        postType.setText(categoryText);

                        break;
                    default:    //  Broadcasts
                        //  Category Tile Icon
                        mPicasso.load(R.drawable.ic_broadcast).memoryPolicy(MemoryPolicy.NO_STORE).into(postTypeIcon);
                        //  Category Tile Color
                        postTypeTile.setBackgroundColor(context.getResources().getColor(R.color.broadcasts));
                        //  New Strip Color
                        newOld.setBackgroundColor(context.getResources().getColor(R.color.broadcasts));
                        //  Category Tile Text
                        categoryText = "BROADCAST";

                        postType.setText(categoryText);

                        break;
                }

                //  Intent Data
                intentPasserId.setText(postId);
                intentPasserCategory.setText(postCategory);


                //  Headline
                postHeadline.setText(postHeadlineText);

                //  Sub Headline
                postSubHeadline.post(new Runnable() {
                    @Override
                    public void run() {

                        try{
                            //  Sub Headline
                            if (postHeadline.getLineCount() == 1) {

                                String splitString = WordUtils.wrap(subHeadline, 150);

                                String[] splitStrings = splitString.split("\\r?\\n");

                                postSubHeadline.setText(splitStrings[0] + "...");

                            } else if (postHeadline.getLineCount() == 2) {

                                String splitString = WordUtils.wrap(subHeadline, 90);

                                String[] splitStrings = splitString.split("\\r?\\n");

                                postSubHeadline.setText(splitStrings[0] + "...");

                            } else {

                                String splitString = WordUtils.wrap(subHeadline, 50);

                                String[] splitStrings = splitString.split("\\r?\\n");

                                postSubHeadline.setText(splitStrings[0] + "...");

                            }

                        }catch (NullPointerException e){

                        }



                    }
                });


                //  Source Name
                sourceName.setText(sourceNameText);


                /**
                 *  Check variables
                 * */
                final RadDbHelper dbHelper = RadDbHelper.getInstance(context.getApplicationContext());
                final SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
                final String radId = intentPasserId.getText().toString();
                final String radCategory = intentPasserCategory.getText().toString();

                /**
                 *      Checking as Love
                 * */
                if (dbHelper.checkAsLove(sqlDB, radCategory, radId) == 1) {
                    mPicasso.load(R.drawable.love).into(loveTrigger);
                } else {
                    mPicasso.load(R.drawable.rad_love).into(loveTrigger);
                }

                /**
                 *  Setup OnClick listener for Share, Like and Details
                 * */

//                Share

                shareTrigger.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareData = "http://www.radmagnet.com/GET/share/" + radCategory + "/" + radId;
                        String shareContent = context.getResources().getString(R.string.share_text) + "\n" + shareData;
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareContent);
                        context.startActivity(Intent.createChooser(sharingIntent, "Share this Rad via"));
                    }
                });

//                Love

                loveTrigger.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Integer insertSuccess = dbHelper.addAsLove(sqlDB, radCategory, radId);

                        switch (insertSuccess) {

                            case 1:
                                view.startAnimation(animation);
                                mPicasso.load(R.drawable.love).into(loveTrigger);
                                break;
                            case 3:
                                view.startAnimation(animation);
                                mPicasso.load(R.drawable.rad_love).into(loveTrigger);
                                break;
                            default:
                                Toast.makeText(context, "Your Magnet is Weak! " + insertSuccess.toString(), Toast.LENGTH_SHORT).show();
                                break;

                        }
                    }
                });

//                Details

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bundle extras = new Bundle();
                        extras.putString("radId", radId);
                        extras.putString("radCategory", radCategory);

                        view.findViewById(R.id.new_old).setVisibility(View.INVISIBLE);

                        Intent i = new Intent(context, DetailsActivity.class);
                        i.putExtras(extras);
                        context.startActivity(i);

                    }
                });

            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.each_rad, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }


        });

    }

    private String getNormalDate(String cursorDate){

        Long startDateTimeStampSeconds = Long.parseLong(cursorDate) * 1000L;

        Date date = new Date(startDateTimeStampSeconds);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
        SimpleDateFormat sdfDay = new SimpleDateFormat("d");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+4"));
        sdfDay.setTimeZone(TimeZone.getTimeZone("GMT+4"));

        return sdf.format(date) + getDayNumberSuffix(Integer.parseInt(sdfDay.format(date)));

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

    public void establishMaterialPager(Context myContext, MaterialViewPager mViewPager, FragmentStatePagerAdapter radPagerAdapter) {

        Typeface radPagerTitle = Typeface.createFromAsset(myContext.getAssets(), "fonts/AlegreyaSans-BoldItalic.otf");
        mViewPager.getViewPager().setAdapter(radPagerAdapter);
        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());
        mViewPager.getPagerTitleStrip().setTypeface(radPagerTitle, Typeface.ITALIC);
        mViewPager.getToolbar().setTitleTextColor(myContext.getResources().getColor(R.color.allTheRads));


    }
}
