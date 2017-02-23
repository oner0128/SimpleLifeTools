package com.hrong.simplelifetools.weather;

/**
 * Created by hrong on 2016/4/22.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hrong.simplelifetools.R;
import com.hrong.simplelifetools.weather.data.WeatherContract;
import com.hrong.simplelifetools.weather.sync.SunshineSyncAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String SELECTED_KEY = "position";
    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    public static boolean mUseTodayLayout;
    private static int smoothPosition = 0;
    ListView listView;
    static ForecastAdapter forecastAdapter;
    public static final String[] FORECAST_COLUNMS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LON,
            WeatherContract.LocationEntry.COLUMN_CITY_NAME,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID};
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_COORD_LAT = 6;
    static final int COL_COORD_LON = 7;
    static final int COL_CITY_NAME = 8;
    static final int COL_WEATHER_CONDITION_ID = 9;
    static final int FORECASE_LOADER_ID = 1;

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (forecastAdapter != null)
            forecastAdapter.setUseTodayLayout(mUseTodayLayout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECASE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        forecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        forecastAdapter.setUseTodayLayout(mUseTodayLayout);
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    ((Callback) getActivity()).onItemSelected(
                            WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)));
                }
                smoothPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY))
            smoothPosition = savedInstanceState.getInt(SELECTED_KEY);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (smoothPosition != ListView.INVALID_POSITION)
            outState.putInt(SELECTED_KEY, smoothPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void openPreferenceLocationInMap() {
        Cursor cursor;
        if (forecastAdapter != null) {
            cursor = forecastAdapter.getCursor();
            cursor.moveToFirst();
            String lat = Double.toString(cursor.getDouble(COL_COORD_LAT));
            String lon = Double.toString(cursor.getDouble(COL_COORD_LON));
            Uri geoLocation = Uri.parse("geo:" + lat + "," + lon);
            Log.v(LOG_TAG, geoLocation.toString());
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocation);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(intent);
            else
                Log.d(LOG_TAG, "couldn't call" + lat + "," + lon + ",no receiving apps installed");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_map) {
            openPreferenceLocationInMap();
            return true;
        }
        if (item.getItemId() == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());
        long startTime = System.currentTimeMillis();//获取当前系统时间并转换为秒为单位，因为OpenWeather每三小时更新一次所以减10800s
        startTime = Utility.formatTodayDate(startTime);
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, startTime);
        return new CursorLoader(getActivity(), weatherForLocationUri,
                FORECAST_COLUNMS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        forecastAdapter.swapCursor(data);
        if (smoothPosition != ListView.INVALID_POSITION)
            listView.smoothScrollToPosition(smoothPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.swapCursor(null);
    }

    public void onLocationChanged() {
        updateWeather();
        getLoaderManager().restartLoader(FORECASE_LOADER_ID, null, this);
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }
}
