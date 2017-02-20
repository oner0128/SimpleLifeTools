package com.hrong.simplelifetools.movie.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.hrong.simplelifetools.movie.MovieUtility;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hrong on 2016/10/16.
 */

public class MovieService extends IntentService {
    private static final String LOG_TAG = MovieService.class.getSimpleName();


    public MovieService() {
        super("Mymovie");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpURLConnection httpURLConnection=null;
        BufferedReader bufferedReader=null;
        String topRatedMovieJson=null;
        Uri topRatedMovieAPI=Uri.parse("https://api.themoviedb.org/3/movie/top_rated?api_key=bc1569b53636fffe9e8fd2abf866c27b&language=en-US");
        Log.v(LOG_TAG,topRatedMovieAPI.toString());
        try {
            URL url=new URL(topRatedMovieAPI.toString());
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            InputStream inputStream=httpURLConnection.getInputStream();
            if (inputStream==null)return;
            bufferedReader=new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            String s;
            StringBuffer buffer=new StringBuffer();
            while ((s=bufferedReader.readLine())!=null){
                buffer.append(s+"\n");
            }
            if (buffer.length()==0)return;
            topRatedMovieJson=buffer.toString();
//            Log.v(LOG_TAG,topRatedMovieJson);
            MovieUtility.getTopRatedMoviesFromJson(getApplicationContext(),topRatedMovieJson);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
