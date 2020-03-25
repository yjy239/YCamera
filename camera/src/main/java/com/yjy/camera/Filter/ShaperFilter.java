package com.yjy.camera.Filter;

import android.content.Context;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/25
 *     desc   : 锐化
 *     version: 1.0
 * </pre>
 */
public class ShaperFilter extends KernelFilter {
    public ShaperFilter(Context context) {
        super(context);
    }

    @Override
    public float[] getKernel() {
        return new float[]{
                0,-1, 0,
                -1, 5 ,-1,
                0 ,-1, 0
        };
    }
}
