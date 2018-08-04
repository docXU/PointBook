package com.debug.xxw.pointbook.model;

import com.amap.api.maps.model.LatLng;

/**
 * Created by xxw on 2017/10/22.
 */

public class ReportPoint {
    private LatLng location;
    private String title;
    private String acticities;

    public ReportPoint() {
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActicities() {
        return acticities;
    }

    public void setActicities(String acticities) {
        this.acticities = acticities;
    }


}
