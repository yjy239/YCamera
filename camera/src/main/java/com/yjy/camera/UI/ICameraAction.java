package com.yjy.camera.UI;

import android.support.annotation.NonNull;

import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Filter.IFBOFilter;
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
    /**
     * 获取快照
     * @param callback
     */
    void takePhoto(final TakePhotoCallback callback);

    /**
     * 设置闪光灯
     * @param flash 闪光灯状态
     */
    void setFlash(int flash);

    /**
     * 获取闪光灯状态
     * @return 闪光灯状态
     */
    int getFlash();

    /**
     * 设置Surface是否和View的边缘对应
     * @param adjustViewBounds true代表边缘适配
     */
    void setAdjustViewBounds(boolean adjustViewBounds);

    /**
     * 获取Surface是否和View的边缘对应
     * @return 获取Surface是否和View的边缘对应
     */
    boolean isAdjustViewBounds();

    /**
     * 设置Camera的 摄像机方向
     * @param facing 前后摄像机
     */
    void setFacing( int facing);

    /**
     * 获取Camera的 摄像机方向
     * @return 前后摄像机
     */
    int getFacing();

    /**
     * 设置Camera 预览和拍照比列
     * @param ratio 4:3,16:9比列
     */
    void setAspectRatio(@NonNull AspectRatio ratio);

    /**
     * 是否自动聚焦
     * @return 是否自动聚焦
     */
    boolean getAutoFocus();

    /**
     * 设置聚焦
     * @param isFocus 是否聚焦
     */
    void setAutoFocus(boolean isFocus);


    /**
     * 设置缩放
     * @param scale 缩放因子（0-1）
     */
    void setZoom(float scale);


    /**
     * 停止Zoom
     */
    void stopZoom();


    /**
     * 是否打开了Camera
     * @return
     */
    boolean isCameraOpened();


    /**
     * 打开Camera
     */
    void openCamera();

    /**
     * 停止Camera
     */
    void stopCamera();

    /**
     * 关闭相机
     */
    void closeCamera();

    /**
     * 销毁相机
     */
    void onDestroy();


    /**
     * 设置是否是软件模拟缩放
     * @param isSoftwareZoom
     */
    void setSoftwareZoom(boolean isSoftwareZoom);


    void postEvent(Runnable runnable);


    void release(IFBOFilter filter);
}
