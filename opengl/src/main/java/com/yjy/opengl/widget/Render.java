package com.yjy.opengl.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/19
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface Render extends ITextureRenderer, GLSurfaceView.Renderer{
    void setViewHeight(int viewHeight);

    void setViewWidth(int viewWidth);

    void takeSurfaceBuffer(TakeBufferCallback takeBufferCallback);


}
