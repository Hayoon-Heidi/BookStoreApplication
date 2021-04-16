package com.example.douglasbookstore.ui.search;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.example.douglasbookstore.adapters.HeaderAdapter;

import java.util.ArrayList;
import java.util.List;

public class     SearchFragment extends Fragment {

    private SearchViewModel SearchViewModel;

    private EditText editTextSearch;
    private Button btnSearch;
    private ListView listViewSearch, listViewHeader;
    private TextView txtNoResult;
    private Spinner spinnerSearch;
    private String searchBy;
    private BookListAdapter bookListAdapter;
    private List<Book> books;

    private SQLiteDatabase db = null;
    private Cursor cursor;

    private int bookIDSelected;

    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SearchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        view = inflater.inflate(R.layout.fragment_search, container, false);

        searchUsingSpinner();

        return view;
    }

    private void searchUsingSpinner(){
        spinnerSearch = view.findViewById(R.id.searchSpinner);
        editTextSearch = view.findViewById(R.id.searchEditTxt);
        //set Hints for editText
        spinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0 :
                        editTextSearch.setHint(R.string.searchHintCourse);
                        break;
                    case 1:
                        editTextSearch.setHint(R.string.searchHintBookTitle);
                        break;
                    case 2:
                        editTextSearch.setHint(R.string.searchHintDepartment);
                        break;
                    default:
                        editTextSearch.setHint(R.string.searchHintCourse);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnSearch = view.findViewById(R.id.searchBtn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Button Onclick: ", "Search Button");

                String keyword = editTextSearch.getText().toString();

                if (keyword.equals("")){
                    Toast.makeText(getParentFragment().getContext(), "Please enter a keyword to search.", Toast.LENGTH_SHORT).show();
                } else {

                    String spinnerText = spinnerSearch.getSelectedItem().toString();

                    listViewSearch = view.findViewById(R.id.searchListView);
                    listViewSearch.setFocusable(false);

                    Log.i("Button Onclick: ", spinnerText);

                    if (spinnerText.equals("Course Name")){

                        searchBy = "CourseName";
                        String table_search = "Course";
                        String column_ID = "CourseID";

                        showSearchResults(listViewSearch, searchQueryText(keyword, table_search, column_ID, searchBy));

                    } else if(spinnerText.equals("Book Title")){

                        searchBy = "Name";
                        String table_search = null;
                        String column_ID = null;

                        showSearchResults(listViewSearch, searchQueryText(keyword, table_search, column_ID, searchBy));

                    } else if (spinnerText.equals("Department")){

                        searchBy = "DepartmentName";
                        String table_search = "Department";
                        String column_ID = "DepartmentID";

                        showSearchResults(listViewSearch, searchQueryText(keyword, table_search, column_ID, searchBy));

                    }
                }

            }

        });
    }

    //creates search sql with the table info and keyword from the spinner.
    private String searchQueryText(String keyword, String tableName, String tableName_id, String columnName_searchBy){

        String searchQuery = "";
        if ( tableName == null && tableName_id == null){
            searchQuery = "SELECT _id, ISBN, Author, Name, Department.DepartmentID, Course.CourseID" +
                    ", Comment, New, Used, InBookShelves, DepartmentName, Section, InstructorName"
                    + " FROM Book, Department, Course"
                    + " WHERE Book.DepartmentID = Department.DepartmentID"
                    + " AND Book.CourseID = Course.CourseID"
                    + " AND " + columnName_searchBy + " LIKE '%"+keyword+"%';";
        } else {
            String tableLeft = "";
            if ( tableName.equals("Department") ){
                tableLeft = "Course";
            } else if ( tableName.equals("Course") ){
                tableLeft = "Department";
            }
            String tableLeft_id = tableLeft + "ID";

            searchQuery = "SELECT _id, ISBN, Author, Name, Department.DepartmentID, Course.CourseID" +
                    ", Comment, New, Used, InBookShelves, DepartmentName, Section, InstructorName"
                    + " FROM Book, Department, Course"
                    + " WHERE Book." +tableName_id+ " = " +tableName+"."+tableName_id
                    + " AND Book."+tableLeft_id+ " = " +tableLeft+"."+tableLeft_id
                    + " AND " +tableName+"."+columnName_searchBy+ " LIKE '%"+keyword+"%';";

        }
        if (searchQuery.equals("")){
            Log.e("searchQueryText: ", "searchQuery is empty.");
        }

        return searchQuery;
    }

    //get search query and apply it to listView.
    public void showSearchResults(ListView listView, String selectQuery){
        try{
            setListViewHeader();
            bookListAdapter = new BookListAdapter(getBookList(selectQuery));
            listView.setAdapter(bookListAdapter);

            //passes to BookInfoActivity with an id of the book clicked
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(getActivity(), "You clicked on "+ books.get(position).getBookName(), Toast.LENGTH_SHORT).show();
                    bookIDSelected = books.get(position).getBookID();
                    Intent intent = new Intent(getActivity(), BookInfoActivity.class);
                    intent.putExtra("bookID", bookIDSelected);
                    intent.putExtra("ISBN", books.get(position).getISBN());
                    startActivity(intent);
                }
            });

            Log.i("showSearchResults: ", "set adapter");

        } catch (Exception ex){
            Log.e("showSearchResults: ", ex.getMessage());
        }
    }

    //simply returns List<Book> generated by the database.
    private List<Book> getBookList(String selectQuery){

        books = new ArrayList<>();

        db = DatabaseHelper.getInstance(getActivity()).getReadableDatabase();

        try{
            cursor = db.rawQuery(selectQuery, null);

            if(cursor.getCount() > 0) {
                txtNoResult = view.findViewById(R.id.searchTxtNoResult);
                txtNoResult.setVisibility(View.INVISIBLE);

                int i = 0;
                while (cursor.moveToNext()) {
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
                            generateDepCode(cursor.getString(10))
                    ));

                    Log.i("Reading database: ", "VALUES: " +
                            cursor.getInt(0) +
                            cursor.getString(1) +
                            cursor.getString(2) +
                            cursor.getString(3) +
                            cursor.getInt(4) +
                            cursor.getInt(5) +
                            cursor.getString(6) +
                            cursor.getString(7) +
                            cursor.getString(8) +
                            cursor.getInt(9) +
                            cursor.getString(10) +
                            cursor.getString(11) +
                            cursor.getString(12) +
                            generateDepCode(cursor.getString(10)));
                    Log.i("Bookstore: adding book", "book: " + i);
                    i++;
                }
            } else {
                //Toast.makeText(getParentFragment().getContext(), "No results!!!!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                txtNoResult = view.findViewById(R.id.searchTxtNoResult);
                txtNoResult.setVisibility(View.VISIBLE);
            }
        } catch (Exception e){
            Log.e("make books List", e.getMessage());
        }

        return books;
    }

    //extracts a department code(e.g. CSIS 3175) from the DepartmentName in the database.
    public static String generateDepCode(String depName){
        String depCode = "";
        String[] depNameTokens = depName.split("-");

        depCode = depNameTokens[1].replaceAll(" ", "") + "\n"
                + depNameTokens[2].replaceAll("[^0-9]", "");

        Log.d("DEPCODE GENERATED", depCode);

        return depCode;
    }

    private void setListViewHeader(){
        List<String[]> header = new ArrayList<>();
        String[] arr = {"id", "Book Title", "Course", "Sec", "♥"};
        header.add(arr);
        listViewHeader = view.findViewById(R.id.searchListViewHeader);
        listViewHeader.setAdapter(new HeaderAdapter(getActivity(), header));
        justifyListViewHeightBasedOnChildren(listViewHeader);
    }

    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }


}