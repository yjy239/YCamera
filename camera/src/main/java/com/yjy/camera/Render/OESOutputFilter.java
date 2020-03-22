package com.yjy.camera.Render;

import android.content.Context;

import com.yjy.opengl.gles.Texture2DProgram;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/16
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OESOutputFilter extends FBOFilter {
    public OESOutputFilter(Context context) {
        super(context);
    }


    @Override
    public int getTextureType() {
        return Texture2DProgram.TEXTURE_EXTERNAL_OES;
    }
}
