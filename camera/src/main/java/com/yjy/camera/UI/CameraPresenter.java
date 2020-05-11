package com.yjy.camera.UI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Camera.TakePhotoFileCallback;
import com.yjy.camera.Engine.CameraParam;
import com.yjy.camera.R;
import com.yjy.camera.Filter.IFBOFilter;
import com.yjy.camera.Utils.AspectRatio;
import com.yjy.camera.Utils.CameraUtils;
import com.yjy.camera.Utils.Utils;
import com.yjy.camera.bitmap.BitmapPool;
import com.yjy.camera.widget.YCameraView;

import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/23
 *     desc   : 所有Fragment的Prsenter
 *     version: 1.0
 * </pre>
 */
public class CameraPresenter implements ICameraPresenter{


    private YCameraView mCameraView;
    public static final int REQUEST_CODE = 11;
    private View mContentView;
    private boolean isStart = false;
    private boolean isHardwareAccelerated = false;

    private CameraParam mCameraParams;

    private ArrayList<Runnable> mRunnables = new ArrayList<>();

    private BitmapPool mPool;

    //设置一个前置补光灯，实际上就是一个全局的白色图片，然后亮度提升到最大
    private ImageView mPreFlashImg;

    private static final int DELAY = 500;



    public void setCameraParams(CameraParam cameraParams) {
        this.mCameraParams = cameraParams;
    }


    public void setHardwareAccelerated(boolean hardwareAccelerated) {
        isHardwareAccelerated = hardwareAccelerated;
    }

