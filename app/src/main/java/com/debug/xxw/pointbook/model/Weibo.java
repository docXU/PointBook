package com.debug.xxw.pointbook.model;

import java.io.Serializable;

/**
 * Created by xxw on 2017/10/20.
 */

public class Weibo implements Serializable {
    private String userId;
    private String weiboId;
    private String content;
    private String publicTime;
    private String msglevel;
    private String recentLike;
    private String recentLow;
    private String recentComment;
    private NineGridModel contentImgs;
    private User user;

    //显示时用的，看详情再去服务器查更多信息
    private String username;
    private String headimg = "headimg";

    public Weibo(){
        contentImgs = new NineGridModel();
        user = null;
    }

    public String getUsername() {
        return username;
    }

    public Weibo setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getHeadimg() {
        return headimg;
    }

    public Weibo setHeadimg(String headimg) {
        this.headimg = headimg;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Weibo setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getWeiboId() {
        return weiboId;
    }

    public Weibo setWeiboId(String weiboId) {
        this.weiboId = weiboId;
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

    public String getPublicTime() {
        return publicTime;
    }

    public Weibo setPublicTime(String publicTime) {
        this.publicTime = publicTime;
        return this;
    }

    public String getMsglevel() {
        return msglevel;
    }

    public Weibo setMsglevel(String msglevel) {
        this.msglevel = msglevel;
        return this;
    }

    public String getRecentLike() {
        return recentLike;
    }

    public Weibo setRecentLike(String recentLike) {
        this.recentLike = recentLike;
        return this;
    }

    public String getRecentLow() {
        return recentLow;
    }

    public Weibo setRecentLow(String recentLow) {
        this.recentLow = recentLow;
        return this;
    }

    public String getRecentComment() {
        return recentComment;
    }

    public Weibo setRecentComment(String recentComment) {
        this.recentComment = recentComment;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Weibo setUser(User user) {
        this.user = user;
        return this;
    }
}
