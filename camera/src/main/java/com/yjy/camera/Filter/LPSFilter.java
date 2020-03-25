package com.yjy.camera.Filter;

import android.content.Context;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/25
 *     desc   : 拉普拉斯
 *     version: 1.0
 * </pre>
 */
public class LPSFilter extends KernelFilter {
    public LPSFilter(Context context) {
        super(context);
    }

    @Override
    public float[] getKernel() {
        return new float[]{
                0,-1,0,
                -1,4,-1,
                0,-1,0
        };
    }
}
