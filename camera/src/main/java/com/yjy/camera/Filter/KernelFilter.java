package com.yjy.camera.Filter;

import android.content.Context;

import com.yjy.opengl.gles.KernelTexture2DProgram;
import com.yjy.opengl.gles.Texture2DProgram;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class KernelFilter extends FBOFilter {
    private KernelTexture2DProgram mProgram;
    private float mColorAdj = 0.0f;

    public KernelFilter(Context context,float colorAdj) {
        super(context);
        this.mColorAdj = colorAdj;
    }

    public KernelFilter(Context context) {
        super(context);
        this.mColorAdj = 0f;
    }

    @Override
    public Texture2DProgram getTextureProgram() {
        mProgram =
                new KernelTexture2DProgram(mContext,getTextureType());
        mProgram.create();

        float[] kernel = getKernel();

        mProgram.setKernel(kernel,mColorAdj);
        return mProgram;
    }


    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        mProgram.setTexSize(width,height);
    }


    public abstract float[] getKernel();


    @Override
    public void release() {
        super.release();
        if(mProgram != null){
            mProgram.release();
            mProgram = null;
        }

    }
}
