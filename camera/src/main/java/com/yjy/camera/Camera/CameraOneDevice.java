package com.yjy.camera.Camera;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.util.SparseArray;

import com.yjy.camera.Engine.CameraParam;
import com.yjy.camera.Utils.AspectRatio;
import com.yjy.camera.Utils.Helper;
import com.yjy.opengl.util.Size;
import com.yjy.camera.Utils.SizeMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class CameraOneDevice extends BaseCameraDevice {

    private static final String TAG = CameraOneDevice.class.getSimpleName();
    private static final SparseArray<String> FLASH_MODES = new SparseArray<>();

    static {
        FLASH_MODES.put(ICameraDevice.FLASH_OFF, Camera.Parameters.FLASH_MODE_OFF);
        FLASH_MODES.put(ICameraDevice.FLASH_ON, Camera.Parameters.FLASH_MODE_ON);
        FLASH_MODES.put(ICameraDevice.FLASH_TORCH, Camera.Parameters.FLASH_MODE_TORCH);
        FLASH_MODES.put(ICameraDevice.FLASH_AUTO, Camera.Parameters.FLASH_MODE_AUTO);
        FLASH_MODES.put(ICameraDevice.FLASH_RED_EYE, Camera.Parameters.FLASH_MODE_RED_EYE);
    }

    private static final int MAGIC_TEXTURE_ID = 0;
    private static final int INVALID_CAMERA_ID = -1;

    private final Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();
    private final SizeMap mPreviewSizes = new SizeMap();
    private final SizeMap mPictureSizes = new SizeMap();

    private int mWidth = 0;
    private int mHeight = 0;

    //支持Zoom
    private boolean isZoomSupport = false;
    //支持平滑Zoom
    private boolean isZoomSmooth = false;





    public Camera mCameraImpl;
    private Camera.Parameters mCameraParams;
    private boolean mIsFocusing = false;
    private SurfaceTexture mBufferTexture;


    public CameraOneDevice(CameraParam param,
                           OnCameraReadyListener listener){
        super(param,listener);
    }



    @Override
    public void open() {
        close();
        openDevice();
    }

    /**
     * 选择相机
     * @return
     */
    private int chooseCamera(int faceId){
        int cameraId = INVALID_CAMERA_ID;
        int types = Camera.getNumberOfCameras();
        for(int i = 0;i<types;i++){
            Camera.getCameraInfo(faceId,mCameraInfo);
            if(mCameraInfo.facing == faceId){
                cameraId = mCameraInfo.facing;
                break;
            }
        }

        return cameraId;
    }

    /**
     * 查找默认比例
     * @return
     */
    private AspectRatio chooseDefaultAspectRatio(){
        AspectRatio ratio = null;
        for(AspectRatio r : mPreviewSizes.ratios()){
            if(AspectRatio.DEFAULT.equals(r) ){
                ratio = r;
                break;
            }
        }

        return ratio;
    }


    /**
     * Calculate camera rotate
     * <p>
     * This calculation is applied to the output JPEG either via Exif Orientation tag
     * or by actually transforming the bitmap. (Determined by vendor camera API implementation)
     * <p>
     * Note: This is not the same calculation as the display orientation
     *
     * @param screenOrientationDegrees Screen orientation in degrees
     * @return Number of degrees to rotate image in order for it to view correctly.
     */
    private int calcTakenPictureRotation(int screenOrientationDegrees) {
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return (mCameraInfo.orientation + screenOrientationDegrees) % 360;
        } else {  // back-facing
            final int landscapeFlip = isLandscape() ? 180 : 0;
            return (mCameraInfo.orientation + screenOrientationDegrees + landscapeFlip) % 360;
        }
    }


    @Override
    public void changeSize(int width, int height) {
        mWidth = width;
        mHeight = width;
    }

    @Override
    public void onSurfacePrepare(SurfaceTexture surfaceTexture) {
        //Render 准备好之后回调
        try{
            mBufferTexture = surfaceTexture;
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public void openDevice() {
        try{
            Size previewSize = prepare();
            mCameraImpl.setPreviewTexture(mBufferTexture);
            preview(previewSize);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private Size prepare(){
        int cameraId = chooseCamera(mParam.getFacing());

        try {
            mCameraImpl = Camera.open(cameraId);

            //设置参数
            mCameraParams = mCameraImpl.getParameters();
            mPreviewSizes.clear();
            //设置预览尺寸
            //检查Camera支持所有的视频帧大小
            for(Camera.Size size : mCameraParams.getSupportedPreviewSizes()){
                mPreviewSizes.add(new Size(size.width,size.height));
            }
            //找到对应比例中最大的
            SortedSet<Size> previewSizes = mPreviewSizes.sizes(aspectRatio);

            if(previewSizes == null){
                //找不到就找默认尺寸
                previewSizes = mPreviewSizes.sizes(chooseDefaultAspectRatio());
            }
            //找到最大的

            //Size previewSize = chooseOptimalPreviewSize(previewSizes);
            Size previewSize = getOptimalSize(mCameraParams.getSupportedPreviewSizes(),mWidth,mHeight);
            mCameraParams.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());

            //设置拍照比例
            mPictureSizes.clear();
            for(Camera.Size size : mCameraParams.getSupportedPictureSizes()){
                mPictureSizes.add(new Size(size.width,size.height));
            }
            SortedSet<Size> pictureSizes = mPictureSizes.sizes(aspectRatio);
            if (pictureSizes == null) {
                // 用户期望的尺寸不存在, 获取默认比例
                pictureSizes = mPreviewSizes.sizes(chooseDefaultAspectRatio());
            }

            Size pictureSize = pictureSizes.last();
            //选择尺寸最大，保证最清晰照片
            mCameraParams.setPictureSize(pictureSize.getWidth(),pictureSize.getHeight());

            //计算旋转
            mCameraParams.setRotation(calcTakenPictureRotation(mParam.getScreenOrientationDegrees()));
            //自动对焦
            setAutoFocus(mParam.isAutoFocus());

            isZoomSupport = mCameraParams.isZoomSupported();

            isZoomSmooth = mCameraParams.isSmoothZoomSupported();

            //闪光模式
            setFlashMode(flashMode);

            mCameraImpl.setParameters(mCameraParams);

            //计算屏幕的旋转
            mCameraImpl.setDisplayOrientation(calcPreviewFrameOrientation(mParam.getScreenOrientationDegrees()));


            mCameraImpl.setZoomChangeListener(new Camera.OnZoomChangeListener() {
                @Override
                public void onZoomChange(int zoomValue, boolean stopped, Camera camera) {
                    if(stopped){
                        //提前停止了，可能不是目标的ZOOM
                        mParam.setZoom(zoomValue);
                    }
                }
            });

            if(!isZoomSupport){
                mParam.setZoom(1.0f);
                mParam.setSoftwareZoom(true);
            }

            return previewSize;


        }catch (Exception e){
            e.printStackTrace();
        }

        return new Size(0,0);
    }

    private void preview(Size previewSize) {
        mCameraImpl.startPreview();
        if(listener != null){
            listener.onCameraReady(mBufferTexture,previewSize,0);
        }
    }

    @Override
    public boolean isZoomSupport() {
        return isZoomSupport;
    }

    /**
     * 只允许100的进度条,0最小，1最大.保证能读满100进度条
     *
     * CameraRender中，则是设置texture的渲染坐标，1最小，0最大
     */
    @Override
    public void notifyZoomChanged() {
        if(mCameraImpl!=null){
            if(isZoomSmooth&&mCameraImpl!=null){
                int maxZooms = mCameraParams.getMaxZoom();
                //先拿到0-1
                //计算每一个step对应提升的Zoom范围
                float zoomFactor = maxZooms / mParam.getZoomSensitive();
                //当前提升多少步
                float zoomStep = mParam.getZoom();

                int result = Math.round(zoomStep*zoomFactor);

                mCameraImpl.startSmoothZoom(result);
            }else if(isZoomSupport&&mCameraImpl!=null){
                int maxZooms = mCameraParams.getMaxZoom();
                //先拿到0-1
                //计算每一个step对应提升的Zoom范围
                float zoomFactor = maxZooms / mParam.getZoomSensitive();
                //当前提升多少步
                float zoomStep = mParam.getZoom();

                int result = Math.round(zoomStep*zoomFactor);

                if(result < maxZooms&&result >=0){
                   // Log.e(TAG,"maxZoom:"+maxZooms+" zoomStep:"+zoomStep+" result:"+result);
                    mCameraParams.setZoom(result);
                    mCameraImpl.setParameters(mCameraParams);
                }



            }
        }
    }

    @Override
    public float getZoom() {
        return mParam.getZoom();
    }

    @Override
    public void stopZoom() {
        if(mCameraImpl!=null){
            if(isZoomSmooth&&mCameraImpl!=null){
                mCameraImpl.stopSmoothZoom();
            }
        }
    }

    private static Size getOptimalSize(List<Camera.Size> supportList, int width, int height) {
        // camera的宽度是大于高度的，这里要保证expectWidth > expectHeight
        int expectWidth = Math.max(width, height);
        int expectHeight = Math.min(width, height);
        // 根据宽度进行排序
        Collections.sort(supportList, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size pre, Camera.Size after) {
                if (pre.width > after.width) {
                    return 1;
                } else if (pre.width < after.width) {
                    return -1;
                }
                return 0;
            }
        });

        Camera.Size result = supportList.get(0);
        boolean widthOrHeight = false; // 判断存在宽或高相等的Size
        // 辗转计算宽高最接近的值
        for (Camera.Size size : supportList) {
            // 如果宽高相等，则直接返回
            if (size.width == expectWidth && size.height == expectHeight) {
                result = size;
                break;
            }
            // 仅仅是宽度相等，计算高度最接近的size
            if (size.width == expectWidth) {
                widthOrHeight = true;
                if (Math.abs(result.height - expectHeight)
                        > Math.abs(size.height - expectHeight)) {
                    result = size;
                }
            }
            // 高度相等，则计算宽度最接近的Size
            else if (size.height == expectHeight) {
                widthOrHeight = true;
                if (Math.abs(result.width - expectWidth)
                        > Math.abs(size.width - expectWidth)) {
                    result = size;
                }
            }
            // 如果之前的查找不存在宽或高相等的情况，则计算宽度和高度都最接近的期望值的Size
            else if (!widthOrHeight) {
                if (Math.abs(result.width - expectWidth)
                        > Math.abs(size.width - expectWidth)
                        && Math.abs(result.height - expectHeight)
                        > Math.abs(size.height - expectHeight)) {
                    result = size;
                }
            }
        }
        return new Size(result.width,result.height);
    }


    /**
     * 选择最合适的预览尺寸
     */
    private Size chooseOptimalPreviewSize(SortedSet<Size> sizes) {
        int desiredWidth;
        int desiredHeight;
        if (isLandscape()) {
            desiredWidth = previewWidth;
            desiredHeight = previewHeight;
        } else {
            desiredWidth = previewHeight;
            desiredHeight = previewWidth;
        }
        Size result = null;
        for (Size size : sizes) {
            result = size;
            // Iterate from small to large
            if (desiredWidth <= size.getWidth() && desiredHeight <= size.getHeight()) {
                break;
            }
        }
        return result;
    }

    private boolean setFlashMode(int flash) {
        List<String> modes = mCameraParams.getSupportedFlashModes();
        String mode = FLASH_MODES.get(flash);
        if(modes != null&&modes.contains(mode)){
            mCameraParams.setFlashMode(mode);
            flashMode = flash;

            return true;
        }

        String currentMode = FLASH_MODES.get(flashMode);
        if(modes == null||!modes.contains(currentMode)){
            mCameraParams.setFlashMode(currentMode);
            flashMode = ICameraDevice.FLASH_OFF;
            return true;
        }

        return false;
    }

    /**
     * Calculate display orientation
     * https://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
     * <p>
     * This calculation is used for orienting the preview
     * <p>
     * Note: This is not the same calculation as the camera rotate
     *
     * @param screenOrientationDegrees Screen orientation in degrees(anticlockwise)
     * @return Number of degrees required to rotate preview
     */
    private int calcPreviewFrameOrientation(int screenOrientationDegrees) {
        int result;
        // front-facing
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + screenOrientationDegrees) % 360;
            // compensate the mirror
            result = (360 - result) % 360;
        }
        // back-facing
        else {
            result = (mCameraInfo.orientation - screenOrientationDegrees + 360) % 360;
        }
        return result;
    }


    /**
     * 设置自动聚焦
     */
    private void setAutoFocus(boolean autoFocus) {
        List<String> modes = mCameraParams.getSupportedFocusModes();
        if(autoFocus&& modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
            //连续对焦
            mCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }else if(autoFocus&& modes.contains(Camera.Parameters.FOCUS_MODE_AUTO)){
            mCameraParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
    }

    @Override
    public void close() {
        if(mCameraImpl != null){
            try {
                mCameraImpl.stopPreview();
                mCameraImpl.setPreviewCallback(null);
                mCameraImpl.release();
                mCameraImpl = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void takePicture(PictureCallback callback) {


    }

    @Override
    public boolean isCameraOpened() {
        return mCameraImpl != null;
    }




    private boolean newCameraFocus(int width,int height,
                                  Point focusPoint, Camera.AutoFocusCallback callback) {
        if (mCameraImpl == null) {
            throw new RuntimeException("mCamera is null");
        }
        Point cameraFocusPoint = Helper.convertToCameraPoint(focusPoint,height,width);
        Rect cameraFocusRect = Helper.convertToCameraRect(cameraFocusPoint, 100);
        Camera.Parameters parameters = mCameraImpl.getParameters();
        if (parameters.getMaxNumFocusAreas() <= 0) {
            return focus(callback);
        }
        clearCameraFocus();
        List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
        // 100是权重
        focusAreas.add(new Camera.Area(cameraFocusRect, 100));
        parameters.setFocusAreas(focusAreas);
        // 设置感光区域
        parameters.setMeteringAreas(focusAreas);
        try {
            mCameraImpl.setParameters(parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return focus(callback);
    }

    private boolean focus(Camera.AutoFocusCallback callback) {
        mCameraImpl.cancelAutoFocus();
        mCameraImpl.autoFocus(callback);
        return true;
    }

    private void clearCameraFocus() {
        if(mCameraImpl == null){
            return;
        }

        mCameraImpl.cancelAutoFocus();
        Camera.Parameters parameters = mCameraImpl.getParameters();
        parameters.setFocusAreas(null);
        parameters.setMeteringAreas(null);

        try {
            mCameraImpl.setParameters(parameters);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void notifyAutoFocusChanged() {
        //是否需要自动对焦
        if(!isCameraOpened()){
            this.autoFocus = mParam.isAutoFocus();
            return;
        }


        if(autoFocus){
            //是允许自动对焦
            setAutoFocus(autoFocus);
            mCameraImpl.setParameters(mCameraParams);
        }else{
            //不自动对焦则
            if (mIsFocusing
                    || mParam.getViewHeight() == 0
                    || mParam.getViewWidth()==0) {
                return;
            }

            mIsFocusing = true;
            Point focusPoint = new Point(mParam.getFocusX(), mParam.getFocusY());
            if (mParam.getFocusCallback() != null) {
                mParam.getFocusCallback().beginFocus(mParam.getFocusX(), mParam.getFocusY());
            }

            newCameraFocus(mParam.getViewWidth(), mParam.getViewHeight(),
                    focusPoint, new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    mIsFocusing = false;
                    if (mParam.getFocusCallback() != null) {
                        mParam.getFocusCallback().endFocus(success);
                    }
                }
            });
        }
    }

    @Override
    public void notifyFlashModeChanged() {
        if (isCameraOpened()) {
            if (flashMode == mParam.getFlashMode()) {
                return;
            }
            // resetMatrix params
            if (setFlashMode(mParam.getFlashMode())) {
                mCameraImpl.setParameters(mCameraParams);
            }
        }
        // not previewing
        else {
            flashMode = mParam.getFlashMode();
        }
    }




}
