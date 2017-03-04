package com.hrong.simplelifetools.weather.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hrong.simplelifetools.R;
import com.hrong.simplelifetools.movie.MovieUtility;
import com.hrong.simplelifetools.weather.Utility;
import com.hrong.simplelifetools.weather.data.WeatherContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 60 * 3;  // 3 hours
    public static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    public static final int WEATHER_NOTIFICATION_ID = 1;
    private static final String[] NOTIFY_WEATHER_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DESC = 1;
    static final int COL_WEATHER_MAX_TEMP = 2;
    static final int COL_WEATHER_MIN_TEMP = 3;

    public SunshineSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Sarting Snyc");
//      传入LOCATION和UNIT_TYPE
        final String locationSetting = Utility.getPreferredLocation(getContext());
//        String appid = "b21e787cebb54337b23e4816da79da62";//OpenWeather KEY
        String appid = "af071ae33e6643368b43a115e597ace4";
        final String FORECAST_BASE_URL = "https://free-api.heweather.com/v5/forecast?";
        final String POSTCODE_PARAM = "city";
        final String APPID_PARAM = "key";

        //构建请求天气的API
        Uri.Builder buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon().
                appendQueryParameter(POSTCODE_PARAM, locationSetting).
                appendQueryParameter(APPID_PARAM, appid);
        Log.v(LOG_TAG, "Built Uri :" + buildUri);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
        String url = buildUri.toString();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d(LOG_TAG, response.toString());
                try {
                    Utility.getHeFengWeatherDataFromJson(getContext(), response.toString(), locationSetting);
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

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.weather_content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.weather_content_authority);
        ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
    }

    public static void onAccountCreated(Account newAccount, Context context) {
         /*
         * Since we've created an account
         */
        SunshineSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL);
        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.weather_content_authority), true);
         /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeAdapter(Context context) {
        getSyncAccount(context);
    }

    public void notifyWeather() {
        Context context = getContext();
        boolean isNotify = PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(
                        context.getString(R.string.pref_notifications_key),
                        Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
        if (!isNotify) return;
        //checking the last update and notify if it' the first of the day
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        long lastSync = sharedPreferences.getLong(lastNotificationKey, 0);
        if ((System.currentTimeMillis() - lastSync) >= DAY_IN_MILLIS) {
//        if (true) {
            // Last sync was more than 1 day ago, let's send a notification with the weather.
            String locationQuery = Utility.getPreferredLocation(context);
            Uri weatherQueryUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationQuery, Utility.formatTodayDate(System.currentTimeMillis()));
            Cursor cursor = context.getContentResolver().query(weatherQueryUri, NOTIFY_WEATHER_PROJECTION, null, null, null);
            if (cursor.moveToFirst()) {
                int weatherId = cursor.getInt(COL_WEATHER_ID);
                double high = cursor.getDouble(COL_WEATHER_MAX_TEMP);
                double low = cursor.getDouble(COL_WEATHER_MIN_TEMP);
                String desc = cursor.getString(COL_WEATHER_DESC);
                int iconId = Utility.getIconResourceForWeatherCondition(weatherId, Utility.VIEW_TYPE_FUTURE);
                String title = context.getString(R.string.app_name);

                // Define the text of the forecast.
                String forecastText = String.format(context.getString(R.string.format_notification)
                        , desc, Utility.formatTemperature(context, high), Utility.formatTemperature(context, low));

                //build your notification here.
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(iconId).
                        setContentTitle(title).
                        setContentText(forecastText);
                Intent intent = new Intent(context, com.hrong.simplelifetools.MainActivity.class);
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                // Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(com.hrong.simplelifetools.MainActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(intent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                builder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(WEATHER_NOTIFICATION_ID, builder.build());

                //refreshing last sync
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(lastNotificationKey, Utility.formatTodayDate(System.currentTimeMillis()));
                editor.commit();
            }
        }
    }
}