    @Override
    public View getView(Activity activity,
                        LayoutInflater inflater, ViewGroup container,
                        @CameraType int type){
        if(type == CameraType.Surface){
            mContentView = inflater.inflate(R.layout.camera_surface, container, false);
        }else {
            if(isHardwareAccelerated){
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                        ,WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
            mContentView = inflater.inflate(R.layout.camera_texture, container, false);
        }

        mCameraView = mContentView.findViewById(R.id.camera);

        if(mCameraParams != null){
            mCameraView.copyCameraParams(activity,mCameraParams);
        }

        mPool = mCameraParams.getBitmapPool();

        mPreFlashImg = new ImageView(activity);
        mPreFlashImg.setImageResource(R.color.white);

        FrameLayout frameLayout = activity.findViewById(android.R.id.content);

        frameLayout.addView(mPreFlashImg);
        mPreFlashImg.setVisibility(View.GONE);


        for(Runnable runnable : mRunnables){
            runnable.run();
        }



        return mContentView;
    }

    @Override
    public BitmapPool getBitmapPool() {
        return mPool;
    }

    @Override
    public void postEvent(Runnable runnable) {
        mCameraView.postEvent(runnable);
    }

    public void openCamera(){
        if(!isStart){
            if(mCameraView == null){
                return;
            }
            if(!mCameraView.isCameraOpened()){
                mCameraView.openCamera();
                isStart = true;
            }


        }

    }


    @Override
    public void release(IFBOFilter filter) {
        if(mCameraView != null){
            mCameraView.release(filter);
        }
    }

    @Override
    public void setSoftwareZoom(final boolean isSoftwareZoom) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setSoftwareZoom(isSoftwareZoom);
                }
            });
            return;
        }
        if(!mCameraView.isCameraOpened()){
            mCameraView.setSoftwareZoom(isSoftwareZoom);
        }

    }

    public void stopCamera(){
        if(isStart){
            if(mCameraView == null){
                return;
            }
            if(mCameraView.isCameraOpened()){
                mCameraView.stopCamera();
                isStart = true;
            }
            isStart = false;
        }

    }

    @Override
    public void setFilterSync(final boolean sync){
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setFilterSync(sync);
                }
            });
            return;
        }
        mCameraView.setFilterSync(sync);
    }

    @Override
    public void addFilter(final IFBOFilter filter) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.addFilter(filter);
                }
            });
            return;
        }
        mCameraView.addFilter(filter);
    }

    @Override
    public void removeFilter(final IFBOFilter filter) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.removeFilter(filter);
                }
            });
            return;
        }
        mCameraView.removeFilter(filter);
    }

    @Override
    public void setFilters(final ArrayList<IFBOFilter> filters) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setFilters(filters);
                }
            });
            return;
        }
        mCameraView.setFilters(filters);
    }

    @Override
    public void setFlash(final int flash) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setFlash(flash);
                }
            });
            return;
        }
        mCameraView.setFlash(flash);


    }

    @Override
    public int getFlash() {
        if(mCameraView == null){
            return 0;
        }
        return mCameraView.getFlash();
    }

    @Override
    public void setAdjustViewBounds(final boolean adjustViewBounds) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setAdjustViewBounds(adjustViewBounds);
                }
            });
            return;
        }

        mCameraView.setAdjustViewBounds(adjustViewBounds);
    }

    @Override
    public boolean isAdjustViewBounds() {
        if(mCameraView == null){
            return false;
        }
        return mCameraView.isAdjustViewBounds();
    }

    @Override
    public void setFacing(final int facing) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setFacing(facing);
                }
            });
            return;
        }
        mCameraView.setFacing(facing);
    }

    @Override
    public int getFacing() {
        if(mCameraView == null){
            return 0;
        }
        return mCameraView.getFacing();
    }

    @Override
    public void setAspectRatio(@NonNull final AspectRatio ratio) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setAspectRatio(ratio);
                }
            });
            return;
        }
        mCameraView.setAspectRatio(ratio);
    }

    @Override
    public boolean getAutoFocus() {
        if(mCameraView == null){
            return false;
        }
        return mCameraView.getAutoFocus();
    }

    @Override
    public void setAutoFocus(final boolean isFocus) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setAutoFocus(isFocus);
                }
            });
            return;
        }
        mCameraView.setAutoFocus(isFocus);
    }

    @Override
    public void takePhoto(final TakePhotoCallback callback){
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    takePhotoWithPreFlash(callback);
                }
            });
            return;
        }
        takePhotoWithPreFlash(callback);
    }

    private void takePhotoWithPreFlash(final TakePhotoCallback callback){
        mCameraView.post(new Runnable() {
            @Override
            public void run() {
                int flash = getFlash();
                if(flash == ICameraDevice.FLASH_FRONT){
                    if(mCameraView.getFacing() == ICameraDevice.FACING_FRONT){
                        mPreFlashImg.setVisibility(View.VISIBLE);
                        CameraUtils.setWindowBrightness((Activity) mCameraView.getContext(),
                                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
                    }
                }

                mCameraView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCameraView.takePhoto(new TakePhotoCallback() {
                            @Override
                            public void takePhoto(Bitmap bitmap) {
                                mPreFlashImg.setVisibility(View.GONE);
                                CameraUtils.setWindowBrightness((Activity) mCameraView.getContext(),
                                        WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
                                if(callback != null){
                                    callback.takePhoto(bitmap);
                                }
                            }
                        });
                    }
                },DELAY);


            }
        });
    }

    @Override
    public void takePhoto(final String name, final TakePhotoFileCallback callback) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    takePhotoWithPreFlash(name,callback);
                }
            });
            return;
        }
        takePhotoWithPreFlash(name,callback);
    }

    private void takePhotoWithPreFlash(final String name,final TakePhotoFileCallback callback){

        mCameraView.post(new Runnable() {
            @Override
            public void run() {
                int flash = getFlash();
                if(flash == ICameraDevice.FLASH_FRONT){
                    if(mCameraView.getFacing() == ICameraDevice.FACING_FRONT){
                        mPreFlashImg.setVisibility(View.VISIBLE);
                        CameraUtils.setWindowBrightness((Activity) mCameraView.getContext(),
                                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);
                    }

                }

                mCameraView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCameraView.takePhoto(name,new TakePhotoFileCallback(){

                            @Override
                            public void takePhoto(String path) {
                                mPreFlashImg.setVisibility(View.GONE);
                                CameraUtils.setWindowBrightness((Activity) mCameraView.getContext(),
                                        WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
                                if(callback != null){
                                    callback.takePhoto(path);
                                }
                            }
                        });
                    }
                },DELAY);


            }
        });

    }

    @Override
    public void setSavePhotoDir(String name) {
        if(mCameraView != null){
            mCameraParams.setSaveDir(name);
        }
    }

    @Override
    public void setZoom(final float scale) {
        if(mCameraView == null){
            mRunnables.add(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setZoom(scale);
                }
            });
            return;
        }
        mCameraView.setZoom(scale);
    }

    @Override
    public void stopZoom() {
        if(mCameraView == null){
            return;
        }
        mCameraView.stopZoom();
    }

    @Override
    public boolean isCameraOpened() {
        if(mCameraView == null){
            return false;
        }
        mCameraView.stopZoom();
        return mCameraView.isCameraOpened();
    }

    public void onDestroy(){
        if(mCameraView != null){
            mCameraView.onDestroy();
        }

        mContentView = null;
        mCameraView = null;
    }

    public void closeCamera() {
        if(mCameraView == null){
            return;
        }
        mCameraView.closeCamera();
        isStart = false;
    }
}
