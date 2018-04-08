package com.debug.xxw.pointbook.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.adapter.WeiboListViewAdapter;
import com.debug.xxw.pointbook.model.NineGridModel;
import com.debug.xxw.pointbook.model.Weibo;
import com.debug.xxw.pointbook.net.RequestManager;
import com.debug.xxw.pointbook.net.WeiboNetter;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Feed流
 * <p>
 * 活动点作为入口，解析id，地名
 * id作为索引获取流，地名作为视图窗口标题
 * todo：图片从服务器加载完之后缓存至本地，用户点击图片浏览时优先加载本地资源，可让用户手动清理缓存
 *
 * @author xxw
 */
public class FeedActivity extends AppCompatActivity {
    private String tag = FeedActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private WeiboListViewAdapter adapter;
    private final int RequestCode = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weibo_list);
        Bundle bundle = this.getIntent().getExtras();
        recyclerView = (RecyclerView) findViewById(R.id.weiboRecycler);
        WeiboNetter mWeiboNetter = new WeiboNetter(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TagFlowLayout mTagFlowLayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);
        final LayoutInflater mInflater = getLayoutInflater();
        String[] mVals = {"test", "second", "third"};
        mTagFlowLayout.setAdapter(new TagAdapter<String>(mVals)
        {
            @Override
            public View getView(FlowLayout parent, int position, String s)
            {
                TextView tv = (TextView) mInflater.inflate(R.layout.tag_tv,
                        mTagFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        //解析bundle
        if (bundle == null) {
            throw new IllegalArgumentException("没有点的身份信息");
        }
        final String entryId = bundle.getString("entry_id");
        final String entryName = bundle.getString("name");

        CollapsingToolbarLayout ctl = ((CollapsingToolbarLayout) findViewById(R.id.ctl_feed_better));
        ctl.setTitle(entryName);
//        设置字体颜色
//        ctl.setCollapsedTitleTextColor(0);
//        ctl.setExpandedTitleColor(11640438);
        //初始化浮层按钮
        findViewById(R.id.post_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("entry_id", entryId);
                intent.setClass(FeedActivity.this, EditorActivity.class);
                startActivityForResult(intent, RequestCode);
            }
        });
        if (null == adapter) {
            adapter = new WeiboListViewAdapter(getApplicationContext(), new ArrayList<Weibo>());
            recyclerView.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
        }

        mWeiboNetter.queryWeiboList(entryId, new RequestManager.ReqCallBack() {
            @Override
            public void onReqSuccess(Object result) {
                try {
                    JSONArray data = new JSONArray((String) result);
                    if (isList(data)) {
                        adapter.setList(parseResultToWeibo(result));
                    } else {
                        Log.e(tag, parseErrorMessage(data));
                    }
                } catch (JSONException je) {
                    Log.e(tag, Arrays.toString(je.getStackTrace()));
                }
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.e(tag, errorMsg);
                Toast.makeText(getApplicationContext(), R.string.query_weibolist_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private List<Weibo> parseResultToWeibo(Object result) {
        List<Weibo> items = new ArrayList<>();
        try {
            JSONArray data = new JSONArray((String) result);
            int itemcount = data.length();
            for (int i = 0; i < itemcount; i++) {
                try {
                    JSONObject jo = data.getJSONObject(i);
                    Weibo item = new Weibo()
                            .setUsername(jo.getString("username"))
                            .setContent(jo.getString("content"))
                            .setHeadimgurl(jo.getString("headimgurl"))
                            .setPublicTime(jo.getString("create_time"))
                            .setFrom(jo.getString("fromwhere"))
                            .setRecentLike(jo.getString("likecount"))
                            .setRecentShare(jo.getString("sharecount"))
                            .setRecentComment(jo.getString("commentcount"));
                    NineGridModel ngm = new NineGridModel();
                    if (jo.has("urls")) {
                        String urlsString = jo.getString("urls");
                        String[] urls = urlsString.split(";");
                        //去掉最后一个元素（使用split分割urlsString时最后一个元素是不要的），使用sublist正好可以控制
                        ngm.urlList.addAll(Arrays.asList(urls).subList(0, urls.length));
                    }
                    item.setContentImgs(ngm);
                    items.add(item);
                } catch (JSONException je) {
                    Log.e(tag, je.toString());
                }
            }
        } catch (JSONException je) {
            Log.e(tag, je.toString());
        }
        return items;
    }

    /**
     * 回调方法，从第二个页面回来的时候会执行这个方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String submitStatus = null;
        if (data != null) {
            submitStatus = data.getStringExtra("status");
        }
        if (submitStatus == null) {
            return;
        }
        switch (requestCode) {
            case RequestCode:
                switch (submitStatus) {
                    case "ok":
                        Weibo w = (Weibo) data.getSerializableExtra("weibo");
                        adapter.addWeiboToList(w);
                        recyclerView.scrollToPosition(0);
                        break;
                    case "fail":
                        Toast.makeText(getApplicationContext(), "发布失败...稍后重试", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private boolean isList(JSONArray data) {
        return data.length() >= 1;
    }

    private String parseErrorMessage(JSONArray data) {
        return data.toString();
    }
}
