package com.example.douglasbookstore;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglasbookstore.adapters.ReviewAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BookReviewActivity extends AppCompatActivity {

    RatingBar ratingBarTotal, ratingBar;
    TextView txtNumRv, txtNoRv;
    ListView listView, listViewHeader;
    EditText editText;
    Button btn;

    SQLiteDatabase db = null;
    Cursor cursor;
    ReviewAdapter adapter;

    double ratingTotal;
    int bookIDSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_review);

        //get id of selected book from BookInfo activity
        Intent intent = getIntent();
        bookIDSelected = intent.getExtras().getInt("bookID");
        getBookName();

        setReviewListHeader();
        addReview();
        loadReviews();
    }

    //gets a book name which is selected.
    private void getBookName(){
        db = DatabaseHelper.getInstance(this).getWritableDatabase();
        cursor = db.rawQuery("SELECT Name FROM Book WHERE _id = "+bookIDSelected+";", null);
        startManagingCursor(cursor);
        String bookNameSelected = "Failed to load Book Name";
        while(cursor.moveToNext()){
            bookNameSelected = cursor.getString(0);
        }

        TextView txtBookTitle = findViewById(R.id.rvTxtName);
        txtBookTitle.setText(String.valueOf(bookNameSelected));
    }

    //adds reviews to the database from a user input
    private void addReview(){
        db = DatabaseHelper.getInstance(this).getWritableDatabase();
        btn = findViewById(R.id.rvBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText = findViewById(R.id.rvEditText);
                ratingBar = findViewById(R.id.rvRatingBar);

                String comment = editText.getText().toString();
                double rating = ratingBar.getRating();

                if(rating == 0){
                    Toast.makeText(BookReviewActivity.this, "Please add your rating", Toast.LENGTH_SHORT).show();
                } else if(comment.equals("")){
                    Toast.makeText(BookReviewActivity.this, "Please add your comment", Toast.LENGTH_SHORT).show();
                } else {
                    db.execSQL("INSERT INTO REVIEW (Comment, Rate, BookID) VALUES ('"+comment+"','"+rating+"','"+bookIDSelected+"');");
                    Toast.makeText(BookReviewActivity.this, comment+", "+rating, Toast.LENGTH_SHORT).show();
                }

                loadReviews();

            }
        });
    }

    //loads all reviews that the selected book has
    private void loadReviews(){
        cursor = db.rawQuery("SELECT * FROM REVIEW WHERE BookID = "+bookIDSelected+";", null);
        startManagingCursor(cursor);
        List<String[]> reviews = new ArrayList<>();
        double sumRating = 0;

        while(cursor.moveToNext()){
            String[] arr ={
                    String.valueOf(cursor.getInt(0)),
                    String.valueOf(cursor.getDouble(3)),
                    cursor.getString(2)
            };
            reviews.add(arr);
            sumRating += cursor.getDouble(3);
        }
        setRatingTotal( sumRating / reviews.size() );
        //Toast.makeText(this, ratingTotal+" = "+sumRating+" / "+reviews.size(), Toast.LENGTH_SHORT).show();
        ratingBarTotal = findViewById(R.id.rvRatingBarTotal);
        ratingBarTotal.setRating((float) ratingTotal);

        txtNumRv = findViewById(R.id.rvTxtNumRv);
        txtNoRv = findViewById(R.id.rvTxtNoReview);
        DecimalFormat df = new DecimalFormat("#.##");
        if(reviews.size() == 0){
            txtNumRv.setText( R.string.noReviewsYet );
        } else {
            txtNumRv.setText(df.format(ratingTotal) + " / " + reviews.size() + " reviews");
            txtNoRv.setVisibility(View.INVISIBLE);
        }

        listView = findViewById(R.id.rvListView);
        listView.setAdapter(adapter = new ReviewAdapter(reviews));

    }

    private void setReviewListHeader(){
        listViewHeader = findViewById(R.id.rvListViewHeader);
        List<String[]> header = new ArrayList<>();
        String[] arr = {"ID", "Rating", "Comment"};
        header.add(arr);
        listViewHeader.setAdapter(new ReviewAdapter(header));
        listViewHeader.setEnabled(false);
    }

    public double getRatingTotal() {
        return ratingTotal;
    }

    private void setRatingTotal(double ratingTotal) {
        this.ratingTotal = ratingTotal;
    }
}
