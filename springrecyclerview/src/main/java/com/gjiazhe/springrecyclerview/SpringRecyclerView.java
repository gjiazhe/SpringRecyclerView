package com.gjiazhe.springrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.TempRecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

/**
 * Created by gjz on 17/11/2016.
 */

public class SpringRecyclerView extends TempRecyclerView {
    private static final int STATE_NORMAL = 0;
    private static final int STATE_DRAG_TOP = 1;
    private static final int STATE_DRAG_BOTTOM = 2;
    private static final int STATE_SPRING_BACK = 3;
    private static final int STATE_FLING = 4;
    private int mState = STATE_NORMAL;

    private static final int DEF_RELEASE_BACK_ANIM_DURATION = 300; // ms
    private static final int DEF_FLING_BACK_ANIM_DURATION = 300;
    private int mReleaseBackAnimDuration;
    private int mFlingBackAnimDuration;

    private static final int INVALID_POINTER = -1;

    private final int mTouchSlop;
    private float mLastMotionY;
    private float mFrom;
    private float mOffsetY;
    private int mActivePointerId = INVALID_POINTER;

    private boolean mEnableSpringEffectWhenDrag;
    private boolean mEnableSpringEffectWhenFling;

    private Animation springAnimation;
    private Interpolator releaseBackAnimInterpolator;
    private Interpolator flingBackAnimInterpolator;

    public SpringRecyclerView(Context context) {
        this(context, null);
    }

