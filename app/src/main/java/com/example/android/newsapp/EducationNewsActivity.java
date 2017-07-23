package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EducationNewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<EducationNews>> {

    // Constant value for the log messages
    private static final String LOG_TAG = EducationNewsActivity.class.getName();

    // The Guardian Base URL
    private static final String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search?&=";

    // Constant value for the news loader ID. We can choose any integer.
    private static final int NEWS_LOADER_ID = 1;

    // Constant for the API search Key
    private static final String API_KEY = "api-key";

    // Constant value for the API Key
    private static final String KEY = "59d0bf63-14c5-4f77-b80d-cbd12411469f";

    // Adapter for the Education News
    private EducationNewsAdapter newsAdapter;

    // TextView that is displayed for the empty state view
    private TextView emptyStateTextView;

    // ProgressBar that is displayed when the application is searching for news
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_news);

        // Find a reference to the ListView in the layout
        ListView newsListView = (ListView) findViewById(R.id.list_view);

        // Set empty state TextView on the ListView with news when no data can be found
        emptyStateTextView = (TextView) findViewById(R.id.empty_text_view);
        newsListView.setEmptyView(emptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        newsAdapter = new EducationNewsAdapter(this, new ArrayList<EducationNews>());

        // Set the adapter on the ListView
        // so the list can be populated in the user interface
        newsListView.setAdapter(newsAdapter);

        // Find a reference to the progress bar
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected news.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current Education News that was clicked on
                EducationNews currentEducationNews = newsAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri educationNewsUri = Uri.parse(currentEducationNews.getUrl());

                // Create a new intent to view the Education News URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, educationNewsUri);
                websiteIntent.setData(educationNewsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.e(LOG_TAG, "There is an internet connection.");
            // Get a reference to the LoaderManager, in order to interact with loaders.
               LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
                loaderManager.initLoader(NEWS_LOADER_ID , null, this);
            } else {
                Log.e(LOG_TAG, "There is no internet connection.");
                // Otherwise, display error
                // First, hide loading indicator so error message will be visible
                View progressBar = findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.GONE);

                // Update empty state with no connection error message
                emptyStateTextView.setText(R.string.no_internet_connection);
                // Show the empty state with no connection error message
                emptyStateTextView.setVisibility(View.VISIBLE);
            }
        }

    @Override
    public Loader<List<EducationNews>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String searchSection = sharedPreferences.getString(
                getString(R.string.settings_search_by_news_key),
                getString(R.string.settings_news_label));

        // Create an URI and an URI Builder
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append the search parameters to the request URL
        uriBuilder.appendQueryParameter("q", searchSection);
        uriBuilder.appendQueryParameter(API_KEY, KEY);

        // Create a NewsLoader with the request URL
        return new EducationNewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<EducationNews>> loader, List<EducationNews> educationNews) {
        // If the load has finished, hide the ProgressBar because the data has been loaded
        View progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        // Clear the adapter of previous news data
        newsAdapter.clear();

        // If no news can be found
        if (educationNews.isEmpty()){
            // Set empty state text view to display "No news found."
            emptyStateTextView.setText(R.string.no_news);

        } else {
            // If there is a valid list of news, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            newsAdapter.addAll(educationNews);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EducationNews>> loader) {
        // Loader reset, so we can clear out our existing data from the adapter.
        newsAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Open the Settings page through an Intent when the Settings icon is clicked or tapped on
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Restart the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).//
        loaderManager.restartLoader(NEWS_LOADER_ID , null, this);
    }
}
