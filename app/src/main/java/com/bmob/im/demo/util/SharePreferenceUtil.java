package com.bmob.im.demo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * 首选项管理
 *
 * @author smile
 * @ClassName: SharePreferenceUtil
 * @Description: TODO
 * @date 2014-6-10 下午4:20:14
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
    private static SharedPreferences.Editor editor;
    Context mContext;
    private SharedPreferences mSharedPreferences;
    private String SHARED_KEY_NOTIFY = "shared_key_notify";
    private String SHARED_KEY_VOICE = "shared_key_sound";
    private String SHARED_KEY_VIBRATE = "shared_key_vibrate";
    private String IS_UPDATE = "is_update";

    public SharePreferenceUtil(Context context, String name) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    // 是否允许推送通知
    public boolean isAllowPushNotify() {
        return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
    }

    public void setPushNotifyEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
        editor.commit();
    }

    public void checkUpdate() {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        editor.putBoolean(IS_UPDATE, true);
                        editor.commit();
                        break;
                    case UpdateStatus.No:
                        editor.putBoolean(IS_UPDATE, false);
                        editor.commit();
                        break;
                }
            }
        });
        UmengUpdateAgent.forceUpdate(mContext);


    }

    public boolean isUpdate() {
        return mSharedPreferences.getBoolean(IS_UPDATE, false);
    }

    // 允许声音
    public boolean isAllowVoice() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
    }

    public void setAllowVoiceEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_VOICE, isChecked);
        editor.commit();
    }

    // 允许震动
    public boolean isAllowVibrate() {
        return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
    }

    public void setAllowVibrateEnable(boolean isChecked) {
        editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
        editor.commit();
    }

}
