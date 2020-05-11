package com.yjy.camera.Utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/04/20
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Utils {
    /**
     * Returns a {@link java.util.Queue} of the given size using Glide's preferred implementation.
     */
    public static <T> Queue<T> createQueue(int size) {
        return new ArrayDeque<T>(size);
    }


    /**
     * Returns the allocated byte size of the given bitmap.
     *
     * @see #getBitmapByteSize(android.graphics.Bitmap)
     *
     * @deprecated Use {@link #getBitmapByteSize(android.graphics.Bitmap)} instead. Scheduled to be removed in Glide
     * 4.0.
     */
    @Deprecated
    public static int getSize(Bitmap bitmap) {
        return getBitmapByteSize(bitmap);
    }

    /**
     * Returns the in memory size of the given {@link Bitmap} in bytes.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapByteSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Workaround for KitKat initial release NPE in Bitmap, fixed in MR1. See issue #148.
            try {
                return bitmap.getAllocationByteCount();
            } catch (NullPointerException e) {
                // Do nothing.
            }
        }
        return bitmap.getHeight() * bitmap.getRowBytes();
    }


    /**
     * Returns the in memory size of {@link android.graphics.Bitmap} with the given width, height, and
     * {@link android.graphics.Bitmap.Config}.
     */
    public static int getBitmapByteSize(int width, int height, Bitmap.Config config) {
        return width * height * getBytesPerPixel(config);
    }

    private static int getBytesPerPixel(Bitmap.Config config) {
        // A bitmap by decoding a gif has null "config" in certain environments.
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:
                bytesPerPixel = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                bytesPerPixel = 2;
                break;
            case ARGB_8888:
            default:
                bytesPerPixel = 4;
        }
        return bytesPerPixel;
    }


    private static volatile ExecutorService sCachedThreadPool;

    public static void execute(Runnable task) {
        if (sCachedThreadPool == null) {
            synchronized (Utils.class) {
                if (sCachedThreadPool == null) {
                    sCachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        sCachedThreadPool.execute(task);
    }
}
