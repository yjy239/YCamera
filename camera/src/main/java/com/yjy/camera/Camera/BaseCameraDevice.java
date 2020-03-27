package com.yjy.camera.Camera;

import com.yjy.camera.Engine.CameraParam;
import com.yjy.camera.Utils.AspectRatio;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class BaseCameraDevice implements ICameraDevice {
    protected OnCameraReadyListener listener;
    protected AspectRatio aspectRatio = AspectRatio.DEFAULT;
    protected int facing;
    protected boolean autoFocus;
    protected int flashMode;
    protected int screenOrientationDegrees;
    protected int previewWidth, previewHeight;
    protected CameraParam mParam;

    public BaseCameraDevice(CameraParam param ,
                            OnCameraReadyListener listener){
        this.listener = listener;
        this.mParam = param;

        if(param == null){
            throw new IllegalArgumentException("CameraParam couldn't be null");
        }

    }


    @Override
    public void notifyFacingChanged() {
        if (this.facing == mParam.getFacing()) {
            return;
        }
        this.facing = mParam.getFacing();
        if (isCameraOpened()) {
            open();
        }
    }

    @Override
    public void notifyAspectRatioChanged() {
        // Handle this later when camera is opened
        if (!isCameraOpened()) {
            aspectRatio = mParam.getAspectRatio();
        }
        // if camera opened
        if (!aspectRatio.equals(mParam.getAspectRatio())) {
            aspectRatio = mParam.getAspectRatio();
            open();
        }
    }

    @Override
    public void notifyScreenOrientationChanged() {
        if (this.screenOrientationDegrees == mParam.getScreenOrientationDegrees()) {
            return;
        }
        this.screenOrientationDegrees = mParam.getScreenOrientationDegrees();
        if (isCameraOpened()) {
            open();
        }
    }

    @Override
    public void notifyDesiredSizeChanged() {
        if (previewWidth == mParam.getDesiredSize().getWidth()
                && previewHeight == mParam.getDesiredSize().getHeight()) {
            return;
        }
        previewWidth = mParam.getDesiredSize().getWidth();
        previewHeight = mParam.getDesiredSize().getHeight();
        if (isCameraOpened()) {
            open();
        }
    }

    /**
     * Test if the supplied orientation is in landscape.
     *
     * @return True if in landscape, false if portrait
     */
    protected boolean isLandscape() {
        return (screenOrientationDegrees == ICameraDevice.LANDSCAPE_90
                || screenOrientationDegrees == ICameraDevice.LANDSCAPE_270);
    }



}
