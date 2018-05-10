package com.debug.xxw.pointbook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.model.User;
import com.squareup.picasso.Picasso;

/**
 * 需要带入user和last_interaction
 */
public class ProfileActivity extends AppCompatActivity {
    private String last_interaction;
    public static int requestCode = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle bundle = this.getIntent().getExtras();

        User user = null;
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
            last_interaction = bundle.getString("last_interaction");
        }

        if (user != null) {
            Picasso.with(ProfileActivity.this).load(user.getHeadimg()).error(R.drawable.defaulthead).into((ImageView) findViewById(R.id.userPicture));
            ((TextView) findViewById(R.id.user_name)).setText(user.getUsername());
            ((TextView) findViewById(R.id.last_interaction)).setText(String.format("状态：%s", last_interaction));
            ((TextView) ((LinearLayout) findViewById(R.id.telephone)).getChildAt(1)).setText(user.getTelephone());
            ((TextView) ((LinearLayout) findViewById(R.id.wechat_id)).getChildAt(1)).setText(user.getWechat_id());
            ((TextView) ((LinearLayout) findViewById(R.id.weibo_name)).getChildAt(1)).setText(user.getWeibo_name());
            ((TextView) ((LinearLayout) findViewById(R.id.sex_age)).getChildAt(1)).setText(String.format("%s / %s", user.getSex(), user.getAge()));
            ((TextView) ((LinearLayout) findViewById(R.id.describe)).getChildAt(1)).setText(user.getDescribe());

            if (MainActivity.user != null && user.getId() == MainActivity.user.getId()) {
                View v = findViewById(R.id.edit);
                v.setVisibility(View.VISIBLE);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("user", MainActivity.user);
                        Intent intent = new Intent().setClass(ProfileActivity.this, ProfileEditActivity.class);
                        intent.putExtras(bundle);
                        ProfileActivity.this.startActivityForResult(intent, requestCode);
                    }
                });
            }
        } else {
            //TODO:去登录界面
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null && requestCode == ProfileActivity.requestCode) {
            Bundle bundle = data.getExtras();
            User user = (User) bundle.getSerializable("user");
            if (user == null) {
                return;
            }

            last_interaction = "修改个人资料";
            MainActivity.user = user;
            SharedPreferences sp = getSharedPreferences("config", 0);
            User.saveUserSingleton(sp, user);

            Picasso.with(ProfileActivity.this).load(user.getHeadimg()).error(R.drawable.defaulthead).into((ImageView) findViewById(R.id.userPicture));
            ((TextView) findViewById(R.id.user_name)).setText(user.getUsername());
            ((TextView) findViewById(R.id.last_interaction)).setText(String.format("状态：%s", last_interaction));
            ((TextView) ((LinearLayout) findViewById(R.id.telephone)).getChildAt(1)).setText(user.getTelephone());
            ((TextView) ((LinearLayout) findViewById(R.id.wechat_id)).getChildAt(1)).setText(user.getWechat_id());
            ((TextView) ((LinearLayout) findViewById(R.id.weibo_name)).getChildAt(1)).setText(user.getWeibo_name());
            ((TextView) ((LinearLayout) findViewById(R.id.sex_age)).getChildAt(1)).setText(String.format("%s / %s", user.getSex(), user.getAge()));
            ((TextView) ((LinearLayout) findViewById(R.id.describe)).getChildAt(1)).setText(user.getDescribe());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
