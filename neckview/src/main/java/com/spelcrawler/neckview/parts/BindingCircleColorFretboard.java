package com.spelcrawler.neckview.parts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import androidx.annotation.ColorInt;

import com.spelcrawler.neckview.DrawHelper;
import com.spelcrawler.neckview.parts.base.FretboardBinding;

public class BindingCircleColorFretboard implements FretboardBinding {

    private int mColor = Color.WHITE;
    private float mRadius = 30f;

    public BindingCircleColorFretboard() {}

    public BindingCircleColorFretboard(@ColorInt int color, float radius) {
        mColor = color;
        mRadius = radius;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    @Override
    public void draw(Context context, Canvas canvas, RectF bounds, int fretIndex, boolean leftHanded) {
        if (fretIndex % 12 == 0) {
            float topY = bounds.top + bounds.height() / 4;
            float bottomY = bounds.bottom - bounds.height() / 4;
            DrawHelper.drawCircle(canvas, bounds.centerX(), topY, mRadius, mColor);
            DrawHelper.drawCircle(canvas, bounds.centerX(), bottomY, mRadius, mColor);
        } else {
            DrawHelper.drawCircle(canvas, bounds.centerX(), bounds.centerY(), mRadius, mColor);
        }
    }

}
