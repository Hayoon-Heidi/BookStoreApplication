package com.example.douglasbookstore;

import android.util.Log;

public class Book {

    //15 fields. 14 from the database + 1 modified
    private int bookID;

    private String ISBN;

    private String authorName;

    private String bookName;

    private int depID;
    private String depCode;
    private String depName;

    private int courseID;
    private String courseName;
    private String section;
    private String instructorName;

    private String comment;
    private String newPrice;
    private String usedPrice;
    private int inBookShelves;

    public Book(){
        bookID = -1;
        ISBN = "";
        authorName = "";
        bookName = "";
        depID = -1;
        courseID = -1;
        comment = "";
        newPrice = "";
        usedPrice = "";
        inBookShelves = -1;
        depName = "";
        courseName = "";
        section = "";
        instructorName = "";
        depCode = "";
    }

    public Book(int bookID, String ISBN, String authorName, String bookName, int depID, int courseID, String comment, String newPrice, String usedPrice, int inBookShelves, String depName, String section, String instructorName, String depCode) {
        this.bookID = bookID;
        this.ISBN = ISBN;
        this.authorName = authorName;
        this.bookName = bookName;
        this.depID = depID;
        this.courseID = courseID;
        this.comment = comment;
        this.newPrice = newPrice;
        this.usedPrice = usedPrice;
        this.inBookShelves = inBookShelves;
        this.depName = depName;
        this.section = section;
        this.instructorName = instructorName;
        this.depCode = depCode;
    }


    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getDepID() {
        return depID;
    }

    public void setDepID(int depID) {
        this.depID = depID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getUsedPrice() {
        return usedPrice;
    }

    public void setUsedPrice(String usedPrice) {
        this.usedPrice = usedPrice;
    }

    public int getInBookShelves() {
        return inBookShelves;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public  boolean getInBookShelvesBooleanValue() {
        if (inBookShelves == 0){
            return false;
        }else if (inBookShelves == 1){
            return true;
        } else {
            inBookShelves = 0;
            Log.e("Book.class", "setInBookShelves out of bound, is set to 0 by getter.");
            return false;
        }

    }


    public void setInBookShelves(boolean val){
        if(val == false){
            inBookShelves = 0;
        } else if(val == true){
            inBookShelves = 1;
        }
    }

}
