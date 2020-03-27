package com.yjy.camera.UI;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Filter.IFilterAction;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ICameraPresenter extends IFilterAction,ICameraAction {

    /**
     * 获取对应的View
     * @param activity
     * @param inflater
     * @param container
     * @param type
     * @return
     */
    View getView(Activity activity,
                        LayoutInflater inflater, ViewGroup container,
                        @CameraType int type);

    /**
     * 当前的Activity是否支持硬件加速
     * @param hardwareAccelerated
     */
    void setHardwareAccelerated(boolean hardwareAccelerated);






    /**
     * 获取屏幕快照
     * @param callback
     */
    void takePhoto(TakePhotoCallback callback);



}
