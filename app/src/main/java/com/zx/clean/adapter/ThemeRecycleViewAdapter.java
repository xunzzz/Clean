package com.zx.clean.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zx.clean.R;
import com.zx.clean.bean.ItemTheme;
import com.zx.clean.utils.Theme;
import com.zx.clean.utils.ThemeSet;

import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zhangxun on 2015/11/20.
 */
public class ThemeRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private Context context;
    private int count = 9;
    private OnItemClickListener itemClickListener;

    private List<ItemTheme> list = new ArrayList<>();
//    private int checkedColor;
//    private int checkedColorDark;
//    private Theme theme;
    private ItemTheme checkedItem;



    public ThemeRecycleViewAdapter(Context context) {
        this.context = context;
        this.list = ThemeSet.initThemeData(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_theme, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.item_theme_cd.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        ItemTheme itemTheme = list.get(position);
        itemHolder.item_theme_text.setText(itemTheme.getCardName());
        itemHolder.item_theme_cd.setCardBackgroundColor(context.getResources().getColor(itemTheme.getColor()));
        if (itemTheme.isChecked()){
            itemHolder.check_iv.setVisibility(View.VISIBLE);
        }else {
            itemHolder.check_iv.setVisibility(View.GONE);
        }
        itemHolder.item_theme_cd.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateChecked(ViewHolder holder){
        for (ItemTheme item : list){
            item.setIsChecked(false);
        }
        int position = holder.getPosition();
        ItemTheme itemTheme = list.get(position);
        itemTheme.setIsChecked(true);
        checkedItem = itemTheme;
//        checkedColor = itemTheme.getColor();
//        checkedColorDark = itemTheme.getColorDark();
//        theme = itemTheme.getTheme();
        list.set(position, itemTheme);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.item_theme_cd :
                if (itemClickListener !=null){
                    ViewHolder holder = (ViewHolder) v.getTag();
                    updateChecked(holder);
                    itemClickListener.onItemClick(v, holder.getPosition());
                }
                break;
        }
    }

    public List<ItemTheme> getThemeData() {
        return list;
    }

    public ItemTheme getCheckedItem() {
        return checkedItem;
    }

    //    public int getCheckedColor() {
//        return checkedColor;
//    }
//
//    public int getCheckedColorDark() {TypedValue typedValue = new TypedValue();
//        return checkedColorDark;
//    }
//
//    public Theme getTheme() {
//        return theme;
//    }

    public interface OnItemClickListener{
        void onItemClick(View v, int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.item_theme_cd)
        CardView item_theme_cd;
        @InjectView(R.id.item_theme_text)
        TextView item_theme_text;
        @InjectView(R.id.check_iv)
        ImageView check_iv;
        @InjectView(R.id.item_rl)
        RelativeLayout item_rl;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

}
