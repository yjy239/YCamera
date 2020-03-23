package com.yjy.camera.UI;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.R;
import com.yjy.camera.Render.IFBOFilter;
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

    YCameraView mCameraView;
    public static final int REQUEST_CODE = 11;
    private View mContentView;
    private boolean isStart = false;
    private boolean isHardwareAccelerated = false;

    public void setHardwareAccelerated(boolean hardwareAccelerated) {
        isHardwareAccelerated = hardwareAccelerated;
    }

    public View getView(Activity activity,
                        LayoutInflater inflater, ViewGroup container,
                        @CameraType int type){
        if(type == CameraType.Surface){
            mContentView = inflater.inflate(R.layout.camera_surface, container, true);
        }else {
            if(isHardwareAccelerated){
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                        ,WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
            mContentView = inflater.inflate(R.layout.camera_texture, container, true);
        }

        mCameraView = mContentView.findViewById(R.id.camera);

        return mContentView;
    }

    public void openCamera(){
        if(!isStart){
            if(mCameraView == null){
                return;
            }
            mCameraView.startPreview();
            isStart = true;
        }

    }

    public void stopCamera(){
        if(isStart){
            if(mCameraView == null){
                return;
            }
            mCameraView.stopPreview();
            isStart = false;
        }

    }

    public void setFilterSync(boolean sync){
        if(mCameraView == null){
            return;
        }
        mCameraView.setFilterSync(sync);
    }

    @Override
    public void addFilter(IFBOFilter filter) {
        if(mCameraView == null){
            return;
        }
        mCameraView.addFilter(filter);
    }

    @Override
    public void removeFilter(IFBOFilter filter) {
        if(mCameraView == null){
            return;
        }
        mCameraView.removeFilter(filter);
    }

    @Override
    public void setFilters(ArrayList<IFBOFilter> filters) {
        if(mCameraView == null){
            return;
        }
        mCameraView.setFilters(filters);
    }


    public void takePhoto(TakePhotoCallback callback){
        if(mCameraView == null){
            return;
        }
        mCameraView.takePhoto(callback);
    }


    public void onDestroy(){
        if(mCameraView != null){
            mCameraView.release();
        }

        mContentView = null;
        mCameraView = null;
    }

    public void closeCamera() {
        if(mCameraView == null){
            return;
        }
        mCameraView.close();
    }
}
