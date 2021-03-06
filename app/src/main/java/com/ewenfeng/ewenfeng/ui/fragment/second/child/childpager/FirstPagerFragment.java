package com.ewenfeng.ewenfeng.ui.fragment.second.child.childpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.ewenfeng.ewenfeng.MainActivity;
import com.ewenfeng.ewenfeng.R;
import com.ewenfeng.ewenfeng.adapter.HomeAdapter;
import com.ewenfeng.ewenfeng.adapter.HomeAdapterForSearch;
import com.ewenfeng.ewenfeng.base.BaseFragment;
import com.ewenfeng.ewenfeng.entity.Article;
import com.ewenfeng.ewenfeng.event.TabSelectedEvent;
import com.ewenfeng.ewenfeng.listener.OnItemClickListener;
import com.ewenfeng.ewenfeng.listener.OnItemTouchListener;
import com.ewenfeng.ewenfeng.ui.fragment.search.Search;
import com.ewenfeng.ewenfeng.ui.fragment.second.child.DetailFragment;
import me.yokeyword.fragmentation.SupportFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class FirstPagerFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecy;
    private SwipeRefreshLayout mRefreshLayout;

    private HomeAdapterForSearch mAdapter;

    private boolean mAtTop = true;

    private int mScrollTotal;

    private String[] mTitles = new String[]{
            "航拍“摩托大军”返乡高峰 如蚂蚁搬家（组图）",
            "苹果因漏电召回部分电源插头",
            "IS宣称对叙利亚爆炸案负责"
    };

    private String[] mContents = new String[]{
            "1月30日，距离春节还有不到十天，“摩托大军”返乡高峰到来。航拍广西梧州市东出口服务站附近的骑行返乡人员，如同蚂蚁搬家一般。",
            "昨天记者了解到，苹果公司在其官网发出交流电源插头转换器更换计划，召回部分可能存在漏电风险的电源插头。",
            "极端组织“伊斯兰国”31日在社交媒体上宣称，该组织制造了当天在叙利亚首都大马士革发生的连环爆炸案。"
    };

    public static FirstPagerFragment newInstance() {

        Bundle args = new Bundle();

        FirstPagerFragment fragment = new FirstPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zhihu_fragment_second_pager_first, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecy = (RecyclerView) view.findViewById(R.id.recy);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);

        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter = new HomeAdapterForSearch(_mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
        mRecy.setLayoutManager(manager);
        mRecy.setAdapter(mAdapter);
        mRecy.addOnItemTouchListener(new OnItemTouchListener(mRecy){
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                Log.i("bug_msg", "onItemClick: /"+vh.getAdapterPosition()+"/");
                //Toast.makeText(_mActivity, "onItemClick: /"+vh.getAdapterPosition()+"/", Toast.LENGTH_SHORT).show();
                if(vh.getAdapterPosition()==0){//搜索
                    ((SupportFragment) getParentFragment()).start(Search.newInstance());
                }else {
                    MainActivity main = (MainActivity) getActivity();
                    main.onHiddenBottombarEvent(true);
                    ((SupportFragment) getParentFragment()).start(DetailFragment.newInstance(mAdapter.getItem(vh.getAdapterPosition()-1).getTitle()));
                }

            }
        });

        // Init Datas
        List<Article> articleList = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            int index = (int) (Math.random() * 3);
            Article article = new Article(mTitles[index], mContents[index]);
            articleList.add(article);
        }
        mAdapter.setDatas(articleList);

        mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrollTotal += dy;
                if (mScrollTotal <= 0) {
                    mAtTop = true;
                } else {
                    mAtTop = false;
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private void scrollToTop() {
        mRecy.smoothScrollToPosition(0);
    }

    /**
     * 选择tab事件
     */
    @Subscribe
    public void onTabSelectedEvent(TabSelectedEvent event) {
        if (event.position != MainActivity.SECOND) return;

        if (mAtTop) {
            mRefreshLayout.setRefreshing(true);
            onRefresh();
        } else {
            scrollToTop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecy.setAdapter(null);
        EventBus.getDefault().unregister(this);
    }

}
