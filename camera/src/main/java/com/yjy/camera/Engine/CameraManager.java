package com.yjy.camera.Engine;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.yjy.camera.UI.CameraFragment;
import com.yjy.camera.UI.CameraSupportFragment;
import com.yjy.camera.UI.ICameraFragment;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/28
 *     desc   : 控制
 *     version: 1.0
 * </pre>
 */
public class CameraManager {
    private static String FRAGMENT_ID = "CameraManager_Fragment";

    public static ICameraFragment init(Activity activity, int viewGroup){
        FragmentManager manager = activity.getFragmentManager();
        CameraFragment cameraFragment = (CameraFragment)manager.findFragmentByTag(FRAGMENT_ID);

        if(cameraFragment == null){
            cameraFragment = new CameraFragment();
            manager.beginTransaction()
                    .add(viewGroup,cameraFragment,FRAGMENT_ID).commit();
        }else {
            manager.beginTransaction()
                    .replace(viewGroup,cameraFragment,FRAGMENT_ID).commit();
        }

        return (ICameraFragment)cameraFragment;
    }


    public static ICameraFragment init(AppCompatActivity activity, int viewGroup){
        android.support.v4.app.FragmentManager manager = activity.getSupportFragmentManager();
        CameraSupportFragment cameraFragment = (CameraSupportFragment)manager.
                findFragmentByTag(FRAGMENT_ID);

        if(cameraFragment == null){
            cameraFragment = new CameraSupportFragment();
            manager.beginTransaction()
                    .add(viewGroup,cameraFragment,FRAGMENT_ID).commit();
        }else {
            manager.beginTransaction()
                    .replace(viewGroup,cameraFragment,FRAGMENT_ID).commit();
        }

        return (ICameraFragment)cameraFragment;
    }
}
