package com.yjy.camera.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.yjy.camera.Utils.CameraUtils;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/21
 *     desc   : 录制或者点击拍照按钮
 *     version: 1.0
 * </pre>
 */
public class RecordButton extends View {

    private static final int MSG_WHAT_CALL_RECORD_START = 848;

    /**
     * 用于绘制的相关属性
     */
    private Paint mPaint;
    private Paint mCirclePaint;
    private Paint mProcessPaint;
    private final RectF mRect = new RectF();
    private final Point mCenterPoint = new Point();

    /**
     * 圆环的半径
     */
    private int[] mInnerRadiusRange = new int[2];
    private int[] mOuterRadiusRange = new int[2];
    private int mCurInnerRadius;
    private int mCurOuterRadius;

    /**
     * 录制进度
     */
    private long mMaxDuration = 100;
    private long mCurDuration = 0;

    /**
     * 颜色相关
     */
    private int mProgressColor = Color.parseColor("#ff4db87f");

    private int mInnerCircleColor = Color.parseColor("#ff4db87f");

    private int mOutCircleColor = Color.parseColor("#ff4db87f");

    /**
     * Flags
     */
    private boolean mIsLongClickEnable = false;
    private boolean mIsRecording = false;

    private int mOutCircleStrokeWidth;
    private int mProcessWidth = 0;

    private int mClickTime = 16;

    private int mValidSize = 0;


    public RecordButton(Context context) {
        this(context,null);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(mInnerCircleColor);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setColor(mOutCircleColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);

        mProcessPaint = new Paint();
        mProcessPaint.setAntiAlias(true);
        mProcessPaint.setDither(true);
        mProcessPaint.setColor(mProgressColor);
        mProcessPaint.setStyle(Paint.Style.STROKE);




        mOutCircleStrokeWidth = CameraUtils.dp2px(getContext(),5);
        mProcessWidth = CameraUtils.dp2px(getContext(),10);

        mCirclePaint.setStrokeWidth(mOutCircleStrokeWidth);
        mProcessPaint.setStrokeWidth(mProcessWidth);



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int validWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int validHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mValidSize = Math.min(validWidth, validHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = mValidSize/2;
        int y = mValidSize/2;
        int radius = (mValidSize - mOutCircleStrokeWidth) /2 ;

        int innerRadius = radius - mProcessWidth/2;


        //绘制内圆
        canvas.drawCircle(x,y,innerRadius,mPaint);


        //绘制进度条
        RectF rectF = new RectF(mProcessWidth/2,mProcessWidth/2,
                mValidSize- mProcessWidth/2,mValidSize - mProcessWidth/2);

        canvas.drawArc(rectF,-90,(mCurDuration*360f/mMaxDuration),false,mProcessPaint);


        //绘制外环
        canvas.drawCircle(x,y,radius,mCirclePaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_UP:
                onEnd();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onEnd() {
        setCurrentProgress(0);
    }

    /**
     * 设置录制的最大时长
     */
    public void setMaxProgress(long maxDuration) {
        this.mMaxDuration = maxDuration;
    }


    public void setIsRecording(boolean mIsRecording) {
        this.mIsRecording = mIsRecording;
    }

    public void setCurrentProgress(long curDuration) {
        // 若处于未录制状态, 则无需影响进度更新
        if(!mIsRecording){
            return;
        }
        this.mCurDuration = curDuration;
        if (mCurDuration <= mMaxDuration) {
            // 处理录制结束
            invalidate();
        } else {
            mCurDuration = 0;
        }
    }


}
