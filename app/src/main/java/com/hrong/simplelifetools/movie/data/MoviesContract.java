package com.hrong.simplelifetools.movie.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rrr on 2017/2/20.
 */

public class MoviesContract {
    public static final String CONTENT_AUTHORITY = "com.hrong.simplelifetools";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TOPRATED = "toprated";
    public static final String PATH_POPULAR= "popular";
    public static final class TopRatedMoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOPRATED).build();
        public static final String TABLE_NAME = "toprated";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_TMDB_ID="tmdb_id";
        public static final String COLUMN_RELEASE_DATE="release_date";
        public static final String COLUMN_GENERE_IDS="genre_ids";//影片类型
        public static final String COLUMN_IMAGE_BACKDROP="backdrop_path";
        public static final String COLUMN_IMAGE_POSTER="poster_path";
        public static final String COLUMN_VOTE_COUNT="vote_count";
        public static final String COLUMN_VOTE_AVERAGE="vote_average";
        public static final String COLUMN_POPULARITY="popularity";
        public static Uri buildTopRatedUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildTopRatedUriWithTitle(String title){
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }
        public static String getTitleFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
    public static final class PopularMoviesEntry implements BaseColumns{
        public static final Uri CONTENT_URI=BASE_CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();
        public static final String TABLE_NAME = "popular";
        public static final String COLUMN_TITLE="title";
        public static final String COLUMN_OVERVIEW="overview";
        public static final String COLUMN_TMDB_ID="tmdb_id";
        public static final String COLUMN_RELEASE_DATE="release_date";
        public static final String COLUMN_GENERE_IDS="genre_ids";//影片类型
        public static final String COLUMN_IMAGE_BACKDROP="backdrop_path";
        public static final String COLUMN_IMAGE_POSTER="poster_path";
        public static final String COLUMN_VOTE_COUNT="vote_count";
        public static final String COLUMN_VOTE_AVERAGE="vote_average";
        public static final String COLUMN_POPULARITY="popularity";
        public static Uri buildPopulardUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildPopularUriWithTitle(String title){
            return CONTENT_URI.buildUpon().appendPath(title).build();
        }
        public static String getTitleFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

}
