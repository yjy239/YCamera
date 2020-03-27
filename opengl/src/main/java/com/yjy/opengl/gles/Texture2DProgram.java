package com.yjy.opengl.gles;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.annotation.IntDef;
import android.text.TextUtils;


import com.yjy.opengl.R;
import com.yjy.opengl.util.Utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/09
 *     desc   : Attention: this will cost too much time,please run it in other thread
 *
 *     version: 1.0
 * </pre>
 */
public class Texture2DProgram implements GLResource{

    public static final int TEXTURE_CUSTOM = 1;
    public static final int TEXTURE_2D = GLES20.GL_TEXTURE_2D;
    public static final int TEXTURE_EXTERNAL_OES = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;



    @IntDef(value = {
            TEXTURE_2D,
            TEXTURE_EXTERNAL_OES,

    })
    public @interface GLTextureType {

    }


    protected int mTextureTarget;
    protected Program mProgram;
    protected Context mContext;


    protected int muMVPMatrixLoc;
    protected int muTexMatrixLoc;


    protected int maPositionLoc;
    protected int maTextureCoordLoc;


    //一个坐标包含几个顶点
    protected int mCoordsPerVertex;
    //顶点读取步长度
    protected int mVertexStride;
    //纹理坐标读取步长
    protected int mTexCoordStride;

    //顶点个数
    protected int mVertexCount;

    protected FloatBuffer mVertexArray;

    protected FloatBuffer mTexCoordArray;

    protected int mVertexLength = 0;

    protected int mTexLength = 0;

    private int mVboID = Utils.GL_NOT_INIT;

    private boolean isCreate = false;

    protected String mVertexShader;
    protected String mFragmentShader;

    private ArrayList<Runnable> mRunnables = new ArrayList<>();


    public Texture2DProgram(Context context, @GLTextureType int type){
        mContext = context;

        mTextureTarget = type;

    }

    public Texture2DProgram(Context context,
                            String vertexShader,String fragmentShader,
                            @GLTextureType int type){
        mContext = context;

        mTextureTarget = type;
        this.mVertexShader = vertexShader;
        this.mFragmentShader = fragmentShader;

    }


    public int getTextureTarget() {
        return mTextureTarget;
    }

    @Override
    public void create() {
        if(isCreate){
            return;
        }
        if(!TextUtils.isEmpty(mVertexShader)&&!TextUtils.isEmpty(mFragmentShader)){
            createProgram(mVertexShader,mFragmentShader);
            isCreate = true;
            return;
        }

        switch (mTextureTarget) {
            case GLES20.GL_TEXTURE_2D:
                createProgram(R.raw.vertex_shader,R.raw.fragment_shader);
                break;
            case GLES11Ext.GL_TEXTURE_EXTERNAL_OES:
                createProgram(R.raw.vertex_mvp_shader,R.raw.fragment_ext_shader);
                break;
            default:
                throw new IllegalArgumentException("only support GLTextureType");
        }



        isCreate = true;
    }

    protected void createProgram(String vertexRes,String fragRes){
        mProgram = new Program.Builder()
                .addShader(new Shader(mContext,GLES20.GL_VERTEX_SHADER, vertexRes))
                .addShader(new Shader(mContext,GLES20.GL_FRAGMENT_SHADER, fragRes))
                .build();

        mProgram.create();

        Utils.checkGlError("Texture2DProgram create");


        //获得glsl的变化矩阵
        muMVPMatrixLoc = mProgram.glGetUniformLocation("uMVPMatrix");

        muTexMatrixLoc = mProgram.glGetUniformLocation("uTexMatrix");

        //顶点转化
        maPositionLoc = mProgram.getGetAttribLocation("aPosition");

        maTextureCoordLoc = mProgram.getGetAttribLocation("aTextureCoord");




    }

