package com.yjy.camera.Render;

import android.opengl.GLSurfaceView;

import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.widget.IPreview;
import com.yjy.opengl.widget.Render;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class BaseRender implements IPreview.Renderer, IMainRender{
    protected int mViewWidth;
    protected int mViewHeight;

    public void setViewHeight(int viewHeight) {
        this.mViewHeight = viewHeight;
    }

    public void setViewWidth(int viewWidth) {
        this.mViewWidth = viewWidth;
    }

    public abstract void release();

}
