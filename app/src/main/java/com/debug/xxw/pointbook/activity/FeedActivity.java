package com.debug.xxw.pointbook.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.adapter.WeiboListViewAdapter;
import com.debug.xxw.pointbook.model.Interaction;
import com.debug.xxw.pointbook.model.NineGridModel;
import com.debug.xxw.pointbook.model.Tag;
import com.debug.xxw.pointbook.model.User;
import com.debug.xxw.pointbook.model.Weibo;
import com.debug.xxw.pointbook.net.ConstURL;
import com.debug.xxw.pointbook.net.RequestManager;
import com.debug.xxw.pointbook.net.WeiboNetter;
import com.debug.xxw.pointbook.viewmodel.FeedTagView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
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
    final Context mContext = this;
    private WeiboListViewAdapter adapter;
    private final int RequestCode = 10;

    private List<Tag> tagList;
    private List<Weibo> weiboList;

    private static int TAG_MAX_SIZE = 9;
    private boolean showGiftTag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weibo_list);
        Bundle bundle = this.getIntent().getExtras();
        recyclerView = (RecyclerView) findViewById(R.id.weiboRecycler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //解析bundle
        if (bundle == null) {
            throw new IllegalArgumentException("没有点的身份信息");
        }
        final String entryId = bundle.getString("entry_id");
        final String entryName = bundle.getString("name");
        tagList = (List<Tag>) bundle.getSerializable("tags");

        //Marker类的点才有tag
        if (bundle.getString("type").equals("marker") && tagList != null) {
            TagFlowLayout mTagFlowLayout = (TagFlowLayout) findViewById(R.id.id_flowlayout);
            mTagFlowLayout.setVisibility(View.VISIBLE);
            final List<FeedTagView> displayTagViews = new LinkedList<>();
            if (0 == tagList.size()) {
                showGiftTag = true;
                FeedTagView gift = new FeedTagView(mContext, null);
                gift.getTv().setText("四大皆空");
                displayTagViews.add(gift);
            }

            //tag不能大于9个
            for (Tag tag : tagList) {
                FeedTagView ftv = new FeedTagView(mContext, null);
                ftv.getTv().setText(tag.getContent());
                displayTagViews.add(ftv);
            }

            //添加预留位
            for (int i = showGiftTag ? 1 : tagList.size(); i < TAG_MAX_SIZE; i++) {
                FeedTagView placeHolder = new FeedTagView(mContext, null);
                placeHolder.setVisibility(View.GONE);
                displayTagViews.add(placeHolder);
            }
            //添加一个view作为Add按钮
            displayTagViews.add(new FeedTagView(mContext, null));

            mTagFlowLayout.setAdapter(new TagAdapter<FeedTagView>(displayTagViews) {
                @Override
                public View getView(final FlowLayout parent, final int position, FeedTagView s) {
                    if (position == displayTagViews.size() - 1) {
                        s.displayBtn();
                        s.getButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showAddTagDialog(parent);
                            }
                        });
                        s.hiddenTv();
                    } else {
                        s.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                showTagDeleteDialog(position, parent);
                                return false;
                            }
                        });
                    }
                    return s;
                }

                private void showAddTagDialog(FlowLayout parent) {
                    if (tagList.size() == TAG_MAX_SIZE) {
                        Toast.makeText(FeedActivity.this, "不好意思，已达标签数最大容量~", Toast.LENGTH_LONG).show();
                        parent.getChildAt(TAG_MAX_SIZE).setVisibility(View.GONE);
                        return;
                    }

                    final FrameLayout tagViewHolder = (FrameLayout) parent.getChildAt(tagList.size());
                    final FeedTagView targetPlaceHolder = (FeedTagView) tagViewHolder.getChildAt(0);
                    final EditText et = new EditText(FeedActivity.this);
                    new AlertDialog.Builder(FeedActivity.this).setTitle("添加标签咯~")
                            .setIcon(android.R.drawable.ic_menu_add)
                            .setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String input = et.getText().toString();
                                    if (input.equals("")) {
                                        Toast.makeText(getApplicationContext(), "标签描述不能为空！", Toast.LENGTH_LONG).show();
                                    } else {
                                        HashMap<String, String> params = new HashMap<String, String>();
                                        params.put("mid", String.valueOf(entryId));
                                        params.put("content", input);
                                        //TODO:加用户信息,这里的Uid是字符串，可能会出现异常
                                        params.put("uid", "1");

                                        Toast.makeText(getApplicationContext(), "正在同步添加Tag...", Toast.LENGTH_LONG).show();
                                        RequestManager.getInstance(mContext).requestAsyn(ConstURL.TAG_ADD, RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<Object>() {
                                            @Override
                                            public void onReqSuccess(Object result) {
                                                try {
                                                    int tid = Integer.parseInt(String.valueOf(result).replace("\"", ""));
                                                    tagList.add(new Tag().setTid(tid).setMid(entryId).setContent(input).setUid(1));
                                                    targetPlaceHolder.getTv().setText(input);
                                                    targetPlaceHolder.setVisibility(View.VISIBLE);
                                                    tagViewHolder.setVisibility(View.VISIBLE);
                                                    if (showGiftTag) {
                                                        showGiftTag = false;
                                                    }
                                                    Toast.makeText(FeedActivity.this, "好了~", Toast.LENGTH_SHORT).show();
                                                } catch (Exception e) {
                                                    Log.e(FeedActivity.this.getLocalClassName(), e.getMessage());
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onReqFailed(String errorMsg) {
                                                Toast.makeText(FeedActivity.this, "完了朋友，现在添加不了...再试试吧", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        dialog.dismiss();
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }

                private void showTagDeleteDialog(final int position, final FlowLayout parent) {
                    if (showGiftTag && position == 0) {
                        Toast.makeText(FeedActivity.this, "这个tag雷打不动的~", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final Tag target = tagList.get(position);
                    AlertDialog dialog = new AlertDialog.Builder(FeedActivity.this)
                            //.setIcon(R.mipmap.icon)
                            .setTitle("严重警告")
                            .setMessage("是否要删除[ " + target.getContent() + " ]标签？")
                            .setNegativeButton("算了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(FeedActivity.this, "为啥不删了啊？", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("嗯哼", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(FeedActivity.this, "正在给你删掉...", Toast.LENGTH_SHORT).show();

                                    HashMap<String, String> params = new HashMap<String, String>();
                                    params.put("tid", String.valueOf(target.getTid()));

                                    RequestManager.getInstance(mContext).requestAsyn(ConstURL.TAG_DELETE, RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<Object>() {
                                        @Override
                                        public void onReqSuccess(Object result) {
                                            squeezeDisplayTag(parent, tagList.size(), position);
                                            tagList.remove(position);
                                            Toast.makeText(FeedActivity.this, "好了~", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onReqFailed(String errorMsg) {
                                            Toast.makeText(FeedActivity.this, "完了朋友，删不掉...再试试吧", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }
            });

            mTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int position, FlowLayout parent) {
                    return false;
                }
            });
        }

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

        WeiboNetter.queryWeiboList(mContext, entryId, new RequestManager.ReqCallBack() {
            @Override
            public void onReqSuccess(Object result) {
                try {
                    JSONArray data = new JSONArray((String) result);
                    if (isList(data)) {
                        weiboList = parseResultToWeibo(result);
                        adapter.setList(weiboList);
                        adapter.setOnLikeClickListener(new WeiboListViewAdapter.OnItemClickListener() {
                            @Override
                            public void onClick(final int position, final String who) {
                                if (who.equals(WeiboNetter.collect_counter)) {
                                    //todo:添加收藏记录
                                } else if (who.equals(WeiboListViewAdapter.head_view)) {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("uid", weiboList.get(position).getUserId());

                                    RequestManager.getInstance(FeedActivity.this).requestAsyn(ConstURL.USER_GET, RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<Object>() {
                                        @Override
                                        public void onReqSuccess(Object result) {
                                            User user = User.parseResult(result);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("user", user);
                                            Intent intent = new Intent().setClass(FeedActivity.this, ProfileActivity.class);
                                            intent.putExtras(bundle);
                                            FeedActivity.this.startActivity(intent);
                                        }

                                        @Override
                                        public void onReqFailed(String errorMsg) {
                                            //网络出错
                                            Toast.makeText(FeedActivity.this, "匿名用户没什么好看的", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                } else {
                                    WeiboNetter.incCounter(mContext, who, weiboList.get(position).getWeiboId(), new RequestManager.ReqCallBack() {
                                        @Override
                                        public void onReqSuccess(Object result) {
                                            String currentLike = (String) result;
                                            adapter.changeCounter(position, currentLike, who);
                                            Toast.makeText(mContext, "+1", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onReqFailed(String errorMsg) {
                                            Toast.makeText(mContext, "网络出错，请稍候~", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            }
                        });
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

    /**
     * 删除tag时将显示的tag向前挤压，隐藏最后一个tag
     *
     * @param parent
     * @param currentDisplayTagCount
     * @param positionToBeDelete
     */
    private void squeezeDisplayTag(FlowLayout parent, int currentDisplayTagCount, int positionToBeDelete) {
        for (int i = positionToBeDelete; i <= currentDisplayTagCount - 2; i++) {
            FeedTagView current = (FeedTagView) ((FrameLayout) parent.getChildAt(i)).getChildAt(0);
            FeedTagView next = (FeedTagView) ((FrameLayout) parent.getChildAt(i + 1)).getChildAt(0);
            current.getTv().setText(next.getTv().getText());
        }
        parent.getChildAt(currentDisplayTagCount - 1).setVisibility(View.GONE);
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
                            .setWeiboId(jo.getString("id"))
                            .setUserId(jo.getString("uid"))
                            .setUsername(jo.getString("username"))
                            .setContent(jo.getString("content"))
                            .setHeadimg(jo.getString("headimg"))
                            .setPublicTime(jo.getString("create_time"))
                            .setMsglevel(jo.getString("msglevel"))
                            .setRecentLike(jo.getString("likecount"))
                            .setRecentLow(jo.getString("lowcount"))
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
