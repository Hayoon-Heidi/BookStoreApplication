package com.example.douglasbookstore.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

//This adapter helps user to choose the image inside of the grid and see bigger.
public class HelpImgAdapter extends BaseAdapter {

    Context context;
    int[] imgs;

    public HelpImgAdapter (Context c, int[] imgs) {
        this.context = c;
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        return imgs.length;
    }

    @Override
    public Object getItem(int position) {
        return imgs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView pic = new ImageView(context);
        pic.setImageResource(imgs[position]);

        pic.setPadding(2,2,2,2);
        pic.setMaxHeight(300);
        pic.setAdjustViewBounds(true);
        pic.setScaleType(ImageView.ScaleType.FIT_XY);


        return pic;
    }
}
