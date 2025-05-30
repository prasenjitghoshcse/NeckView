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
public class PositionMarkerInlayTrapeze implements PositionMarkerInlay {
    @ColorInt
    private int mColor = Color.WHITE;
    private float mPadding = 25f;
    private float mTrapezeDegree = 65f;

    private final Path mPath = new Path();

    public PositionMarkerInlayTrapeze() {}

    public PositionMarkerInlayTrapeze(int color, float padding, float trapezeDegree) {
        mColor = color;
        mPadding = padding;
        mTrapezeDegree = trapezeDegree;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public float getPadding() {
        return mPadding;
    }

    public void setPadding(float padding) {
        mPadding = padding;
    }

    public float getTrapezeDegree() {
        return mTrapezeDegree;
    }

    public void setTrapezeDegree(float trapezeDegree) {
        mTrapezeDegree = trapezeDegree;
    }

    @Override
    public void draw(Context context, Canvas canvas, RectF bounds, int fretIndex, boolean leftHanded) {
        bounds.left += mPadding;
        bounds.top += mPadding;
        bounds.right -= mPadding;
        bounds.bottom -= mPadding;

        setupTrapezePath(mPath, bounds, mTrapezeDegree, leftHanded);

        DrawHelper.drawPath(canvas, mPath, mColor);
    }

    private void setupTrapezePath(Path source, RectF bounds, float degree, boolean leftHanded) {
        //With square triangle formula - b = a * tg(B); B = 90 - A; we know A as degree an b as bounds.width()
        float padding = (float) (bounds.width() * Math.tan(Math.toRadians(90 - degree)));

        source.reset();
        if (leftHanded) {
            source.moveTo(bounds.right, bounds.bottom);
            source.lineTo(bounds.right, bounds.top);
            source.lineTo(bounds.left, bounds.top + padding);
            source.lineTo(bounds.left, bounds.bottom - padding);
        } else {
            source.moveTo(bounds.left, bounds.bottom);
            source.lineTo(bounds.left, bounds.top);
            source.lineTo(bounds.right, bounds.top + padding);
            source.lineTo(bounds.right, bounds.bottom - padding);
        }
        source.close();
    }
}
