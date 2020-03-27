package com.yjy.opengl.gles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/26
 *     desc   : 可以放大缩小的Drawable
 *     version: 1.0
 * </pre>
 */
public class ScaledDrawable2D extends Drawable2D {

    private static final int SIZEOF_FLOAT = 4;

    private FloatBuffer mTweakedTexCoordArray;
    private float mScale = 1.0f;

    /**
     * Set the scale factor.
     */
    public void setScale(float scale) {
        if (scale < 0.0f || scale > 1.0f) {
            throw new RuntimeException("invalid scale " + scale);
        }
        mScale = scale;
    }

    public float getScale() {
        return mScale;
    }

    @Override
    public FloatBuffer getTexCoordArray() {
        FloatBuffer parentBuf = super.getTexCoordArray();
        int count = parentBuf.capacity();


        if(mTweakedTexCoordArray == null){
            ByteBuffer bb = ByteBuffer.allocateDirect(count * SIZEOF_FLOAT);
            bb.order(ByteOrder.nativeOrder());
            mTweakedTexCoordArray = bb.asFloatBuffer();
        }


        FloatBuffer fb = mTweakedTexCoordArray;
        float scale = mScale;
        //进行缩放
        for(int i = 0;i<count;i++){
            float fl = parentBuf.get(i);
            fl = ((fl-0.5f)*scale)+0.5f;
            fb.put(i,fl);
        }


        return mTweakedTexCoordArray;
    }
}
