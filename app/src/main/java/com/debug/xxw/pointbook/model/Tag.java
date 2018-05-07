package com.debug.xxw.pointbook.model;

import java.io.Serializable;

public class Tag implements Serializable {
    private Integer tid;
    private String mid;
    private Integer uid;
    private String content;

    public Integer getTid() {
        return tid;
    }

    public Tag setTid(Integer tid) {
        this.tid = tid;
        return this;
    }

    public String getMid() {
        return mid;
    }

    public Tag setMid(String mid) {
        this.mid = mid;
        return this;
    }

    public Integer getUid() {
        return uid;
    }

    public Tag setUid(Integer uid) {
        this.uid = uid;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Tag setContent(String content) {
        this.content = content;
        return this;
    }
}
