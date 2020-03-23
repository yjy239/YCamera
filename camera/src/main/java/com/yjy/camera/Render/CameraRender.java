package com.yjy.camera.Render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.NonNull;
import android.util.Log;


import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.SurfaceBufferCallback;
import com.yjy.opengl.widget.TakeBufferCallback;
import com.yjy.opengl.gles.FrameDrawer;
import com.yjy.opengl.util.Size;
import com.yjy.opengl.gles.Texture2DProgram;
import com.yjy.opengl.util.Utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/28
 *     desc   : 注意渲染器全流程在GL的线程中处理
 *     核心思路，绘制到FBO之后，最后绘制到屏幕上
 *     相机的自定义渲染
 *     version: 1.0
 * </pre>
 */
public class CameraRender extends BaseRender {
    public static final String TAG = CameraRender.class.getName();

    private Context mContext;
    private OESOutputFilter mOESFilter;

    private float[] matrix = new float[16];

    //设置世界坐标系
    //https://www.jianshu.com/p/4853a463d892
    private float[] mProjectionMatrix = new float[16];//投影矩阵
    private float[] mRotationMatrix = new float[16];//旋转矩阵 相当于View
    private float[] mFinalMatrix = new float[16];//裁剪矩阵

    //
    private int mVboID = Utils.GL_NOT_INIT;


    private SurfaceTexture mSurfaceTexture;

    private int mTextureId =  Utils.GL_NOT_TEXTURE;
    private ICameraDevice mPrepareListener;

    private final float[] mOESTextureMatrix = new float[16];


    private boolean isRead = false;
    private TakeBufferCallback mCallback;


    private int mSurfaceWidth = 0;
    private int mSurfaceHeight = 0;

    private ByteBuffer mBuffer;
    private int mLastWidth = 0;
    private int mLastHeight = 0;

    private Texture2DProgram mTextureProgram;
    private FrameDrawer mDrawer;
    private boolean isSync = true;


    private boolean isInit = false;


    private ArrayList<IFBOFilter> mFilters = new ArrayList<>();

    private SurfaceTexture.OnFrameAvailableListener mFrameAvailableListener;

    Runnable getShotRunnable = new Runnable() {
        @Override
        public void run() {
            if(!isSync&&isRead&&mCallback != null){

                isRead = false;
                getSurfaceBuffer(new SurfaceBufferCallback() {
                    @Override
                    public void callback(Size size,ByteBuffer buffer) {
                        if(mCallback!=null){
                            mCallback.takeCurrentBuffer(size,buffer);
                        }

                    }
                });

            }
        }
    };


    public CameraRender() {
        // 每一个OpenGL es都是在一个默认的FBO中渲染，可以通过绑定，让绘制行为转移到自己的FBO中
    }

    @Override
    public boolean isInit() {
        return isInit;
    }

    @Override
    public void setContext(Context context) {
        mContext = context;
        if(mOESFilter == null){
            mOESFilter = new OESOutputFilter(context);
        }


    }

    @Override
    public void setFilters(ArrayList<IFBOFilter> renders) {
        if(renders == null){
            return;
        }
        mFilters = renders;
    }

    @Override
    public void addFilter(IFBOFilter renders) {
        if(mFilters == null||renders == null){
            return;
        }
        if(mFilters.size() > 0){
            mFilters.get(mFilters.size()-1).removeDrawEnd(getShotRunnable);
        }

        mFilters.add(renders);


        mFilters.get(mFilters.size()-1).addDrawEnd(getShotRunnable);



    }



    @Override
    public void removeFilter(IFBOFilter renders) {
        if(mFilters == null){
            return;
        }
        mFilters.remove(renders);
    }

    @Override
    public void takeSurfaceBuffer(final TakeBufferCallback callback) {
        isRead = true;
        mCallback = callback;
        //mOESFilter.takeSurfaceBuffer(callback);

    }

    @Override
    public void setPrepareListener(ICameraDevice prepareListener) {
        this.mPrepareListener = prepareListener;
    }



