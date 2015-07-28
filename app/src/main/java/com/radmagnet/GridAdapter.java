package com.radmagnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edwin on 28/02/2015.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder>{

    List<EachCategory> mItems;
    public Map<String, Integer> choiceMap = new HashMap<>();
    String choicesPrefs;
    String oldPrefs;
    Context context;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Map<String, Integer> oldMap;

    public GridAdapter(String[] categoriesID, String[] categories, Context myContext, String moldPrefs) {
        super();

        context     =   myContext;
        settings = myContext.getSharedPreferences(RadContract.SystemConstants.PREFS_NAME, myContext.MODE_PRIVATE);
        choicesPrefs = settings.getString(myContext.getResources().getString(R.string.sharedprefs_modifiedchoices), "0");
        editor = settings.edit();
        oldPrefs    =   moldPrefs;

        mItems = new ArrayList<>();

        EachCategory category       =   new EachCategory();
        category.setName("City News");
        category.setImage(myContext.getResources().getDrawable(R.drawable.blogs));
        category.setColor(myContext.getResources().getColor(R.color.blogs));
        category.setType("news");
        category.setSubtext(myContext.getResources().getString(R.string.city_news_subtext));
        mItems.add(category);

        category       =   new EachCategory();
        category.setName("Events");
        category.setImage(myContext.getResources().getDrawable(R.drawable.events));
        category.setColor(myContext.getResources().getColor(R.color.events));
        category.setType("events");
        category.setSubtext(myContext.getResources().getString(R.string.events_subtext));
        mItems.add(category);

        category       =   new EachCategory();
        category.setName("Live Better");
        category.setImage(myContext.getResources().getDrawable(R.drawable.life_hacks));
        category.setColor(myContext.getResources().getColor(R.color.lifeHacks));
        category.setType("life_hacks");
        category.setSubtext(myContext.getResources().getString(R.string.live_better_subtext));
        mItems.add(category);

        category       =   new EachCategory();
        category.setName("Entertainment");
        category.setImage(myContext.getResources().getDrawable(R.drawable.entertainment));
        category.setColor(myContext.getResources().getColor(R.color.movies));
        category.setType("movies");
        category.setSubtext(myContext.getResources().getString(R.string.entertainment_subtext));
        mItems.add(category);

        category       =   new EachCategory();
        category.setName("Hotspots");
        category.setImage(myContext.getResources().getDrawable(R.drawable.grubs_pubs));
        category.setColor(myContext.getResources().getColor(R.color.hotspots));
        category.setType("hotspots");
        category.setSubtext(myContext.getResources().getString(R.string.grubs_pubs_subtext));
        mItems.add(category);

        category       =   new EachCategory();
        category.setName("Jobs & Internships");
        category.setImage(myContext.getResources().getDrawable(R.drawable.jobs_internships));
        category.setColor(myContext.getResources().getColor(R.color.jobs_internships));
        category.setType("jobs_internships");
        category.setSubtext(myContext.getResources().getString(R.string.jobs_internships_subtext));
        mItems.add(category);

        category       =   new EachCategory();
        category.setName("Citizen's Voice");
        category.setImage(myContext.getResources().getDrawable(R.drawable.blogs));
        category.setColor(myContext.getResources().getColor(R.color.blogs));
        category.setType("blogs");
        category.setSubtext(myContext.getResources().getString(R.string.citizens_voice_subtext));
        mItems.add(category);

        category       =   new EachCategory();
        category.setName("Deals");
        category.setImage(myContext.getResources().getDrawable(R.drawable.deals));
        category.setColor(myContext.getResources().getColor(R.color.deals));
        category.setType("deals");
        category.setSubtext(myContext.getResources().getString(R.string.deals_subtext));
        mItems.add(category);

        /**
         *  Preparing Category Prefs Map
         * */
        if(!oldPrefs.equals("0")){

            oldPrefs    =   oldPrefs.substring(1, oldPrefs.length()-1);
            RadDbHelper dbHelper    =   RadDbHelper.getInstance(context.getApplicationContext());
            oldMap          =   dbHelper.makeProperMap(oldPrefs);

        }


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.each_category, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else{
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        try{
            EachCategory nature = mItems.get(i);
            viewHolder.categoryName.setText(nature.getName());
            viewHolder.categoryImage.setBackground(nature.getImage());
            viewHolder.categoryType.setText(nature.getType());
            viewHolder.categorySubtitle.setText(nature.getSubtext());
            viewHolder.categoryBlanket.setBackgroundColor(nature.getColor());
            if(!oldPrefs.equals("0")){
                if(oldMap.containsKey(nature.getType())){
                    viewHolder.confirmation.setVisibility(View.VISIBLE);
                    viewHolder.titleBox.setVisibility(View.GONE);
                }else{
                    viewHolder.confirmation.setVisibility(View.GONE);
                    viewHolder.titleBox.setVisibility(View.VISIBLE);
                }
            }else{
                viewHolder.confirmation.setVisibility(View.GONE);
                viewHolder.titleBox.setVisibility(View.VISIBLE);
            }

        }catch (NullPointerException e){

        }

    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView categoryImage;
        public TextView categoryName;
        public TextView categorySubtitle;
        public TextView categoryType;
        public FrameLayout confirmation;
        public LinearLayout titleBox;
        public View categoryBlanket;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryImage   =   (ImageView) itemView.findViewById(R.id.category_image);
            categoryName    =   (TextView) itemView.findViewById(R.id.category_title);
            categoryType    =   (TextView) itemView.findViewById(R.id.category_type);
            confirmation    =   (FrameLayout) itemView.findViewById(R.id.confirmation);
            categorySubtitle =   (TextView) itemView.findViewById(R.id.category_subtitle);
            titleBox        =   (LinearLayout) itemView.findViewById(R.id.title_box);
            categoryBlanket     =   itemView.findViewById(R.id.category_blanket);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(final View view) {

            if(view.findViewById(R.id.confirmation).getVisibility() == View.VISIBLE){

                String pickedValue =   String.valueOf(categoryType.getText());

                choicesPrefs += pickedValue + "=-1,";

                confirmation.setVisibility(View.GONE);
                titleBox.setVisibility(View.VISIBLE);

            } else {

                String pickedValue =   String.valueOf(categoryType.getText());

                choicesPrefs += pickedValue + "=1,";

                confirmation.setVisibility(View.VISIBLE);
                titleBox.setVisibility(View.GONE);

            }

            String firstChar    = String.valueOf(choicesPrefs.charAt(0));

            if((choicesPrefs.length() > 2) & firstChar.equals("0")){
                choicesPrefs    =   choicesPrefs.substring(1);
            }

            editor.putString(context.getResources().getString(R.string.sharedprefs_modifiedchoices), choicesPrefs);
            editor.apply();

        }
    }
}