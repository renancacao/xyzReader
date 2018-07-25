package com.example.xyzreader.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

@SuppressWarnings("FieldCanBeLocal")
public class ArticlesLoader extends AsyncTaskLoader<ArrayList<Article>> {

    public static final String ARG_LOAD_BODY = "loadbody";
    private final Bundle args;
    private ArrayList<Article> articles;

    private final String _ID = ItemsContract.Items._ID;
    private final String TITLE = ItemsContract.Items.TITLE;
    private final String PUBLISHED_DATE = ItemsContract.Items.PUBLISHED_DATE;
    private final String AUTHOR = ItemsContract.Items.AUTHOR;
    private final String THUMB_URL = ItemsContract.Items.THUMB_URL;
    private final String BODY = ItemsContract.Items.BODY;
    private final String PHOTO = ItemsContract.Items.PHOTO_URL;

    private final String[] PROJ = {
            _ID,
            TITLE,
            PUBLISHED_DATE,
            AUTHOR,
            THUMB_URL,
            BODY,
            PHOTO};

    private final String[] PROJ_NO_BODY = {
            _ID,
            TITLE,
            PUBLISHED_DATE,
            AUTHOR,
            THUMB_URL,
            PHOTO};



    public ArticlesLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;
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

        boolean loadBody = args.getBoolean(ARG_LOAD_BODY);

        Cursor c;

        if(loadBody){
            c = getContext().getContentResolver().query(ItemsContract.Items.buildDirUri(), PROJ,
                  null,
                  null,
                  ItemsContract.Items.DEFAULT_SORT);
        }
        else{
            c = getContext().getContentResolver().query(ItemsContract.Items.buildDirUri(), PROJ_NO_BODY,
                    null,
                    null,
                    ItemsContract.Items.DEFAULT_SORT);
        }


        if (c != null) {
            if (c.moveToFirst()) {
                articles = new ArrayList<>();
                do {

                    Article art = new Article();
                    art.setId(c.getInt(c.getColumnIndex(_ID)));
                    art.setAuthor(c.getString(c.getColumnIndex(AUTHOR)));
                    art.setPublishDate(c.getString(c.getColumnIndex(PUBLISHED_DATE)));
                    art.setThumb(c.getString(c.getColumnIndex(THUMB_URL)));
                    art.setTitle(c.getString(c.getColumnIndex(TITLE)));
                    art.setAuthor(c.getString(c.getColumnIndex(AUTHOR)));
                    art.setPhoto(c.getString(c.getColumnIndex(PHOTO)));

                    if (loadBody){
                        art.setBody(c.getString(c.getColumnIndex(BODY)));
                    }

                    articles.add(art);
                } while (c.moveToNext());
            }

            c.close();

        }

        return articles;
    }


}