    @Override
    public void onEGLContextCreated() {

    }

    @Override
    public void setFilterSync(boolean sync) {
        isSync = sync;
    }

    @Override
    public FrameDrawer getDrawer() {
        return mDrawer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        if(isInit){
            return;
        }
        if(mDrawer == null){
            mTextureProgram = new Texture2DProgram(mContext,Texture2DProgram.TEXTURE_2D);
            mTextureProgram.create();
            mDrawer = new FrameDrawer(mTextureProgram);
        }


        if(mTextureId== Utils.GL_NOT_TEXTURE){

            mTextureId = mDrawer.createTextureObject();

            //生成一个Surface
            mSurfaceTexture = new SurfaceTexture(mTextureId);
            if(mPrepareListener != null){
                mPrepareListener.onSurfacePrepare(mSurfaceTexture);
            }


            mSurfaceTexture.setOnFrameAvailableListener(mFrameAvailableListener);

            //FBO的创建
            mOESFilter.onSurfaceCreated(mViewWidth,mViewHeight);
            Utils.checkGlError("onSurfaceCreated");

            for(int i = 0; i< mFilters.size(); i++){
                mFilters.get(i).onSurfaceCreated(mViewWidth,mViewHeight);
            }

        }


        isInit = true;

        Utils.checkGlError("onSurfaceCreated");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        if(mDrawer==null||mDrawer.isError()){
            return;
        }
        //区域发生变换

        //重新设定Surface的渲染区域
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        Utils.checkGlError("onSurfaceChanged");

        mPrepareListener.changeSize(width,height);
        mOESFilter.onSurfaceChanged(width,height);

        for(int i = 0; i< mFilters.size(); i++){
            mFilters.get(i).onSurfaceChanged(width,height);
        }

        Utils.checkGlError("after onSurfaceCreated");


    }


