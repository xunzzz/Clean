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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zx.clean.R;
import com.zx.clean.bean.AppProcessInfo;
import com.zx.clean.bean.RubbishItemBean;
import com.zx.clean.utils.StorageUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxun on 2015/11/30.
 */
public class MemoryItemAdapter extends RecyclerView.Adapter<MemoryItemAdapter.MemoryViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<AppProcessInfo> list = new ArrayList<AppProcessInfo>();
    private List<AppProcessInfo> checkeddata  = new ArrayList<AppProcessInfo>();
    private List<Integer> checkPositionlist = new ArrayList<Integer>();

    private boolean animationsLocked = false;
    private int lastAnimatedPosition = -1;
    private boolean delayEnterAnimation = true;

    public MemoryItemAdapter(Context mContext, List<AppProcessInfo> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public MemoryItemAdapter.MemoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.memory_item, parent, false);
        return new MemoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MemoryItemAdapter.MemoryViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        AppProcessInfo info = list.get(position);
        holder.app_icon.setImageDrawable(info.icon);
        holder.app_name.setText(info.appName);
        holder.app_use_size.setText(StorageUtil.convertStorage(info.memory));
        holder.item_rl.setOnClickListener(this);
        holder.item_rl.setTag(holder);

        holder.checkbox.setTag(new Integer(position));
        if (checkPositionlist !=null){
            holder.checkbox.setChecked(checkPositionlist.contains(new Integer(position)) ? true : false);
        }else {
            holder.checkbox.setChecked(false);
        }
        onchecked(holder, position);

    }

    private void onchecked(final MemoryViewHolder holder, final int position) {
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppProcessInfo info = list.get(position);

                if (isChecked) {
                    if (!checkPositionlist.contains(holder.checkbox.getTag())) {
                        checkeddata.add(info);
                        checkPositionlist.add(new Integer(position));
                    }
                } else {
                    if (checkPositionlist.contains(holder.checkbox.getTag())) {
                        checkeddata.remove(info);
                        checkPositionlist.remove(new Integer(position));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null && list.size() > 0){
            return list.size();
        }
        return 0;
    }

    public void updateAdapter(boolean checked){
        if (checked){
            if (checkPositionlist != null && checkeddata != null){
                checkPositionlist.clear();
                checkeddata.clear();
            }else {
                checkPositionlist = new ArrayList<>();
                checkeddata = new ArrayList<>();
            }
            checkeddata.addAll(list);
            for (int i = 0; i < list.size(); i++){
                checkPositionlist.add(i);
            }
        }else {
            if (checkPositionlist == null && checkeddata == null){
                checkPositionlist = new ArrayList<>();
                checkeddata = new ArrayList<>();
            }else {
                checkPositionlist.clear();
                checkeddata.clear();
            }
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(List<AppProcessInfo> list) {
        checkPositionlist.clear();
        checkeddata.clear();
        this.list = list;
        notifyDataSetChanged();
    }

   public List<AppProcessInfo> getCheckeddata() {
       if (checkeddata != null && checkeddata.size() > 0){
           return checkeddata;
       }
       return null;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_rl :
                MemoryViewHolder holder = (MemoryViewHolder) v.getTag();
//                onchecked(holder, holder.getPosition());
                int position = holder.getPosition();
                if (checkPositionlist.contains(position)){
                    checkPositionlist.remove(new Integer(position));
                    checkeddata.remove(list.get(position));
                    holder.checkbox.setChecked(false);
                }else {
                    checkPositionlist.add(new Integer(position));
                    checkeddata.add(list.get(position));
                    holder.checkbox.setChecked(true);
                }
                break;
        }
    }

    public class MemoryViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.app_icon)
        ImageView app_icon;
        @InjectView(R.id.app_name)
        TextView app_name;
        @InjectView(R.id.app_use_size)
        TextView app_use_size;
        @InjectView(R.id.checkbox)
        CheckBox checkbox;
        @InjectView(R.id.item_rl)
        RelativeLayout item_rl;

        public MemoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
