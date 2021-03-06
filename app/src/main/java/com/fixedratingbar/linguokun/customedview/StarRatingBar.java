package com.fixedratingbar.linguokun.customedview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RatingBar;

import com.fixedratingbar.linguokun.fixedratingbar.R;


/**
 * @author linguokun
 * @packageName com.example.linguokun.myapplication.customedview
 * @description
 * @date 16/6/29
 */
/**星星评分控件*/
public class StarRatingBar extends RatingBar {

    private int mNumStars;//quantity of icones
    private int mBgBitmapResourceId;//Foreground drawable
    private int mPreBitmapResourceId;//Background drawable
    private Bitmap mBgBitmap;
    private Bitmap mPreBitmap;
    private float mHorizontalSpace;//The spacing among icons

    public StarRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StarRatingBar);

        mNumStars = a.getInt(R.styleable.StarRatingBar_numStars,5);
        mHorizontalSpace = a.getDimension(R.styleable.StarRatingBar_horizontal_space, 0);

        mBgBitmapResourceId = a.getResourceId(R.styleable.StarRatingBar_bg_drawable, -1);
        mBgBitmap = BitmapFactory.decodeResource(getResources(), mBgBitmapResourceId);

        mPreBitmapResourceId = a.getResourceId(R.styleable.StarRatingBar_pre_drawable, -1);
        mPreBitmap = BitmapFactory.decodeResource(getResources(), mPreBitmapResourceId);
        a.recycle();
    }

    public StarRatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StarRatingBar(Context context) {
        super(context);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth = (int) (mBgBitmap.getWidth() * mNumStars + (mNumStars -1)* mHorizontalSpace+getPaddingLeft()+getPaddingRight());

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;

        if(widthMode == MeasureSpec.EXACTLY){//按当前模式测出的值来取值
            width = widthSize;
        }else if(widthMode == MeasureSpec.AT_MOST){
            width = Math.min(desiredWidth, widthSize);//不能大于 按期望宽度与当前模式测出来的宽度取较小值
        }else{
            width = desiredWidth;//包裹 按期望宽度取值
        }

        int height;
        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else if(heightMode == MeasureSpec.AT_MOST){
            height = mBgBitmap.getHeight()+getPaddingTop()+getPaddingBottom();
        }else{
            height = width/mNumStars;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw background Bitmap
        for(int x=0; x<mNumStars; x++){
            canvas.drawBitmap(mBgBitmap, getPaddingLeft()+mBgBitmap.getWidth() * x + mHorizontalSpace * x, getPaddingTop(), null);
        }
        //Draw foreground Bitmap
        for(int x=0; x<mRating; x++){
            canvas.drawBitmap(mPreBitmap, getPaddingLeft()+mPreBitmap.getWidth() * x + mHorizontalSpace * x, getPaddingTop(), null);
        }
    }

    int mRating;
    int moveX;
    int lastMoveX;
    int lastX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN:
            case  MotionEvent.ACTION_MOVE:
                moveX = (int) event.getX();
                for (int x = 0; x <= mNumStars; x++) {
                    if (moveX < (getPaddingLeft() + mBgBitmap.getWidth() * x + mHorizontalSpace * x)) {
                        if (mRating != x) {//excuting when it is different from last rating.
                            if(moveX != lastMoveX || event.getAction() == MotionEvent.ACTION_DOWN) {
                                mRating = x;
                            }
                        }else{//excuting when it equal to last rating.
                            if(event.getAction() == MotionEvent.ACTION_DOWN){
                                    mRating = x - 1;
                            }
                        }
                        if(x != lastX){//防止在滑动过程中不断刷新,只有在x != lastX的情况下才重绘
                            invalidate();
                            if(mOnRatingChangeListener != null){
                                mOnRatingChangeListener.onRatingChange(mRating);
                            }
                            lastX = x;
                        }
                        break;
                    }
                }
                lastMoveX = moveX;
                break;
            case  MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    @SuppressWarnings("unused")
    public void setRating(int rating){
        mRating = rating;
    }

    @SuppressWarnings("unused")
    public int getRating(int rating){
        return mRating;
    }

    @Override
    public int getNumStars() {
        return mNumStars;
    }

    @Override
    public void setNumStars(int numStars) {
        mNumStars = numStars;
    }

    public interface OnRatingChangeListener{
        void onRatingChange(int rating);
    }

    OnRatingChangeListener mOnRatingChangeListener;

    @SuppressWarnings("unused")
    public void setOnRatingChangeListener(OnRatingChangeListener listener){
        mOnRatingChangeListener = listener;
    }
    @SuppressWarnings("unused")
    public OnRatingChangeListener getOnRatingChangeListener(){
        return mOnRatingChangeListener;
    }
}
