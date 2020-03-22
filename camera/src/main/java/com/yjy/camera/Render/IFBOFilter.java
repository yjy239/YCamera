package com.yjy.camera.Render;

import com.yjy.opengl.gles.FrameDrawer;
import com.yjy.opengl.gles.Texture2DProgram;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/26
 *     desc   : 帧缓冲接口
 *     version: 1.0
 * </pre>
 */
public interface IFBOFilter {

    Texture2DProgram getTextureProgram();

    FrameDrawer getFrameDrawer();

    void onSurfaceCreated(int viewWidth, int viewHeight);

    void onSurfaceChanged(int width, int height);

    @Texture2DProgram.GLTextureType int getTextureType();

    int onDrawFrame(int textureId);

    void release();

    void reset();

    int getTextureId();




}
