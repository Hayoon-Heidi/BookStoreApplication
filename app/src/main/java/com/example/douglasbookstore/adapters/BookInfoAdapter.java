package com.example.douglasbookstore.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.douglasbookstore.Book;
import com.example.douglasbookstore.R;

public class BookInfoAdapter extends BaseAdapter {
    String[] bookInfo = new String[8];
    Context activity;

    public  BookInfoAdapter(Context context, Book book){
        activity = context;
        bookInfo[0] = "Author: " + book.getAuthorName();
        bookInfo[1] = "ISBN: " + book.getISBN();
        bookInfo[2] = "New Price: " + book.getNewPrice();
        bookInfo[3] = "Old Price: " + book.getUsedPrice();
        bookInfo[4] = "Comment: "+book.getComment();
        bookInfo[5] = "Course: " + book.getCourseName() + " " + book.getSection();
        bookInfo[6] = "Instructor: " + book.getInstructorName();
        bookInfo[7] = "Department: " + book.getDepName();
    }
    @Override
    public int getCount() {
        return bookInfo.length;
    }

    @Override
    public String getItem(int i) {
        return bookInfo[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(activity);
            convertView = inflater.inflate(R.layout.layout_book_info,parent,false);
        }

        TextView txtBookInfo = convertView.findViewById(R.id.txtBookInfo);


        txtBookInfo.setText(bookInfo[i]);

        txtBookInfo.setGravity(Gravity.CENTER_VERTICAL);


        return convertView;
    }
}
