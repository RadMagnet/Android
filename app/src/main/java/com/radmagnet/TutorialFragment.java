package com.radmagnet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nikhil on 7/13/15.
 */
public class TutorialFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.each_tutorial, container, false);

        ImageView slideImage        = (ImageView) rootView.findViewById(R.id.tutorial_screen);
        FrameLayout slideSheath     = (FrameLayout) rootView.findViewById(R.id.sheath);

        final ScheduledExecutorService worker =
                Executors.newSingleThreadScheduledExecutor();

        int page = getArguments().getInt("slide", 1) + 1;
        Log.e("pageNumber", String.valueOf(page));
        switch (page) {
            case 1:
                Picasso.with(getActivity()).load(R.drawable.tutorial_one).into(slideImage);
                slideSheath.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            case 2:
                Picasso.with(getActivity()).load(R.drawable.tutorial_two).into(slideImage);
                slideSheath.setBackgroundColor(getResources().getColor(R.color.events));
                break;
            case 3:
                Picasso.with(getActivity()).load(R.drawable.tutorial_three).into(slideImage);
                slideSheath.setBackgroundColor(getResources().getColor(R.color.events));
                break;
            case 4:
                Picasso.with(getActivity()).load(R.drawable.splash_three).into(slideImage);
                slideSheath.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            case 5:
                Picasso.with(getActivity()).load(R.drawable.splash_two).into(slideImage);
                slideSheath.setBackgroundColor(getResources().getColor(R.color.black));
                Runnable task = new Runnable() {
                    public void run() {
                        getActivity().finish();
                    }
                };
                worker.schedule(task, 5, TimeUnit.SECONDS);
                break;
            default:
                break;
        }

        return rootView;
    }


}