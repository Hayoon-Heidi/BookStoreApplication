package com.example.douglasbookstore;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.douglasbookstore.adapters.BookInfoAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.DecimalFormat;

public class BookInfoActivity extends AppCompatActivity {
    DatabaseHelper dbh;
    Book selectedBook;
    private int bookIDSelected; //get this from SearchFragment listView OnItemClickListener.
    private String bookISBNSelected;
    private int numOfReviews;
    private double ratings;

    RequestQueue queue;
    SimpleDraweeView draweeView;
    String[] results = new String[2];
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_book_info);

        //get the id of the book selected in SearchFragment
        //get ISBN for image searching
        Intent intent = getIntent();
        bookIDSelected = intent.getExtras().getInt("bookID");
        bookISBNSelected = intent.getExtras().getString("ISBN");

        //go to review activity if click on Review text view
        openReviewActivity();

        //get selected book from previous screen
        setSelectedBook();

        TextView txtBookTitle = findViewById(R.id.infoTxtBookName);
        txtBookTitle.setText(selectedBook.getBookName());

        //display information of selected book through list view
        ListView listView = findViewById(R.id.infoListView);
        listView.setAdapter(new BookInfoAdapter(this, selectedBook));

        //bring user to Douglas Bookstore's order page to buy book
        Button btnBuy = findViewById(R.id.infoBtnBuy);

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse("https://bookstore.douglascollege.ca/buy_textbooks.asp");
                Intent intent1 = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent1);
            }
        });

        TextView rate = findViewById(R.id.infoTxtRate);

        getNumOfRvAndRatings();
        if (numOfReviews == 0){
            rate.setText(R.string.noReviewsYet);
        } else {
            rate.setText(new DecimalFormat("#.##").format(ratings) + " / from " + numOfReviews+ " reviews");
        }


        //Get a book image from Google books through Volley
        queue = Volley.newRequestQueue(this);
        searchBookImageOnline();

        dbh.close();
    }
    private void setSelectedBook(){
        //first set selectedBook with default values, then use it to hold the data of selected book in SearchFragment/BookShelfFragment
        selectedBook = new Book();
        dbh = new DatabaseHelper(this);

        //get information of the book that needed to be displayed and set for selected book
        Cursor cursor = dbh.findBookByID(bookIDSelected);

        if(cursor.getCount() == 1){
            cursor.moveToNext();
            selectedBook.setAuthorName(cursor.getString(0));
            selectedBook.setISBN(cursor.getString(1));
            selectedBook.setNewPrice(cursor.getString(2));
            selectedBook.setUsedPrice(cursor.getString(3));
            selectedBook.setComment(cursor.getString(4));
            selectedBook.setCourseName(cursor.getString(5));
            selectedBook.setSection(cursor.getString(6));
            selectedBook.setInstructorName(cursor.getString(7));
            selectedBook.setBookName(cursor.getString(8));
            selectedBook.setDepName(cursor.getString(9));

        }else{
            Log.i("BookInfo: ", "Cannot get selected book");
        }
    }
    private void openReviewActivity(){
        //bring user to review page to add review for this book
        TextView txtSeeReview = findViewById(R.id.infoTxtSeeRv);
        txtSeeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookInfoActivity.this, BookReviewActivity.class);
                intent.putExtra("bookID", bookIDSelected);
                startActivity(intent);
            }
        });

    }

    //get the number of reviews and the total rating for the book selected.
    private void getNumOfRvAndRatings(){
        dbh = new DatabaseHelper(this);
        Cursor cursor = dbh.getNumOfReviews(bookIDSelected);
        if(cursor.getCount() > 0){
            cursor.moveToNext();
            numOfReviews = cursor.getInt(0);
        }else{
            Log.i("BookInfo: ", "Cannot get numOfReviews");
        }
        Cursor cursor2 = dbh.getRatings(bookIDSelected);
        if(cursor2.getCount() > 0){
            cursor2.moveToNext();
            ratings = cursor2.getDouble(0);
        }else{
            Log.i("BookInfo: ", "Cannot get ratings");
        }

    }


    //makes an URI which will be the request for searching
    private void searchBookImageOnline(){
        //this keyword will be the ISBN passed from SearchFragment (OnItemClickListener).
        String keyword = bookISBNSelected;

        boolean isConnected = Read_network_state(this);
        if(!isConnected){
           failedToFindImageOnline();
        }

        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, keyword)
                .appendQueryParameter(MAX_RESULTS, "1")
                .appendQueryParameter(PRINT_TYPE, "books")
                .build();

        parseJson(uri.toString());
    }

    //sends a request to Google books using Volley.
    // receives a result as a from of JSON, extracts an image URI from it.
    public void parseJson(String key){

        // Request a json response from the provided URL.
        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, key.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("BookImage JSON", response.toString());

                        //sometimes result is weird depends on the size of items so I limited to fetch 1 result only.

                        try {
                            JSONArray itemsArray = response.getJSONArray("items");

                            for(int i = 0; i < itemsArray.length(); i++){
                                JSONObject item = itemsArray.getJSONObject(i);
                                JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                                //get the title of a book
                                try{
                                    String title = volumeInfo.getString("title");
                                    results[0] = title;

                                    Log.d("BookImage JSON", "Title: " + title);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    failedToFindImageOnline();
                                }

                                //get the image URI of a book
                                try{
                                    String thumbnail = volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail");
                                    //this replacement is needed because Android does not support "http"
                                    thumbnail = thumbnail.replaceAll("http","https");
                                    results[1] = thumbnail;
                                    setBookImageWithURI(thumbnail);
                                }catch (Exception e){
                                    e.printStackTrace();
                                    failedToFindImageOnline();
                                }

                            }

                            //for result test
                            /*
                            Toast.makeText(BookInfoActivity.this,
                                    results[0]+ "\n" + results[1],
                                    Toast.LENGTH_LONG).show();
                            TextView txt = findViewById(R.id.textViewJSONResult);
                            txt.setText(results[0]+ "\n" + results[1]);
                             */

                            Log.d("BookImage JSON",results[0]+ "\n" + results[1]);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            failedToFindImageOnline();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                failedToFindImageOnline();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonRequest);
    }

    //sets an image by an URI
    public void setBookImageWithURI(String imageLink){
        TextView txtImageWarning = findViewById(R.id.infoTxtImageWarning);
        ImageView imgWarning = findViewById(R.id.infoImgWarning);
        imgWarning.setVisibility(View.VISIBLE);

        draweeView = findViewById(R.id.imageViewBookCover);
        if (imageLink.equals("nullString")){
            draweeView.setImageResource(R.drawable.ic_image_24dp);
            txtImageWarning.setText(R.string.infoTxtFailed);
            Log.d("SetImageUri", "image null");
        } else {
            Uri uri = Uri.parse(imageLink);
            draweeView.setImageURI(uri);
            txtImageWarning.setText(R.string.infoTxtWarning);
            Log.d("SetImageUri", "image not null");
        }
    }

    private void failedToFindImageOnline(){
        setBookImageWithURI("nullString");
        //Toast.makeText(this, "Something is wrong with finding the book!", Toast.LENGTH_SHORT).show();
    }

    private boolean Read_network_state(Context context) {    boolean is_connected;
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        is_connected=info!=null&&info.isConnectedOrConnecting();
        return is_connected;
    }

}
