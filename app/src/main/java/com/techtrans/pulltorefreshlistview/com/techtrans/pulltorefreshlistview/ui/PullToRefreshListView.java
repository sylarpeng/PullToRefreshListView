package com.techtrans.pulltorefreshlistview.com.techtrans.pulltorefreshlistview.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techtrans.pulltorefreshlistview.R;


/**
 * Created by Administrator on 2015/8/9.
 */
public class PullToRefreshListView extends ListView {
    private static final String TAG = "PullToRefreshListView";
    private View headerView;
    private TextView mTvDesc;


    private int headerHeight;//下拉头部的高度
    private int paddingTop; //下拉头部离屏幕顶部的距离

    private static final  int STATUS_0=0;//隐藏
    private static final  int STATUS_1=1;//显示
    private static final  int STATUS_2=2;//其他
    private int status=STATUS_0;//记录listview头部下拉的状态(完全显示，完全隐藏,其他)
    private setOnPullToRefreshListener mListener;

    private ProgressBar mPb;
    private ImageView mIcon;

    private android.os.Handler mHander=new Handler();

    public PullToRefreshListView(Context context) {
        this(context, null);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化view
        initView(context);
    }



    //需要先测量
    private void initView(Context context) {
        headerView=View.inflate(context, R.layout.item_header, null);
        mTvDesc= (TextView) headerView.findViewById(R.id.header_desc);
        mPb= (ProgressBar) headerView.findViewById(R.id.head_progress);
        mIcon= (ImageView) headerView.findViewById(R.id.header_icon);
        //当根布局为relativelayout时,需要添加上setLayoutParams，否则调用measure时会报错，（此bug出现在4.4以下版本）
        headerView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        headerView.measure(0, 0);
        Log.d(TAG, "width===" + headerView.getMeasuredWidth());
        addHeaderView(headerView);

        this.headerHeight=headerView.getMeasuredHeight();
        this.paddingTop=-this.headerHeight;

        //初始化时隐藏头部
        headerView.setPadding(0,this.paddingTop,0,0);
    }


    private int startY;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int position=getFirstVisiblePosition();
        if(position==0){
            switch (ev.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startY= (int) ev.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    int newY= (int) ev.getRawY();
                    int dy=newY-startY;
                    this.paddingTop+=dy;
                    Log.d(TAG,"paddingTop==="+this.paddingTop);
                    startY=newY;

                    //bug？不会慢慢滑动出来，只有当整个头部的距离时才突然显示（没有渐进过程） 后发现是因为xml 布局为relativelayout的原因
                    headerView.setPadding(0,this.paddingTop,0,0);

                    if(this.paddingTop>=0){
                        //完全显示
                        this.status=STATUS_1;

                        mTvDesc.setText("松手刷新");
                    }else if(this.paddingTop<=-this.headerHeight){
                        //完全隐藏
                        this.status=STATUS_0;
                        mTvDesc.setText("下拉刷新");
                    }else{
                        //其他
                        this.status=STATUS_2;
                        mTvDesc.setText("下拉刷新");
                    }

                    break;

                case MotionEvent.ACTION_UP:
                    if(this.status==STATUS_1){
                        //重置
                        this.paddingTop=0;
                        headerView.setPadding(0,this.paddingTop,0,0);

                        //TODO 刷新操作
                        if(mListener!=null){
                            mTvDesc.setText("玩命加载中...");
                            mPb.setVisibility(View.VISIBLE);
                            mIcon.setVisibility(View.GONE);
                            mListener.onPreExcute();

                            new Thread(){
                                @Override
                                public void run() {
                                    mListener.doInBackGround();

                                    mHander.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            mListener.onPostExcute();

                                            //隐藏头部
                                            paddingTop=-headerHeight;
                                            headerView.setPadding(0, paddingTop, 0, 0);
                                            mPb.setVisibility(View.GONE);
                                            mIcon.setVisibility(View.VISIBLE);

                                        }
                                    });


                                }
                            }.start();

                        }
                    }else{

                        //隐藏头部
                        this.paddingTop=-this.headerHeight;
                        headerView.setPadding(0,this.paddingTop,0,0);
                        mPb.setVisibility(View.GONE);
                        mIcon.setVisibility(View.VISIBLE);
                    }

                    break;

            }
        }

        return super.onTouchEvent(ev);
    }


    public  interface setOnPullToRefreshListener{

        public void onPreExcute();
        //运行在子线程
        public void doInBackGround();
        //
        public void onPostExcute();
    }

    public void setOnPullToRefreshListener(setOnPullToRefreshListener listener){
        this.mListener=listener;
    }

}
