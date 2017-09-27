package com.st00.afir.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.st00.afir.sunshine.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    private TextView mErrorMessageTextView;

    private ProgressBar mProgessBar;

    private EditText mSearchBoxEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        mErrorMessageTextView = (TextView) findViewById(R.id.error_msg);

        mProgessBar = (ProgressBar) findViewById(R.id.pb_fetching);

        mSearchBoxEditText = (EditText) findViewById(R.id.et_search_box);

    }



    private void loadWeatherData() {
        if(mSearchBoxEditText.getText() != null && !mSearchBoxEditText.getText().toString().equals("")){
            URL buildUrl = NetworkUtils.buildUrl(mSearchBoxEditText.getText().toString());
            showWeatherDataView();
            new FetchWeatherTask().execute(buildUrl);
            mWeatherTextView.setText("");
        }else{
            showWeatherDataView();
        }
    }

    private void showWeatherDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(){
        mWeatherTextView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    public class FetchWeatherTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgessBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {

            /* If there's no zip code, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            URL weatherRequestUrl = params[0];

            try {
                String jsonWeatherResponse = NetworkUtils
                        .getResponseFromHttpUrl(weatherRequestUrl);

                return jsonWeatherResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String weatherData) {
            mProgessBar.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                mWeatherTextView.setVisibility(View.VISIBLE);
                mWeatherTextView.setText((weatherData));

            }else {
                showErrorMessage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_search:
                loadWeatherData();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
