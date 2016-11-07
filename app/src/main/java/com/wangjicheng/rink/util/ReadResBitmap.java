package com.wangjicheng.rink.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * Copyright2012-2016  CST.All Rights Reserved
 *
 * Comments：
 *
 * @author huanghj
 *
 *         Time: 2016/9/22 0022
 *
 *         Modified By:
 *         Modified Date:
 *         Why & What is modified:
 * @version 5.0.0
 */
public class ReadResBitmap {

    public static Bitmap decodeBitmapFromRes(Context context, int resourseId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;

        InputStream is = context.getResources().openRawResource(resourseId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap createBitmap(Context context, Bitmap source,
            int n, int total) {
        Bitmap bitmap = Bitmap.createBitmap(source,
                (n - 1) * source.getWidth() / total,
                0, source.getWidth() / total, source.getHeight());
        return bitmap;
    }

    public static Bitmap[] generateBitmapArray(Context context, int resourseId,
            int total) {
        Bitmap bitmaps[] = new Bitmap[total];
        Bitmap source = decodeBitmapFromRes(context, resourseId);
//        this.spriteWidth = source.getWidth() / col;
//        this.spriteHeight = source.getHeight() / row;
        for (int i = 1; i <= total; i++) {
            bitmaps[i - 1] = createBitmap(context, source, i, total);
        }
        if (source != null && !source.isRecycled()) {
            source.recycle();
            source = null;
        }
        return bitmaps;
    }

}
