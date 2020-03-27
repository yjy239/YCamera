package com.yjy.camera.Filter;

import android.content.Context;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BlurFilter extends KernelFilter {

    public BlurFilter(Context context) {
        super(context);
    }
    @Override
    public float[] getKernel() {
        return  new float[] {
                1f/16f, 2f/16f, 1f/16f,
                2f/16f, 4f/16f, 2f/16f,
                1f/16f, 2f/16f, 1f/16f };
    }
}
