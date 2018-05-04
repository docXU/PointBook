package com.debug.xxw.pointbook.model;

import com.amap.api.maps.model.LatLng;
import com.debug.xxw.pointbook.map.cluster.ClusterItem;

import java.sql.Date;

/**
 * Created by yiyi.qi on 16/10/10.
 */

public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    private String mTitle;
    private String mId;
    public RegionItem(String id, LatLng latLng, String title) {
        mId =id;
        mLatLng=latLng;
        mTitle=title;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }
    public String getTitle(){
        return mTitle;
    }

    public Date getCreateTime(){
        return null;
    }

    public String getId() { return mId; }
}
