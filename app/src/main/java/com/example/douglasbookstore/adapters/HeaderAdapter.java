package com.example.douglasbookstore.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.douglasbookstore.R;

import java.util.ArrayList;
import java.util.List;

public class HeaderAdapter extends BaseAdapter {

    List<String[]> header = new ArrayList<>();
    Context context;

    public HeaderAdapter(Context context, List<String[]> header) {
        this.context = context;
        this.header = header;
    }

    @Override
    public boolean isEnabled(int position){
        return false;
    }

    @Override
    public int getCount() {
        return header.size();
    }

    @Override
    public Object getItem(int position) {
        return header.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( convertView == null ){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_book_list_header, parent, false);
        }
        TextView id, title, dep, sec, cb;
        id = convertView.findViewById(R.id.headerBookID);
        title = convertView.findViewById(R.id.headerBookName);
        dep = convertView.findViewById(R.id.headerDepCode);
        sec = convertView.findViewById(R.id.headerSection);
        cb = convertView.findViewById(R.id.headerCheckBoxBookshelf);

        id.setText(header.get(position)[0]);
        title.setText(header.get(position)[1]);
        dep.setText(header.get(position)[2]);
        sec.setText(header.get(position)[3]);
        cb.setText(header.get(position)[4]);

        id.setGravity(Gravity.CENTER);
        title.setGravity(Gravity.CENTER);
        dep.setGravity(Gravity.CENTER);
        sec.setGravity(Gravity.CENTER);
        cb.setGravity(Gravity.CENTER);

        return convertView;
    }
}
