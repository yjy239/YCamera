package com.yjy.mediaapplication;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Engine.CameraManager;
import com.yjy.camera.Render.WaterFilter;
import com.yjy.camera.UI.ICameraFragment;
import com.yjy.camera.Utils.CameraUtils;
import com.yjy.camera.widget.RecordButton;
import com.yjy.camera.widget.YCameraView;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ICameraFragment cameraView;
    RecordButton mButton;

    public static final int REQUEST_CODE = 11;
    private boolean isStart = false;
    ImageView img;
    FrameLayout mLayout;
    ImageView mFlashIv;
    ImageView mFaceIv;
    private boolean flash = false;
    private boolean front = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cameraView = CameraManager.init(this,R.id.camera_layout);
        mButton = findViewById(R.id.btn);
        img = findViewById(R.id.image);
        mFlashIv = findViewById(R.id.flash_iv);
        mFaceIv = findViewById(R.id.face_iv);
        mLayout = findViewById(R.id.content);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStart){
                    cameraView.stopCamera();
                    cameraView.takePhoto(new TakePhotoCallback() {
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

        mButton.setIsRecording(true);

        mFlashIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flash = !flash;
                cameraView.setFlash(flash? ICameraDevice.FLASH_TORCH:ICameraDevice.FLASH_OFF);
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
                cameraView.setFacing(front?ICameraDevice.FACING_FRONT:ICameraDevice.FACING_BACK);
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

        cameraView.addFilter(new WaterFilter(this,R.drawable.ic_launcher,
                CameraUtils.dp2px(this,100),CameraUtils.dp2px(this,40)));

        TextView view = new TextView(this);
        view.setText("edit by yjy");
        view.setTextSize(16);
        view.setTextColor(Color.BLACK);

        cameraView.addFilter(new WaterFilter(this,view,
                CameraUtils.dp2px(this,100),CameraUtils.dp2px(this,40),true));


        cameraView.setFilterSync(true);
    }



    private void openCamera(){
        cameraView.openCamera();
        img.setVisibility(View.GONE);
        mLayout.setVisibility(View.GONE);
    }



}
