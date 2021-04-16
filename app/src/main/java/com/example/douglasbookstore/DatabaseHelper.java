package com.example.douglasbookstore;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SectionIndexer;
import android.widget.Toast;


import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "BOOKSTORE.db";
    private static final int DB_VERSION = 1;

    private static final String BOOK_TABLE = "BOOK";
    private static final String BOOK_BOOKID = "_id";
    private static final String BOOK_ISBN = "ISBN";
    private static final String BOOK_AUTHOR = "Author";
    private static final String BOOK_NAME = "Name";
    private static final String BOOK_DEPART = "DepartmentID";
    private static final String BOOK_COURSE = "CourseID";
    private static final String BOOK_COMM = "Comment";
    private static final String BOOK_NEWPR = "New";
    private static final String BOOK_USEDPR = "Used";
    private static final String BOOK_INBOOKSHELVES = "InBookShelves";

    private static final String DEPART_TABLE = "DEPARTMENT";
    private static final String DEPART_ID = "DepartmentID";
    private static final String DEPART_NAME = "DepartmentName";

    private static final String COURSE_TABLE = "COURSE";
    private static final String COURSE_ID = "CourseID";
    private static final String COURSE_NAME = "CourseName";
    private static final String COURSE_INSTRUCT = "InstructorName";
    private static final String COURSE_SECTION = "Section";

    private static final String REVIEW_TABLE = "REVIEW";
    private static final String REVIEW_ID = "ReviewID";
    private static final String REVIEW_COMMENT = "Comment";
    private static final String REVIEW_RATE = "Rate";
    private static final String REVIEW_BOOK = "BookID";


    private SQLiteDatabase db;
    private final Context context;
    private static DatabaseHelper dbInstance;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        this.context = context;

    }

    public static DatabaseHelper getInstance(Context context){
        if(dbInstance == null){
            dbInstance = new DatabaseHelper(context);
        }
        return dbInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String bookQuery = "create table "+ BOOK_TABLE +"("
                + BOOK_BOOKID + " integer primary key, "
                + BOOK_ISBN + " text, "
                + BOOK_AUTHOR + " text, "
                + BOOK_NAME + " text, "
                + BOOK_DEPART+ " integer, "
                + BOOK_COURSE+ " integer, "
                + BOOK_COMM+ " text, "
                + BOOK_NEWPR+ " text, "
                + BOOK_USEDPR+ " text, "
                + BOOK_INBOOKSHELVES+ " integer, "
                + "CONSTRAINT BOOK_DEPART_FK FOREIGN KEY("+ BOOK_DEPART + ")"+ " REFERENCES " + DEPART_TABLE+"(" + DEPART_ID+"), "
                + "CONSTRAINT BOOK_COURSE_FK FOREIGN KEY("+ BOOK_COURSE+")" +" REFERENCES "+ COURSE_TABLE+"("+COURSE_ID+ ")"
                +") ";

        String departQuery = "create table "+ DEPART_TABLE +"("
                + DEPART_ID + " integer primary key, "
                + DEPART_NAME + " text"
                +") ";

        String courseQuey = "create table "+ COURSE_TABLE +"("
                + COURSE_ID + " integer primary key, "
                + COURSE_NAME + " text, "
                + COURSE_SECTION+ " text, "
                + COURSE_INSTRUCT + " text"
                +") ";

        String reviewQuery = "create table "+ REVIEW_TABLE +"("
                + REVIEW_ID + " integer primary key autoincrement, "
                + REVIEW_BOOK + " integer, "
                + REVIEW_COMMENT + " text, "
                + REVIEW_RATE + " double, "
                + "CONSTRAINT REVIEW_BOOK_FK FOREIGN KEY("+ REVIEW_BOOK + ")"+ " REFERENCES " + BOOK_TABLE+"(" + BOOK_BOOKID+")"
                +") ";

        db.execSQL(bookQuery);
        db.execSQL(departQuery);
        db.execSQL(courseQuey);
        db.execSQL(reviewQuery);

        this.db = db;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DEPART_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COURSE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + REVIEW_TABLE);
        onCreate(db);
    }

    private Cursor findDepart(String departName){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + DEPART_TABLE + " WHERE " + DEPART_NAME + " = '"+ departName +"'";

        return db.rawQuery(query, null);
    }

    private Cursor findCourse(String courseName, String section, String instructor){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + COURSE_TABLE + " WHERE " + COURSE_NAME + " = '"+ courseName +"'" +" AND "+ COURSE_SECTION + " = '"+section+"' AND "
                        +COURSE_INSTRUCT+ " = '"+instructor+"'";

        return db.rawQuery(query, null);
    }

    private boolean addDepart(String depart){
        //populate data for department
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DEPART_NAME, depart);

            long insert = sqLiteDatabase.insert(DEPART_TABLE, null, values);


            return insert > 0;
    }

    private boolean addCourse(String course, String section, String instructor){
        //populate data for course
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COURSE_NAME, course);
        values.put(COURSE_SECTION, section);
        values.put(COURSE_INSTRUCT, instructor);

        long insert = sqLiteDatabase.insert(COURSE_TABLE, null, values);


        return insert > 0;
    }

    private boolean addBook(String id, String ISBN, String author, String name, String depart, String course, String section, String instructor, String comment, String newPrice, String usedPrice){
        Cursor cursor;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            //find and check whether there is the same department exists, if not then add department
            cursor = findDepart(depart);
            if (cursor.getCount() <= 0) {
                boolean addDepartCheck = addDepart(depart);
                if (!addDepartCheck) {
                    Log.i("Department Adding", "Cannot add department");
                }
            }
            //find and check whether there is the same course exists, if not then add course
            cursor = findCourse(course,section,instructor);
            if(cursor.getCount() <= 0){
                boolean addCourseCheck = addCourse(course, section, instructor);
                if(!addCourseCheck){
                    Log.i("Course Adding", "Cannot add course");
                }
            }

            //populate data for each book with corresponding department and course
            sqLiteDatabase.execSQL("INSERT INTO " + BOOK_TABLE+"("+BOOK_DEPART+","+BOOK_COURSE+","+BOOK_BOOKID+","+BOOK_ISBN+","+BOOK_AUTHOR+","+BOOK_NAME+","+BOOK_NEWPR+","+BOOK_USEDPR+","+BOOK_INBOOKSHELVES+","+BOOK_COMM+")"+
                    "VALUES((SELECT "+DEPART_ID+ " FROM "+DEPART_TABLE+" WHERE "+DEPART_NAME+" = '"+depart+"'"+"),"
                    +"(SELECT "+COURSE_ID+" FROM "+COURSE_TABLE+" WHERE "+COURSE_NAME+" = '"+course+"' AND "+ COURSE_SECTION +" = '"+section+"'),"+Integer.valueOf(id)+",'"+ISBN+"','"+author+"','"+name+"','"+newPrice+"','"+usedPrice+"',"+0+",'"+comment+"')");

            return true;

        }catch (Exception ex){
            ex.getMessage();
            return false;
        }

    }

    void loadBooksList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{

                    loadBook();

                }catch(IOException e){
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    void loadBook() throws IOException {
        final Resources res = context.getResources();
        InputStream inputStream = res.openRawResource(R.raw.books_list);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try{

            String line;
            while((line = reader.readLine()) != null){
                String[] str = TextUtils.split(line, "\"");
                //check if all elements of a row are added properly
                if(str.length < 11){
                    Log.e("Text Line Adding", "Cannot add line: "+ line);
                    continue;
                }

                boolean check = addBook(str[0], str[1], str[2], str[3], str[4], str[5], str[6], str[7], str[8],str[9],str[10]);
                if(!check){
                    Log.i("Book Adding", "Cannot add book "+line);
                }
            }
        }finally {
            reader.close();
        }

    }


    public Cursor findBookByID(int id){
        db = getReadableDatabase();

        String query = "SELECT Author, ISBN, New, Used, Comment, CourseName, Section, InstructorName, Name, DepartmentName FROM BOOK " +
                "JOIN COURSE ON BOOK.CourseID = COURSE.CourseID " +
                "JOIN DEPARTMENT ON BOOK.DepartmentID = DEPARTMENT.DepartmentID " +
                "WHERE _id = "+id;

        return db.rawQuery(query, null);
    }

    public Cursor getNumOfReviews(int id){
        db = getReadableDatabase();

        String query = "SELECT COUNT(*) FROM REVIEW WHERE BookID = " + id;

        return db.rawQuery(query, null);
    }

    public Cursor getRatings(int id){
        db = getReadableDatabase();

        String query = "SELECT AVG(Rate) FROM REVIEW WHERE BookID = " + id;

        return db.rawQuery(query, null);
    }
}

