package com.debug.xxw.pointbook.map;

import com.amap.api.maps.model.Marker;
import com.debug.xxw.pointbook.ClusterLib.ClusterItem;

import java.util.List;

/**
 * Created by xxw on 2017/10/21.
 */

public interface ActivityPointClickListener {
    /**
     * 点击活动点的回调处理函数
     *
     * @param marker 活动点
     */
    public void onActivityPointClick(Marker marker);
}
