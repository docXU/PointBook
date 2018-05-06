package com.debug.xxw.pointbook.model;

import com.amap.api.maps.model.LatLng;
import com.debug.xxw.pointbook.map.cluster.ClusterItem;

import java.sql.Date;
import java.util.List;

public class RegionItem implements ClusterItem, Cloneable {
    private LatLng mLatLng;
    private String mTitle;
    private String mId;
    private List<String> tags = null;

    public RegionItem(String id, LatLng latLng, String title) {
        mId = id;
        mLatLng = latLng;
        mTitle = title;
    }

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getId() {
        return mId;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
