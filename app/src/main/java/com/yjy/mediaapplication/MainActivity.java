package com.yjy.mediaapplication;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Camera.TakePhotoFileCallback;
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
import com.yjy.camera.bitmap.BitmapPool;
import com.yjy.camera.widget.RecordButton;
import com.yjy.mediaapplication.bean.FilterModel;


import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
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
    private ImageView mBack;

    private EffectDialogFragment mDialog;
    private static final String TAG = MainActivity.class.getName();

    private boolean isSync = false;

    private ArrayList<FilterModel> mDatas = new ArrayList<>();
    private WeakReference<Bitmap> bitmapReference = new WeakReference<>(null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMoreIv = findViewById(R.id.more_iv);
        mSyncIv = findViewById(R.id.sync_iv);
        mBack = findViewById(R.id.back_iv);
        mCamera = new CameraFragmentBuilder(this,R.id.camera_layout)
                .setAspectRatio(AspectRatio.DEFAULT)
                .setZoomSensitive(3)
                .setAdjustViewBounds(true)
                .setSoftwareZoom(false)
                .setFlash(ICameraDevice.FLASH_OFF)
                .setFilterSync(true)
                .setFacing(ICameraDevice.FACING_BACK)
                .setPreviewMaxSize(true)
                .setSaveDir(Environment.getExternalStorageDirectory() + "/camera/images/waterImages/")
                .setAuthority("com.yjy.camera")
                .setAutoFocus(true)
                .asSurface()
                .build();
        mButton = findViewById(R.id.btn);
        img = findViewById(R.id.image);
        mFlashIv = findViewById(R.id.flash_iv);
        mFaceIv = findViewById(R.id.face_iv);
        mLayout = findViewById(R.id.content);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23){
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
                }else {
                    showPic();
                }

            }
        });


        isStart = true;

        mButton.setIsRecording(true);

        mFlashIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flash = !flash;
                if(flash){
                    mCamera.setFlash(!front? ICameraDevice.FLASH_TORCH:ICameraDevice.FLASH_FRONT);
                }else {
                    mCamera.setFlash(ICameraDevice.FLASH_OFF);
                }

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
                mFlashIv.setImageResource(R.drawable.ic_close_flash);
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(permissions.length>0&&  Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[0])){
                if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    showPic();
                }
            }
        }
    }

    private void showPic(){
        if(isStart){
            mCamera.stopCamera();

//                    mCamera.takePhoto(new TakePhotoCallback() {
//                        @Override
//                        public void takePhoto(Bitmap bitmap) {
//                            img.setVisibility(View.VISIBLE);
//                            mLayout.setVisibility(View.VISIBLE);
//                            if(bitmapReference.get() == null){
//                                bitmapReference = new WeakReference<>(bitmap);
//
//                            }else {
//                                Bitmap previous = bitmapReference.get();
//                                BitmapPool pool = mCamera.getBitmapPool();
//                                pool.put(previous);
//                            }
//
//                            img.setImageBitmap(bitmapReference.get());
//                        }
//                    });

            isStart = false;

            mCamera.takePhoto("" + System.currentTimeMillis(), new TakePhotoFileCallback() {
                @Override
                public void takePhoto(String path) {
                    img.setVisibility(View.VISIBLE);
                    mLayout.setVisibility(View.VISIBLE);
                    Log.e("path","path:"+path);
                    Glide.with(MainActivity.this)
                            .load(path)
                            .into(img);
                }
            });


        }else {
            openCamera();
            img.setVisibility(View.GONE);
            mLayout.setVisibility(View.GONE);
            isStart = true;

        }
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
