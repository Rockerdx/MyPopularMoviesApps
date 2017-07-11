package com.example.android.mypopularmoviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.mypopularmoviesapp.data.MoviesContract;
import com.example.android.mypopularmoviesapp.data.MoviesDbHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GridView mGridview;
    ImageModel movieDetail;
    ArrayList<ImageModel> arrayResults;
    List<String> listFavorite;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridview = (GridView) findViewById(R.id.mGrid);

        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        mDb = dbHelper.getReadableDatabase();

        Cursor mCursor = getAllFavoriteMovies();
        listFavorite = new ArrayList<>();
        while (mCursor.moveToNext()) {
            listFavorite.add(mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_ID)));
            Log.d("tes", mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_ID)));
        }
//        for(int g =0;g<mCursor.getCount();g++){
//            mCursor.moveToNext();
//
//        }

        new getData().execute("popular");
        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this,arrayResults.get(position).getImagePath(),Toast.LENGTH_SHORT).show();
                Intent x = new Intent(MainActivity.this, DetailActivity.class);
                x.putExtra("position", position);

                if (listFavorite.contains(arrayResults.get(position).getId())) {
                    x.putExtra("favorite", true);
                    Toast.makeText(getApplicationContext(), "fav", Toast.LENGTH_SHORT).show();
                } else {
                    x.putExtra("favorite", false);
                    Toast.makeText(getApplicationContext(), "not fav", Toast.LENGTH_SHORT).show();
                }
                x.putParcelableArrayListExtra("list", arrayResults);
                startActivity(x);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_top) {
            new getData().execute("top_rated");
            return true;
        } else if (id == R.id.sort_popular) {
            new getData().execute("popular");
            return true;
        } else if (id == R.id.sort_fav) {
            populateOfflineMovie();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class getData extends AsyncTask<String, Void, ArrayList<ImageModel>> {
        @Override
        protected void onPostExecute(ArrayList<ImageModel> result) {


            ImageAdapter adapter = new ImageAdapter(MainActivity.this, R.layout.image_thumb, result);

            mGridview.setAdapter(adapter);
            adapter.updateResults(result);
            super.onPostExecute(result);
        }

        @Override
        protected ArrayList<ImageModel> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            URL movieURL = NetworkUtils.buildUrl(params[0]);

            arrayResults = new ArrayList<>();
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(movieURL);
                JSONObject jsonObject = new JSONObject(response);
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    movieDetail = new ImageModel();
                    JSONObject c = results.getJSONObject(i);
                    movieDetail.setImagePath(c.get("poster_path").toString());
                    movieDetail.setOverview(c.get("overview").toString());
                    movieDetail.setReleaseDate("Release Date : " + c.get("release_date").toString());
                    movieDetail.setRating("Rating : " + c.get("vote_average").toString());
                    movieDetail.setLanguage("Language : " + c.get("original_language").toString());
                    movieDetail.setTitle(c.get("title").toString());
                    movieDetail.setId(c.getString("id"));
                    arrayResults.add(movieDetail);

                }
                //Log.d("tes",results.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }


            return arrayResults;
        }
    }

    private void populateOfflineMovie(){
        movieDetail = new ImageModel();
        arrayResults = new ArrayList<>();
        Cursor mCursor = getAllFavoriteMovies();
        while (mCursor.moveToNext()) {
            listFavorite.add(mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_ID)));
            Log.d("tes", mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_ID)));

            movieDetail = new ImageModel();
            movieDetail.setOverview(mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_REVIEW)));
            Log.d("tes",mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_NAME)));
            movieDetail.setReleaseDate("Release Date : " + mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_RELEASE_DATE)));
            movieDetail.setRating("Rating : " + mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_RATING)));
            movieDetail.setLanguage("Language : " + mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_LANGUANGE)));
            movieDetail.setTitle(mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_NAME)));
            movieDetail.setId(mCursor.getString(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_ID)));
            byte[] image = mCursor.getBlob(mCursor.getColumnIndex(MoviesContract.MoviesDB.COLUMN_MOVIE_IMAGE));
            movieDetail.setOfflineImage(getImage(image));
            arrayResults.add(movieDetail);

            ImageAdapter adapter = new ImageAdapter(MainActivity.this, R.layout.image_thumb, arrayResults);

            mGridview.setAdapter(adapter);
            adapter.updateResults(arrayResults);

        }



    }

    private Cursor getAllFavoriteMovies() {
        return mDb.query(
                MoviesContract.MoviesDB.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}