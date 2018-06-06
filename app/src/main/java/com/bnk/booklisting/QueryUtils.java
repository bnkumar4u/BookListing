package com.bnk.booklisting;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.bnk.booklisting.BookListingActivity.LOG_TAG;

final class QueryUtils
{
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils(){}

    private static ArrayList<Book> extractFeatureFromJson(String booksJSON)
    {
        //if JSON string is empty or null,then return early.
        if(TextUtils.isEmpty(booksJSON))
        {
            return null;
        }
        //creates empty array list to hold books.
        ArrayList<Book> books=new ArrayList<>();
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject baseJsonResponse=new JSONObject(booksJSON);

            JSONArray itemsArray =baseJsonResponse.getJSONArray("items");
            for(int i=0;i<itemsArray.length();i++)
            {
                JSONObject item=itemsArray.getJSONObject(i);
                JSONObject volumeInfo=item.getJSONObject("volumeInfo");
                String title=volumeInfo.getString("title");

                String description=null;
                if(volumeInfo.has("description"))
                    description=volumeInfo.getString("description");


                String author_names="";
                if(volumeInfo.has("authors"))
                {
                    JSONArray authors=volumeInfo.getJSONArray("authors");
                    for(int j=0;j<authors.length();j++)
                    {
                        author_names=author_names+","+authors.getString(j);
                    }
                    author_names=author_names.replaceFirst(",","");
                }

                JSONObject imageLinks=volumeInfo.getJSONObject("imageLinks");
                String image_url=imageLinks.getString("smallThumbnail");

                Bitmap bitmap=getBitmapFromURL(image_url);

                //to-do
                //Bitmap request from image_url

                JSONObject saleInfo=item.getJSONObject("saleInfo");

                String buyLink="";
                if(saleInfo.has("buyLink"))
                {
                    buyLink=saleInfo.getString("buyLink");
                }
                books.add(new Book(title,author_names,description,bitmap,buyLink));
            }


        }catch (JSONException e)
        {
            Log.e("QueryUtils","Problem parsing the books Json results",e);
        }

        return books;
    }

    private static Bitmap getBitmapFromURL(String image_url)
    {
        try {
            URL url=new URL(image_url);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input=connection.getInputStream();

            Bitmap bm= BitmapFactory.decodeStream(input);
            return bm;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Book> fetchBookData(String requestUrl)
    {


        //create url object
        URL url=createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse=null;
        try {
            jsonResponse =makeHttpRequest(url);
        }catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);

        }
        ArrayList<Book> books=extractFeatureFromJson(jsonResponse);

        return books;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        //// If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        try {
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000);
            urlConnection.setConnectTimeout(60000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            int x=urlConnection.getResponseCode();
            if (urlConnection.getResponseCode() == 200) {
                inputStream=urlConnection.getInputStream();
                jsonResponse=readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e){
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        }finally {
            if(urlConnection!=null)
                urlConnection.disconnect();
            if(inputStream!=null)
                inputStream.close();
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream!=null){
            InputStreamReader inputStremReader = new InputStreamReader(inputStream);
            BufferedReader reader= new BufferedReader(inputStremReader);
            String line=reader.readLine();
            while (line!=null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static URL createUrl(String requestUrl)
    {
        URL url=null;
        try{
            url=new URL(requestUrl);
        }catch (MalformedURLException e){
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }
}
