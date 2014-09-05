package com.bmob.im.demo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import cn.bmob.im.BmobUserManager;

public class ActivityBase extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
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

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    public void checkLogin() {
        BmobUserManager userManager = BmobUserManager.getInstance(this);
        if (userManager.getCurrentUser() == null) {
            ShowToast("您的账号已在其他设备上登录!");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
