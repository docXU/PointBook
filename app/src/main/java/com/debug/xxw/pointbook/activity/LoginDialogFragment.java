package com.debug.xxw.pointbook.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.adapter.TextWatcherAdapter;
import com.debug.xxw.pointbook.model.User;
import com.debug.xxw.pointbook.utils.net.ConstURL;
import com.debug.xxw.pointbook.utils.net.RequestManager;

import java.util.HashMap;

public class LoginDialogFragment extends DialogFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    EditText mEtEmail;
    EditText mEtPwd;
    Button mBtnNext;
    ImageButton btn_close;
    ProgressBar loading;
    CheckBox iv_display;
    TextView tv_register;
    TextWatcherAdapter adapter = new TextWatcherAdapter() {
        @Override
        public void afterTextChanged(Editable s) {
            //密码长度保证
            int len = s.length();
            if (len >= 6 && len <= 16) {
                mBtnNext.setEnabled(true);
            } else {
                mBtnNext.setEnabled(false);
            }
        }
    };
    private String email;

    public static LoginDialogFragment newInstance(String email) {
        LoginDialogFragment mFragment = new LoginDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("email", email);
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_login, null);
        initView(view);
        builder.setView(view);
        return builder.create();
    }

    private void initView(View view) {
        email = getArguments().getString("email");
        mEtEmail = view.findViewById(R.id.et_email);
        loading = view.findViewById(R.id.loading);
        tv_register = view.findViewById(R.id.tv_register);
        mEtPwd = view.findViewById(R.id.et_pwd);
        mBtnNext = view.findViewById(R.id.btn_next);
        btn_close = view.findViewById(R.id.btn_close);
        iv_display = view.findViewById(R.id.iv_display);

        mEtEmail.setText(email);
        loading.setVisibility(View.GONE);
//        mEtEmail.setOnFocusChangeListener(this);
        tv_register.setOnClickListener(this);
        mEtPwd.addTextChangedListener(adapter);
        mBtnNext.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        iv_display.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next:
                loading.setVisibility(View.VISIBLE);
                HashMap<String, String> params = new HashMap<>();
                params.put("email", mEtEmail.getText().toString());
                params.put("password", mEtPwd.getText().toString());

                RequestManager.getInstance(this.getActivity()).requestAsyn(ConstURL.USER_VERIFY, RequestManager.TYPE_GET, params, new RequestManager.ReqCallBack<Object>() {
                    @Override
                    public void onReqSuccess(Object result) {
                        //登录成功或失败通过result判断
                        User user = User.parseResult(result);
                        if (user != null) {
                            MainActivity.user = user;
                            SharedPreferences sp = LoginDialogFragment.this.getActivity().getSharedPreferences("config", 0);
                            if (User.saveUserSingleton(sp, user)) {
                                Toast.makeText(LoginDialogFragment.this.getActivity(), "登录成功~", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginDialogFragment.this.getActivity(), "一次性登录哈哈~", Toast.LENGTH_SHORT).show();
                            }
                            dismiss();
                            //todo:刷新user_bar
                            ((SettingActivity) LoginDialogFragment.this.getActivity()).refreshUserBar(user);
                        } else {
                            //账号或密码错误
                            loading.setVisibility(View.GONE);
                            Toast.makeText(LoginDialogFragment.this.getActivity(), "账号或密码错误！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onReqFailed(String errorMsg) {
                        //网络出错
                        loading.setVisibility(View.GONE);
                        Toast.makeText(LoginDialogFragment.this.getActivity(), "网络罢工了...请检查网络设置", Toast.LENGTH_LONG).show();
                    }
                });

                break;
            case R.id.tv_register:
                //注册
                registerDialogFragment rdf = new registerDialogFragment();
                rdf.show(getFragmentManager(), "registerdailog");
                dismiss();
                break;
            case R.id.btn_close:
                dismiss();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}
