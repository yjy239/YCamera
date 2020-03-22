package com.yjy.opengl.gles;

import android.graphics.Point;
import android.graphics.PointF;

import com.yjy.opengl.util.Utils;

import java.nio.FloatBuffer;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/18
 *     desc   : 动态变化的 2D
 *     version: 1.0
 * </pre>
 */
public class DynamicDrawable2D extends Drawable2D {

    /**
     * 水印顶点坐标
     */
    private final float[] mDynamicVertexCoords = new float[]{
            0f, 0f,  // 左上
            0f, 0f,  // 左下
            0f, 0f,  // 右上
            0f, 0f,  // 右下
    };

    /**
     * 水印纹理坐标, 水印从 Bitmap 中加载, 坐标系相反
     */
    private final float[] mDynamicTextureCoords = new float[]{
            0f, 0f,   // 左下
            0f, 1f,   // 左上
            1f, 0f,   // 右下
            1f, 1f    // 右上
    };

    /**
     * 水印纹理顶点和纹理坐标
     */
    private final FloatBuffer mDynamicVertexBuffer = Utils.createFloatBuffer(mDynamicVertexCoords);
    private final FloatBuffer mDynamicTextureBuffer = Utils.createFloatBuffer(mDynamicTextureCoords);

    public DynamicDrawable2D(){
        mVertexArray = mDynamicVertexBuffer;
        mTexCoordArray = mDynamicTextureBuffer;
        mCoordsPerVertex = 2;
        mVertexStride = mCoordsPerVertex * SIZEOF_FLOAT;
        mVertexCount = mDynamicVertexCoords.length / mCoordsPerVertex;
        mTexCoordStride = 2 * SIZEOF_FLOAT;
    }

    @Override
    public int getVertexLength(){
        return mDynamicVertexCoords.length * 4;
    }

    @Override
    public int getTexLength(){
        return mDynamicTextureCoords.length * 4;
    }

    public void setVertexBuffer(PointF leftTop , PointF leftBottom, PointF rightTop, PointF rightBottom){
        // 左上
        mDynamicVertexCoords[0] = leftTop.x;
        mDynamicVertexCoords[1] = leftTop.y;
        // 左下
        mDynamicVertexCoords[2] = leftBottom.x;
        mDynamicVertexCoords[3] = leftBottom.y;
        // 右上
        mDynamicVertexCoords[4] = rightTop.x;
        mDynamicVertexCoords[5] = rightTop.y;
        // 右下
        mDynamicVertexCoords[6] = rightBottom.x;
        mDynamicVertexCoords[7] = rightBottom.y;
        // 更新 Buffer
        mDynamicVertexBuffer.put(mDynamicVertexCoords, 0, mDynamicVertexCoords.length)
                .position(0);
    }


    public void setTextureBuffer(Point leftTop , Point leftBottom, Point rightTop, Point rightBottom){
        // 左上
        mDynamicTextureCoords[0] = leftTop.x;
        mDynamicTextureCoords[1] = leftTop.y;
        // 左下
        mDynamicTextureCoords[2] = leftBottom.x;
        mDynamicTextureCoords[3] = leftBottom.y;
        // 右上
        mDynamicTextureCoords[4] = rightTop.x;
        mDynamicTextureCoords[5] = rightTop.y;
        // 右下
        mDynamicTextureCoords[6] = rightBottom.x;
        mDynamicTextureCoords[7] = rightBottom.y;
        // 更新 Buffer
        mDynamicTextureBuffer.put(mDynamicTextureCoords, 0, mDynamicTextureCoords.length)
                .position(0);
    }

}
