package com.yjy.opengl.widget;

import android.graphics.Bitmap;

import com.yjy.opengl.util.Size;

import java.nio.ByteBuffer;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface TakeBufferCallback {
    void takeCurrentBuffer(Size size, ByteBuffer bitmap);
}
