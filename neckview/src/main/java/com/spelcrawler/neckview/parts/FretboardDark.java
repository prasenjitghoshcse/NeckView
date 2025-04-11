package com.spelcrawler.neckview.parts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;

import androidx.annotation.DrawableRes;

import com.spelcrawler.neckview.DrawHelper;
import com.spelcrawler.neckview.parts.base.Fretboard;

public class FretboardDark implements Fretboard {

    @DrawableRes
    private final int mDrawableRes;

    public FretboardDark(int drawableRes) {
        mDrawableRes = drawableRes;
    }

    @Override
    public void draw(Context context, Canvas canvas, RectF bounds) {
        DrawHelper.drawDrawable(context, canvas, mDrawableRes, bounds);
    }

}