    private void createProgram(int vertexRes,int fragRes){
        mProgram = new Program.Builder()
                .addShader(new Shader(mContext,GLES20.GL_VERTEX_SHADER, vertexRes))
                .addShader(new Shader(mContext,GLES20.GL_FRAGMENT_SHADER, fragRes))
                .build();

        mProgram.create();




        //获得glsl的变化矩阵
        muMVPMatrixLoc = mProgram.glGetUniformLocation("uMVPMatrix");

        muTexMatrixLoc = mProgram.glGetUniformLocation("uTexMatrix");

        //顶点转化
        maPositionLoc = mProgram.getGetAttribLocation("aPosition");

        maTextureCoordLoc = mProgram.getGetAttribLocation("aTextureCoord");



    }

    public Program getProgram() {
        return mProgram;
    }

    public void deleteVbo(){
        if(mVboID <= 0){
            return;
        }
        GLES20.glDeleteBuffers(1,new int[]{mVboID},0);
        mVboID = 0;
    }

    public void addDrawMore(Runnable runnable) {
        mRunnables.add(runnable);
    }


    public void bindVBO(){

        if(mVertexArray == null||mTexCoordArray==null){
            throw new IllegalArgumentException("mVertexArray or mTexCoordArray is NULL");
        }


        if(mProgram==null||mProgram.isError()){
            throw new IllegalArgumentException("GLProgram is error");
        }


        Utils.checkGlError("bindVBO");
        mProgram.use();

        if(mVboID<=0){
            //生成VBO缓冲对象
            int[] VBOs = new int[1];
            GLES20.glGenBuffers(1,VBOs,0);
            mVboID = VBOs[0];
        }

        //绑定缓冲对象
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,mVboID);
        Utils.checkGlError("glBindBuffer");

