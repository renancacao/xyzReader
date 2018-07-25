package com.example.xyzreader.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.adapter.TextAdapter;
import com.example.xyzreader.data.Article;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;


public class ArticleDetailFragment extends Fragment{

    private static final String TAG = "ArticleDetailFragment";
    private static final String ARG_POSITION = "position";

    private View mRootView;
    private Article article;

    private ImageView imageView;
    private FloatingActionButton fab;
    private TextView textTitle;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());

    private final SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
    private final GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    private final Target target = createTargetCallback();
    private LinearLayoutManager sglm;
    private int scrollItem =-1;


    private Target createTargetCallback() {

       return new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Palette p = Palette.from(bitmap).generate();
                    Window window = Objects.requireNonNull(getActivity()).getWindow();
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


                    window.setStatusBarColor(
                            p.getDominantColor(ContextCompat.getColor(getActivity(),R.color.deep_orange_900)));

                    textTitle.setTextColor(
                            p.getDarkVibrantColor(ContextCompat.getColor(getActivity(),R.color.gray_700)));

                }
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

    }


    public ArticleDetailFragment() {
    }

    public void setArticle(Article article) {
        this.article = article;
        bindViews();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        sglm = new LinearLayoutManager(getContext());

        if (savedInstanceState!=null){
            scrollItem = savedInstanceState.getInt(ARG_POSITION);
        }

        return mRootView;
    }

     private Date parsePublishedDate() {
        try {
            String date = article.getPublishDate();
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    private void bindViews() {

        if (mRootView == null) {
            return;
        }

        AppBarLayout mAppBarLayout = mRootView.findViewById(R.id.appBarLayout);
        mAppBarLayout.setExpanded(true);
        TextView textByLine = mRootView.findViewById(R.id.text_byline);
        textTitle = mRootView.findViewById(R.id.text_title);
        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        fab = mRootView.findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.VISIBLE);
        addFabClickListener();

        recyclerView.setLayoutManager(sglm);

        imageView = mRootView.findViewById(R.id.toolbar_image);

        if (article != null) {

            TextAdapter adapter = new TextAdapter(article.getSplitBody());
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

            if (scrollItem!=-1) {
                sglm.scrollToPositionWithOffset(scrollItem, 0);
                scrollItem = -1;
            }

            textTitle.setText(article.getTitle());

            Date publishedDate = parsePublishedDate();

            if (!publishedDate.before(START_OF_EPOCH.getTime())) {
                textByLine.setText(
                        String.format("%s by %s", DateUtils.getRelativeTimeSpanString(
                                publishedDate.getTime(),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString(),
                                article.getAuthor()));

            } else {
                textByLine.setText(
                        String.format("%s by %s", outputFormat.format(publishedDate),
                                article.getAuthor()));

            }

            Picasso.get().load(article.getPhoto()).into(imageView);
            Picasso.get().load(article.getPhoto()).into(target);


        }
    }

    private void addFabClickListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (article != null){
                    String title = article.getTitle();
                    String body = article.getTitle() + "\n\n" + article.getBody();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                }
            }
        });
    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.get().cancelRequest(target);
        Picasso.get().cancelRequest(imageView);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        if (sglm.findFirstCompletelyVisibleItemPosition()!=-1){
            outState.putInt(ARG_POSITION, sglm.findFirstCompletelyVisibleItemPosition());
        }
        else{
            outState.putInt(ARG_POSITION, sglm.findLastVisibleItemPosition());
        }

        super.onSaveInstanceState(outState);
    }
}
