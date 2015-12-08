package com.zx.clean.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zx.clean.R;
import com.zx.clean.bean.RubbishItemBean;
import com.zx.clean.utils.StorageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxun on 2015/11/26.
 */
public class RubbishItemAdapter extends RecyclerView.Adapter<RubbishItemAdapter.ItemViewHolder> implements View.OnClickListener{

    private Context mContext;
    private List<RubbishItemBean> list = new ArrayList<>();
    private int size = 0;
    private boolean animationsLocked = false;
    private int lastAnimatedPosition = -1;
    private boolean delayEnterAnimation = true;

    public RubbishItemAdapter(Context mContext, List<RubbishItemBean> list) {
        this.mContext = mContext;
        this.list = list;
        if (list != null && list.size() > 0){
            this.size = list.size();
        }

    }

    @Override
    public RubbishItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_rv_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RubbishItemAdapter.ItemViewHolder holder, int position) {
        final RubbishItemBean itemBean = list.get(position);
        runEnterAnimation(holder.itemView, position);
        holder.uninstall.setVisibility(View.INVISIBLE);
        holder.app_icon.setImageDrawable(itemBean.getmIcon());
        holder.app_name.setText(itemBean.getmApplicationName());
        holder.app_use_size.setText(StorageUtil.convertStorage(itemBean.getmCacheSize()));
        holder.item_rl.setOnClickListener(this);
        holder.item_rl.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0){
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
    public void updateAdapter(List<RubbishItemBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_rl :
                int position = (int) v.getTag();
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + list.get(position).getmPackageName()));
                mContext.startActivity(intent);
                break;
        }
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.app_icon)
        ImageView app_icon;
        @InjectView(R.id.app_name)
        TextView app_name;
        @InjectView(R.id.app_use_size)
        TextView app_use_size;
        @InjectView(R.id.uninstall)
        TextView uninstall;
        @InjectView(R.id.item_rl)
        RelativeLayout item_rl;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
