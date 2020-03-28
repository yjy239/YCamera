package com.yjy.camera.Camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.NonNull;


import com.yjy.opengl.util.Size;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ICameraDevice  {
    /**
     * Constants of facing
     */
    int FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    int FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    /**
     * Constants of flash
     */
    int FLASH_OFF = 0;
    int FLASH_ON = 1;
    int FLASH_TORCH = 2;
    int FLASH_AUTO = 3;
    int FLASH_RED_EYE = 4;

    /**
     * Constants of orientation
     */
    int LANDSCAPE_90 = 90;
    int LANDSCAPE_270 = 270;


    void open();

    void close();

    void takePicture(PictureCallback callback);

    boolean isCameraOpened();

    void notifyFacingChanged();

    void notifyAspectRatioChanged();

    void notifyAutoFocusChanged();

    void notifyFlashModeChanged();

    void notifyScreenOrientationChanged();

    void notifyDesiredSizeChanged();

    void notifyZoomChanged();

    float getZoom();

    void stopZoom();

    boolean isZoomSupport();

    void onSurfacePrepare(SurfaceTexture surfaceTexture);

    void changeSize(int width,int height);

    boolean isZoomable(float zoom);

    interface OnCameraReadyListener {

        void onCameraReady(@NonNull SurfaceTexture cameraTexture, @NonNull Size surfaceSize, int displayRotation);

    }

    interface OnCameraOpenListener {

        void onOpen();

    }


    interface PictureCallback{
        void onPictureTaken(byte[] bytes);
        void onError(Throwable throwable);
    }
}
