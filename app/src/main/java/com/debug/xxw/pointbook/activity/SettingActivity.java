package com.debug.xxw.pointbook.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.model.LetterDrawable;
import com.debug.xxw.pointbook.model.User;
import com.debug.xxw.pointbook.viewmodel.CircleImageView;
import com.debug.xxw.pointbook.viewmodel.SettingView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author xxw
 */
public class SettingActivity extends AppCompatActivity {
    private List<HashMap<String, Object>> mListData;
    private final String FUN_NAME = "fun_name";
    private final String FUN_ICON = "fun_icon";
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initListData();
        Bundle bundle = this.getIntent().getExtras();
        user = (User) bundle.getSerializable("user");
        SettingView mSettingView = (SettingView) findViewById(R.id.setting_view);
        mSettingView.setListAdapter(new SettingListAdapter());
        mSettingView.setUserAdapter(new UserDataAdapter());
    }

    public class SettingListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.setting_listitem, null);
                holder = new ListViewHolder();
                holder.funName = convertView.findViewById(R.id.fun_name);
                holder.funIcon = convertView.findViewById(R.id.fun_ic);

                convertView.setTag(holder);
                ((AdapterView) parent).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(SettingActivity.this, "点击了列元素", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                holder = (ListViewHolder) (convertView.getTag());
            }

            holder.funName.setText(mListData.get(position).get(FUN_NAME).toString());
            holder.funIcon.setImageDrawable((Drawable) mListData.get(position).get(FUN_ICON));

            return convertView;
        }
    }

    private final class ListViewHolder {
        private TextView funName;
        private ImageView funIcon;
    }

    public class UserDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserBarViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(SettingActivity.this).inflate(R.layout.user_bar, null);
                holder = new UserBarViewHolder();
                holder.head = convertView.findViewById(R.id.userhead);
                holder.username = convertView.findViewById(R.id.username);
                holder.describe = convertView.findViewById(R.id.describe);

                convertView.setTag(holder);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginDialogFragment.newInstance("1304924151@qq.com")
                                .show(getSupportFragmentManager(), "loginFragment");
                    }
                });
            } else {
                holder = (UserBarViewHolder) (convertView.getTag());
            }
            if (user == null) {
                Picasso.with(SettingActivity.this).load(R.mipmap.ic_launcher_round).error(R.drawable.defaulthead).into(holder.head);
                holder.username.setText("__点击登录");
                holder.describe.setText(" click to login");
            } else {
                Picasso.with(SettingActivity.this).load("https://t12.baidu.com/it/u=1379413910,591030782&fm=173&app=25&f=JPEG?w=550&h=309&s=5EAE8744760A714306AB51C7030050AB").error(R.drawable.defaulthead).into(holder.head);
                holder.username.setText("xxw");
                holder.describe.setText("我的app由大自然创造");
            }

            return convertView;
        }
    }

    private final class UserBarViewHolder {
        private CircleImageView head;
        private TextView username;
        private TextView describe;

    }


    public void initListData() {
        mListData = new ArrayList<>();

        HashMap<String, Object> map1 = new HashMap<>(2);
        map1.put(FUN_NAME, "首页");
        map1.put(FUN_ICON, new LetterDrawable("M", getResources().getColor(R.color.colorCircleText), getResources().getColor(R.color.colorAccent)));
        mListData.add(map1);

        HashMap<String, Object> map2 = new HashMap<>(2);
        map2.put(FUN_NAME, "关闭图层");
        map2.put(FUN_ICON, new LetterDrawable("P", getResources().getColor(R.color.colorCircleText), getResources().getColor(R.color.colorAccent)));
        mListData.add(map2);

        HashMap<String, Object> map4 = new HashMap<>(2);
        map4.put(FUN_NAME, "调整活动范围");
        map4.put(FUN_ICON, new LetterDrawable("R", getResources().getColor(R.color.colorCircleText), getResources().getColor(R.color.colorAccent)));
        mListData.add(map4);

        HashMap<String, Object> map5 = new HashMap<>(2);
        map5.put(FUN_NAME, "我的收藏");
        map5.put(FUN_ICON, new LetterDrawable("T", getResources().getColor(R.color.colorCircleText), getResources().getColor(R.color.colorAccent)));
        mListData.add(map5);

        HashMap<String, Object> map6 = new HashMap<>(2);
        map6.put(FUN_NAME, "个人中心");
        map6.put(FUN_ICON, new LetterDrawable("S", getResources().getColor(R.color.colorCircleText), getResources().getColor(R.color.colorAccent)));
        mListData.add(map6);

        HashMap<String, Object> map7 = new HashMap<>(2);
        map7.put(FUN_NAME, "关于我");
        map7.put(FUN_ICON, new LetterDrawable("I", getResources().getColor(R.color.colorCircleText), getResources().getColor(R.color.colorAccent)));
        mListData.add(map7);
    }


}
