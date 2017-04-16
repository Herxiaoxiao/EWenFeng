package com.ewenfeng.ewenfeng.ui.fragment.search;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.ewenfeng.ewenfeng.R;
import com.ewenfeng.ewenfeng.base.BaseFragment;
import com.ewenfeng.ewenfeng.base.BaseMainFragment;

import java.util.Date;

public class Search extends BaseFragment {

    private EditText et_search;
    private ImageView searchBack;
    private TextView tv_tip;
    private MyListView listView;
    private TextView tv_clear;
    private RecordSQLiteOpenHelper helper;
    private SQLiteDatabase db;
    private BaseAdapter adapter;
    private View view;

    public static Search newInstance() {
        Bundle args = new Bundle();

        Search fragment = new Search();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_detail, container, false);
        helper = new RecordSQLiteOpenHelper(_mActivity);


        initView();

        // 清空搜索历史
        tv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
                queryData("");
            }
        });

        // 搜索框的键盘搜索键点击回调
        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {// 修改回车键功能
                    // 先隐藏键盘
                    /*((InputMethodManager) _mActivity.getSystemService()).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);*/

                    /*View mv = getActivity().getWindow().peekDecorView();
                    if (mv != null){
                        InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(
                                _mActivity.INPUT_METHOD_SERVICE);
                        inputmanger.hideSoftInputFromWindow(mv.getWindowToken(), 0);
                    }*/

                    InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( _mActivity.INPUT_METHOD_SERVICE );
                    if ( imm.isActive( ) ) {
                        imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );

                    }

                    // 按完搜索键后将当前查询的关键字保存起来,如果该关键字已经存在就不执行保存
                    boolean hasData = hasData(et_search.getText().toString().trim());
                    if (!hasData) {
                        insertData(et_search.getText().toString().trim());
                        queryData("");
                    }
                    et_search.setText("");
                    //  根据输入的内容模糊查询商品，并跳转到另一个界面，由你自己去实现
                    Toast.makeText(_mActivity, "clicked!", Toast.LENGTH_SHORT).show();

                }
                return false;
            }
        });

        // 搜索框的文本变化实时监听
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    tv_tip.setText("搜索历史");
                } else {
                    tv_tip.setText("搜索结果");
                }
                String tempName = et_search.getText().toString();
                // 根据tempName去模糊查询数据库中有没有数据
                queryData(tempName);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                String name = textView.getText().toString();
                et_search.setText(name);
                Toast.makeText(_mActivity, name, Toast.LENGTH_SHORT).show();
                //  获取到item上面的文字，根据该关键字跳转到另一个页面查询，由你自己去实现
            }
        });

        // 插入数据，便于测试，否则第一次进入没有数据怎么测试呀？
        Date date = new Date();
        long time = date.getTime();
        insertData("Leo" + time);

        //select * from (select * from tblmessage order by sortfield ASC limit 10) order by sortfield DESC;

        // 第一次进入查询所有的历史记录
        queryData("");
        return view;
    }

    /**
     * 插入数据
     */
    private void insertData(String tempName) {
        db = helper.getWritableDatabase();
        db.execSQL("insert into records(name) values('" + tempName + "')");
        db.close();
    }

    /**
     * 模糊查询数据 select * from (select * from tblmessage order by sortfield ASC limit 10) order by sortfield DESC;
     */
    private void queryData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name like '%" + tempName + "%' order by id desc limit 10", null);
        // 创建adapter适配器对象
        adapter = new SimpleCursorAdapter(_mActivity, android.R.layout.simple_list_item_1, cursor, new String[] { "name" },
                new int[] { android.R.id.text1 }, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        // 设置适配器
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    /**
     * 检查数据库中是否已经有该条记录
     */
    private boolean hasData(String tempName) {
        Cursor cursor = helper.getReadableDatabase().rawQuery(
                "select id as _id,name from records where name =?", new String[]{tempName});
        //判断是否有下一个
        return cursor.moveToNext();
    }

    /**
     * 清空数据
     */
    private void deleteData() {
        db = helper.getWritableDatabase();
        db.execSQL("delete from records");
        db.close();
    }

    private void initView() {
        et_search = (EditText) view.findViewById(R.id.et_search);
        tv_tip = (TextView) view.findViewById(R.id.tv_tip);
        listView = (MyListView) view.findViewById(R.id.listView);
        tv_clear = (TextView) view.findViewById(R.id.tv_clear);
        searchBack = (ImageView)view.findViewById(R.id.search_back);

        searchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( _mActivity.INPUT_METHOD_SERVICE );
                if ( imm.isActive( ) ) {
                    imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );

                }
                _mActivity.onBackPressed();
            }
        });

        // 调整EditText左边的搜索按钮的大小
        Drawable drawable = getResources().getDrawable(R.mipmap.search_detail);
        drawable.setBounds(0, 0, 60, 60);// 第一0是距左边距离，第二0是距上边距离，60分别是长宽
        et_search.setCompoundDrawables(drawable, null, null, null);// 只放左边
    }

}