        //开辟缓冲对象内存
        //开辟一段顶点缓冲和纹理坐标缓冲
        //GL_STATIC_DRAW 有待商榷
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                (mVertexLength+mTexLength),
                null,GLES20.GL_STATIC_DRAW);
        Utils.checkGlError("glBufferData");

        //填充数据
        //填充顶点数据
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,0,mVertexLength,
                mVertexArray);
        Utils.checkGlError("glBufferSubData");

        //填充纹理坐标
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,
                mVertexLength,mTexLength,
                mTexCoordArray);
        Utils.checkGlError("glBufferSubData");
        //解绑
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        Utils.checkGlError("glBindBuffer");

        mProgram.disable();

    }

    /**
     * Creates a texture object suitable for use with this program.
     * <p>
     * On exit, the texture will be bound.
     */
    public int createTextureObject() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        Utils.checkGlError("glGenTextures");

        int texId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId);
        Utils.checkGlError("glBindTexture " + texId);

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        Utils.checkGlError("glTexParameter");
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);

        return texId;
    }


    public int reallocTextureObject(int texId) {
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texId);
        Utils.checkGlError("glBindTexture " + texId);

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        Utils.checkGlError("glTexParameter");

        return texId;
    }


    /**
     * Creates a texture from raw data.
     *
     * @param data Image data, in a "direct" ByteBuffer.
     * @param width Texture width, in pixels (not bytes).
     * @param height Texture height, in pixels.
     * @param format Image data format (use constant appropriate for glTexImage2D(), e.g. GL_RGBA).
     * @return Handle to texture.
     */
    public int createImageTexture(ByteBuffer data, int width, int height, int format) {
        int[] textureHandles = new int[1];
        int textureHandle;

        GLES20.glGenTextures(1, textureHandles, 0);
        textureHandle = textureHandles[0];
        Utils.checkGlError("glGenTextures");

        // Bind the texture handle to the 2D texture target.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        // Configure min/mag filtering, i.e. what scaling method do we use if what we're rendering
        // is smaller or larger than the source image.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        Utils.checkGlError("loadImageTexture");

        // Load the data from the buffer into the texture handle.
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, /*level*/ 0, format,
                width, height, /*border*/ 0, format, GLES20.GL_UNSIGNED_BYTE, data);
        Utils.checkGlError("loadImageTexture");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        Utils.checkGlError("loadImageTexture");

        return textureHandle;
    }


    public int reallocImageTexture(int textureHandle,ByteBuffer data, int width, int height, int format) {

        // Bind the texture handle to the 2D texture target.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 设置纹理过滤方式

        // Configure min/mag filtering, i.e. what scaling method do we use if what we're rendering
        // is smaller or larger than the source image.
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        Utils.checkGlError("loadImageTexture");

        // Load the data from the buffer into the texture handle.
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, /*level*/ 0, format,
                width, height, /*border*/ 0, format, GLES20.GL_UNSIGNED_BYTE, data);
        Utils.checkGlError("loadImageTexture");

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureHandle;
    }


    /**
     *
     * @param coordsPerVertex 多少顶点是一个坐标
     * @param vertexStride 顶点读取步伐
     * @param vertexBuffer 坐标系
     */
    public void setAnalysisVertex(int coordsPerVertex,int vertexStride,FloatBuffer vertexBuffer,int length){
        mCoordsPerVertex = coordsPerVertex;
        mVertexStride = vertexStride;
        mVertexArray = vertexBuffer;
        mVertexLength = length;
    }

    /**
     *
     * @param texStride 纹理读取步伐
     * @param texBuffer 纹理数据
     */
    public void setAnalysisTex(int texStride,FloatBuffer texBuffer,int length){
        mTexCoordArray =texBuffer;
        mTexCoordStride = texStride;
        mTexLength = length;
    }


    public void draw(float[] mvpMatrix, float[] texMatrix, int textureId,
                     int firstVertex,int vertexCount){
        draw(mVboID,mvpMatrix, texMatrix, textureId, firstVertex, vertexCount,mTextureTarget);
    }



    public void draw(int vbo, float[] mvpMatrix, float[] texMatrix, int textureId, int firstVertex, int vertexCount) {
        draw(vbo,mvpMatrix, texMatrix, textureId, firstVertex, vertexCount,mTextureTarget);
    }



    /**
     * 绘制
     * @param mvpMatrix 世界坐标系
     * @param firstVertex 读取起点
     * @param vertexCount 顶点数量
     * @param texMatrix 纹理变化矩阵
     * @param textureId 纹理Id
     */
    public void draw(int vbo,float[] mvpMatrix, float[] texMatrix, int textureId,
                     int firstVertex,int vertexCount,int textureTarget){
        if(mTexCoordArray==null){
            throw new IllegalArgumentException("please use setAnalysisTex set Texture Buffer");
        }

        if(mVertexArray == null){
            throw new IllegalArgumentException("please use setAnalysisVertex set Vertex Buffer");
        }

        if(mProgram == null||mProgram.isError()){
            return;
        }



       mProgram.use();


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vbo <= 0?mVboID:vbo);
        Utils.checkGlError("glBindBuffer");


        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        Utils.checkGlError("Enable maPositionLoc");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, mCoordsPerVertex,
                GLES20.GL_FLOAT, false, mVertexStride, 0);

        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, mTexCoordStride, mVertexLength);

        //mProgram.setInt("sTexture",0);
        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(textureTarget, textureId);
        Utils.checkGlError("glBindTexture");

        // Copy the model / view / projection matrix over.
        if(muMVPMatrixLoc>=0){
            GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
            Utils.checkGlError("muMVPMatrixLoc");
        }



        // Copy the texture transformation matrix over.
        if(muTexMatrixLoc>=0){
            GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
            Utils.checkGlError("muTexMatrixLoc");
        }


        //draw more
        drawMore();
        Utils.checkGlError("drawMore");

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
        Utils.checkGlError("glDrawArrays");

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(mTextureTarget, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
        mProgram.disable();
    }


    protected void drawMore(){
        for (Runnable run : mRunnables){
            run.run();
        }

        mRunnables.clear();
    }



    @Override
    public void release() {
        if(mProgram !=null){
            GLES20.glDeleteBuffers(1,new int[]{mVboID},0);
            Utils.checkGlError("be release");
            mProgram.release();
            Utils.checkGlError("release");
            isCreate = false;

        }
    }

    @Override
    public boolean isError() {
        return mProgram == null||mProgram.getID() <= 0;
    }

    @Override
    public int getID() {
        return mProgram.getID();
    }

}
