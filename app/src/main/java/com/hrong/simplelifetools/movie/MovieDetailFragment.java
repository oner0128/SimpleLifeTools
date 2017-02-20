package com.hrong.simplelifetools.movie;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hrong.simplelifetools.movie.data.MoviesContract;
import com.hrong.simplelifetools.R;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String MOVIE_TITLE = "title";

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private String movieTitle;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MOVIE_TITLE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            movieTitle = getArguments().getString(MOVIE_TITLE);
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(movieTitle);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        // Show the dummy content as text in a TextView.
        return rootView;
    }

    public final String[] MOVIEDS_COLUNMS = {
            MoviesContract.TopRatedMoviesEntry._ID,
            MoviesContract.TopRatedMoviesEntry.COLUMN_TITLE,
            MoviesContract.TopRatedMoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.TopRatedMoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.TopRatedMoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.TopRatedMoviesEntry.COLUMN_POPULARITY,
            MoviesContract.TopRatedMoviesEntry.COLUMN_IMAGE_POSTER};
    public static final int COLUMN_MOVIE_ID = 0;
    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_OVERVIEW = 2;
    public static final int COLUMN_RELEASE_DATE = 3;
    public static final int COLUMN_VOTE_AVERAGE = 4;
    public static final int COLUMN_POPULARITY = 5;
    public static final int COLUMN_IMAGE_POSTER = 6;

    private static final int MOVIES_DETAIL_LOADER_ID = 2;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIES_DETAIL_LOADER_ID, null, this);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (MovieListActivity.isPopular()) {
            Uri movieDetailByTitle = MoviesContract.PopularMoviesEntry.buildPopularUriWithTitle(movieTitle);
            return new CursorLoader(getContext(), movieDetailByTitle,
                    MOVIEDS_COLUNMS, null, null, null);
        }
//        Log.v(LOG_TAG,movieDetailByTitle.toString());
        Uri movieDetailByTitle = MoviesContract.TopRatedMoviesEntry.buildTopRatedUriWithTitle(movieTitle);
        return new CursorLoader(getContext(), movieDetailByTitle,
                MOVIEDS_COLUNMS, null, null, null);
    }

    static class ViewHolder {
        private TextView tv_release_date;
        private TextView tv_vote_average;
        private TextView tv_popularity;
        private TextView tv_overview;
        private ImageView imagePoster;

        public ViewHolder(View view) {
            tv_release_date = (TextView) view.findViewById(R.id.tv_release_date);
            tv_vote_average = (TextView) view.findViewById(R.id.tv_vote_average);
            tv_popularity = (TextView) view.findViewById(R.id.tv_popularity);
            tv_overview = (TextView) view.findViewById(R.id.tv_overview);
            imagePoster = ((ImageView) view.findViewById(R.id.image_detail_poster));
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            ViewHolder viewHolder = new ViewHolder(getView());
            viewHolder.tv_release_date.setText("Release Date :\n" + data.getString(COLUMN_RELEASE_DATE));
            viewHolder.tv_vote_average.setText("Vote : " + data.getString(COLUMN_VOTE_AVERAGE) + "/10");
            viewHolder.tv_popularity.setText("Popularity :\n" + data.getString(COLUMN_POPULARITY));
            viewHolder.tv_overview.setText(data.getString(COLUMN_OVERVIEW));
            Picasso picasso = Picasso.with(getContext());
//            the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
            picasso.load("http://image.tmdb.org/t/p/w185" + data.getString(COLUMN_IMAGE_POSTER)).into(viewHolder.imagePoster);
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
    }
}
