package com.example.xyzreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.adapter.ListAdapter;
import com.example.xyzreader.data.Article;
import com.example.xyzreader.data.ArticlesLoader;
import com.example.xyzreader.data.UpdaterService;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.xyzreader.data.ArticlesLoader.ARG_LOAD_BODY;

public class ArticleListActivity extends AppCompatActivity implements
        LoaderCallbacks<ArrayList<Article>>, ListAdapter.ListAdapterListener {

    private static final String ARG_POSITION = "position";
    private static final String ARG_ARTICLE_INDEX = "index";


    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private ArrayList<Article> articles = null;
    private boolean isloading = false;
    private Animation animation = null;
    private int serviceResponse;
    private Snackbar snack = null;
    private int newcontent=0;
    private ListAdapter adapter;
    private GridLayoutManager sglm;
    private int index = -1;
    private int scrollItem = -1;

    private String date = "dateid";
    private Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN );

        progressBar.bringToFront();

        mRecyclerView = findViewById(R.id.recycler_view);

        articles = new ArrayList<>();

        adapter = new ListAdapter(articles, this);
        adapter.setHasStableIds(true);

        mRecyclerView.setAdapter(adapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        sglm = new GridLayoutManager( this, columnCount);
        mRecyclerView.setLayoutManager(sglm);

        ImageView mLoadingImage = findViewById(R.id.loading_image);

        setImageRefreshListener(mLoadingImage);

        args = new Bundle();
        args.putBoolean(ARG_LOAD_BODY, getResources().getBoolean(R.bool.load_body));

        loadArticles();

        if (savedInstanceState == null) {
            refresh();
        }
        else{
            index = savedInstanceState.getInt(ARG_ARTICLE_INDEX);
            scrollItem = savedInstanceState.getInt(ARG_POSITION);
        }
    }

    private void setImageRefreshListener(ImageView mLoadingImage) {
        mLoadingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isloading){
                    view.startAnimation(getAnimation());
                    refresh();
                }
            }
        });
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
        date = new Date().toString();

        Intent intent = new Intent(this, UpdaterService.class);
        intent.putExtra(UpdaterService.EXTRA_DATE, date);

        startService(intent);
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


    private final BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                if (intent.hasExtra(UpdaterService.EXTRA_DATE) &&
                        intent.getStringExtra(UpdaterService.EXTRA_DATE).equals(date)) {
                    if (intent.hasExtra(UpdaterService.EXTRA_STATUS)) {
                        serviceResponse = intent.getIntExtra(UpdaterService.EXTRA_STATUS, -1);
                        intent.setAction("used");
                        updateRefreshingUI();
                    }
                }
            }
        }
    };



    private void updateRefreshingUI() {

        if (!isloading){return;}

        switch (serviceResponse){
            case UpdaterService.ERROR:
                progressBar.setVisibility(View.INVISIBLE);
                showSnack(getString(R.string.error_connecting), Snackbar.LENGTH_SHORT);
                isloading=false;
                break;

            case UpdaterService.NOTHING:
                progressBar.setVisibility(View.INVISIBLE);
                showSnack(getString(R.string.content_updated), Snackbar.LENGTH_SHORT);
                isloading=false;
                break;

            case UpdaterService.NEW_CONTET:
                progressBar.setVisibility(View.VISIBLE);
                newcontent++;
                snack.setText(String.format(Locale.getDefault(),"%s %d %s",
                        getString(R.string.processing), newcontent, getString(R.string.stories)));
                snack.setDuration(Snackbar.LENGTH_INDEFINITE);
                snack.show();
                break;

            case UpdaterService.START:
                progressBar.setVisibility(View.VISIBLE);
                showSnack(getString(R.string.connecting), Snackbar.LENGTH_LONG);
                isloading=true;
                newcontent = 0;
                break;

            case UpdaterService.RELOAD:
                showSnack(getString(R.string.content_updated), Snackbar.LENGTH_SHORT);
                loadArticles();
                isloading=false;
                break;
        }

    }

    private Snackbar newSnack() {
        Snackbar n = Snackbar.make(mRecyclerView,"",Snackbar.LENGTH_INDEFINITE);

        View v = n.getView();
        v.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        TextView t = v.findViewById(android.support.design.R.id.snackbar_text);
        t.setTextColor(ContextCompat.getColor(this,R.color.white));

        return n;
    }

    private void showSnack(String s, int lengthShort) {

        if(snack==null){
            snack = newSnack();
        }

        snack.setText(s);
        snack.setDuration(lengthShort);
        snack.show();
    }

    private void loadArticles() {
        getSupportLoaderManager().restartLoader(0, args, this);
    }


    @NonNull
    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ArticlesLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Article>> loader, ArrayList<Article> data) {

        articles = data;
        adapter.setArticles(articles);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);

        if (scrollItem> -1){
            if (adapter.getItemCount()>scrollItem){
                sglm.scrollToPositionWithOffset(scrollItem,0);
            }
            scrollItem = -1;
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Article>> loader) {
        mRecyclerView.setAdapter(null);
    }


    @Override
    public void onItemClick(int pos) {

        if (getResources().getBoolean(R.bool.twoPanel)){

            FragmentManager fragmentManager = getSupportFragmentManager();
            ArticleDetailFragment fragment = (ArticleDetailFragment) fragmentManager.findFragmentById(R.id.fragment_content);
            fragment.setArticle(articles.get(pos));

        }
        else{

            Intent intent = new Intent(this, ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.EXTRA_ID,articles.get(pos).getId());
            startActivity(intent);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (sglm.findFirstCompletelyVisibleItemPosition()!=-1){
            outState.putInt(ARG_POSITION, sglm.findFirstCompletelyVisibleItemPosition());
        }
        else{
            outState.putInt(ARG_POSITION, sglm.findFirstVisibleItemPosition());
        }

        outState.putInt(ARG_ARTICLE_INDEX, index);

        super.onSaveInstanceState(outState);
    }
}
