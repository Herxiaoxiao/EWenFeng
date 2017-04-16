package com.ewenfeng.ewenfeng;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.ewenfeng.ewenfeng.base.BaseMainFragment;
import com.ewenfeng.ewenfeng.event.TabSelectedEvent;
import com.ewenfeng.ewenfeng.m_interface.HideMainBottomBar;
import com.ewenfeng.ewenfeng.ui.fragment.first.ZhihuFirstFragment;
import com.ewenfeng.ewenfeng.ui.fragment.first.child.FirstHomeFragment;
import com.ewenfeng.ewenfeng.ui.fragment.fourth.ZhihuFourthFragment;
import com.ewenfeng.ewenfeng.ui.fragment.fourth.child.MeFragment;
import com.ewenfeng.ewenfeng.ui.fragment.second.ZhihuSecondFragment;
import com.ewenfeng.ewenfeng.ui.fragment.second.child.ViewPagerFragment;
import com.ewenfeng.ewenfeng.ui.fragment.third.ZhihuThirdFragment;
import com.ewenfeng.ewenfeng.ui.fragment.third.child.ShopFragment;
import com.ewenfeng.ewenfeng.ui.view.BottomBar;
import com.ewenfeng.ewenfeng.ui.view.BottomBarTab;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import me.yokeyword.fragmentation.helper.FragmentLifecycleCallbacks;
import org.greenrobot.eventbus.EventBus;
import android.view.Gravity;
import org.greenrobot.eventbus.Subscribe;


public class MainActivity extends SupportActivity implements BaseMainFragment.OnBackToFirstListener {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;

    private SupportFragment[] mFragments = new SupportFragment[4];
    private BottomBar mBottomBar;
    //侧滑栏
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView bottomBar_image;
    private FrameLayout fl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihu_activity_main);
//        EventBus.getDefault().register(this);

        if (savedInstanceState == null) {
            mFragments[FIRST] = ZhihuFirstFragment.newInstance();
            mFragments[SECOND] = ZhihuSecondFragment.newInstance();
            mFragments[THIRD] = ZhihuThirdFragment.newInstance();
            mFragments[FOURTH] = ZhihuFourthFragment.newInstance();

            loadMultipleRootFragment(R.id.fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用,也可以通过getSupportFragmentManager.getFragments()自行进行判断查找(效率更高些),用下面的方法查找更方便些
            mFragments[FIRST] = findFragment(ZhihuFirstFragment.class);
            mFragments[SECOND] = findFragment(ZhihuSecondFragment.class);
            mFragments[THIRD] = findFragment(ZhihuThirdFragment.class);
            mFragments[FOURTH] = findFragment(ZhihuFourthFragment.class);
        }

        initView();
        initSlidingMenu();


        // 可以监听该Activity下的所有Fragment的18个 生命周期方法
        registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacks() {

            @Override
            public void onFragmentSupportVisible(SupportFragment fragment) {
                Log.i("MainActivity", "onFragmentSupportVisible--->" + fragment.getClass().getSimpleName());
            }
        });
    }

    @Override
    protected FragmentAnimator onCreateFragmentAnimator() {
        return super.onCreateFragmentAnimator();
    }

    private void initView() {
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar_image = (ImageView) findViewById(R.id.bottomBar_image);
        fl = (FrameLayout)findViewById(R.id.fl_container);

        mBottomBar.addItem(new BottomBarTab(this, R.drawable.ic_home_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_discover_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_message_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_account_circle_white_24dp));

        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                SupportFragment currentFragment = mFragments[position];
                int count = currentFragment.getChildFragmentManager().getBackStackEntryCount();

                // 如果不在该类别Fragment的主页,则回到主页;
                if (count > 1) {
                    if (currentFragment instanceof ZhihuFirstFragment) {
                        currentFragment.popToChild(FirstHomeFragment.class, false);
                    } else if (currentFragment instanceof ZhihuSecondFragment) {
                        currentFragment.popToChild(ViewPagerFragment.class, false);
                    } else if (currentFragment instanceof ZhihuThirdFragment) {
                        currentFragment.popToChild(ShopFragment.class, false);
                    } else if (currentFragment instanceof ZhihuFourthFragment) {
                        currentFragment.popToChild(MeFragment.class, false);
                    }
                    return;
                }


                // 这里推荐使用EventBus来实现 -> 解耦
                if (count == 1) {
                    // 在FirstPagerFragment中接收, 因为是嵌套的孙子Fragment 所以用EventBus比较方便
                    // 主要为了交互: 重选tab 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                    EventBus.getDefault().post(new TabSelectedEvent(position));
                }
            }
        });
    }

    public void initSlidingMenu(){

        drawerLayout = (DrawerLayout)findViewById(R.id.slidingmenu);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        //获取头布局文件
        View headerView = navigationView.getHeaderView(0);

        //item 设置监听
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.collection_button:
                        //item.setChecked(true);
                        break;
                    case R.id.comment_button:
                        //item.setChecked(true);
                        break;
                    case R.id.message_button:
                        break;
                    case R.id.night:
                        break;
                    case R.id.nopicture:
                        break;
                    case R.id.outline:
                        break;
                    case R.id.setting:
                        break;
                }
                drawerLayout.closeDrawer(Gravity.LEFT);
                return false;
            }
        });
    }

    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

    @Override
    public void onBackToFirstFragment() {
        mBottomBar.setCurrentItem(0);
    }

    /**
     * 这里暂没实现,忽略
     */
    public void onHiddenBottombarEvent(boolean hidden) {
        if (hidden) {
            mBottomBar.hide();
            bottomBar_image.setVisibility(View.GONE);
            setBottomMargin(0);
        } else {
            bottomBar_image.setVisibility(View.VISIBLE);
            setBottomMargin(48);
            mBottomBar.show();
        }
    }
    public void setBottomMargin(int h){
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fl.getLayoutParams();
        Log.i("debug", "before/"+lp.bottomMargin+"/");
        lp.bottomMargin=dip2px(this,h);
        fl.setLayoutParams(lp);
        Log.i("debug", "after/"+lp.bottomMargin+"/");
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }

/*    @Override
    public void HideBottomBar() {
        mBottomBar.hide();
    }

    @Override
    public void ShowBottomBar() {
        mBottomBar.show();
    }*/
}
