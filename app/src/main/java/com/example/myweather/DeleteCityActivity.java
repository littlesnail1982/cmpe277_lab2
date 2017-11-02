package com.example.myweather;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class DeleteCityActivity extends AppCompatActivity {
    private ListView lv;
    private MyAdapter mAdapter;
    private ArrayList<String> list;
    private Button bt_selectall;
    private Button bt_deselectall;
    private int checkNum;
    private TextView tv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_city);

        lv = (ListView) findViewById(R.id.city_delete_list);
        bt_selectall = (Button) findViewById(R.id.bt_select_all);
        bt_deselectall = (Button) findViewById(R.id.bt_cancle_select);
        tv_show = (TextView) findViewById(R.id.tv);
        list = new ArrayList<String>();
        // prepare city list for Adapter
        initData();

        mAdapter = new MyAdapter(list, this);
        // bind Adapter
        lv.setAdapter(mAdapter);

        bt_selectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 遍历list的长度，将MyAdapter中的map值全部设为true
                for (int i = 0; i < list.size(); i++) {
                    MyAdapter.getIsSelected().put(i, true);
                }
                // 数量设为list的长度
                checkNum = list.size();
                // 刷新listview和TextView的显示
                dataChanged();
            }
        });

        // 取消按钮的回调接口
        bt_deselectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 遍历list的长度，将已选的按钮设为未选
                for (int i = 0; i < list.size(); i++) {
                    if (MyAdapter.getIsSelected().get(i)) {
                        MyAdapter.getIsSelected().put(i, false);
                        checkNum--;// 数量减1
                    }
                }
                // 刷新listview和TextView的显示
                dataChanged();
            }
        });

        // 绑定listView的监听器
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder) arg1.getTag();
                // 改变CheckBox的状态
                holder.cb.toggle();
                // 将CheckBox的选中状况记录下来
                MyAdapter.getIsSelected().put(arg2, holder.cb.isChecked());
                // 调整选定条目
                if (holder.cb.isChecked() == true) {
                    checkNum++;
                } else {
                    checkNum--;
                }
                // 用TextView显示
                tv_show.setText("Has chosen" + checkNum + "items");
            }
        });
    }

    private void initData() {
        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> cities = sp.getStringSet("cities", null);
        if(cities != null){
            list = new ArrayList<>(cities);
        }
    }
    // 刷新listview和TextView的显示
    private void dataChanged() {
        // 通知listView刷新
        mAdapter.notifyDataSetChanged();
        // TextView显示最新的选中数目
        tv_show.setText("Has chosen" + checkNum + "items");
    };

}
