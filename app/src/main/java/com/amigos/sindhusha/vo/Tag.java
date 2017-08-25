package com.amigos.sindhusha.vo;

/**
 * Created by Sachin on 7/30/2017.
 */


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.cunoraz.tagview.Constants;

import org.w3c.dom.Text;

public class Tag extends com.cunoraz.tagview.Tag {
    public int id;
    public String text;
    public int tagTextColor;
    public float tagTextSize;
    public int layoutColor;
    public int layoutColorPress;
    public boolean isDeletable;
    public int deleteIndicatorColor;
    public float deleteIndicatorSize;
    public float radius;
    public String deleteIcon;
    public float layoutBorderSize;
    public int layoutBorderColor;
    public Drawable background;

    public Tag(String text) {
        super(text);

        this.init(0, text, Constants.DEFAULT_TAG_TEXT_COLOR, 14.0F, Constants.DEFAULT_TAG_LAYOUT_COLOR, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 14.0F, 100.0F, "×", 0.0F, Constants.DEFAULT_TAG_LAYOUT_BORDER_COLOR);
    }




/*

    public Tag(String text) {
        this.init(0, text, Constants.DEFAULT_TAG_TEXT_COLOR, 14.0F, Constants.DEFAULT_TAG_LAYOUT_COLOR, Constants.DEFAULT_TAG_LAYOUT_COLOR_PRESS, false, Constants.DEFAULT_TAG_DELETE_INDICATOR_COLOR, 14.0F, 100.0F, "×", 0.0F, Constants.DEFAULT_TAG_LAYOUT_BORDER_COLOR);
    }
*/

    private void init(int id, String text, int tagTextColor, float tagTextSize, int layoutColor, int layoutColorPress, boolean isDeletable, int deleteIndicatorColor, float deleteIndicatorSize, float radius, String deleteIcon, float layoutBorderSize, int layoutBorderColor) {
        this.id = id;
        this.text = text;
        this.tagTextColor = tagTextColor;
        this.tagTextSize = tagTextSize;
        this.layoutColor = layoutColor;
        this.layoutColorPress = layoutColorPress;
        this.isDeletable = isDeletable;
        this.deleteIndicatorColor = deleteIndicatorColor;
        this.deleteIndicatorSize = deleteIndicatorSize;
        this.radius = radius;
        this.deleteIcon = deleteIcon;
        this.layoutBorderSize = layoutBorderSize;
        this.layoutBorderColor = layoutBorderColor;
    }

//    public String getText(){
//        return this.text;
//    }
//    public int getTextColor(){
//        return this.tagTextColor;
//    }

/*
    public void onClick(View v) {
        Log.i("TopicTagsActivity","onclick of Tag in amigos");
        ((Tag)v).layoutColor= Color.BLUE;
    }*/

}
