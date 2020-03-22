package com.yjy.opengl.gles;

import com.yjy.opengl.util.Utils;

import java.nio.FloatBuffer;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/09
 *     desc   : 模仿grafkia 一个满屏幕的纹理，顶点坐标系
 *     version: 1.0
 * </pre>
 */
public class Drawable2D {

    //顶点坐标设置最好保持逆时针，为后面面剔除作准备
    private static final float[] FullVertexCoordinate = {
            -1f, -1f,//左下
            1f, -1f,//右下
            -1f, 1f,//左上
            1f, 1f//右上

    };

    //同理 https://www.jianshu.com/p/9c58cd895fa5
    private static final float[] FullTexCoordinate = {
            0.0f, 0.0f,     // 0 bottom left
            1.0f, 0.0f,     // 1 bottom right
            0.0f, 1.0f,     // 2 top left
            1.0f, 1.0f      // 3 top right
    };

    private static final FloatBuffer FULL_RECTANGLE_BUF =
            Utils.createFloatBuffer(FullVertexCoordinate);
    private static final FloatBuffer FULL_RECTANGLE_TEX_BUF =
            Utils.createFloatBuffer(FullTexCoordinate);

    //选中的
    protected FloatBuffer mVertexArray;
    protected FloatBuffer mTexCoordArray;

    protected static final int SIZEOF_FLOAT = 4;

    //一个坐标包含几个顶点
    protected int mCoordsPerVertex;
    //顶点读取步长度
    protected int mVertexStride;
    //纹理坐标读取步长
    protected int mTexCoordStride;

    //顶点个数
    protected int mVertexCount;


    public Drawable2D(){
        mVertexArray = FULL_RECTANGLE_BUF;
        mTexCoordArray = FULL_RECTANGLE_TEX_BUF;
        mCoordsPerVertex = 2;
        mVertexStride = mCoordsPerVertex * SIZEOF_FLOAT;
        mVertexCount = FullVertexCoordinate.length / mCoordsPerVertex;
        mTexCoordStride = 2 * SIZEOF_FLOAT;
    }


    public int getVertexLength(){
        return FullVertexCoordinate.length * 4;
    }

    public int getTexLength(){
        return FullTexCoordinate.length * 4;
    }


    /**
     * Returns the array of vertices.
     * <p>
     * To avoid allocations, this returns internal state.  The caller must not modify it.
     */
    public FloatBuffer getVertexArray() {
        return mVertexArray;
    }

    /**
     * Returns the array of texture coordinates.
     * <p>
     * To avoid allocations, this returns internal state.  The caller must not modify it.
     */
    public FloatBuffer getTexCoordArray() {
        return mTexCoordArray;
    }


    /**
     * Returns the number of vertices stored in the vertex array.
     */
    public int getVertexCount() {
        return mVertexCount;
    }

    /**
     * Returns the width, in bytes, of the data for each vertex.
     */
    public int getVertexStride() {
        return mVertexStride;
    }

    /**
     * Returns the width, in bytes, of the data for each texture coordinate.
     */
    public int getTexCoordStride() {
        return mTexCoordStride;
    }

    /**
     * Returns the number of position coordinates per vertex.  This will be 2 or 3.
     */
    public int getCoordsPerVertex() {
        return mCoordsPerVertex;
    }




}
