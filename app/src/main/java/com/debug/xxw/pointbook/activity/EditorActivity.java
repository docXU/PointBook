package com.debug.xxw.pointbook.activity;

/**
 * Created by xxw on 2017/10/23.
 */

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.adapter.GridImageAdapter;
import com.debug.xxw.pointbook.model.NineGridModel;
import com.debug.xxw.pointbook.model.Weibo;
import com.debug.xxw.pointbook.net.QiniuUtils;
import com.debug.xxw.pointbook.net.RequestManager;
import com.debug.xxw.pointbook.net.WeiboNetter;
import com.debug.xxw.pointbook.adapter.FullyGridLayoutManager;
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
import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ren.qinc.edit.PerformEdit;

/**
 * @author xxw
 */
public class EditorActivity extends AppCompatActivity {
    private int RequestCode = 10;
    private EditText mEditText;
    private GridImageAdapter adapter;
    private final static String TAG = EditorActivity.class.getSimpleName();
    private List<LocalMedia> selectList = new ArrayList<>();

    private Weibo newWeiboInstance = new Weibo();

    private PerformEdit mPerformEdit;
    private WeiboNetter mWeiboNetter;
    private QiniuUtils mQiniuUtils = new QiniuUtils();
    private Bundle b;
    String[] c = new String[]{
            "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "楮", "卫", "蒋", "沈", "韩", "杨",
            "段干", "百里", "东郭", "南门", "呼延", "羊舌", "梁丘", "左丘", "东门", "西门", "南宫"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_editor);

        initView();
        b = getIntent().getExtras();

