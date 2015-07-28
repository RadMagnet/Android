package com.radmagnet;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;

/**
 * Created by Nikhil on 7/6/15.
 */
public class EachCategory {

    private String mName;
    private String mSubtext;
    protected Drawable mImage;
    private int mColor;
    private String type;

    public void setName(String name) {
        this.mName = name;
    }

    public void setSubtext(String subtext) {
        this.mSubtext = subtext;
    }

    public void setImage(Drawable drawable) {this.mImage = drawable;}

    public void setColor(@ColorInt int color) {this.mColor = color;}

    public void setType(String type){
        this.type   =   type;
    }

    public String getName() {
        return mName;
    }

    public String getSubtext() {
        return mSubtext;
    }

    public Drawable getImage(){ return mImage; }

    public Integer getColor(){ return mColor; }

    public String getType(){
        return type;
    }

}
