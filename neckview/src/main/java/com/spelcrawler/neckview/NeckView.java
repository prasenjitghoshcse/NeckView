package com.spelcrawler.neckview;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.spelcrawler.neckview.model.GuitarString;
import com.spelcrawler.neckview.parts.PositionMarkerInlayCircle;
import com.spelcrawler.neckview.parts.FretColor;
import com.spelcrawler.neckview.parts.SideFinishColor;
import com.spelcrawler.neckview.parts.NutColor;
import com.spelcrawler.neckview.parts.StringColor;
import com.spelcrawler.neckview.parts.base.Fret;
import com.spelcrawler.neckview.parts.base.PositionMarkerInlay;
import com.spelcrawler.neckview.parts.base.SideFinish;
import com.spelcrawler.neckview.parts.base.Nut;
import com.spelcrawler.neckview.parts.base.String;
import com.spelcrawler.neckview.parts.base.Fretboard;
import com.spelcrawler.neckview.parts.base.NoteMark;
import com.spelcrawler.neckview.parts.base.NoteMarkAnimatable;

import java.util.ArrayList;
import java.util.List;


//================================================================================================//
//================================================================================================//
public class NeckView extends View {
    public enum FretboardFinishPosition {
        TOP,
        BOTTOM
    }

    @NonNull
    private final RectF mBounds = new RectF();
    @NonNull
    private final RectF mFretboardBounds = new RectF();
    @NonNull
    private final RectF mDrawBounds = new RectF();
    @NonNull
    private final RectF mOldDrawBounds = new RectF();

    private int mFretCount = 14;
    private boolean mDrawZeroFret = false;
    private boolean mLeftHanded = true;

    private float mNutWidth = 50;
    private float mFretWidth = 25;
    private float mLastFretPadding = 50;
    private float mTopFinishWidth = 25;
    private float mBottomFinishWidth = 25;

    @NonNull
    private float[] mFretPositions;

    @Nullable
    private Fretboard mFretboard = null;
    @Nullable
    private Nut mNut = new NutColor();
    @Nullable
    private Fret mFret = new FretColor();
    @Nullable
    private SideFinish mSideFinish = new SideFinishColor();
    @Nullable
    private PositionMarkerInlay mPositionMarkerInlay = new PositionMarkerInlayCircle();
    @Nullable
    private String mString = new StringColor();
    @NonNull
    private List<? extends NoteMark> mNoteMarks = new ArrayList<>();

    @NonNull
    private List<Integer> mPositionMarkerInlayFrets = new ArrayList<>();
    @NonNull
    private List<GuitarString> mGuitarStrings = new ArrayList<>();
    @Nullable
    private OnNoteClickListener mOnNoteClickListener;

    //x - fret index, y - string index
    @NonNull
    private final Point mTouchedNote = new Point();

    private final ValueAnimator mValueAnimator = new ValueAnimator();
    private float mAnimationValue = 0f;
    private long mAnimationDuration = 300L;

    @NonNull
    private List<? extends NoteMark> mOldNoteMarks = new ArrayList<>();


    //==================================================================================//
    // Constructors
    //==================================================================================//
    public NeckView(Context context) {
        super(context, null, 0);
    }

