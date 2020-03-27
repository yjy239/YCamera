package com.yjy.mediaapplication.bean;

import com.yjy.camera.Filter.IFBOFilter;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/25
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class FilterModel {
    IFBOFilter filter;

    String name;

    boolean isSelect;



    public FilterModel(IFBOFilter filter,String name) {
        this.filter = filter;
        this.name = name;

    }

    public IFBOFilter getFilter() {
        return filter;
    }

    public void setFilter(IFBOFilter filter) {
        this.filter = filter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
