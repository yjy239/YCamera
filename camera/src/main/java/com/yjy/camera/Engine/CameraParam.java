package com.yjy.camera.Engine;

import android.view.View;

import com.yjy.camera.Utils.AspectRatio;
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
public class CameraParam {
    private AspectRatio aspectRatio = AspectRatio.DEFAULT;
    private int facing;
    private boolean autoFocus;
    private int flashMode;
    private int screenOrientationDegrees;
    boolean adjustViewBounds;
    private Size desiredSize;
    private FocusListener mFocusCallback;
    private int mFocusX = 0;
    private int mFocusY = 0;
    private int mViewHeight = 0;
    private int mViewWidth = 0;
    private float mZoom = HARDWARE_ZOOM_START;

    private int mZoomSensitive = 3;

    private boolean isSoftwareZoom = false;

    public static final float TEXTURE_ZOOM_START = 1.0f;

    public static final float HARDWARE_ZOOM_START = 0.0f;


    /**
     * 是否是软件模式Zoom
     * @return
     */
    public boolean isSoftwareZoom() {
        return isSoftwareZoom;
    }

    /**
     * 设置软件Zoom
     * @param softwareZoom
     */
    public void setSoftwareZoom(boolean softwareZoom) {
        isSoftwareZoom = softwareZoom;
        if(softwareZoom){
            mZoom = TEXTURE_ZOOM_START;
        }else {
            mZoom = HARDWARE_ZOOM_START;
        }

    }

    public int getZoomSensitive() {
        return mZoomSensitive;
    }

    public void setZoomSensitive(int mZoomStep) {
        this.mZoomSensitive = mZoomStep;
    }

    public float getZoom() {
        return mZoom;
    }

    public void setZoom(float scale) {
        this.mZoom = scale;
    }

    public AspectRatio getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(AspectRatio aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public int getFacing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public boolean isAutoFocus() {
        return autoFocus;
    }

    public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }

    public int getFlashMode() {
        return flashMode;
    }

    public void setFlashMode(int flashMode) {
        this.flashMode = flashMode;
    }

    public int getScreenOrientationDegrees() {
        return screenOrientationDegrees;
    }

    public void setScreenOrientationDegrees(int screenOrientationDegrees) {
        this.screenOrientationDegrees = screenOrientationDegrees;
    }

    public boolean isAdjustViewBounds() {
        return adjustViewBounds;
    }

    public void setAdjustViewBounds(boolean adjustViewBounds) {
        this.adjustViewBounds = adjustViewBounds;
    }

    public Size getDesiredSize() {
        return desiredSize;
    }

    public void setDesiredSize(Size desiredSize) {
        this.desiredSize = desiredSize;
    }

    public FocusListener getFocusCallback() {
        return mFocusCallback;
    }

    public void setFocusCallback(FocusListener mFocusCallback) {
        this.mFocusCallback = mFocusCallback;
    }

    public void setFocus(View view,int pointX,int pointY){
        mViewHeight = view.getMeasuredHeight();
        mViewWidth = view.getMeasuredWidth();
        mFocusX = pointX;
        mFocusY = pointY;
    }

    public int getFocusX() {
        return mFocusX;
    }

    public int getFocusY() {
        return mFocusY;
    }

    public int getViewHeight() {
        return mViewHeight;
    }

    public int getViewWidth() {
        return mViewWidth;
    }


    public interface FocusListener {
        void beginFocus(int x, int y);

        void endFocus(boolean success);
    }
}
