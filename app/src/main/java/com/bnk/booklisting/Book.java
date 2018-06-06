package com.bnk.booklisting;


import android.graphics.Bitmap;

public class Book
{
    private String title;
    private String author;
    private String description;
    private Bitmap bitmap=null;
    private String buyLink="";

    public Book(String title, String author, String description, Bitmap bitmap,String buyLink) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.bitmap = bitmap;
        this.buyLink=buyLink;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getBuyLink() {
        return buyLink;
    }
}
