package com.yjy.mediaapplication;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.yjy.camera.Camera.ICameraDevice;
import com.yjy.camera.Camera.TakePhotoCallback;
import com.yjy.camera.Render.WaterFilter;
import com.yjy.camera.Utils.CameraUtils;
import com.yjy.camera.widget.RecordButton;
import com.yjy.camera.widget.YCameraView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.style.UpdateAppearance;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    YCameraView cameraView;
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


        cameraView = findViewById(R.id.camera);
        mButton = findViewById(R.id.btn);
        img = findViewById(R.id.image);
        mFlashIv = findViewById(R.id.flash_iv);
        mFaceIv = findViewById(R.id.face_iv);
        mLayout = findViewById(R.id.content);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStart){
                    cameraView.stopPreview();
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
                    if(Build.VERSION.SDK_INT >= 23){
                        requestPermissions(new String[]{Manifest.permission.CAMERA},REQUEST_CODE);
                    }else {
                        cameraView.startPreview();
                        img.setVisibility(View.GONE);
                        mLayout.setVisibility(View.GONE);
                        isStart = true;
                    }
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


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            switch (permissions[0]){
                case Manifest.permission.CAMERA://权限1
                    if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                        cameraView.startPreview();
                        img.setVisibility(View.GONE);
                        mLayout.setVisibility(View.GONE);
                        isStart = true;
                    }else {
                        Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
