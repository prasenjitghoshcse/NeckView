package com.spelcrawler.neckview.parts.base;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;


//================================================================================================//
//================================================================================================//
public interface String extends FretboardPart {
    void draw(Context context, Canvas canvas, RectF bounds, boolean wounded);
}
