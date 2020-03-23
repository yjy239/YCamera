package com.yjy.opengl.gles;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.IntDef;
import android.text.TextUtils;


import com.yjy.opengl.util.Utils;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/29
 *     desc   : 着色器
 *     version: 1.0
 * </pre>
 */
public class Shader implements GLResource {

    private int mId;

    protected String mContent;

    protected int mResId;

    protected Context mContext;

    private int mType = 0;


    @IntDef(value = {
            GLES20.GL_VERTEX_SHADER,
            GLES20.GL_FRAGMENT_SHADER

    })
    public @interface ShaderType {

    }


    public Shader(Context context,@ShaderType int type,String content){
        mContent = content;
        mContext = context;
        mType = type;
    }


    public Shader(Context context,@ShaderType int type,int resId){
        mResId = resId;
        mContext = context;
        mType = type;
    }


    @Override
    public void create() {
        if(mContext == null&&mResId != 0){
            throw new IllegalArgumentException("Shader must be had resId");
        }

        if(mResId == 0&& TextUtils.isEmpty(mContent)){
            throw new IllegalArgumentException("Shader must be had resource");
        }



        if(!TextUtils.isEmpty(mContent)){
            mId = Utils.loadShader(mType,mContent);
        }else{
            mContent = Utils.getGLResource(mContext,mResId);
            mId = Utils.loadShader(mType,mContent);
        }

    }



    @Override
    public void release() {
        //销毁
        if(mId > 0){
            GLES20.glDeleteShader(mId);
        }

        mContext = null;
        mId = 0;

    }

    @Override
    public boolean isError() {
        return mId == 0;
    }

    @Override
    public int getID() {
        return mId;
    }
}