    public NeckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NeckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NeckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr);
    }

    //==================================================================================//
    //==================================================================================//
    protected void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            initAttributes(attrs, defStyleAttr);
        }
    }

    //==================================================================================//
    // Extracts attributes set in layout XML
    //==================================================================================//
    protected void initAttributes(@NonNull AttributeSet attrs, int defStyleAttr) {
        TypedArray attributes = getContext().obtainStyledAttributes(attrs, R.styleable.NeckView, defStyleAttr, 0);

        mNutWidth = attributes.getDimension(R.styleable.NeckView_nv_nutWidth, mNutWidth);
        mFretWidth = attributes.getDimension(R.styleable.NeckView_nv_fretWidth, mFretWidth);
        mLastFretPadding = attributes.getDimension(R.styleable.NeckView_nv_lastFretPadding, mLastFretPadding);
        float finishWidth = attributes.getDimension(R.styleable.NeckView_nv_finishWidth, -1);
        if (finishWidth > 0) {
            mTopFinishWidth = finishWidth;
            mBottomFinishWidth = finishWidth;
        }
        mTopFinishWidth = attributes.getDimension(R.styleable.NeckView_nv_topFinishWidth, mTopFinishWidth);
        mBottomFinishWidth = attributes.getDimension(R.styleable.NeckView_nv_bottomFinishWidth, mBottomFinishWidth);
        mLeftHanded = attributes.getBoolean(R.styleable.NeckView_nv_leftHanded, mLeftHanded);
        mDrawZeroFret = attributes.getBoolean(R.styleable.NeckView_nv_drawZeroFret, mDrawZeroFret);
        mFretCount = attributes.getInt(R.styleable.NeckView_nv_fretCount, mFretCount);
        mAnimationDuration = attributes.getInt(R.styleable.NeckView_nv_animationDuration, (int) mAnimationDuration);

        attributes.recycle();
    }

    //==================================================================================//
    public int getFretCount() {
        return mFretCount;
    }

    public void setFretCount(int fretCount) {
        mFretCount = fretCount;
    }

    //==================================================================================//
    public boolean isDrawZeroFret() {
        return mDrawZeroFret;
    }

    public void setDrawZeroFret(boolean drawZeroFret) {
        mDrawZeroFret = drawZeroFret;
    }

    //==================================================================================//
    public float getNutWidth() {
        return mNutWidth;
    }

    public void setNutWidth(float nutWidth) {
        mNutWidth = nutWidth;
    }

    //==================================================================================//
    public float getFretWidth() {
        return mFretWidth;
    }

    public void setFretWidth(float fretWidth) {
        mFretWidth = fretWidth;
    }

    //==================================================================================//
    public float getLastFretPadding() {
        return mLastFretPadding;
    }

    public void setLastFretPadding(float lastFretPadding) {
        mLastFretPadding = lastFretPadding;
    }

    //==================================================================================//
    public float getTopFinishWidth() {
        return mTopFinishWidth;
    }

    public void setTopFinishWidth(float topFinishWidth) {
        mTopFinishWidth = topFinishWidth;
    }

    public float getBottomFinishWidth() {
        return mBottomFinishWidth;
    }

    public void setBottomFinishWidth(float bottomFinishWidth) {
        mBottomFinishWidth = bottomFinishWidth;
    }

    //Sets both top and bottom finish with.
    public void setFinishWith(float finishWidth) {
        setTopFinishWidth(finishWidth);
        setBottomFinishWidth(finishWidth);
    }

    //==================================================================================//
    @Nullable
    public Fretboard getFretboard() {
        return mFretboard;
    }

    public void setFretboard(@Nullable Fretboard fretboard) {
        mFretboard = fretboard;
    }

    //==================================================================================//
    @Nullable
    public Nut getNut() {
        return mNut;
    }

    public void setNut(@Nullable Nut nut) {
        mNut = nut;
    }

    //==================================================================================//
    @Nullable
    public Fret getFret() {
        return mFret;
    }

    public void setFret(@Nullable Fret fret) {
        mFret = fret;
    }

    //==================================================================================//
    @Nullable
    public SideFinish getSideFinish() {
        return mSideFinish;
    }

    public void setSideFinish(@Nullable SideFinish sideFinish) {
        mSideFinish = sideFinish;
    }

    //==================================================================================//
    @Nullable
    public PositionMarkerInlay getPositionMarkerInlay() {
        return mPositionMarkerInlay;
    }

    public void setPositionMarkerInlay(@Nullable PositionMarkerInlay positionMarkerInlay) {
        mPositionMarkerInlay = positionMarkerInlay;
    }

    //==================================================================================//
    @Nullable
    public String getString() {
        return mString;
    }

    public void setString(@Nullable String string) {
        mString = string;
    }

    //==================================================================================//
    @NonNull
    public List<? extends NoteMark> getNoteMarks() {
        return mNoteMarks;
    }

    public void setNoteMarks(@NonNull List<? extends NoteMark> noteMarks) {
        mNoteMarks = noteMarks;
    }

    //==================================================================================//
    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void setNoteMarksWithAnimation(@NonNull List<? extends  NoteMark> noteMarks) {
        mOldNoteMarks = new ArrayList<>(mNoteMarks);
        mNoteMarks = new ArrayList<>(noteMarks);

        mValueAnimator.setDuration(mAnimationDuration);
        mValueAnimator.setFloatValues(0f, 1f);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                mAnimationValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        mValueAnimator.start();
    }

    //==================================================================================//
    @NonNull
    public List<Integer> getPositionMarkerInlayFrets() {
        return mPositionMarkerInlayFrets;
    }

    public void setPositionMarkerInlayFrets(@NonNull List<Integer> positionMarkerInlayFrets) {
        mPositionMarkerInlayFrets = positionMarkerInlayFrets;
    }

    //==================================================================================//
    @NonNull
    public List<GuitarString> getGuitarStrings() {
        return mGuitarStrings;
    }

    public void setGuitarStrings(@NonNull List<GuitarString> guitarStrings) {
        mGuitarStrings = guitarStrings;
    }

    //==================================================================================//
    @Nullable
    public OnNoteClickListener getOnNoteClickListener() {
        return mOnNoteClickListener;
    }

    public void setOnNoteClickListener(@Nullable OnNoteClickListener onNoteClickListener) {
        mOnNoteClickListener = onNoteClickListener;
    }

    //==================================================================================//
    public void setupGuitarStrings(int stringCount, int woundedCount, float minWidth, float maxWidth) {
        float widthDifference = (maxWidth - minWidth) / stringCount;
        mGuitarStrings = new ArrayList<>();
        for (int i = stringCount - 1; i >= 0; i--) {
            GuitarString string = new GuitarString(maxWidth - widthDifference * i, i < woundedCount);
            mGuitarStrings.add(string);
        }
    }

    public void setupGuitarStrings(int stringCount, int woundedCount, @DimenRes int minWidth, @DimenRes int maxWidth) {
        float minWidthPixel = getResources().getDimension(minWidth);
        float maxWidthPixel = getResources().getDimension(maxWidth);

        setupGuitarStrings(stringCount, woundedCount, minWidthPixel, maxWidthPixel);
    }

    //==================================================================================//
    public boolean isLeftHanded() {
        return mLeftHanded;
    }

    public void setLeftHanded(boolean leftHanded) {
        mLeftHanded = leftHanded;
    }

    //==================================================================================//
    //==================================================================================//
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mBounds.set(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
    }

    //==================================================================================//
    //==================================================================================//
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mFretboardBounds.set(mBounds);
        if (mLeftHanded) {
            mFretboardBounds.right -= mNutWidth;
        } else {
            mFretboardBounds.left += mNutWidth;
        }
        mFretPositions = FretCalculator.calculate(mFretboardBounds.width() - mLastFretPadding, mFretCount, true, false);
        drawFretboard(canvas);
        drawNut(canvas);
        drawSideFinish(canvas);
        drawPositionMarkerInlay(canvas);
        drawFrets(canvas);
        drawStrings(canvas);
        drawNoteMarks(canvas);
    }

    //==================================================================================//
    //==================================================================================//
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mOnNoteClickListener == null) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchedNote.x = findFretWithX(mFretPositions, event.getX());
                mTouchedNote.y = findStringWithY(event.getY());
                break;
            case MotionEvent.ACTION_UP:
                int touchedFret = findFretWithX(mFretPositions, event.getX());
                int touchedString = findStringWithY(event.getY());
                if (touchedFret == mTouchedNote.x && touchedString == mTouchedNote.y) {
                    mOnNoteClickListener.onNoteClick(touchedFret, touchedString);
                }
                mTouchedNote.set(-1, -1);
                break;
        }

        return true;
    }

    //==================================================================================//
    private void drawFretboard(Canvas canvas) {
        if (mFretboard == null) return;
        calculateFretboardTopBounds(mDrawBounds);
        mFretboard.draw(getContext(), canvas, mDrawBounds);
    }

    //==================================================================================//
    private void drawSideFinish(Canvas canvas) {
        if (mSideFinish == null) return;
        calculateFretboardFinishBounds(mDrawBounds, FretboardFinishPosition.TOP);
        mSideFinish.draw(getContext(), canvas, mDrawBounds);
        calculateFretboardFinishBounds(mDrawBounds, FretboardFinishPosition.BOTTOM);
        mSideFinish.draw(getContext(), canvas, mDrawBounds);
    }

    //==================================================================================//
    private void drawNut(Canvas canvas) {
        if (mNut == null) return;
        calculateNutBounds(mDrawBounds);
        mNut.draw(getContext(), canvas, mDrawBounds);
    }

    //==================================================================================//
    private void drawFrets(Canvas canvas) {
        if (mFret == null) return;

        for (int i = mDrawZeroFret ? 0 : 1; i < mFretCount; i++) {
            calculateFretBounds(mDrawBounds, mFretPositions[i]);
            mFret.draw(getContext(), canvas, mDrawBounds);
        }
    }

    //==================================================================================//
    private void drawPositionMarkerInlay(Canvas canvas) {
        if (mPositionMarkerInlay == null) return;
        for (int fretIndex : mPositionMarkerInlayFrets) {
            if (fretIndex < mFretCount) {
                calculateFretboardBindingBounds(mDrawBounds, mFretPositions, fretIndex);
                mPositionMarkerInlay.draw(getContext(), canvas, mDrawBounds, fretIndex, mLeftHanded);
            }
        }
    }

    //==================================================================================//
    private void drawStrings(Canvas canvas) {
        if (mString == null) return;
        for (int i = 0; i < mGuitarStrings.size(); i++) {
            calculateStringBounds(mDrawBounds, i);
            GuitarString string = mGuitarStrings.get(i);
            mString.draw(getContext(), canvas, mDrawBounds, string.isWounded());
        }
    }

    //==================================================================================//
    private void drawNoteMarks(Canvas canvas) {
        if (!mOldNoteMarks.isEmpty() && !mNoteMarks.isEmpty() && isMarksAnimatable(mOldNoteMarks) && isMarksAnimatable(mNoteMarks)) {
            drawAnimatableNoteMarks(canvas);
        } else {
            drawNotAnimatableNoteMarks(canvas);
        }
    }

    private void drawAnimatableNoteMarks(Canvas canvas) {
        for (int i = 0; i < Math.max(mOldNoteMarks.size(), mNoteMarks.size()); i++) {
            NoteMarkAnimatable oldMark = mOldNoteMarks.size() > i ? (NoteMarkAnimatable) mOldNoteMarks.get(i) : null;
            NoteMarkAnimatable newMark = mNoteMarks.size() > i ? (NoteMarkAnimatable) mNoteMarks.get(i) : null;

            if (oldMark == null && newMark != null) {
                drawNoteMark(canvas, newMark);
                continue;
            } else if (newMark == null || newMark.getFret() >= mFretCount) {
                continue;
            } else if (oldMark.getFret() >= mFretCount) {
                drawNoteMark(canvas, newMark);
                continue;
            }

            calculateNoteMarkBounds(mOldDrawBounds, mFretPositions, oldMark.getFret(), oldMark.getString());
            calculateNoteMarkBounds(mDrawBounds, mFretPositions, newMark.getFret(), newMark.getString());

            if (newMark.getClass() == oldMark.getClass()) {
                newMark.draw(getContext(), canvas, mOldDrawBounds, mDrawBounds, mAnimationValue, oldMark);
            }
        }
    }

    private void drawNotAnimatableNoteMarks(Canvas canvas) {
        for (NoteMark mark : mNoteMarks) {
            drawNoteMark(canvas, mark);
        }
    }

    private boolean isMarksAnimatable(List<? extends NoteMark> marks) {
        for (NoteMark mark : marks) {
            if (!(mark instanceof NoteMarkAnimatable)) return false;
        }

        return true;
    }

    private void drawNoteMark(Canvas canvas, NoteMark noteMark) {
        calculateNoteMarkBounds(mDrawBounds, mFretPositions, noteMark.getFret(), noteMark.getString());
        noteMark.draw(getContext(), canvas, mDrawBounds);
    }

    //==================================================================================//
    protected void calculateFretboardTopBounds(RectF source) {
        source.set(mFretboardBounds);
        source.top += mTopFinishWidth;
        source.bottom -= mBottomFinishWidth;
    }

    //==================================================================================//
    protected void calculateFretboardFinishBounds(RectF source, FretboardFinishPosition position) {
        source.left = mFretboardBounds.left;
        source.right = mBounds.right;

        switch (position) {
            case TOP:
                source.top = mFretboardBounds.top;
                source.bottom = source.top + mTopFinishWidth;
                break;
            case BOTTOM:
                source.top = mFretboardBounds.bottom - mBottomFinishWidth;
                source.bottom = mFretboardBounds.bottom;
                break;
        }
    }

    //==================================================================================//
    protected void calculateNutBounds(RectF source) {
        source.set(mBounds);
        if (mLeftHanded) {
            source.left = mBounds.right - mNutWidth;
        } else {
            source.right = mBounds.left + mNutWidth;
        }
    }

    //==================================================================================//
    protected void calculateFretBounds(RectF source, float fretCenterX) {
        if (mLeftHanded) fretCenterX = mFretboardBounds.right - fretCenterX;

        source.left = mFretboardBounds.left + fretCenterX - mFretWidth / 2;
        source.top = mFretboardBounds.top;
        source.right = source.left + mFretWidth;
        source.bottom = mFretboardBounds.bottom;
    }

    //==================================================================================//
    protected void calculateFretboardBindingBounds(RectF source, float[] frets, int fretIndex) {
        if (fretIndex == 0) throw new IllegalArgumentException("Can't draw binding on zero fret");

        float leftFretCenterX = mLeftHanded ? mFretboardBounds.right - frets[fretIndex] : frets[fretIndex - 1];
        float rightFretCenterX = mLeftHanded ? mFretboardBounds.right - frets[fretIndex - 1] : frets[fretIndex];

        source.left = mFretboardBounds.left + leftFretCenterX + mFretWidth / 2;
        source.top = mFretboardBounds.top + mTopFinishWidth;
        source.right = mFretboardBounds.left + rightFretCenterX - mFretWidth / 2;
        source.bottom = mFretboardBounds.bottom - mTopFinishWidth;

        if (fretIndex == 1 && !mDrawZeroFret && !mLeftHanded) {
            source.left -= mFretWidth / 2;
        } else if (fretIndex == 1 && !mDrawZeroFret && mLeftHanded) {
            source.right += mFretWidth / 2;
        }
    }

    //==================================================================================//
    protected void calculateStringBounds(RectF source, int stringIndex) {
        float heightForString = mFretboardBounds.height() / mGuitarStrings.size();
        float stringCenterY = mFretboardBounds.top + stringIndex * heightForString + heightForString / 2;
        float stringWidth = mGuitarStrings.get(stringIndex).getWidth();

        source.left = mBounds.left;
        source.top = stringCenterY - stringWidth / 2;
        source.right = mBounds.right;
        source.bottom = stringCenterY + stringWidth / 2;
    }

    //==================================================================================//
    protected void calculateNoteMarkBounds(RectF source, float[] frets, int fretIndex, int stringIndex) {
        float heightForString = mFretboardBounds.height() / mGuitarStrings.size();

        if (mLeftHanded) {
            source.left = fretIndex == 0 ? mFretboardBounds.right : mFretboardBounds.right - frets[fretIndex] - mFretWidth / 2;
            source.top = mBounds.top + heightForString * stringIndex;
            source.right = fretIndex == 0 ? mFretboardBounds.right : mFretboardBounds.right - frets[fretIndex - 1] + mFretWidth / 2;
            source.bottom = source.top + heightForString;
        } else {
            source.left = fretIndex == 0 ? mFretboardBounds.left : mFretboardBounds.left + frets[fretIndex - 1] + mFretWidth / 2;
            source.top = mBounds.top + heightForString * stringIndex;
            source.right = fretIndex == 0 ? mFretboardBounds.left : mFretboardBounds.left + frets[fretIndex] - mFretWidth / 2;
            source.bottom = source.top + heightForString;
        }
    }

    //==================================================================================//
    private int findFretWithX(float[] frets, float x) {
        return mLeftHanded ? findFretWithXLeftHanded(frets, x) : findFretWithXRightHanded(frets, x);
    }

    private int findFretWithXLeftHanded(float[] frets, float x) {
        if (x > mFretboardBounds.right) return 0;

        for (int i = 0; i < frets.length; i++) {
            if (mFretboardBounds.right - frets[i] < x) return i;
        }

        return frets.length;
    }

    private int findFretWithXRightHanded(float[] frets, float x) {
        if (x < mFretboardBounds.left) return 0;

        for (int i = 0; i < frets.length; i++) {
            if (frets[i] + mFretboardBounds.left > x) return i;
        }

        return frets.length;
    }

    //==================================================================================//
    private int findStringWithY(float y) {
        float heightForString = mFretboardBounds.height() / mGuitarStrings.size();

        return (int) (y / heightForString);
    }

    //==================================================================================//
    public interface OnNoteClickListener {
        void onNoteClick(int fret, int string);
    }
}
