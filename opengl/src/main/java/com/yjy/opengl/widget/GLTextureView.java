package com.yjy.opengl.widget;

import android.app.Application;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGLContext;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.TextureView;


import com.yjy.opengl.core.EGLCoreFactory;
import com.yjy.opengl.core.Egl14Core;
import com.yjy.opengl.core.EglContext;
import com.yjy.opengl.core.IEGLCore;
import com.yjy.opengl.core.LowEglCore;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/26
 *     desc   : 模仿GLSurfaceView 设计的textureView
 *     version: 1.0
 * </pre>
 */
public class GLTextureView extends TextureView {

    protected ITextureRenderer mRenderer;
    protected RendererThread mRendererThread;
    private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private ArrayList<Runnable> mPrepareRunnable = new ArrayList<>();

    public GLTextureView(Context context) {
        this(context, null);
    }

    public GLTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GLTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSurfaceTextureListener(new SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (mRendererThread != null) {
                    mRendererThread.handleSurfaceTextureChanged();
                    return;
                }
                // do launch
                mRendererThread = new RendererThread(RendererThread.class.getSimpleName(),
                        new WeakReference<>(GLTextureView.this));
                mRendererThread.start();

                if(mPrepareRunnable!=null&&mPrepareRunnable.size()>0){
                    for(Runnable run : mPrepareRunnable){
                        if(run != null){
                            mRendererThread.mRendererHandler.post(run);
                        }

                    }

                }

                // invoke renderer lifecycle sequence.
                if (mRenderer != null) {
                    mRendererThread.handleRendererChanged();
                }
                mRendererThread.handleSurfaceSizeChanged();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                if (mRendererThread != null) {
                    mRendererThread.handleSurfaceSizeChanged();
                }
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (mRendererThread != null) {
                    mRendererThread.quitSafely();
                    mRendererThread = null;
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // nothing.
            }

        });
    }

    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory factory) {
        mEGLWindowSurfaceFactory = factory;
    }

    public void queueEvent(Runnable runnable){
        if(mRendererThread != null){
            mRendererThread.mRendererHandler.post(runnable);
        }else {
            mPrepareRunnable.add(runnable);
        }

    }

    /**
     * Set a renderer.
     */
    public void setRenderer(@NonNull ITextureRenderer renderer) {
        if (mRenderer == renderer) {
            return;
        }
        mRenderer = renderer;
        if (mRendererThread != null) {
            mRendererThread.handleRendererChanged();
        }
    }

    /**
     * request renderer.
     */
    public void requestRenderer() {
        if (mRendererThread != null) {
            mRendererThread.handleDrawFrame();
        }
    }


    public EglContext getEgLContext(){
        if(Build.VERSION.SDK_INT < 17){
            return new EglContext(getLowEglContext());
        }else {
            return new EglContext(getEGlContext());
        }
    }

    /**
     * Gets a EGLContext
     *
     * @return return a instance of EGLContext. if mRendererThread not start, will be null.
     */
    public EGLContext getEGlContext() {
        if(Build.VERSION.SDK_INT < 17){
            throw new IllegalArgumentException("getLowEglContext only available");
        }

        EGLContext res = null;
        if (mRendererThread != null) {
            IEGLCore core = mRendererThread.mEglCore;
            if(core instanceof Egl14Core){
                res = ((Egl14Core)core).getContext();
            }

        }
        return res;
    }


    public javax.microedition.khronos.egl.EGLContext getLowEglContext() {
        if(Build.VERSION.SDK_INT >= 17){
            throw new IllegalArgumentException("getEglContext only available");
        }
        javax.microedition.khronos.egl.EGLContext res = null;
        if (mRendererThread != null) {
            IEGLCore core = mRendererThread.mEglCore;
            if(core instanceof LowEglCore){
                res = ((LowEglCore)core).getContext();
            }

        }
        return res;
    }

    public interface EGLWindowSurfaceFactory {
        /**
         *  @return null if the surface cannot be constructed.
         */
        void createWindowSurface();
        void destroySurface();
    }



    static class RendererThread extends HandlerThread
            implements SurfaceTexture.OnFrameAvailableListener, Handler.Callback {

        private static final int MSG_SURFACE_TEXTURE_CHANGED = 0;
        private static final int MSG_RENDERER_CHANGED = 1;
        private static final int MSG_SURFACE_SIZE_CHANGED = 2;
        private static final int MSG_DRAW_FRAME = 3;

        private final WeakReference<GLTextureView> mWkRef;
        private final IEGLCore mEglCore;
        private EGLCoreFactory mFactory = new EGLCoreFactory();
        private Handler mRendererHandler;
        private boolean hasCreate = false;

        private RendererThread(String name, WeakReference<GLTextureView> view) {
            super(name);
            mWkRef = view;
            mEglCore = mFactory.createCore();
        }

        @Override
        public synchronized void start() {
            super.start();
            mRendererHandler = new Handler(getLooper(), this);
            mRendererHandler.sendEmptyMessage(MSG_SURFACE_TEXTURE_CHANGED);
        }

        @Override
        public boolean quitSafely() {
            release();
            return super.quitSafely();
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                // 画布变更
                case MSG_SURFACE_TEXTURE_CHANGED:
                    preformSurfaceTextureChanged();
                    break;
                // 渲染器变更
                case MSG_RENDERER_CHANGED:
                    performRendererChanged();
                    break;
                // 画布尺寸变更
                case MSG_SURFACE_SIZE_CHANGED:
                    performSurfaceSizeChanged();
                    break;
                // 绘制数据帧
                case MSG_DRAW_FRAME:
                    performDrawFrame();
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            if (mRendererHandler != null) {
                mRendererHandler.sendEmptyMessage(MSG_DRAW_FRAME);
            }
        }

        /**
         * {@link #preformSurfaceTextureChanged}
         */
        void handleSurfaceTextureChanged() {
            release();
            if (mRendererHandler != null) {
                mRendererHandler.sendEmptyMessage(MSG_SURFACE_TEXTURE_CHANGED);
            }
        }

        /**
         * {@link #performRendererChanged}
         */
        void handleRendererChanged() {
            if (mRendererHandler != null) {
                mRendererHandler.sendEmptyMessage(MSG_RENDERER_CHANGED);
            }
        }

        /**
         * {@link #performSurfaceSizeChanged}
         */
        void handleSurfaceSizeChanged() {
            if (mRendererHandler != null) {
                mRendererHandler.sendEmptyMessage(MSG_SURFACE_SIZE_CHANGED);
            }
        }

        /**
         * {@link #performSurfaceSizeChanged}
         */
        void handleDrawFrame() {
            if (mRendererHandler != null) {
                mRendererHandler.sendEmptyMessage(MSG_DRAW_FRAME);
            }
        }

        private void preformSurfaceTextureChanged() {
            GLTextureView view = mWkRef.get();
            if (view == null) {
                return;
            }
            // Create egl context
            mEglCore.initialize(view.getSurfaceTexture());
            // invoke render lifecycle
            ITextureRenderer renderer = view.mRenderer;
            if (renderer != null) {
                renderer.onEGLContextCreated();
                // it had init
                if(!hasCreate){
                    renderer.setContext(mWkRef.get()!=null?mWkRef.get().getContext():null);
                    if(view.mEGLWindowSurfaceFactory!=null){
                        view.mEGLWindowSurfaceFactory.createWindowSurface();
                    }
                    renderer.onSurfaceCreated(mEglCore.getGL10(),null);
                    hasCreate = true;
                }

                renderer.onSurfaceChanged(mEglCore.getGL10(),view.getWidth(), view.getHeight());
            }
        }

        private void performRendererChanged() {
            GLTextureView view = mWkRef.get();
            if (view == null) {
                return;
            }
            mRendererHandler.removeMessages(MSG_DRAW_FRAME);
            ITextureRenderer renderer = view.mRenderer;
            if (renderer != null) {
                renderer.onEGLContextCreated();
                // it had init
                if(!hasCreate){
                    renderer.setContext(mWkRef.get()!=null?mWkRef.get().getContext():null);
                    if(view.mEGLWindowSurfaceFactory!=null){
                        view.mEGLWindowSurfaceFactory.createWindowSurface();
                    }
                    renderer.onSurfaceCreated(mEglCore.getGL10(),null);
                    hasCreate = true;
                }
                renderer.onSurfaceChanged(mEglCore.getGL10(),view.getWidth(), view.getHeight());
            }
        }

        private void performSurfaceSizeChanged() {
            GLTextureView view = mWkRef.get();
            if (view == null) {
                return;
            }
            ITextureRenderer renderer = view.mRenderer;
            if (renderer != null) {
                renderer.onSurfaceChanged(mEglCore.getGL10(),view.getWidth(), view.getHeight());
            }
        }

        private void performDrawFrame() {
            GLTextureView view = mWkRef.get();
            if (view == null) {
                return;
            }
            // 更新纹理数据
            ITextureRenderer renderer = view.mRenderer;
            // 执行渲染器的绘制
            if (renderer != null) {
                renderer.onDrawFrame(mEglCore.getGL10());
            }
            // 将 EGL 绘制的数据, 输出到 View 的 preview 中
            mEglCore.swapBuffers();
        }

        private void release() {
            if (mRendererHandler != null) {
                mRendererHandler.removeMessages(MSG_SURFACE_TEXTURE_CHANGED);
                mRendererHandler.removeMessages(MSG_SURFACE_SIZE_CHANGED);
                mRendererHandler.removeMessages(MSG_DRAW_FRAME);
            }


            if(mWkRef != null){
                GLTextureView view = mWkRef.get();
                if (view != null) {
                    if(view.mEGLWindowSurfaceFactory!=null){
                        view.mEGLWindowSurfaceFactory.destroySurface();
                    }

                    ITextureRenderer renderer = view.mRenderer;
                    renderer.release();
                }
            }

            mEglCore.release();
            hasCreate = false;
        }
    }

}
