package com.zx.clean.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zx.clean.R;

/**
 * Created by zhangxun on 2015/11/20.
 */
public class ItemCardViewMini extends RelativeLayout{

    private ImageView cardImage;
    private TextView cardName;
    private Context mContext;

    public ItemCardViewMini(Context context) {
        super(context);
        init(context);
    }

    public ItemCardViewMini(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemCardView);
        cardName.setText(typedArray.getString(R.styleable.ItemCardView_card_name));
        cardImage.setImageDrawable(typedArray.getDrawable(R.styleable.ItemCardView_card_image));
        typedArray.recycle();
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.card_item_mini, this);
        cardName = (TextView) view.findViewById(R.id.card_name);
        cardImage = (ImageView) view.findViewById(R.id.card_image);
    }
}
