package com.example.android.mypopularmoviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.mypopularmoviesapp.data.MoviesContract;
import com.example.android.mypopularmoviesapp.data.MoviesDbHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements RecyclerItemClickListener.OnItemClickListener {

    @BindView(R.id.title) TextView movieTitle;
    @BindView(R.id.txtLang) TextView movieLang;
    @BindView(R.id.txtReleaseDate) TextView movieReleaseDate;
    @BindView(R.id.txtOverview) TextView movieOverview;
    @BindView(R.id.txtRating) TextView movieRating;
    @BindView(R.id.imgMoviePoster) ImageView imagePoster;
    @BindView(R.id.rvTrailer) RecyclerView rvTrailer;
    @BindView(R.id.rvReview) RecyclerView rvReview;
    @BindView(R.id.btn_favorite) Button btnFavorite;
    @BindView(R.id.mScroll) ScrollView mScroll;


    byte[] image;
    static String URL_YOUTUBE = "https://www.youtube.com/watch?v=";
    List<String> listTrailerLinks;
    ArrayList<ReviewModel> listReviews;
    Integer pos;
    private static final String LIFECYCLE_KEY = "savedState";

    ImageModel movieDetail;
    ArrayList<ImageModel> arrayDetails;
    Integer id;
    Boolean Favorite;

    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        movieDetail = new ImageModel();

        Intent getIntent = getIntent();
        id = getIntent.getIntExtra("position",0);
        Favorite = getIntent.getBooleanExtra("favorite",false);
        arrayDetails = (ArrayList<ImageModel>) getIntent.getSerializableExtra("list");
        movieDetail = arrayDetails.get(id);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(LIFECYCLE_KEY)){
                pos = savedInstanceState.getInt(LIFECYCLE_KEY,0);
                Log.d("tes","restored pos " + savedInstanceState.getInt(LIFECYCLE_KEY,0));
            }
        }

        ButterKnife.bind(this);

        movieTitle.setText(movieDetail.getTitle());
        movieLang.setText(movieDetail.getLanguage());
        movieReleaseDate.setText(movieDetail.getReleaseDate());
        movieRating.setText(movieDetail.getRating());
        movieOverview.setText((movieDetail.getOverview()));

        if(!(movieDetail.getImagePath() == null)) {
            Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + movieDetail.getImagePath()).into(imagePoster);
            Picasso.with(this).load("http://image.tmdb.org/t/p/w185/" + movieDetail.getImagePath()).into(target);
        }else {
            imagePoster.setImageBitmap(getImage(movieDetail.getOfflineImage()));
        }
        new getTrailer().execute(movieDetail.getId(),"videos");
        new getTrailer().execute(movieDetail.getId(),"reviews");

        MoviesDbHelper dbHelper = new MoviesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        if(Favorite){
            btnFavorite.setCompoundDrawablesRelative(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_star_black_24dp),null,null,null);
            btnFavorite.setText(R.string.favorited);
        }

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFavorite.setCompoundDrawablesRelative(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_star_black_24dp),null,null,null);
                btnFavorite.setText(R.string.favorited);
                addNewFavoriteMovie(movieDetail.getTitle(),movieDetail.getId(),movieDetail.getReleaseDate(),movieDetail.getLanguage(),movieDetail.getRating(),movieDetail.getOverview(),image);
            }
        });

    }

    @Override
    public void onItemClick(View childView, int position) {

    }

    @Override
    public void onItemLongPress(View childView, int position) {

    }

    private class getTrailer extends AsyncTask<String, Void, String> {


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("tes",result);
            if(result.equals("videos")){
                rvTrailer.setLayoutManager(new LinearLayoutManager(rvTrailer.getContext()));
                rvTrailer.setAdapter(new TrailerAdapter(getApplicationContext(),
                        listTrailerLinks){
                    @Override
                    public void onBindViewHolder(ViewHolder holder, final int position) {

                        String trailertext = getResources().getString(R.string.trailerText) + (position+1);

                        holder.mTextView.setText(trailertext);
                        holder.mTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(listTrailerLinks.get(position))));
                            }
                        });
                    }
                });
            }
            else if(result.equals("reviews")){
                rvReview.setLayoutManager(new LinearLayoutManager(rvTrailer.getContext()));
                rvReview.setAdapter(new ReviewAdapter(getApplicationContext(),listReviews));
                if(pos!=null){
                    mScroll.scrollTo(pos,mScroll.getScrollY());
                }
            }
            else{
                Toast.makeText(DetailActivity.this,"error",Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... params) {



            URL movieURL = NetworkUtils.buildUrl(params[0],params[1]);

            if(params[1].equals("videos")) {
                try {
                    listTrailerLinks = new ArrayList<>();
                    String response = NetworkUtils.getResponseFromHttpUrl(movieURL);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject c = results.getJSONObject(i);
                        String trailerKey = c.getString("key");
                        String link = URL_YOUTUBE + trailerKey;
                        Log.d("tes", link);
                        listTrailerLinks.add(link);
                    }
                    return "videos";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "fail";
                }
            }
            else if(params[1].equals("reviews")){

                listReviews = new ArrayList<>();
                try {
                    String response = NetworkUtils.getResponseFromHttpUrl(movieURL);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject c = results.getJSONObject(i);
                        ReviewModel temp = new ReviewModel();
                        temp.setId(c.getString("id"));
                        temp.setAuthor(c.getString("author"));
                        temp.setContent(c.getString("content"));
                        listReviews.add(temp);
                    }
                    return "reviews";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "fail";
                }
            }
            return "fail";
        }
    }

    private long addNewFavoriteMovie(String name,String movie_id,String release_date,String language,String rating,String review,byte[] Image) {
        ContentValues cv = new ContentValues();
        cv.put(MoviesContract.MoviesDB.COLUMN_MOVIE_NAME, name);
        cv.put(MoviesContract.MoviesDB.COLUMN_MOVIE_ID, movie_id);
        cv.put(MoviesContract.MoviesDB.COLUMN_MOVIE_RELEASE_DATE,release_date);
        cv.put(MoviesContract.MoviesDB.COLUMN_MOVIE_LANGUANGE,language);
        cv.put(MoviesContract.MoviesDB.COLUMN_MOVIE_RATING,rating);
        cv.put(MoviesContract.MoviesDB.COLUMN_MOVIE_REVIEW,review);
        cv.put(MoviesContract.MoviesDB.COLUMN_MOVIE_IMAGE,Image);
        return mDb.insert(MoviesContract.MoviesDB.TABLE_NAME, null, cv);
    }

    Target target = new Target() {

        @Override
        public void onPrepareLoad(Drawable arg0) {
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
           image = getBitmapAsByteArray(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable arg0) {
        }
    };

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LIFECYCLE_KEY,mScroll.getScrollX());
        Log.d("tes","current pos " + mScroll.getScrollX());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pos = savedInstanceState.getInt(LIFECYCLE_KEY,0);
    }
}
