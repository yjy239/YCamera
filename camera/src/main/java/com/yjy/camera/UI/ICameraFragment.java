package com.yjy.camera.UI;

import android.view.View;

import com.yjy.camera.Filter.IFilterAction;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface ICameraFragment extends IFilterAction,ICameraAction {

    void setCameraType(@CameraType int type);

    View getView();




}
