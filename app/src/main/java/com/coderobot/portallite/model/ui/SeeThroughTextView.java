package com.coderobot.portallite.model.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Created by Tony on 2015/3/23.
 */
public class SeeThroughTextView extends FontTextView {
    Bitmap mMaskBitmap;
    Canvas mMaskCanvas;
    Paint mPaint;

    Drawable mBackground;
    Bitmap mBackgroundBitmap;
    Canvas mBackgroundCanvas;
    boolean mSetBoundsOnSizeAvailable = false;

    public SeeThroughTextView(Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        super.setTextColor(Color.BLACK);
        super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public SeeThroughTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        super.setTextColor(Color.BLACK);
        super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public SeeThroughTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        super.setTextColor(Color.BLACK);
        super.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    @Deprecated
    public void setBackgroundDrawable(Drawable bg) {
        mBackground = bg;
        int w = bg.getIntrinsicWidth();
        int h = bg.getIntrinsicHeight();

        // Drawable has no dimensions, retrieve View's dimensions
        if (w == -1 || h == -1) {
            w = getWidth();
            h = getHeight();
        }

        // Layout has not run
        if (w == 0 || h == 0) {
            mSetBoundsOnSizeAvailable = true;
            return;
        }

        mBackground.setBounds(0, 0, w, h);
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        setBackgroundDrawable(new ColorDrawable(color));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBackgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mBackgroundCanvas = new Canvas(mBackgroundBitmap);
        mMaskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mMaskCanvas = new Canvas(mMaskBitmap);

        if (mSetBoundsOnSizeAvailable) {
            mBackground.setBounds(0, 0, w, h);
            mSetBoundsOnSizeAvailable = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw background
        mBackground.draw(mBackgroundCanvas);

        // Draw mask
        mMaskCanvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        super.onDraw(mMaskCanvas);

        mBackgroundCanvas.drawBitmap(mMaskBitmap, 0.f, 0.f, mPaint);
        canvas.drawBitmap(mBackgroundBitmap, 0.f, 0.f, null);
    }
}