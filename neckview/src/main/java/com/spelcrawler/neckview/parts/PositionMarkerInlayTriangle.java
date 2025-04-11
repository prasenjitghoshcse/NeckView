package com.spelcrawler.neckview.parts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;

import androidx.annotation.ColorInt;

import com.spelcrawler.neckview.DrawHelper;
import com.spelcrawler.neckview.parts.base.PositionMarkerInlay;


//================================================================================================//
//================================================================================================//
public class PositionMarkerInlayTriangle implements PositionMarkerInlay {
    @ColorInt
    private int mColor = Color.WHITE;

    private final Path mPath = new Path();

    public PositionMarkerInlayTriangle() {}

    public PositionMarkerInlayTriangle(int color) {
        mColor = color;
    }

    @Override
    public void draw(Context context, Canvas canvas, RectF bounds, int fretIndex, boolean leftHanded) {
        mPath.reset();
        if (leftHanded) {
            mPath.moveTo(bounds.right, bounds.bottom);
            mPath.lineTo(bounds.left, bounds.top);
            mPath.lineTo(bounds.left, bounds.bottom);
        } else {
            mPath.moveTo(bounds.left, bounds.bottom);
            mPath.lineTo(bounds.right, bounds.top);
            mPath.lineTo(bounds.right, bounds.bottom);
        }
        mPath.close();

        DrawHelper.drawPath(canvas, mPath, mColor);
    }
}
