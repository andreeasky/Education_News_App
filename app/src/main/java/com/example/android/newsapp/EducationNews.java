package com.example.android.newsapp;

public class EducationNews {

    // Title of the News
    private String newsTitle;

    // Section of the News
    private String newsSection;

    // Date when the News was published
    private String newsDate;

    // URL of the news
    private String newsUrl;

    /**
     * Constructs a new EducationNews object.
     *
     * @param title   is the title of the news
     * @param section is the section of the news
     * @param date    is the date when the news was published
     * @param url     is the website URL to find more details about the news
     */

    public EducationNews(String title, String section, String date, String url) {
        newsTitle = title;
        newsSection = section;
        newsDate = date;
        newsUrl = url;
    }

    /**
     * Returns the title of the news.
     */
    public String getTitle() {
        return newsTitle;
    }

    /**
     * Returns the section of the news.
     */
    public String getNewsSection() {
        return newsSection;
    }

    /**
     * Returns the date of the news.
     */
    public String getNewsDate() {
        return newsDate;
    }

    /**
     * Returns the website URL to find more information about the news.
     */
    public String getUrl() {
        return newsUrl;
    }
}

