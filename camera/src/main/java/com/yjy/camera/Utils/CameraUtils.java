package com.yjy.camera.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yjy.camera.Engine.CameraParam;
import com.yjy.camera.R;
import com.yjy.camera.bitmap.LruBitmapPool;

import java.io.File;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CameraUtils {
    public static int dp2px(Context context,float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context,float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(screenshot);
        canvas.translate(-v.getScrollX(), -v.getScrollY());
        //我们在用滑动View获得它的Bitmap时候，获得的是整个View的区域（包括隐藏的），如果想得到当前区域，需要重新定位到当前可显示的区域
        v.draw(canvas);// 将 view 画到画布上
        return screenshot;
    }


    public static String savePhoto(Context context, Bitmap bitmap,CameraParam param,String name) {
        try {
            if (VersionUtils.isQ()) {
                Uri uri = FileUtil.createJpegPendingItem(context, param.getSaveDir(),name);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "w");
                CompressUtil.qualityCompress(bitmap, param.getQuality(), pfd.getFileDescriptor());
                FileUtil.publishPendingItem(context, uri);
                return  FileUtil.getImagePath(context, uri);

            } else {
                File file = FileUtil.createJpegFile(context, param.getSaveDir(),name);
                CompressUtil.doCompress(bitmap, file, param.getQuality());
                FileUtil.notifyMediaStore(context, file.getAbsolutePath());
                return file.getAbsolutePath();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return null;
    }


    public static void setWindowBrightness(Activity activity,float brightness) {
        if(activity == null){
            return;
        }
        Window window = activity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness;
        window.setAttributes(lp);
    }



}
