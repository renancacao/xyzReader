package com.example.xyzreader.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.Article;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss", Locale.getDefault());
    private SimpleDateFormat outputFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    private String TAG = "ListAdapter";

    private ArrayList<Article> articles;
    private ListAdapterListener listener;

    public ListAdapter(ArrayList<Article> articles, ListAdapterListener listener) {
        this.articles = articles;
        this.listener = listener;
    }

    @Override
    public long getItemId(int position) {
        return articles.get(position).getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);

        final ViewHolder vh = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });
        return vh;
    }

    private Date parsePublishedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Article article = articles.get(position);

        holder.titleView.setText(article.getTitle());

        Date publishedDate = parsePublishedDate(article.getPublishDate());

        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            holder.subtitleView.setText(
                    String.format("%s by %s", DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString(),
                            article.getAuthor()));
        } else {
            holder.subtitleView.setText(
                    String.format("%s by %s", outputFormat.format(publishedDate),
                            article.getAuthor()));
        }

        Picasso.get().load(article.getThumb()).into(holder.thumbnailView);
    }

    public interface ListAdapterListener {
        void onItemClick(int pos);
    }

    @Override
    public int getItemCount() {

        return articles == null ? 0 : articles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView thumbnailView;
        TextView titleView;
        TextView subtitleView;

        ViewHolder(View view) {
            super(view);
            thumbnailView = view.findViewById(R.id.thumbnail);
            titleView = view.findViewById(R.id.article_title);
            subtitleView = view.findViewById(R.id.article_subtitle);

            subtitleView.setOnClickListener(this);
            titleView.setOnClickListener(this);
            thumbnailView.setOnClickListener(this);
            view.setOnClickListener(this);

           }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());

        }
    }

}
