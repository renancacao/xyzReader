package com.example.xyzreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.xyzreader.R;
import com.example.xyzreader.adapter.ListAdapter;
import com.example.xyzreader.data.Article;
import com.example.xyzreader.data.ArticlesLoader;
import com.example.xyzreader.data.UpdaterService;

import java.util.ArrayList;

public class ArticleListActivity extends AppCompatActivity implements
        LoaderCallbacks<ArrayList<Article>>, ListAdapter.ListAdapterListener {

    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private ArrayList<Article> articles = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        mRecyclerView = findViewById(R.id.recycler_view);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);
    }

    private boolean mIsRefreshing = false;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                if (intent.hasExtra(UpdaterService.EXTRA_REFRESHING)) {
                    mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                    updateRefreshingUI();
                }
            }
        }
    };

    private void updateRefreshingUI() {
        if (mIsRefreshing) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            getSupportLoaderManager().restartLoader(0, null, this);
        }

    }


    @NonNull
    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ArticlesLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Article>> loader, ArrayList<Article> data) {

        articles = data;

        ListAdapter adapter = new ListAdapter(data, this);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        int columnCount = getResources().getInteger(R.integer.list_column_count);
        GridLayoutManager sglm =
                new GridLayoutManager( this, columnCount);
        mRecyclerView.setLayoutManager(sglm);

        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Article>> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public void onItemClick(int pos) {

        if (getResources().getBoolean(R.bool.twoPanel)){

        }
        else{
            Intent intent = new Intent(this, ArticleDetailActivity.class);
            //intent.putParcelableArrayListExtra(ArticleDetailActivity.EXTRA_ARTICLES,articles);
            intent.putExtra(ArticleDetailActivity.EXTRA_ID,articles.get(pos).getId());
            startActivity(intent);

        }

    }
}
