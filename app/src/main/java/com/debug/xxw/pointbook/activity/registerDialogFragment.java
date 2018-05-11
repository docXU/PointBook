package com.debug.xxw.pointbook.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.adapter.TextWatcherAdapter;
import com.debug.xxw.pointbook.model.User;
import com.debug.xxw.pointbook.net.ConstURL;
import com.debug.xxw.pointbook.net.RequestManager;

import java.util.HashMap;

public class registerDialogFragment extends DialogFragment implements View.OnClickListener {

    EditText mEtEmail;
    EditText mEtPwd;
    EditText mEtPwdRe;
    Button mBtnRegister;
    ImageButton btn_close;
    ProgressBar loading;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_register, null);
        initView(view);
        builder.setView(view);
        return builder.create();
    }

    private void initView(View view) {
        mEtEmail = view.findViewById(R.id.et_email);
        loading = view.findViewById(R.id.loading);
        mEtPwd = view.findViewById(R.id.et_password);
        mEtPwdRe = view.findViewById(R.id.et_password_re);
        mBtnRegister = view.findViewById(R.id.btn_register);
        btn_close = view.findViewById(R.id.btn_close);

        loading.setVisibility(View.GONE);
        mEtPwd.addTextChangedListener(adapter);
        mBtnRegister.setOnClickListener(this);
        btn_close.setOnClickListener(this);
    }

    TextWatcherAdapter adapter = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            //密码长度保证
            int len = s.length();
            if (len >= 8 && len <= 16) {
                mBtnRegister.setEnabled(true);
            } else {
                mBtnRegister.setEnabled(false);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_register:
                if (!mEtPwd.getText().toString().equals(mEtPwdRe.getText().toString())) {
                    Toast.makeText(registerDialogFragment.this.getActivity(), "密码不一样~", Toast.LENGTH_SHORT).show();
                    return;
                }
                loading.setVisibility(View.VISIBLE);
                HashMap<String, String> params = new HashMap<>();
                params.put("email", mEtEmail.getText().toString());
                params.put("password", mEtPwd.getText().toString());

                RequestManager.getInstance(this.getActivity()).requestAsyn(ConstURL.USER_REGISTER, RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        //登录成功或失败通过result判断
                        User user = User.parseResult(result);
                        if (user != null) {
                            MainActivity.user = user;

                            SharedPreferences sp = registerDialogFragment.this.getActivity().getSharedPreferences("config", 0);
                            if (User.saveUserSingleton(sp, user)) {
                                Toast.makeText(registerDialogFragment.this.getActivity(), "注册成功~", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(registerDialogFragment.this.getActivity(), "一次性登录哈哈~", Toast.LENGTH_SHORT).show();
                            }
                            dismiss();
                            //todo:刷新user_bar
                            ((SettingActivity) registerDialogFragment.this.getActivity()).refreshUserBar(user);
                        } else {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(registerDialogFragment.this.getActivity(), "账号或密码错误！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        //账号或密码错误
                        loading.setVisibility(View.GONE);
                        Toast.makeText(registerDialogFragment.this.getActivity(), "邮箱已被注册", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            default:
                break;
        }
    }
}
