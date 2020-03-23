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

import com.yjy.camera.Render.IFBOFilter;

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
            switch (permissions[0]){
                case Manifest.permission.CAMERA://权限1
                    if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                        if(mPresenter == null){
                            return;
                        }
                        mPresenter.openCamera();
                    }else {
                        Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
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
        mPresenter.stopCamera();
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
}
