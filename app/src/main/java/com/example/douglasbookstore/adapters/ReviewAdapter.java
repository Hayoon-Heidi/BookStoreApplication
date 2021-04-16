package com.example.douglasbookstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.douglasbookstore.R;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    List<String[]> reviews;

    public ReviewAdapter(List<String[]> reviews){
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.layout_review_list, parent, false);
        }
        //TextView id = convertView.findViewById(R.id.txtReviewId);
        TextView rating = convertView.findViewById(R.id.txtReviewRating);
        TextView comment = convertView.findViewById(R.id.txtReviewComment);

        //id.setText(String.valueOf(reviews.get(position)[0]));
        rating.setText(String.valueOf(reviews.get(position)[1]));
        comment.setText(reviews.get(position)[2]);

        return convertView;
    }
}
