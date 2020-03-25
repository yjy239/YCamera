package com.yjy.camera.Filter;

import java.util.ArrayList;

/**
 * <pre>
 *     author : yjy
 *     e-mail : yujunyu12@gmail.com
 *     time   : 2020/03/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface IFilterAction {
    /**
     * 是否Filter同步到屏幕
     * @param sync
     */
    void setFilterSync(boolean sync);


    /**
     * 新增一个Filter
     * @param filter
     */
    void addFilter(IFBOFilter filter);


    /**
     * 移除一个Filter
     * @param filter
     */
    void removeFilter(IFBOFilter filter);


    /**
     * 批量设置Filter
     * @param filters
     */
    void setFilters(ArrayList<IFBOFilter> filters);
}
