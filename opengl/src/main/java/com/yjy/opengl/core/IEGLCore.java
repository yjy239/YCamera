package com.yjy.opengl.core;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.view.Surface;


import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL10;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/09
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface IEGLCore {



    GL10 getGL10();

    /**
     * init EGL Context of SurfaceTexture
     * @param surfaceTexture
     */
    void initialize(@NonNull SurfaceTexture surfaceTexture);

    /**
     * init EGL Context of SurfaceTexture
     * @param surface
     */
    void initialize(@NonNull Surface surface);


    /**
     * change EGL context
     */
    void makeCurrent();

    /**
     * swap EGL Graphic  Buffers
     * @return
     */
    boolean swapBuffers();


    /**
     * release EGL Resource
     */
    void release();
}
