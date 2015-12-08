package com.zx.clean.utils;

import android.content.Context;

import com.zx.clean.R;
import com.zx.clean.bean.ItemTheme;

import java.util.ArrayList;


/**
 * Created by zhangxun on 2015/11/19.
 */
public class ThemeSet {

    public static void onPreCreate(Context context) {
        final Theme currentTheme = PreferenceManager.getCurrentTheme(context);

        switch (currentTheme) {
            case Blue:
                context.setTheme(R.style.BlueTheme);
                break;
            case Green:
                context.setTheme(R.style.GreenTheme);
                break;
            case Red:
                context.setTheme(R.style.RedTheme);
                break;
            case Indigo:
                context.setTheme(R.style.IndigoTheme);
                break;
            case BlueGrey:
                context.setTheme(R.style.BlueGreyTheme);
                break;
            case Black:
                context.setTheme(R.style.BlackTheme);
                break;
            case Orange:
                context.setTheme(R.style.OrangeTheme);
                break;
            case Purple:
                context.setTheme(R.style.PurpleTheme);
                break;
            case Pink:
                context.setTheme(R.style.PinkTheme);
                break;
            default:
                context.setTheme(R.style.BlueTheme);
                break;
        }
    }

    public static ArrayList<ItemTheme> initThemeData(Context context) {
        ArrayList<ItemTheme> themeList = new ArrayList<>();

        ItemTheme t1 = new ItemTheme(false, "Blue", R.color.blue_primary, R.color.blue_primary_dark, Theme.Blue);
        ItemTheme t2 = new ItemTheme(false, "Green", R.color.green_primary, R.color.green_primary_dark, Theme.Green);
        ItemTheme t3 = new ItemTheme(false, "Red", R.color.red_primary, R.color.red_primary_dark, Theme.Red);
        ItemTheme t4 = new ItemTheme(false, "Indigo", R.color.indigo_primary, R.color.indigo_primary_dark, Theme.Indigo);
        ItemTheme t5 = new ItemTheme(false, "Black", R.color.black_primary, R.color.black_primary_dark, Theme.Black);
        ItemTheme t6 = new ItemTheme(false, "BlueGrey", R.color.blue_grey_primary, R.color.blue_grey_primary_dark, Theme.BlueGrey);
        ItemTheme t7 = new ItemTheme(false, "Orange", R.color.orange_primary, R.color.orange_primary_dark, Theme.Orange);
        ItemTheme t8 = new ItemTheme(false, "Purple", R.color.purple_primary, R.color.purple_primary_dark, Theme.Purple);
        ItemTheme t9 = new ItemTheme(false, "Pink", R.color.pink_primary, R.color.pink_primary_dark, Theme.Pink);

        themeList.add(t1);
        themeList.add(t2);
        themeList.add(t3);
        themeList.add(t4);
        themeList.add(t5);
        themeList.add(t6);
        themeList.add(t7);
        themeList.add(t8);
        themeList.add(t9);


        return themeList;
    }


}
