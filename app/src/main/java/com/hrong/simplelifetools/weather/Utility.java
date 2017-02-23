/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hrong.simplelifetools.weather;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.hrong.simplelifetools.R;
import com.hrong.simplelifetools.weather.data.WeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class Utility {
    public static final String DATE_FORMAT = "yyyyMMdd";
    private static final String LOG_TAG = Utility.class.getSimpleName();

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @return a user-friendly representation of the date.
     */
//    public static String getFriendlyDayString(Context context, long dateInMillis){
//        // The day string for forecast uses the following logic:
//        // For today: "Today, June 8"
//        // For tomorrow:  "Tomorrow"
//        // For the next 5 days: "Wednesday" (just the day name)
//        // For all days after that: "Mon Jun 8"
//
//        Time time = new Time();
//        time.setToNow();
//        long currentTime = System.currentTimeMillis();
//        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
//        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);
//
//        // If the date we're building the String for is today's date, the format
//        // is "Today, June 24"
//        if (julianDay == currentJulianDay) {
//            String today = context.getString(R.string.today);
//            int formatId = R.string.format_full_friendly_date;
//            return String.format(context.getString(formatId, today, getFormattedMonthDay(context, dateInMillis)));
//        } else if (julianDay < currentJulianDay + 7) {
//            // If the input date is less than a week in the future, just return the day name.
//            return getDayName(context, dateInMillis);
//        } else {
//            // Otherwise, use the form "Mon Jun 3"
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(dateInMillis);
//        }
//        return null;
//    }
//
//    /**
//     * Given a day, returns just the name to use for that day.
//     * E.g "today", "tomorrow", "wednesday".
//     *
//     * @param context      Context to use for resource localization
//     * @param dateInMillis The date in milliseconds
//     * @return
//     */
//    public static String getDayName(Context context, long dateInMillis) {
//        // If the date is today, return the localized version of "Today" instead of the actual
//        // day name.
//
//        Time t = new Time();
//        t.setToNow();
//        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
//        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
//        if (julianDay == currentJulianDay) {
//            return context.getString(R.string.today);
//        } else if (julianDay == currentJulianDay + 1) {
//            return context.getString(R.string.tomorrow);
//        } else {
//            Time time = new Time();
//            time.setToNow();
//            // Otherwise, the format is just the day of the week (e.g "Wednesday".
//            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
//            return dayFormat.format(dateInMillis);
//        }
//    }
//
//    /**
//     * Converts db date format to the format "Month day", e.g "June 24".
//     *
//     * @param context      Context to use for resource localization
//     * @param dateInMillis The db formatted date string, expected to be of the form specified
//     *                     in Utility.DATE_FORMAT
//     * @return The day in the form of a string formatted "December 6"
//     */
//    public static String getFormattedMonthDay(Context context, long dateInMillis) {
//        Time time = new Time();
//        time.setToNow();
//        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
//        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
//        String monthDayString = monthDayFormat.format(dateInMillis);
//        return monthDayString;
//    }
    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_Location),
                context.getString(R.string.pref_default_Location));
    }

    public static String getUnitType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_temperature_Units_key),
                context.getString(R.string.pref_temperature_Units_defaul));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_temperature_Units_key),
                context.getString(R.string.pref_temperature_Units_defaul))
                .equals(context.getString(R.string.pref_temperature_Units_defaul));
    }

    public static String formatTemperature(Context context, double temperature) {
        boolean isMetric = isMetric(context);
        double temp;
        if (!isMetric) {
            temp = 9 * temperature / 5 + 32;
        } else {
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }

    public static String formatDate(long dateInMilliSeconds) {
        Date date = new Date(dateInMilliSeconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd", Locale.ENGLISH);
        return simpleDateFormat.format(date);
    }
    public static long formatTodayDate(long currentdateInMilliSeconds) {
        Date date = new Date(currentdateInMilliSeconds);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String dateInString = simpleDateFormat.format(date);
        long dateInMilliSeconds=formatDateStringToMilliSecond(dateInString);
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

    public static final int VIEW_TYPE_TODAY = 0;
    public static final int VIEW_TYPE_FUTURE = 1;

    //通过weatherId获取相对应天气的图标ID，即drawable中的
    public static int getIconResourceForWeatherCondition(int weatherId, int view_type) {
        if (view_type == VIEW_TYPE_TODAY) {
            if ((weatherId >= 310 && weatherId <= 312) || (weatherId >= 302 && weatherId <= 304)) {
                return R.drawable.art_storm;
            } else if (weatherId >= 305 && weatherId <= 308) {
                return R.drawable.art_rain;
            } else if ((weatherId >= 300 && weatherId <= 301) || (weatherId == 309)) {
                return R.drawable.art_light_rain;
            } else if (weatherId >= 400 && weatherId <= 407) {
                return R.drawable.art_snow;
            } else if (weatherId >= 500 && weatherId <= 508) {
                return R.drawable.art_fog;
            } else if ((weatherId == 100) || (weatherId >= 200 && weatherId <= 204)) {
                return R.drawable.art_clear;
            } else if (weatherId == 103 || weatherId == 104) {
                return R.drawable.art_light_clouds;
            } else if (weatherId >= 101 && weatherId <= 102) {
                return R.drawable.art_clouds;
            } else return R.drawable.art_unknown;
        } else {
            if ((weatherId >= 310 && weatherId <= 312) || (weatherId >= 302 && weatherId <= 304)) {
                return R.drawable.ic_storm;
            } else if (weatherId >= 305 && weatherId <= 308) {
                return R.drawable.ic_rain;
            } else if ((weatherId >= 300 && weatherId <= 301) || (weatherId == 309)) {
                return R.drawable.ic_light_rain;
            } else if (weatherId >= 400 && weatherId <= 407) {
                return R.drawable.ic_snow;
            } else if (weatherId >= 500 && weatherId <= 508) {
                return R.drawable.ic_fog;
            } else if ((weatherId == 100) || (weatherId >= 200 && weatherId <= 204)) {
                return R.drawable.ic_clear;
            } else if (weatherId == 103 || weatherId == 104) {
                return R.drawable.ic_light_clouds;
            } else if (weatherId >= 101 && weatherId <= 102) {
                return R.drawable.ic_cloudy;
            } else return R.drawable.ic_unknown;
        }
    }

    //OpenWeather获取
    public static void getWeatherDataFromJson(Context context, String weatherJsonStr, String locationSetting)
            throws JSONException {
        final String LOG_TAG = context.getClass().getName();
        final String JSON_LIST = "list";
        final String JSON_date = "dt";
        final String JSON_MAIN = "main";
        final String JSON_CITY = "city";
        final String JSON_CITYNAME = "name";
        final String JSON_WEATHER = "weather";
        final String JSON_DESCRIPTION = "description";
        final String JSON_TEMP_MAX = "temp_max";
        final String JSON_TEMP_MIN = "temp_min";
        final String JSON_COORD = "coord";
        final String JSON_LAT = "lat";
        final String JSON_LON = "lon";
        final String JSON_WEATHER_ID = "id";
        final String JSON_HUMIDITY = "humidity";
        final String JSON_PRESSURE = "pressure";
        final String JSON_WIND = "wind";
        final String JSON_WIND_SPEED = "speed";
        final String JSON_WIND_DEGREES = "deg";

        JSONObject weatherObject = new JSONObject(weatherJsonStr);
        JSONObject city = weatherObject.getJSONObject(JSON_CITY);
        String cityName = city.getString(JSON_CITYNAME);
        JSONObject coord = city.getJSONObject(JSON_COORD);
        double lat = coord.getDouble(JSON_LAT);
        double lon = coord.getDouble(JSON_LON);

        JSONArray days = weatherObject.getJSONArray(JSON_LIST);

        long locationId = addLocation(context, locationSetting, cityName, lat, lon);

        Vector<ContentValues> valuesVector = new Vector<>(days.length());

        for (int i = 0; i < days.length(); i++) {
            double windSpeed;
            double degrees;
            double humidity;
            double pressure;
            double temp_max;
            double temp_min;
            String description;
            int weather_id;
            long dateTime;

            JSONObject dayInfo = days.getJSONObject(i);
            dateTime = dayInfo.getLong(JSON_date);
            description = dayInfo.getJSONArray(JSON_WEATHER).getJSONObject(0).getString(JSON_DESCRIPTION);
            weather_id = dayInfo.getJSONArray(JSON_WEATHER).getJSONObject(0).getInt(JSON_WEATHER_ID);
            JSONObject windInfo = dayInfo.getJSONObject(JSON_WIND);
            windSpeed = windInfo.getDouble(JSON_WIND_SPEED);
            degrees = windInfo.getDouble(JSON_WIND_DEGREES);

            JSONObject mainInfo = dayInfo.getJSONObject(JSON_MAIN);
            humidity = mainInfo.getDouble(JSON_HUMIDITY);
            pressure = mainInfo.getDouble(JSON_PRESSURE);
            temp_max = mainInfo.getDouble(JSON_TEMP_MAX);
            temp_min = mainInfo.getDouble(JSON_TEMP_MIN);

            ContentValues weatherValues = new ContentValues();


            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weather_id);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateTime);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, temp_max);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, temp_min);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, degrees);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
            valuesVector.add(weatherValues);
        }
        if (valuesVector.size() > 0) {
            ContentValues[] contentValues = new ContentValues[valuesVector.size()];
            valuesVector.toArray(contentValues);
            context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, contentValues);
        }

        Log.d(LOG_TAG, "FetchWeatherTask complete." + valuesVector.size() + " inserted");
    }

    public static long addLocation(Context context, String locationSetting, String cityName, double lat, double lon) {
        long locationId;

        Cursor locationCursor = context.getContentResolver().query(WeatherContract.LocationEntry.CONTENT_URI,
                new String[]{WeatherContract.LocationEntry._ID},
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{locationSetting},
                null);
        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(WeatherContract.LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            ContentValues locationValues = new ContentValues();

            locationValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, lat);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LON, lon);
            locationValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, locationSetting);

            Uri insertedUri = context.getContentResolver().insert(
                    WeatherContract.LocationEntry.CONTENT_URI, locationValues);
            locationId = ContentUris.parseId(insertedUri);
        }
        locationCursor.close();
        return locationId;
    }

    //从和风天气获取
    public static void getHeFengWeatherDataFromJson(Context context, String weatherJsonStr, String locationSetting)
            throws JSONException {
        long yesterday=0L;
        final String LOG_TAG = context.getClass().getName();
        final String JSON_HeWeather5 = "HeWeather5";
        final String JSON_date = "date";
        final String JSON_CITY_NAME = "city";
        final String JSON_CITYINFO = "basic";
        final String JSON_DAYS_INFO = "daily_forecast";
        final String JSON_WEATHER_CONDITON = "cond";
        final String JSON_DESCRIPTION = "txt_d";
        final String JSON_TEMP = "tmp";
        final String JSON_TEMP_MAX = "max";
        final String JSON_TEMP_MIN = "min";
        final String JSON_LAT = "lat";
        final String JSON_LON = "lon";
        final String JSON_WEATHER_ID = "code_d";
        final String JSON_HUMIDITY = "hum";
        final String JSON_PRESSURE = "pres";
        final String JSON_WIND = "wind";
        final String JSON_WIND_SPEED = "spd";
        final String JSON_WIND_DEGREES = "deg";

        JSONObject weatherObject = new JSONObject(weatherJsonStr);
        JSONArray dataArray = weatherObject.getJSONArray(JSON_HeWeather5);
        JSONObject dataObject = dataArray.getJSONObject(0);
        JSONObject cityInfo = dataObject.getJSONObject(JSON_CITYINFO);
        String cityName = cityInfo.getString(JSON_CITY_NAME);
        double lat = cityInfo.getDouble(JSON_LAT);
        double lon = cityInfo.getDouble(JSON_LON);

        JSONArray daysInfo = dataObject.getJSONArray(JSON_DAYS_INFO);

        long locationId = addLocation(context, locationSetting, cityName, lat, lon);

        Vector<ContentValues> valuesVector = new Vector<>(daysInfo.length());

        for (int i = 0; i < daysInfo.length(); i++) {
            int windSpeed;
            int degrees;
            double humidity;
            double pressure;
            double temp_max;
            double temp_min;
            String description;
            int weather_id;
            String date;
            long dateInMillis;


            JSONObject dayInfo = daysInfo.getJSONObject(i);
            //date
            date = dayInfo.getString(JSON_date);
            dateInMillis = Utility.formatDateStringToMilliSecond(date);
            if (i==0){
                yesterday=dateInMillis-60*60*24*1000;
            }
            //conditionId and desc
            JSONObject condition = dayInfo.getJSONObject(JSON_WEATHER_CONDITON);
            weather_id = condition.getInt(JSON_WEATHER_ID);
            description = condition.getString(JSON_DESCRIPTION);
            //temp
            JSONObject temp = dayInfo.getJSONObject(JSON_TEMP);
            temp_max = temp.getDouble(JSON_TEMP_MAX);
            temp_min = temp.getDouble(JSON_TEMP_MIN);
            //humidity and pressure
            humidity = dayInfo.getDouble(JSON_HUMIDITY);
            pressure = dayInfo.getDouble(JSON_PRESSURE);
            //windSpeed and degrees
            JSONObject winds = dayInfo.getJSONObject(JSON_WIND);
            windSpeed = winds.getInt(JSON_WIND_SPEED);
            degrees = winds.getInt(JSON_WIND_DEGREES);

            ContentValues weatherValues = new ContentValues();


            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationId);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, weather_id);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATE, dateInMillis);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, temp_max);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, temp_min);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, degrees);
            weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, description);
            valuesVector.add(weatherValues);
        }
        if (valuesVector.size() > 0) {
            ContentValues[] contentValues = new ContentValues[valuesVector.size()];
            valuesVector.toArray(contentValues);
            context.getContentResolver().bulkInsert(WeatherContract.WeatherEntry.CONTENT_URI, contentValues);
            context.getContentResolver().delete(WeatherContract.WeatherEntry.CONTENT_URI, WeatherContract.WeatherEntry.COLUMN_DATE+" <= ?",new String[]{Long.toString(yesterday)});
        }

        Log.d(LOG_TAG, "FetchWeatherTask complete." + valuesVector.size() + " inserted");
    }

}