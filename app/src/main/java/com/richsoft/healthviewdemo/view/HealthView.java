package com.richsoft.healthviewdemo.view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.richsoft.healthviewdemo.utils.DateUtil;

import java.util.List;

/**
 * 作者：chengjie on 17/3/24 10:46
 * 邮箱：10078216660@qq.com
 * 描述：显示步数View
 */
public class HealthView extends View {
    public static final String TAG = HealthView.class.getSimpleName();

    private int mWidth;//自定义View的宽度
    private int mHeight;//自定义View的高度
    private int mBackgroundCorner;//背景四角的弧度半径
    private int mThemeColor;//主题颜色
    private int mDefaultThemeColor;//默认主题颜色
    private int mBackgroundDefaultColor;//默认背景颜色

    private int steps[];
    private int length;

    private List<String> mDdateList;//最近7天日期

    /*--------------------------------圆弧-----------------------------*/
    private RectF mArcRectF;//圆弧矩形框
    private float mArcWidth;//圆弧宽度
    private float mBarWidth;//圆弧Bar宽度
    private int mArcCenterX;//圆弧中心X坐标
    private int mArcCenterY;//圆弧中心Y坐标
    private Paint mArcPaint;//圆弧画笔
    private float percent = 0.5f;

    /*-------------------------------圆弧里截至时间的文字---------------------------------*/
    private Paint mTextUpToPaint;//截至时间文字画笔
    private int mPosX;//x坐标
    private int mPosUpToY;//截至时间文字y坐标
    private int mTextUpToPaintColor;

    /*-------------------------------圆弧里步数的文字---------------------------------*/
    private Paint mTextStepPaint;//步数画笔
    private int mTextStepPaintColor;//步数文字画笔
    private int mStep;//今日步数

    /*-------------------------------好友平均步数的文字---------------------------------*/
    private Paint mTextAverageStepPaint;//好友平均步数画笔
    private int mTextAveragePaintColor;//好友平均步数画笔颜色
    private int mAverageStep; //平局步数
    private int mAverageStepY;//好友平均步数文字y坐标

    /*------------------------------名次文字-----------------------------------------------*/
    private Paint mTextMinRankingPaint;//名次小字
    private Paint mTextMaxRankingPaint;//名次大字
    private int mMinLRankingX;//名次小字左边x坐标
    private int mMinRRankingX;//名次小字右边y坐标
    private int mRankingY;//名次y坐标
    private int range;

    /*-------------------------------圆弧下面文字-------------------------------------------*/
    private Paint mTextRecentDaysPaint;
    private Paint mTextAveragePaint;
    private int mRecentDaysX;
    private int mAverageX;
    private int mRecentY;

    /*------------------------------绘制虚线-----------------------------------------------*/
    private Paint mDottedLinePaint;//虚线画笔
    private int mDottedLineStartX;//虚线开始x坐标
    private int mDottedLineStopX;//虚线结束x坐标
    private int mDottedLineStartY;//虚线开始y坐标

    /*------------------------------竖形条--------------------------------------------------*/
    private Paint mVerticalBarPaint;//竖条画笔
    private Paint mVerticalTextPaint;//竖条下面日期画笔
    private int mVerticalBarStartY;//竖条y开始坐标
    private int mVerticalBarStopY;//竖条y结束坐标

    /*----------------------------最下层文字画笔---------------------------------------------*/
    private Paint mChampionTextPaint;//冠军文字画笔

    private float mRatio;//自定义View宽高比例

    private Paint mBackgroundPaint;//背景画笔

    public HealthView(Context context) {
        this(context, null);
    }

    public HealthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HealthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mDdateList = DateUtil.getCurrent7Days();

        mRatio = 450.f / 525.f;
        mArcWidth = 20.f / 450.f * mWidth;
        mBarWidth = 16.f / 450.f * mWidth;
        mBackgroundDefaultColor = Color.WHITE;
        //关闭硬件加速,防止某些4.0设备徐虚线显示实线的问题。
        //可以在AndroidManifest.xml中的Application标签加上android:hardwareAccelerated="true",这样整个应用关闭了硬件加速,这样影响性能
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mBackgroundPaint = new Paint();
        //设置抗锯齿
        mBackgroundPaint.setAntiAlias(true);
        //设置画笔颜色
        mBackgroundPaint.setColor(mThemeColor);

        mBackgroundCorner = 10;
        mDefaultThemeColor = Color.parseColor("#2EC3FD");
        mThemeColor = mDefaultThemeColor;

