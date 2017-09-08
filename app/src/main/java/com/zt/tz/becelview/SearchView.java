package com.zt.tz.becelview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by zt on 16-1-6.
 */
public class SearchView extends View {

    private int mScrollStart;//滑动开始点
    private int mScrollEnd;//滑动结束点
    private Scroller mScroller;//滑动的辅助类

    private int mLastY;//最后的位置
    private int mStartY;//最开始的Y的位置
    private int mAccumulatedY = 0;//增加的y
    private VelocityTracker mVelocityTracker2;//加速度检测
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mPath = new Path();
    private int width;
    private boolean isRunning = true;
    private OnScrollListener mScrollListener;
    private int detaY = 0;
    private boolean isWantSearch=false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(isWantSearch){
                if(mAccumulatedY>0&&mAccumulatedY < 2000){
                    Log.e("isWantSearch", isWantSearch + "," + mAccumulatedY);
                    mAccumulatedY +=60;
                    mHandler.sendEmptyMessageDelayed(0, 10);
                }else{
                    mAccumulatedY=0;
                    detaY=0;
                }
            }else{
                if (mAccumulatedY < 300 && mAccumulatedY > 0) {
                    mAccumulatedY -= 5;
                    mHandler.sendEmptyMessageDelayed(0, 10);
                } else if (mAccumulatedY >= 300 && mAccumulatedY < 2000) {
                    isWantSearch=true;
//                    mAccumulatedY += 20;
                    mHandler.sendEmptyMessageDelayed(0, 10);
                } else {
                    mAccumulatedY = 0;
//                    if(mAccumulatedY==0){
                        if(detaY>0){
                            detaY-=4;
                            mHandler.sendEmptyMessageDelayed(0,10);
                        }else{
                            detaY=0;
                        }
//                    }
                }
            }
            invalidate();
        }
    };

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(30);

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        Log.e("++", mAccumulatedY + "," + getMeasuredWidth());
        canvas.save();
        mPaint.setColor(Color.TRANSPARENT);
        canvas.drawPaint(mPaint);
        canvas.restore();
        canvas.save();
        mPaint.setColor(Color.RED);
        mPaint.setAlpha(255);
        width = getMeasuredWidth();
        int h = 40;
        Path pathSearch = new Path();
        pathSearch.moveTo(0, 0);
        pathSearch.lineTo(0, mAccumulatedY);
        pathSearch.quadTo(width / 8 - h, detaY / 4 + mAccumulatedY, width / 4, detaY / 2 + mAccumulatedY);//D
        pathSearch.quadTo(width * 3 / 8 + h, detaY * 3 / 4 + mAccumulatedY, width / 2, detaY + mAccumulatedY);//C
        pathSearch.quadTo(width / 2 + h, detaY * 3 / 4 + mAccumulatedY, width * 3 / 4, detaY / 2 + mAccumulatedY);//E
        pathSearch.quadTo(width * 7 / 8 + h, detaY * 1 / 4 + mAccumulatedY, width, mAccumulatedY);//B
        pathSearch.lineTo(width, 0);
        pathSearch.close();
        canvas.drawPath(pathSearch, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //计算宽
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        //高模型
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //计算高
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(specWidth, specHeight);
        } else {
            try {
                throw new Exception("this view must be match parent");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        obtainVelocity(event);//初始加速度检测
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                detaY=200;
//                mStartY = (int) event.getY();
//            case MotionEvent.ACTION_MOVE:
//                int moveY = (int) event.getY();
//                detaY=moveY-mStartY;
//                if(detaY<200){
//                    detaY=moveY-mStartY;
//                    mAccumulatedY=0;
//                }else{
//                    detaY=200;
//                    mAccumulatedY = moveY - mStartY-detaY;
//                }
//                invalidate();
//                break;
//            case MotionEvent.ACTION_UP:
//                if(getVelocity()>600){
//                    isWantSearch=true;
//                    mAccumulatedY=1;
//                    detaY=200;
//                }else {
//                    isWantSearch=false;
//                }
//                mHandler.sendEmptyMessage(0);
//                recycleVelocity();
//                break;
//        }
//
//        return true;
//    }

    /**
     * 回收VelocityTracker
     */
    private void recycleVelocity() {
        if (mVelocityTracker2 != null) {
            mVelocityTracker2.recycle();
            mVelocityTracker2 = null;
        }
    }

    private int getVelocity() {
        mVelocityTracker2.computeCurrentVelocity(1000);
        //获取y方向加速度
        return (int) mVelocityTracker2.getYVelocity();
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mScrollListener = onScrollListener;
    }

    //初始化速度检测器
    public void obtainVelocity(MotionEvent event) {
        if (mVelocityTracker2 == null) {
            mVelocityTracker2 = VelocityTracker.obtain();
        }
        mVelocityTracker2.addMovement(event);
    }

    public void eventDown(MotionEvent event) {
        Log.e("SEARECH", "DOWN");
        obtainVelocity(event);
        detaY=200;
        mStartY = (int) event.getY();
    }

    public void eventMove(MotionEvent event) {
        Log.e("SEARECH","MOVE");
        int moveY = (int) event.getY();
        detaY=moveY-mStartY;
        if(detaY<200){
            detaY=moveY-mStartY;
            mAccumulatedY=0;
        }else{
            detaY=200;
            mAccumulatedY = moveY - mStartY-detaY;
        }
        if (mAccumulatedY > 0 && mAccumulatedY < 200) {
            if(mScrollListener!=null){
                mScrollListener.scrollUp();
            }

        }
        if (mAccumulatedY >= 200) {
            if(mScrollListener!=null){
                mScrollListener.scrollDown();
            }
        }
        invalidate();
    }

    public void eventUp(MotionEvent event) {
        Log.e("SEARECH","UP");
        if(getVelocity()>600){
            isWantSearch=true;
            mAccumulatedY=1;
            detaY=200;
        }else {
            isWantSearch=false;
        }
        mHandler.sendEmptyMessage(0);
        recycleVelocity();
    }

    public interface OnScrollListener {
        public void scrollDown();

        public void scrollUp();
    }

}
