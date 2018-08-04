package com.debug.xxw.pointbook.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.adapter.FullyGridLayoutManager;
import com.debug.xxw.pointbook.adapter.GridImageAdapter;
import com.debug.xxw.pointbook.model.User;
import com.debug.xxw.pointbook.utils.net.ConstURL;
import com.debug.xxw.pointbook.utils.net.QiniuUtils;
import com.debug.xxw.pointbook.utils.net.RequestManager;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ProfileEditActivity extends AppCompatActivity {

    private GridImageAdapter adapter;
    private List<LocalMedia> selectHeadImgList = new ArrayList<>();
    private User user;
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            PictureSelector.create(ProfileEditActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(1)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(false)// 是否可预览视频
                    .enablePreviewAudio(false) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    .compress(true)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                    .isGif(true)// 是否显示gif图片
                    .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(selectHeadImgList)// 是否传入已选图片
                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    .cropCompressQuality(50)// 裁剪压缩质量 默认100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    .rotateEnabled(true) // 裁剪是否可旋转图片
                    .scaleEnabled(true)// 裁剪是否可放大缩小图片
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        user = (User) (getIntent().getExtras().getSerializable("user"));
        initViewAndListner();

        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(ProfileEditActivity.this);
                } else {
                    Toast.makeText(ProfileEditActivity.this,
                            getString(R.string.picture_jurisdiction) + "，无法清楚本地缓存，请试着手动清理", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void initViewAndListner() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.headimg);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(ProfileEditActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(ProfileEditActivity.this, onAddPicClickListener);
        adapter.setList(selectHeadImgList);
        adapter.setSelectMax(1);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectHeadImgList.size() > 0) {
                    PictureSelector.create(ProfileEditActivity.this).externalPicturePreview(position, "/save", selectHeadImgList);
                }
            }
        });

        ((EditText) findViewById(R.id.username)).setText(user.getUsername());
        ((EditText) findViewById(R.id.telephone)).setText(user.getTelephone());
        ((EditText) findViewById(R.id.wechat_id)).setText(user.getWechat_id());
        ((EditText) findViewById(R.id.weibo_name)).setText(user.getWechat_id());
        if (user.getSex().equals("男")) {
            ((RadioButton) ((RadioGroup) findViewById(R.id.sex)).getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton) ((RadioGroup) findViewById(R.id.sex)).getChildAt(1)).setChecked(true);
        }
        ((EditText) findViewById(R.id.age)).setText(user.getAge());
        ((EditText) findViewById(R.id.describe)).setText(user.getDescribe());

        findViewById(R.id.quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                HashMap<>
                if (selectHeadImgList.size() == 1) {
                    final LocalMedia lm = selectHeadImgList.get(0);
                    final String imgPath = lm.isCompressed() ? lm.getCompressPath() : (lm.isCut() ? lm.getCutPath() : lm.getPath());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ProfileEditActivity.this, "正在上传头像...", Toast.LENGTH_SHORT).show();
                            QiniuUtils.uploadFile(imgPath, new UpCompletionHandler() {
                                @Override
                                public void complete(String key, ResponseInfo info, JSONObject res) {
                                    try {
                                        String url = null;
                                        if (info.isOK()) {
                                            url = getString(R.string.qiniu_address) + res.getString("key");
                                            Log.i(" qiniu", "headurl---------->" + url);
                                            Toast.makeText(ProfileEditActivity.this, "头像上传完毕~", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i("qiniu", "Upload Fail");
                                            Log.e(res.getString("code"), res.getString("error"));
                                            if ("token out of date".equals(res.getString("error"))) {
                                                QiniuUtils.queryToken();
                                            }
                                        }
                                        saveUser(url);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });

                } else {
                    saveUser(null);
                }
            }
        });
    }

    private void saveUser(String newheadurl) {

        HashMap<String, String> params = new HashMap<>();

        if (newheadurl == null) {
            params.put("headimg", user.getHeadimg());
        } else {
            params.put("headimg", newheadurl);
        }

        params.put("id", "" + user.getId());
        params.put("username", ((EditText) findViewById(R.id.username)).getText().toString());
        params.put("telephone", ((EditText) findViewById(R.id.telephone)).getText().toString());
        params.put("wechat_id", ((EditText) findViewById(R.id.wechat_id)).getText().toString());
        params.put("weibo_name", ((EditText) findViewById(R.id.weibo_name)).getText().toString());
        params.put("sex", ((RadioButton) findViewById((((RadioGroup) findViewById(R.id.sex)).getCheckedRadioButtonId()))).getText().equals("男") ? "1" : "0");
        params.put("age", ((EditText) findViewById(R.id.age)).getText().toString());
        params.put("describe", ((EditText) findViewById(R.id.describe)).getText().toString());

        RequestManager.getInstance(ProfileEditActivity.this).requestAsyn(ConstURL.USER_UPDATE, RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<Object>() {
            @Override
            public void onReqSuccess(Object result) {
                //登录成功或失败通过result判断
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", User.parseResult(result));
                intent.putExtras(bundle);
                ProfileEditActivity.this.setResult(ProfileActivity.requestCode, intent);
                finish();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                //网络出错
                Toast.makeText(ProfileEditActivity.this, "网络罢工了...请检查网络设置", Toast.LENGTH_LONG).show();
            }
        });
    }

    //把本地的onActivityResult()方法回调绑定到对象
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectHeadImgList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectHeadImgList);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }
}
