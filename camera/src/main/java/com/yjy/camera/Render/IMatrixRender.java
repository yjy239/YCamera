package com.yjy.camera.Render;

import androidx.annotation.NonNull;

import com.yjy.opengl.util.Size;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface IMatrixRender {

    /**
     * 获取渲染的TextureID
     * @return
     */
    int getTextureId();

    /**
     * 设置世界坐标矩阵
     * @param matrix
     */
    void setMatrix(@NonNull float[] matrix);

    /**
     * 获取世界坐标矩阵
     * @return
     */
    @NonNull
    float[] getMatrix();

    /**
     * 重置世界坐标
     */
    void resetMatrix();

    /**
     * 旋转
     * @param degrees
     */
    void rotate(int degrees);

    /**
     * 居中裁剪 要么适应宽度裁剪高度，要么适应高度裁剪宽度
     * @param isLandscape
     * @param viewSize 控件大小
     * @param surfaceSize 预览大小
     */
    void centerCrop(boolean isLandscape, Size viewSize, Size surfaceSize);

    /**
     * 合并矩阵
     */
    void applyMatrix();
}
