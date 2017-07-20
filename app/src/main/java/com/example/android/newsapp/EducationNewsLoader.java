package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class EducationNewsLoader extends AsyncTaskLoader<List<EducationNews>> {

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new EducationNewsLoader.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public EducationNewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<EducationNews> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of news.
        List<EducationNews> educationNews = Utils.fetchEducationNewsData(mUrl);
        return educationNews;
    }

}
