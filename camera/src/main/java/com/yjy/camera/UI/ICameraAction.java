package com.yjy.camera.UI;

import android.support.annotation.NonNull;

import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Utils.AspectRatio;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ICameraAction {
    void takePhoto(final TakePhotoCallback callback);

    void setFlash(int flash);

    int getFlash();

    void setAdjustViewBounds(boolean adjustViewBounds);

    boolean isAdjustViewBounds();

    void setFacing( int facing);

    int getFacing();

    void setAspectRatio(@NonNull AspectRatio ratio);

    boolean getAutoFocus();

    void setAutoFocus(boolean isFocus);


}