        mWeiboNetter = new WeiboNetter(getApplicationContext());

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    PictureFileUtils.deleteCacheDirFile(EditorActivity.this);
                } else {
                    Toast.makeText(EditorActivity.this,
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

    private void initView() {
        mEditText = ((EditText) findViewById(R.id.editweibo));
        if (null != mEditText) {
            mPerformEdit = new PerformEdit(mEditText) {
                @Override
                protected void onTextChanged(Editable s) {
                    super.onTextChanged(s);
                }
            };
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(EditorActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(EditorActivity.this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(9);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, selectList);
                            PictureSelector.create(EditorActivity.this).externalPicturePreview(position, "/save", selectList);
                            break;
                        case 2:
                            // 预览视频
                            PictureSelector.create(EditorActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                            PictureSelector.create(EditorActivity.this).externalPictureAudio(media.getPath());
                            break;
                        default:
                            break;
                    }
                }
            }
        });

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            // 进入相册 以下是例子：不需要的api可以不写
            PictureSelector.create(EditorActivity.this)
                    .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                    .maxSelectNum(12)// 最大图片选择数量
                    .minSelectNum(1)// 最小选择数量
                    .imageSpanCount(4)// 每行显示个数
                    .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                    .previewImage(true)// 是否可预览图片
                    .previewVideo(true)// 是否可预览视频
                    .enablePreviewAudio(true) // 是否可播放音频
                    .isCamera(true)// 是否显示拍照按钮
                    .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                    //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                    //.setOutputCameraPath("/CustomPath")// 自定义拍照保存路径
                    //.enableCrop(true)// 是否裁剪
                    .compress(true)// 是否压缩
                    .synOrAsy(true)//同步true或异步false 压缩 默认同步
                    //.compressSavePath(getPath())//压缩图片保存地址
                    //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                    .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                    //.withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                    .isGif(true)// 是否显示gif图片
                    .freeStyleCropEnabled(false)// 裁剪框是否可拖拽
                    .circleDimmedLayer(false)// 是否圆形裁剪
                    .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                    .openClickSound(false)// 是否开启点击声音
                    .selectionMedia(selectList)// 是否传入已选图片
                    //.videoMaxSecond(15)
                    //.videoMinSecond(10)
                    .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                    .cropCompressQuality(50)// 裁剪压缩质量 默认100
                    .minimumCompressSize(100)// 小于100kb的图片不压缩
                    //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                    .rotateEnabled(true) // 裁剪是否可旋转图片
                    .scaleEnabled(true)// 裁剪是否可放大缩小图片
                    //.videoQuality()// 视频录制质量 0 or 1
                    //.videoSecond()//显示多少秒以内的视频or音频也可适用
                    //.recordVideoSecond()//录制视频秒数 默认60s
                    .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
        }
    };


    //把本地的onActivityResult()方法回调绑定到对象
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }


    private void startUpload() {
        final int picsSize = selectList.size();
        Log.e(TAG, "picsSize=" + picsSize);
        if (picsSize > 0) {
            final NineGridModel nineGridTestModel = new NineGridModel();
            int i = 0;
            for (LocalMedia m : selectList) {
                String path;
                if (m.isCut() && !m.isCompressed()) {
                    path = m.getCutPath();
                } else if (m.isCompressed() || (m.isCut() && m.isCompressed())) {
                    path = m.getCompressPath();
                } else {
                    path = m.getPath();
                }
                nineGridTestModel.urlList.add(path);
                nineGridTestModel.remoteUrlList.put(i, "upload fail");
                i++;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EditorActivity.this, "正在上传图片...", Toast.LENGTH_SHORT).show();
                    for (String path : nineGridTestModel.urlList) {
                        final String locality = path;
                        mQiniuUtils.uploadFile(locality, new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject res) {
                                //res包含hash、key等信息，具体字段取决于上传策略的设置
                                try {
                                    if (info.isOK()) {
                                        Log.i(TAG + " qiniu", "Upload Success");
                                        //替换图片路径为七牛外链
                                        List<String> urlList = nineGridTestModel.urlList;
                                        int size = urlList.size();
                                        for (int i = 0; i < size; i++) {
                                            if (locality.equals(urlList.get(i))) {
                                                nineGridTestModel.remoteUrlList.put(i, getString(R.string.qiniu_address) + res.getString("key"));
                                                break;
                                            }
                                        }
                                    } else {
                                        Log.i(TAG + "qiniu", "Upload Fail");
                                        //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                                        Log.e(res.getString("code"), res.getString("error"));
                                        if ("token out of date".equals(res.getString("error"))) {
                                            mQiniuUtils.queryToken();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //上传完毕
                                mQiniuUtils.UploadCurItemComplete();
                                if (mQiniuUtils.getUploadComItemCount() == picsSize) {
                                    mQiniuUtils.UploadTaskComplete();
                                    newWeiboInstance.setContentImgs(nineGridTestModel);
                                    Log.i(TAG, nineGridTestModel.remoteUrlList.size() + "");

                                    submitWeibo();
                                    Log.i(TAG, "uploadTask Complete");
                                    Toast.makeText(EditorActivity.this, "图片上传完毕~", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.i(TAG, "uploadTask Running...");
                                }
                            }
                        });
                    }
                }
            });
        } else {
            submitWeibo();
        }
    }

    public void submitWeibo() {
        String content = mEditText.getText().toString();
        String name = c[new Random().nextInt(c.length)] + "可爱";
        newWeiboInstance.setUsername(name)
                .setContent(content)
                .setFrom("Bigbang")
                .setPublicTime("1s前")
                .setRecentComment("0")
                .setRecentLike("0")
                .setRecentShare("0");
        mWeiboNetter.addWeibo(b.getString("entry_id"), newWeiboInstance, new RequestManager.ReqCallBack() {
            @Override
            public void onReqSuccess(Object result) {
                Intent i = new Intent();
                i.putExtra("status", "ok");
                i.putExtra("weibo", newWeiboInstance);
                setResult(RequestCode, i);
                finish();
            }

            @Override
            public void onReqFailed(String errorMsg) {
                Log.e(TAG + "add weibo callback fail", errorMsg);
                Intent i = new Intent();
                i.putExtra("status", "fail");
                setResult(RequestCode, i);
                finish();
            }
        });
    }

    /**
     * onRequestPermissionsResult()方法权限回调绑定到对象
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RequestCode);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_undo) {
            mPerformEdit.undo();
            return true;
        } else if (itemId == R.id.action_redo) {
            mPerformEdit.redo();
            return true;
        } else if (itemId == R.id.action_clear) {
            mPerformEdit.clearHistory();
            return true;
        } else if (itemId == R.id.action_submit) {
            startUpload();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
