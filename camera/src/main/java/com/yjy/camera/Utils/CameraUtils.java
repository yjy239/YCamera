package com.yjy.camera.Utils;

import android.content.Context;

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
}
