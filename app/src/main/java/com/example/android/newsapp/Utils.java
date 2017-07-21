package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving news data from The Guardian.
 */
public class Utils {

    // Tag for the log messages
    private static final String LOG_TAG = Utils.class.getSimpleName();

    // Key used for the JSON response
    static final String RESPONSE = "response";

    // Key used for the results with the list of news
    static final String RESULTS = "results";

    // Key used for the title of the news
    static final String NEWS_TITLE = "newsTitle";

    // Key used for the section of the news
    static final String SECTION = "section";

    // Key used for the date when the news was published
    static final String DATE = "date";

    // Key used for the URL of the news
    static final String NEWS_URL = "url";

    /**
     * Create a private constructor.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name Utils (and an object instance of Utils is not needed).
     */
    private Utils() {
    }

    /**
     * Query the Guardian dataset and return a list of EducationNews objects.
     */
    public static List<EducationNews> fetchEducationNewsData(String requestUrl) {
        // Create the URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            // Try to create a HTTP request with the request URL by using the makeHttpRequest method to get the data
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // If the request fails, print the error message to the Log
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of Education News
        List<EducationNews> educationNews = extractFeatureFromJson(jsonResponse);

        // Return the list of Eeducation News
        return educationNews;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            // Try to create an URL from the String
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            // If the request fails, print the error to the Log
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";

        // If the URL is null (if there is no request URL), then return early.
        if (url == null) {
            return jsonResponse;
        }

        // Initialize variables for the HTTP connection and for the InputStream
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            // Try to establish a HTTP connection with the request URL and set up the properties of the request
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");

            // Send a request to connect
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the Input Stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                // If the response failed, print it to the Log
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {

            // If the connection was not established, print it to the log
            Log.e(LOG_TAG, "Problem retrieving the Education News JSON results.", e);
        } finally {

            // Disconnect the HTTP connection if it is not disconnected yet
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            // Close the Input Stream if it is not closed yet
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the InputStream into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        // Create a new StringBuilder
        StringBuilder output = new StringBuilder();

        // If the InputStream exists, create an InputStreamReader from it and a BufferedReader from the InputStreamReader
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Append the data of the BufferedReader line by line to the StringBuilder
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        // Convert the output into String and return it
        return output.toString();
    }

    /**
     * Return a list of EducationNews objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<EducationNews> extractFeatureFromJson(String educationNewsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(educationNewsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Education News to
        ArrayList<EducationNews> educationNews = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(educationNewsJSON);

            // Extract the JSONArray associated with the key called "response",
            // which represents a list of features for the Education News.
            JSONArray educationNewsArray = baseJsonResponse .getJSONArray(RESPONSE);

            int i = 0;

            // Get a single Education News at position i within the list of Education News
            JSONObject currentEducationNews = educationNewsArray.getJSONObject(i);

            if(currentEducationNews.has(RESPONSE)) {
                // Extract the value for the key called "response"
                currentEducationNews.getJSONArray(RESPONSE);


                String newsTitle = "N/A";
                // Extract the value for the key called "title" - Get the title of the current Education News
                if (currentEducationNews.has(NEWS_TITLE)) {
                    newsTitle = currentEducationNews.getString(NEWS_TITLE);

                }

                String newsSection = "N/A";
                if (currentEducationNews.has(SECTION)) {
                    // Extract the value for the key called "section" - Get the section of the current Education News
                    newsSection = currentEducationNews.getString(SECTION);
                }

                String newsDate = "N/A";
                if (currentEducationNews.has(DATE)) {
                    // Extract the value for the key called "date" - Get the information about the published date of the current Education News
                    newsDate = currentEducationNews.getString(DATE);
                }

                String urlNews = "N/A";
                if (currentEducationNews.has(NEWS_URL)) {
                    // Extract the value for the key called "url" - Get the URL of the current Education News
                    urlNews = currentEducationNews.getString(NEWS_URL);
                }
            }

            // For each Education News in the educationNewsArray, create an EducationNews object
            for (i = 0; i < educationNewsArray.length(); i++) {

                // Get a single Education News at position i within the list of Education News
                currentEducationNews = educationNewsArray.getJSONObject(i);

                // Extract the JSONArray associated with the key called "results",
                // which represents a list of results for the Education News.
                JSONObject results =  currentEducationNews.getJSONObject(RESULTS);

                String title = "N/A";
                if ( results.has (NEWS_TITLE)) {
                    // Extract the value for the key called "title" - Get the title of the current Education News
                   title = results.getString(NEWS_TITLE);
                }

                String section = "N/A";
                if ( results.has (SECTION)){
                // Extract the value for the key called "section" - Get the section of the current Education News
                    section = results.getString(SECTION);
                }

                String date = "N/A";
                if ( results.has (DATE)){
                    // Extract the value for the key called "date" - Get the information about the published date of the current Education News
                    date = results.getString(DATE);
                }

                String newsUrl = "N/A";
                if (results.has(NEWS_URL)) {
                    // Extract the value for the key called "url" - Get the URL of the current Education News
                    newsUrl = results.getString(NEWS_URL);
                }

                // Create a new Education News object with the title, section, date and URL from the JSON response
                EducationNews newsEducationNews = new EducationNews(title, section, date, newsUrl);

                // Add the new Education News to the list of Education News.
                educationNews.add(newsEducationNews);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the educationNews JSON results", e);
        }

        // Return the list of Education News
        return educationNews;
    }

}






