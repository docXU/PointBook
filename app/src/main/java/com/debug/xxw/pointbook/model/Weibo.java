package com.debug.xxw.pointbook.model;

import java.io.Serializable;

/**
 * Created by xxw on 2017/10/20.
 */

public class Weibo implements Serializable {
    private String username;
    private String headimgurl;
    private String content;
    private String publicTime;
    private String from;
    private String recentLike;
    private String recentShare;
    private String recentComment;
    private NineGridModel contentImgs;

    public Weibo(){
        contentImgs = new NineGridModel();
    };

    public String getHeadimgurl() {
        return headimgurl;
    }

    public Weibo setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Weibo setContent(String content) {
        this.content = content;
        return this;
    }

    public NineGridModel getContentImgs() {
        return contentImgs;
    }

    public Weibo setContentImgs(NineGridModel contentImgs) {
        this.contentImgs.isShowAll = contentImgs.isShowAll;
        this.contentImgs.urlList.addAll(contentImgs.urlList);
        this.contentImgs.remoteUrlList.putAll(contentImgs.remoteUrlList);
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Weibo setUsername(String username) {
        this.username = username;
        return this;
    }


    public String getPublicTime() {
        return publicTime;
    }

    public Weibo setPublicTime(String publicTime) {
        this.publicTime = publicTime;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public Weibo setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getRecentLike() {
        return recentLike;
    }

    public Weibo setRecentLike(String recentLike) {
        this.recentLike = recentLike;
        return this;
    }

    public String getRecentShare() {
        return recentShare;
    }

    public Weibo setRecentShare(String recentShare) {
        this.recentShare = recentShare;
        return this;
    }

    public String getRecentComment() {
        return recentComment;
    }

    public Weibo setRecentComment(String recentComment) {
        this.recentComment = recentComment;
        return this;
    }
}
