package com.spelcrawler.neckview.parts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import androidx.annotation.ColorInt;

import com.spelcrawler.neckview.parts.base.SideFinish;

public class SideFinishColor implements SideFinish {

    @ColorInt
    private int mColor = Color.WHITE;

    public SideFinishColor() {}

    public SideFinishColor(int color) {
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public void draw(Context context, Canvas canvas, RectF bounds) {
//        Drawer.drawColor(canvas, mColor, bounds);
    }

}
