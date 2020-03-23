package com.yjy.camera.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;


import com.yjy.camera.Camera.CameraOneDevice;
import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Engine.CameraParam;
import com.yjy.camera.R;
import com.yjy.camera.Render.IFBOFilter;
import com.yjy.camera.Utils.AspectRatio;
import com.yjy.camera.Utils.CameraUtils;
import com.yjy.camera.Utils.ScreenOrientationDetector;
import com.yjy.opengl.util.Size;

import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/29
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class YCameraView extends FrameLayout
        implements ICameraDevice.OnCameraReadyListener,
        ScreenOrientationDetector.OnDisplayChangedListener {


    private IPreview mCameraSurfaceView;
    private CameraParam mParams;
    private ICameraDevice mDevice;

    private CameraFocusView mFocusView;
    private ScreenOrientationDetector mScreenOrientationDetector;



    public YCameraView(@NonNull Context context) {
        this(context,null);
    }

    public YCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public YCameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context,AttributeSet attrs){
        mParams = new CameraParam();
        this.mScreenOrientationDetector = new ScreenOrientationDetector(context, this);
        mDevice = new CameraOneDevice(mParams,this);


        setAutoFocus(true);
        setAdjustViewBounds(true);
        parseAttr(context,attrs);

    }

    private void parseAttr(Context context,AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.camera_view);
        //parse Camera Params
        int type = typedArray.getInteger(R.styleable.camera_view_camera_type,0);

        if(type == 1){
            this.mCameraSurfaceView = new TexturePreviewer(context,mParams,mDevice);
        }else {
            this.mCameraSurfaceView = new SurfacePreviewer(context,mParams,mDevice);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mCameraSurfaceView.getView(),params);

        boolean autoFocus = typedArray.getBoolean(R.styleable.camera_view_auto_focus,true);
        setAutoFocus(autoFocus);


        boolean flash = typedArray.getBoolean(R.styleable.camera_view_auto_flash,false);
        setFlash(!flash?ICameraDevice.FLASH_OFF:ICameraDevice.FLASH_AUTO);

        int radio = typedArray.getInteger(R.styleable.camera_view_radio,0);
        if(radio == 1){
            setAspectRatio(AspectRatio.of(16,9));
        }else {
            setAspectRatio(AspectRatio.DEFAULT);
        }

        int face = typedArray.getInteger(R.styleable.camera_view_face,0);
        if(face == 1){
            setFacing(ICameraDevice.FACING_FRONT);
        }else {
            setFacing(ICameraDevice.FACING_BACK);
        }

        boolean adjustView = typedArray.getBoolean(R.styleable.camera_view_adjust_view,true);
        setAdjustViewBounds(adjustView);

        // parse Focus view Params
        boolean add = typedArray.getBoolean(R.styleable.camera_view_add_focus_view,false);
        if(add){
            mFocusView = new CameraFocusView(context);
            int size = typedArray.getDimensionPixelSize(R.styleable.camera_view_focus_view_size,100);
            int startColor = typedArray.getColor(R.styleable.camera_view_focus_start_color, Color.RED);
            int endColor = typedArray.getColor(R.styleable.camera_view_focus_end_color, Color.YELLOW);
            mFocusView.setPrepareColor(startColor);
            mFocusView.setFinishColor(endColor);

            FrameLayout.LayoutParams focusParams = new FrameLayout.LayoutParams(
                    size, size);

            addView(mFocusView,focusParams);
        }



        typedArray.recycle();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            Display display = null;
            if(Build.VERSION.SDK_INT < 17){
                display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            }else {
                display = getDisplay();
            }

            mScreenOrientationDetector.enable(display);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!isInEditMode()) {
            mScreenOrientationDetector.disable();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (mParams.isAdjustViewBounds()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        // Handle android:adjustViewBounds
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        // 宽为精确测量
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            // 根据比例计算高
            final AspectRatio ratio = getAspectRatio();
            int height = (int) (MeasureSpec.getSize(widthMeasureSpec) * ratio.toFloat());
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
            }
            super.onMeasure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
        // 高为精确测量
        else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            // 根据比例计算宽
            final AspectRatio ratio = getAspectRatio();
            int width = (int) (MeasureSpec.getSize(heightMeasureSpec) * ratio.toFloat());
            if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        // Measure the PreviewView
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        // 若为横屏, 则颠倒一下比例, 方便计算
        AspectRatio ratio = getAspectRatio();
        if (!mScreenOrientationDetector.isLandscape()) {
            ratio = ratio.inverse();
        }
        if (height < width * ratio.getY() / ratio.getX()) {
            mCameraSurfaceView.getView().measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(width * ratio.getY() / ratio.getX(),
                            MeasureSpec.EXACTLY));
        } else {
            mCameraSurfaceView.getView().measure(
                    MeasureSpec.makeMeasureSpec(height * ratio.getX() / ratio.getY(),
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    public AspectRatio getAspectRatio() {
        return mParams.getAspectRatio();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mParams.setDesiredSize(mCameraSurfaceView.getSize());
        mDevice.notifyDesiredSizeChanged();
    }

    public void startPreview() {
        if(mCameraSurfaceView!=null){
            mCameraSurfaceView.postEvent(new Runnable() {
                @Override
                public void run() {
                    mDevice.open();
                }
            });
        }

    }

    public void setFilterSync(boolean isSync){
        if(mCameraSurfaceView!=null){
            mCameraSurfaceView.setFilterSync(isSync);
        }
    }

    public void stopPreview() {
        if(mCameraSurfaceView!=null){
            mCameraSurfaceView.postEvent(new Runnable() {
                @Override
                public void run() {
                    mDevice.close();
                }
            });
        }
    }


    @Override
    public void onCameraReady(@NonNull SurfaceTexture cameraTexture,
                              @NonNull Size surfaceSize, int displayRotation) {
        mCameraSurfaceView.getRenderer().resetMatrix();
        mCameraSurfaceView.getRenderer().rotate(displayRotation);
        mCameraSurfaceView.getRenderer().centerCrop(mScreenOrientationDetector.isLandscape(),
                mCameraSurfaceView.getSize(), surfaceSize);
        mCameraSurfaceView.getRenderer().applyMatrix();
    }

    @Override
    public void onDisplayOrientationChanged(int displayOrientation) {
        mParams.setScreenOrientationDegrees(displayOrientation);
        mDevice.notifyScreenOrientationChanged();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!mParams.isAutoFocus()){
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // 聚焦
                focus((int) event.getX(), (int) event.getY());
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    //进行聚焦
    private void focus(int x, int y) {
        mParams.setFocus(this,x,y);
        mParams.setFocusCallback(new CameraParam.FocusListener() {
            @Override
            public void beginFocus(int x, int y) {
                if(mFocusView != null){
                    mFocusView.beginFocus(x,y);
                }


            }

            @Override
            public void endFocus(boolean success) {
                if(mFocusView != null){
                    mFocusView.endFocus(success);
                }

            }
        });
        mDevice.notifyAutoFocusChanged();
    }

    /**
     * 是否自动聚焦
     * @param isFocus
     */
    public void setAutoFocus(boolean isFocus){
        mParams.setAutoFocus(isFocus);
        if(mFocusView != null){
            mFocusView.cancelFocus();
        }

        mDevice.notifyAutoFocusChanged();
    }


    /**
     * 设置摄像头
     * @param facing
     */
    public void setFacing( int facing) {
        mParams.setFacing(facing);
        mDevice.notifyFacingChanged();
    }

    public void takePhoto(final TakePhotoCallback callback){
        if(mCameraSurfaceView != null){
            mCameraSurfaceView.takePhoto(new TakePhotoCallback() {
                @Override
                public void takePhoto(Bitmap bitmap) {
                    if(callback != null){
                        callback.takePhoto(bitmap);
                    }
                }
            });
        }
    }


    /**
     * 设置 相机的宽高比列
     *
     * @param ratio The {@link AspectRatio} to be set.
     */
    public void setAspectRatio(@NonNull AspectRatio ratio) {
        if (mParams.getAspectRatio().equals(ratio)) {
            return;
        }
        mParams.setAspectRatio(ratio);
        mDevice.notifyAspectRatioChanged();
        requestLayout();
    }


    public void addFilter(IFBOFilter filter){
        mCameraSurfaceView.addFilter(filter);
    }

    public void removeFilter(IFBOFilter filter){
        mCameraSurfaceView.removeFilter(filter);
    }

    public void setFilters(ArrayList<IFBOFilter> filters){
        mCameraSurfaceView.setFilters(filters);
    }


    /**
     * 打开闪光灯模式
     * @param flash
     */
    public void setFlash(int flash) {
        mParams.setFlashMode(flash);
        mDevice.notifyFlashModeChanged();
    }

    /**
     * 设置View是否适配View的边缘
     * @param adjustViewBounds
     */
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (mParams.isAdjustViewBounds() != adjustViewBounds) {
            mParams.setAdjustViewBounds(adjustViewBounds);
            requestLayout();
        }
    }


    public int getFacing() {
        return mParams.getFacing();
    }


    /**
     * Returns whether the continuous auto-focus mode is enabled.
     *
     * @return {@code true} if the continuous auto-focus mode is enabled. {@code false} if it is
     * disabled, or if it is not supported by the current camera.
     */
    public boolean getAutoFocus() {
        return mParams.isAutoFocus();
    }

    /**
     * Gets the current flash mode.
     *
     * @return The current flash mode.
     */
    public int getFlash() {
        //noinspection WrongConstant
        return mParams.getFlashMode();
    }

    /**
     * Returns whether the adjustViewBounds is enabled.
     *
     * @return {@code true} if the adjustViewBounds is enabled. {@code false} if it is disabled
     */
    public boolean isAdjustViewBounds() {
        return mParams.isAdjustViewBounds();
    }


    public void release() {
        mCameraSurfaceView.release();
    }

    public void close() {
        if(mDevice == null){
            return;
        }
        mDevice.close();
    }
}
