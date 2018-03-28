package com.debug.xxw.pointbook.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xxw on 2016/5/19.
 */

public class NineGridModel implements Serializable {

    public List<String> urlList = new ArrayList<>();  //用于存储配图的地址

    // 发布微博时，urlList中的图片上传云端后会存储在remoteUrlList，而本机加载图片优先加载urlList列表
    // 下标从0开始，依次记录一条微博各图片的序列和链接
    // 链接可能为失效的地址
    public HashMap<Integer, String> remoteUrlList = new HashMap<>();

    public boolean isShowAll = false;
}
