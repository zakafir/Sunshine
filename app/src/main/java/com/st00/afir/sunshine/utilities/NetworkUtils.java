package com.st00.afir.sunshine.utilities;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    final static String WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast";

    final static String PARAM_QUERY = "q";

    final static String API_ID = "APPID";

    final static String API_KEY = "a68fdf92027be19325c7ea94f0a7786e";


    public static URL buildUrl(String location) {
        URL myUrl = null;
        Uri builtUri = Uri.parse(WEATHER_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, location)
                .appendQueryParameter(API_ID, API_KEY)
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

    //parsing JSON data
    public static String[] getSimpleWeatherStringsFromJson(String forecastJsonStr)
            throws JSONException {

        final String OWM_LIST = "list";
        final String OWM_MAX = "temp_max";
        final String OWM_MIN = "temp_min";
        final String OWM_WEATHER = "weather";
        final String OWM_MAIN = "main";
        final String OWM_DESCRIPTION = "description";
        final String OWM_DATE = "dt_txt";
        final String OWM_MESSAGE_CODE = "cod";

        /* String array to hold each day's weather String */
        String[] parsedWeatherData = null;
        JSONObject forecastJson = new JSONObject(forecastJsonStr);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray weatherArray = forecastJson.optJSONArray(OWM_LIST);
        parsedWeatherData = new String[weatherArray.length()];

        String date;
        for (int i = 0; i < weatherArray.length(); i++) {
            JSONObject dayForecast = weatherArray.optJSONObject(i);
            date = dayForecast.optString(OWM_DATE);
            JSONArray weather = dayForecast.optJSONArray(OWM_WEATHER);
            String description = weather.optJSONObject(0).optString(OWM_DESCRIPTION);
            JSONObject mainInfos = dayForecast.optJSONObject(OWM_MAIN);
            int min = Math.round(Float.parseFloat(mainInfos.optString(OWM_MIN)) - 273.15F);
            int max = Math.round(Float.parseFloat(mainInfos.getString(OWM_MAX)) - 273.15F);
            parsedWeatherData[i] = date + " - " + description + " - " + min + "°C / " + max + "°C";
        }

        return parsedWeatherData;
    }
}
