package com.techtrans.pulltorefreshlistview;

import android.app.Activity;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techtrans.pulltorefreshlistview.com.techtrans.pulltorefreshlistview.ui.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends Activity {

    private PullToRefreshListView mLv;
    private List<String> mDatas;
    private Myadapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLv= (PullToRefreshListView) findViewById(R.id.lv);

        mDatas=new ArrayList<String>();
        for(int i=0;i<30;i++){
            mDatas.add("俺是新闻条目:"+i);
        }

        mAdapter=new Myadapter();
        mLv.setAdapter(mAdapter);

        mLv.setOnPullToRefreshListener(new PullToRefreshListView.setOnPullToRefreshListener() {
            @Override
            public void onPreExcute() {

            }

            @Override
            public void doInBackGround() {
                SystemClock.sleep(2000);
                mDatas.add(0,"俺是新闻条目:"+mDatas.size());
            }

            @Override
            public void onPostExcute() {
                mAdapter.notifyDataSetChanged();
            }
        });



    }

    private class Myadapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv=null;
            if(convertView==null)
                tv=new TextView(getApplicationContext());
            else
                tv= (TextView) convertView;
            tv.setText(mDatas.get(position));
            tv.setTextSize(20);
            tv.setTextColor(Color.BLACK);
            return tv;
        }
    }

}
