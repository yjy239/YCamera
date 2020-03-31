package com.yjy.camera.widget;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.view.View;



import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Filter.IFBOFilter;
import com.yjy.camera.Filter.IFilterAction;
import com.yjy.camera.Render.IMatrixRender;
import com.yjy.opengl.util.Size;
import com.yjy.opengl.core.EglContext;
import com.yjy.opengl.widget.Render;

import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface IPreview  {

    void postEvent(Runnable runnable);


    /**
     * 设置 previewer 的渲染器
     */
    void setRenderer(@NonNull Renderer renderer);

    /**
     * 获取用于预览的 view
     */
    View getView();

    /**
     * 设置 previewer 的渲染器
     */
    @NonNull
    Renderer getRenderer();

    /**
     * 获取渲染器的尺寸
     */
    @NonNull
    Size getSize();

    /**
     * 获取当前帧的数据
     */
    void takePhoto(TakePhotoCallback callback);

    /**
     * 获取当前的渲染环境
     */
    EglContext getEGLContext();

    /**
     * 新增一个Filter
     * @param filter
     */
    void addFilter(IFBOFilter filter);


    /**
     * 移除一个Filter
     * @param filter
     */
    void removeFilter(IFBOFilter filter);


    /**
     * 批量设置Filter
     * @param filters
     */
    void setFilters(ArrayList<IFBOFilter> filters);


    /**
     * Filter是否同步到屏幕上
     * @param isSync
     */
    void setFilterSync(boolean isSync);

    /**
     * 释放 filter
     * @param filter
     */
    void release(IFBOFilter filter);

    /**
     * 释放所有资源
     */
    void release();

    /**
     * 设置缩放
     * @param scale
     */
    void setZoom(float scale);

    /**
     * 获取缩放
     * @return
     */
    float getZoom();


    /**
     * 停止Zoom
     */
    void stopZoom();


    /**
     * 相机预览器的 Renderer
     * <p>
     * 对 ITextureRenderer,Render 的增强, 拓展 matrix 功能
     *
     */
    interface Renderer extends Render,IMatrixRender, IFilterAction {

        void setViewHeight(int viewHeight);

        void setViewWidth(int viewWidth);

        void setFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener mFrameAvailableListener);


        void setPrepareListener(ICameraDevice prepareListener);

        /**
         * 设置缩放
         * @param scale
         */
        void setZoom(float scale);


        boolean isZoomable(float zoom);


        /**
         * 停止Zoom
         */
        void stopZoom();


    }
}
