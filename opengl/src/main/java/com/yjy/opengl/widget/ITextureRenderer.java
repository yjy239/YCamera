package com.yjy.opengl.widget;


import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/26
 *     desc   : GLSurfaceView 共有设计
 *     version: 1.0
 * </pre>
 */
public interface ITextureRenderer {


    boolean isInit();

    void setContext(Context context);

    void onEGLContextCreated();

    void onSurfaceCreated(GL10 gl, EGLConfig config);

    void onSurfaceChanged(GL10 gl, int width, int height);

    void onDrawFrame(GL10 gl);

    void release();

}
