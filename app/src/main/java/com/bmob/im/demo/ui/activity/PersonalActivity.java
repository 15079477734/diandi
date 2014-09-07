package com.bmob.im.demo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bmob.im.demo.CustomApplication;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.PersonCenterContentAdapter;
import com.bmob.im.demo.bean.DianDi;
import com.bmob.im.demo.bean.User;
import com.bmob.im.demo.config.Constant;
import com.bmob.im.demo.util.ActivityUtil;
import com.bmob.im.demo.util.LogUtils;
import com.bmob.im.demo.view.xlist.XListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;


public class PersonalActivity extends ActivityBase implements XListView.IXListViewListener, AdapterView.OnItemClickListener, View.OnClickListener {


    public static final int EDIT_USER = 1;
    private ImageView personalIcon;
    private TextView personalName;
    private TextView personalSign;
    private ImageView goSettings;
    private TextView personalTitle;
    private XListView mListView;
    private ArrayList<DianDi> mDianDis;
    private PersonCenterContentAdapter mAdapter;
    private User mUser;
    private int pageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findView();
        initView();
        loadData();
    }

    void findView() {
        setContentView(R.layout.fragment_personal);
        personalIcon = (ImageView) findViewById(R.id.personal_icon);
        personalName = (TextView) findViewById(R.id.personl_name);
        personalSign = (TextView) findViewById(R.id.personl_signature);
        goSettings = (ImageView) findViewById(R.id.go_settings);
        personalTitle = (TextView) findViewById(R.id.personl_title);

        mListView = (XListView) findViewById(R.id.pull_refresh_list_personal);
    }

    void initView() {
        initData();
        initTopBarForLeft("个人中心");
        initMyPublish();
        initXListView();
        bindEvent();
    }

    void initData() {
        mUser = CustomApplication.getInstance().getCurrentDianDi().getAuthor();
        updatePersonalInfo(mUser);
    }

    private void initXListView() {
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mAdapter = new PersonCenterContentAdapter(this, mDianDis);
        mListView.setAdapter(mAdapter);
    }

    private void initMyPublish() {
        if (isCurrentUser(mUser)) {
            personalTitle.setText("我发表过的");
            goSettings.setVisibility(View.INVISIBLE);
            User user = BmobUser.getCurrentUser(this, User.class);
            updatePersonalInfo(user);
        } else {
            goSettings.setVisibility(View.GONE);
            if (mUser != null && mUser.getSex().equals(Constant.SEX_FEMALE)) {
                personalTitle.setText("她发表过的");
            } else if (mUser != null && mUser.getSex().equals(Constant.SEX_MALE)) {
                personalTitle.setText("他发表过的");
            }
        }
        mDianDis = new ArrayList<DianDi>();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent();
        intent.setClass(PersonalActivity.this, CommentActivity.class);
        intent.putExtra("data", mDianDis.get(position - 1));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        String label = DateUtils.formatDateTime(this, System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        pageNum = 0;
        loadData();
    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    private void refreshLoad() {
        if (mListView.getPullLoading()) {
            mListView.stopLoadMore();
        }
    }

    private void refreshPull() {
        if (mListView.getPullRefreshing()) {
            mListView.stopRefresh();
        }
    }

    private void updatePersonalInfo(User user) {
        personalName.setText(user.getNick());
        personalSign.setText(user.getSignature());
        if (user.getAvatarImg() != null) {
            ImageLoader.getInstance()
                    .displayImage(user.getAvatarImg().getFileUrl(), personalIcon,
                            CustomApplication.getInstance().getOptions(R.drawable.content_image_default),
                            new SimpleImageLoadingListener() {

                                @Override
                                public void onLoadingComplete(String imageUri, View view,
                                                              Bitmap loadedImage) {
                                    // TODO Auto-generated method stub
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                    LogUtils.i(TAG, "load personal icon completed.");
                                }

                            }
                    );
        }
    }

    //判断点击条目的用户是否是当前登录用户

    private boolean isCurrentUser(User user) {
        if (null != user) {
            User cUser = BmobUser.getCurrentUser(mContext, User.class);
            if (cUser != null && cUser.getObjectId().equals(user.getObjectId())) {
                return true;
            }
        }
        return false;
    }

    protected void bindEvent() {
        // TODO Auto-generated method stub
        personalIcon.setOnClickListener(this);
        personalSign.setOnClickListener(this);
        personalTitle.setOnClickListener(this);
        goSettings.setOnClickListener(this);
    }

    protected void loadData() {
        getPublishion();
    }

    private void getPublishion() {
        BmobQuery<DianDi> query = new BmobQuery<DianDi>();
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        query.order("-createdAt");
        query.include("author");
        query.addWhereEqualTo("author", mUser);
        query.findObjects(this, new FindListener<DianDi>() {

            @Override
            public void onSuccess(List<DianDi> data) {
                // TODO Auto-generated method stub
                if (data.size() != 0 && data.get(data.size() - 1) != null) {
                    mDianDis.clear();

                    if (data.size() < Constant.NUMBERS_PER_PAGE) {
                        ShowToast("已加载完所有数据~");
                    }

                    mDianDis.addAll(data);
                    mAdapter.notifyDataSetChanged();
                } else {
                    ShowToast("暂无更多数据~");
                    pageNum--;
                }
                refreshPull();
            }

            @Override
            public void onError(int arg0, String msg) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "find failed." + msg);
                pageNum--;
                refreshPull();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.personal_icon:
                Intent intent = new Intent(mContext, SettingActivity.class);
                if (!isCurrentUser(mUser)) {
                    intent.putExtra("from", "add");
                    intent.putExtra("username", mUser.getUsername());
                } else {
                    intent.putExtra("from", "me");
                    intent.putExtra("username", mUser.getUsername());
                }
                startAnimActivity(intent);
                break;
            case R.id.personl_signature:
            case R.id.go_settings:
                if (isCurrentUser(mUser)) {
                    Intent intent2 = new Intent();
                    intent2.setClass(PersonalActivity.this, SettingActivity.class);
                    startActivityForResult(intent2, EDIT_USER);
                    LogUtils.i(TAG, "current user edit...");
                }
                break;
            case R.id.personl_title:

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case EDIT_USER:
                    getCurrentUserInfo();
                    pageNum = 0;
                    getPublishion();
                    break;

                default:
                    break;
            }
        }
    }

    //  查询当前用户具体信息

    private void getCurrentUserInfo() {
        User user = BmobUser.getCurrentUser(PersonalActivity.this, User.class);
        LogUtils.i(TAG, "sign:" + user.getSignature() + "sex:" + user.getSex());
        updatePersonalInfo(user);
        ActivityUtil.show(PersonalActivity.this, "更新信息成功。");
    }


}

