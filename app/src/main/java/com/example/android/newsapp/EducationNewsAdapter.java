package com.example.android.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EducationNewsAdapter extends ArrayAdapter<EducationNews> {

    /**
     * An EducationNewsAdapter knows how to create a list item layout for each news
     * in the data source (a list of educationNews objects).
     *
     * These list item layouts will be provided to an adapter view like ListView
     * to be displayed to the user.
     */
    public EducationNewsAdapter(Context context, List<EducationNews> educationNews) {
        super(context, 0, educationNews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Find the news at the given position in the list of news
        EducationNews currentNews = getItem(position);

        // Find the TextView with view ID title of the news
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);

        // Display the title of the current news in that TextView
        titleView.setText(currentNews.getTitle());

        // Find the TextView with view ID news_section of the news
        TextView newsSectionView = (TextView) listItemView.findViewById(R.id.news_section);

        // Display the news_section of the current news in that TextView
        newsSectionView.setText(currentNews.getNewsSection());

        // Find the TextView with view ID date
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);

        // Display the date when the current news was published in that TextView
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.US);
        try {
            Date date = dateFormat.parse(currentNews.getNewsDate());

            String parsedaDate = dateFormat2.format(date);
            dateView.setText(parsedaDate);
        } catch (ParseException e) {
        }

        // Return the list item view
        return listItemView;
    }

}
