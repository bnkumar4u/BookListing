package com.bnk.booklisting;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


class BookAdapter extends ArrayAdapter{

    public BookAdapter(Activity c, List<Book> books)
    {
        super(c,0,books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View listItemView=convertView;
        if (listItemView==null)
        {
            listItemView= LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item,parent,false);

        }
        Book currentBook=(Book)getItem(position);

        ImageView iv=listItemView.findViewById(R.id.book_image);
        iv.setImageBitmap(currentBook.getBitmap());

        TextView t1=listItemView.findViewById(R.id.book_title);
        t1.setText(currentBook.getTitle());

        t1=listItemView.findViewById(R.id.book_author);
        t1.setText(currentBook.getAuthor());

        t1=listItemView.findViewById(R.id.book_des);
        t1.setText(currentBook.getDescription());


        return listItemView;
    }
}
