package com.bmob.im.demo.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bmob.im.demo.CustomApplication;
import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.activity.AboutActivity;
import com.bmob.im.demo.ui.activity.BlackListActivity;
import com.bmob.im.demo.ui.activity.LoginActivity;
import com.bmob.im.demo.ui.activity.MsgReciverSetActivity;
import com.bmob.im.demo.ui.activity.SettingActivity;

import cn.bmob.im.BmobUserManager;

/**
 * 设置
 *
 * @author smile
 * @ClassName: SetFragment
 * @Description: TODO
 * @date 2014-6-7 下午1:00:27
 */
@SuppressLint("SimpleDateFormat")
public class SettingsFragment extends BaseFragment implements OnClickListener {

    Button mlogoutBtn;
    TextView mNameText;
    TextView mUpdateText;
    RelativeLayout mInfoLayout, mBlanklistLayout, mMsgReciverLayout, mAboutLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findView();
        initView();
        if (CustomApplication.getInstance().getSpUtil().isUpdate()) {
            mUpdateText.setText("有更新！");
        }
    }

    @Override
    void findView() {
        mBlanklistLayout = (RelativeLayout) findViewById(R.id.fragment_set_blanklist_layout);
        mInfoLayout = (RelativeLayout) findViewById(R.id.fragment_set_info_layout);
        mAboutLayout = (RelativeLayout) findViewById(R.id.fragment_setting_about_layout);
        mNameText = (TextView) findViewById(R.id.fragment_set_info_text);
        mlogoutBtn = (Button) findViewById(R.id.fragment_set_logout_btn);
        mUpdateText = (TextView) findViewById(R.id.fragment_set_update_text);
        mMsgReciverLayout = (RelativeLayout) findViewById(R.id.fragment_set_msg_reciver_layout);
    }

    public void initView() {
        initTopBarForOnlyTitle("设置");
        initData();
        bindEvent();
    }

    void initData() {
        mNameText.setText(BmobUserManager.getInstance(getActivity())
                .getCurrentUser().getNick());
    }

    @Override
    void bindEvent() {
        mlogoutBtn.setOnClickListener(this);
        mInfoLayout.setOnClickListener(this);
        mBlanklistLayout.setOnClickListener(this);
        mMsgReciverLayout.setOnClickListener(this);
        mAboutLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_set_info_layout:// 启动到个人资料页面
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("from", "me");
                startActivity(intent);
                break;
            case R.id.fragment_set_blanklist_layout:// 启动到黑名单页面
                startAnimActivity(new Intent(getActivity(), BlackListActivity.class));
                break;
            case R.id.fragment_set_msg_reciver_layout://启动到消息设置页面
                startActivity(new Intent(getActivity(), MsgReciverSetActivity.class));
                break;
            case R.id.fragment_setting_about_layout://启动到关于页面
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.fragment_set_logout_btn://登出
                CustomApplication.getInstance().logout();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;

        }
    }
}
