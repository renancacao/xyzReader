package com.example.xyzreader.data;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class ArticlesLoader extends AsyncTaskLoader<ArrayList<Article>> {

    private ArrayList<Article> articles;

    private String[] PROJECTION = {
            ItemsContract.Items._ID,
            ItemsContract.Items.TITLE,
            ItemsContract.Items.PUBLISHED_DATE,
            ItemsContract.Items.AUTHOR,
            ItemsContract.Items.THUMB_URL};

    private int _ID = 0;
    private int TITLE = 1;
    private int PUBLISHED_DATE = 2;
    private int AUTHOR = 3;
    private int THUMB_URL = 4;

    public ArticlesLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if(articles == null){
            forceLoad();
        }
        else {
            deliverResult(articles);
        }
    }

    @Override
    public ArrayList<Article> loadInBackground() {

        articles = null;

        Cursor c = getContext().getContentResolver().query(ItemsContract.Items.buildDirUri(),PROJECTION,
                null,
                null,
                ItemsContract.Items.DEFAULT_SORT);

        if (c != null) {
            if (c.moveToFirst()) {
                articles = new ArrayList<>();
                do {

                    Article art = new Article();
                    art.setId(c.getInt(_ID));
                    art.setAuthor(c.getString(AUTHOR));
                    art.setPublishDate(c.getString(PUBLISHED_DATE));
                    art.setThumb(c.getString(THUMB_URL));
                    art.setTitle(c.getString(TITLE));
                    art.setAuthor(c.getString(AUTHOR));
                    articles.add(art);
                } while (c.moveToNext());
            }

            c.close();

        }

        return articles;
    }


}
