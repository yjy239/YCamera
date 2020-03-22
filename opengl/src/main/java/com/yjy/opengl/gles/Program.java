package com.yjy.opengl.gles;

import android.opengl.GLES20;

import com.yjy.opengl.util.Utils;

import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/29
 *     desc   : 着色器程序 注意是耗时，需要在异步线程中操作
 *     version: 1.0
 * </pre>
 */
public class Program implements GLResource {
    private int mId;

    protected final ArrayList<Shader> mShaders;

    private final ArrayList<Integer> mShaderIDs = new ArrayList<>();


    private static final int PROGRAM = 0;

    private boolean isCompileWithRelease = false;

    Program(ArrayList<Shader> shaders,boolean withRelease){
        this.mShaders = shaders;
        isCompileWithRelease = withRelease;
    }


    @Override
    public void create() {
        //编译每一个Shader
        if(mShaders.size()<=0){
            return;
        }
        for(Shader shader : mShaders){
            shader.create();
            int id = shader.getID();
            //一旦其中一个出错就没必要继续下去了
            if(id > 0){
                mShaderIDs.add(id);
            }else {
                break;
            }
        }


        //说明所有编译成功
        if(mShaderIDs.size() > 0&&mShaderIDs.size()==mShaders.size()){
            //生成 着色器程序
            mId = GLES20.glCreateProgram();
            //绑定 着色器
            for(int id : mShaderIDs){
                GLES20.glAttachShader(mId,id);
            }

            //链接
            GLES20.glLinkProgram(mId);

        }else {
            mId = 0;
        }


        //释放每一个着色器程序
        if(isCompileWithRelease
                &&Utils.checkCompileSucess(mId,PROGRAM)){
            for(Shader shader:mShaders){
                shader.release();
            }
        }



    }

    @Override
    public void release() {
        if(mId <= 0){
            return;
        }
        mShaderIDs.clear();
        Utils.checkGlError("be release");
        for(Shader shader : mShaders){
            shader.release();
        }

        Utils.checkGlError("release");

        mShaders.clear();

        if(mId > 0){
            GLES20.glDeleteProgram(mId);
        }


        Utils.checkGlError("glDeleteProgram");

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


    //-------Program 操作方法---------

    public void use(){
        if(mId != 0){
            GLES20.glValidateProgram(mId);
            Utils.checkGlError("glValidateProgram");
            GLES20.glUseProgram(mId);
            Utils.checkGlError("glUseProgram");
        }
    }


    public void disable(){
        GLES20.glUseProgram(0);
        Utils.checkGlError("glUseProgram");
    }


    public void setFloat(String name,float param){
        if(mId == 0){
            return;
        }
        GLES20.glUniform1f(GLES20.glGetUniformLocation(mId,name),param);
        Utils.checkGlError("glUniform1f");
    }


    public void setInt(String name,int param){
        if(mId == 0){
            return;
        }
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mId,name),param);
        Utils.checkGlError("glUniform1i");
    }


    public void setBoolean(String name,boolean param){
        if(mId == 0){
            return;
        }
        GLES20.glUniform1i(GLES20.glGetUniformLocation(mId,name),param?1:0);
        Utils.checkGlError("setBoolean");
    }


    public void setMat4v(String name, FloatBuffer param){
        if(mId == 0){
            return;
        }
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mId,name),
                1,false,param);
        Utils.checkGlError("glUniformMatrix4fv");
    }


    public void setMat4v(String name, float[] param){
        if(mId == 0){
            return;
        }
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(mId,name),
                1,false,param,0);
        Utils.checkGlError("setMat4v");
    }


    public void setVec3(String name, float x,float y,float z){
        if(mId == 0){
            return;
        }
        GLES20.glUniform3f(GLES20.glGetUniformLocation(mId,name),
                x,y,z);
        Utils.checkGlError("glUniform3f");
    }

    public int getGetAttribLocation(String name){
        return GLES20.glGetAttribLocation(mId,name);
    }


    public int glGetUniformLocation(String name){
        return GLES20.glGetUniformLocation(mId,name);
    }



    public static class Builder{
        ArrayList<Shader> mShaders = new ArrayList<>();

        public Builder addShader(Shader shader){
            mShaders.add(shader);
            return this;
        }


        public Builder removeShader(Shader shader){
            mShaders.remove(shader);
            return this;
        }

        public Program build(){
            return new Program(mShaders,true);
        }


        public Program build(boolean withRelease){
            return new Program(mShaders,withRelease);
        }

    }
}
