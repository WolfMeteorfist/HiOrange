package com.yuanshi.hiorange.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yuanshi.hiorange.BaseActivity;
import com.yuanshi.hiorange.R;
import com.yuanshi.hiorange.bean.BoxMissingInfo;
import com.yuanshi.hiorange.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

/**
 * @author Administrator
 * @date 2018/4/9
 */

public class AlarmActivity extends BaseActivity {

    private static final String TAG = "AlarmActivity";
    @BindView(R.id.recycler_alarm)
    RecyclerView mRecyclerAlarm;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_alarm_title_list)
    TextView mTvAlarmTitleList;
    private List<String> mListBoxMiss;
    private List<String> mListBoxOpen;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        ButterKnife.bind(this);
        getWindow().setStatusBarColor(getResources().getColor(R.color.statue_bar));
        mTvTitle.setText(R.string.AlarmInfo);
        mContext = this;
        if (initDatas()) {
            mRecyclerAlarm.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mRecyclerAlarm.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
            ScaleInAnimationAdapter adapter = new ScaleInAnimationAdapter(new MyAdapter());
            adapter.setInterpolator(new OvershootInterpolator());
            adapter.setFirstOnly(false);
            mRecyclerAlarm.setAdapter(adapter);
        }

        mRecyclerAlarm.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View stickyInfoView = recyclerView.getChildAt(0);
                if (stickyInfoView != null && stickyInfoView.getContentDescription() != null) {
                    mTvAlarmTitleList.setText(String.valueOf(stickyInfoView.getContentDescription()));
                }
                View transInfoView = recyclerView.findChildViewUnder(mTvAlarmTitleList.getMeasuredWidth() / 2, mTvAlarmTitleList.getMeasuredHeight() + 1);
                if (transInfoView != null && transInfoView.getTag() != null) {
                    int tag = (int) transInfoView.getTag();
                    int deltaY = transInfoView.getTop() - mTvAlarmTitleList.getMeasuredHeight();
                    if (tag == MyAdapter.HAS_STICKY_VIEW) {
                        if (transInfoView.getTop() > 0) {
                            mTvAlarmTitleList.setTranslationY(deltaY);
                        } else {
                            mTvAlarmTitleList.setTranslationY(0);
                        }
                    } else {
                        mTvAlarmTitleList.setScaleX(1);
                        mTvAlarmTitleList.setTranslationY(0);
                    }
                }
            }
        });

    }

    @OnClick({R.id.back})
    public void onClick(View view) {
        finish();
    }

    private boolean initDatas() {

        List<String>[] lists = loadTime();
        if (lists != null) {
            mListBoxMiss = lists[0];
            mListBoxOpen = lists[1];
            return true;
        }
        return false;
//        mListBoxMiss = Arrays.asList("2018", "2018", "2017", "2016", "2015", "2017", "2016", "2015", "2017", "2016", "2015", "2017", "2016", "2015");
//        mListBoxOpen = Arrays.asList("10:20", "10:20", "10:20", "10:20", "10:20", "10:20", "10:20", "10:20", "10:20", "10:20", "10:20", "10:20", "10:20", "10:20");
//        return true;
    }

    private List[] loadTime() {

        String fileName = "boxInfo.txt";
        StringBuilder sb = new StringBuilder();
        File file = new File(FileUtils.getRootPath(), fileName);

        if (!file.exists()) {
            return null;
        }

        try {
            String temp;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }
            reader.close();

            BoxMissingInfo boxMissingInfo = new Gson().fromJson(sb.toString(), BoxMissingInfo.class);
            return new List[]{boxMissingInfo.getMissTime(), boxMissingInfo.getOpenTime()};

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {

        private final int TYPE_OPEN = 1;
        private final int TYPE_MISS = 2;
        int openTimeSize = mListBoxOpen == null ? 0 : mListBoxOpen.size();
        int missTimeSize = mListBoxMiss == null ? 0 : mListBoxMiss.size();//14
        public static final int FIRST_STICKY_VIEW = 1;
        public static final int HAS_STICKY_VIEW = 2;
        public static final int NO_STICKY_VIEW = 3;

        @Override
        public int getItemViewType(int position) {
            //箱子开启时间在前
            if (position < openTimeSize) {
                return TYPE_OPEN;
            }

            return TYPE_MISS;

        }

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.alarm_list_item, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
            if (getItemViewType(position) == TYPE_OPEN) {
                holder.mTvTitle.setText("箱子开箱时间");
                holder.mTvTime.setText(mListBoxOpen.get(position));
                holder.itemView.setContentDescription("箱子开箱时间");
            } else if (getItemViewType(position) == TYPE_MISS) {
                holder.mTvTitle.setText("箱子丢失时间");
                holder.mTvTime.setText(mListBoxMiss.get(position - openTimeSize));
                holder.itemView.setContentDescription("箱子丢失时间");
            }

            if (position == 0) {
                //首个要展示粘性头部用列表的
                holder.mTvTitle.setVisibility(View.VISIBLE);
                holder.itemView.setTag(FIRST_STICKY_VIEW);
            } else {
                if (position == openTimeSize) {
                    holder.mTvTitle.setVisibility(View.VISIBLE);
                    holder.itemView.setTag(HAS_STICKY_VIEW);
                } else {
                    holder.mTvTitle.setVisibility(View.GONE);
                    holder.itemView.setTag(NO_STICKY_VIEW);
                }
            }


        }

        @Override
        public int getItemCount() {
            return openTimeSize + missTimeSize;
        }


        class MyHolder extends RecyclerView.ViewHolder {

            TextView mTvTime;
            TextView mTvTitle;

            public MyHolder(View itemView) {
                super(itemView);
                mTvTime = itemView.findViewById(R.id.tv_alarm_time_list_item);
                mTvTitle = itemView.findViewById(R.id.tv_alarm_title_list_item);

            }
        }
    }

}
