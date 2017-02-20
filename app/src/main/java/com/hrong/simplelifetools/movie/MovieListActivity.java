package com.hrong.simplelifetools.movie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hrong.simplelifetools.R;
import com.hrong.simplelifetools.movie.data.MoviesContract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
        * An activity representing a list of MoviesItems. This activity
        * has different presentations for handset and tablet-size devices. On
        * handsets, the activity presents a list of items, which when touched,
        * lead to a {import org.json.JSONException;
import org.json.JSONObject;

@link MovieDetailActivity} representing
        * item details. On tablets, the activity presents the list of items and
        * item details side-by-side using two vertical panes.
        */
public class MovieListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LIST_LOADER_ID = 1;
    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private MovieListCursorAdapter movieListCursorAdapter;
    private boolean mTwoPane;
    private View recyclerView;
    public final String[] MOVIEDS_COLUNMS = {
            MoviesContract.TopRatedMoviesEntry._ID,
            MoviesContract.TopRatedMoviesEntry.COLUMN_TITLE,
            MoviesContract.TopRatedMoviesEntry.COLUMN_IMAGE_POSTER,
    };
    public static final int COLUMN_MOVIE_ID = 0;
    public static final int COLUMN_TITLE = 1;
    public static final int COLUMN_IMAGE_POSTER = 2;

    public static boolean isPopular() {
        return isPopular;
    }

    private static boolean isPopular=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        recyclerView = findViewById(R.id.movie_list);
        assert recyclerView != null;
        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        getSupportLoaderManager().initLoader(MOVIES_LIST_LOADER_ID, null, this);
        movieListCursorAdapter = new MovieListCursorAdapter(this, null, mTwoPane);
        setupRecyclerView((RecyclerView) recyclerView, movieListCursorAdapter);
        updateTopRateMoviesList();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, MovieListCursorAdapter movieListCursorAdapter) {
        recyclerView.setAdapter(movieListCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        if (!isPopular)return new CursorLoader(this, MoviesContract.TopRatedMoviesEntry.CONTENT_URI,
                MOVIEDS_COLUNMS, null, null, null);
        return new CursorLoader(this, MoviesContract.PopularMoviesEntry.CONTENT_URI,
                MOVIEDS_COLUNMS, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieListCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieListCursorAdapter.swapCursor(null);
    }

    public class MovieListCursorAdapter extends CursorRecyclerViewAdapter<MovieListCursorAdapter.ViewHodler> {
        private final String LOG_TAG = MovieListCursorAdapter.class.getSimpleName();
        private final Context mContext;
        private boolean mTwoPane;

        public MovieListCursorAdapter(Context context, Cursor cursor, boolean mTwoPane) {
            super(context, cursor);
            this.mContext = context;
            this.mTwoPane = mTwoPane;
        }

        @Override
        public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_content, parent, false);
            ViewHodler viewHodler = new ViewHodler(view);
            return viewHodler;
        }

        @Override
        public void onBindViewHolder(final MovieListCursorAdapter.ViewHodler viewHolder, Cursor cursor) {
            //movie_image
            viewHolder.image_posterURL = cursor.getString(MovieListActivity.COLUMN_IMAGE_POSTER);
//            Log.v(LOG_TAG,Integer.toString(cursor.getPosition()));

//            The base URL will look like: http://image.tmdb.org/t/p/.
//            Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
//            And finally the poster path returned by the query, in this case “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”
            Picasso picasso = Picasso.with(mContext);
            picasso.load("http://image.tmdb.org/t/p/w92" + viewHolder.image_posterURL).into(viewHolder.imagePoster);
//            picasso.setLoggingEnabled(true);
//        movie title
            viewHolder.title = cursor.getString(MovieListActivity.COLUMN_TITLE);
//            holder.mMovieView.setContentDescription(title);

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MovieDetailFragment.MOVIE_TITLE, viewHolder.title);
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra(MovieDetailFragment.MOVIE_TITLE, viewHolder.title);
                        context.startActivity(intent);
                    }
                }
            });

        }

        public class ViewHodler extends RecyclerView.ViewHolder {
            public final ImageView imagePoster;
            public final View mView;
            String title;
            String image_posterURL;

            public ViewHodler(View itemView) {
                super(itemView);
                mView = itemView;
                imagePoster = (ImageView) itemView.findViewById(R.id.image_movieList_poster);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.Sorted_by_Top_Rated) {
            isPopular=false;
            getSupportLoaderManager().restartLoader(MOVIES_LIST_LOADER_ID, null, this);
//            setupRecyclerView((RecyclerView) recyclerView, movieListCursorAdapter);
            return true;
        }
        if (itemId == R.id.Sorted_by_Popular) {
            updatePopularMovies();
            isPopular=true;
            getSupportLoaderManager().restartLoader(MOVIES_LIST_LOADER_ID, null, this);
//            setupRecyclerView((RecyclerView) recyclerView, movieListCursorAdapter);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateTopRateMoviesList() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String popular = "https://api.themoviedb.org/3/movie/top_rated?api_key=bc1569b53636fffe9e8fd2abf866c27b&language=en-US";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(popular, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d(LOG_TAG, response.toString());
                try {
                    MovieUtility.getTopRatedMoviesFromJson(getApplicationContext(),response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, error.toString());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    public void updatePopularMovies() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String popular = "https://api.themoviedb.org/3/movie/popular?api_key=bc1569b53636fffe9e8fd2abf866c27b";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(popular, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d(LOG_TAG, response.toString());
                try {
                    MovieUtility.getPopularMoviesFromJson(getApplicationContext(),response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, error.toString());
            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }
}

