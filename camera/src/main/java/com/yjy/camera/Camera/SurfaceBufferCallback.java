package com.yjy.camera.Camera;

import com.yjy.opengl.util.Size;

import java.nio.ByteBuffer;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface SurfaceBufferCallback {
    void callback(Size size,ByteBuffer buffer);
}
