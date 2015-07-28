package com.radmagnet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.radmagnet.database.RadContract;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nikhil on 7/18/15.
 */
public class UltraRadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LinkedHashMap<Integer, String[]> hashMap;
    static final int TYPE_NOTIF = 0;
    static final int TYPE_CATEGORY = 1;
    static final int TYPE_CELL = 2;
    Context context;

    public UltraRadAdapter(LinkedHashMap<Integer, String[]> map, Context myContext) {
        this.hashMap     =   map;
        this.context     =   myContext;
    }


    @Override
    public int getItemViewType(int position) {
        String[] entry  =   hashMap.get(position);

        switch (entry[4]) {
            case "notif":
                return TYPE_NOTIF;
            case "category":
                return TYPE_CATEGORY;
            default:
                return TYPE_CELL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        switch (viewType) {
            case TYPE_NOTIF: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notif_block, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            case TYPE_CATEGORY: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.category_block, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
            case TYPE_CELL: {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_card_small, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }
        }
        return null;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String[] entry              =   hashMap.get(position);
        final Picasso mPicasso      =   Picasso.with(context);

        switch (getItemViewType(position)) {
            case TYPE_NOTIF:

                ImageView notifImage        =   (ImageView) holder.itemView.findViewById(R.id.notif_image);
                TextView notifTitle         =   (TextView) holder.itemView.findViewById(R.id.notif_title);
                TextView notifsubtitle      =   (TextView) holder.itemView.findViewById(R.id.notif_subtitle);
                TextView notifAction        =   (TextView) holder.itemView.findViewById(R.id.notif_action);
                LinearLayout notifCover     =   (LinearLayout) holder.itemView.findViewById(R.id.notif_cover);

                String notifCategory        =   entry[11];
                String notifHeadline        =   entry[1];
                String notifSubHeadline     =   entry[2];

                switch (notifCategory){
                    case "category_missing":

                        notifTitle.setText(notifHeadline);
                        notifsubtitle.setText(notifSubHeadline);
                        notifAction.setText("Do it now");
                        notifAction.setBackgroundColor(context.getResources().getColor(R.color.darkevents));
                        notifCover.setBackgroundColor(context.getResources().getColor(R.color.events));
                        mPicasso.load(R.drawable.personalize).into(notifImage);
                        notifImage.setColorFilter(context.getResources().getColor(R.color.darkevents));

                        notifAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(context, CategoriesActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            }
                        });

                        break;
                    case "login_missing":

                        notifTitle.setText(notifHeadline);
                        notifsubtitle.setText(notifSubHeadline);
                        notifAction.setText("Do it now");
                        notifAction.setBackgroundColor(context.getResources().getColor(R.color.darknews));
                        notifCover.setBackgroundColor(context.getResources().getColor(R.color.news));
                        mPicasso.load(R.drawable.login_missing).into(notifImage);
                        notifImage.setColorFilter(context.getResources().getColor(R.color.darknews));

                        notifAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(context, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(i);
                            }
                        });

                        break;
                    case "love_missing":

                        notifTitle.setText(notifHeadline);
                        notifsubtitle.setText(notifSubHeadline);
                        notifAction.setVisibility(View.GONE);
                        notifCover.setBackgroundColor(context.getResources().getColor(R.color.radsIlove));
                        mPicasso.load(R.drawable.ic_like).into(notifImage);
                        notifImage.setColorFilter(context.getResources().getColor(R.color.darkevents));

                        break;
                    default:
                        break;
                }

                break;

            case TYPE_CATEGORY:

                ImageView categoryImage             =   (ImageView) holder.itemView.findViewById(R.id.category_image);
                TextView categoryTitle              =   (TextView) holder.itemView.findViewById(R.id.category_title);
                TextView categorySubtitle           =   (TextView) holder.itemView.findViewById(R.id.category_subtitle);
                TextView categoryAction             =   (TextView) holder.itemView.findViewById(R.id.category_action);
                TextView categoryType               =   (TextView) holder.itemView.findViewById(R.id.category_type);

                String categoriesName               =   entry[1];
                String categoriesType               =   entry[2];

                categoryTitle.setText(categoriesName);
                categoryAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, CategoriesActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                });
                categoryType.setText(categoriesType);

                switch (categoriesType){
                    case "news":
                        categoryImage.setBackgroundColor(context.getResources().getColor(R.color.news));
                        mPicasso.load(R.drawable.ic_news).into(categoryImage);
                        categorySubtitle.setText(context.getResources().getString(R.string.city_news_subtext));
                        break;
                    case "blogs":
                        categoryImage.setBackgroundColor(context.getResources().getColor(R.color.blogs));
                        mPicasso.load(R.drawable.ic_blogs).into(categoryImage);
                        categorySubtitle.setText(context.getResources().getString(R.string.citizens_voice_subtext));
                        break;
                    case "events":
                        categoryImage.setBackgroundColor(context.getResources().getColor(R.color.events));
                        mPicasso.load(R.drawable.ic_events).into(categoryImage);
                        categorySubtitle.setText(context.getResources().getString(R.string.events_subtext));
                        break;
                    case "life_hacks":
                        categoryImage.setBackgroundColor(context.getResources().getColor(R.color.lifeHacks));
                        mPicasso.load(R.drawable.ic_lifehacks).into(categoryImage);
                        categorySubtitle.setText(context.getResources().getString(R.string.live_better_subtext));
                        break;
                    case "hotspots":
                        categoryImage.setBackgroundColor(context.getResources().getColor(R.color.hotspots));
                        mPicasso.load(R.drawable.ic_hotspots).into(categoryImage);
                        categorySubtitle.setText(context.getResources().getString(R.string.grubs_pubs_subtext));
                        break;
                    case "movies":
                        categoryImage.setBackgroundColor(context.getResources().getColor(R.color.movies));
                        mPicasso.load(R.drawable.ic_movies).into(categoryImage);
                        categorySubtitle.setText(context.getResources().getString(R.string.entertainment_subtext));
                        break;
                    case "jobs_internships":
                        categoryImage.setBackgroundColor(context.getResources().getColor(R.color.jobs_internships));
                        mPicasso.load(R.drawable.ic_jobs_internships).into(categoryImage);
                        categorySubtitle.setText(context.getResources().getString(R.string.jobs_internships_subtext));
                        break;
                    default:
                        Log.e("new Category", categoriesType);
                        holder.itemView.setVisibility(View.GONE);
                        break;

                }

                    break;
            case TYPE_CELL:
                String[] entryCell  =   hashMap.get(position);
                Log.e("smallbox", entryCell[1]);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return hashMap.size();
    }
}
