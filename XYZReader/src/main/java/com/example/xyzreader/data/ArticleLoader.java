package com.example.xyzreader.data;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.example.xyzreader.ui.ArticleDetailActivity;

@SuppressWarnings("FieldCanBeLocal")
public class ArticleLoader extends AsyncTaskLoader<Article> {

    private final Bundle args;
    private Article article = null;

    private final String[] PROJECTION = {
            ItemsContract.Items._ID,
            ItemsContract.Items.TITLE,
            ItemsContract.Items.PUBLISHED_DATE,
            ItemsContract.Items.AUTHOR,
            ItemsContract.Items.PHOTO_URL,
            ItemsContract.Items.BODY};

    private final int _ID = 0;
    private final int TITLE = 1;
    private final int PUBLISHED_DATE = 2;
    private final int AUTHOR = 3;
    private final int PHOTO_URL = 4;
    private final int BODY = 5;

    public ArticleLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        if(article == null){
            forceLoad();
        }
        else {
            deliverResult(article);
        }
    }

    @Override
    public Article loadInBackground() {

        int id;

        if(args != null && args.containsKey(ArticleDetailActivity.EXTRA_ID)){
            id = args.getInt(ArticleDetailActivity.EXTRA_ID);
        }
        else{
            return null;
        }

        article = null;


        Cursor c = getContext().getContentResolver().query(ItemsContract.Items.buildDirUri(),PROJECTION,
                "_ID=?",
                new String[]{String.valueOf(id)},
                ItemsContract.Items.DEFAULT_SORT);

        if (c != null) {
            if (c.moveToFirst()) {
                article = new Article();

                article.setId(c.getInt(_ID));
                article.setAuthor(c.getString(AUTHOR));
                article.setBody(c.getString(BODY));
                article.setPhoto(c.getString(PHOTO_URL));
                article.setPublishDate(c.getString(PUBLISHED_DATE));
                article.setTitle(c.getString(TITLE));
                article.setAuthor(c.getString(AUTHOR));
            }

            c.close();

        }

        return article;
    }


}
