package com.ewenfeng.ewenfeng.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ewenfeng.ewenfeng.R;
import com.ewenfeng.ewenfeng.entity.Article;
import com.ewenfeng.ewenfeng.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;


public class HomeAdapterForSearch extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<Article> mItems = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;

    private OnItemClickListener mClickListener;

    private static final int TYPE_SEARCH = 0;//带搜索框的布局
    private static final int TYPE_INFO = 1;

    public HomeAdapterForSearch(Context context) {
        mContext=context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<Article> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder = getViewHolderByViewType(viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position > 0){
            Article item = mItems.get(position-1);
            ((ViewHolder_Info)holder).tvTitle.setText(item.getTitle());
            ((ViewHolder_Info)holder).tvContent.setText(item.getContent());
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TYPE_SEARCH;
        }else {
            return TYPE_INFO;
        }
        //return 0;
    }

    @Override
    public int getItemCount() {
        return mItems.size() + 1;
    }

    public Article getItem(int position) {
        return mItems.get(position);
    }


    private RecyclerView.ViewHolder getViewHolderByViewType(int viewType) {

        View view_info = View.inflate(mContext, R.layout.item_home, null);
        View view_seach = View.inflate(mContext, R.layout.seach_item, null);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_INFO:
                holder = new ViewHolder_Info(view_info);
                break;
            case TYPE_SEARCH:
                holder = new ViewHolder_Search(view_seach);
                break;
        }
        return holder;
    }

    class ViewHolder_Info extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvContent;

        public ViewHolder_Info(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    class ViewHolder_Search extends RecyclerView.ViewHolder {
        public ViewHolder_Search(View itemView) {
            super(itemView);
        }
    }
}
