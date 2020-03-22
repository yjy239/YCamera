package com.yjy.camera.Render;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import androidx.annotation.DrawableRes;

import com.yjy.camera.R;
import com.yjy.opengl.gles.DynamicDrawable2D;
import com.yjy.opengl.gles.Program;
import com.yjy.opengl.gles.Texture2DProgram;
import com.yjy.opengl.util.Utils;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/15
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class WaterFilter extends FBOFilter {

    private int mWaterTextureId = 0;

    private int mWaterVboId = Utils.GL_NOT_TEXTURE;

    private DynamicDrawable2D mDynamic2D = new DynamicDrawable2D();
    private Bitmap bitmap;
    private float mMarginLeft;
    private float mMarginTop;
    private int mResId;

    public WaterFilter(Context context, @DrawableRes int resId, float marginLeft, float marginTop){
        super(context);
        mMarginLeft = marginLeft;
        mMarginTop = marginTop;
        mResId = resId;

    }


    private Bitmap createTextureFromRes(int resId) {
        if(mWaterTextureId > 0&&bitmap!=null){
            return bitmap;
        }
        // 生成绑定纹理
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mWaterTextureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mWaterTextureId);
        // 设置环绕方向
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 设置纹理过滤方式
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // 将 Bitmap 生成 2D 纹理
        bitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        // 解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        Utils.checkGlError("createTextureFromRes");
        return bitmap;
    }




    private void setupWatermark(int surfaceWidth, int surfaceHeight) {
        Bitmap bitmap = createTextureFromRes(mResId);
        float height = bitmap.getHeight();
        float width = bitmap.getWidth();
        height = height * (1 / (float) surfaceHeight);
        width = width * (1 / (float) surfaceWidth);
        float left = -1.0f+(mMarginLeft/(float) surfaceWidth);
        float top = 1.0f-(mMarginTop/(float) surfaceHeight);
        // 设置水印的位置
        // 左上
        PointF leftTop= new PointF(),leftBottom= new PointF(),
                rightTop= new PointF(),rightBottom = new PointF();
        leftTop.x = left;
        leftTop.y = top;
        // 左下
        leftBottom.x = left;
        leftBottom.y = top - height;
        // 右上
        rightTop.x= left + width;
        rightTop.y = top ;
        // 右下
        rightBottom.x= left + width;
        rightBottom.y = top - height;
        // 更新 Buffer
        mDynamic2D.setVertexBuffer(leftTop,leftBottom,rightTop,rightBottom);

//        // 更新 VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mWaterVboId);
        Utils.checkGlError("glBindBuffer");
        // 写入水印顶点坐标
        GLES20.glBufferSubData(
                GLES20.GL_ARRAY_BUFFER,
                0,
                mDynamic2D.getVertexLength(),
                mDynamic2D.getVertexArray()
        );
        Utils.checkGlError("glBufferSubData");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        Utils.checkGlError("setupWatermark");
    }

    private void initWaterVBO(){
        if(mWaterVboId <= Utils.GL_NOT_TEXTURE){
            int vbo[] = new int[1];
            GLES20.glGenBuffers(1,vbo,0);
            mWaterVboId = vbo[0];
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,mWaterVboId);

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                mDynamic2D.getTexLength()
                        +mDynamic2D.getVertexLength(),null,
                GLES20.GL_STATIC_DRAW);

        // 写入水印顶点坐标
        GLES20.glBufferSubData(
                GLES20.GL_ARRAY_BUFFER,
                0,
                mDynamic2D.getVertexLength(),
                mDynamic2D.getVertexArray()
        );
        // 写入水印纹理坐标
        GLES20.glBufferSubData(
                GLES20.GL_ARRAY_BUFFER,
                mDynamic2D.getVertexLength(),
                mDynamic2D.getTexLength(),
                mDynamic2D.getTexCoordArray()
        );

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);

        Utils.checkGlError("initWaterVBO");

    }




    @Override
    public void onSurfaceCreated(int viewWidth, int viewHeight) {
        super.onSurfaceCreated(viewWidth,viewHeight);
        initWaterVBO();
        Utils.checkGlError("onSurfaceCreated");

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        setupWatermark(width, height);


    }

    @Override
    public int getTextureType() {
        return Texture2DProgram.TEXTURE_2D;
    }

    @Override
    public void onDrawEnd() {
        //绘制更多,绘制水印
        if(mFBODrawer == null||mFBODrawer.isError()){
            return;
        }
        mFBODrawer.drawFrame(mWaterVboId,mWaterTextureId);
    }




    @Override
    public void release() {
        super.release();
        if(bitmap != null){
            bitmap.recycle();
        }
        GLES20.glDeleteBuffers(1,new int[]{mWaterVboId},0);
        mWaterVboId= 0;

        GLES20.glDeleteTextures(1,new int[]{mWaterTextureId},0);
        mWaterTextureId=0;
    }

    @Override
    public int getTextureId() {
        return fbTextureId;
    }

}
