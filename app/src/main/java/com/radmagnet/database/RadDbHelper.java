package com.radmagnet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nikhil on 5/17/15.
 */
public class RadDbHelper extends SQLiteOpenHelper {

    private static RadDbHelper mInstance = null;

    public static RadDbHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RadDbHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private RadDbHelper(Context context) {
        super(context, RadContract.DB_NAME, null, RadContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQLqueryRads = "CREATE TABLE IF NOT EXISTS rads(" +
                RadContract.RadsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RadContract.RadsEntry.COLUMN_HEADLINE + " TEXT NULL UNIQUE, " +
                RadContract.RadsEntry.COLUMN_SUB_HEADLINE + " TEXT NULL, " +
                RadContract.RadsEntry.COLUMN_BANNER_IMAGE + " TEXT NULL, " +
                RadContract.RadsEntry.COLUMN_CATEGORY + " TEXT NULL, " +
                RadContract.RadsEntry.COLUMN_SERVER_ID + " INTEGER NULL, " +
                RadContract.RadsEntry.COLUMN_LOCATION + " TEXT NULL, " +
                RadContract.RadsEntry.COLUMN_START_DATE + " INTEGER NULL, " +
                RadContract.RadsEntry.COLUMN_CREATOR + " TEXT NULL, " +
                RadContract.RadsEntry.COLUMN_CREATOR_DP + " TEXT NULL, " +
                RadContract.RadsEntry.COLUMN_DATE_FETCHED + " INTEGER NULL, " +
                RadContract.RadsEntry.COLUMN_NEW_OLD + " INTEGER DEFAULT 0" +
                ")";

        String SQLqueryLove = "CREATE TABLE IF NOT EXISTS love(" +
                RadContract.RadsIlove._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RadContract.RadsIlove.COLUMN_HEADLINE + " INTEGER NULL, " +
                RadContract.RadsIlove.COLUMN_SUB_HEADLINE + " TEXT NULL, " +
                RadContract.RadsIlove.COLUMN_BANNER_IMAGE + " TEXT NULL, " +
                RadContract.RadsIlove.COLUMN_CATEGORY + " TEXT NULL, " +
                RadContract.RadsIlove.COLUMN_SERVER_ID + " INTEGER NULL, " +
                RadContract.RadsIlove.COLUMN_LOCATION + " TEXT NULL, " +
                RadContract.RadsIlove.COLUMN_START_DATE + " INTEGER NULL, " +
                RadContract.RadsIlove.COLUMN_CREATOR + " TEXT NULL, " +
                RadContract.RadsIlove.COLUMN_CREATOR_DP + " TEXT NULL, " +
                RadContract.RadsIlove.COLUMN_DATE_FETCHED + " INTEGER NULL, " +
                RadContract.RadsIlove.COLUMN_NEW_OLD + " INTEGER DEFAULT 0" +
                ")";

        String SQLqueryCategories = "CREATE TABLE IF NOT EXISTS " + RadContract.TABLE_CATEGORIES + "(" +
                RadContract.Categories._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RadContract.Categories.COLUMN_CATEGORY_NAME + " TEXT NULL UNIQUE, " +
                RadContract.Categories.COLUMN_CATEGORY_TITLE + " TEXT NULL" +
                ")";

        Log.e("TaskDBHelper", "Query to form table Rads: " + SQLqueryRads);
        Log.e("TaskDBHelper", "Query to form table Loves: " + SQLqueryLove);
        Log.e("TaskDBHelper", "Query to form table Categories: " + SQLqueryCategories);


        db.execSQL(SQLqueryRads);
        db.execSQL(SQLqueryLove);
        db.execSQL(SQLqueryCategories);

        putCategoryValues(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + RadContract.TABLE_CATEGORIES);

        String SQLqueryCategories = "CREATE TABLE IF NOT EXISTS " + RadContract.TABLE_CATEGORIES + "(" +
                RadContract.Categories._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RadContract.Categories.COLUMN_CATEGORY_NAME + " TEXT NULL UNIQUE, " +
                RadContract.Categories.COLUMN_CATEGORY_TITLE + " TEXT NULL" +
                ")";

        db.execSQL(SQLqueryCategories);

        putCategoryValues(db);

        onCreate(db);

    }

    private void putCategoryValues(SQLiteDatabase sqlDB){

        ContentValues values = new ContentValues();


        String[] categoryNames  = {"news", "blogs", "events", "hotspots", "life_hacks", "movies", "jobs_internships"};

        String[] categoryTitles     =   {"City News", "Citizen's Voice", "Events", "Hotspots", "Live Better", "Entertainment", "Jobs & Internships"};

        for(int i=0; i<categoryNames.length; i++){

            values.clear();
            values.put(RadContract.Categories.COLUMN_CATEGORY_NAME, categoryNames[i]);
            values.put(RadContract.Categories.COLUMN_CATEGORY_TITLE, categoryTitles[i]);
            sqlDB.insertWithOnConflict(RadContract.TABLE_CATEGORIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        }


    }

    public void manageMaxEntries(Integer maxEntries) {

        /**
         *  Keep max number of entries to maxEntries =   100;
         *  Count number of current entries - if more than maxEntries - remove excess from the top
         *
         * */

        SQLiteDatabase sqlDB = this.getWritableDatabase();

        Cursor countMax = sqlDB.rawQuery("SELECT * FROM " + RadContract.TABLE_RADS, null);

        try {
            if (countMax.getCount() > maxEntries) {

                Integer entriesToDelete = countMax.getCount() - maxEntries;

                String deleteQuery = "DELETE FROM " + RadContract.TABLE_RADS + " WHERE " + RadContract.RadsEntry._ID + " IN (SELECT " + RadContract.RadsEntry._ID + " FROM " + RadContract.TABLE_RADS + " ORDER BY " + RadContract.RadsEntry.COLUMN_DATE_FETCHED + " ASC LIMIT " + entriesToDelete + ")";
//                Log.e("deleteQuery", deleteQuery);
                sqlDB.execSQL(deleteQuery);

            }

        } finally {
            countMax.close();
        }


    }

    public Cursor fetchNormal(String selectedCategory) {

        /**
         *  Fetch Normal Rads from Database
         *
         * */
        SQLiteDatabase fetchSqlDB = this.getWritableDatabase();

        String[] From = new String[]{
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
                RadContract.RadsEntry.COLUMN_SERVER_ID,
                RadContract.RadsEntry.COLUMN_NEW_OLD
        };

        String Where = "";

        String orderBy = RadContract.RadsEntry.COLUMN_DATE_FETCHED + " DESC";

        switch (selectedCategory) {
            case "all_the_rads":

                Where = "";

                break;
            default:

                Where = RadContract.RadsEntry.COLUMN_CATEGORY + " LIKE '%" + selectedCategory + "%'";

                break;
        }

        return fetchSqlDB.query(RadContract.TABLE_RADS,
                From
                , Where, null, null, null, orderBy, null);

    }

    public Cursor fetchLove() {

        /**
         *  Fetch Normal Rads from Database
         *
         * */
        SQLiteDatabase fetchSqlDB = this.getWritableDatabase();
        String[] From = new String[]{
                RadContract.RadsEntry._ID,
                RadContract.RadsEntry.COLUMN_HEADLINE,
                RadContract.RadsEntry.COLUMN_CATEGORY,
                RadContract.RadsEntry.COLUMN_DATE_FETCHED,
                RadContract.RadsEntry.COLUMN_SERVER_ID
        };

        String orderBy = RadContract.RadsEntry.COLUMN_CATEGORY + " DESC, " + RadContract.RadsEntry._ID + " DESC";

        return fetchSqlDB.query(RadContract.TABLE_LOVE,
                From
                , null, null, null, null, orderBy, null);

    }

    public String[] fetchCategoriesID(SQLiteDatabase sqlDB) {

        String[] From = new String[]{
                RadContract.Categories._ID,
                RadContract.Categories.COLUMN_CATEGORY_TITLE,
                RadContract.Categories.COLUMN_CATEGORY_NAME
        };

        Cursor cursor = sqlDB.query(RadContract.TABLE_CATEGORIES,
                From
                , null, null, null, null, null, null);

        cursor.moveToFirst();
        ArrayList<String> categoriesArray = new ArrayList<>();
        ArrayList<String> categoriesNameArray = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            categoriesArray.add(cursor.getString(cursor.getColumnIndex(RadContract.Categories.COLUMN_CATEGORY_NAME)));
            categoriesNameArray.add(cursor.getString(cursor.getColumnIndex(RadContract.Categories.COLUMN_CATEGORY_TITLE)));
            cursor.moveToNext();
        }
        cursor.close();
        sqlDB.close();

        return categoriesArray.toArray(new String[categoriesArray.size()]);

    }

    public Integer addAsLove(SQLiteDatabase sqlDB, String resourceCategory, String resourceId) {

        Integer success;

//        Fetch from RadsTable -> Check if record exists in LoveTable -> if not, insert

        try {

            String cursorString = "SELECT * FROM " + RadContract.TABLE_RADS
                    + " WHERE " + RadContract.RadsIlove.COLUMN_CATEGORY + " = '" + resourceCategory
                    + "' AND " + RadContract.RadsIlove.COLUMN_SERVER_ID + " = '" + resourceId + "'";

            Cursor fetchFromRads = sqlDB.rawQuery(cursorString, null);

            fetchFromRads.moveToFirst();

            Integer exists = checkAsLove(sqlDB, resourceCategory, resourceId);

            if (exists == 0) {

                ContentValues values = new ContentValues();

                values.put(RadContract.RadsIlove.COLUMN_HEADLINE, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_HEADLINE)));
                values.put(RadContract.RadsIlove.COLUMN_SUB_HEADLINE, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_SUB_HEADLINE)));
                values.put(RadContract.RadsIlove.COLUMN_BANNER_IMAGE, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_BANNER_IMAGE)));
                values.put(RadContract.RadsIlove.COLUMN_CATEGORY, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_CATEGORY)));
                values.put(RadContract.RadsIlove.COLUMN_SERVER_ID, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_SERVER_ID)));
                values.put(RadContract.RadsIlove.COLUMN_LOCATION, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_LOCATION)));
                values.put(RadContract.RadsIlove.COLUMN_START_DATE, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_START_DATE)));
                values.put(RadContract.RadsIlove.COLUMN_CREATOR, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_CREATOR)));
                values.put(RadContract.RadsIlove.COLUMN_CREATOR_DP, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_CREATOR_DP)));
                values.put(RadContract.RadsIlove.COLUMN_DATE_FETCHED, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_DATE_FETCHED)));
                values.put(RadContract.RadsIlove.COLUMN_NEW_OLD, fetchFromRads.getString(fetchFromRads.getColumnIndex(RadContract.RadsEntry.COLUMN_NEW_OLD)));

                sqlDB.insertWithOnConflict(RadContract.TABLE_LOVE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                success = 1;

            } else {
                // delete and unlove

                String deleteExistence = "DELETE FROM " + RadContract.TABLE_LOVE
                        + " WHERE " + RadContract.RadsIlove.COLUMN_CATEGORY + " = '" + resourceCategory
                        + "' AND " + RadContract.RadsIlove.COLUMN_SERVER_ID + " = '" + resourceId + "'";

                sqlDB.execSQL(deleteExistence);

//                Log.e("likeRad", "deleted");
                success = 3;
            }


        } catch (Exception e) {

//            Log.e("AddAsRad", e.toString());

            success = 0;

        }

        return success;

    }

    public Integer checkAsLove(SQLiteDatabase sqlDB, String radCategory, String radServerId) {

        String checkExistence = "SELECT * FROM " + RadContract.TABLE_LOVE
                + " WHERE " + RadContract.RadsIlove.COLUMN_CATEGORY + " = '" + radCategory
                + "' AND " + RadContract.RadsIlove.COLUMN_SERVER_ID + " = '" + radServerId + "'";

        Cursor checkExist = sqlDB.rawQuery(checkExistence, null);

        Integer getCount = checkExist.getCount();


        return getCount;

    }

    public String[] fetchCategories(SQLiteDatabase sqlDB) {

        String[] From = new String[]{
                RadContract.Categories._ID,
                RadContract.Categories.COLUMN_CATEGORY_TITLE,
                RadContract.Categories.COLUMN_CATEGORY_NAME
        };

        Cursor cursor = sqlDB.query(RadContract.TABLE_CATEGORIES,
                From
                , null, null, null, null, null, null);

        cursor.moveToFirst();
        ArrayList<String> categoriesArray = new ArrayList<>();
        ArrayList<String> categoriesNameArray = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            categoriesArray.add(cursor.getString(cursor.getColumnIndex(RadContract.Categories.COLUMN_CATEGORY_NAME)));
            categoriesNameArray.add(cursor.getString(cursor.getColumnIndex(RadContract.Categories.COLUMN_CATEGORY_TITLE)));
            cursor.moveToNext();
        }
        cursor.close();

        return categoriesNameArray.toArray(new String[categoriesArray.size()]);

    }

    public List<String> fetchCategoriesTags() {

        SQLiteDatabase sqlDB = this.getWritableDatabase();

        String[] From = new String[]{
                RadContract.Categories._ID,
                RadContract.Categories.COLUMN_CATEGORY_NAME
        };

        Cursor cursor = sqlDB.query(RadContract.TABLE_CATEGORIES,
                From
                , null, null, null, null, null, null);

        cursor.moveToFirst();
        List<String> categoryTags = new ArrayList<>();

        while (!cursor.isAfterLast()) {

            categoryTags.add(cursor.getString(cursor.getColumnIndex(RadContract.Categories.COLUMN_CATEGORY_NAME)));
            cursor.moveToNext();
        }
        cursor.close();

        return categoryTags;

    }

    public List<String> fetchCategoriesTitles(List<String> categoryTags) {

        List<String> categoryTitles = new ArrayList<>();

        SQLiteDatabase sqlDB = this.getWritableDatabase();

        String[] From = new String[]{
                RadContract.Categories._ID,
                RadContract.Categories.COLUMN_CATEGORY_TITLE
        };

        for(int i=0; i<categoryTags.size(); i++){

            Cursor cursor;
            String where = RadContract.Categories.COLUMN_CATEGORY_NAME + "='" + categoryTags.get(i) + "'";
            Log.e("where", where);
            try{

                cursor   =   sqlDB.query(RadContract.TABLE_CATEGORIES,
                        From
                        , where, null, null, null, null, null);
                cursor.moveToFirst();

                categoryTitles.add(cursor.getString(cursor.getColumnIndex(RadContract.Categories.COLUMN_CATEGORY_TITLE)));

                cursor.close();

            }catch (NullPointerException e){

            }


        }

        Cursor cursor = sqlDB.query(RadContract.TABLE_CATEGORIES,
                From
                , null, null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            cursor.moveToNext();
        }
        cursor.close();

        return categoryTitles;

    }

    public Cursor fetchFromNormalDB(SQLiteDatabase sqlDB, String category, String id) {

        Cursor cursor = null;

        /**
         *  2 places the user can chech a rad from, Love and Normal
         *  Loved Rads might not be available in Normal DB... so it's important to check both DBs
         * */

        String cursorString = "SELECT * FROM " + RadContract.TABLE_RADS
                + " WHERE " + RadContract.RadsIlove.COLUMN_CATEGORY + " = '" + category
                + "' AND " + RadContract.RadsIlove.COLUMN_SERVER_ID + " = '" + id + "'";


        cursor = sqlDB.rawQuery(cursorString, null);

        return cursor;
    }

    public Cursor fetchFromLoveDB(SQLiteDatabase sqlDB, String category, String id) {

        Cursor cursor = null;

        /**
         *  2 places the user can chech a rad from, Love and Normal
         *  Loved Rads might not be available in Normal DB... so it's important to check both DBs
         * */

        String cursorString = "SELECT * FROM " + RadContract.TABLE_LOVE
                + " WHERE " + RadContract.RadsIlove.COLUMN_CATEGORY + " = '" + category
                + "' AND " + RadContract.RadsIlove.COLUMN_SERVER_ID + " = '" + id + "'";

        cursor = sqlDB.rawQuery(cursorString, null);

        return cursor;
    }

    public void markAsOld(SQLiteDatabase sqlDB, String id) {
        ContentValues cv = new ContentValues();
        cv.put(RadContract.RadsEntry.COLUMN_NEW_OLD, 1);

        sqlDB.update(RadContract.TABLE_RADS, cv, RadContract.RadsEntry._ID + " = " + id, null);

    }

    public String changeCategoryChoices(String choiceAction, String oldPrefs) {

        String returnString;

        try {

            Log.e("PreviousString", oldPrefs);
            Log.e("ChangesString", choiceAction);

        } catch (NullPointerException e) {
            Log.e("Null", e.getMessage());
        }

        /**
         *  If OldPrefs = 0; I.E. This is the first time the user is selecting categories
         * */
        if (oldPrefs.equals("0")) {

            /**
             *  If choiceAction = 0; The user hasn't picked any categories
             * */

            if (choiceAction.equals("0")) {

                returnString = "0";

            } else {

                choiceAction = choiceAction.substring(0, (choiceAction.length() - 1));

                Map<String, Integer> changesMap = makeProperMap(choiceAction);

                returnString = changesMap.toString();

            }

        } else {

            oldPrefs = oldPrefs.substring(1, oldPrefs.length() - 1);

            if (!choiceAction.equals("0")) {
//            Changes Made

                choiceAction = choiceAction.substring(0, (choiceAction.length() - 1));

                Map<String, Integer> oldPrefsMap;
                Map<String, Integer> changesMap;

                oldPrefsMap = makeProperMap(oldPrefs);

                changesMap = makeProperMap(choiceAction);

                for (Map.Entry<String, Integer> entry : changesMap.entrySet()) {

                    String category = entry.getKey();
//                    Log.e("checking", category);

                    Integer value = entry.getValue();

                    if (!oldPrefsMap.containsKey(category)) {

//                    Doesnt Contain Category

                        oldPrefsMap.put(category, value);

                    } else {

//                    Contains Category

                        Integer currentValues = oldPrefsMap.get(category);

                        currentValues += value;
//                        Log.e("currentValue", String.valueOf(currentValues));

                        if (currentValues <= 0) {

                            oldPrefsMap.remove(category);

                        } else {

                            oldPrefsMap.put(category, currentValues);

                        }
                    }

                }

                returnString = oldPrefsMap.toString();

            } else {

                returnString = "0";

            }

        }

        return returnString;

    }

    public Map<String, Integer> makeProperMap(String string) {

        Map<String, Integer> returnMap = new HashMap<>();

        String[] actions = string.split(",");


        for (String action : actions) {

            List<String> key_value = Arrays.asList(action.split("="));

//            String[] keyValueArray = (String[]) key_value.toArray();

            String category = key_value.get(0);
            category = category.trim();

            Integer value = Integer.valueOf(key_value.get(1).trim());

            if (!returnMap.containsKey(category)) {

                returnMap.put(category, value);

            } else {

                Integer currentValues = returnMap.get(category);

                currentValues += value;

                if (currentValues == 0) {
                    returnMap.remove(category);
                } else {
                    returnMap.put(category, currentValues);
                }

            }

        }

        return returnMap;

    }

    public String fetchUserPreferencesCategories(String userPrefs) {

        String Where = "";
        ArrayList<String> whereArray = new ArrayList<>();

        if (userPrefs.equals("0")) {

            Where += "";

        } else {

            Map<String, Integer> userPrefsMap = makeProperMap(userPrefs);

            for (Map.Entry<String, Integer> entry : userPrefsMap.entrySet()) {

                String category = entry.getKey();

                whereArray.add("'%" + category.trim() + "%'");

                Where += RadContract.Categories.COLUMN_CATEGORY_NAME + " LIKE '%" + category.trim() + "%' OR ";

            }

            Where = Where.substring(0, Where.length() - 4);

        }


        return Where;

    }

    public Map<String, String> fetchCategoriesMap(List<String> categoryTags, List<String> categoryTitles) {

        Map<String, String> returnMap = new HashMap<>();

        for (int i = 0; i < categoryTags.size(); i++) {

            returnMap.put(categoryTags.get(i), categoryTitles.get(i));

        }

        return returnMap;
    }

    public List<String> userCategoriesList(String userPrefs) {

        if (!userPrefs.equals("0")) {
            userPrefs = userPrefs.substring(1, userPrefs.length() - 1);
            userPrefs = userPrefs.replace("\\s", "").replace("1", "").replace("=", "");
        }

        String[] userCategories = userPrefs.split(",");
        List<String> userCategoryTags = Arrays.asList(userCategories);

        List<String> clearUserCategoryTags = new ArrayList<>();

        for (int i = 0; i < userCategoryTags.size(); i++)
            clearUserCategoryTags.add(i, userCategoryTags.get(i).trim());

        return clearUserCategoryTags;
    }

    public Cursor fetchRemainingCategories(String userPrefs, String[] From) {

        MatrixCursor matrixCursor = new MatrixCursor(From);

        List<String> categoryTags = this.fetchCategoriesTags();

        List<String> categoryTitles = this.fetchCategoriesTitles(categoryTags);

        Map<String, String> categoriesMap = this.fetchCategoriesMap(categoryTags, categoryTitles);

        /** converting userPrefs to List
         * */
        List<String> clearUserCategoryTags = this.userCategoriesList(userPrefs);

        /** getting pending categories
         * */
        categoryTags.removeAll(clearUserCategoryTags);

        /** creating Cursor
         * */

        for (String category : categoryTags) {

            matrixCursor.addRow(new Object[]{"0", categoriesMap.get(category), category, null, "category", null, null, null, null, null, null, "category_suggest"});

        }

        return matrixCursor;

    }

    public Cursor fetchFeaturedRads(SQLiteDatabase sqlDB, String userPrefs, String[] From) {

        Cursor cursor = null;

//        List<String> clearUserCategoryTags  =   this.userCategoriesList(userPrefs);

//        if(clearUserCategoryTags.size() > 0){
//        // Fetch Featured from pref category
//            for(String eachCategory: clearUserCategoryTags){
//
//            }
//
//        }else{
////            Fetch featured from all categories
//        }


        return cursor;

    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
