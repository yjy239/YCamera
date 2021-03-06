#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 ft_Position;
uniform sampler2D sTexture;

void main() {
    vec4 tc = texture2D(sTexture, ft_Position);
    float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;
    gl_FragColor = vec4(color, color, color, 1.0);
}