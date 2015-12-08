package com.zx.clean.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zx.clean.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxun on 2015/11/30.
 */
public class MemoryInfoView extends LinearLayout{

    @InjectView(R.id.advice)
    TextView advice;
    @InjectView(R.id.scan_completed)
    TextView scan_completed;
    @InjectView(R.id.action)
    TextView action;

    public MemoryInfoView(Context context) {
        super(context);
        ButterKnife.inject(context, MemoryInfoView.this);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout mainV = (LinearLayout) inflater.inflate(R.layout.view_memory_info, this);
    }

    public void setTextValues(String action_tv, String scan_completed_tv, boolean show){
        action.setText(action_tv);
        scan_completed.setText(scan_completed_tv);
        if (show){
            action.setVisibility(VISIBLE);
        }else {
            action.setVisibility(GONE);
        }
    }

    public void setScanText(String scan_completed_tv){
        scan_completed.setText(scan_completed_tv);
    }

    public String getScanText(){
        return scan_completed.getText().toString();
    }


}
