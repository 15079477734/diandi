package com.bmob.im.demo.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.bmob.im.BmobUserManager;

import com.bmob.im.demo.CustomApplication;
import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.activity.AboutActivity;
import com.bmob.im.demo.ui.activity.BlackListActivity;
import com.bmob.im.demo.ui.activity.LoginActivity;
import com.bmob.im.demo.ui.activity.MsgReciverSetActivity;
import com.bmob.im.demo.ui.activity.SetMyInfoActivity;
import com.bmob.im.demo.ui.activity.SettingActivity;
import com.bmob.im.demo.util.SharePreferenceUtil;

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

    Button btn_logout;
    TextView tv_set_name;
    RelativeLayout layout_info, layout_blacklist, layout_msgreciver, layout_about;


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
        initView();
        initData();
    }

    public void initView() {
        findView();
        initData();
        bindEvent();
        initTopBarForOnlyTitle("设置");



    }

    @Override
    void findView() {
        layout_blacklist = (RelativeLayout) findViewById(R.id.layout_blacklist);
        layout_info = (RelativeLayout) findViewById(R.id.layout_info);
        layout_about = (RelativeLayout) findViewById(R.id.fragment_setting_about_layout);
        tv_set_name = (TextView) findViewById(R.id.tv_set_name);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        layout_msgreciver = (RelativeLayout) findViewById(R.id.fragment_setting_msg_reciver);

    }

    void initData() {
        tv_set_name.setText(BmobUserManager.getInstance(getActivity())
                .getCurrentUser().getNick());
    }

    @Override
    void bindEvent() {
        btn_logout.setOnClickListener(this);
        layout_info.setOnClickListener(this);
        layout_blacklist.setOnClickListener(this);
        layout_msgreciver.setOnClickListener(this);
        layout_about.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_blacklist:// 启动到黑名单页面
                startAnimActivity(new Intent(getActivity(), BlackListActivity.class));
                break;
            case R.id.layout_info:// 启动到个人资料页面
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("from", "me");
                startActivity(intent);
                break;
            case R.id.btn_logout:
                CustomApplication.getInstance().logout();
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                break;
            case R.id.fragment_setting_msg_reciver:
                startActivity(new Intent(getActivity(), MsgReciverSetActivity.class));
                break;
            case R.id.fragment_setting_about_layout:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
        }
    }

}
