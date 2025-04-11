package com.spelcrawler.neckview.parts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import androidx.annotation.ColorInt;

import com.spelcrawler.neckview.DrawHelper;
import com.spelcrawler.neckview.parts.base.String;

public class StringColor implements String {

    @ColorInt
    private int mColor = Color.YELLOW;

    public StringColor() {}

    public StringColor(int color) {
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public void draw(Context context, Canvas canvas, RectF bounds, boolean wounded) {
        DrawHelper.drawColor(canvas, mColor, bounds);
    }

    @Override
    public void draw(Context context, Canvas canvas, RectF bounds) {
        throw new RuntimeException("Use draw(Context, Canvas, RectF, boolean) method instead of this");
    }

}
