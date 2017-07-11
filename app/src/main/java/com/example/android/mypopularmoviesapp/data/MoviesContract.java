package com.example.android.mypopularmoviesapp.data;

import android.provider.BaseColumns;

/**
 * Created by acer on 7/10/2017.
 */

public class MoviesContract {

    public static final class MoviesDB implements BaseColumns{

        public static final String TABLE_NAME = "movielist";
        public static final String COLUMN_MOVIE_ID = "id_movie";
        public static final String COLUMN_MOVIE_NAME = "name";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_LANGUANGE = "language";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_REVIEW = "review";
        public static final String COLUMN_MOVIE_IMAGE = "image";
    }



}
