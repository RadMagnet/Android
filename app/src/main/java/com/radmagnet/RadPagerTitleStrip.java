package com.radmagnet;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.PagerTitleStrip;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Nikhil on 6/29/15.
 */
public class RadPagerTitleStrip extends PagerTitleStrip {
    public RadPagerTitleStrip(Context context) {
        super(context);
    }
    public RadPagerTitleStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/AlegreyaSans-ExtraBoldItalic.otf");
        for (int i=0; i<this.getChildCount(); i++) {
            if (this.getChildAt(i) instanceof TextView) {
                ((TextView)this.getChildAt(i)).setTypeface(tf);
            }
        }
    }
}