package com.bnk.booklisting;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookListingActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Book>> {

    private BookAdapter bookAdapter;
    private TextView mEmptyStateTextView;
    private EditText editText;

    public static final String LOG_TAG=BookListingActivity.class.getName();

    private static int BOOK_LOADER_ID=1;
    private static String query=null;
    private String mBookRequestUrl="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_listing);

        List<Book> books=new ArrayList<Book>();

        editText=(EditText)findViewById(R.id.edit_search_query);


       // ArrayAdapter<Book> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,books);
        bookAdapter =new BookAdapter(this,new ArrayList<Book>());
        ListView list= (ListView)findViewById(R.id.list);


        mEmptyStateTextView=(TextView)findViewById(R.id.empty_view);
        list.setEmptyView(mEmptyStateTextView);

       // list.setAdapter(adapter);

        list.setAdapter(bookAdapter);

        ConnectivityManager connMgr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //get details on currently active default data network
        NetworkInfo networkInfo=connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if(networkInfo!=null && networkInfo.isConnected())
        {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager=getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID,null,this);
        }
        else
        {
            //otherwise display error
            //first ,hide loading indicator so error message will be visible
            View loadingIndicator=findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internetconnection);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book currentBook=(Book)bookAdapter.getItem(position);

                try {
                    Uri uri= Uri.parse(currentBook.getBuyLink());
                    Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                    startActivity(intent);
                }catch (ActivityNotFoundException e)
                {
                    e.printStackTrace();
                    Toast.makeText(BookListingActivity.this,"This book not available",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        //Create a new loader for the given URL
        return new BookLoader(this,mBookRequestUrl);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {

        ProgressBar progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_books);


        bookAdapter.clear();

        if(books!=null && !books.isEmpty())
        {
            bookAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {

        bookAdapter.clear();
    }

    public void searchQuery(View view)
    {
        String newQuery=editText.getText().toString();
        if(!newQuery.equals(query))
        {
            query=newQuery.replaceAll("\\s","+");
            mBookRequestUrl="https://www.googleapis.com/books/v1/volumes?q="+query+"&maxResults=5&key=AIzaSyAonAQdun9GjpYKVUj_6cVQQytaKaHS5pA";
            BOOK_LOADER_ID++;
        }

        ConnectivityManager connMgr=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        //get details on currently active default data network
        NetworkInfo networkInfo=connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if(networkInfo!=null && networkInfo.isConnected())
        {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager=getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID,null,this);
        }
        else
        {
            //otherwise display error
            //first ,hide loading indicator so error message will be visible
            View loadingIndicator=findViewById(R.id.progress_bar);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internetconnection);
        }
    }
}
