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
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ext.SatelliteMenu;
import android.view.ext.SatelliteMenu.SateliteClickedListener;
import android.view.ext.SatelliteMenuItem;
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
import com.bmob.im.demo.util.LogUtils;
import com.bmob.im.demo.view.HeaderLayout.onRightImageButtonClickListener;
import com.bmob.im.demo.view.xlist.XListView;

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
    private SatelliteMenu menu;
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
        menu = (SatelliteMenu) findViewById(R.id.menu);
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
        List<SatelliteMenuItem> items = new ArrayList<SatelliteMenuItem>();
        items.add(new SatelliteMenuItem(4, R.drawable.ic_1));
        items.add(new SatelliteMenuItem(4, R.drawable.ic_3));
        items.add(new SatelliteMenuItem(4, R.drawable.ic_4));
        items.add(new SatelliteMenuItem(3, R.drawable.ic_5));
        items.add(new SatelliteMenuItem(2, R.drawable.ic_6));
        items.add(new SatelliteMenuItem(1, R.drawable.ic_2));

        menu.addItems(items);
        bindEvent();
    }

    private void initMenu() {


     /*   WindowManager wm = getActivity().getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();*/
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

        menu.setOnItemClickedListener(new SateliteClickedListener() {

            public void eventOccured(int id) {
                ShowToast("Clicked on " + id);
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
                    ShowToast("暂无更多数据~");
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

