package com.yjy.opengl.core;

import android.annotation.TargetApi;
import android.opengl.EGLContext;
import android.os.Build;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EGLCoreFactory {

    public IEGLCore createCore(){
        if(Build.VERSION.SDK_INT >= 17){
            return new Egl14Core();
        }else {
            return new LowEglCore();
        }
    }
}
