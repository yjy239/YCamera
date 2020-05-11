package com.yjy.camera.Utils;

import android.os.Build;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/05/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class VersionUtils {
    static boolean isJellyBeanMr1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    static boolean isQ() {
        return Build.VERSION.SDK_INT >= 29;
    }
}
