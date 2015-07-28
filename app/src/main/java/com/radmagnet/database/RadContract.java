package com.radmagnet.database;

import android.provider.BaseColumns;

/**
 * Created by Nikhil on 5/17/15.
 */
public class RadContract {

    public static final String DB_NAME = "com.example.TodoList.db.radDB";
    public static final int DB_VERSION = 4;
    public static final String TABLE_RADS       = "rads";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_LOVE       = "love";

    public class RadsEntry implements BaseColumns{
        //  Name of columns in the rads table

        public static final String COLUMN_HEADLINE          =   "headline";
        public static final String COLUMN_SUB_HEADLINE      =   "sub_headline";
        public static final String COLUMN_BANNER_IMAGE      =   "banner_image";
        public static final String COLUMN_CATEGORY          =   "category";
        public static final String COLUMN_SERVER_ID         =   "server_id";
        public static final String COLUMN_LOCATION          =   "location";
        public static final String COLUMN_START_DATE        =   "start_date";
        public static final String COLUMN_CREATOR           =   "creator";
        public static final String COLUMN_CREATOR_DP        =   "creator_dp";
        public static final String COLUMN_DATE_FETCHED      =   "date_fetched";
        public static final String COLUMN_NEW_OLD           =   "new_old";
        public static final String _ID                      =   "_id";

    }

    public class RadsIlove implements BaseColumns{
        //  All the bookmarked Rads

        public static final String COLUMN_HEADLINE          =   "headline";
        public static final String COLUMN_SUB_HEADLINE      =   "sub_headline";
        public static final String COLUMN_BANNER_IMAGE      =   "banner_image";
        public static final String COLUMN_CATEGORY          =   "category";
        public static final String COLUMN_SERVER_ID         =   "server_id";
        public static final String COLUMN_LOCATION          =   "location";
        public static final String COLUMN_START_DATE        =   "start_date";
        public static final String COLUMN_CREATOR           =   "creator";
        public static final String COLUMN_CREATOR_DP        =   "creator_dp";
        public static final String COLUMN_DATE_FETCHED      =   "date_fetched";
        public static final String COLUMN_NEW_OLD           =   "new_old";
        public static final String _ID                      =   "_id";


    }

    public class Categories implements BaseColumns{
        public static final String _ID                      =   "_id";
        public static final String COLUMN_CATEGORY_NAME     =   "category_name";
        public static final String COLUMN_CATEGORY_TITLE    =   "category_title";
    }

    public class SystemConstants{

        //  What to show as category title //--//-- For logic purpose
        public static final String ALL_THE_RADS             =   "all_the_rads";
        public static final String BROADCASTS               =   "broadcast";
        public static final String HOTSPOTS                 =   "hotspots";
        public static final String EVENTS                   =   "events";
        public static final String LIFE_HACKS               =   "life_hacks";
        public static final String DEALS                    =   "deals";
        public static final String MOVIES                   =   "movies";
        public static final String RADS_I_LOVE              =   "rads_i_love";
        public static final String NEWS                     =   "news";
        public static final String BLOGS                    =   "blogs";
        public static final String JOBS                     =   "jobs_internships";

        //  What to show as category title //--//-- For display purpose only, no logic attached
        public static final String all_the_rads        =   "All The Rads";
        public static final String broadcasts          =   "Broadcasts";
        public static final String hotspots            =   "Hotspots";
        public static final String events              =   "Events";
        public static final String life_hacks          =   "Life Hacks";
        public static final String deals               =   "Deals";
        public static final String movies              =   "Movies";
        public static final String rads_i_love         =   "Rads I Love";
        public static final String news                =   "News";
        public static final String blogs               =   "Blogs";

        public static final String PREFS_NAME               =   "rad_settings";



    }


}
