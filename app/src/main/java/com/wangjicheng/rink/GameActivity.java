package com.wangjicheng.rink;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.wangjicheng.rink.view.MySurfaceView;

public class GameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics outMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        MySurfaceView.SCREEN_WIDTH = outMetrics.widthPixels;
        MySurfaceView.SCREEN_HEIGHT = outMetrics.heightPixels;
        MySurfaceView mySurfaceView=new MySurfaceView(this);
        setContentView(mySurfaceView);
    }
}
