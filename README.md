# YCamera
关于相机相关的应用。可以自由添加，删除滤镜的Camera。

# 使用：
```
ICameraFragment mCamera = new CameraFragmentBuilder(this,R.id.camera_layout)
                .setAspectRatio(AspectRatio.DEFAULT)
                .setZoomSensitive(3)
                .setAdjustViewBounds(true)
                .setSoftwareZoom(false)
                .addFilter(new WaterFilter(this,
                        R.drawable.ic_launcher,
                        CameraUtils.dp2px(this,100),
                        CameraUtils.dp2px(this,40)))
                .setFilterSync(true)
                .setFlash(ICameraDevice.FLASH_TORCH)
                .setFacing(ICameraDevice.FACING_BACK)
                .asSurface()
                .setAutoFocus(true)
                .build();
```

ICameraFragment 实际上是一个Fragment。目的是让其可以自动监听整个Activity的生命周期，进而自动销毁不必要的OpenGL等资源。

Builder的构造函数是指Fragment需要添加的父布局。

### setAspectRatio
设置相机的比例是4:3,16:9,1:1等。这个属性和setAdjustViewBounds向排斥。

### setAdjustViewBounds
当前Camera生成的图像是否大小适配View的大小。当设置为true，生成的图像将会View大小范围一致。设置为false，则生成的图像会根据setAspectRatio比例进行调整

### setZoomSensitive
双指进行硬件缩放的敏感度，越小越敏感。最小为1.

### setSoftwareZoom
setSoftwareZoom则是是否使用软件模拟的缩放，false是指优先使用硬件缩放，硬件不支持将会使用软件缩放。true是指强制使用软件模拟缩放。

### addFilter
添加一个滤镜，为相机添加特效。内置一个常用的水印添加。

#### WaterFilter 水印滤镜

```java
new WaterFilter(this,view,
                CameraUtils.dp2px(this,100)
                ,CameraUtils.dp2px(this,40),true);
```
允许添加View作为水印，也允许添加Bitmap作为水印。

#### BlackWhiteFilter
黑白滤镜，相机生成图像为黑白


### setFilterSync
添加的滤镜是否同步到相机上预览。true是同步到相机预览界面中，false则不同步。不关是否同步，只要添加了滤镜最后获得的图像必定带着滤镜效果。


### setFacing
设置前后摄像机

### setAutoFocus
是否进行自动聚焦

### setFlash
设置闪光灯效果。ICameraDevice.FLASH_TORCH 打开闪光灯，ICameraDevice.FLASH_OFF关闭闪光灯


### asSurface
YCamera以SurafceView为主体进行显示

### asTexture
YCamera以TextureView为主体进行显示

### takePhoto
```
mCamera.takePhoto(new TakePhotoCallback() {
                        @Override
                        public void takePhoto(Bitmap bitmap) {
                            img.setImageBitmap(bitmap);
                        }
                    });
```
将获得当前添加滤镜后的照片。


## 更多自定义
如果不想被CameraFragment束缚，开放核心View的对象YCameraView进行更多配置。
```
    <declare-styleable name="camera_view">
        <!-- 非自动聚焦时候显示聚焦框的ui -->
        
        <attr name="add_focus_view" format="boolean"/>
        <attr name="focus_view_size" format="dimension"/>
        <attr name="focus_start_color" format="color"/>
        <attr name="focus_end_color" format="color"/>

        <!--Camera View type-->
        <attr name="camera_type" format="enum">
        <!-- YCamera 以SurfaceView添加到ui -->
            <enum name="surface" value="0"/>
            <!-- YCamera 以TextureView添加到ui -->
            <enum name="texture" value="1"/>
        </attr>

        <attr name="auto_focus" format="boolean"/>
        <attr name="auto_flash" format="boolean"/>
        <attr name="radio" format="enum">
            <enum name="four2three" value="0"/>
            <enum name="sixteen2nine" value="1"/>
        </attr>

        <attr name="face" format="enum">
            <enum name="front" value="0"/>
            <enum name="back" value="1"/>
        </attr>


        <attr name="adjust_view" format="boolean"/>

    </declare-styleable>
```


- YCameraView.openCamera 打开相机


- YCameraView.stopCamera 关闭相机

- onDestroy 销毁相机中的资源。


### 添加自定义Filter滤镜

所有的滤镜都会按照添加顺序渲染到屏幕上。比如说，先添加一个水印再添加一个黑白滤镜，则水印就跟着全局会变成黑白。
但是如果先添加黑白滤镜，再添加水印。则会相机都变成黑白的基础上，添加了一个彩色的水印。

```
public class BlackWhiteFilter extends FBOFilter {

    public BlackWhiteFilter(Context context) {
        super(context, Utils.getGLResource(context, R.raw.vertex_shader),
                Utils.getGLResource(context, R.raw.fragment_bw));
    }
}
```

允许通过这种方式进行自定义的顶点着色器和片元着色器的设置。

需要更多定制的Filter，如KernelFilter,则是控制一个3*3的卷积核的Filter实现更多的效果：
```
public abstract class KernelFilter extends FBOFilter {
    private KernelTexture2DProgram mProgram;
    private float mColorAdj = 0.0f;

    public KernelFilter(Context context,float colorAdj) {
        super(context);
        this.mColorAdj = colorAdj;
    }

    public KernelFilter(Context context) {
        super(context);
        this.mColorAdj = 0f;
    }

    @Override
    public Texture2DProgram getTextureProgram() {
        mProgram =
                new KernelTexture2DProgram(mContext,getTextureType());
        mProgram.create();

        float[] kernel = getKernel();

        mProgram.setKernel(kernel,mColorAdj);
        return mProgram;
    }


    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        mProgram.setTexSize(width,height);
    }


    public abstract float[] getKernel();


    @Override
    public void release() {
        super.release();
        if(mProgram != null){
            mProgram.release();
            mProgram = null;
        }

    }
}

```
> Texture2DProgram 代表了整个OpenGL es的运行核心OpenGL es的着色器程序。自带了一个纹理的逻辑。

> 允许自定义 FrameDrawer。FrameDrawer将会控制Texture2DProgram，以及外部设置的纹理和定点坐标进行调控整个OpenGL es绘制行为。


待补充...


