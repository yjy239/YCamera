package com.yjy.opengl.gles;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface GLResource {
    /**
     * 创建OpenGL es资源
     */
    void create();


    /**
     * 释放OpenGL es资源
     */
    void release();

    /**
     * 创建OpenGL es资源是否出错
     * @return
     */
    boolean isError();

    /**
     * 获取OpenGL es资源id
     * @return OpenGL es资源id
     */
    int getID();
}
