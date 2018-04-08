package com.yuanshi.hiorange.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yuanshi.hiorange.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/4/8.
 */

public class BoxMissingDialog extends AppCompatActivity {


    @BindView(R.id.tv_dialog)
    TextView mTvDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.box_miss_dialog);
        ButterKnife.bind(this);
        mTvDialog.setText(getIntent().getStringExtra("text"));

    }
}
