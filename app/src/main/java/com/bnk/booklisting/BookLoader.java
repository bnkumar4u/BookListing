package com.bnk.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;


class BookLoader extends AsyncTaskLoader<ArrayList<Book>>
{
    /** Tag for log messages */
    private static final String LOG_TAG = BookLoader.class.getName();

    //Query URL
    private String mUrl;

    public BookLoader (Context context,String url)
    {
        super(context);
        mUrl=url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**This is background thread**/
    @Override
    public ArrayList<Book> loadInBackground() {
        ArrayList<Book> books =  QueryUtils.fetchBookData(mUrl);
        return books;
    }
}