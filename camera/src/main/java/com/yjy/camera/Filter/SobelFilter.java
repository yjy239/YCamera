package com.yjy.camera.Filter;

import android.content.Context;
import android.text.TextUtils;

import com.yjy.opengl.gles.FrameDrawer;
import com.yjy.opengl.gles.KernelTexture2DProgram;
import com.yjy.opengl.gles.Texture2DProgram;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/25
 *     desc   : 索贝尔
 *     version: 1.0
 * </pre>
 */
public class SobelFilter extends KernelFilter {
    public SobelFilter(Context context) {
        super(context);
    }

    @Override
    public float[] getKernel() {
        return new float[] {
                -1f,0f,1f,
                -2f,0f,2f,
                -1f,0f,1f };
    }
}
