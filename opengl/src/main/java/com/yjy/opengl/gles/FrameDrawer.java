package com.yjy.opengl.gles;

import com.yjy.opengl.util.Utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/09
 *     desc   : 模仿grafika 绘制全Frame方法
 *     version: 1.0
 * </pre>
 */
public class FrameDrawer implements GLResource{

    private Drawable2D mRectDrawable = new Drawable2D();
    private Texture2DProgram mProgram;
    private boolean isCreate = false;


    public FrameDrawer(Texture2DProgram mProgram) {
        this.mProgram = mProgram;
        create();
    }

    public FrameDrawer(Texture2DProgram mProgram, Drawable2D drawable2D) {
        this.mProgram = mProgram;
        create(drawable2D);
    }


    @Override
    public void create() {
        if(isCreate){
            return;
        }
        mProgram.setAnalysisVertex(mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(),mRectDrawable.getVertexArray(),mRectDrawable.getVertexLength());
        mProgram.setAnalysisTex(mRectDrawable.getTexCoordStride(),mRectDrawable.getTexCoordArray(),
                mRectDrawable.getTexLength());
        mProgram.bindVBO();
        isCreate = true;
    }


    public void create(Drawable2D drawable2D){

        mProgram.deleteVbo();
        mRectDrawable  = drawable2D;
        mProgram.setAnalysisVertex(mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(),mRectDrawable.getVertexArray(),mRectDrawable.getVertexLength());
        mProgram.setAnalysisTex(mRectDrawable.getTexCoordStride(),mRectDrawable.getTexCoordArray(),
                mRectDrawable.getTexLength());
        mProgram.bindVBO();
    }

    public void reset(Drawable2D drawable2D){

        mRectDrawable  = drawable2D;
        mProgram.setAnalysisVertex(mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(),mRectDrawable.getVertexArray(),mRectDrawable.getVertexLength());
        mProgram.setAnalysisTex(mRectDrawable.getTexCoordStride(),mRectDrawable.getTexCoordArray(),
                mRectDrawable.getTexLength());
        mProgram.bindVBO();
    }


    @Override
    public void release() {
        if(mProgram != null){
            mProgram.release();
            mProgram= null;
            isCreate = false;
        }



    }

    /**
     * Changes the program.  The previous program will be released.
     * <p>
     * The appropriate EGL context must be current.
     */
    public void changeProgram(Texture2DProgram program) {
        if(mProgram != null){
            mProgram.release();
        }

        mProgram = program;
    }

    /**
     * Creates a texture object suitable for use with drawFrame().
     */
    public int createTextureObject() {
        if(mProgram == null){
            return 0;
        }
        return mProgram.createTextureObject();
    }

    public int createImageTextureObject(ByteBuffer data, int width, int height, int format){
        if(mProgram == null){
            return 0;
        }
        return mProgram.createImageTexture(data, width, height, format);
    }

    public void reallocImageTexture(int textureHandle,ByteBuffer data, int width, int height, int format){
        if(mProgram == null){
            return ;
        }
        mProgram.reallocImageTexture(textureHandle, data, width, height, format);
    }

    @Override
    public boolean isError() {
        if(mProgram == null){
            return true;
        }
        return mProgram.isError();
    }

    @Override
    public int getID() {
        if(mProgram == null){
            return 0;
        }
        return mProgram.getID();
    }

    /**
     *
     * @param coordsPerVertex 多少顶点是一个坐标
     * @param vertexStride 顶点读取步伐
     * @param vertexBuffer 坐标系
     */
    public void setAnalysisVertex(int coordsPerVertex, int vertexStride, FloatBuffer vertexBuffer,int length){
        if(mProgram == null){
            throw new IllegalArgumentException("please set Texture2DProgram");
        }
        mProgram.setAnalysisVertex(coordsPerVertex, vertexStride, vertexBuffer,length);
    }

    /**
     *
     * @param texStride 纹理读取步伐
     * @param texBuffer 纹理数据
     */
    public void setAnalysisTex(int texStride,FloatBuffer texBuffer,int length){
        if(mProgram == null){
            throw new IllegalArgumentException("please set Texture2DProgram");
        }
        mProgram.setAnalysisTex(texStride, texBuffer,length);
    }

    public int getTarget(){
        return mProgram.getTextureTarget();
    }



    public Program getGLProgram(){
        return mProgram.getProgram();
    }


    /**
     * Draws a viewport-filling rect, texturing it with the specified texture object.
     */
    public void drawFrame(int textureId) {
        if(mProgram == null){
            return;
        }
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.
        mProgram.draw(Utils.IDENTITY_MATRIX,
                Utils.IDENTITY_MATRIX, textureId,0,mRectDrawable.getVertexCount());
    }



    /**
     * Draws a viewport-filling rect, texturing it with the specified texture object.
     */
    public void drawFrame(int textureId, float[] texMatrix) {
        if(mProgram == null){
            return;
        }
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.
        mProgram.draw(Utils.IDENTITY_MATRIX,
                texMatrix, textureId,0,mRectDrawable.getVertexCount());
    }


    /**
     * Draws a viewport-filling rect, texturing it with the specified texture object.
     */
    public void drawFrame(int textureId,float[] mvpMatrix, float[] texMatrix) {
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.

        if(mProgram == null){
            return;
        }
        mProgram.draw(mvpMatrix,
                texMatrix, textureId,0,mRectDrawable.getVertexCount());
    }

    public void drawFrame(int vbo,int textureId) {
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.

        if(mProgram == null){
            return;
        }
        mProgram.draw(vbo,Utils.IDENTITY_MATRIX,
                Utils.IDENTITY_MATRIX, textureId,0,mRectDrawable.getVertexCount());
    }

    public void drawFrame(int vbo,int textureId, float[] mvpMatrix, float[] texMatrix) {
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.

        if(mProgram == null){
            return;
        }
        mProgram.draw(vbo,mvpMatrix,
                texMatrix, textureId,0,mRectDrawable.getVertexCount());
    }

    public void reallocTextureObject(int mTextureId) {
        if(mProgram == null){
            return;
        }
        mProgram.reallocTextureObject(mTextureId);
    }

    public void addDrawMore(Runnable runnable) {
        mProgram.addDrawMore(runnable);
    }
}
