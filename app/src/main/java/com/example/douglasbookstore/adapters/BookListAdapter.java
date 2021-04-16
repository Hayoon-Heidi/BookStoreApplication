package com.example.douglasbookstore.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.douglasbookstore.Book;
import com.example.douglasbookstore.DatabaseHelper;
import com.example.douglasbookstore.R;
import java.util.List;

public class BookListAdapter extends BaseAdapter {

    List<Book> books ;
    SQLiteDatabase db;
    boolean calledFromBookshelf;
    boolean removeRequest;
    Context context;

    public BookListAdapter(List<Book> books){
        this.books = books;
    }

    public BookListAdapter(List<Book> books, boolean bookshelf, Context context){
        this.books = books;
        this.calledFromBookshelf = bookshelf;
        this.context = context;
    }

    static class ViewHolder{
        protected TextView txt1;
        protected TextView txt2;
        protected TextView txt3;
        protected TextView txt4;

        protected CheckBox cb;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        db = DatabaseHelper.getInstance(parent.getContext()).getWritableDatabase();
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.layout_book_list, parent,false);

            viewHolder = new ViewHolder();
            viewHolder.txt1 = convertView.findViewById(R.id.textViewBookID);
            viewHolder.txt2 = convertView.findViewById(R.id.textViewBookName);
            viewHolder.txt3 = convertView.findViewById(R.id.textViewDepCode);
            viewHolder.txt4 = convertView.findViewById(R.id.textViewSection);
            viewHolder.cb = convertView.findViewById(R.id.checkBoxBookshelf);

            //checkBox ChangeListener to add or remove books from bookshelf
            viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    final int getPosition = (int) buttonView.getTag();
                    books.get(getPosition).setInBookShelves(buttonView.isChecked());

                    ContentValues val = new ContentValues();
                    if (isChecked){
                        val.put("InBookShelves", 1);
                        Log.i("BookList Adapter: ", getPosition + " checkBox checked");
                        removeRequest = false;
                    } else {
                        val.put("InBookShelves", "0");
                        Log.i("BookList Adapter: ", getPosition + " checkBox unchecked");
                        removeRequest = true;
                    }
                    db.update("Book", val, "_id = ?", new String[]{String.valueOf(books.get(getPosition).getBookID())});

                    //if the checkBox is unchecked from the bookshelf, it removes the book from the list.
                    if (calledFromBookshelf == true && removeRequest == true){
                        books.remove(getPosition);
                        notifyDataSetChanged();
                    }
                }
            });
            convertView.setTag(viewHolder);
            convertView.setTag(R.id.textViewBookID, viewHolder.txt1);
            convertView.setTag(R.id.textViewBookName, viewHolder.txt2);
            convertView.setTag(R.id.textViewDepCode, viewHolder.txt3);
            convertView.setTag(R.id.textViewSection, viewHolder.txt4);
            convertView.setTag(R.id.checkBoxBookshelf, viewHolder.cb);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.cb.setTag(position);
        viewHolder.cb.setFocusable(false);

        viewHolder.txt1.setText(String.valueOf(books.get(position).getBookID()));
        viewHolder.txt2.setText(books.get(position).getBookName());
        viewHolder.txt3.setText(books.get(position).getDepCode());
        viewHolder.txt4.setText(books.get(position).getSection());
        viewHolder.cb.setChecked(books.get(position).getInBookShelvesBooleanValue());

        viewHolder.txt2.setGravity(Gravity.CENTER_VERTICAL);
        viewHolder.txt3.setGravity(Gravity.CENTER_VERTICAL);

        return convertView;
    }

}