    @Override
    public void onDrawFrame(GL10 gl) {
        //核心：绘制到FBO以及Camera的previewSurface中

        if(mDrawer==null||mDrawer.isError()){
            return;
        }

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


       drawScreenThroughFBO();

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    /**
     * 先绘制到FBO，再绘制到屏幕中
     */
    private void drawScreenThroughFBO(){


        if(mSurfaceTexture != null){
            mSurfaceTexture.updateTexImage();
            Utils.checkGlError("updateTexImage");
            //每一次更新纹理之后，需要获取变换矩阵，因为可能发生变化
            mSurfaceTexture.getTransformMatrix(mOESTextureMatrix);
        }


        //先绘制到fbo
        mOESFilter.setVertexAndTextureMatrix(mFinalMatrix,mOESTextureMatrix);

        int oesTextureId = mOESFilter.onDrawFrame(mTextureId);

        int textureId = oesTextureId;

        //不断的绘制
        for(int i = 0; i< mFilters.size(); i++){
            textureId = mFilters.get(i).onDrawFrame(textureId);
        }


        //绘制到屏幕
        if(isSync){
            drawTextureToScreen(textureId);
        }else {
            drawTextureToScreen(oesTextureId);
        }

        getShotRunnable.run();






    }


    private void drawTextureToScreen(int texture){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //背景被清空黑色
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        mDrawer.drawFrame(texture,mFinalMatrix,Utils.IDENTITY_MATRIX);

    }

    @Override
    public void release() {
        //释放
        if(mOESFilter !=null){
            mOESFilter.release();
            mOESFilter =null;
        }

        if(mTextureId>Utils.GL_NOT_TEXTURE){
            GLES20.glDeleteTextures(1,new int[]{mTextureId},0);
            mTextureId = 0;
        }
        if(mVboID > Utils.GL_NOT_INIT){
            GLES20.glDeleteBuffers(1,new int[]{mVboID},0);
            mVboID=-1;
        }
        if(mDrawer != null){
            mDrawer.release();
            mDrawer=null;
        }

        for(int i = 0; i< mFilters.size(); i++){
            mFilters.get(i).release();
        }
        if(mSurfaceTexture!=null){
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        mFilters.clear();
        mPrepareListener= null;

        mContext = null;
        isInit = false;
    }



    @Override
    public int getTextureId() {
        return mTextureId;
    }

    @Override
    public void setMatrix(@NonNull float[] matrix) {
        System.arraycopy(matrix, 0, mFinalMatrix,
                0, matrix.length);
    }

    @NonNull
    @Override
    public float[] getMatrix() {
        return mFinalMatrix;
    }

    @Override
    public void resetMatrix() {
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mRotationMatrix, 0);
        Matrix.setIdentityM(mFinalMatrix, 0);
    }

    @Override
    public void rotate(int degrees) {
        Matrix.rotateM(mRotationMatrix, 0, degrees, 0, 0, 1);
    }

    @Override
    public void centerCrop(boolean isLandscape, Size viewSize, Size surfaceSize) {
        // 设置正交投影
        float aspectPlane = viewSize.getWidth() / (float) viewSize.getHeight();
        float aspectSurface = isLandscape ? surfaceSize.getWidth() / (float) surfaceSize.getHeight()
                : surfaceSize.getHeight() / (float) surfaceSize.getWidth();

        mSurfaceHeight = surfaceSize.getHeight();
        mSurfaceWidth = surfaceSize.getWidth();

        float left, top, right, bottom;
        //centerCrop实际上就是变换了投射矩阵的大小，也就是顶点坐标的大小

//        //通过控件宽高比和Surface(纹理)的宽高比比较。
//        // 1. 纹理宽高比例 > 投影平面宽高比例
        if (aspectSurface > aspectPlane) {
            //说明w(t)/h(t) > w(v)/h(v) 说明宽度比高度的比例大。说明view的控件是偏向竖的，而surface偏向横的。平衡
            // left和right都进行等比例压缩。left是x的负半轴，right是正半轴
            left = -aspectPlane / aspectSurface;
            right = -left;
            top = 1;
            bottom = -1;
        }
        // 2. 纹理比例 < 投影平面比例
        //说明w(t)/h(t) > w(v)/h(v) 说明宽度比高度的比例小，需要平衡，因此需要变化高度
        else {
            left = -1;
            right = 1;
            top = 1 / aspectPlane * aspectSurface;
            bottom = -top;
        }
        Matrix.orthoM(
                mProjectionMatrix, 0,
                left, right, bottom, top,
                1, -1
        );


        Log.e(TAG, "preview size = " + viewSize + ", camera size = " + surfaceSize);
    }

    @Override
    public void applyMatrix() {
        Matrix.multiplyMM(mFinalMatrix, 0, mProjectionMatrix, 0,
                mRotationMatrix, 0);
    }



    public void getSurfaceBuffer(final SurfaceBufferCallback callback) {


        //小于19使用最原始的方式
        //onBindFbo();
        //读取位置从 预览宽度 - 控件宽度
        //预览大于控件，则获取控件的区域
        //预览小于控件，则获取预览区域
        int minWidth = mViewWidth;
        int minHeight = mViewHeight;



        //竖屏有限填充宽度，反过来计算

        if(mBuffer == null||((minWidth!=mLastWidth)&&(minHeight!=mLastHeight))){
            mBuffer = ByteBuffer.allocate((minWidth)*(minHeight)*4)
                    .order(ByteOrder.LITTLE_ENDIAN);
            mBuffer.position(0);
            mLastWidth = minWidth;
            mLastHeight = minHeight;
        }

        mBuffer.clear();

        GLES20.glReadPixels(0, 0, minWidth, minHeight, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, mBuffer);
        mBuffer.rewind();

        Utils.reverseBuf(mBuffer,minWidth,minHeight);
        //onUnbindFbo();
        if(callback != null){
            callback.callback(new Size(minWidth,minHeight),mBuffer);
        }


    }

    @Override
    public void setFrameAvailableListener(SurfaceTexture.OnFrameAvailableListener frameAvailableListener) {
        mFrameAvailableListener = frameAvailableListener;
    }
}
