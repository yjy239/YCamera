package com.yjy.opengl.util;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Log;

import com.yjy.opengl.widget.EGLLogWrapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.opengles.GL;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Utils {

    public static final String PROGRAM = "PROGRAM";
    public static final String VERTEX = "VERTEX";
    public static final String FRAGMENT = "FRAGMENT";

    // 从初始化失败
    public static final int GL_NOT_INIT = -1;
    // 没有Texture
    public static final int GL_NOT_TEXTURE = 0;

    public static final String TAG = Utils.class.getName();

    private static boolean isDebug = false;


    //单位矩阵
    public static final float[] IDENTITY_MATRIX;
    static {
        IDENTITY_MATRIX = new float[16];
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }

    public static boolean isIsDebug() {
        return isDebug;
    }

    public static void setDebug(boolean isDebug) {
        Utils.isDebug = isDebug;
    }

    /**
     * mmap 在底层直接映射一段直接缓存
     * @param coords
     * @return
     */
    public static FloatBuffer createFloatBuffer(float[] coords) {
        FloatBuffer buffer = ByteBuffer.allocateDirect(coords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(coords, 0, coords.length)
                .position(0);
        return buffer;
    }


    public static String getGLResource(Context context, int rawId) {
        InputStream inputStream = context.getResources().openRawResource(rawId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }


    /**
     * 编译着色器
     *
     * @param shaderType 着色器的类型
     * @param source     资源源代码
     */
    public static int loadShader(int shaderType, String source) {
        // 创建着色器 ID
        int shaderId = GLES20.glCreateShader(shaderType);
        if (shaderId != 0) {
            // 1. 将着色器 ID 和着色器程序内容关联
            GLES20.glShaderSource(shaderId, source);
            // 2. 编译着色器
            GLES20.glCompileShader(shaderId);
            // 3. 验证编译结果
            if(!checkCompileSucess(shaderId,shaderType)){
                GLES20.glDeleteShader(shaderId);
                return 0;
            }
        }
        return shaderId;
    }


    private static boolean checkShaderSucess(int shaderId, String Tag){
        int[] status = new int[1];
        GLES20.glGetShaderiv(shaderId, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            // 编译失败删除这个着色器 id
            String result = GLES20.glGetShaderInfoLog(shaderId);
            try{
                throw new IllegalStateException("OpenGL es compile error"+Tag+result);
            }catch (Exception e){
                e.printStackTrace();
            }
//            Log.e("OpenGL es compile error",Tag+result);
            return false;
        }else {
            return true;
        }
    }


    public static boolean checkCompileSucess(int shaderId, int type){
        if(type == GLES20.GL_VERTEX_SHADER){
            return checkShaderSucess(shaderId,"VERTEX_SHADER ERROR ");
        }else if(type == GLES20.GL_FRAGMENT_SHADER){
            return checkShaderSucess(shaderId,"FRAGMENT_SHADER ERROR ");
        }else {
            int[] status = new int[1];
            GLES20.glGetProgramiv(shaderId, GLES20.GL_LINK_STATUS, status, 0);
            if(status[0] != GLES20.GL_TRUE){
                String result = GLES20.glGetProgramInfoLog(shaderId);
                Log.e("Program LINK Errors",result);
                return false;
            }else {
                return true;
            }
        }

    }



    public static void reverseBuf(ByteBuffer buf, int width, int height)
    {
        long ts = System.currentTimeMillis();
        int i = 0;
        byte[] tmp = new byte[width * 4];
        while (i++ < height / 2)
        {
            buf.get(tmp);
            System.arraycopy(buf.array(), buf.limit() - buf.position(), buf.array(), buf.position() - width * 4, width * 4);
            System.arraycopy(tmp, 0, buf.array(), buf.limit() - buf.position(), width * 4);
        }
        buf.rewind();
        Log.d(TAG, "reverseBuf took " + (System.currentTimeMillis() - ts) + "ms");
    }


    public static void checkGlError(String op) {
        if(!isDebug){
            return;
        }
       int error = GLES10.glGetError();
        if (error != GLES10.GL_NO_ERROR) {
            String msg = op + ": glError :" + EGLLogWrapper.getErrorString(error);
            Log.e(TAG, msg);
            throw new RuntimeException(msg);
        }
    }


    /**
     * 绑定纹理
     * @param location  句柄
     * @param texture   纹理id
     * @param index     索引
     */
    public static void bindTexture(int location, int texture, int index) {
        bindTexture(location, texture, index, GLES20.GL_TEXTURE_2D);
    }

    /**
     * 绑定纹理
     * @param location  句柄
     * @param texture   纹理值
     * @param index     绑定的位置
     * @param textureType 纹理类型
     */
    public static void bindTexture(int location, int texture, int index, int textureType) {
        // 最多支持绑定32个纹理
        if (index > 31) {
            throw new IllegalArgumentException("index must be no more than 31!");
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        GLES20.glBindTexture(textureType, texture);
        GLES20.glUniform1i(location, index);
    }




}
