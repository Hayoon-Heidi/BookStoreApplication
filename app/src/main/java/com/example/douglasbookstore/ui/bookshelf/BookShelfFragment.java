package com.example.douglasbookstore.ui.bookshelf;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.douglasbookstore.Book;
import com.example.douglasbookstore.BookInfoActivity;
import com.example.douglasbookstore.adapters.BookListAdapter;
import com.example.douglasbookstore.DatabaseHelper;
import com.example.douglasbookstore.R;
import com.example.douglasbookstore.ui.search.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class BookShelfFragment extends Fragment {

    ListView listViewBookShelf;
    TextView txtViewNoBook;
    SQLiteDatabase db = null;
    Cursor cursor;
    List<Book> books;
    BookListAdapter bookListAdapter;
    View view;

    private BookShelfViewModel bookShelfViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookShelfViewModel =
                ViewModelProviders.of(this).get(BookShelfViewModel.class);
        view = inflater.inflate(R.layout.fragment_bookshelf, container, false);

        try {
            listBooks();
        }catch (Exception ex){
            Log.i("shelves", "error");
        }

        return view;
    }

    //lists books from the database that are added to the bookshelf (depending on InBookShelves column)
    private void listBooks(){
        listViewBookShelf = view.findViewById(R.id.shfListView);
        txtViewNoBook = view.findViewById(R.id.shfTxtNoBook);
        db = DatabaseHelper.getInstance(getActivity()).getWritableDatabase();
        cursor = db.rawQuery("SELECT _id, ISBN, Author, Name, Department.DepartmentID, Course.CourseID" +
                ", Comment, New, Used, InBookShelves, DepartmentName, Section, InstructorName "+
                "FROM BOOK,Department,Course " +
                "WHERE Book.DepartmentID = Department.DepartmentID AND Book.CourseID = Course.CourseID " +
                "AND InBookShelves = 1;", null);
        //startManagingCursor(cursor);

        books = new ArrayList<>();

        int i = 0;
        if(cursor.getCount() > 0) {

            while(cursor.moveToNext()) {
                books.add(new Book(
                        //id
                        cursor.getInt(0),
                        //isbn
                        cursor.getString(1),
                        //authorName
                        cursor.getString(2),
                        //bookName
                        cursor.getString(3),
                        //depID
                        cursor.getInt(4),
                        //courseID
                        cursor.getInt(5),
                        //comment
                        cursor.getString(6),
                        //newPrice
                        cursor.getString(7),
                        //usedPrice
                        cursor.getString(8),
                        //inBookShelves
                        cursor.getInt(9),
                        //depName
                        cursor.getString(10),
                        //section
                        cursor.getString(11),
                        //instructorName
                        cursor.getString(12),
                        //depCode
                        SearchFragment.generateDepCode(cursor.getString(10))
                ));

                Log.i("Reading database: ", "VALUES: " +
                        cursor.getInt(0)+
                        cursor.getString(1)+
                        cursor.getString(2)+
                        cursor.getString(3)+
                        cursor.getInt(4)+
                        cursor.getInt(5)+
                        cursor.getString(6)+
                        cursor.getString(7)+
                        cursor.getString(8)+
                        cursor.getInt(9)+
                        cursor.getString(10)+
                        cursor.getString(11)+
                        cursor.getString(12)+
                        SearchFragment.generateDepCode(cursor.getString(10)));
                Log.i("Bookstore: adding book", "book: " + i);
                i++;
            }

            bookListAdapter = new BookListAdapter(books, true, getParentFragment().getContext());
            listViewBookShelf.setAdapter(bookListAdapter);
            txtViewNoBook.setVisibility(View.INVISIBLE);
        }else{
            //Toast.makeText(getActivity(),"Saved books failed",Toast.LENGTH_LONG).show();
            txtViewNoBook.setVisibility(View.VISIBLE);
        }

        //passes to BookInfoActivity
        listViewBookShelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "You clicked on " + books.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                int bookIDSelected = books.get(position).getBookID();
                Intent intent = new Intent(getActivity(), BookInfoActivity.class);
                intent.putExtra("bookID", bookIDSelected);
                intent.putExtra("ISBN", books.get(position).getISBN());
                startActivity(intent);
            }
        });
    }

}