package com.bmob.im.demo.ui.fragment;

/**
 * ************************************************************
 * *********    User : SuLinger(462679107@qq.com) .
 * *********    Date : 2014-08-29  .
 * *********    Time:  2014-08-29  .
 * *********    Project name :BmobChatDemo .
 * *********    Copyright @ 2014, SuLinger, All Rights Reserved
 * *************************************************************
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.bmob.im.demo.CustomApplication;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.AIContentAdapter;
import com.bmob.im.demo.bean.DianDi;
import com.bmob.im.demo.config.Constant;
import com.bmob.im.demo.db.DatabaseUtil;
import com.bmob.im.demo.ui.activity.CommentActivity;
import com.bmob.im.demo.ui.activity.NewDiandiActivity;
import com.bmob.im.demo.util.ActivityUtil;
import com.bmob.im.demo.util.LogUtils;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.xlist.XListView;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuItem;
import com.touchmenotapps.widget.radialmenu.menu.v1.RadialMenuWidget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;


public class DianDiFragment extends BaseFragment implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private ArrayList<DianDi> mListItems;
    private AIContentAdapter mAdapter;

    private XListView mListView;
    private TextView networkTips;
    private RadialMenuWidget mPieMenu;
    private RadialMenuItem mNormalMenu, mCloseMenu, mExpandMenu;
    private RadialMenuItem mMainMenu, mLoveItem, mEditItem;
    private ArrayList<RadialMenuItem> children = new ArrayList<RadialMenuItem>(0);
    private View mView;

    private int pageNum;
    private String lastItemTime;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return mView = inflater.inflate(R.layout.fragment_diandi, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findView();
        initView();
        loadData();
    }

    public void onResume() {
        super.onResume();
        onRefresh();
    }

    void findView() {
        mListView = (XListView) findViewById(R.id.fragment_diandi_list);
        networkTips = (TextView) findViewById(R.id.networkTips);
    }

    public void initView() {
        initData();
        initMenu();
        initTopBarForRight("点滴", R.drawable.ic_action_edit_selector, new onRightImageButtonClickListener() {
            @Override
            public void onClick() {
                startAnimActivity(NewDiandiActivity.class);
            }
        });
        initXListView();
        bindEvent();
    }

    private void initMenu() {
        mPieMenu = new RadialMenuWidget(getActivity());
        mCloseMenu = new RadialMenuItem("关闭", null);
        mCloseMenu.setDisplayIcon(android.R.drawable.ic_menu_close_clear_cancel);

        mNormalMenu = new RadialMenuItem("脚印", "脚印");
        mExpandMenu = new RadialMenuItem("更多", "更多");
        mMainMenu = new RadialMenuItem("主页", "主页");
        mLoveItem = new RadialMenuItem("收藏", "收藏");
        mEditItem = new RadialMenuItem("纪念", "纪念");

        children.add(mMainMenu);
        children.add(mLoveItem);
        children.add(mEditItem);
        mExpandMenu.setMenuChildren(children);
     /*   WindowManager wm = getActivity().getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();*/
        mPieMenu.setAnimationSpeed(0L);
        mPieMenu.setSourceLocation(0, 0);
        //     mPieMenu.setCenterLocation(width,height);
        mPieMenu.setIconSize(15, 30);
        mPieMenu.setTextSize(13);
        mPieMenu.setOutlineColor(Color.BLACK, 225);
        mPieMenu.setInnerRingColor(0xAA66CC, 999);
        mPieMenu.setOuterRingColor(0x0099CC, 999);
        //pieMenu.setHeader("Test Menu", 20);
        mPieMenu.setCenterCircle(mCloseMenu);
        mPieMenu.addMenuEntry(mNormalMenu);
        mPieMenu.addMenuEntry(mExpandMenu);
    }


    void initData() {
        mListItems = new ArrayList<DianDi>();
        pageNum = 0;
    }

    private void initXListView() {
        mListView.setOnItemClickListener(this);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(true);
        mListView.setXListViewListener(this);
        mListView.pullRefreshing();
        mAdapter = new AIContentAdapter(getActivity(), mListItems);
        mListView.setAdapter(mAdapter);
    }

    void bindEvent() {
        mNormalMenu.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                mPieMenu.dismiss();
                startAnimActivity(NewDiandiActivity.class);
            }
        });
        mEditItem.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                startAnimActivity(NewDiandiActivity.class);
                mPieMenu.dismiss();
            }
        });
        mCloseMenu.setOnMenuItemPressed(new RadialMenuItem.RadialMenuItemClickListener() {
            @Override
            public void execute() {
                mPieMenu.dismiss();
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), CommentActivity.class);
        intent.putExtra("data", mListItems.get(position - 1));
        startActivity(intent);
    }

    public void loadData() {
        BmobQuery<DianDi> query = new BmobQuery<DianDi>();
        query.order("-createdAt");
        query.setLimit(Constant.NUMBERS_PER_PAGE);
        BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
        query.addWhereLessThan("createdAt", date);
        LogUtils.i(TAG, "SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.setSkip(Constant.NUMBERS_PER_PAGE * (pageNum++));
        LogUtils.i(TAG, "SIZE:" + Constant.NUMBERS_PER_PAGE * pageNum);
        query.include("author");
        query.findObjects(getActivity(), new FindListener<DianDi>() {

            @Override
            public void onSuccess(List<DianDi> list) {
                LogUtils.i(TAG, "find success." + list.size());
                if (list.size() != 0 && list.get(list.size() - 1) != null) {
                    mListItems.clear();
                    if (list.size() < Constant.NUMBERS_PER_PAGE) {
                        LogUtils.i(TAG, "已加载完所有数据~");
                    }
                    if (CustomApplication.getInstance().getCurrentUser() != null) {
                        list = DatabaseUtil.getInstance(getActivity()).setFav(list);
                    }
                    mListItems.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    networkTips.setVisibility(View.INVISIBLE);

                } else {
                    ActivityUtil.show(getActivity(), "暂无更多数据~");
                    pageNum--;
                }
                refreshPull();
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                LogUtils.i(TAG, "find failed." + arg1);
                pageNum--;
                refreshPull();
            }

        });
    }

    @Override
    public void onRefresh() {
        String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
        pageNum = 0;
        lastItemTime = getCurrentTime();
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
            networkTips.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoadMore() {
        loadData();
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = formatter.format(new Date(System.currentTimeMillis()));
        return times;
    }
}

