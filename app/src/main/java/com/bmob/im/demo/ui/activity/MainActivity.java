package com.bmob.im.demo.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.v3.update.BmobUpdateAgent;

import com.bmob.im.demo.CustomApplication;
import com.bmob.im.demo.MyMessageReceiver;
import com.bmob.im.demo.R;
import com.bmob.im.demo.ui.fragment.ContactFragment;
import com.bmob.im.demo.ui.fragment.DianDiFragment;
import com.bmob.im.demo.ui.fragment.RecentFragment;
import com.bmob.im.demo.ui.fragment.SettingsFragment;

/**
 * 登陆
 * @ClassName: MainActivity
 * @Description: TODO
 * @author smile
 * @date 2014-5-29 下午2:45:35
 */
public class MainActivity extends ActivityBase implements EventListener{

    private static long firstTime;
    ImageView iv_recent_tips, iv_contact_tips, iv_diandi_tips;//消息提示
    private Button[] mTabs;
    private DianDiFragment mDianDiFragment;
    private ContactFragment contactFragment;
    private RecentFragment recentFragment;
    private SettingsFragment settingFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTab();
    }

    void initView() {
        mTabs = new Button[4];
        mTabs[0] = (Button) findViewById(R.id.btn_diandi);
        mTabs[1] = (Button) findViewById(R.id.btn_message);
        mTabs[2] = (Button) findViewById(R.id.btn_contract);
        mTabs[3] = (Button) findViewById(R.id.btn_set);
        iv_diandi_tips = (ImageView) findViewById(R.id.iv_diandi_tips);
        iv_recent_tips = (ImageView) findViewById(R.id.iv_recent_tips);
        iv_contact_tips = (ImageView) findViewById(R.id.iv_contact_tips);
        //把第一个tab设为选中状态
        mTabs[0].setSelected(true);
    }

    private void initTab() {
        mDianDiFragment = new DianDiFragment();
        contactFragment = new ContactFragment();
        recentFragment = new RecentFragment();
        settingFragment = new SettingsFragment();
        fragments = new Fragment[]{mDianDiFragment, recentFragment, contactFragment, settingFragment};
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mDianDiFragment).
                add(R.id.fragment_container, contactFragment).hide(contactFragment).show(mDianDiFragment).commit();
    }

    /**
     * button点击事件
     * @param view
     */
    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.btn_diandi:
                index = 0;
                break;
            case R.id.btn_message:
                index = 1;
                break;
            case R.id.btn_contract:
                index = 2;
                break;
            case R.id.btn_set:
                index = 3;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        //把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //小圆点提示
        if (BmobDB.create(this).hasUnReadMsg()) {
            iv_recent_tips.setVisibility(View.VISIBLE);

        } else {
            iv_recent_tips.setVisibility(View.GONE);
        }
        if (BmobDB.create(this).hasNewInvite()) {
            iv_contact_tips.setVisibility(View.VISIBLE);
        } else {
            iv_contact_tips.setVisibility(View.GONE);
        }
        iv_diandi_tips.setVisibility(View.GONE);
        MyMessageReceiver.ehList.add(this);// 监听推送的消息
        //清空
        MyMessageReceiver.mNewNum = 0;

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
    }

    @Override
    public void onMessage(BmobMsg message) {
        // TODO Auto-generated method stub
        // 声音提示
        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            CustomApplication.getInstance().getMediaPlayer().start();
        }
        iv_recent_tips.setVisibility(View.VISIBLE);
        //保存接收到的消息-并发送已读回执给对方
        BmobChatManager.getInstance(this).saveReceiveMessage(true, message);
        if (currentTabIndex == 0) {
            //当前页面如果为会话页面，刷新此页面
            if (recentFragment != null) {
                recentFragment.refresh();
            }
        }

    }

    @Override
    public void onNetChange(boolean isNetConnected) {
        // TODO Auto-generated method stub
        if (isNetConnected) {
            ShowToast(R.string.network_tips);
        }
    }

    @Override
    public void onAddUser(BmobInvitation message) {
        // TODO Auto-generated method stub
        // 声音提示
        boolean isAllow = CustomApplication.getInstance().getSpUtil().isAllowVoice();
        if (isAllow) {
            CustomApplication.getInstance().getMediaPlayer().start();
        }
        iv_contact_tips.setVisibility(View.VISIBLE);
        if (currentTabIndex == 1) {
            if (contactFragment != null) {
                contactFragment.refresh();
            }
        } else {
            //同时提醒通知
            String tickerText = message.getFromname() + "请求添加好友";
            boolean isAllowVibrate = CustomApplication.getInstance().getSpUtil().isAllowVibrate();
            BmobNotifyManager.getInstance(this).showNotify(isAllow, isAllowVibrate, R.drawable.ic_launcher, tickerText, message.getFromname(), tickerText.toString(), NewFriendActivity.class);
        }
    }

    @Override
    public void onOffline() {
        // TODO Auto-generated method stub
        showOfflineDialog(this);
    }

    @Override
    public void onReaded(String conversionId, String msgTime) {
        // TODO Auto-generated method stub
    }

    /**
     * 连续按两次返回键就退出
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (firstTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            ShowToast("再按一次退出程序");
        }
        firstTime = System.currentTimeMillis();
    }


}
