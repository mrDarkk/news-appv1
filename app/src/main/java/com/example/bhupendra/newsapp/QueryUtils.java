package com.example.bhupendra.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;



public class QueryUtils {

    public static List<News> fetchNewsData(String stringUrl) {
        if(stringUrl == null) {
            return null;
        }

        URL url = createUrl(stringUrl);
        String jsonResponse = makeHttpRequest(url);

        List<News> newsList = extractDataFromJson(jsonResponse);

        return newsList;
    }

    //Recieves a string and returns a url
    private static URL createUrl(String stringUrl) {
        URL url = null;
        if (stringUrl == null) {
            return null;
        }

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    private static String makeHttpRequest(URL url) {
        HttpURLConnection urlConnection = null;
        String jsonResponse = "";
        InputStream inputStream = null;

        if(url == null) {
            return null;
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "makeHttpRequest: "+jsonResponse);
        return jsonResponse;
    }

    private static String readFromStream (InputStream inputStream) {
        InputStreamReader streamReader = null;
        StringBuilder result = new StringBuilder() ;
        BufferedReader bufferedReader = null;

        streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        bufferedReader = new BufferedReader(streamReader);

        try {
            String line = bufferedReader.readLine();
            while (line != null) {
                result.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private static List<News> extractDataFromJson (String jsonResponse) {

        List<News> newsList = new ArrayList<>();
        try {

            JSONObject json = new JSONObject(jsonResponse);
            JSONObject response = json.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {

                JSONObject currentNews = results.getJSONObject(i);
                String title = currentNews.getString("webTitle");
                String section = currentNews.getString("sectionName");
                String date = currentNews.getString("webPublicationDate");
                String url = currentNews.getString("webUrl");
                String type = currentNews.getString("type");

                News newsObject = new News(title, date, url, title, section);
                newsList.add(newsObject);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }
}
