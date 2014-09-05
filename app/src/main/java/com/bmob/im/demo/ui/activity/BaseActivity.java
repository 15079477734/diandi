package com.bmob.im.demo.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.bmob.im.demo.CustomApplication;
import com.bmob.im.demo.R;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Constant;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.Sputil;
import com.bmob.im.demo.view.HeaderLayout;
import com.bmob.im.demo.view.HeaderLayout.HeaderStyle;
import com.bmob.im.demo.view.HeaderLayout.onLeftImageButtonClickListener;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.dialog.DialogTips;

import java.util.List;

import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 基类
 *
 * @author smile
 * @ClassName: BaseActivity
 * @Description: TODO
 * @date 2014-6-13 下午5:05:38
 */
abstract class BaseActivity extends FragmentActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public final String TAG = getClass().getName();

    protected BmobUserManager userManager;
    protected BmobChatManager chatManager;
    protected Sputil sputil;


    protected HeaderLayout mHeaderLayout;
    protected int mScreenWidth;
    protected int mScreenHeight;

    protected CustomApplication mApplication;
    protected Context mContext;
    protected Resources mResources;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (userManager == null)
            userManager = BmobUserManager.getInstance(this);
        if (chatManager == null)
            chatManager = BmobChatManager.getInstance(this);
        if (mApplication == null)
            mApplication = CustomApplication.getInstance();
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        initConfigure();
    }

    abstract void findView();

    abstract void initView();

    abstract void initData();

    abstract void bindEvent();

    private void initConfigure() {
        mContext = this;
        if (null == mApplication) {
            mApplication = mApplication.getInstance();
        }
        mApplication.addActivity(this);
        if (null == sputil) {
            sputil = new Sputil(this, Constant.PRE_NAME);
        }
        sputil.getInstance().registerOnSharedPreferenceChangeListener(this);
        mResources = getResources();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
    }

    public void ShowToast(final String text) {
        if (!TextUtils.isEmpty(text)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG);
                    } else {
                        mToast.setText(text);
                    }
                    mToast.show();
                    Log.d(TAG, text);
                }
            });

        }
    }

    public void ShowToast(final int resId) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(BaseActivity.this.getApplicationContext(), resId,
                            Toast.LENGTH_LONG);
                } else {
                    mToast.setText(resId);
                }
                mToast.show();
                Log.d(TAG, getString(resId));
            }
        });
    }


    public void ShowLog(String msg) {
        BmobLog.i(msg);
    }

    public void initTopBarForOnlyTitle(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
        mHeaderLayout.setDefaultTitle(titleName);
    }

    public void initTopBarForBoth(String titleName, int rightDrawableId, String text,
                                  onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text,
                listener);
    }

    public void initTopBarForBoth(String titleName, int rightDrawableId,
                                  onRightImageButtonClickListener listener) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
        mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
                listener);
    }

    public void initTopBarForLeft(String titleName) {
        mHeaderLayout = (HeaderLayout) findViewById(R.id.common_actionbar);
        mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
        mHeaderLayout.setTitleAndLeftImageButton(titleName,
                R.drawable.base_action_bar_back_bg_selector,
                new OnLeftButtonClickListener());
    }


    public void showOfflineDialog(final Context context) {
        DialogTips dialog = new DialogTips(this, "您的账号已在其他设备上登录!", "重新登录");


        // 设置成功事件
        dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int userId) {
                CustomApplication.getInstance().logout();
                startActivity(new Intent(context, LoginActivity.class));
                finish();
                dialogInterface.dismiss();
            }
        });
        dialog.show();
        dialog = null;
    }

    public void startAnimActivity(Class<?> cla) {
        this.startActivity(new Intent(this, cla));
    }

    public void startAnimActivity(Intent intent) {
        this.startActivity(intent);
    }

    public void updateUserInfos() {
        //更新地理位置信息
        updateUserLocation();
        //查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
        //这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
        userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
            @Override
            public void onError(int arg0, String arg1) {
                if (arg0 == BmobConfig.CODE_COMMON_NONE) {
                    ShowLog(arg1);
                } else {
                    ShowLog("查询好友列表失败：" + arg1);
                }
            }

            @Override
            public void onSuccess(List<BmobChatUser> arg0) {

                // 保存到application中方便比较
                ShowLog("查询好友列表成功");
                CustomApplication.getInstance().setContactList(CollectionUtils.list2map(arg0));
            }
        });
    }

    /**
     * 更新用户的经纬度信息
     */
    public void updateUserLocation() {
        if (CustomApplication.lastPoint != null) {
            String saveLatitude = mApplication.getLatitude();
            String saveLongtitude = mApplication.getLongtitude();
            String newLat = String.valueOf(CustomApplication.lastPoint.getLatitude());
            String newLong = String.valueOf(CustomApplication.lastPoint.getLongitude());
//			ShowLog("saveLatitude ="+saveLatitude+",saveLongtitude = "+saveLongtitude);
//			ShowLog("newLat ="+newLat+",newLong = "+newLong);
            if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {//只有位置有变化就更新当前位置，达到实时更新的目的
                final User user = (User) userManager.getCurrentUser(User.class);
                user.setLocation(CustomApplication.lastPoint);
                user.update(this, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        CustomApplication.getInstance().setLatitude(String.valueOf(user.getLocation().getLatitude()));
                        CustomApplication.getInstance().setLongtitude(String.valueOf(user.getLocation().getLongitude()));
//						ShowLog("经纬度更新成功");
                    }

                    @Override
                    public void onFailure(int code, String msg) {
//						ShowLog("经纬度更新 失败:"+msg);
                    }
                });
            } else {
//				ShowLog("用户位置未发生过变化");
            }
        }
    }

    public class OnLeftButtonClickListener implements
            onLeftImageButtonClickListener {

        @Override
        public void onClick() {
            finish();
        }
    }
}
