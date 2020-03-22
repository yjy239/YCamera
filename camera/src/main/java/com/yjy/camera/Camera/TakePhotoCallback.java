package com.yjy.camera.Camera;

import android.graphics.Bitmap;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface TakePhotoCallback {
    void takePhoto(Bitmap bitmap);
}
