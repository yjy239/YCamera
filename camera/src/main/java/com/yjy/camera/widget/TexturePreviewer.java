package com.yjy.camera.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;


import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Engine.CameraParam;
import com.yjy.camera.Render.CameraRender;
import com.yjy.camera.Filter.IFBOFilter;
import com.yjy.camera.Render.IMatrixRender;
import com.yjy.opengl.util.Size;
import com.yjy.opengl.core.EglContext;
import com.yjy.opengl.widget.GLTextureView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Camera 预览器
 * <p>
 * 使用 TextureView 渲染硬件相机输出的 SurfaceTexture
 *
 * @author Sharry <a href="sharrychoochn@gmail.com">Contact me.</a>
 * @version 1.0
 * @since 2019-04-24
 */
@SuppressLint("ViewConstructor")
public final class TexturePreviewer extends GLTextureView implements IPreview {

    private static final String TAG = TexturePreviewer.class.getSimpleName();

    private final SurfaceTexture.OnFrameAvailableListener mFrameAvailableListener;
    private Renderer mRenderer;
    private Handler mHandler;
    private CameraParam mParam;
    private ICameraDevice mCameraDevice;

    TexturePreviewer(Context context, CameraParam param, final ICameraDevice prepare) {
        super(context);
        mCameraDevice = prepare;
        mParam = param;
        mHandler = new Handler(Looper.getMainLooper());

        // create frame available listener
        this.mFrameAvailableListener = new FrameAvailableListenerImpl(this);

        // set default renderer
        setRenderer(new CameraRender());

        setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory() {
            @Override
            public void createWindowSurface() {
                mRenderer.setContext(getContext());
                mRenderer.setPrepareListener(prepare);
            }

            @Override
            public void destroySurface() {

            }
        });


        mRenderer.setFrameAvailableListener(mFrameAvailableListener);
    }

    @Override
    public void postEvent(final Runnable runnable) {
        post(new Runnable() {
            @Override
            public void run() {
                queueEvent(runnable);
            }
        });

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRenderer.setViewWidth(getWidth());
        mRenderer.setViewHeight(getHeight());
    }

    @Override
    public void setRenderer(@NonNull Renderer renderer) {
        IMatrixRender before = mRenderer;
        // Copy transform matrix from before.
        if (before != null) {
            renderer.setMatrix(before.getMatrix());
        }
        // update renderer.
        mRenderer = renderer;
        super.setRenderer(mRenderer);
    }

    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public IPreview.Renderer getRenderer() {
        return mRenderer;
    }

    @NonNull
    @Override
    public Size getSize() {
        return new Size(getWidth(),getHeight());
    }

    @Override
    public void takePhoto(final TakePhotoCallback callback) {
        postEvent(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getBitmap();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(callback != null){
                            callback.takePhoto(bitmap);
                        }
                    }
                });

            }
        });

    }

    @Override
    public EglContext getEGLContext() {
        return getEgLContext();
    }




    private static class FrameAvailableListenerImpl extends WeakReference<TexturePreviewer>
            implements SurfaceTexture.OnFrameAvailableListener {

        private FrameAvailableListenerImpl(TexturePreviewer referent) {
            super(referent);
        }

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            TexturePreviewer texturePreviewer = get();
            if (texturePreviewer != null) {
                texturePreviewer.requestRenderer();
            }
        }
    }

    @Override
    public void addFilter(IFBOFilter filter) {
        mRenderer.addFilter(filter);
    }

    @Override
    public void removeFilter(IFBOFilter filter) {
        mRenderer.removeFilter(filter);
    }

    @Override
    public void setFilters(ArrayList<IFBOFilter> filters) {
        mRenderer.setFilters(filters);
    }

    @Override
    public void setFilterSync(boolean isSync) {
        mRenderer.setFilterSync(isSync);
    }

    @Override
    public void release() {
        mRenderer.release();
    }

    @Override
    public void setZoom(@FloatRange(from = 0.0,to = 1.0)final float zoom) {
        if(mCameraDevice == null||mRenderer == null){
            return;
        }
        postEvent(new Runnable() {
            @Override
            public void run() {

                if(mCameraDevice != null){
                    if(mCameraDevice.isZoomSupport()&&!mParam.isSoftwareZoom()){
                        mCameraDevice.notifyZoomChanged();
                    }else {
                        mRenderer.setZoom(zoom);
                    }

                }

            }
        });

    }

    @Override
    public float getZoom() {
        return mParam.getZoom();
    }

    @Override
    public void stopZoom() {
        if(mCameraDevice != null){
            if(mCameraDevice.isZoomSupport()){
                mCameraDevice.stopZoom();
            }

        }
    }
}
