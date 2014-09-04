package com.bmob.im.demo.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.listener.SaveListener;

import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Constant;
import com.bmob.im.demo.proxy.UserProxy;
import com.bmob.im.demo.util.CommonUtils;

public class RegisterActivity extends BaseActivity implements UserProxy.ISignUpListener {

    TextView btn_register;
    EditText et_username, et_password, et_email;
    BmobChatUser currentUser;
    UserProxy userProxy;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userProxy = new UserProxy(this);
        initTopBarForLeft("注册");

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        et_email = (EditText) findViewById(R.id.et_email);

        btn_register = (TextView) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                register();
            }
        });
    }
    @Override
    void findView() {

    }

    @Override
    void initView() {

    }

    @Override
    void initData() {

    }

    @Override
    void bindEvent() {

    }
    private void register() {
        String name = et_username.getText().toString();
        String password = et_password.getText().toString();
        String pwd_again = et_email.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ShowToast(R.string.toast_error_username_null);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ShowToast(R.string.toast_error_password_null);
            return;
        }
        if (!pwd_again.equals(password)) {
            ShowToast(R.string.toast_error_comfirm_password);
            return;
        }

        boolean isNetConnected = CommonUtils.isNetworkAvailable(this);
        if (!isNetConnected) {
            ShowToast(R.string.network_tips);
            return;
        }
        progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在注册...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        userProxy.setOnSignUpListener(this);
        userProxy.signUp(name, password);
    }

    @Override
    public void onSignUpFailure(String msg) {
        ShowToast("注册失败");
        progress.dismiss();
    }

    @Override
    public void onSignUpSuccess() {
        progress.dismiss();
        ShowToast("注册成功");
        userManager.bindInstallationForRegister(et_username.getText().toString());
        updateUserLocation();
        sendBroadcast(new Intent(Constant.ACTION_REGISTER_SUCCESS_FINISH));
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
