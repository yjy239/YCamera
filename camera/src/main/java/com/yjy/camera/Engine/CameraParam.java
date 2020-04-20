package com.yjy.camera.Engine;

import android.support.annotation.NonNull;
import android.view.View;

import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Filter.IFBOFilter;
import com.yjy.camera.UI.CameraType;
import com.yjy.camera.Utils.AspectRatio;
import com.yjy.camera.bitmap.BitmapPool;
import com.yjy.opengl.util.Size;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

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
    public AspectRatio aspectRatio = AspectRatio.DEFAULT;
    public int facing;
    private boolean autoFocus = true;
    private int flashMode;
    private int screenOrientationDegrees;
    private boolean adjustViewBounds;
    private Size desiredSize;
    private FocusListener mFocusCallback;
    private int mFocusX = 0;
    private int mFocusY = 0;
    private int mViewHeight = 0;
    private int mViewWidth = 0;
    private float mZoom = HARDWARE_ZOOM_START;
    private int mViewType;

    private int mZoomSensitive = 3;

    private boolean isSoftwareZoom = false;

    private static final float TEXTURE_ZOOM_START = 1.0f;

    private static final float HARDWARE_ZOOM_START = 0.0f;

    private boolean isFilterSync = false;

    private boolean isPreviewMaxSize = false;

    private BitmapPool mBitmapPool;

    @NonNull
    public BitmapPool getBitmapPool() {
        return mBitmapPool;
    }

    public void setBitmapPool(BitmapPool mBitmapPool) {
        this.mBitmapPool = mBitmapPool;
    }

    public boolean isPreviewMaxSize() {
        return isPreviewMaxSize;
    }



    public void setPreviewMaxSize(boolean previewMaxSize) {
        isPreviewMaxSize = previewMaxSize;
    }

    private ArrayList<WeakReference<IFBOFilter>> mFilters = new ArrayList<>();


    public ArrayList<WeakReference<IFBOFilter>> getFilters() {
        return mFilters;
    }


    public void addFilters(IFBOFilter filters) {
        this.mFilters.add(new WeakReference<IFBOFilter>(filters)) ;
    }


    public void removeFilters(IFBOFilter filter) {
        Iterator<WeakReference<IFBOFilter>> it = mFilters.iterator();
        while (it.hasNext()){
            WeakReference<IFBOFilter> f = it.next();
            if(filter == f.get()){
                it.remove();
                break;
            }
        }
    }

    public boolean isFilterSync() {
        return isFilterSync;
    }

    public void setFilterSync(boolean filterSync) {
        isFilterSync = filterSync;
    }


    public int getViewType() {
        return mViewType;
    }

    public void setViewType(@CameraType int viewType) {
        this.mViewType = viewType;
    }

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

    public CameraParam copyTo(CameraParam cameraParam){
        cameraParam.aspectRatio = this.aspectRatio;
        cameraParam.facing = this.facing;
        cameraParam.autoFocus = this.autoFocus;
        cameraParam.flashMode = this.flashMode;
        cameraParam.adjustViewBounds = this.adjustViewBounds;
        cameraParam.mZoom = this.mZoom;
        cameraParam.mZoomSensitive = this.mZoomSensitive;
        cameraParam.isSoftwareZoom = this.isSoftwareZoom;
        cameraParam.isFilterSync = this.isFilterSync;
        cameraParam.mViewType = this.getViewType();
        cameraParam.isPreviewMaxSize = isPreviewMaxSize;
        cameraParam.mBitmapPool = mBitmapPool;
        return cameraParam;
    }

    public void reset() {
        mViewHeight = 0;
        mViewWidth = 0;
        mFocusX = 0;
        mFocusY = 0;
        mFocusCallback = null;
        isPreviewMaxSize = false;

        facing = ICameraDevice.FACING_BACK;
        flashMode = ICameraDevice.FLASH_OFF;
        adjustViewBounds = true;
        mZoom = HARDWARE_ZOOM_START;
        if(mViewType == CameraType.Surface){
            mZoom = TEXTURE_ZOOM_START;
        }else {
            mZoom = HARDWARE_ZOOM_START;
        }

        isFilterSync = false;
        mZoomSensitive = 3;

    }


    public interface FocusListener {
        void beginFocus(int x, int y);

        void endFocus(boolean success);
    }
}
