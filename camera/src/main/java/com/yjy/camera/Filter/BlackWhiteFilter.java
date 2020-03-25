package com.yjy.camera.Filter;

import android.content.Context;

import com.yjy.camera.R;
import com.yjy.opengl.util.Utils;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class BlackWhiteFilter extends FBOFilter {

    public BlackWhiteFilter(Context context) {
        super(context, Utils.getGLResource(context, R.raw.vertex_shader),
                Utils.getGLResource(context, R.raw.fragment_bw));
    }
}
