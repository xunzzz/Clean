package com.zx.clean.bean;

import com.zx.clean.utils.Theme;

/**
 * Created by zhangxun on 2015/11/20.
 */
public class ItemTheme {
    private boolean isChecked;
    private String cardName;
    private int color;
    private int colorDark;
    private Theme theme;

    public ItemTheme(boolean isChecked, String cardName, int color, int colorDark, Theme theme) {
        this.isChecked = isChecked;
        this.cardName = cardName;
        this.color = color;
        this.colorDark = colorDark;
        this.theme = theme;
    }

    public int getColorDark() {
        return colorDark;
    }

    public void setColorDark(int colorDark) {
        this.colorDark = colorDark;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }
}
