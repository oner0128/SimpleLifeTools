package com.hrong.simplelifetools.movie;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.hrong.simplelifetools.movie.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by rrr on 2017/2/20.
 */

public class MovieUtility {
    public static final String DATE_FORMAT = "yyyyMMdd";
    private static final String LOG_TAG = MovieUtility.class.getSimpleName();


    public static String formatDate(long dateInMilliSeconds) {
        Date date = new Date(dateInMilliSeconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }

    public static long formatTodayDate(long currentdateInMilliSeconds) {
        Date date = new Date(currentdateInMilliSeconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String dateInString = simpleDateFormat.format(date);
        long dateInMilliSeconds = formatDateStringToMilliSecond(dateInString);
        return dateInMilliSeconds;
    }

    public static long formatDateStringToMilliSecond(String dateString) {
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateInMilliSecond = date.getTime();
        return dateInMilliSecond;
    }

    //从TheMovieDB获取TopRatedMovies
    public static void getTopRatedMoviesFromJson(Context context, String topTatedMoviesJsonStr)
            throws JSONException {
        context.getContentResolver().delete(MoviesContract.TopRatedMoviesEntry.CONTENT_URI,
                null, null);
        final String JSON_MOVIES_ARRAY = "results";
        final String COLUMN_TITLE = "title";
        final String COLUMN_OVERVIEW = "overview";
        final String COLUMN_TMDB_ID = "id";
        final String COLUMN_RELEASE_DATE = "release_date";
        final String COLUMN_GENERE_IDS = "genre_ids";//影片类型
        final String COLUMN_IMAGE_BACKDROP = "backdrop_path";
        final String COLUMN_IMAGE_POSTER = "poster_path";
        final String COLUMN_VOTE_COUNT = "vote_count";
        final String COLUMN_VOTE_AVERAGE = "vote_average";
        final String COLUMN_POPULARITY = "popularity";

        JSONObject Object = new JSONObject(topTatedMoviesJsonStr);
        JSONArray moviesArray = Object.getJSONArray(JSON_MOVIES_ARRAY);

        Vector<ContentValues> valuesVector = new Vector<>(moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {
            String title;
            String overview;
            String tmdb_id;
            String release_date;
            int genre_ids;//影片类型
            String image_backdrop;
            String image_poster;
            int vote_count;
            double vote_average;
            double popularity;


            JSONObject movieInfo = moviesArray.getJSONObject(i);
            //title
            title = movieInfo.getString(COLUMN_TITLE);
            //overview
            overview = movieInfo.getString(COLUMN_OVERVIEW);
            //tmdb_id
            tmdb_id = movieInfo.getString(COLUMN_TMDB_ID);
            //release_date
            release_date = movieInfo.getString(COLUMN_RELEASE_DATE);
            //genre_ids
            genre_ids = movieInfo.getJSONArray(COLUMN_GENERE_IDS).getInt(0);
            //image_backdrop
            image_backdrop = movieInfo.getString(COLUMN_IMAGE_BACKDROP);
            //image_poster
            image_poster = movieInfo.getString(COLUMN_IMAGE_POSTER);
//            Log.v(LOG_TAG, image_poster);
            popularity = movieInfo.getDouble(COLUMN_POPULARITY);
            //设置第一个imageView优先加载
            // the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
//            if (i==0)
//            Picasso.with(context).
//                    load("http://image.tmdb.org/t/p/w92" + image_poster).priority(Picasso.Priority.HIGH);
//            Picasso.with(context).
//                    load("http://image.tmdb.org/t/p/w92" + image_poster);
            //vote_count;
            vote_count = movieInfo.getInt(COLUMN_VOTE_COUNT);
            //vote_average;
            vote_average = movieInfo.getDouble(COLUMN_VOTE_AVERAGE);

            ContentValues weatherValues = new ContentValues();


            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_TITLE, title);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_OVERVIEW, overview);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_TMDB_ID, tmdb_id);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_RELEASE_DATE, release_date);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_GENERE_IDS, genre_ids);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_IMAGE_BACKDROP, image_backdrop);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_IMAGE_POSTER, image_poster);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_VOTE_COUNT, vote_count);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_VOTE_AVERAGE, vote_average);
            weatherValues.put(MoviesContract.TopRatedMoviesEntry.COLUMN_POPULARITY, popularity);
            valuesVector.add(weatherValues);
        }
        int isInsert = 0;
        if (valuesVector.size() > 0) {
            ContentValues[] contentValues = new ContentValues[valuesVector.size()];
            valuesVector.toArray(contentValues);
            isInsert = context.getContentResolver().bulkInsert(MoviesContract.TopRatedMoviesEntry.CONTENT_URI, contentValues);
        }
        if (isInsert != 0)
            Log.d(LOG_TAG, "MovieService complete." + isInsert + " inserted");
    }

    //从TheMovieDB获取popularMovies
    public static void getPopularMoviesFromJson(Context context, String topTatedMoviesJsonStr)
            throws JSONException {
        context.getContentResolver().delete(MoviesContract.PopularMoviesEntry.CONTENT_URI, null, null);
        final String JSON_MOVIES_ARRAY = "results";
        final String COLUMN_TITLE = "title";
        final String COLUMN_OVERVIEW = "overview";
        final String COLUMN_TMDB_ID = "id";
        final String COLUMN_RELEASE_DATE = "release_date";
        final String COLUMN_GENERE_IDS = "genre_ids";//影片类型
        final String COLUMN_IMAGE_BACKDROP = "backdrop_path";
        final String COLUMN_IMAGE_POSTER = "poster_path";
        final String COLUMN_VOTE_COUNT = "vote_count";
        final String COLUMN_VOTE_AVERAGE = "vote_average";
        final String COLUMN_POPULARITY = "popularity";

        JSONObject Object = new JSONObject(topTatedMoviesJsonStr);
        JSONArray moviesArray = Object.getJSONArray(JSON_MOVIES_ARRAY);

        Vector<ContentValues> valuesVector = new Vector<>(moviesArray.length());

        for (int i = 0; i < moviesArray.length(); i++) {
            String title;
            String overview;
            String tmdb_id;
            String release_date;
            int genre_ids;//影片类型
            String image_backdrop;
            String image_poster;
            int vote_count;
            double vote_average;
            double popularity;


            JSONObject movieInfo = moviesArray.getJSONObject(i);
            //title
            title = movieInfo.getString(COLUMN_TITLE);
            //overview
            overview = movieInfo.getString(COLUMN_OVERVIEW);
            //tmdb_id
            tmdb_id = movieInfo.getString(COLUMN_TMDB_ID);
            //release_date
            release_date = movieInfo.getString(COLUMN_RELEASE_DATE);
            //genre_ids
            genre_ids = movieInfo.getJSONArray(COLUMN_GENERE_IDS).getInt(0);
            //image_backdrop
            image_backdrop = movieInfo.getString(COLUMN_IMAGE_BACKDROP);
            //image_poster
            image_poster = movieInfo.getString(COLUMN_IMAGE_POSTER);
//            Log.v(LOG_TAG, image_poster);
            popularity = movieInfo.getDouble(COLUMN_POPULARITY);
            //设置第一个imageView优先加载
            // the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
//            if (i==0)
//            Picasso.with(context).
//                    load("http://image.tmdb.org/t/p/w92" + image_poster).priority(Picasso.Priority.HIGH);
//            Picasso.with(context).
//                    load("http://image.tmdb.org/t/p/w92" + image_poster);
            //vote_count;
            vote_count = movieInfo.getInt(COLUMN_VOTE_COUNT);
            //vote_average;
            vote_average = movieInfo.getDouble(COLUMN_VOTE_AVERAGE);

            ContentValues weatherValues = new ContentValues();


            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_TITLE, title);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_OVERVIEW, overview);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_TMDB_ID, tmdb_id);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE, release_date);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_GENERE_IDS, genre_ids);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_IMAGE_BACKDROP, image_backdrop);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_IMAGE_POSTER, image_poster);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_VOTE_COUNT, vote_count);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_VOTE_AVERAGE, vote_average);
            weatherValues.put(MoviesContract.PopularMoviesEntry.COLUMN_POPULARITY, popularity);
            valuesVector.add(weatherValues);
        }
        int isInsert = 0;
        if (valuesVector.size() > 0) {
            ContentValues[] contentValues = new ContentValues[valuesVector.size()];
            valuesVector.toArray(contentValues);
            isInsert = context.getContentResolver().bulkInsert(MoviesContract.PopularMoviesEntry.CONTENT_URI, contentValues);
        }
        if (isInsert != 0)
            Log.d(LOG_TAG, "MovieService complete." + isInsert + " inserted (popular)");
    }

}