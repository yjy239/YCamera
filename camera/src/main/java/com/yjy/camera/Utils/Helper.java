package com.yjy.camera.Utils;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/02/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Helper {

    /**
     * 将屏幕坐标转换成camera坐标
     * @param focusPoint
     * @return cameraPoint
     */
    public static Point convertToCameraPoint(Point focusPoint,int viewHeight,int viewWidth) {
        int newX = focusPoint.y * 2000 / viewHeight - 1000;
        int newY = -focusPoint.x * 2000 / viewWidth + 1000;
        return new Point(newX, newY);
    }

    public static Rect convertToCameraRect(Point centerPoint, int radius) {
        int left = limit(centerPoint.x - radius, 1000, -1000);
        int right = limit(centerPoint.x + radius, 1000, -1000);
        int top = limit(centerPoint.y - radius, 1000, -1000);
        int bottom = limit(centerPoint.y + radius, 1000, -1000);
        return new Rect(left, top, right, bottom);
    }

    private static int limit(int s, int max, int min) {
        if (s > max) {
            return max;
        }
        if (s < min) {
            return min;
        }
        return s;
    }
}
