package com.zx.clean.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.zx.clean.R;
import com.zx.clean.bean.AppInfo;
import com.zx.clean.utils.StorageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxun on 2015/11/24.
 */
public class AppManagerItemAdapter extends RecyclerView.Adapter<AppManagerItemAdapter.AppManagerItemViewHolder> {

    private Context mContext;
    private ArrayList<AppInfo> list = new ArrayList<>();
    private List<Integer> checkedPositionList;//选中的位置
    private List<AppInfo> checkedData = new ArrayList<>();//选中的数据

    private boolean animationsLocked = false;
    private int lastAnimatedPosition = -1;
    private boolean delayEnterAnimation = true;

    public AppManagerItemAdapter(Context mContext, ArrayList<AppInfo> list) {
        this.mContext = mContext;
        this.list = list;
        checkedPositionList = new ArrayList<>();
    }

    @Override
    public AppManagerItemAdapter.AppManagerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_rv_item, parent, false);
        return new AppManagerItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppManagerItemAdapter.AppManagerItemViewHolder holder, int position) {
        final AppInfo appInfo = list.get(position);
        runEnterAnimation(holder.itemView, position);
        holder.app_icon.setImageDrawable(appInfo.getAppIcon());
        holder.app_name.setText(appInfo.getAppName());
        holder.app_use_size.setText("占用" + StorageUtil.convertStorage(appInfo.getPkgSize()));
        holder.uninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setAction("android.intent.action.DELETE");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("package:" + appInfo.getPackName()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }
    private void runEnterAnimation(View view, int position) {
        if (animationsLocked){
            return;
        }

        if (position > lastAnimatedPosition){
            lastAnimatedPosition = position;
            view.setTranslationY(140);
            view.setAlpha(0.f);
            view.animate().translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? list.size()*(position) : 0)
                    .setInterpolator(new DecelerateInterpolator(2.f))
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            animationsLocked = true;
                        }
                    }).start();
        }
    }
    public class AppManagerItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.app_icon)
        ImageView app_icon;
        @InjectView(R.id.app_name)
        TextView app_name;
        @InjectView(R.id.app_use_size)
        TextView app_use_size;
        @InjectView(R.id.uninstall)
        TextView uninstall;

        public AppManagerItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}