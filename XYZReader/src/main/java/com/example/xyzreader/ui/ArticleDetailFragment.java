package com.example.xyzreader.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.adapter.TextAdapter;
import com.example.xyzreader.data.Article;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class ArticleDetailFragment extends Fragment{

    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM = "item";
    private static ArticleDetailFragment fragment;

    private View mRootView;
    private Article article;

    private SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());

    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());

    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);


    public ArticleDetailFragment() {
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public static Fragment newInstance(Article article) {
        if (fragment == null) {
            fragment = new ArticleDetailFragment();
        }
        fragment.setArticle(article);
        return fragment;
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

    private void bindViews() {

        if (mRootView == null) {
            return;
        }



        TextView textByLine = mRootView.findViewById(R.id.text_byline);
        RecyclerView recyclerView = mRootView.findViewById(R.id.recycler_view);
        TextAdapter adapter = new TextAdapter(article.getSplitBody());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager sglm =
                new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(sglm);

        ImageView imageView = mRootView.findViewById(R.id.toolbar_image);

        if (article != null) {

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

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindViews();

    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }
}
