package com.ewenfeng.ewenfeng.base;

import android.support.v7.widget.Toolbar;
import android.view.View;
import com.ewenfeng.ewenfeng.R;


public class BaseBackFragment extends BaseFragment {

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mActivity.onBackPressed();
            }
        });
    }
}
