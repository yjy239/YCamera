package com.yjy.opengl.core;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGLContext;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EglContext {
    Object mContext;

    public EglContext(Object context) {
        this.mContext = context;
    }

    public Object getContext() {
        return mContext;
    }

    public void setContext(Object context) {
        mContext = context;
    }
}
