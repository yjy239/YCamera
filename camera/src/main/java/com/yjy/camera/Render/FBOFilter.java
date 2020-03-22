package com.yjy.camera.Render;

import android.content.Context;
import android.opengl.GLES20;
import android.text.TextUtils;
import android.util.Log;

import com.yjy.opengl.gles.FrameDrawer;
import com.yjy.opengl.gles.Texture2DProgram;
import com.yjy.opengl.util.Utils;

import java.nio.ByteBuffer;


/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FBOFilter implements IFBOFilter {

    private static final String LOG_TAG = FBOFilter.class.getName();
    private static final String TAG = FBOFilter.class.getName();
    protected int mFramebufferId = Utils.GL_NOT_INIT;
    protected int fbTextureId;
    //private Program mWaterProgram;

    private int mViewWidth;
    private int mViewHeight;
    private ByteBuffer mBuffer;


    protected FrameDrawer mFBODrawer;
    protected Context mContext;
    private float[] mVertexMatrix;//裁剪矩阵
    private float[] mOESTextureMatrix;


    protected String mVertexShader;
    protected String mFragmentShader;


    public FBOFilter(Context context){
        mContext = context;
    }

    public FBOFilter(Context context, String vertexShader, String fragmentShader){
        mContext = context;
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;

        if(TextUtils.isEmpty(mVertexShader)||TextUtils.isEmpty(mFragmentShader)){
            throw new IllegalArgumentException("shader can not be null");
        }
    }


    public void setVertexAndTextureMatrix(float[] vertexMatrix,float[] OESTextureMatrix){
        this.mVertexMatrix = vertexMatrix;
        this.mOESTextureMatrix = OESTextureMatrix;
    }

    @Override
    public Texture2DProgram getTextureProgram() {
        Texture2DProgram program;
        if(!TextUtils.isEmpty(mVertexShader)&&!TextUtils.isEmpty(mFragmentShader)){
            program = new Texture2DProgram(mContext,mVertexShader,mFragmentShader);
        }else {
            program = new Texture2DProgram(mContext,getTextureType());
        }

        program.create();
        return program;
    }

    @Override
    public FrameDrawer getFrameDrawer() {
        return new FrameDrawer(getTextureProgram());
    }

    @Override
    public void onSurfaceCreated(int viewWidth, int viewHeight) {
        mFBODrawer = getFrameDrawer();
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;

    }

    @Override
    public void reset() {
        Context context = mContext;
        release();
        mContext = context;
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        GLES20.glViewport(0, 0, width, height);
        setUpTexture(width,height);
        setUpFbo();


    }

    @Override
    public @Texture2DProgram.GLTextureType int getTextureType() {
        return Texture2DProgram.TEXTURE_2D;
    }


    protected void setUpFbo() {
        if(mFramebufferId>Utils.GL_NOT_INIT){
            return;
        }

        int[] fbos = new int[1];
        GLES20.glGenFramebuffers(1,fbos,0);
        mFramebufferId = fbos[0];
        //绑定fbo
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFramebufferId);
        //设置纹理为帧缓冲的颜色附件
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
                //颜色附件
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                fbTextureId,0);
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(LOG_TAG, "Failed to create framebuffer!!!");
        }


        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);

        Utils.checkGlError("setUpFbo");

    }


    private void setUpTexture(int width,int height){
        if(mFBODrawer == null||mFBODrawer.isError()){
            return;
        }
        if(fbTextureId == 0){
            //生成纹理
            int textures[] = new int[1];
            GLES20.glGenTextures(1,textures,0);
            fbTextureId = textures[0];

            fbTextureId = mFBODrawer.createImageTextureObject(null,width,height,GLES20.GL_RGBA);
        }else {
            mFBODrawer.reallocImageTexture(fbTextureId,null,width,height,GLES20.GL_RGBA);
        }


        Utils.checkGlError("setUpTexture");

    }

    protected void onBind(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFramebufferId);
        Utils.checkGlError("glBindFramebuffer");
    }

    protected void onUnBind(){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
        Utils.checkGlError("glBindFramebuffer");
    }

    public void onDrawBegin(){

    }

    public void addDrawMore(Runnable runnable){
        if(mFBODrawer == null){
            return;
        }
        mFBODrawer.addDrawMore(runnable);
    }


    @Override
    public int onDrawFrame(int textureId) {
        if(mFBODrawer == null||mFBODrawer.isError()){
            return textureId;
        }


        //绑定FBO
        onBind();
        onDrawBegin();
        mFBODrawer.drawFrame(textureId,mVertexMatrix,
                mOESTextureMatrix);

        onDrawEnd();
        onUnBind();

        return fbTextureId;


    }

    public void onDrawEnd() {

    }

    @Override
    public void release() {
        //销毁
        if(mFBODrawer != null){
            mFBODrawer.release();
            mFBODrawer = null;
        }

        if(fbTextureId > 0){
            GLES20.glDeleteTextures(1,new int[]{fbTextureId},0);
            fbTextureId = 0;
        }

        if(mFramebufferId>Utils.GL_NOT_INIT){
            GLES20.glDeleteFramebuffers(1,new int[]{mFramebufferId},0);
            mFramebufferId = Utils.GL_NOT_INIT;
        }

        mContext = null;



    }

    @Override
    public int getTextureId() {
        return fbTextureId;
    }



}
