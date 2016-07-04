package com.answer.waveview.lib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.answer.waveview.R;

import java.lang.ref.WeakReference;

/**
 * Created by jianhaohong on 7/4/16.
 */
public class WaveView extends View {

    private final static int WAVE_LENGTH = 10;
    private final static int WAVE_HEIGHT = 3;
    private final static int SHAPE_STROKE_WIDTH = 2;
    private final static int SHAPE_CIRCLE = 0;
    private final static int SHAPE_RECTANGLE = 1;
    private final static int WAVE_DURATION = 1000;

    private Paint wavePaint;
    private Path wavePath;
    private Paint strokePaint;
    private int waveLength;
    private int waveHeight;
    private int moveDistance;
    private int percentage = 50;
    private boolean isCircle;
    private int animDuration;
    private Path circlePath;
    private boolean isAnimRunning;
    private ValueAnimator animator;

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WaveView);
        waveLength = (int) typedArray.getDimension(R.styleable.WaveView_wave_length, dpToPx(WAVE_LENGTH));
        waveHeight = (int) typedArray.getDimension(R.styleable.WaveView_wave_height, dpToPx(WAVE_HEIGHT));
        int strokeSize = (int) typedArray.getDimension(R.styleable.WaveView_stroke_width, dpToPx(SHAPE_STROKE_WIDTH));
        int strokeColor = typedArray.getColor(R.styleable.WaveView_stroke_color, Color.BLACK);
        int shape = typedArray.getInt(R.styleable.WaveView_shape, SHAPE_CIRCLE);
        animDuration = typedArray.getInt(R.styleable.WaveView_wave_duration, WAVE_DURATION);
        int waveColor = typedArray.getColor(R.styleable.WaveView_fill_color, Color.GRAY);

        isCircle = (shape == SHAPE_CIRCLE);

        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeSize);

        wavePath = new Path();
        wavePaint = new Paint();
        wavePaint.setAntiAlias(true);
        wavePaint.setColor(waveColor);
        wavePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isCircle) {
            if (circlePath == null) {
                circlePath = new Path();
                if (getHeight() > getWidth()) {
                    circlePath.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CW);
                } else {
                    circlePath.addCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, Path.Direction.CW);
                }
            }
            canvas.clipPath(circlePath);
        }

        wavePath.reset();

        int originY;
        if (isCircle) {
            if (getHeight() > getWidth()) {
                originY = (getHeight() / 2 - getWidth() / 2) + (100 - percentage) * getWidth() / 100 - waveHeight;
            } else {
                originY = (100 - percentage) * getHeight() / 100 - waveHeight;
            }

        } else {
            originY = (100 - percentage) * getHeight() / 100 - waveHeight;
        }

        int halfWaveLength = waveLength / 2;
        wavePath.moveTo(-waveLength + moveDistance, originY);
        for (int i = -waveLength; i <= getWidth() + waveLength; i += waveLength) {
            wavePath.rQuadTo(halfWaveLength / 2, -waveHeight, halfWaveLength, 0);
            wavePath.rQuadTo(halfWaveLength / 2, waveHeight, halfWaveLength, 0);
        }
        wavePath.lineTo(getWidth(), getHeight());
        wavePath.lineTo(0, getHeight());
        wavePath.close();
        canvas.drawPath(wavePath, wavePaint);

        if (isCircle) {
            canvas.drawPath(circlePath, strokePaint);
        } else {
            canvas.drawRect(0, 0, getWidth(), getHeight(), strokePaint);
        }
    }

    public void startAnim() {
        if (animator == null) {
            animator = ValueAnimator.ofInt(0, waveLength);
            animator.setDuration(animDuration);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new AnimUpdateListener(this));
        }
        animator.start();
        isAnimRunning = true;
    }

    public void stopAnim() {
        if (animator != null) {
            animator.cancel();
            isAnimRunning = false;
        }
    }

    public boolean isAnimRunning() {
        return isAnimRunning;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        if (percentage > 100) {
            percentage = 100;
        }

        if (percentage < 0) {
            percentage = 0;
        }

        this.percentage = percentage;
        postInvalidate();
    }

    public void updateMoveDistance(int moveDistance) {
        this.moveDistance = moveDistance;
        postInvalidate();
    }

    private int dpToPx(float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
        return (int) px;
    }

    private class AnimUpdateListener implements ValueAnimator.AnimatorUpdateListener {

        private WeakReference<WaveView> waveViewWeakReference;

        public AnimUpdateListener(WaveView waveView) {
            waveViewWeakReference = new WeakReference<>(waveView);
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (waveViewWeakReference != null && waveViewWeakReference.get() != null) {
                int distance = (int) animation.getAnimatedValue();
                waveViewWeakReference.get().updateMoveDistance(distance);
            }
        }
    }
}
