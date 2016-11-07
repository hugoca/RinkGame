package com.wangjicheng.rink.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.wangjicheng.rink.R;
import com.wangjicheng.rink.util.ReadResBitmap;

import java.util.Random;


/**
 * Copyright2012-2016  CST.All Rights Reserved
 *
 * Comments：
 *
 * @author huanghj
 *
 *         Time: 2016/9/7 0007
 *
 *         Modified By:
 *         Modified Date:
 *         Why & What is modified:
 * @version 5.0.0
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //屏幕宽高
    public static int SCREEN_WIDTH;

    public static int SCREEN_HEIGHT;


    private SurfaceHolder mSurfaceHolder;

    private Canvas mCanvas;

    //子线程的标志位
    private boolean isDrawing;

    /**
     * 默认矩形画笔
     */
    private Paint mPaint;

    /**
     * 图片画笔
     */
    private Paint mBitmapPaint;

    /**
     * 文本画笔
     */
    private Paint mTextPaint;

    private Paint mRiverPaint;

    /**
     * 桥是否在旋转中
     */
    private boolean isRorate = false;

    /**
     * 初始所在岸边的位置
     */
    private int left = 150;

    private int top = 900;

    private int right = 160;

    private int bottom = 900;

    /**
     * 连接桥
     */
    private Path mPath;

    /**
     * 两岸
     */
    private Rect mFirstBank, mSecondBank;

    private Rect mRiverRect;

    /**
     * 人物bitmap
     */
    private Bitmap mBitmap;

    /**
     * 两岸之间的距离（随机）
     */
    private int mSpaceWidth;

    /**
     * 对岸的岸宽度（随机）
     */
    private int mSecondBankWidth;

    /**
     * 屏幕是否可以触发点击事件
     */
    private boolean mScreenCanPress;

    /**
     * 是否按下
     */
    private boolean isPressed = false;

    /**
     * 桥的结束想x，y坐标
     */
    private int x, y;

    /**
     * 人的位置坐标
     */
    private int personX = 0;

    private int personY = 825;

    /**
     * 人是否在移动
     */
    private boolean personMove = false;

    /**
     * 人物图
     */
    private Bitmap[] mBitmaps;

    /**
     * 图片上裁剪数量
     */
    private int n=0;


    /**
     * 得分
     */
    private int mScore = 0;


    public MySurfaceView(Context context) {
        super(context);
        init(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        setFocusable(true); // 用键盘是否能获得焦点
        setFocusableInTouchMode(true); //是否可以通过触摸获取焦点
        this.setKeepScreenOn(true);  //设置屏幕常亮

        //初始化画笔
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.surface_bg));
        mPaint.setStrokeWidth(15);
        mPaint.setStyle(Paint.Style.STROKE);
        mBitmapPaint = new Paint();
        mBitmapPaint.setStyle(Paint.Style.FILL);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(100);
        mTextPaint.setColor(getResources().getColor(R.color.surface_bg));

        mRiverPaint = new Paint();
        mRiverPaint.setColor(getResources().getColor(R.color.river));
        mRiverPaint.setStyle(Paint.Style.FILL);

        //初始化相关对象和默认值
        mPath = new Path();
        mFirstBank = new Rect();
        mSecondBank = new Rect();
        mRiverRect=new Rect();
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.boy_60);
        mScreenCanPress = true;

        //设置首次进入的对岸
        setSecondBank();

        mBitmaps= ReadResBitmap.generateBitmapArray(context,R.mipmap.spriter,4);
        mBitmap=mBitmaps[n];
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (isDrawing) {
            draw();
            setLine();
            drawFirstBank();
            translate();
            setRersonBitmap();
            setRiver();
            //设置刷新频率，防止一直刷新
            long end = System.currentTimeMillis();
            if (end - start < 100) {
                try {
                    Thread.sleep(100 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 重置参数
     */
    private void reset() {
        bottom = 900;
        right = 160;
        personX = 0;
        personY = 825;
        top = 900;
        left = 150;
        setSecondBank();
        mScreenCanPress = true;
        mPath.reset();
        mCanvas.drawColor(getResources().getColor(R.color.surface_bg));
    }

    /**
     * 设置桥梁参数
     */
    private void setLine() {
        if (mScreenCanPress && isPressed) {
            top = top - 2;
            mPath.lineTo(left, top);
            x = left;
            y = bottom - top;
        }


    }

    /**
     * 设置桥梁旋转
     */
    private void translate() {
        if (isRorate && x < left + (bottom - top)) {
            mPath.reset();
            mPath.moveTo(left, bottom);
            x = x + 4;
            y = y - 4;
            if (y < 4) {
                y = 0;
                isRorate = false;
                personMove = true;
            }
            mPath.lineTo(x, bottom - y);
        }


    }

    /**
     * 设置所在岸
     */
    private void drawFirstBank() {
        mFirstBank.set(0, bottom, right, getHeight());


    }

    /**
     * 设置人图片位置
     */
    private void setRersonBitmap() {
        if (personMove) {
            personX += 2;
            if (x - personX < 1) {
                if (x < (left + mSpaceWidth) || x > (left + mSpaceWidth + mSecondBankWidth+8)) {
                    personY = personY + 8;
                    if (personY >= SCREEN_HEIGHT) {
                        personMove = false;
                        reset();
                        mScore = 0;
                    }
                } else {
                    personMove = false;
                    setScore();
                    reset();
                }
            }

            if(n<40){
                if(n%10==0){
                    mBitmap=mBitmaps[n/10];
                }
                n++;
            }else {
                n=0;
                mBitmap=mBitmaps[n];
            }

        }
    }

    /**
     * 设置对岸
     */
    private void setSecondBank() {
        int randNum = SCREEN_WIDTH - right;
        Random random = new Random();
        mSpaceWidth = random.nextInt(randNum);
        if (mSpaceWidth < 10) {
            mSpaceWidth = 10;
        }

        mSecondBankWidth = random.nextInt(randNum - mSpaceWidth);

        if (mSecondBankWidth < 10) {
            mSecondBankWidth = 10;
        }
        mSecondBank.set(right + mSpaceWidth, bottom, right + mSpaceWidth + mSecondBankWidth,
                SCREEN_HEIGHT);
    }


    private void setRiver(){
        mRiverRect.set(right+8,1100,right + mSpaceWidth-8,SCREEN_HEIGHT);
    }

    /**
     * 统计分数
     */
    private void setScore() {
        mScore = mScore + 1;
    }

    /**
     * 将各对象持续画在view上
     */
    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            //在这里进行绘画操作
            mCanvas.drawColor(getResources().getColor(R.color.surface));
            mCanvas.drawRect(mFirstBank, mPaint);
            mCanvas.drawPath(mPath, mPaint);
            mCanvas.drawRect(mSecondBank, mPaint);
            mCanvas.drawRect(mRiverRect,mRiverPaint);
            mCanvas.drawRect(right + mSpaceWidth+mSecondBankWidth+8,1100,SCREEN_WIDTH,SCREEN_HEIGHT,mRiverPaint);
            mCanvas.drawBitmap(mBitmap, personX, personY, mBitmapPaint);
            mCanvas.drawText(mScore + "", 400, 150, mTextPaint);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScreenCanPress) {
                    mPath.moveTo(left, bottom);
                    isPressed = true;
                }

                break;
            case MotionEvent.ACTION_UP:
                isPressed = false;
                isRorate = true;
                mScreenCanPress = false;
                break;
        }
        return true;
    }

}
