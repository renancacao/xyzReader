package com.example.xyzreader.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private boolean isloading = false;
    private Animation animation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        progressBar.bringToFront();

        mRecyclerView = findViewById(R.id.recycler_view);

        ImageView mLoadingImage = findViewById(R.id.loading_image);
        mLoadingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isloading){
                    view.startAnimation(getAnimation());
                    refresh();
                }
            }
        });

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private Animation getAnimation() {
        if (animation==null){
            animation = new RotateAnimation(0, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            animation.setStartOffset(0);
            animation.setDuration(1000);
            animation.setFillAfter(true);
            animation.setInterpolator(this, android.R.anim.decelerate_interpolator);
        }

        return animation;
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
        isloading=true;
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

    private int serviceResponse;

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                if (intent.hasExtra(UpdaterService.EXTRA_STATUS)) {
                    serviceResponse = intent.getIntExtra(UpdaterService.EXTRA_STATUS, -1);
                    updateRefreshingUI();
                }
            }
        }
    };

    private Snackbar snack = null;
    private int newcontent=0;
    @SuppressLint("ShowToast")
    private void updateRefreshingUI() {

        if (!isloading) return;

        if(snack==null){
            snack = Snackbar.make(mRecyclerView,"",Snackbar.LENGTH_INDEFINITE);

            View v = snack.getView();
            v.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
            TextView t = v.findViewById(android.support.design.R.id.snackbar_text);
            t.setTextColor(ContextCompat.getColor(this,R.color.white));
        }

        switch (serviceResponse){
            case UpdaterService.ERROR:
                progressBar.setVisibility(View.INVISIBLE);
                snack.setText("Error connecting to server");
                snack.setDuration(Snackbar.LENGTH_SHORT);
                snack.show();
                isloading=false;
                break;

            case UpdaterService.NOTHING:
                progressBar.setVisibility(View.INVISIBLE);
                snack.setText("Updated content");
                snack.setDuration(Snackbar.LENGTH_SHORT);
                snack.show();
                getSupportLoaderManager().restartLoader(0, null, this);
                isloading=false;
                break;

            case UpdaterService.NEW_CONTET:
                progressBar.setVisibility(View.VISIBLE);
                newcontent++;
                snack.setText("Processing: " + newcontent + " storie(s)");
                snack.setDuration(Snackbar.LENGTH_INDEFINITE);
                snack.show();
                break;

            case UpdaterService.START:
                progressBar.setVisibility(View.VISIBLE);
                snack.setText("Connecting to the server");
                snack.setDuration(Snackbar.LENGTH_INDEFINITE);
                snack.show();
                isloading=true;
                break;

            case UpdaterService.RELOAD:
                snack.setText("Updated content");
                snack.setDuration(Snackbar.LENGTH_SHORT);
                snack.show();
                getSupportLoaderManager().restartLoader(0, null, this);
                isloading=false;
                break;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
