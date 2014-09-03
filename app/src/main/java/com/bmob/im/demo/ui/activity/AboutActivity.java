package com.bmob.im.demo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.bmob.im.demo.R;

import cn.bmob.v3.update.BmobUpdateAgent;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mCheckUpdateLayout, mShareLayout, mFeedBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
    }

    private void findView() {
        setContentView(R.layout.activity_about);
        initTopBarForLeft("关于点滴");
        mCheckUpdateLayout = (RelativeLayout) findViewById(R.id.activity_about_check_update_layout);
        mShareLayout = (RelativeLayout) findViewById(R.id.activity_about_share_layout);
        mFeedBackLayout = (RelativeLayout) findViewById(R.id.activity_about_feedback_layout);
    }

    private void initView() {
        initData();
        bindEvent();
    }

    private void initData() {
    }

    private void bindEvent() {
        mCheckUpdateLayout.setOnClickListener(this);
        mShareLayout.setOnClickListener(this);
        mFeedBackLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_about_check_update_layout:
                BmobUpdateAgent.forceUpdate(AboutActivity.this);

                break;
            case R.id.activity_about_share_layout:
                Intent localIntent1 = new Intent("android.intent.action.SEND");
                localIntent1.setType("text/plain");
                localIntent1.putExtra("android.intent.extra.SUBJECT", "分享");
                localIntent1.putExtra("android.intent.extra.TEXT", "点滴是记录生活中重要的日子的小工具。还在为女友突然问你们相恋了多久而瞠目结舌吗？还在为关键时刻忘记女友的生日而发愁吗？那么这个小工具正是你需要的。/\n");
                startActivity(Intent.createChooser(localIntent1, "分享给好友"));
                break;
            case R.id.activity_about_feedback_layout:
                Intent localIntent2 = new Intent("android.intent.action.SENDTO");
                localIntent2.setData(Uri.parse("mailto:462679107.com"));
                localIntent2.putExtra("android.intent.extra.SUBJECT", "我觉得你们的软件还有一些地方可以改进");
                startActivity(Intent.createChooser(localIntent2, ""));
                break;
        }
    }
}
