package com.yjy.camera.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Render.IFBOFilter;
import com.yjy.opengl.widget.Render;
import com.yjy.opengl.widget.TakeBufferCallback;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Engine.CameraParam;
import com.yjy.camera.Render.CameraRender;
import com.yjy.camera.Render.IMatrixRender;
import com.yjy.opengl.util.Size;
import com.yjy.opengl.core.EglContext;
import com.yjy.opengl.widget.BaseGLSurfaceView;


import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class SurfacePreviewer extends BaseGLSurfaceView implements IPreview  {


    private static final String TAG = SurfacePreviewer.class.getName();
    private CameraParam mParam;
    protected IPreview.Renderer mRender;

    private Context mContext;
    private Handler mMainHandler = new android.os.Handler(Looper.getMainLooper());



    public SurfacePreviewer(Context context, CameraParam param, ICameraDevice prepare) {
        super(context);
        init(context,param,prepare);
    }


    void init(Context context,CameraParam param,final ICameraDevice prepare){
        mContext = context;
        mParam = param;

        mRender = new CameraRender();
        setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory(){

            @Override
            public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
                EGLSurface result = null;
                try {
                    if(mRender!=null){
                        mRender.setContext(getContext());
                        mRender.setPrepareListener(prepare);
//                        if(!mRender.isInit()){
//                            mRender.onSurfaceCreated(null,config);
//                        }
                    }

                    result = egl.eglCreateWindowSurface(display, config, nativeWindow, null);
                } catch (IllegalArgumentException e) {
                    // This exception indicates that the surface flinger surface
                    // is not valid. This can happen if the surface flinger surface has
                    // been torn down, but the application has not yet been
                    // notified via SurfaceHolder.Callback.surfaceDestroyed.
                    // In theory the application should be notified first,
                    // but in practice sometimes it is not. See b/4588890
                    Log.e(TAG, "eglCreateWindowSurface", e);
                }
                return result;
            }

            @Override
            public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
//                if(mRender!=null){
//                    mRender.release();
//                }

                egl.eglDestroySurface(display, surface);
            }
        });


        setRenderer(mRender);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRender.setViewWidth(getWidth());
        mRender.setViewHeight(getHeight());
    }

    @Override
    public void postEvent(Runnable runnable) {
        queueEvent(runnable);
    }



    @Override
    public void setRenderer(@NonNull IPreview.Renderer renderer) {
        if(mRender instanceof IMatrixRender){
            IMatrixRender before = (IMatrixRender)mRender;
            // Copy transform matrix from before.
            if (before != null) {
                renderer.setMatrix(before.getMatrix());
            }
        }

        // update renderer.
        mRender = renderer;

        super.setRenderer(renderer);
    }

    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public IMatrixRender getRenderer() {

        return mRender;
    }

    @NonNull
    @Override
    public Size getSize() {
        return new Size(getWidth(), getHeight());
    }

    @Override
    public void takePhoto(final TakePhotoCallback callback) {
        postEvent(new Runnable() {
            @Override
            public void run() {
                mRender.takeSurfaceBuffer(new TakeBufferCallback() {
                    @Override
                    public void takeCurrentBuffer(Size size,ByteBuffer bits) {
                        if(bits != null){
                            final Bitmap bitmap = Bitmap.createBitmap(size.getWidth(), size.getHeight() , Bitmap.Config.ARGB_8888);
                            bitmap.copyPixelsFromBuffer(bits);
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(callback != null){

                                        callback.takePhoto(bitmap);
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });
    }

    @Override
    public EglContext getEGLContext() {
        return new EglContext(getEglContext());
    }


    @Override
    public void addFilter(IFBOFilter filter) {
        mRender.addFilter(filter);
    }

    @Override
    public void removeFilter(IFBOFilter filter) {
       mRender.removeFilter(filter);
    }

    @Override
    public void setFilters(ArrayList<IFBOFilter> filters) {
        mRender.setFilters(filters);
    }
}
