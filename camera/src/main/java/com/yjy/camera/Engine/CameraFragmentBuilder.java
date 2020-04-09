package com.yjy.camera.Engine;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.yjy.camera.Filter.IFBOFilter;
import com.yjy.camera.UI.CameraFragment;
import com.yjy.camera.UI.CameraSupportFragment;
import com.yjy.camera.UI.CameraType;
import com.yjy.camera.UI.ICameraAction;
import com.yjy.camera.UI.ICameraFragment;
import com.yjy.camera.Utils.AspectRatio;
import com.yjy.opengl.gles.Program;
import com.yjy.opengl.util.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/28
 *     desc   : 控制
 *     version: 1.0
 * </pre>
 */
public class CameraFragmentBuilder  {
    private static String FRAGMENT_ID = "CameraManager_Fragment";



    //建造者模式
    private WeakReference<Activity> mActivity;
    private WeakReference<AppCompatActivity> mAppCompatActivity;
    private int mResId;

    private CameraParam mCameraParam = new CameraParam();

    public CameraFragmentBuilder(Activity activity,@LayoutRes int resId){
        mActivity = new WeakReference<>(activity);
        mResId = resId;
    }

    public CameraFragmentBuilder(AppCompatActivity activity,@IdRes int resId){
        mAppCompatActivity = new WeakReference<>(activity);
        mResId = resId;
    }

    /**
     * 设置Surface 是否适应View的大小
     * @param adjustViewBounds true 代表适应View
     */
    public CameraFragmentBuilder setAdjustViewBounds(boolean adjustViewBounds){
        mCameraParam.setAdjustViewBounds(adjustViewBounds);
        return this;
    }


    /**
     * 设置前后摄像头
     * @param facing front,back
     */
    public CameraFragmentBuilder setFacing( int facing){
        mCameraParam.setFacing(facing);
        return this;
    }


    /**
     * 设置 预览和图片比列
     * @param ratio 4:3 16:9
     */
    public CameraFragmentBuilder setAspectRatio(@NonNull AspectRatio ratio){
        mCameraParam.setAspectRatio(ratio);
        return this;
    }


    /**
     * 设置是否聚焦
     * @param isFocus 是否聚焦
     */
    public CameraFragmentBuilder setAutoFocus(boolean isFocus){
        mCameraParam.setAutoFocus(isFocus);
        return this;
    }


    /**
     * 设置Camera的缩放
     * @param zoom 缩放倍数
     */
    public CameraFragmentBuilder setZoom(float zoom){
        mCameraParam.setZoom(zoom);
        return this;
    }


    /**
     * 设置Zoom缩放敏感度
     * @param sensitive 敏感度，越小越敏感 最小为1
     */
    public CameraFragmentBuilder setZoomSensitive(int sensitive){
        if(sensitive <= 1){
            sensitive = 1;
        }
        mCameraParam.setZoomSensitive(sensitive);
        return this;
    }



    /**
     * 设置Camera是否使用软件模拟。默认硬件缩放，如果发现硬件不支持，则使用soft
     * @param isSoftwareZoom
     * @return
     */
    public CameraFragmentBuilder setSoftwareZoom(boolean isSoftwareZoom){
        mCameraParam.setSoftwareZoom(isSoftwareZoom);
        return this;
    }

    /**
     * 设置闪光灯状态
     * @param flash
     */
    public CameraFragmentBuilder setFlash(int flash){
        mCameraParam.setFlashMode(flash);
        return this;
    }


    /**
     * 是否Filter同步到屏幕
     * @param sync
     */
    public CameraFragmentBuilder setFilterSync(boolean sync){
        mCameraParam.setFilterSync(sync);
        return this;
    }


    /**
     * 新增一个Filter
     * @param filter
     */
    public CameraFragmentBuilder addFilter(IFBOFilter filter){
        mCameraParam.addFilters(filter);
        return this;
    }


    /**
     * 直接查找最高分辨率的Size
     */
    public CameraFragmentBuilder setPreviewMaxSize(boolean isMax){
        mCameraParam.setPreviewMaxSize(isMax);
        return this;
    }




    /**
     * 移除一个Filter
     * @param filter
     */
    public CameraFragmentBuilder removeFilter(IFBOFilter filter){
        mCameraParam.removeFilters(filter);
        return this;
    }

    public CameraFragmentBuilder asTexture(){
        mCameraParam.setViewType(CameraType.Texture);
        return this;
    }

    public CameraFragmentBuilder asSurface(){
        mCameraParam.setViewType(CameraType.Surface);
        return this;
    }




    /**
     * 是否处于debug模式 openGL es 异常会报错退出
     * @param isDebug
     * @return
     */
    public CameraFragmentBuilder setDebug(boolean isDebug){
        Utils.setDebug(isDebug);
        return this;
    }


    public ICameraFragment build(){
        if(mAppCompatActivity.get() != null){
            return init(mAppCompatActivity.get(),mResId);
        }else if(mActivity.get() != null){
            return init(mActivity.get(),mResId);
        }
        return null;
    }


    private ICameraFragment init(Activity activity, int viewGroup){
        FragmentManager manager = activity.getFragmentManager();
        CameraFragment cameraFragment = (CameraFragment)manager.findFragmentByTag(FRAGMENT_ID);


        if(cameraFragment == null){
            cameraFragment = new CameraFragment();
            cameraFragment.setCameraParams(mCameraParam);
            manager.beginTransaction()
                    .add(viewGroup,cameraFragment,FRAGMENT_ID).commit();
        }else {
            cameraFragment.setCameraParams(mCameraParam);
            manager.beginTransaction()
                    .replace(viewGroup,cameraFragment,FRAGMENT_ID).commit();
        }

        return (ICameraFragment)cameraFragment;
    }


    private ICameraFragment init(AppCompatActivity activity, int viewGroup){
        android.support.v4.app.FragmentManager manager = activity.getSupportFragmentManager();
        CameraSupportFragment cameraFragment = (CameraSupportFragment)manager.
                findFragmentByTag(FRAGMENT_ID);


        if(cameraFragment == null){
            cameraFragment = new CameraSupportFragment();
            cameraFragment.setCameraParams(mCameraParam);
            manager.beginTransaction()
                    .add(viewGroup,cameraFragment,FRAGMENT_ID).commit();
        }else {
            cameraFragment.setCameraParams(mCameraParam);
            manager.beginTransaction()
                    .replace(viewGroup,cameraFragment,FRAGMENT_ID).commit();
        }

        return (ICameraFragment)cameraFragment;
    }



}
