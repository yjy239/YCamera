package com.yjy.camera.Utils;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ScreenOrientationDetector {
    /**
     * Mapping from Surface.Rotation_n to degrees.
     */
    private static final SparseIntArray DISPLAY_ORIENTATIONS = new SparseIntArray();

    static {
        DISPLAY_ORIENTATIONS.put(Surface.ROTATION_0, 0);
        DISPLAY_ORIENTATIONS.put(Surface.ROTATION_90, 90);
        DISPLAY_ORIENTATIONS.put(Surface.ROTATION_180, 180);
        DISPLAY_ORIENTATIONS.put(Surface.ROTATION_270, 270);
    }

    private final OrientationEventListener mOrientationListener;
    private OnDisplayChangedListener mOnDisplayChangedListener;
    private Display mDisplay;

    int LANDSCAPE_90 = 90;
    int LANDSCAPE_270 = 270;


    private int mLastRotation = 0;

    public ScreenOrientationDetector(Context context, final OnDisplayChangedListener listener){
        this.mOnDisplayChangedListener = listener;

        mOrientationListener = new OrientationEventListener(context){

            @Override
            public void onOrientationChanged(int orientation) {
                if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN
                        ||mDisplay == null){
                    return;
                }
                int rotation = mDisplay.getRotation();
                //和上一次的旋转角度不一样
                if(mLastRotation != rotation){
                    mLastRotation = rotation;
                    mOnDisplayChangedListener.onDisplayOrientationChanged(DISPLAY_ORIENTATIONS.get(mLastRotation));
                }
            }
        };

    }

    public void enable(Display display) {
        mDisplay = display;
        mOrientationListener.enable();
        // callback at once
        mLastRotation = mDisplay.getRotation();
        mOnDisplayChangedListener.onDisplayOrientationChanged(DISPLAY_ORIENTATIONS.get(mLastRotation));
    }

    public void disable() {
        mOrientationListener.disable();
        mDisplay = null;
    }

    public boolean isLandscape() {
        int screenOrientationDegrees = DISPLAY_ORIENTATIONS.get(mLastRotation);
        return (screenOrientationDegrees == LANDSCAPE_90
                || screenOrientationDegrees == LANDSCAPE_270);
    }


    public interface OnDisplayChangedListener {

        /**
         * Called when display orientation is changed.
         *
         * @param displayOrientation One of 0, 90, 180, and 270.
         */
        void onDisplayOrientationChanged(int displayOrientation);
    }
}
