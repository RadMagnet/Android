package com.radmagnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.MaterialViewPagerHelper;
import com.github.florent37.materialviewpager.adapter.RecyclerViewMaterialAdapter;
import com.radmagnet.database.RadContract;
import com.radmagnet.database.RadDbHelper;

import java.util.Arrays;

/**
 * Created by Nikhil on 7/1/15.
 */
public class RecyclerViewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private String selectedCategory;
    private FragmentStatePagerAdapter radPagerAdapter;


    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectedCategory    =   getArguments().getString("selectedCategory");
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        final Context myContext                       =   getActivity();
        final String[] categoriesArray                =   ((MainActivity)getActivity()).getCategoriesArray();
        final String[] categoriesTitleArray           =   ((MainActivity)getActivity()).getCategoriesTitleArray();
        final MaterialViewPager materialViewPager           =   ((MainActivity)getActivity()).getMaterialPager();
//        final MaterialViewPager materialViewPager           =   (MaterialViewPager) getActivity().findViewById(R.id.materialViewPager);
        final Integer currentTab                        = Arrays.asList(categoriesArray).indexOf(selectedCategory);
        radPagerAdapter =   new FragmentStatePagerAdapter(getFragmentManager()) {

            @Override
            public Fragment getItem(int position) {

                Bundle args = new Bundle();
                args.putString("selectedCategory", categoriesArray[position]);

                RecyclerViewFragment selectedFragment = new RecyclerViewFragment();
                selectedFragment.setArguments(args);
                return selectedFragment;

            }

            @Override
            public int getCount() {
                return categoriesArray.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {

                return categoriesTitleArray[position];
            }

        };


        SQLiteDatabase fetchSqlDB = RadDbHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
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


        String orderBy = RadContract.RadsEntry.COLUMN_DATE_FETCHED + " DESC";


        MatrixCursor matrixCursor   =   new MatrixCursor(From);
        MergeCursor mergeCursor;
        NewRadAdapter radAdapter    =   new NewRadAdapter();

        String Where   = RadContract.RadsEntry.COLUMN_CATEGORY + " LIKE '%" + selectedCategory + "%'";
        Cursor cursorTwo = fetchNormal(fetchSqlDB, From, Where, null, orderBy);
        mAdapter    =   radAdapter.getRecyclerViewMaterialAdapter(cursorTwo, myContext);


        /**
         *  For Pop_Rads, display custom pro
         * */


        mRecyclerView.setAdapter(mAdapter);

        final SwipeRefreshLayout swipeToRefresh  =   (SwipeRefreshLayout) view.findViewById(R.id.swipe_for_rads);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isOnline()) {
                    /**
                     *  Fetch More Rads from API
                     * */
                    final SharedPreferences settings      = myContext.getSharedPreferences(RadContract.SystemConstants.PREFS_NAME, myContext.MODE_PRIVATE);
                    final String userNumber               = settings.getString(myContext.getResources().getString(R.string.sharedprefs_loginid), "notset");
                    final String userSource               = settings.getString(myContext.getResources().getString(R.string.sharedprefs_loginsource), "notset");
                    final String requestString            = settings.getString(myContext.getResources().getString(R.string.sharedprefs_APIrequestString), "");
                    fetchMoreRads(myContext, settings, userNumber, userSource, requestString, mRecyclerView, (RecyclerViewMaterialAdapter) mAdapter, swipeToRefresh, selectedCategory, materialViewPager, radPagerAdapter, currentTab);

                } else {

                    if (swipeToRefresh!=null) {
                        swipeToRefresh.setRefreshing(false);
                        swipeToRefresh.destroyDrawingCache();
                        swipeToRefresh.clearAnimation();
                    }

                    Toast.makeText(myContext, "No Internet!", Toast.LENGTH_LONG).show();

                }

            }
        });

        MaterialViewPagerHelper.registerRecyclerView(getActivity(), mRecyclerView, null);
    }

    public Cursor fetchNormal(SQLiteDatabase fetchSqlDB, String[] From, String Where, String groupBy, String orderBy) {

        return fetchSqlDB.query(RadContract.TABLE_RADS,
                From
                , Where, null, groupBy, null, orderBy, null);

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void fetchMoreRads(Context context, SharedPreferences prefs, String number, String source, String APIrequest, RecyclerView mvp, RecyclerViewMaterialAdapter fspa, SwipeRefreshLayout swipeToRefresh, String category, MaterialViewPager matViewPager, FragmentStatePagerAdapter matPagerAdapter, Integer currentTab){

        FetchRads fetchRads         =   new FetchRads();
        fetchRads.userNumber        =   number;
        fetchRads.userSource        =   source;
        fetchRads.myContext         =   context;
        fetchRads.selectedCategory  =   category;
        fetchRads.mViewPager        =   mvp;
        fetchRads.requestString     =   APIrequest;
        fetchRads.swipeToRefresh    =   swipeToRefresh;
        fetchRads.matViewPager      =   matViewPager;
        fetchRads.matPagerAdapter   =   matPagerAdapter;
        fetchRads.currentTab        =   currentTab;

        fetchRads.execute(APIrequest);

    }


}
