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
