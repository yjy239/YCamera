package com.yjy.opengl.gles;

import android.content.Context;

import com.yjy.opengl.R;
import com.yjy.opengl.util.Utils;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/24
 *     desc   : 3*3 卷积核矩阵
 *     version: 1.0
 * </pre>
 */
public class KernelTexture2DProgram extends Texture2DProgram {

    private float[] mKernel = new float[KERNEL_SIZE];
    private float[] mTexOffset;

    private static final int KERNEL_SIZE = 9;
    //核心思想，生成一个空的四维矩阵,texture2D生成rbga的像素数据。
    // 把像素矩阵每一次和卷积核的每一行乘，赋值到对应行的sum中
    private static final String FRAGMENT_SHADER_KERNEL =
                    "#define KERNEL_SIZE " + KERNEL_SIZE + "\n" +
                    "precision highp float;\n" +
                    "varying vec2 ft_Position;\n" +
                    "uniform sampler2D sTexture;\n" +
                    "uniform float uKernel[KERNEL_SIZE];\n" +
                    "uniform vec2 uTexOffset[KERNEL_SIZE];\n" +
                    "uniform float uColorAdjust;\n" +
                    "void main() {\n" +
                    "    int i = 0;\n" +
                    "    vec4 sum = vec4(0.0);\n" +
                    "    for (i = 0; i < KERNEL_SIZE; i++) {\n" +
                    "         vec4 texc = texture2D(sTexture, ft_Position + uTexOffset[i]);\n" +
                    "         sum += texc * uKernel[i];\n" +
                    "    }\n" +
                    "    sum += vec4(uColorAdjust,uColorAdjust,uColorAdjust,1.0) ;\n" +
                    "    gl_FragColor = sum;\n" +
                    "}\n";
    private float mColorAdjust;
    private int muKernelLoc;
    private int muColorAdjustLoc;
    private int muTexOffsetLoc;


    public KernelTexture2DProgram(Context context, int type) {
        super(context, Utils.getGLResource(context, R.raw.vertex_shader), FRAGMENT_SHADER_KERNEL, type);

    }

    @Override
    protected void createProgram(String vertexRes, String fragRes) {
        super.createProgram(vertexRes, fragRes);
        muKernelLoc = mProgram.glGetUniformLocation("uKernel");
        muColorAdjustLoc = mProgram.glGetUniformLocation("uColorAdjust");
        muTexOffsetLoc = mProgram.glGetUniformLocation("uTexOffset");
        setKernel(new float[] {0f, 0f, 0f,  0f, 1f, 0f,  0f, 0f, 0f}, 0.0f);
        setTexSize(256, 256);
    }

    public void setKernel(float[] values, float colorAdj){
        if (values.length != KERNEL_SIZE) {
            throw new IllegalArgumentException("Kernel size is " + values.length +
                    " vs. " + KERNEL_SIZE);
        }
        System.arraycopy(values, 0, mKernel, 0, KERNEL_SIZE);
        mColorAdjust = colorAdj;
    }

    public void setTexSize(int width, int height) {
        float rw = 1.0f / width;
        float rh = 1.0f / height;

        // Don't need to create a new array here, but it's syntactically convenient.
        mTexOffset = new float[] {
                -rw, -rh,   0f, -rh,    rw, -rh,
                -rw, 0f,    0f, 0f,     rw, 0f,
                -rw, rh,    0f, rh,     rw, rh
        };
        //Log.d(TAG, "filt size: " + width + "x" + height + ": " + Arrays.toString(mTexOffset));
    }



    @Override
    protected void drawMore() {
        mProgram.setFloatVec("uKernel",mKernel);

        mProgram.setFloatVec2("uTexOffset",mTexOffset);

        mProgram.setFloat("uColorAdjust",mColorAdjust);
    }
}
