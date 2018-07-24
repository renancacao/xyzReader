package com.example.xyzreader.ui;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    private View mRootView;
    private Article article;

    private ImageView imageView;
    private FloatingActionButton fab;
    private TextView textTitle;
    private TextView textByLine;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());

    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());

    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    private Target target = new Target() {
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

    public ArticleDetailFragment() {
    }

    public void setArticle(Article article) {
        this.article = article;
        bindViews();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);

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

    public void bindViews() {

        if (mRootView == null) {
            return;
        }



        textByLine = mRootView.findViewById(R.id.text_byline);
        textTitle = mRootView.findViewById(R.id.text_title);
        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        fab = mRootView.findViewById(R.id.floatingActionButton);

        LinearLayoutManager sglm =
                new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(sglm);

        imageView = mRootView.findViewById(R.id.toolbar_image);

        if (article != null) {

            TextAdapter adapter = new TextAdapter(article.getSplitBody());
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

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

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.get().cancelRequest(target);
        Picasso.get().cancelRequest(imageView);
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }
}
