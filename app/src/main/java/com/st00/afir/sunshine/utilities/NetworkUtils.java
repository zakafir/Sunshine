package com.st00.afir.sunshine.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by zakaria_afir on 26/09/2017.
 */

public class NetworkUtils {

    final static String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    final static String PARAM_QUERY = "q";

    final static String API_ID = "APPID";

    final static String API_KEY = "a68fdf92027be19325c7ea94f0a7786e";


    public static URL buildUrl(String location) {
        URL myUrl = null;
        Uri builtUri = Uri.parse(WEATHER_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY,location)
                .appendQueryParameter(API_ID,API_KEY)
                .build();

        try {
             myUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return myUrl;
    }

    //extracting data from Internet
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
