package com.yjy.camera.bitmap;

import android.graphics.Bitmap;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/04/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface LruPoolStrategy {
    void put(Bitmap bitmap);
    Bitmap get(int width, int height, Bitmap.Config config);
    Bitmap removeLast();
    String logBitmap(Bitmap bitmap);
    String logBitmap(int width, int height, Bitmap.Config config);
    int getSize(Bitmap bitmap);
}
