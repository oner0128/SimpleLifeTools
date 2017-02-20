package com.hrong.simplelifetools.movie.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by rrr on 2017/2/20.
 */

public class MoviesProvider extends ContentProvider {
    private static final String LOG_TAG = MoviesProvider.class.getSimpleName();
    private MoviesDBHelper rMoviesDBHelper;
    private static final UriMatcher rUriMatcher = buildUriMatcher();
    static final int TOP_RATED = 100;
    static final int TOP_RATED_WITH_TITLE = 101;
    static final int POPULAR = 200;
    static final int POPULAR_WITH_TITLE = 201;
//    private static final SQLiteQueryBuilder MOVIES_QUERY_BUILDER;
////
//    static {
//        MOVIES_QUERY_BUILDER = new SQLiteQueryBuilder();
//        MOVIES_QUERY_BUILDER.setTables(MoviesContract.TopRatedMoviesEntry.TABLE_NAME);
//    }

    @Override
    public boolean onCreate() {
        rMoviesDBHelper = new MoviesDBHelper(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, MoviesContract.PATH_TOPRATED, TOP_RATED);
        uriMatcher.addURI(authority, MoviesContract.PATH_TOPRATED + "/*", TOP_RATED_WITH_TITLE);
        uriMatcher.addURI(authority, MoviesContract.PATH_POPULAR, POPULAR);
        uriMatcher.addURI(authority, MoviesContract.PATH_POPULAR + "/*", POPULAR_WITH_TITLE);
        return uriMatcher;
    }
    private static final String sTitleSelection =
            MoviesContract.TopRatedMoviesEntry.COLUMN_TITLE +" = ? ";
    private static final String sPopularTitleSelection =
            MoviesContract.TopRatedMoviesEntry.COLUMN_TITLE +" = ? ";
    //    private Cursor getDetailByTitle(Uri uri, String[] projection,String sortOrder) {
//        String title = MoviesContract.TopRatedMoviesEntry.getTitleFromUri(uri);
//        return MOVIES_QUERY_BUILDER.query(
//                rMoviesDBHelper.getReadableDatabase(),
//                projection,
//                sTitleSelection,
//                new String[]{title},
//                null, null,
//                sortOrder);}
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = rMoviesDBHelper.getReadableDatabase();
        Cursor resultCursor;
        switch (rUriMatcher.match(uri)) {
            case TOP_RATED: {
                resultCursor = db.query(
                        MoviesContract.TopRatedMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder
                );
                break;
            }
            case TOP_RATED_WITH_TITLE: {
                String title = MoviesContract.TopRatedMoviesEntry.getTitleFromUri(uri);
                resultCursor = db.query(
                        MoviesContract.TopRatedMoviesEntry.TABLE_NAME,
                        projection,
                        sTitleSelection,
                        new String[]{title},
                        null, null,
                        null);
                break;
            }
            case POPULAR: {
                resultCursor = db.query(
                        MoviesContract.PopularMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder
                );
                break;
            }
            case POPULAR_WITH_TITLE: {
                String title = MoviesContract.PopularMoviesEntry.getTitleFromUri(uri);
                resultCursor = db.query(
                        MoviesContract.PopularMoviesEntry.TABLE_NAME,
                        projection,
                        sPopularTitleSelection,
                        new String[]{title},
                        null, null,
                        null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return resultCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = rMoviesDBHelper.getWritableDatabase();
        Uri returnUri;
        switch (rUriMatcher.match(uri)) {
            case TOP_RATED: {
                long _id = db.insert(
                        MoviesContract.TopRatedMoviesEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0)
                    returnUri = MoviesContract.TopRatedMoviesEntry.buildTopRatedUri(_id);
                else throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case TOP_RATED_WITH_TITLE: {
                long _id = db.insert(
                        MoviesContract.TopRatedMoviesEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0)
                    returnUri = MoviesContract.TopRatedMoviesEntry.buildTopRatedUri(_id);
                else throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case POPULAR: {
                long _id = db.insert(
                        MoviesContract.PopularMoviesEntry.TABLE_NAME,
                        null,
                        values);
                if (_id > 0)
                    returnUri = MoviesContract.PopularMoviesEntry.buildPopulardUri(_id);
                else throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = rMoviesDBHelper.getWritableDatabase();
        int isDelete;
        switch (rUriMatcher.match(uri)) {
            case TOP_RATED: {
                isDelete = db.delete(
                        MoviesContract.TopRatedMoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case TOP_RATED_WITH_TITLE: {
                isDelete = db.delete(
                        MoviesContract.TopRatedMoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case POPULAR: {
                isDelete = db.delete(
                        MoviesContract.PopularMoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case POPULAR_WITH_TITLE: {
                isDelete = db.delete(
                        MoviesContract.PopularMoviesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (isDelete != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return isDelete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = rMoviesDBHelper.getWritableDatabase();
        int isUpdate;
        switch (rUriMatcher.match(uri)) {
            case TOP_RATED: {
                isUpdate = db.update(
                        MoviesContract.TopRatedMoviesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case TOP_RATED_WITH_TITLE: {
                isUpdate = db.update(
                        MoviesContract.TopRatedMoviesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case POPULAR: {
                isUpdate = db.update(
                        MoviesContract.PopularMoviesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case POPULAR_WITH_TITLE: {
                isUpdate = db.update(
                        MoviesContract.PopularMoviesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (isUpdate != 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return isUpdate;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = rMoviesDBHelper.getWritableDatabase();
        switch (rUriMatcher.match(uri)) {
            case TOP_RATED: {
                db.beginTransaction();
                int returnInt = 0;
                try {
                    for (ContentValues value : values) {
//                        normalizeDate(value);
                        long _id = db.insert(MoviesContract.TopRatedMoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnInt++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnInt;
            }
            case POPULAR: {
                db.beginTransaction();
                int returnInt = 0;
                try {
                    for (ContentValues value : values) {
//                        normalizeDate(value);
                        long _id = db.insert(MoviesContract.PopularMoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnInt++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnInt;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