    public SpringRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpringRecyclerView);
        mReleaseBackAnimDuration = a.getInt(R.styleable.SpringRecyclerView_srv_releaseBackAnimDuration, DEF_RELEASE_BACK_ANIM_DURATION);
        mFlingBackAnimDuration = a.getInt(R.styleable.SpringRecyclerView_srv_flingBackAnimDuration, DEF_FLING_BACK_ANIM_DURATION);
        mEnableSpringEffectWhenDrag = a.getBoolean(R.styleable.SpringRecyclerView_srv_enableSpringEffectWhenDrag, true);
        mEnableSpringEffectWhenFling = a.getBoolean(R.styleable.SpringRecyclerView_srv_enableSpringEffectWhenFling, true);
        a.recycle();

        initAnimation();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mEnableSpringEffectWhenDrag && onInterceptTouchEventInternal(ev)) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean onInterceptTouchEventInternal(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                // If STATE_SPRING_BACK, we intercept the event and stop the animation.
                // If STATE_FLING, we do not intercept and allow the animation to finish.
                if (mState == STATE_SPRING_BACK) {
                    if (mOffsetY != 0) {
                        clearAnimation();
                        setState(mOffsetY > 0 ? STATE_DRAG_TOP : STATE_DRAG_BOTTOM);
                    } else {
                        setState(STATE_NORMAL);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    break;
                }
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex == -1) {
                    break;
                }
                final float y = ev.getY(pointerIndex);
                final float yDiff = y - mLastMotionY;
                mLastMotionY = y;
                if (!isDragged()) {
                    boolean canScrollUp, canScrollDown;
                    final int offset = super.computeVerticalScrollOffset();
                    final int range = super.computeVerticalScrollRange() - super.computeVerticalScrollExtent();
                    if (range == 0) {
                        canScrollDown = canScrollUp = false;
                    } else {
                        canScrollUp = offset > 0;
                        canScrollDown = offset < (range - 1);
                    }
                    if (canScrollUp && canScrollDown) {
                        break;
                    }
                    if ((Math.abs(yDiff) > mTouchSlop)) {
                        boolean isOverScroll = false;
                        if (!canScrollUp && yDiff > 0) {
                            setState(STATE_DRAG_TOP);
                            isOverScroll = true;
                        } else if (!canScrollDown && yDiff < 0) {
                            setState(STATE_DRAG_BOTTOM);
                            isOverScroll = true;
                        }
                        if (isOverScroll) {
                            // Prevent touch effect on item
                            MotionEvent fakeCancelEvent = MotionEvent.obtain(ev);
                            fakeCancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                            super.onTouchEvent(fakeCancelEvent);
                            fakeCancelEvent.recycle();
                            super.awakenScrollBars();

                            final ViewParent parent = getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER;
                break;
        }
        return isDragged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mEnableSpringEffectWhenDrag && onTouchEventInternal(ev)) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private boolean onTouchEventInternal(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mActivePointerId = ev.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == INVALID_POINTER) {
                    break;
                }
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                if (pointerIndex < 0) {
                    break;
                }
                final float y = ev.getY(pointerIndex);
                final float yDiff = y - mLastMotionY;
                mLastMotionY = y;
                if (!isDragged()) {
                    boolean canScrollUp, canScrollDown;
                    final int offset = super.computeVerticalScrollOffset();
                    final int range = super.computeVerticalScrollRange()
                            - super.computeVerticalScrollExtent();
                    if (range == 0) {
                        canScrollDown = canScrollUp = false;
                    } else {
                        canScrollUp = offset > 0;
                        canScrollDown = offset < (range - 1);
                    }
                    if (canScrollUp && canScrollDown) {
                        break;
                    }

                    if ((Math.abs(yDiff) >= mTouchSlop)) {
                        boolean isOverScroll = false;
                        if (!canScrollUp && yDiff > 0) {
                            setState(STATE_DRAG_TOP);
                            isOverScroll = true;
                        } else if (!canScrollDown && yDiff < 0) {
                            setState(STATE_DRAG_BOTTOM);
                            isOverScroll = true;
                        }
                        if (isOverScroll) {
                            // Prevent touch effect on item
                            MotionEvent fakeCancelEvent = MotionEvent.obtain(ev);
                            fakeCancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                            super.onTouchEvent(fakeCancelEvent);
                            fakeCancelEvent.recycle();
                            super.awakenScrollBars();

                            final ViewParent parent = getParent();
                            if (parent != null) {
                                parent.requestDisallowInterceptTouchEvent(true);
                            }
                        }
                    }
                }
                if (isDragged()) {
                    mOffsetY += yDiff;
                    // correct mOffsetY
                    if ((isDraggedTop() && mOffsetY <= 0) || (isDraggedBottom() && mOffsetY >=0)) {
                        setState(STATE_NORMAL);
                        mOffsetY = 0;
                        // return to touch item
                        MotionEvent fakeDownEvent = MotionEvent.obtain(ev);
                        fakeDownEvent.setAction(MotionEvent.ACTION_DOWN);
                        super.onTouchEvent(fakeDownEvent);
                        fakeDownEvent.recycle();
                    }
                    invalidate();
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(ev);
                mLastMotionY = ev.getY(index);
                mActivePointerId = ev.getPointerId(index);
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP: {
                onSecondaryPointerUp(ev);
                final int index = ev.findPointerIndex(mActivePointerId);
                if (index != -1) {
                    mLastMotionY = ev.getY(index);
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mOffsetY != 0) {
                    // Spring back
                    mFrom = mOffsetY;
                    startReleaseAnimation();
                    setState(STATE_SPRING_BACK);
                }
                mActivePointerId = INVALID_POINTER;
            }
        }
        return isDragged();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mState == STATE_NORMAL) {
            super.draw(canvas);
        } else {
            final int sc = canvas.save();

            // scale the canvas vertically
            final int viewHeight = getHeight();
            final float scaleY = 1 + Math.abs(mOffsetY) / viewHeight * 0.3f;
            canvas.scale(1, scaleY, 0, mOffsetY >= 0 ? 0 : (viewHeight + getScrollY()));

            super.draw(canvas);
            canvas.restoreToCount(sc);
        }
    }

    @Override
    protected void absorbGlows(int velocityX, int velocityY) {
        if (mEnableSpringEffectWhenFling && mState != STATE_FLING) {
            mFrom = - velocityY * (1f/60);
            startFlingAnimation();
            setState(STATE_FLING);
        }
    }

    private void initAnimation() {
        springAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                mOffsetY = mFrom * interpolatedTime;
                if (hasEnded()) {
                    mOffsetY = 0;
                    setState(STATE_NORMAL);
                }
                invalidate();
            }
        };

        releaseBackAnimInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return (float) Math.cos(Math.PI * v / 2);
            }
        };

        flingBackAnimInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float v) {
                return (float) Math.sin(Math.PI * v);
            }
        };
    }

    private void startReleaseAnimation() {
        springAnimation.setDuration(mReleaseBackAnimDuration);
        springAnimation.setInterpolator(releaseBackAnimInterpolator);
        startAnimation(springAnimation);
    }

    private void startFlingAnimation() {
        springAnimation.setDuration(mFlingBackAnimDuration);
        springAnimation.setInterpolator(flingBackAnimInterpolator);
        startAnimation(springAnimation);
    }

    private void setState(int newState) {
        if (mState != newState) {
            mState = newState;
        }
    }

    private boolean isDragged() {
        return mState == STATE_DRAG_TOP || mState == STATE_DRAG_BOTTOM;
    }

    private boolean isDraggedTop() {
        return mState == STATE_DRAG_TOP;
    }

    private boolean isDraggedBottom() {
        return mState == STATE_DRAG_BOTTOM;
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    public void setEnableSpringEffectWhenDrag(boolean enable) {
        mEnableSpringEffectWhenDrag = enable;
    }

    public void setEnableSpringEffectWhenFling(boolean enable) {
        mEnableSpringEffectWhenFling = enable;
    }

    public void setReleaseBackAnimDuration(int duration) {
        mReleaseBackAnimDuration = duration;
    }

    public void setFlingBackAnimDuration(int duration) {
        mFlingBackAnimDuration = duration;
    }

}
