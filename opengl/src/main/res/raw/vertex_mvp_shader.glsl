// 定义一个属性，顶点坐标
attribute vec4 aPosition;
// 定义一个属性，纹理坐标
attribute vec4 aTextureCoord;
// varying 可用于相互传值
varying vec2 ft_Position;
// 顶点的正交投影矩阵
uniform mat4 uMVPMatrix;
//传进来的纹理旋转矩阵，不然Surface怎么旋转都没用
uniform mat4 uTexMatrix;


void main() {
    // 赋值 ft_Position;gl_Position 变量是 gl 内置的
    //因为是2d只需要获取s和t轴
    ft_Position = (uTexMatrix * aTextureCoord).xy;
    gl_Position = uMVPMatrix * aPosition;
}