        //圆弧的画笔
        mArcPaint = new Paint();
        mArcPaint.setColor(mThemeColor);//画笔颜色
        mArcPaint.setAntiAlias(true);//设置抗锯齿
        mArcPaint.setStyle(Paint.Style.STROKE);//设置空心
        mArcPaint.setDither(true);//防抖动
        mArcPaint.setStrokeJoin(Paint.Join.ROUND);//在画笔的连接处是圆滑的
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);//在画笔的起始处是圆滑的
        mArcPaint.setPathEffect(new CornerPathEffect(10));//设置画笔效果

        //圆弧里的截至时间文字画笔
        mTextUpToPaint = new Paint();
        mTextUpToPaint.setAntiAlias(true);//抗锯齿
        mTextUpToPaintColor = Color.parseColor("#C1C1C1");
        mTextUpToPaint.setColor(mTextUpToPaintColor);
        mTextUpToPaint.setTextAlign(Paint.Align.CENTER);

        //圆弧里的步数文字画笔
        mTextStepPaint = new Paint();
        mTextStepPaint.setAntiAlias(true);
        mTextStepPaintColor = mThemeColor;
        mTextStepPaint.setColor(mTextStepPaintColor);
        mTextStepPaint.setTextAlign(Paint.Align.CENTER);

        //圆弧里的好友步数画笔
        mTextAverageStepPaint = new Paint();
        mTextAverageStepPaint.setAntiAlias(true);
        mTextAveragePaintColor = Color.parseColor("#C1C1C1");
        mTextAverageStepPaint.setColor(mTextAveragePaintColor);
        mTextAverageStepPaint.setTextAlign(Paint.Align.CENTER);

        //圆弧里名次画笔
        mTextMinRankingPaint = new Paint();
        mTextMinRankingPaint.setAntiAlias(true);//抗锯齿
        mTextMinRankingPaint.setColor(Color.parseColor("#C1C1C1"));
        mTextMinRankingPaint.setTextAlign(Paint.Align.CENTER);
        mTextMaxRankingPaint = new Paint();
        mTextMaxRankingPaint.setAntiAlias(true);
        mTextMaxRankingPaint.setColor(mThemeColor);
        mTextMaxRankingPaint.setTextAlign(Paint.Align.CENTER);

        //圆弧下面最近7天和平均步数画笔
        mTextRecentDaysPaint = new Paint();
        mTextRecentDaysPaint.setAntiAlias(true);//抗锯齿
        mTextRecentDaysPaint.setColor(Color.parseColor("#C1C1C1"));
        mTextRecentDaysPaint.setTextAlign(Paint.Align.LEFT);
        mTextAveragePaint = new Paint();
        mTextAveragePaint.setAntiAlias(true);
        mTextAveragePaint.setColor(Color.parseColor("#C1C1C1"));
        mTextAveragePaint.setTextAlign(Paint.Align.RIGHT);

        //圆弧下面虚线画笔
        mDottedLinePaint = new Paint();
        mDottedLinePaint.setAntiAlias(true);//抗锯齿
        mDottedLinePaint.setColor(Color.parseColor("#C1C1C1"));
        mDottedLinePaint.setStyle(Paint.Style.STROKE);
        mDottedLinePaint.setPathEffect(new DashPathEffect(new float[]{8, 4}, 0));//设置虚线

        //竖条画笔
        mVerticalBarPaint = new Paint();
        mVerticalBarPaint.setAntiAlias(true);// 抗锯齿
        mVerticalBarPaint.setDither(true);//防抖动
        mVerticalBarPaint.setStrokeCap(Paint.Cap.ROUND);//在画笔的起始处是圆滑的
        mVerticalBarPaint.setStrokeJoin(Paint.Join.ROUND);//在画笔的连接处是圆滑的
        //竖条下面的日期文字画笔
        mVerticalTextPaint = new Paint();
        mVerticalTextPaint.setAntiAlias(true);//抗锯齿
        mVerticalTextPaint.setColor(Color.parseColor("#C1C1C1"));
        mVerticalTextPaint.setTextAlign(Paint.Align.CENTER);

        //最下层冠军文字和查看文字画笔
        mChampionTextPaint = new Paint();
        mChampionTextPaint.setAntiAlias(true);
        mChampionTextPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制最下层背景
        mBackgroundPaint.setColor(mThemeColor);
        drawBelowBackgroud(0, mWidth, mWidth, mHeight, mBackgroundCorner, canvas, mBackgroundPaint);
        //绘制上层背景
        mBackgroundPaint.setColor(mBackgroundDefaultColor);
        drawUpBackground(0, 0, mWidth, mWidth, mBackgroundCorner, canvas, mBackgroundPaint);
        //绘制圆弧
        canvas.drawArc(mArcRectF, 120, 300 * percent, false, mArcPaint);
        //绘制圆弧里面的文字
        //setTextSize必须在onDraw()里面,否则文字不显示,原因还没找到
        mTextUpToPaint.setTextSize(15.f / 450.f * mWidth);
        canvas.drawText("截至" + DateUtil.getCurrentHoursAndMin() + "已走", mPosX, mPosUpToY, mTextUpToPaint);
        //绘制圆弧里面的步数文字
        mTextStepPaint.setTextSize(42.f / 450.f * mWidth);
        canvas.drawText(mStep + "", mArcCenterX, mArcCenterY, mTextStepPaint);
        //绘制圆弧里面的好友平均步数
        mTextAverageStepPaint.setTextSize(15.f / 450.f * mWidth);
        canvas.drawText("好友平均" + mAverageStep, mPosX, mAverageStepY, mTextAverageStepPaint);
        //绘制圆弧里面的名次
        mTextMinRankingPaint.setTextSize(15.f / 450.f * mWidth);
        canvas.drawText("第", mMinLRankingX, mRankingY, mTextMinRankingPaint);
        mTextMaxRankingPaint.setTextSize(24.f / 450.f * mWidth);
        canvas.drawText(range + "", mArcCenterX, mRankingY, mTextMaxRankingPaint);
        canvas.drawText("名", mMinRRankingX, mRankingY, mTextMinRankingPaint);
        //绘制圆弧下面的最近天数和平均步数
        mTextRecentDaysPaint.setTextSize(15.f / 450.f * mWidth);
        canvas.drawText("最近7日", mRecentDaysX, mRecentY, mTextRecentDaysPaint);
        mTextAveragePaint.setTextSize(15.f / 450.f * mWidth);
        canvas.drawText("平均" + averageSteps() + "/天", mAverageX, mRecentY, mTextAveragePaint);
        //绘制圆弧下面的虚线
        canvas.drawLine(mDottedLineStartX, mDottedLineStartY, mDottedLineStopX, mDottedLineStartY, mDottedLinePaint);
        //绘制圆弧下面的竖形条和日期
        mVerticalTextPaint.setTextSize(15.f / 450.f * mWidth);
        for (int i = 0; i < length; i++) {
            int barHeight = (int) (steps[i] * 1.f / mAverageStep * 35.f / 525.f * mHeight);
            mVerticalBarStopY = mVerticalBarStartY - barHeight;
            if (steps[i] < averageSteps()) {
                mVerticalBarPaint.setColor(Color.parseColor("#C1C1C1"));
            } else {
                mVerticalBarPaint.setColor(mThemeColor);
            }
            canvas.drawLine(55.f / 450.f * mWidth + i * (57.f / 450.f * mWidth), mVerticalBarStartY, 55.f / 450.f * mWidth + i * (57.f / 450.f * mWidth), mVerticalBarStopY, mVerticalBarPaint);
            canvas.drawText(mDdateList.get(i) + "日", 55.f / 450.f * mWidth + i * (57.f / 450.f * mWidth), mVerticalBarStartY + 25.f / 525.f * mHeight, mVerticalTextPaint);

        }
        //绘制下层文字
        mChampionTextPaint.setTextSize(18.f / 450.f * mWidth);
        mChampionTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("LBJ获得今日冠军", 50.f / 450.f * mWidth, (mHeight - mWidth) / 2.f + mWidth, mChampionTextPaint);
        mChampionTextPaint.setTextSize(15.f / 450.f * mWidth);
        mChampionTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("查看 >", 425f / 450.f * mWidth, (mHeight - mWidth) / 2.f + mWidth, mChampionTextPaint);

    }

    //绘制下层背景
    private void drawBelowBackgroud(int l, int t, int r, int b, int radius, Canvas canvas, Paint paint) {
        Path path = new Path();
        //不绘制,移动画笔,移动至坐标(l,t)
        path.moveTo(l, t);
        //绘制一条直线,默认从坐标(0,0)开始
        path.lineTo(r, t);
        //绘制圆滑曲线,即贝赛尔曲线,quadTo(float x1, float y1, float x2, float y2),(x1,y1)为控制点,(x2,y2)为结束点
        // path.quadTo(r, t, r, t + radius);

        path.lineTo(r, b - radius);
        path.quadTo(r, b, r - radius, b);

        path.lineTo(l + radius, b);
        path.quadTo(l, b, l, b - radius);

        path.lineTo(l, t);

        canvas.drawPath(path, paint);
    }

    //绘制上层背景
    private void drawUpBackground(int l, int t, int r, int b, int radius, Canvas canvas, Paint paint) {
        Path path = new Path();
        path.moveTo(l, t);
        path.lineTo(r - radius, t);
        path.quadTo(r, t, r, t + radius);
        path.lineTo(r, b - radius);
        path.lineTo(l, b - radius);
        path.lineTo(l, t + radius);
        path.quadTo(l, t, l + radius, t);
        canvas.drawPath(path, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultWidth = Integer.MAX_VALUE;
        int width;
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        //如果父ViewGroup的测量模式是精准模式或者最大模式,那么子View的宽度就等于父View的可用宽度
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
            width = widthSize;
        } else {
            width = defaultWidth;
        }
        int defaultHeight = (int) (width * 1.f / mRatio);
        height = defaultHeight;
        setMeasuredDimension(width, height);
        Log.i(TAG, "width=" + width + "|height=" + height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        mArcCenterX = (int) (mWidth / 2.f);
        mArcCenterY = (int) (160.f / 525.f * mHeight);
        mArcRectF = new RectF();
        mArcRectF.left = mArcCenterX - 125.f / 450.f * mWidth;
        mArcRectF.top = mArcCenterY - 125.f / 525.f * mHeight;
        mArcRectF.right = mArcCenterX + 125.f / 450.f * mWidth;
        mArcRectF.bottom = mArcCenterY + 125.f / 525.f * mHeight;
        mArcWidth = 20.f / 450.f * mWidth;
        mBarWidth = 16.f / 450.f * mWidth;
        //画笔的宽度一定要在这里设置才能自适应
        mArcPaint.setStrokeWidth(mArcWidth);

        mPosX = mArcCenterX;
        mPosUpToY = (int) (mArcCenterY - 40.f / 525.f * mHeight);

        mAverageStepY = (int) (mArcCenterY + 50.f / 525.f * mHeight);

        mMinLRankingX = (int) (mArcCenterX - 35.f / 450.f * mWidth);
        mMinRRankingX = (int) (mArcCenterX + 35.f / 450.f * mWidth);
        mRankingY = (int) (mArcCenterY + 120.f / 525.f * mHeight);

        mDottedLineStartX = (int) (25.f / 450.f * mWidth);
        mDottedLineStopX = (int) (mDottedLineStartX + (450.f - 50.f) / 450.f * mWidth);
        mDottedLineStartY = (int) (352.f / 525.f * mHeight);

        mRecentDaysX = (int) (25.f / 450.f * mWidth);
        mAverageX = (int) ((450.f - 25.f) / 450.f * mWidth);
        mRecentY = (int) (320.f / 525.f * mHeight);

        mVerticalBarStartY = (int) (388.f / 525.f * mHeight);

        mVerticalBarPaint.setStrokeWidth(15.f / 450.f * mWidth);

    }

    /**
     * 初始化数据
     */
    public void start(int[] steps, int averageStep, int range) {
        this.steps = steps;
        mAverageStep = averageStep;
        this.range = range;
        //步数动画
        ValueAnimator valueAnimatorStep = ValueAnimator.ofInt(0, steps[steps.length - 1]);
        valueAnimatorStep.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mStep = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        //圆弧动画
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                percent = (float) animation.getAnimatedValue();
                //刷新View,会执行draw()方法
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(3000);
        set.playTogether(valueAnimatorStep, valueAnimator);
        set.start();
    }

    /**
     * 获取平均步数
     *
     * @return 平均步数
     */
    private int averageSteps() {
        int totalSteps = 0;
        length = steps.length;
        for (int i = 0; i < length; i++) {
            totalSteps += steps[i];
        }
        return (int) (totalSteps * 1.f / length);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        RectF rectF = new RectF();
        rectF.left = 380.f / 450.f * mWidth;
        rectF.top = mWidth;
        rectF.right = mWidth;
        rectF.bottom = mHeight;


        //如果点击位置处在查看区域内,则回调点击事件
        if (rectF.contains(event.getX(), event.getY())) {
            if (mOnLookClickListener != null) {
                Log.i(TAG, "onclick");
                mOnLookClickListener.onClick();
            }
            return false;
        } else {
            return super.onTouchEvent(event);
        }
    }

    public interface OnLookClickListener {
        void onClick();
    }

    private OnLookClickListener mOnLookClickListener;

    public void setOnLookClickListener(OnLookClickListener onLookClickListener) {
        this.mOnLookClickListener = onLookClickListener;
    }


}
