package com.example.android.mypopularmoviesapp;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by acer on 6/14/2017.
 */

public class NetworkUtils {

    private static final String BASE_URL = "http://api.themoviedb.org";
    final static String API_PARAM = "api_key";
    final static String API_KEY = "";


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(5000);
        urlConnection.setReadTimeout(10000);
        try {
            InputStream in = urlConnection.getInputStream();


            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildUrl(String sort) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sort)
                .appendQueryParameter(API_PARAM,API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v("tes", "Built URI " + url);
        return url;

    }

    public static URL buildUrl(String sort,String sub) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sort)
                .appendPath(sub)
                .appendQueryParameter(API_PARAM,API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v("tes", "Built URI " + url);
        return url;

    }

}
