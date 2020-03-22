package com.yjy.opengl.core;





import android.graphics.SurfaceTexture;


import android.util.Log;
import android.view.Surface;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;


/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/08
 *     desc   : OpenGL es 操作核心类
 *     version: 1.0
 * </pre>
 */
public class LowEglCore implements IEGLCore {
    public static final int EGL_VERSION_2 = 2;
    public static final int EGL_VERSION_3 = 3;

    @IntDef(value = {
            EGL_VERSION_2,
            EGL_VERSION_3
    })
    private @interface EGLVersion {

    }


    private static final String TAG = LowEglCore.class.getSimpleName();

    private final int mEGLVersion;
    private EGLDisplay mEGLDisplay = EGL10.EGL_NO_DISPLAY;
    private EGLContext mEGLContext = EGL10.EGL_NO_CONTEXT;
    private EGLSurface mEGLSurface = EGL10.EGL_NO_SURFACE;
    EGL10 mEgl;


    public LowEglCore() {
        this(EGL_VERSION_2);
    }

    public LowEglCore(@EGLVersion int eglVersion) {
        mEGLVersion = eglVersion;
    }



    /**
     * Initialize EGL for a given configuration spec.
     *
     * @param surface    native window
     */
    public void initialize(@NonNull Surface surface) {
        initializeInternal(surface, EGL10.EGL_NO_CONTEXT);
    }

    @Override
    public GL10 getGL10() {
        return null;
    }

    /**
     * Initialize EGL for a given configuration spec.
     *
     * @param surfaceTexture native window
     */
    public void initialize(@NonNull SurfaceTexture surfaceTexture) {
        initializeInternal(surfaceTexture,  EGL10.EGL_NO_CONTEXT);
    }

    /**
     * Initialize EGL for a given configuration spec.
     *
     * @param surface    native window
     * @param eglContext if null will create new context, false will use shared context
     */
    public void initialize(@NonNull Surface surface, @Nullable EGLContext eglContext) {
        initializeInternal(surface, eglContext == null ? EGL10.EGL_NO_CONTEXT : eglContext);
    }





    /**
     * Initialize EGL for a given configuration spec.
     *
     * @param surfaceTexture native window
     * @param eglContext     if null will create new context, false will use shared context
     */
    public void initialize(@NonNull SurfaceTexture surfaceTexture, @Nullable EGLContext eglContext) {
        initializeInternal(surfaceTexture, eglContext == null ?  EGL10.EGL_NO_CONTEXT : eglContext);
    }

    /**
     * Copy from {@link android.opengl.GLSurfaceView#EglHelper}
     */
    private void initializeInternal(Object nativeWindow, EGLContext sharedEglContext) {

        mEgl = (EGL10) EGLContext.getEGL();
        /*
         * Create a connection for system native window
         */
        mEGLDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        /*
         * We can now initialize EGL for that display
         */
        int[] version = new int[2];
        if (!mEgl.eglInitialize(mEGLDisplay, version)) {
            mEGLDisplay = null;
            throw new RuntimeException("eglInitialize failed");
        }

        /*
         * Create EGLConfig
         */
        EGLConfig eglConfig = chooseConfig();
        if (eglConfig == null) {
            throw new RuntimeException("Cannot find suitable config.");
        }

        /*
         * Create EGLContext
         */
        int[] attrib_list = {0x3098, mEGLVersion, EGL10.EGL_NONE};
        EGLContext eglContext = mEgl.eglCreateContext(mEGLDisplay, eglConfig, sharedEglContext,
                attrib_list);
        if (mEgl.eglGetError() == EGL10.EGL_SUCCESS) {
            mEGLContext = eglContext;
        } else {
            throw new RuntimeException("Create EGLContext failed.");
        }

        /*
         * Create EGLSurface
         */
        int[] surfaceAttribs = {EGL10.EGL_NONE};
        mEGLSurface = mEgl.eglCreateWindowSurface(mEGLDisplay, eglConfig, nativeWindow,
                surfaceAttribs);
        if (mEGLSurface == null || mEGLSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
        }

        /*
         * Bind context
         */
        makeCurrent();
    }



    /**
     * Finds a suitable EGLConfig.
     */
    private EGLConfig chooseConfig() {
        int renderableType = 4;
        if (mEGLVersion >= 3) {
            renderableType |= 0x0040;
        }
        // The actual surface is generally RGBA or RGBX, so situationally omitting alpha
        // doesn't really help.  It can also lead to a huge performance hit on glReadPixels()
        // when reading into a GL_RGBA buffer.
        int[] attribList = {
                EGL10.EGL_RED_SIZE, 8,
                EGL10.EGL_GREEN_SIZE, 8,
                EGL10.EGL_BLUE_SIZE, 8,
                EGL10.EGL_ALPHA_SIZE, 8,
                //EGL14.EGL_DEPTH_SIZE, 16,
                //EGL14.EGL_STENCIL_SIZE, 8,
                EGL10.EGL_RENDERABLE_TYPE,renderableType,
                EGL10.EGL_NONE, 0,      // placeholder for recordable [@-3]
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        if (!mEgl.eglChooseConfig(mEGLDisplay, attribList, configs,
                configs.length, numConfigs)) {
            Log.w(TAG, "unable to find RGB8888 / " + mEGLVersion + " EGLConfig");
            return null;
        }
        return configs[0];
    }


    /**
     * Makes our EGL context current, using the supplied "draw" and "read" surfaces.
     */
    public void makeCurrent() {
        if (!mEgl.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent failed");
        }
    }


    /**
     * Calls eglSwapBuffers.  Use this to "publish" the current frame.
     *
     * @return false on failure
     */
    public boolean swapBuffers() {
        return mEgl.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }

    /**
     * Discards all resources held by this class, notably the EGL context.  This must be
     * called from the thread where the context was created.
     * <p>
     * On completion, no context will be current.
     */
    public void release() {
        if (mEGLDisplay != EGL10.EGL_NO_DISPLAY) {
            mEgl.eglMakeCurrent(mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                    EGL10.EGL_NO_CONTEXT);
            mEgl.eglDestroyContext(mEGLDisplay, mEGLContext);
            mEgl.eglDestroySurface(mEGLDisplay, mEGLSurface);
            mEgl.eglTerminate(mEGLDisplay);
        }
        mEGLContext = EGL10.EGL_NO_CONTEXT;
        mEGLDisplay = EGL10.EGL_NO_DISPLAY;
        mEGLSurface = EGL10.EGL_NO_SURFACE;
    }



    /**
     * Gets the current EGLContext
     *
     * @return the current EGLContext
     */
    public EGLContext getContext() {
        return mEGLContext;
    }


    @Override
    protected void finalize() throws Throwable {
        try {
            if (mEGLDisplay != EGL10.EGL_NO_DISPLAY) {
                // We're limited here -- finalizers don't run on the thread that holds
                // the EGL state, so if a surface or context is still current on another
                // thread we can't fully release it here.  Exceptions thrown from here
                // are quietly discarded.  Complain in the log file.
                Log.w(TAG, "WARNING: LowEglCore was not explicitly released -- state may be leaked");
                release();
            }
        } finally {
            super.finalize();
        }
    }



}
