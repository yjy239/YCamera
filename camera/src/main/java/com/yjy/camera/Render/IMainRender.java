package com.yjy.camera.Render;

import com.yjy.opengl.widget.TakeBufferCallback;
import com.yjy.opengl.gles.FrameDrawer;

import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface IMainRender {



    void takeSurfaceBuffer(TakeBufferCallback callback);

    FrameDrawer getDrawer();
}
