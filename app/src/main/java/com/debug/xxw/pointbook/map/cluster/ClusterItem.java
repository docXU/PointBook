package com.debug.xxw.pointbook.map.cluster;

import com.amap.api.maps.model.LatLng;

/**
 * @author yiyi.qi
 * @date 16/10/10
 */

public interface ClusterItem {
    /**
     * 返回聚合元素的地理位置
     *
     * @return
     */
    LatLng getPosition();
}
