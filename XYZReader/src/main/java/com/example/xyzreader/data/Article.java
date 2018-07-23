package com.example.xyzreader.data;

import android.text.Html;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Article {

    private int id;
    private String title;
    private String publishDate;
    private String author;
    private String thumb;
    private String photo;
    private double aspectRatio;
    private String body;

    Article() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = Html.fromHtml(body).toString();
    }

    public List<String> getSplitBody(){

        TextUtils.SimpleStringSplitter splitter=new TextUtils.SimpleStringSplitter('.');
        splitter.setString(body);

        List<String> list=new ArrayList<>();
        while(splitter.hasNext()) {
            StringBuilder builder = new StringBuilder();
            for(int index=0;index<12;index++){
                builder.append(splitter.next());
                builder.append('.');
                if(!splitter.hasNext()) break;
            }
            list.add(builder.toString());
        }

        return list;
    }

}
