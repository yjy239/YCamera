package com.yjy.camera.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Engine.CameraParam;
import com.yjy.camera.Filter.IFBOFilter;
import com.yjy.camera.Utils.AspectRatio;
import com.yjy.camera.bitmap.BitmapPool;
import com.yjy.opengl.util.Utils;

import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/23
 *     desc   : 通过Fragment对YCamera资源进行释放
 *     version: 1.0
 * </pre>
 */
public class CameraSupportFragment extends Fragment implements ICameraFragment {

    public static final int REQUEST_CODE = 11;
    private int mType = CameraType.Surface;

    private CameraPresenter mPresenter;


    public CameraSupportFragment(){
        mPresenter = new CameraPresenter();
    }

    public void setCameraParams(CameraParam cameraParams) {
        mPresenter.setCameraParams(cameraParams);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof Activity){
            Activity activity = (Activity)context;
            WindowManager.LayoutParams params =
                    activity.getWindow().getAttributes();
            boolean hardwareAccelerated =
                    (params.flags & WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED) != 0;
            mPresenter.setHardwareAccelerated(hardwareAccelerated);
        }

    }

    public void setCameraType(@CameraType int type){
        mType = type;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return mPresenter.getView(getActivity(),inflater,container,mType);
    }


    @Override
    public void postEvent(Runnable runnable) {
        if(mPresenter!=null){
            mPresenter.postEvent(runnable);
        }
    }

    @Override
    public void release(IFBOFilter filter) {
        if(mPresenter!=null){
            mPresenter.release(filter);
        }
    }


    public void openCamera(){
        if(Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CODE);
        }else {
            if(mPresenter == null){
                return;
            }
            mPresenter.openCamera();
        }
    }

    public void stopCamera(){
        if(mPresenter == null){
            return;
        }
        mPresenter.stopCamera();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(permissions.length>0&&Manifest.permission.CAMERA.equals(permissions[0])){
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if(mPresenter == null){
                        return;
                    }
                    mPresenter.openCamera();
                }else {
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mPresenter == null){
            return;
        }
        mPresenter.openCamera();

    }


    @Override
    public void onStop() {
        super.onStop();
        if(mPresenter == null){
            return;
        }
        mPresenter.openCamera();
    }

    @Override
    public boolean isCameraOpened() {
        if(mPresenter == null){
            return false;
        }
        return mPresenter.isCameraOpened();
    }


    @Override
    public void setSoftwareZoom(boolean isSoftwareZoom) {
        if(mPresenter == null){
            return;
        }
        mPresenter.setSoftwareZoom(isSoftwareZoom);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mPresenter == null){
            return;
        }
        mPresenter.onDestroy();
    }

    @Override
    public void closeCamera() {
        if(mPresenter == null){
            return;
        }
        mPresenter.closeCamera();
    }


    @Override
    public void setFlash(int flash) {
        if(mPresenter == null){
            return;
        }
        mPresenter.setFlash(flash);


    }

    @Override
    public int getFlash() {
        if(mPresenter == null){
            return 0;
        }
        return mPresenter.getFlash();
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if(mPresenter == null){
            return;
        }

        mPresenter.setAdjustViewBounds(adjustViewBounds);
    }

    @Override
    public boolean isAdjustViewBounds() {
        if(mPresenter == null){
            return false;
        }
        return mPresenter.isAdjustViewBounds();
    }

    @Override
    public void setFacing(int facing) {
        if(mPresenter == null){
            return;
        }
        mPresenter.setFacing(facing);
    }

    @Override
    public int getFacing() {
        if(mPresenter == null){
            return 0;
        }
        return mPresenter.getFacing();
    }

    @Override
    public void setAspectRatio(@NonNull AspectRatio ratio) {
        if(mPresenter == null){
            return;
        }
        mPresenter.setAspectRatio(ratio);
    }

    @Override
    public boolean getAutoFocus() {
        if(mPresenter == null){
            return false;
        }
        return mPresenter.getAutoFocus();
    }

    @Override
    public void setAutoFocus(boolean isFocus) {
        if(mPresenter == null){
            return;
        }
        mPresenter.setAutoFocus(isFocus);
    }

    @Override
    public void setZoom(float scale) {
        if(mPresenter == null){
            return;
        }
        mPresenter.setZoom(scale);
    }

    @Override
    public void stopZoom() {
        if(mPresenter == null){
            return;
        }
        mPresenter.stopZoom();
    }


    public void takePhoto(final TakePhotoCallback callback){
        if(mPresenter != null){
            mPresenter.takePhoto(callback);
        }
    }

    @Override
    public void setFilterSync(boolean sync) {
        if(mPresenter == null){
            return;
        }
        mPresenter.setFilterSync(sync);
    }

    @Override
    public void addFilter(IFBOFilter filter) {
        if(mPresenter == null){
            return;
        }
        mPresenter.addFilter(filter);
    }

    @Override
    public void removeFilter(IFBOFilter filter) {
        if(mPresenter == null){
            return;
        }
        mPresenter.removeFilter(filter);
    }

    @Override
    public void setFilters(ArrayList<IFBOFilter> filters) {
        if(mPresenter == null){
            return;
        }

        mPresenter.setFilters(filters);
    }

    @Override
    public void setDebug(boolean isDebug) {
        Utils.setDebug(isDebug);
    }

    @Override
    public BitmapPool getBitmapPool() {
        return mPresenter.getBitmapPool();
    }
}
