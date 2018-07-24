package com.example.xyzreader.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.example.xyzreader.R;
import com.example.xyzreader.data.Article;
import com.example.xyzreader.data.ArticleLoader;


public class ArticleDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Article> {



    public static String EXTRA_ID = "id";
    public static String EXTRA_NEXT_ID = "next_id";

    private int id;
    private int next_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);

        if (getIntent() != null){

            id = getIntent().getIntExtra(EXTRA_ID,0);
            next_id  = getIntent().getIntExtra(EXTRA_NEXT_ID,-1);

            //TODO: pegar o scroll quando rotacionado
        }

        Bundle args = new Bundle();
        args.putInt(EXTRA_ID,id);
        getSupportLoaderManager().restartLoader(0, args, this);

    }

    @NonNull
    @Override
    public Loader<Article> onCreateLoader(int id, @Nullable Bundle args) {
        return new ArticleLoader(this,args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Article> loader, Article data) {

        if (data != null) {
            setContent(data);
        }
    }

    private void setContent(Article article) {

        //TODO: fazer aqui o carregamento estatico
        FragmentManager fragmentManager = getSupportFragmentManager();
        ArticleDetailFragment fragment = (ArticleDetailFragment) fragmentManager.findFragmentById(R.id.fragment_content);
        fragment.setArticle(article);

    }


    @Override
    public void onLoaderReset(@NonNull Loader<Article> loader) {
    }

}
