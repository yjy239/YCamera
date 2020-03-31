package com.yjy.mediaapplication;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Engine.CameraFragmentBuilder;
import com.yjy.camera.Filter.BlackWhiteFilter;
import com.yjy.camera.Filter.BlurFilter;
import com.yjy.camera.Filter.LPSFilter;
import com.yjy.camera.Filter.ShaperFilter;
import com.yjy.camera.Filter.SobelFilter;
import com.yjy.camera.Filter.WaterFilter;
import com.yjy.camera.UI.ICameraFragment;
import com.yjy.camera.Utils.AspectRatio;
import com.yjy.camera.Utils.CameraUtils;
import com.yjy.camera.widget.RecordButton;
import com.yjy.mediaapplication.bean.FilterModel;


import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ICameraFragment mCamera;
    RecordButton mButton;

    public static final int REQUEST_CODE = 11;
    private boolean isStart = false;
    private ImageView img;
    private FrameLayout mLayout;
    private ImageView mFlashIv;
    private ImageView mFaceIv;
    private boolean flash = false;
    private boolean front = false;
    private ImageView mMoreIv;
    private ImageView mSyncIv;

    private EffectDialogFragment mDialog;
    private static final String TAG = MainActivity.class.getName();

    private boolean isSync = false;

    private ArrayList<FilterModel> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMoreIv = findViewById(R.id.more_iv);
        mSyncIv = findViewById(R.id.sync_iv);
        mCamera = new CameraFragmentBuilder(this,R.id.camera_layout)
                .setAspectRatio(AspectRatio.DEFAULT)
                .setZoomSensitive(3)
                .setAdjustViewBounds(true)
                .setSoftwareZoom(false)
                .setFlash(ICameraDevice.FLASH_OFF)
                .setFilterSync(true)
                .setFacing(ICameraDevice.FACING_BACK)
                .setAutoFocus(true)
                .asSurface()
                .build();
        mButton = findViewById(R.id.btn);
        img = findViewById(R.id.image);
        mFlashIv = findViewById(R.id.flash_iv);
        mFaceIv = findViewById(R.id.face_iv);
        mLayout = findViewById(R.id.content);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(isStart){
                    mCamera.stopCamera();
                    mCamera.takePhoto(new TakePhotoCallback() {
                        @Override
                        public void takePhoto(Bitmap bitmap) {
                            img.setVisibility(View.VISIBLE);
                            mLayout.setVisibility(View.VISIBLE);

                            img.setImageBitmap(bitmap);
                        }
                    });

                    isStart = false;
                }else {
                    openCamera();
                    img.setVisibility(View.GONE);
                    mLayout.setVisibility(View.GONE);
                    isStart = true;

                }


            }
        });

        isStart = true;

        mButton.setIsRecording(true);

        mFlashIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flash = !flash;
                mCamera.setFlash(flash? ICameraDevice.FLASH_TORCH:ICameraDevice.FLASH_OFF);
                if(!flash){
                    mFlashIv.setImageResource(R.drawable.ic_close_flash);
                }else {
                    mFlashIv.setImageResource(R.drawable.ic_open_flash);
                }
            }
        });

        mFaceIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                front = !front;
                mCamera.setFacing(front?ICameraDevice.FACING_FRONT:ICameraDevice.FACING_BACK);
            }
        });


        mButton.setMaxProgress(100);

        mButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ValueAnimator animator = ValueAnimator.ofInt(0,100);
                animator.setDuration(3000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int current = (int)animation.getAnimatedValue();
                        mButton.setCurrentProgress(current);
                    }
                });
                animator.start();
                return false;
            }
        });


        mSyncIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSync = !isSync;
                if(isSync){
                    mSyncIv.setImageResource(R.drawable.ic_sync_stop);
                }else {
                    mSyncIv.setImageResource(R.drawable.ic_sync);
                }
                mCamera.setFilterSync(isSync);
            }
        });


        mDialog = EffectDialogFragment.newInstance();

        mDialog.setCamera(mCamera);

        mMoreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mDialog.isAdded()){
                    mDialog.show(getSupportFragmentManager(),TAG);
                }else {
                    mDialog.dismiss();
                }

            }
        });

        mCamera.setFilterSync(isSync);

    }

    public ArrayList<FilterModel> getData(){
        if(mDatas != null&&mDatas.size()>0){
            return mDatas;
        }
        TextView view = new TextView(this);
        view.setText("edit by yjy");
        view.setTextSize(16);
        view.setTextColor(Color.BLACK);


        ArrayList<FilterModel> list = new ArrayList<>();

        list.add(new FilterModel(new WaterFilter(this,view,
                CameraUtils.dp2px(this,100)
                ,CameraUtils.dp2px(this,40),true),
                getString(R.string.add_water_view_filter)));

        list.add(new FilterModel(new BlackWhiteFilter(this),getString(R.string.bw_filter)));

        list.add(new FilterModel(new SobelFilter(this),getString(R.string.sobel_filter)));

        list.add(new FilterModel(new ShaperFilter(this),getString(R.string.shaper_filter)));

        list.add(new FilterModel(new LPSFilter(this),getString(R.string.LPS_filter)));

        list.add(new FilterModel(new BlurFilter(this),getString(R.string.blur_filter)));

        list.add(new FilterModel(new WaterFilter(this,
                R.drawable.ic_launcher,
                CameraUtils.dp2px(this,100),
                CameraUtils.dp2px(this,40)),getString(R.string.add_water_icon_filter)));

        mDatas = list;

        return mDatas;
    }




    private void openCamera(){
        mCamera.openCamera();
        img.setVisibility(View.GONE);
        mLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
