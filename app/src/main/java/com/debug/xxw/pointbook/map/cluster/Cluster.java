package com.debug.xxw.pointbook.map.cluster;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yiyi.qi
 * @date 16/10/10
 */

public class Cluster {

    private LatLng mLatLng;
    private List<ClusterItem> mClusterItems;
    private Marker mMarker;

    Cluster(LatLng latLng) {
        mLatLng = latLng;
        mClusterItems = new ArrayList<>();
    }

    void addClusterItem(ClusterItem clusterItem) {
        mClusterItems.add(clusterItem);
    }

    int getClusterCount() {
        return mClusterItems.size();
    }

    LatLng getCenterLatLng() {
        return mLatLng;
    }

    void setMarker(Marker marker) {
        mMarker = marker;
    }

    Marker getMarker() {
        return mMarker;
    }

    List<ClusterItem> getClusterItems() {
        return mClusterItems;
    }
}
